/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Lab;

import java.util.List;
import java.util.Map;

/**
 * <h2>LabServiceimpl</h2>
 */
public interface LabService {
	Lab create(Lab d);

	Lab update(Lab d);

	Lab getOne(int id);
	
	String resolveLabName(Integer id);

	List<Lab> getAll();

	long getTotal();

	boolean delete(int id);

	List<Integer> getLabsIdsByUser(Integer userId);

	List<Integer> getLabsIdsByDistricts(List<Integer> districtIds);

	List<Map<String, Object>> getLabIdAndNames();
	
	List<Map<String, Object>> getAllLabIdAndNamesByRider(Integer userId);
	
	List<Map<String, Object>> getAllLabIdAndNamesByLabUser(Integer userId);
	
	List<Map<String, Object>> getLabIdAndNamesByDistricts(List<Integer> districtIds);

	List<Map<String, Object>> getLabIdAndNamesByDistrict(Integer districtId);
	
	List<Map<String, Object>> getReferenceLabIdAndNames();
	
	List<Map<String, Object>> getHubIdAndNames();
}
