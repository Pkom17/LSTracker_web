/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Circuit;

import java.util.List;
import java.util.Map;

/**
 * <h2>CircuitServiceimpl</h2>
 */
public interface CircuitService {
	Circuit create(Circuit d);

	Circuit update(Circuit d);

	Circuit getOne(int id);
	
	Circuit getCircuitByNumber(String circuitNumber);

	List<Circuit> getAll();

	long getTotal();

	boolean delete(int id);
	
	List<Map<String, Object>> getAllCircuitsDetails();
	
	List<Map<String, Object>> getCircuitIdAndNumber();
		
	List<Map<String, Object>> getCircuitSitesIdAndName(Integer circuit);
	
	List<Map<String, Object>> getCircuitIdAndNumberByUser(Integer userId);
}
