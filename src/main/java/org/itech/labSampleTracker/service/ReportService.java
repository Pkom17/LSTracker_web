package org.itech.labSampleTracker.service;

import org.itech.labSampleTracker.dto.ReportItem;

public interface ReportService {
	public byte[] getReportData(ReportItem reportItems, String reportName);

}
