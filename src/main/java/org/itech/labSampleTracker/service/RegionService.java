/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface RegionService {
	Region create(Region d);

	Region update(Region d);

	Region getOne(int id);

	String resolveRegionName(Integer id);

	List<Region> getAll();

	long getTotal();

	boolean delete(int id);

	List<Map<String, Object>> getRegionIdAndName();

	/**
	 * Paginated admin region list with full-text search and aggregated child counts.
	 */
	Page<Map<String, Object>> findRegionsAdvanced(Pageable pageable, String searchText);

	/**
	 * Number of districts attached to a region (delete guard).
	 */
	long countDistricts(Integer regionId);
}
