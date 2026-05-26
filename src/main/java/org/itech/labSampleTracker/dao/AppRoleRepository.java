/*
 * Java domain class for entity "AppRole" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;
import org.itech.labSampleTracker.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * <h2>AppRoleRepository</h2>
 *
 * createdAt : 2024-03-31 - Time 19:08:03
 * <p>
 * Description: "AppRole" Repository
 */


public interface AppRoleRepository  extends JpaRepository<AppRole, Integer> , JpaSpecificationExecutor<AppRole> {

}
