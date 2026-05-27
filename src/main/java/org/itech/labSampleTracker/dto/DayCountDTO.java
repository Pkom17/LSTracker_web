package org.itech.labSampleTracker.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

public class DayCountDTO {
	// Sans @JsonFormat, Jackson sérialise LocalDate en tableau [YYYY, M, D]
	// (WRITE_DATES_AS_TIMESTAMPS=true par défaut), incompatible avec le client
	// dashboard.js qui attend "YYYY-MM-DD".
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private LocalDate day;
	private long cnt;

	public DayCountDTO(LocalDate d, long c) {
		this.day = d;
		this.cnt = c;
	}

	public LocalDate getDay() {
		return day;
	}

	public long getCnt() {
		return cnt;
	}
}
