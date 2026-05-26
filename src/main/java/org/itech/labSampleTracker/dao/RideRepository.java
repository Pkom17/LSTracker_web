/*
 * Java domain class for entity "Ride" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.Ride;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>RideRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:04
 * <p>
 * Description: "Ride" Repository
 */


public interface RideRepository  extends JpaRepository<Ride, Integer> , JpaSpecificationExecutor<Ride> {

}
