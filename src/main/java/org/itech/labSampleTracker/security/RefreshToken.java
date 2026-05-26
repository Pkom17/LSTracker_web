package org.itech.labSampleTracker.security;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "refresh_tokens", indexes = { @Index(name = "idx_rt_user", columnList = "userId"),
		@Index(name = "idx_rt_token_hash", columnList = "tokenHash", unique = true) })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;
	private String username;

	/** Authority ex: ADMIN / LAB_TECH / CONVEYOR */
	private String role;

	@Column(nullable = false, length = 128)
	private String tokenHash;

	private Instant expiresAt;
	private Instant createdAt;
	private String createdByIp;

	private boolean revoked;
	private String replacedByTokenHash;
}
