package org.itech.labSampleTracker.api.sync.dto;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class SampleDto {
	private String external_id; 
	private String uuid;

	private Long sample_conveyor;
	private Integer referring_sample_id;

	private Integer start_mileage;
	private Integer end_mileage;
	private Integer result_start_mileage;
	private Integer result_end_mileage;

	private String from_site_name;
	private String from_site_code;
	private Long from_site_id;
	private Long destination_lab_id;
	private Long delivered_lab_id;

	private String sample_identifier;
	private String patient_identifier;
	private String sample_type; 
	private String sample_nature;

	private String collection_date;
	private String pickup_date;
	private String delivered_date;
	private String accepted_date;
	private String lab_number;
	private String sample_status;

	private String analysis_started_date;
	private String analysis_completed_date;
	private String analysis_released_date;
	private String result_collection_date;
	private String result_delivered_date;
	private Long result_collector;

	private Long rejection_type_id;
	private String rejection_comment;
	private String rejection_date;

	private String created_at;
	private String lastupdated_at;
}
