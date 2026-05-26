package org.itech.labSampleTracker.dto;

import lombok.ToString;

@ToString
public class StepDurationDTO {
	private String sampleType;
	private String step;
	private int n, avgDays, medianDays, minDays, maxDays;

	public String getSampleType() {
		return sampleType;
	}

	public void setSampleType(String s) {
		this.sampleType = s;
	}

	public String getStep() {
		return step;
	}

	public void setStep(String s) {
		this.step = s;
	}

	public int getN() {
		return n;
	}

	public void setN(int n) {
		this.n = n;
	}

	public int getAvgDays() {
		return avgDays;
	}

	public void setAvgDays(int v) {
		this.avgDays = v;
	}

	public int getMedianDays() {
		return medianDays;
	}

	public void setMedianDays(int v) {
		this.medianDays = v;
	}

	public int getMinDays() {
		return minDays;
	}

	public void setMinDays(int v) {
		this.minDays = v;
	}

	public int getMaxDays() {
		return maxDays;
	}

	public void setMaxDays(int v) {
		this.maxDays = v;
	}
}