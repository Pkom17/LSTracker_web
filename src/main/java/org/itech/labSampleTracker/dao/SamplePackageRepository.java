/*
 * Java domain class for entity "SamplePackage" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.SamplePackage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>SamplePackageRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "SamplePackage" Repository
 */


public interface SamplePackageRepository  extends JpaRepository<SamplePackage, Integer> , JpaSpecificationExecutor<SamplePackage> {

}
