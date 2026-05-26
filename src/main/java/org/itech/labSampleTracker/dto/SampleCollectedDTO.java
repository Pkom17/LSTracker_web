package org.itech.labSampleTracker.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SampleCollectedDTO {
	private Integer id;
	@PositiveOrZero
	private Integer userId;
	@PositiveOrZero
	private Integer sampleId;
	@PositiveOrZero
	private Integer circuitId;
	@PositiveOrZero
	private Integer sampleRetrievingId;
	@PositiveOrZero
	private Integer rejectionTypeId;
	@Size(max = 1000)
	private String rejectionComment;
	@PositiveOrZero
	private Integer destinationLabId;
	@PositiveOrZero
	private Integer siteId;
	@PositiveOrZero
	private Integer hubId;
	@PositiveOrZero
	private Integer labId;
	@Size(max = 100)
	private String patientIdentifier;
	@Size(max = 100)
	private String sampleIdentifier;
	@Size(max = 100)
	private String circuitNumber;
	@PositiveOrZero
	private Integer collectionStartMileage;
	@PositiveOrZero
	private Integer collectionEndMileage;
	@PositiveOrZero
	private Integer resultStartMileage;
	@PositiveOrZero
	private Integer resultEndMileage;
	@Size(max = 40)
	private String collectionDate;
	@Size(max = 40)
	private String deliveredAtHubDate;
	@Size(max = 40)
	private String deliveredAtReferenceLabDate;
	@Size(max = 40)
	private String acceptedAtHubDate;
	@Size(max = 40)
	private String acceptedAtReferenceLabDate;
	@Size(max = 40)
	private String rejectionDate;
	@Size(max = 50)
	private String sampleType;
	@Size(max = 100)
	private String labNumber;
	@Size(max = 40)
	private String analysisCompletedDate;
	@Size(max = 40)
	private String analysisReleasedDate;
	@Size(max = 40)
	private String analysisResultReportedDate;
	@Size(max = 255)
	private String requesterSiteName;
	@Size(max = 255)
	private String destinationLabName;
	@Size(max = 40)
	private String resultCollectionDate;
	@Size(max = 40)
	private String resultDeliveryDate;
	@Size(max = 64)
	private String status;
	@Size(max = 64)
	private String action;
}
