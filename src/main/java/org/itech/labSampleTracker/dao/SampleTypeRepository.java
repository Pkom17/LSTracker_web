/*
 * Java domain class for entity "SampleType" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.SampleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleTypeRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleType" Repository
 */

public interface SampleTypeRepository extends JpaRepository<SampleType, Integer>, JpaSpecificationExecutor<SampleType> {
	public SampleType findByName(String name);
}
