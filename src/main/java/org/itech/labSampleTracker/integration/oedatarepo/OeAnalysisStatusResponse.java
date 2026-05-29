package org.itech.labSampleTracker.integration.oedatarepo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Réponse de l'API d'interopérabilité oedatarepo
 * (GET /api/v1/order-analysis/{labno}).
 *
 * Miroir du DTO côté OpenELIS consolidé : statut consolidé + dates d'étape
 * pour un numéro de laboratoire.
 *
 * @JsonIgnoreProperties : tolère d'éventuels champs supplémentaires ajoutés
 * côté oedatarepo sans casser la désérialisation.
 */
@Getter
@Setter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OeAnalysisStatusResponse {

    private String labno;
    private String sampleStatus;

    private LocalDateTime startedDate;
    private LocalDateTime completedDate;
    private LocalDateTime releasedDate;

    private boolean resultReady;
    private boolean failed;

    /** Statut suggéré côté LSTracker : RESULT_READY / ANALYSIS_FAILED / NON_CONFORM / null. */
    private String lstrackerStatus;

    private int analysisCount;
}
