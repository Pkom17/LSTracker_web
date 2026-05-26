package org.itech.labSampleTracker.api.sync.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SamplePullResponse {
	private List<SampleDto> samples;
}