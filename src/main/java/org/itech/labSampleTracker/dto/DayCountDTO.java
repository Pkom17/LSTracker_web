package org.itech.labSampleTracker.dto;

import java.time.LocalDate;

public class DayCountDTO {
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