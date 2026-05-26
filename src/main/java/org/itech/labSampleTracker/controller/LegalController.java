package org.itech.labSampleTracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalController {

	@GetMapping({ "/legal/privacy" })
	public String privacy(Model model) {
		return "legal/privacy";
	}

	@GetMapping({ "/legal/terms" })
	public String terms(Model model) {
		return "legal/terms";
	}
}