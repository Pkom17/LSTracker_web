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
 * Domain class for entity "SampleAtLab"
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
@Table(name = "sample_at_lab")
public class SampleAtLab implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "sample_id", nullable = false)
	private Integer sampleId;

	@Column(name = "lab_id", nullable = false)
	private Integer labId;

	@Column(name = "lab_number", length = 100)
	private String labNumber;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "received_date")
	private Date receivedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "started_date")
	private Date startedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "completed_date")
	private Date completedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "released_date")
	private Date releasedDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "result_collect_date")
	private Date resultCollectDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at", nullable = false)
	private Date createdAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "lastupdated_at", nullable = false)
	private Date lastupdatedAt;

	@ManyToOne
	@JoinColumn(name = "lab_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Lab lab;

	@ManyToOne
	@JoinColumn(name = "sample_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Sample sample;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id);
		sb.append("|");
		sb.append(sampleId);
		sb.append("|");
		sb.append(labId);
		sb.append("|");
		sb.append(labNumber);
		sb.append("|");
		sb.append(receivedDate);
		sb.append("|");
		sb.append(startedDate);
		sb.append("|");
		sb.append(completedDate);
		sb.append("|");
		sb.append(releasedDate);
		sb.append("|");
		sb.append(resultCollectDate);
		sb.append("|");
		sb.append(createdAt);
		sb.append("|");
		sb.append(lastupdatedAt);
		return sb.toString();
	}

}
