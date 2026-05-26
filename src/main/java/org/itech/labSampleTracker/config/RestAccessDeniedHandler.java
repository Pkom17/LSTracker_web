package org.itech.labSampleTracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.Map;

public class RestAccessDeniedHandler implements AccessDeniedHandler {
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response,
			AccessDeniedException accessDeniedException) throws IOException {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getOutputStream(), Map.of("timestamp", System.currentTimeMillis(), "status", 403,
				"error", "Forbidden", "message", accessDeniedException.getMessage(), "path", request.getRequestURI()));
	}
}
