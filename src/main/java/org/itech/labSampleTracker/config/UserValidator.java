package org.itech.labSampleTracker.config;

import org.itech.labSampleTracker.dto.UserDTO;
import org.itech.labSampleTracker.service.AppUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class UserValidator implements Validator {

	@Autowired
	private AppUserService userService;

	@Override
	public boolean supports(Class<?> clazz) {
		return UserDTO.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		UserDTO user = (UserDTO) target;

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty");
		if (user.getFirstName().length() > 32) {
			errors.rejectValue("firstName", "Size.userForm.firstname");
		}
		if (user.getLastName().length() > 32) {
			errors.rejectValue("lastName", "Size.userForm.lastname");
		}
		if (userService.findUserByLogin(user.getLogin()) != null) {
			errors.rejectValue("login", "Duplicate.userForm.login");
		}

		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty");
		if (user.getPassword().length() < 8 || user.getPassword().length() > 32) {
			errors.rejectValue("password", "Size.userForm.password");
		}

		if (!user.getRepassword().equals(user.getPassword())) {
			errors.rejectValue("passwordConfirm", "Diff.userForm.passwordConfirm");
		}

	}

}
