package org.itech.labSampleTracker.enums;

import org.apache.commons.lang3.StringUtils;

public enum UserType {
	RIDER("CONVOYEUR"),BIOLOGIST("BIOLOGISTE"),ADMIN("ADMIN"),  OTHER("AUTRE");

	private String type;

	UserType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public static UserType get(final String type) {
		for (final UserType e : UserType.values()) {
			if (StringUtils.equals(e.getType(), type)) {
				return e;
			}
		}
		return null;
	}

}
