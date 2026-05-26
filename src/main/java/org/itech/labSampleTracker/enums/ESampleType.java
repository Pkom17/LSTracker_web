package org.itech.labSampleTracker.enums;

import org.apache.commons.lang3.StringUtils;

public enum ESampleType {
	BI("Bilan Initial"), BS("Bilan Suivi"), CV("Charge Virale"), EID("EID"), TB("Tuberculose"), HPV("HPV");

	private String type;

	ESampleType(String status) {
		this.type = status;
	}

	public String getType() {
		return type;
	}

	public static ESampleType get(final String type) {
		for (final ESampleType e : ESampleType.values()) {
			if (StringUtils.equals(e.getType(), type)) {
				return e;
			}
		}
		return null;
	}
}
