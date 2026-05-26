package org.itech.labSampleTracker.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDTO {
	private Integer id;
	private String lastName;
	private String firstName;
	@Pattern(regexp = "^[0-9a-zA-Z]{6,20}$", message = "Doit contenir au moins 6 caractères")
	private String login;
	@Pattern(regexp = "^((00225)?|(\\+225)?)([\\d]{10})$", message = "Numéro de phone invalide !")
	private String phoneContact;
	private String password;
	private String repassword;
	
}
