package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Trace d'une exécution de synchronisation oedatarepo (job planifié ou
 * déclenchement manuel), pour l'historique consultable depuis la page admin.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "oedatarepo_sync_run")
public class OeSyncRun implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "started_at", nullable = false)
    private Date startedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "finished_at")
    private Date finishedAt;

    /** Origine du passage : {@code SCHEDULED} ou {@code MANUAL:<login>}. */
    @Column(name = "triggered_by", nullable = false, length = 32)
    private String triggeredBy;

    @Column(name = "examined")
    private int examined;

    @Column(name = "updated")
    private int updated;

    @Column(name = "errors")
    private int errors;

    @Column(name = "duration_ms")
    private Long durationMs;
}
