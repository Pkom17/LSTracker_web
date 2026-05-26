/*
 * Java domain class for entity "SampleRejectionType" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.SampleRejectionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleRejectionTypeRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleRejectionType" Repository
 */


public interface SampleRejectionTypeRepository  extends JpaRepository<SampleRejectionType, Integer> , JpaSpecificationExecutor<SampleRejectionType> {

}
