package org.itech.labSampleTracker.dao;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.itech.labSampleTracker.entities.OeSampleSync;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès au suivi de synchronisation oedatarepo par échantillon.
 */
public interface OeSampleSyncRepository extends JpaRepository<OeSampleSync, Long> {

    Optional<OeSampleSync> findBySampleId(Integer sampleId);

    /** Pour décorer une liste d'échantillons avec leur état de suivi. */
    List<OeSampleSync> findBySampleIdIn(Collection<Integer> sampleIds);
}
