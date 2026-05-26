/* 
 * Created on 2024-03-31 ( Date ISO 2024-03-31 - Time 19:08:04 )
 */
package org.itech.labSampleTracker.controller;

import org.itech.labSampleTracker.entities.SampleStatus;
import org.itech.labSampleTracker.service.SampleStatusService;


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
* <h2>SampleStatusController</h2>
*/
@Controller
@RequestMapping("/samplestatus")
public class SampleStatusController {


	@Autowired
	private SampleStatusService samplestatusService;

	@PostMapping(value = "")
	public String createSampleStatus(@Valid SampleStatus model) {

   		 SampleStatus data = samplestatusService.create(model);
    		if (data != null) {
    			return "";
  			  } else {
    			return "";
   			 }
    }

    @GetMapping(value = "")
    public String getAllSampleStatus(Model model) {
        List<SampleStatus> samplestatusList = samplestatusService.getAll();

		model.addAttribute("SampleStatuss",samplestatusList);

        return "";
    }

        @GetMapping(value = "/{id}")
    public String getOneSampleStatus(@PathVariable("id") Integer id, Model model) {

            SampleStatus e = samplestatusService.getOne(id);
            if (e == null) {
            	return "";
            }
			model.addAttribute("samplestatus",e);
            return "";
    }



}
