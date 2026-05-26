/*
 * Java domain class for entity "SampleRetrieving" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.SampleRetrieving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleRetrievingRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleRetrieving" Repository
 */


public interface SampleRetrievingRepository  extends JpaRepository<SampleRetrieving, Integer> , JpaSpecificationExecutor<SampleRetrieving> {

}
