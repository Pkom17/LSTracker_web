package org.itech.labSampleTracker.integration.oedatarepo;

import java.util.HashMap;
import java.util.Map;

import org.itech.labSampleTracker.entities.Sample;
import org.itech.labSampleTracker.service.SampleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Rafraîchissement à la demande du statut/dates d'analyse d'un échantillon
 * depuis oedatarepo (complément du job périodique).
 *
 * POST /api/tracker/oedatarepo/refresh/{sampleId}
 *  → interroge oedatarepo via le labNumber de l'échantillon et applique le
 *    résultat. Renvoie un petit résumé { updated, sampleStatusId }.
 */
@RestController
@RequestMapping("/api/tracker/oedatarepo")
public class OeAnalysisSyncController {

    private static final Logger log = LoggerFactory.getLogger(OeAnalysisSyncController.class);

    private final SampleService sampleService;
    private final OeAnalysisSyncService syncService;
    private final OeDataRepoClient client;

    public OeAnalysisSyncController(SampleService sampleService, OeAnalysisSyncService syncService,
            OeDataRepoClient client) {
        this.sampleService = sampleService;
        this.syncService = syncService;
        this.client = client;
    }

    @PostMapping("/refresh/{sampleId}")
    public ResponseEntity<Map<String, Object>> refresh(@PathVariable("sampleId") int sampleId) {
        if (!client.isReady()) {
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Intégration oedatarepo non configurée/activée"));
        }

        Sample sample;
        try {
            sample = sampleService.getOne(sampleId);
        } catch (Exception e) {
            sample = null;
        }
        if (sample == null) {
            return ResponseEntity.notFound().build();
        }
        if (sample.getLabNumber() == null || sample.getLabNumber().trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Échantillon sans numéro de laboratoire (labNumber)"));
        }

        boolean updated = syncService.syncSample(sample);
        Map<String, Object> body = new HashMap<>();
        body.put("sampleId", sampleId);
        body.put("labNumber", sample.getLabNumber());
        body.put("updated", updated);
        body.put("sampleStatusId", sample.getSampleStatusId());
        log.info("Refresh oedatarepo à la demande sample={} labno={} updated={}",
                sampleId, sample.getLabNumber(), updated);
        return ResponseEntity.ok(body);
    }
}
