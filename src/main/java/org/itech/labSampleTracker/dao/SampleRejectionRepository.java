/*
 * Java domain class for entity "SampleRejection" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.SampleRejection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleRejectionRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleRejection" Repository
 */

public interface SampleRejectionRepository
		extends JpaRepository<SampleRejection, Integer>, JpaSpecificationExecutor<SampleRejection> {
	Long removeBySampleId(Integer sampleId);

	SampleRejection findOneBySampleId(Integer sampleId);
}
