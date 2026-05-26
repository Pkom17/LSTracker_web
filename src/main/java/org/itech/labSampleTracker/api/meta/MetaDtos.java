package org.itech.labSampleTracker.api.meta;

import java.util.List;

public class MetaDtos {

	public record LabDto(Long id, String name, String labType) {
	}

	public record CircuitDto(Long id, String name) {
	}

	public record SiteDto(Long id, String name, String dhisCode) {
	}

	public record RejectionTypeDto(Long id, String name) {
	}

	public record CircuitSiteDto(Long circuitId, Long siteId) {
	}

	public record MetaFullResponse(String version, List<LabDto> labs, List<CircuitDto> circuits, List<SiteDto> sites,
			List<RejectionTypeDto> rejectionTypes, List<CircuitSiteDto> circuitSites) {
	}
}