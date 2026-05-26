package org.itech.labSampleTracker.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SampleUpdateDTO{

	private Integer id;
	
	private Integer requesterSiteId;

	private Integer sampleTypeId;

	private Integer sampleStatusId;

	private Integer labId;

	private Integer destinationLabId;

	private Integer hubId;

	private String patientIdentifier;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm")
	private Date collectionDate;

	private Date deliverAtHubDate;

	private Date deliverAtLabDate;

	private Date acceptedAtHubDate;

	private Date acceptedAtLabDate;

	private Date rejectionDate;
	
	private Integer rejectionTypeId;
	
	private String rejectionComment;

	private Date resultCollectionDate;

	private Date resultDeliveryDate;

	private Date analysisCompletedDate;

	private Date analysisReleasedDate;

	private Date resultReportedDate;

	private String labNumber;

	private Integer collectionStartMileage;

	private Integer collectionEndMileage;

	private Integer resultStartMileage;

	private Integer resultEndMileage;

	private Date createdAt;

	private Date lastupdatedAt;

}
