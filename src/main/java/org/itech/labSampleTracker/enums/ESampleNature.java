package org.itech.labSampleTracker.enums;

import org.apache.commons.lang3.StringUtils;

public enum ESampleNature {
	DBS("DBS"), SANG_TOTAL("SANG TOTAL"), PLASMA("PLASMA"), PSC("PSC"), CRACHAT("CRACHAT"), LCR("LCR"),
	SELLES("SELLES"), PV("PV"), Autre("Autre");

	private String type;

	ESampleNature(String status) {
		this.type = status;
	}

	public String getType() {
		return type;
	}

	public static ESampleNature get(final String type) {
		for (final ESampleNature e : ESampleNature.values()) {
			if (StringUtils.equals(e.getType(), type)) {
				return e;
			}
		}
		return null;
	}
}
