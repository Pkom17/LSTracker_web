package org.itech.labSampleTracker.integration.oedatarepo;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.entities.SampleStatus;
import org.itech.labSampleTracker.enums.ESampleStatus;
import org.itech.labSampleTracker.service.SampleService;
import org.itech.labSampleTracker.service.SampleStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Synchronise le statut et les dates d'analyse d'un échantillon LSTracker à
 * partir de l'API oedatarepo (OpenELIS consolidé), via son labNumber.
 *
 * Mapping lstrackerStatus (oedatarepo) → ESampleStatus (LSTracker) :
 *   RESULT_READY    → ANALYSIS_DONE   (analyse terminée / résultat prêt)
 *   ANALYSIS_FAILED → ANALYSIS_FAILED
 *   NON_CONFORM     → NON_CONFORM
 *   null            → aucun changement (analyse encore en cours)
 *
 * Les 3 dates (started/completed/released) sont toujours mises à jour si
 * présentes, même quand le statut ne change pas (affichage de l'avancement).
 */
@Service
public class OeAnalysisSyncService {

    private static final Logger log = LoggerFactory.getLogger(OeAnalysisSyncService.class);

    private final OeDataRepoClient client;
    private final SampleService sampleService;
    private final SampleStatusService sampleStatusService;
    private final OeSyncTrackingService trackingService;

    public OeAnalysisSyncService(OeDataRepoClient client, SampleService sampleService,
            SampleStatusService sampleStatusService, OeSyncTrackingService trackingService) {
        this.client = client;
        this.sampleService = sampleService;
        this.sampleStatusService = sampleStatusService;
        this.trackingService = trackingService;
    }

    /**
     * Interroge oedatarepo pour le labNumber de l'échantillon et applique le
     * résultat (dates + statut). Persiste si quelque chose a changé.
     *
     * @return true si l'échantillon a été mis à jour, false sinon.
     */
    public boolean syncSample(Sample sample) {
        return syncSampleTracked(sample) == OeSyncOutcome.UPDATED;
    }

    /**
     * Comme {@link #syncSample(Sample)} mais renvoie le résultat détaillé
     * ({@link OeSyncOutcome}) et enregistre le suivi (compteur de tentatives /
     * dernier résultat) via {@link OeSyncTrackingService}.
     */
    public OeSyncOutcome syncSampleTracked(Sample sample) {
        if (sample == null || sample.getLabNumber() == null || sample.getLabNumber().trim().isEmpty()) {
            return OeSyncOutcome.SKIPPED;
        }

        OeAnalysisResult result = client.fetchAnalysis(sample.getLabNumber());
        OeSyncOutcome outcome;
        String error = null;

        switch (result.getKind()) {
            case SKIPPED:
                return OeSyncOutcome.SKIPPED; // pas d'écriture de suivi
            case NOT_FOUND:
                outcome = OeSyncOutcome.NOT_FOUND;
                break;
            case ERROR:
                outcome = OeSyncOutcome.ERROR;
                error = result.getErrorMessage();
                break;
            case OK:
            default:
                outcome = applyResponse(sample, result.getBody());
                break;
        }

        trackingService.recordOutcome(sample.getId(), outcome, error);
        return outcome;
    }

    /**
     * Applique le corps de réponse oedatarepo à l'échantillon et persiste si
     * besoin. Renvoie UPDATED si un changement a été persisté, PENDING sinon
     * (labno présent mais analyse encore en cours / rien à changer).
     */
    private OeSyncOutcome applyResponse(Sample sample, OeAnalysisStatusResponse resp) {
        if (resp == null) {
            return OeSyncOutcome.PENDING;
        }

        boolean changed = false;

        // 1) Dates d'étape (toujours rafraîchies si fournies).
        // NB : startedDate est exposé par l'API oedatarepo mais NON persisté
        // côté Sample (champ analysis_started_date non utilisé / non affiché).
        changed |= applyDate(resp.getCompletedDate(), sample::getAnalysisCompletedDate, sample::setAnalysisCompletedDate);
        changed |= applyDate(resp.getReleasedDate(), sample::getAnalysisReleasedDate, sample::setAnalysisReleasedDate);

        // 2) Statut (seulement si oedatarepo en propose un et qu'il diffère).
        ESampleStatus target = mapStatus(resp.getLstrackerStatus());
        if (target != null) {
            Integer newStatusId = resolveStatusId(target);
            if (newStatusId != null && !newStatusId.equals(sample.getSampleStatusId())) {
                sample.setSampleStatusId(newStatusId);
                changed = true;
                log.info("Sample id={} (labno={}) → statut {} via oedatarepo",
                        sample.getId(), sample.getLabNumber(), target.name());
            }
        }

        if (changed) {
            sampleService.update(sample);
        }
        // UPDATED si un changement a été persisté (statut et/ou dates) ; PENDING
        // sinon (labno présent mais rien à appliquer / analyse encore en cours).
        return changed ? OeSyncOutcome.UPDATED : OeSyncOutcome.PENDING;
    }

    /**
     * Prévisualise sans rien persister : interroge oedatarepo et renvoie le
     * statut actuel vs proposé + les dates, pour affichage admin. N'écrit ni le
     * sample ni le suivi.
     */
    public PreviewResult previewSample(Sample sample) {
        if (sample == null || sample.getLabNumber() == null || sample.getLabNumber().trim().isEmpty()) {
            return PreviewResult.skipped();
        }
        OeAnalysisResult result = client.fetchAnalysis(sample.getLabNumber());
        switch (result.getKind()) {
            case SKIPPED:
                return PreviewResult.skipped();
            case NOT_FOUND:
                return PreviewResult.notFound(sample.getSampleStatusId());
            case ERROR:
                return PreviewResult.error(sample.getSampleStatusId(), result.getErrorMessage());
            case OK:
            default:
                OeAnalysisStatusResponse resp = result.getBody();
                ESampleStatus target = resp == null ? null : mapStatus(resp.getLstrackerStatus());
                Integer proposedId = target == null ? null : resolveStatusId(target);
                OeSyncOutcome outcome = (target != null && proposedId != null
                        && !proposedId.equals(sample.getSampleStatusId()))
                        ? OeSyncOutcome.UPDATED : OeSyncOutcome.PENDING;
                return new PreviewResult(outcome, sample.getSampleStatusId(), proposedId,
                        target == null ? null : target.name(),
                        resp == null ? null : resp.getCompletedDate(),
                        resp == null ? null : resp.getReleasedDate(), null);
        }
    }

    /** Traduit le statut suggéré par oedatarepo en ESampleStatus, ou null. */
    private ESampleStatus mapStatus(String lstrackerStatus) {
        if (lstrackerStatus == null) {
            return null;
        }
        switch (lstrackerStatus) {
            case "RESULT_READY":
                return ESampleStatus.ANALYSIS_DONE;
            case "ANALYSIS_FAILED":
                return ESampleStatus.ANALYSIS_FAILED;
            case "NON_CONFORM":
                return ESampleStatus.NON_CONFORM;
            default:
                log.warn("lstrackerStatus inconnu reçu d'oedatarepo: {}", lstrackerStatus);
                return null;
        }
    }

    /** Récupère l'id du SampleStatus correspondant au code enum, ou null. */
    private Integer resolveStatusId(ESampleStatus status) {
        SampleStatus ss = sampleStatusService.findByStatus(status.name());
        if (ss == null) {
            log.error("Statut '{}' introuvable dans la table sample_status — "
                    + "vérifier le référentiel", status.name());
            return null;
        }
        return ss.getId();
    }

    /**
     * Applique une nouvelle date si elle est non nulle et différente de
     * l'actuelle. Retourne true si modifiée.
     */
    private boolean applyDate(LocalDateTime incoming, java.util.function.Supplier<Date> getter,
            java.util.function.Consumer<Date> setter) {
        if (incoming == null) {
            return false;
        }
        Date newDate = Date.from(incoming.atZone(ZoneId.systemDefault()).toInstant());
        Date current = getter.get();
        if (current != null && current.getTime() == newDate.getTime()) {
            return false;
        }
        setter.accept(newDate);
        return true;
    }
}
