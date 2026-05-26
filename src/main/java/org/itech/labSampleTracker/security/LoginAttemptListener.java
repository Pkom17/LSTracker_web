package org.itech.labSampleTracker.security;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.itech.labSampleTracker.entities.AppUser;
import org.itech.labSampleTracker.service.AppUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

/**
 * Locks an AppUser after {@value #MAX_ATTEMPTS} consecutive failed login
 * attempts and clears the counter on success.
 *
 * In-memory counter (no DB migration needed): a per-instance
 * {@link ConcurrentHashMap} indexed by lowercased username. The window
 * resets after {@value #COUNTER_TTL_MINUTES} minutes of inactivity to
 * avoid permanently locking accounts after sporadic typos.
 *
 * Note: counters are per JVM. On a multi-instance deployment users could
 * exhaust attempts faster than {@code MAX_ATTEMPTS * instances}. Acceptable
 * trade-off for now; for cross-instance protection move the cache to Redis.
 */
@Component
@RequiredArgsConstructor
public class LoginAttemptListener {

	private static final Logger log = LoggerFactory.getLogger(LoginAttemptListener.class);

	private static final int MAX_ATTEMPTS = 5;
	private static final long COUNTER_TTL_MINUTES = 30;

	private final AppUserService appUserService;

	private final ConcurrentHashMap<String, Attempts> attempts = new ConcurrentHashMap<>();

	@EventListener
	public void onFailure(AbstractAuthenticationFailureEvent event) {
		String principal = principalName(event.getAuthentication().getName());
		if (principal == null) return;

		Attempts a = attempts.compute(principal, (k, current) -> {
			if (current == null || current.isExpired()) {
				return new Attempts();
			}
			current.touch();
			current.counter.incrementAndGet();
			return current;
		});

		int count = a.counter.get();
		log.info("Login failure for '{}' ({}/{} consecutive attempts in window)",
				principal, count, MAX_ATTEMPTS);

		if (count >= MAX_ATTEMPTS) {
			lockUserIfPossible(principal);
		}
	}

	@EventListener
	public void onSuccess(AuthenticationSuccessEvent event) {
		String principal = principalName(extractUsername(event));
		if (principal != null) {
			Attempts removed = attempts.remove(principal);
			if (removed != null) {
				log.debug("Cleared {} failed-attempt counter for '{}'", removed.counter.get(), principal);
			}
		}
	}

	private void lockUserIfPossible(String username) {
		try {
			AppUser user = appUserService.findUserByLogin(username);
			if (user == null) {
				// Username may be wrong → nothing to lock. Counter still
				// expires after the TTL so the attacker cannot bombard
				// indefinitely without consequences (log noise + IP scrutiny).
				log.warn("Cannot lock unknown user '{}' (reached {} attempts)", username, MAX_ATTEMPTS);
				return;
			}
			if (Boolean.TRUE.equals(user.getIsLocked())) {
				return; // already locked
			}
			user.setIsLocked(true);
			user.setLastUpdatedAt(new java.util.Date());
			appUserService.update(user, false);
			log.warn("Account '{}' locked after {} failed login attempts", username, MAX_ATTEMPTS);
		} catch (Exception ex) {
			log.error("Failed to lock user '{}' after threshold: {}", username, ex.getMessage(), ex);
		}
	}

	private static String principalName(String raw) {
		return (raw == null || raw.isBlank()) ? null : raw.toLowerCase();
	}

	private static String extractUsername(AuthenticationSuccessEvent event) {
		Object p = event.getAuthentication().getPrincipal();
		if (p instanceof UserDetails ud) return ud.getUsername();
		if (p instanceof String s) return s;
		return event.getAuthentication().getName();
	}

	/** Per-username failure record with sliding TTL. */
	private static class Attempts {
		final AtomicInteger counter = new AtomicInteger(1);
		volatile Instant lastTouched = Instant.now();

		void touch() { lastTouched = Instant.now(); }

		boolean isExpired() {
			return Duration.between(lastTouched, Instant.now())
					.compareTo(Duration.ofMinutes(COUNTER_TTL_MINUTES)) > 0;
		}
	}
}
