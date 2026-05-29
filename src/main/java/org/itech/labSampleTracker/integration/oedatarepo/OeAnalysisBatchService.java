package org.itech.labSampleTracker.integration.oedatarepo;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.itech.labSampleTracker.dao.SampleRepository;
import org.itech.labSampleTracker.entities.OeSyncRun;
import org.itech.labSampleTracker.entities.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * Exécution d'un lot de synchronisation oedatarepo, partagée par le job
 * planifié et le déclenchement manuel (même logique → parité garantie).
 *
 * Une garde {@link AtomicBoolean} empêche deux exécutions simultanées dans la
 * même instance (job vs manuel) : un second déclenchement renvoie immédiatement
 * un résumé "occupé" sans écrire de run. (Garde par instance ; suffisant pour le
 * modèle de déploiement actuel d'un conteneur par environnement.)
 */
@Service
public class OeAnalysisBatchService {

    private static final Logger log = LoggerFactory.getLogger(OeAnalysisBatchService.class);

    private final SampleRepository sampleRepository;
    private final OeAnalysisSyncService syncService;
    private final OeSyncTrackingService trackingService;
    private final int batchSize;
    private final int maxAttempts;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public OeAnalysisBatchService(SampleRepository sampleRepository,
            OeAnalysisSyncService syncService, OeSyncTrackingService trackingService,
            @Value("${lstracker.oedatarepo.sync.batch-size:200}") int batchSize,
            @Value("${lstracker.oedatarepo.sync.max-attempts:5}") int maxAttempts) {
        this.sampleRepository = sampleRepository;
        this.syncService = syncService;
        this.trackingService = trackingService;
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
    }

    public boolean isRunning() {
        return running.get();
    }

    /**
     * Lance un lot. Renvoie un résumé ; {@code busy=true} si un lot était déjà en
     * cours (aucun run n'est alors écrit).
     *
     * @param triggeredBy origine (ex. {@code SCHEDULED} ou {@code MANUAL:login}).
     */
    public RunSummary runBatch(String triggeredBy) {
        if (!running.compareAndSet(false, true)) {
            log.info("Sync oedatarepo déjà en cours, déclenchement '{}' ignoré", triggeredBy);
            return RunSummary.busy();
        }
        OeSyncRun run = trackingService.startRun(triggeredBy);
        int examined = 0;
        int updated = 0;
        int errors = 0;
        try {
            List<Sample> eligible = sampleRepository.findEligibleForAnalysisSync(batchSize, maxAttempts);
            examined = eligible.size();
            for (Sample s : eligible) {
                try {
                    OeSyncOutcome outcome = syncService.syncSampleTracked(s);
                    if (outcome == OeSyncOutcome.UPDATED) {
                        updated++;
                    } else if (outcome == OeSyncOutcome.ERROR) {
                        errors++;
                    }
                } catch (Exception e) {
                    // Un échec sur un échantillon ne doit pas stopper le lot.
                    errors++;
                    log.error("Sync oedatarepo échouée pour sample id={} labno={}: {}",
                            s.getId(), s.getLabNumber(), e.getMessage());
                }
            }
            log.info("Sync oedatarepo ({}) : {} échantillon(s) examinés, {} mis à jour, {} erreur(s)",
                    triggeredBy, examined, updated, errors);
            return new RunSummary(false, examined, updated, errors);
        } finally {
            trackingService.finishRun(run, examined, updated, errors);
            running.set(false);
        }
    }

    /** Résumé d'un lot, renvoyé aux appelants (job/contrôleur). */
    @Getter
    public static final class RunSummary {
        private final boolean busy;
        private final int examined;
        private final int updated;
        private final int errors;

        public RunSummary(boolean busy, int examined, int updated, int errors) {
            this.busy = busy;
            this.examined = examined;
            this.updated = updated;
            this.errors = errors;
        }

        static RunSummary busy() {
            return new RunSummary(true, 0, 0, 0);
        }
    }
}
