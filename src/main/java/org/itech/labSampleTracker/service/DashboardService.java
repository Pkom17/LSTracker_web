package org.itech.labSampleTracker.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.itech.labSampleTracker.dto.DayCountDTO;
import org.itech.labSampleTracker.dto.StepDurationDTO;

public interface DashboardService {

	public Map<String, Object> getSummary(LocalDate start, LocalDate end, Integer regionId, Integer districtId,
			Integer siteId, Integer labId);

	public Map<String, Object> getSummary(LocalDate start, LocalDate end, Integer regionId, Integer districtId,
			Integer siteId, Integer labId, List<Integer> accessibleSiteIds);

	public List<DayCountDTO> seriesCollected(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId);

	public List<DayCountDTO> seriesCollected(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId,
			List<Integer> accessibleSiteIds);

	public List<DayCountDTO> seriesDeposited(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId);

	public List<DayCountDTO> seriesDeposited(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId,
			List<Integer> accessibleSiteIds);

	public List<DayCountDTO> seriesAnalysed(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId);

	public List<DayCountDTO> seriesAnalysed(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId,
			List<Integer> accessibleSiteIds);

	public List<DayCountDTO> seriesDelivered(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId);

	public List<DayCountDTO> seriesDelivered(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId,
			List<Integer> accessibleSiteIds);

	public List<StepDurationDTO> stepDurations(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId);

	public List<StepDurationDTO> stepDurations(LocalDate s, LocalDate e, Integer r, Integer d, Integer si, Integer labId,
			List<Integer> accessibleSiteIds);

}
