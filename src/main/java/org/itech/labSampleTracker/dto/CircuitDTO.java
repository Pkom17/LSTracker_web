package org.itech.labSampleTracker.dto;

import java.util.List;

import org.itech.labSampleTracker.entities.Circuit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CircuitDTO {
	private Circuit circuit;
	private List<Integer> sites;
	
}
