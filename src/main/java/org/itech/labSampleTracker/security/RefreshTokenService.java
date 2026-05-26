package org.itech.labSampleTracker.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RefreshTokenService {

	@Autowired
	private RefreshTokenRepository repo;
	@Value("${app.jwt.refresh-ttl-days:30}")
	private long refreshTtlDays;

	public static String sha256(String s) {
		try {
			var md = MessageDigest.getInstance("SHA-256");
			return Base64.getEncoder().encodeToString(md.digest(s.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public String issue(Long userId, String username, String role, String clientIp) {
		String raw = "rt_" + UUID.randomUUID() + "_" + System.nanoTime();
		String hash = sha256(raw);
		var now = Instant.now();
		var expires = now.plusSeconds(refreshTtlDays * 24 * 3600);
		var entity = RefreshToken.builder().userId(userId).username(username).role(role).tokenHash(hash).createdAt(now)
				.expiresAt(expires).createdByIp(clientIp).revoked(false).build();
		repo.save(entity);
		return raw; // on renvoie la valeur RAW, stockée hashée côté DB
	}

	public RefreshToken verify(String rawToken) {
		var hash = sha256(rawToken);
		var rt = repo.findByTokenHash(hash).orElseThrow(() -> new IllegalArgumentException("Invalid refresh"));
		if (rt.isRevoked() || rt.getExpiresAt().isBefore(Instant.now()))
			throw new IllegalArgumentException("Expired/Revoked");
		return rt;
	}

	public String rotate(RefreshToken old, String clientIp) {
		String newRaw = issue(old.getUserId(), old.getUsername(), old.getRole(), clientIp);
		old.setRevoked(true);
		old.setReplacedByTokenHash(sha256(newRaw));
		repo.save(old);
		return newRaw;
	}

	public void revoke(String rawToken) {
		repo.findByTokenHash(sha256(rawToken)).ifPresent(rt -> {
			rt.setRevoked(true);
			repo.save(rt);
		});
	}
}
