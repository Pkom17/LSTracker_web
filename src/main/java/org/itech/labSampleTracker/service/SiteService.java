/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 * @author Pascal
 */
package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.entities.Site;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <h2>SiteServiceimpl</h2>
 */
public interface SiteService {
	Site create(Site d);

	Site update(Site d);

	Site getOne(int id);

	List<Site> getAll();

	long getTotal();

	boolean delete(int id);

	List<Integer> getSitesIdsByUser(Integer userId);

	List<Integer> getSitesIdsByDistricts(List<Integer> districtIds);

	List<Map<String, Object>> getSiteIdAndNames();
	
	List<Map<String, Object>>  getSiteIdAndNamesAndCicuit();

	List<Map<String, Object>> getSiteIdAndNamesByDistrict(Integer districtId);

	List<Map<String, Object>> getSiteIdAndNamesByDistricts(List<Integer> districtIds);

	Map<String, String> getSiteIdAndRegionName();

	Map<String, String> getSiteIdAndDistrictName();

	Site getOneBySiteCode(String siteCode);

	List<Map<String, Object>> getSiteIdAndCodeAndNamesByUser(Integer userId);
	
	List<Map<String, Object>> getSiteIdAndCodeAndNamesAndCircuitByUser(Integer userId);
	
}
