package org.itech.labSampleTracker.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.Map;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		mapper.writeValue(response.getOutputStream(), Map.of("timestamp", System.currentTimeMillis(), "status", 401,
				"error", "Unauthorized", "message", authException.getMessage(), "path", request.getRequestURI()));
	}
}
