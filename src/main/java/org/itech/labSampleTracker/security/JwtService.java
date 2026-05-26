package org.itech.labSampleTracker.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

	private final SecretKey key;
	private final String issuer;
	private final long expirationMinutes;

	public JwtService(@Value("${app.jwt.secret}") String secret, @Value("${app.jwt.issuer}") String issuer,
			@Value("${app.jwt.expiration-minutes}") long expirationMinutes) {
		this.key = Keys.hmacShaKeyFor(io.jsonwebtoken.io.Decoders.BASE64.decode(secret));
		this.issuer = issuer;
		this.expirationMinutes = expirationMinutes;
	}

	public String generateToken(String username, String role, Long userId) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(expirationMinutes * 60);
		return Jwts.builder().setSubject(username).setIssuer(issuer).setIssuedAt(Date.from(now))
				.setExpiration(Date.from(exp)).addClaims(Map.of("role", role, "user_id", userId))
				.signWith(key, SignatureAlgorithm.HS256).compact();
	}

	public Jws<Claims> parse(String token) {
		return Jwts.parserBuilder().requireIssuer(issuer).setSigningKey(key).build().parseClaimsJws(token);
	}
}
