package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.AppUserHasLab;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasLabRepository
		extends JpaRepository<AppUserHasLab, Integer>, JpaSpecificationExecutor<AppUserHasLab> {
	Long removeByAppUserId(Integer appUserId);
	
	List<AppUserHasLab> findByAppUserId(Integer appUserId);

}
