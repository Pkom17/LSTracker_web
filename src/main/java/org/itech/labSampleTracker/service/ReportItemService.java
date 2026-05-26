package org.itech.labSampleTracker.service;

import java.time.LocalDate;
import java.util.List;

import org.itech.labSampleTracker.dto.ReportItem;

public interface ReportItemService {

	public ReportItem buildForScope(LocalDate start, LocalDate end, Integer regionId, Integer districtId,
			Integer siteId, Integer labId, Integer riderId, List<Integer> accessibleSiteIds);
}