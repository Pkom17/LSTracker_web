package org.itech.labSampleTracker.config;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorMessage {
	private int statusCode;
	private OffsetDateTime timestamp;
	private String error;
	private String message;
	private String path;
	private String requestId;
	private List<FieldError> fieldErrors;
	private Map<String, Object> details;

	@Data
	@Builder
	@AllArgsConstructor
	@NoArgsConstructor
	public static class FieldError {
		private String field;
		private Object rejectedValue;
		private String message;
	}
}
