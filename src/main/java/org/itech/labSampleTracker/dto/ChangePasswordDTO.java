package org.itech.labSampleTracker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = { "oldPassword", "password", "repassword" })
public class ChangePasswordDTO {

	private Integer userId;

	@NotBlank(message = "Le mot de passe actuel est requis")
	private String oldPassword;

	@NotBlank(message = "Le nouveau mot de passe est requis")
	@Size(min = 8, max = 128, message = "Doit comporter entre 8 et 128 caractères")
	@Pattern(
		regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()\\-\\[{}\\]:;',?/*~$^+=<>]).{8,128}$",
		message = "Doit contenir au moins 1 majuscule, 1 minuscule, 1 chiffre et 1 caractère spécial"
	)
	private String password;

	@NotBlank(message = "La confirmation est requise")
	private String repassword;
}
