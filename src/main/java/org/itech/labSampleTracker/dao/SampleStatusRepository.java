/*
 * Java domain class for entity "SampleStatus" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.SampleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleStatusRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleStatus" Repository
 */

public interface SampleStatusRepository
		extends JpaRepository<SampleStatus, Integer>, JpaSpecificationExecutor<SampleStatus> {
	public SampleStatus findByStatus(String status);
}
