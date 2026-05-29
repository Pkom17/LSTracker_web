package org.itech.labSampleTracker.integration.oedatarepo;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.itech.labSampleTracker.dao.OeSampleSyncRepository;
import org.itech.labSampleTracker.dao.OeSyncRunRepository;
import org.itech.labSampleTracker.entities.OeSampleSync;
import org.itech.labSampleTracker.entities.OeSyncRun;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Persiste le suivi de la synchronisation oedatarepo :
 * <ul>
 *   <li>compteur de tentatives + dernier résultat par échantillon
 *       (table oedatarepo_sample_sync), selon la machine d'état de
 *       {@link OeSyncOutcome} ;</li>
 *   <li>historique des exécutions (table oedatarepo_sync_run).</li>
 * </ul>
 *
 * Les écritures sont en transactions courtes, indépendantes de la mise à jour
 * de l'échantillon elle-même (cohérence éventuelle assumée : un échec de suivi
 * ne doit pas annuler une mise à jour réussie, et réciproquement).
 */
@Service
public class OeSyncTrackingService {

    private final OeSampleSyncRepository sampleSyncRepository;
    private final OeSyncRunRepository syncRunRepository;

    public OeSyncTrackingService(OeSampleSyncRepository sampleSyncRepository,
            OeSyncRunRepository syncRunRepository) {
        this.sampleSyncRepository = sampleSyncRepository;
        this.syncRunRepository = syncRunRepository;
    }

    // --- Historique des exécutions ---------------------------------------

    /** Ouvre une exécution (started_at = maintenant) et la persiste. */
    @Transactional
    public OeSyncRun startRun(String triggeredBy) {
        OeSyncRun run = new OeSyncRun();
        run.setStartedAt(new Date());
        run.setTriggeredBy(triggeredBy);
        return syncRunRepository.save(run);
    }

    /** Clôt une exécution avec ses compteurs et sa durée. */
    @Transactional
    public void finishRun(OeSyncRun run, int examined, int updated, int errors) {
        Date now = new Date();
        run.setFinishedAt(now);
        run.setExamined(examined);
        run.setUpdated(updated);
        run.setErrors(errors);
        if (run.getStartedAt() != null) {
            run.setDurationMs(now.getTime() - run.getStartedAt().getTime());
        }
        syncRunRepository.save(run);
    }

    public List<OeSyncRun> recentRuns() {
        return syncRunRepository.findTop20ByOrderByStartedAtDesc();
    }

    public Optional<OeSyncRun> lastRun() {
        return syncRunRepository.findFirstByOrderByStartedAtDesc();
    }

    // --- Suivi par échantillon (machine d'état) --------------------------

    /**
     * Enregistre le résultat d'une tentative pour un échantillon. Met toujours à
     * jour last_at et last_outcome ; l'effet sur le compteur de tentatives suit
     * la machine d'état (cf. {@link OeSyncOutcome}) :
     * <ul>
     *   <li>UPDATED / PENDING → compteur remis à 0 (auto-guérison) ;</li>
     *   <li>NOT_FOUND → compteur +1 (seul cas qui mène à l'épuisement) ;</li>
     *   <li>ERROR → compteur inchangé, message d'erreur conservé ;</li>
     *   <li>SKIPPED → aucune écriture.</li>
     * </ul>
     */
    @Transactional
    public void recordOutcome(Integer sampleId, OeSyncOutcome outcome, String error) {
        if (sampleId == null || outcome == null || outcome == OeSyncOutcome.SKIPPED) {
            return;
        }
        OeSampleSync row = sampleSyncRepository.findBySampleId(sampleId)
                .orElseGet(() -> new OeSampleSync(sampleId));

        row.setLastAt(new Date());
        row.setLastOutcome(outcome.name());

        switch (outcome) {
            case UPDATED:
            case PENDING:
                row.setAttempts(0);
                row.setLastError(null);
                break;
            case NOT_FOUND:
                row.setAttempts(row.getAttempts() + 1);
                row.setLastError(null);
                break;
            case ERROR:
                // compteur inchangé
                row.setLastError(truncate(error));
                break;
            default:
                break;
        }
        sampleSyncRepository.save(row);
    }

    /** Réinitialise le compteur d'un échantillon (réintroduit un labno épuisé). */
    @Transactional
    public void resetSample(Integer sampleId) {
        sampleSyncRepository.findBySampleId(sampleId).ifPresent(row -> {
            row.setAttempts(0);
            row.setLastOutcome(null);
            row.setLastError(null);
            row.setLastAt(new Date());
            sampleSyncRepository.save(row);
        });
    }

    public Optional<OeSampleSync> findBySampleId(Integer sampleId) {
        return sampleSyncRepository.findBySampleId(sampleId);
    }

    public List<OeSampleSync> findBySampleIds(Collection<Integer> sampleIds) {
        if (sampleIds == null || sampleIds.isEmpty()) {
            return List.of();
        }
        return sampleSyncRepository.findBySampleIdIn(sampleIds);
    }

    private static String truncate(String s) {
        if (s == null) {
            return null;
        }
        return s.length() <= 500 ? s : s.substring(0, 500);
    }
}
