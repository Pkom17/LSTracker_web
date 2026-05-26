package org.itech.labSampleTracker.helper;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;

public class Utils {
	public static Map<String, String> objectToMap(Object bean) {
		Map<String, String> map = new HashMap<>();
		for (var f : bean.getClass().getDeclaredFields()) {
			try {
				f.setAccessible(true);
				Object v = f.get(bean);
				String value = ObjectUtils.isNotEmpty(v) ? v.toString() : "";
				map.put(f.getName(), value);
			} catch (IllegalAccessException ignored) {
			}
		}
		return map;
	}

}
