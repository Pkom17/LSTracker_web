package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.AppUserHasCircuit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AppUserHasCircuitRepository
		extends JpaRepository<AppUserHasCircuit, Integer>, JpaSpecificationExecutor<AppUserHasCircuit> {
	Long removeByAppUserId(Integer appUserId);
	
	List<AppUserHasCircuit> findByAppUserId(Integer appUserId);

}
