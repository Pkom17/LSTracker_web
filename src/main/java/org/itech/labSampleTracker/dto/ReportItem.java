package org.itech.labSampleTracker.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReportItem {

	private String startDate = null;
	private String endDate = null;
	
	private String regionName = null;
	private String districtName = null;
	private String conveyorName = null;
	private String labName = null;
	private String focalPointName = null;
	

	// collected
	private Integer collectedBICount = 0;
	private Integer collectedBSCount = 0;
	private Integer collectedCVCount = 0;
	private Integer collectedEIDCount = 0;
	private Integer collectedHPVCount = 0;
	private Integer collectedTBCount = 0;
	private Integer collectedPREPCount = 0;
	private Integer collectedIVSACount = 0;
	private Integer collectedAUTRECount = 0;

	// delivered
	private Integer deliveredBICountDistrict = 0;
	private Integer deliveredBSCountDistrict = 0;
	private Integer deliveredCVCountDistrict = 0;
	private Integer deliveredEIDCountDistrict = 0;
	private Integer deliveredHPVCountDistrict = 0;
	private Integer deliveredTBCountDistrict = 0;
	private Integer deliveredPREPCountDistrict = 0;
	private Integer deliveredIVSACountDistrict = 0;
	private Integer deliveredAUTRECountDistrict = 0;
	private Integer deliveredBICountRelais = 0;
	private Integer deliveredBSCountRelais = 0;
	private Integer deliveredCVCountRelais = 0;
	private Integer deliveredEIDCountRelais = 0;
	private Integer deliveredHPVCountRelais = 0;
	private Integer deliveredTBCountRelais = 0;
	private Integer deliveredPREPCountRelais = 0;
	private Integer deliveredIVSACountRelais = 0;
	private Integer deliveredAUTRECountRelais = 0;
	private Integer deliveredTBCountCAT = 0;
	private Integer deliveredBICountBM = 0;
	private Integer deliveredBSCountBM = 0;
	private Integer deliveredCVCountBM = 0;
	private Integer deliveredEIDCountBM = 0;
	private Integer deliveredHPVCountBM = 0;
	private Integer deliveredTBCountBM = 0;
	private Integer deliveredPREPCountBM = 0;
	private Integer deliveredIVSACountBM = 0;
	private Integer deliveredAUTRECountBM = 0;

	// accepted
	private Integer acceptedBICount = 0;
	private Integer acceptedBSCount = 0;
	private Integer acceptedCVCount = 0;
	private Integer acceptedEIDCount = 0;
	private Integer acceptedHPVCount = 0;
	private Integer acceptedTBCount = 0;
	private Integer acceptedPREPCount = 0;
	private Integer acceptedIVSACount = 0;
	private Integer acceptedAUTRECount = 0;

	// result ready
	private Integer resultReadyBICount = 0;
	private Integer resultReadyBSCount = 0;
	private Integer resultReadyCVCount = 0;
	private Integer resultReadyEIDCount = 0;
	private Integer resultReadyHPVCount = 0;
	private Integer resultReadyTBCount = 0;
	private Integer resultReadyPREPCount = 0;
	private Integer resultReadyIVSACount = 0;
	private Integer resultReadyAUTRECount = 0;

	// result Collected
	private Integer resultCollectedBICount = 0;
	private Integer resultCollectedBSCount = 0;
	private Integer resultCollectedCVCount = 0;
	private Integer resultCollectedEIDCount = 0;
	private Integer resultCollectedHPVCount = 0;
	private Integer resultCollectedTBCount = 0;
	private Integer resultCollectedPREPCount = 0;
	private Integer resultCollectedIVSACount = 0;
	private Integer resultCollectedAUTRECount = 0;

	// result Delivered
	private Integer resultDeliveredBICount = 0;
	private Integer resultDeliveredBSCount = 0;
	private Integer resultDeliveredCVCount = 0;
	private Integer resultDeliveredEIDCount = 0;
	private Integer resultDeliveredHPVCount = 0;
	private Integer resultDeliveredTBCount = 0;
	private Integer resultDeliveredPREPCount = 0;
	private Integer resultDeliveredIVSACount = 0;
	private Integer resultDeliveredAUTRECount = 0;

	// non-conform - rejected
	private Integer rejectedBICount = 0;
	private Integer rejectedBSCount = 0;
	private Integer rejectedCVCount = 0;
	private Integer rejectedEIDCount = 0;
	private Integer rejectedHPVCount = 0;
	private Integer rejectedTBCount = 0;
	private Integer rejectedPREPCount = 0;
	private Integer rejectedIVSACount = 0;
	private Integer rejectedAUTRECount = 0;

	// failed
	private Integer failedBICount = 0;
	private Integer failedBSCount = 0;
	private Integer failedCVCount = 0;
	private Integer failedEIDCount = 0;
	private Integer failedHPVCount = 0;
	private Integer failedTBCount = 0;
	private Integer failedPREPCount = 0;
	private Integer failedIVSACount = 0;
	private Integer failedAUTRECount = 0;

	// TAT
	private Integer BITAT = 0;
	private Integer BSTAT = 0;
	private Integer CVTAT = 0;
	private Integer EIDTAT = 0;
	private Integer HPVTAT = 0;
	private Integer TBTAT = 0;
	private Integer PREPTAT = 0;
	private Integer IVSATAT = 0;
	private Integer AUTRETAT = 0;
}
