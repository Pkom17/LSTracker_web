/*
 * Java domain class for entity "SampleAtLab" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.SampleAtLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SampleAtLabRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SampleAtLab" Repository
 */


public interface SampleAtLabRepository  extends JpaRepository<SampleAtLab, Integer> , JpaSpecificationExecutor<SampleAtLab> {

}
