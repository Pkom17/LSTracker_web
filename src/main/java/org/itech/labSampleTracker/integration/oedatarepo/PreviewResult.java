package org.itech.labSampleTracker.integration.oedatarepo;

import java.time.LocalDateTime;

import lombok.Getter;

/**
 * Résultat d'une prévisualisation de synchronisation (sans persistance) pour un
 * échantillon : ce qu'oedatarepo propose, comparé à l'état actuel. Sérialisé tel
 * quel en JSON pour la page admin.
 */
@Getter
public final class PreviewResult {

    /** UPDATED (changement proposé), PENDING, NOT_FOUND, ERROR ou SKIPPED. */
    private final OeSyncOutcome outcome;
    private final Integer currentStatusId;
    private final Integer proposedStatusId;
    private final String proposedStatus;
    private final LocalDateTime completedDate;
    private final LocalDateTime releasedDate;
    private final String errorMessage;

    public PreviewResult(OeSyncOutcome outcome, Integer currentStatusId, Integer proposedStatusId,
            String proposedStatus, LocalDateTime completedDate, LocalDateTime releasedDate,
            String errorMessage) {
        this.outcome = outcome;
        this.currentStatusId = currentStatusId;
        this.proposedStatusId = proposedStatusId;
        this.proposedStatus = proposedStatus;
        this.completedDate = completedDate;
        this.releasedDate = releasedDate;
        this.errorMessage = errorMessage;
    }

    public static PreviewResult skipped() {
        return new PreviewResult(OeSyncOutcome.SKIPPED, null, null, null, null, null, null);
    }

    public static PreviewResult notFound(Integer currentStatusId) {
        return new PreviewResult(OeSyncOutcome.NOT_FOUND, currentStatusId, null, null, null, null, null);
    }

    public static PreviewResult error(Integer currentStatusId, String message) {
        return new PreviewResult(OeSyncOutcome.ERROR, currentStatusId, null, null, null, null, message);
    }
}
