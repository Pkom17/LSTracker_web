/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import org.itech.labSampleTracker.entities.SampleType;
import org.itech.labSampleTracker.service.SampleTypeService;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import java.util.List;



/**
* <h2>SampleTypeController</h2>
*/
@Controller
@RequestMapping("/sampletype")
public class SampleTypeController {


	@Autowired
	private SampleTypeService sampletypeService;

	@PostMapping(value = "")
	public String createSampleType(@Valid SampleType model) {

   		 SampleType data = sampletypeService.create(model);
    		if (data != null) {
    			return "";
  			  } else {
    			return "";
   			 }
    }

    @GetMapping(value = "")
    public String getAllSampleType(Model model) {
        List<SampleType> sampletypeList = sampletypeService.getAll();

		model.addAttribute("SampleTypes",sampletypeList);

        return "";
    }

        @GetMapping(value = "/{id}")
    public String getOneSampleType(@PathVariable("id") Integer id, Model model) {

            SampleType e = sampletypeService.getOne(id);
            if (e == null) {
            	return "";
            }
			model.addAttribute("sampletype",e);
            return "";
    }



}
