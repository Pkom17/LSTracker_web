package org.itech.labSampleTracker.dao;

import org.itech.labSampleTracker.entities.AppUserHasRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasRoleRepository
		extends JpaRepository<AppUserHasRole, Integer>, JpaSpecificationExecutor<AppUserHasRole> {

}
