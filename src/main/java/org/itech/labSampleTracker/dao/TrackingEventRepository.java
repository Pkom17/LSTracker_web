/*
 * Java domain class for entity "TrackingEvent" 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.dao;

import java.util.List;

import org.itech.labSampleTracker.entities.TrackingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TrackingEventRepository
		extends JpaRepository<TrackingEvent, Integer>, JpaSpecificationExecutor<TrackingEvent> {

	List<TrackingEvent> findBySampleIdOrderByCreatedAtAsc(Integer sampleId);
}
