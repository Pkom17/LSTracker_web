/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * <h2>DistrictServiceimpl</h2>
 */
public interface DistrictService {
	District create(District d);

	District update(District d);

	District getOne(int id);

	String resolveDistrictName(Integer id);

	List<District> getAll();

	long getTotal();

	boolean delete(int id);

	public List<Map<String, Object>> getDistrictIdAndNames();

	public List<Map<String, Object>> getDistrictIdAndNamesByRegion(Integer regionId);

	public List<Map<String, Object>> getDistrictIdAndNamesByRegions(List<Integer> regions);

	/**
	 * Paginated admin district list with full-text search, region filter and
	 * aggregated child counts.
	 */
	Page<Map<String, Object>> findDistrictsAdvanced(Pageable pageable, String searchText, Integer regionId);

	/**
	 * Number of sites attached to a district (delete guard).
	 */
	long countSites(Integer districtId);
}
