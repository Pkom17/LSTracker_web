package org.itech.labSampleTracker.config;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Populates SLF4J MDC with requestId and userName for every HTTP request,
 * so logs can be traced end-to-end. Exposes X-Request-Id response header.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class RequestContextFilter extends OncePerRequestFilter {

	public static final String MDC_REQUEST_ID = "requestId";
	public static final String MDC_USER = "user";
	public static final String HEADER_REQUEST_ID = "X-Request-Id";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String requestId = request.getHeader(HEADER_REQUEST_ID);
		if (requestId == null || requestId.isBlank()) {
			requestId = UUID.randomUUID().toString();
		}
		MDC.put(MDC_REQUEST_ID, requestId);

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
			MDC.put(MDC_USER, auth.getName());
		}

		try {
			response.setHeader(HEADER_REQUEST_ID, requestId);
			chain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_REQUEST_ID);
			MDC.remove(MDC_USER);
		}
	}
}
