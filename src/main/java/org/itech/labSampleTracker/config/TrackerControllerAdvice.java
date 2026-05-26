package org.itech.labSampleTracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class TrackerControllerAdvice {

	@Value("${app.version}")
	private String appVersion;

	@ModelAttribute
	public void addSessionAttributes(HttpSession session, Model model) {
		if (session.getAttribute("appVersion") == null) {
			session.setAttribute("appVersion", appVersion);
		}

		model.addAttribute("appVersion", session.getAttribute("appVersion"));
	}
}
