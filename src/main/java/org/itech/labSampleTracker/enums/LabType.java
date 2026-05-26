package org.itech.labSampleTracker.enums;

import org.apache.commons.lang3.StringUtils;

public enum LabType {
	DISTRICT_LAB("DISTRICT"), HUB_LAB("RELAIS"), PLATEFORM_LAB("PLATEFORME"), TB_LAB("CAT");

	private String type;

	LabType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static LabType get(final String type) {
		for (final LabType e : LabType.values()) {
			if (StringUtils.equals(e.getType(), type)) {
				return e;
			}
		}
		return null;
	}
}
