/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Version;

import org.hibernate.annotations.BatchSize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for entity "Sample"
 *
 * @author Pascal
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "sample")
public class Sample implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Version
	@Column(name = "version", nullable = false)
	private Long version;

	@Column(name = "uuid", nullable = true)
	private String uuid;

	@Column(name = "sample_type_id", nullable = false)
	private Integer sampleTypeId;

	@Column(name = "sample_nature", nullable = false)
	private String sampleNature;

	@Column(name = "sample_status_id", nullable = false)
	private Integer sampleStatusId;

	@Column(name = "sample_package_id", nullable = true)
	private Integer samplePackageId;

	@Column(name = "sample_retrieving_id", nullable = true)
	private Integer sampleRetrievingId;

	@Column(name = "reference_lab_id", nullable = true)
	private Integer labId;

	@Column(name = "destination_lab_id", nullable = true)
	private Integer destinationLabId;

	@Column(name = "hub_id", nullable = true)
	private Integer hubId;

	@Column(name = "sample_identifier", length = 50, nullable = true)
	private String sampleIdentifier;

	@Column(name = "patient_identifier", nullable = false, length = 50)
	private String patientIdentifier;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "pickup_date", nullable = true)
	private Date pickupDate;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "collection_date", nullable = false)
	private Date collectionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deliver_at_hub_date", nullable = true)
	private Date deliverAtHubDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "deliver_at_lab_date", nullable = true)
	private Date deliverAtLabDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accepted_at_hub_date", nullable = true)
	private Date acceptedAtHubDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "accepted_at_lab_date", nullable = true)
	private Date acceptedAtLabDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "rejection_date", nullable = true)
	private Date rejectionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "result_collection_date", nullable = true)
	private Date resultCollectionDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "result_delivery_date", nullable = true)
	private Date resultDeliveryDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "analysis_completed_date", nullable = true)
	private Date analysisCompletedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "analysis_released_date", nullable = true)
	private Date analysisReleasedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "result_reported_date", nullable = true)
	private Date resultReportedDate;

	@Column(name = "requester_site_name", nullable = true)
	private String requesterSiteName;

	@Column(name = "requester_site_id", nullable = true)
	private Long requesterSiteId;
	
	@Column(name = "requester_site_code", nullable = true)
	private String requesterSiteCode;

	@Column(name = "sample_conveyor_id", nullable = true)
	private Long sampleConveyorId;

	@Column(name = "result_collector_id", nullable = true)
	private Long resultCollectorId;

	@Column(name = "destination_lab_name", nullable = true)
	private String destinationLabName;

	@Column(name = "lab_number", length = 100)
	private String labNumber;

	@Column(name = "sample_referring_id", length = 100, nullable = true)
	private Integer sampleReferringId;

	@Column(name = "collection_start_mileage", nullable = true)
	private Integer collectionStartMileage;

	@Column(name = "collection_end_mileage", nullable = true)
	private Integer collectionEndMileage;

	@Column(name = "result_start_mileage", nullable = true)
	private Integer resultStartMileage;

	@Column(name = "result_end_mileage", nullable = true)
	private Integer resultEndMileage;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastupdated_at", nullable = false)
	private Date lastupdatedAt;

	@ManyToOne
	@JoinColumn(name = "sample_package_id", referencedColumnName = "id", insertable = false, updatable = false)
	private SamplePackage samplePackage;

	@ManyToOne
	@JoinColumn(name = "sample_retrieving_id", referencedColumnName = "id", insertable = false, updatable = false)
	private SampleRetrieving sampleRetrieving;

	@OneToMany(mappedBy = "sample")
	@BatchSize(size = 50)
	private List<SampleAtLab> listOfSampleAtLab;

	@OneToMany(mappedBy = "sample")
	@BatchSize(size = 50)
	private List<TrackingEvent> listOfTrackingEvent;

	@ManyToOne
	@JoinColumn(name = "sample_type_id", referencedColumnName = "id", insertable = false, updatable = false)
	private SampleType sampleType;
}
