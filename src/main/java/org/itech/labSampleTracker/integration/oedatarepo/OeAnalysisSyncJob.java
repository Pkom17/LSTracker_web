package org.itech.labSampleTracker.integration.oedatarepo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Déclencheur périodique de la synchronisation oedatarepo. La logique de lot
 * (sélection des éligibles, application, suivi, historique) est dans
 * {@link OeAnalysisBatchService}, partagée avec le déclenchement manuel admin.
 *
 * Actif uniquement si lstracker.oedatarepo.enabled=true (sinon le bean n'est
 * pas créé, @EnableScheduling de cette config n'est pas chargée → aucun
 * scheduling parasite quand l'intégration est désactivée).
 *
 * Intervalle et délai initial configurables :
 *   lstracker.oedatarepo.sync.interval-ms       (défaut 30 min)
 *   lstracker.oedatarepo.sync.initial-delay-ms  (défaut 1 min)
 */
@Component
@EnableScheduling
@ConditionalOnProperty(name = "lstracker.oedatarepo.enabled", havingValue = "true")
public class OeAnalysisSyncJob {

    private final OeAnalysisBatchService batchService;

    public OeAnalysisSyncJob(OeAnalysisBatchService batchService) {
        this.batchService = batchService;
    }

    @Scheduled(
            fixedDelayString = "${lstracker.oedatarepo.sync.interval-ms:1800000}",
            initialDelayString = "${lstracker.oedatarepo.sync.initial-delay-ms:60000}")
    public void syncEligibleSamples() {
        batchService.runBatch("SCHEDULED");
    }
}
