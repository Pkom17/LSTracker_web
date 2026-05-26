package org.itech.labSampleTracker.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserLevel {
	CIRCUIT_LEVEL("CIRCUIT"),SITE_LEVEL("SITE"), LAB_LEVEL("LABO"),DISTRICT_LEVEL("DISTRICT"), REGION_LEVEL("REGION"), CENTRAL_LEVEL("CENTRAL");

	private String level;

	UserLevel(String level) {
		this.level = level;
	}

	public String getLevel() {
		return level;
	}

	public static UserLevel get(final String level) {
		for (final UserLevel e : UserLevel.values()) {
			if (StringUtils.equals(e.getLevel(), level)) {
				return e;
			}
		}
		return null;
	}

}
