package org.itech.labSampleTracker.dto;

public class DashboardSummaryDTO {

	public long allCount, inTransit, received, accepted, resultReady, resultCollected, resultOnSite, rejected, failed,
			delivered;

	public DashboardSummaryDTO(Object[] r) {
		this.allCount = ((Number) r[0]).longValue();
		this.inTransit = ((Number) r[1]).longValue();
		this.received = ((Number) r[2]).longValue();
		this.accepted = ((Number) r[3]).longValue();
		this.resultReady = ((Number) r[4]).longValue();
		this.resultCollected = ((Number) r[5]).longValue();
		this.resultOnSite = ((Number) r[6]).longValue();
		this.rejected = ((Number) r[7]).longValue();
		this.failed = ((Number) r[8]).longValue();

		// les cumuls
		this.delivered = this.received + this.accepted + this.resultCollected + this.resultOnSite + this.resultReady
				+ this.failed + this.rejected;
		this.resultReady = this.resultCollected + this.resultOnSite + this.resultReady;

		this.resultCollected = this.resultCollected + this.resultOnSite;

	}
}