package org.itech.labSampleTracker.dao;

import java.util.List;
import java.util.Optional;

import org.itech.labSampleTracker.entities.OeSyncRun;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Accès à l'historique des exécutions de synchronisation oedatarepo.
 */
public interface OeSyncRunRepository extends JpaRepository<OeSyncRun, Long> {

    /** Les N derniers passages, du plus récent au plus ancien (historique page admin). */
    List<OeSyncRun> findTop20ByOrderByStartedAtDesc();

    /** Dernier passage, pour le panneau d'état. */
    Optional<OeSyncRun> findFirstByOrderByStartedAtDesc();
}
