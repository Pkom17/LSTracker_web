/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import org.itech.labSampleTracker.entities.SampleRejectionType;
import org.itech.labSampleTracker.service.SampleRejectionTypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <h2>SampleRejectionTypeController</h2>
 */
@Controller
@RequestMapping("/samplerejectiontype")
public class SampleRejectionTypeController {

	@Autowired
	private SampleRejectionTypeService samplerejectiontypeService;

	@PostMapping(value = "")
	public String createSampleRejectionType(@Valid SampleRejectionType model) {

		SampleRejectionType data = samplerejectiontypeService.create(model);
		if (data != null) {
			return "";
		} else {
			return "";
		}
	}

	@GetMapping(value = "")
	public String getAllSampleRejectionType(Model model) {
		List<SampleRejectionType> samplerejectiontypeList = samplerejectiontypeService.getAll();

		model.addAttribute("SampleRejectionTypes", samplerejectiontypeList);

		return "";
	}

	@GetMapping(value = "/{id}")
	public String getOneSampleRejectionType(@PathVariable("id") Integer id, Model model) {

		SampleRejectionType e = samplerejectiontypeService.getOne(id);
		if (e == null) {
			return "";
		}
		model.addAttribute("samplerejectiontype", e);
		return "";
	}

	@GetMapping(value = "names", produces = "application/json")
	public ResponseEntity<List<Map<String, Object>>> getLabNames() {
		List<Map<String, Object>> types = samplerejectiontypeService.getIdAndNames();
		return new ResponseEntity<List<Map<String, Object>>>(types, HttpStatus.OK);
	}

}
