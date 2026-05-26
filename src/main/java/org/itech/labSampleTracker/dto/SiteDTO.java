package org.itech.labSampleTracker.dto;

import lombok.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SiteDTO {

	private Integer id;

	private String name;

	private String dhisCode;

	private String datimCode;

	private Integer districtId;

	private Double longitude;

	private Double latitude;

	private Date lastUpdatedAt;

}
