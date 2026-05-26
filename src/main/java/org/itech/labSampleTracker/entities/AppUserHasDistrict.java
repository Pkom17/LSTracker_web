/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:03 )
 */
package org.itech.labSampleTracker.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
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
@Table(name = "app_user_has_district")
public class AppUserHasDistrict implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "district_id", nullable = false)
	private Integer districtId;

	@Column(name = "app_user_id", nullable = false)
	private Integer appUserId;

	@ManyToOne
	@JoinColumn(name = "app_user_id", referencedColumnName = "id", insertable = false, updatable = false)
	private AppUser appUser;

	@ManyToOne
	@JoinColumn(name = "district_id", referencedColumnName = "id", insertable = false, updatable = false)
	private District district;

}
