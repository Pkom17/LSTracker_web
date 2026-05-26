package org.itech.labSampleTracker.config;

import java.util.stream.Collectors;

import org.springframework.web.servlet.HandlerInterceptor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthorizationInterceptor implements HandlerInterceptor {
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		try {
			String requestBody = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
			ObjectMapper mapper = new ObjectMapper();
			JsonNode jsonNode = mapper.readTree(requestBody);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
