package org.itech.labSampleTracker.helper;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class DateUtils {

	private static final DateTimeFormatter ISO_OFFSET_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
	private static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm");
	private static final DateTimeFormatter SPACE_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd HH:mm");

	/**
	 * Parses a date string from one of the expected formats.
	 * 
	 * @param s The date string to parse.
	 * @return A Date object if parsing is successful, otherwise null.
	 */
	public static Date parseIsoDateTimeOrNull(String s) {
		if (s == null || s.trim().isEmpty()) {
			return null;
		}
		
		s=s.substring(0, 16);

		// Try to parse as ISO 8601 with offset
		try {
			return Date.from(OffsetDateTime.parse(s, ISO_OFFSET_DATE_TIME_FORMATTER).toInstant());
		} catch (DateTimeParseException ex) {
			try {
				return Date.from(java.time.LocalDateTime.parse(s, ISO_LOCAL_DATE_TIME_FORMATTER)
						.toInstant(java.time.ZoneOffset.UTC));
			} catch (DateTimeParseException ex2) {
				try {
					return Date.from(java.time.LocalDateTime.parse(s, SPACE_LOCAL_DATE_TIME_FORMATTER)
							.toInstant(java.time.ZoneOffset.UTC));
				} catch (DateTimeParseException ex3) {
					ex3.printStackTrace();
					return null;
				}
			}
		}
	}

	/**
	 * Parses a date string from an ISO 8601 format with offset.
	 * 
	 * @param s The date string to parse.
	 * @return An OffsetDateTime object if parsing is successful, otherwise null.
	 */
	public static OffsetDateTime parseIsoOrNull(String s) {
		if (s == null || s.trim().isEmpty()) {
			return null;
		}
		try {
			return OffsetDateTime.parse(s, ISO_OFFSET_DATE_TIME_FORMATTER);
		} catch (DateTimeParseException ex) {
			return null;
		}
	}

	/**
	 * Converts a java.util.Date object to an ISO 8601 string representation.
	 * 
	 * @param d The Date object to convert.
	 * @return The ISO 8601 string, or null if the input is null.
	 */
	public static String toIso(Date d) {
		return (d == null) ? null : OffsetDateTime.ofInstant(d.toInstant(), ZoneOffset.UTC).toString();
	}

	/**
	 * Converts a java.util.Date object to an OffsetDateTime object.
	 * 
	 * @param d The Date object to convert.
	 * @return The OffsetDateTime object, or null if the input is null.
	 */
	public static OffsetDateTime toOffset(Date d) {
		return (d == null) ? null : OffsetDateTime.ofInstant(d.toInstant(), ZoneOffset.UTC);
	}

	/**
	 * Converts an OffsetDateTime object to a java.util.Date object. * @param odt
	 * The OffsetDateTime object to convert.
	 * 
	 * @return The Date object, or null if the input is null.
	 */
	public static Date toDate(OffsetDateTime odt) {
		return (odt == null) ? null : Date.from(odt.toInstant());
	}

}