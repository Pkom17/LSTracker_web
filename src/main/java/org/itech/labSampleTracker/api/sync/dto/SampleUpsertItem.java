package org.itech.labSampleTracker.api.sync.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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
public class SampleUpsertItem {
	@Size(max = 36)
	private String external_id;

	@NotBlank
	@Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
			message = "uuid must be a valid UUID")
	private String uuid;

	@PositiveOrZero
	private Long sample_conveyor;

	@Size(max = 100)
	private String referring_sample_id;

	@PositiveOrZero
	@Max(value = 9_999_999, message = "start_mileage must not exceed 9 999 999 km")
	private Integer start_mileage;

	@PositiveOrZero
	@Max(value = 9_999_999, message = "end_mileage must not exceed 9 999 999 km")
	private Integer end_mileage;

	@PositiveOrZero
	@Max(value = 9_999_999)
	private Integer result_start_mileage;

	@PositiveOrZero
	@Max(value = 9_999_999)
	private Integer result_end_mileage;

	@Size(max = 255)
	private String from_site_name;
	@Size(max = 64)
	private String from_site_code;
	@PositiveOrZero
	private Long from_site_id;
	@PositiveOrZero
	private Long destination_lab_id;
	@PositiveOrZero
	private Long delivered_lab_id;

	@Size(max = 100)
	private String sample_identifier;
	@Size(max = 100)
	private String patient_identifier;

	@Size(max = 50)
	private String sample_type;
	@Size(max = 100)
	private String sample_nature;

	@Size(max = 40)
	private String collection_date;
	@Size(max = 40)
	private String pickup_date;
	@Size(max = 40)
	private String delivered_date;
	@Size(max = 40)
	private String accepted_date;
	@Size(max = 100)
	private String lab_number;
	@Size(max = 64)
	private String sample_status;

	@Size(max = 40)
	private String analysis_started_date;
	@Size(max = 40)
	private String analysis_completed_date;
	@Size(max = 40)
	private String analysis_released_date;
	@Size(max = 40)
	private String result_collection_date;
	@Size(max = 40)
	private String result_delivered_date;
	@PositiveOrZero
	private Long result_collector;

	@PositiveOrZero
	private Long rejection_type_id;
	@Size(max = 1000)
	private String rejection_comment;
	@Size(max = 40)
	private String rejection_date;

	@Size(max = 40)
	private String created_at;
	@Size(max = 40)
	private String lastupdated_at;
}