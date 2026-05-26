/*
 * Java domain class for entity "CircuitSite" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.CircuitSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>CircuitSiteRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:03
 * <p>
 * Description: "CircuitSite" Repository
 */

public interface CircuitSiteRepository
		extends JpaRepository<CircuitSite, Integer>, JpaSpecificationExecutor<CircuitSite> {
	public List<CircuitSite> findAllByCircuitId(Integer circuitId);

	Long removeByCircuitId(Integer circuitId);

	Long removeBySiteId(Integer siteId);
}
