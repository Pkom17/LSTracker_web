package org.itech.labSampleTracker.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Domain class for entity "AppUser"
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
@Table(name = "app_user")
public class AppUser implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "login", nullable = false, length = 255)
	private String login;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Column(name = "password_reset")
	private Boolean passwordReset;

	@Column(name = "is_active", nullable = false)
	private Boolean isActive;

	@Column(name = "is_locked", nullable = false)
	private Boolean isLocked;

	@Column(name = "first_name", length = 100)
	private String firstName;

	@Column(name = "last_name", length = 100)
	private String lastName;

	@Column(name = "phone_contact", nullable = false, length = 45)
	private String phoneContact;

	@Column(name = "role", nullable = false, length = 45)
	private String role;

	@Column(name = "user_type", nullable = false, length = 100)
	private String userType;

	@Column(name = "user_level", nullable = true, length = 100)
	private String userLevel;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_login")
	private Date lastLogin;

	@Temporal(TemporalType.DATE)
	@Column(name = "password_expire_at")
	private Date passwordExpireAt;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_at")
	private Date createdAt;

	@Column(name = "created_by")
	private Integer createdBy;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_updated_at")
	private Date lastUpdatedAt;

	@Column(name = "last_updated_by")
	private Integer lastUpdatedBy;

	@OneToMany(mappedBy = "appUser")
	private List<Ride> listOfRide;

	public String getAuthorities() {
		return this.role;
	}
}
