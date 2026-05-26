package org.itech.labSampleTracker.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserOrgDTO {
	private Integer userId;
	private List<Integer> regions;
	private List<Integer> districts;
	private List<Integer> sites;
	private List<Integer> labs;
	private List<Integer> circuits;
}
