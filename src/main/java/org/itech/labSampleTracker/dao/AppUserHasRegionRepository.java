package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.AppUserHasRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasRegionRepository
		extends JpaRepository<AppUserHasRegion, Integer>, JpaSpecificationExecutor<AppUserHasRegion> {
	Long removeByAppUserId(Integer appUserId);
	
	List<AppUserHasRegion> findByAppUserId(Integer appUserId);

}
