package org.itech.labSampleTracker.service_impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.itech.labSampleTracker.dto.ReportItem;
import org.itech.labSampleTracker.helper.Utils;
import org.itech.labSampleTracker.service.ReportService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;

@Slf4j
@Service
@Transactional
public class ReportServiceImpl implements ReportService {

	@PersistenceContext
	private EntityManager em;

	@Override
	public byte[] getReportData(ReportItem reportsItems, String reportName) {
		JasperReport jasperReport;
		
		try {
			jasperReport = (JasperReport) JRLoader
					.loadObject(ResourceUtils.getURL("classpath:reports/" + reportName).openStream());
			Map<String, Object> parameters = new HashMap<String, Object>();

			parameters.putAll(Utils.objectToMap(reportsItems));
			String reportDir = getClass().getResource("/reports/").toString();
			parameters.put("REPORT_DIR", reportDir);

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());
			byte[] reportContent = JasperExportManager.exportReportToPdf(jasperPrint);
			return reportContent;

		} catch (JRException | IOException ex) {
			ex.printStackTrace();
			log.error("error building report. ", ex);
			return null;
		}
	}

}
