/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Domain class for entity "TrackingEvent"
 *
 * @author Pascal
 *
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
//@ToString
@Entity
@Table(name = "tracking_event")
public class TrackingEvent implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "sample_id", nullable = false)
	private Integer sampleId;

	@Column(name = "longitude")
	private Double longitude;

	@Column(name = "latitude")
	private Double latitude;

	@Column(name = "mileage")
	private Integer mileage;

	@Column(name = "org_id")
	private Integer orgId;

	@Column(name = "app_user_id", nullable = false)
	private Integer appUserId;

	@Column(name = "org_type")
	private String orgType;

	@Column(name = "event_type")
	private String eventType;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private Date createdAt;

	@ManyToOne
	@JoinColumn(name = "sample_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Sample sample;

	@ManyToOne
	@JoinColumn(name = "app_user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private AppUser appUser;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append("|");
		sb.append(sampleId);
		sb.append("|");
		sb.append(longitude);
		sb.append("|");
		sb.append(latitude);
		sb.append("|");
		sb.append(createdAt);
		return sb.toString();
	}

}
