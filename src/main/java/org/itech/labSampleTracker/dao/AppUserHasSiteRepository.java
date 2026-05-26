package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.AppUserHasSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasSiteRepository
		extends JpaRepository<AppUserHasSite, Integer>, JpaSpecificationExecutor<AppUserHasSite> {
	Long removeByAppUserId(Integer appUserId);
	
	List<AppUserHasSite> findByAppUserId(Integer appUserId);

}
