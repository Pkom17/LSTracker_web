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
 * Suivi de synchronisation oedatarepo, une ligne par échantillon.
 *
 * Porte le compteur de tentatives (NOT_FOUND) et le résultat du dernier
 * passage, utilisés pour exclure des cycles automatiques les labno absents
 * d'oedatarepo (cf. {@code OeSyncTrackingService} et la machine d'état de
 * {@code OeSyncOutcome}).
 */
@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "oedatarepo_sample_sync")
public class OeSampleSync implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sample_id", nullable = false)
    private Integer sampleId;

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_at")
    private Date lastAt;

    /** Dernier résultat (valeur de {@code OeSyncOutcome}). */
    @Column(name = "last_outcome", length = 20)
    private String lastOutcome;

    @Column(name = "last_error", length = 500)
    private String lastError;

    public OeSampleSync(Integer sampleId) {
        this.sampleId = sampleId;
        this.attempts = 0;
    }
}
