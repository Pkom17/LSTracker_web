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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain class for entity "SampleRetrieving"
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
@Table(name = "sample_retrieving")
public class SampleRetrieving implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

    @Column(name="ride_id", nullable=true)
    private Integer rideId ;

	@Column(name = "site_id", nullable = false)
	private Integer siteId;

	@Column(name = "app_user_id", nullable = false)
	private Integer appUserId;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sample_retrieve_date", nullable = false)
	private Date sampleRetrieveDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "entered_date", nullable = false)
	private Date enteredDate;

	@ManyToOne
	@JoinColumn(name = "app_user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private AppUser appUser;

	@ManyToOne
	@JoinColumn(name = "ride_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Ride ride;

	@ManyToOne
	@JoinColumn(name = "site_id", referencedColumnName = "id", insertable = false, updatable = false)
	private Site site;

	@OneToMany(mappedBy = "sampleRetrieving")
	private List<Sample> listOfSample;

}
