package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.AppUserHasDistrict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasDistrictRepository
		extends JpaRepository<AppUserHasDistrict, Integer>, JpaSpecificationExecutor<AppUserHasDistrict> {
	Long removeByAppUserId(Integer appUserId);
	
	List<AppUserHasDistrict> findByAppUserId(Integer appUserId);

}
