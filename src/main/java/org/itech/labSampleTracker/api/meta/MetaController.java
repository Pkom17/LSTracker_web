package org.itech.labSampleTracker.api.meta;

import org.itech.labSampleTracker.api.meta.MetaDtos.MetaFullResponse;
import org.itech.labSampleTracker.controller.BaseController;
import org.itech.labSampleTracker.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api_v2/meta")
@RequiredArgsConstructor
public class MetaController extends BaseController {

	@Autowired
	private MetaService metaService;

	@GetMapping(value = "/full", produces = MediaType.APPLICATION_JSON_VALUE)
	public MetaFullResponse full() {
		AppUser user = accountService.getOne(getCurrentUserId());
		return metaService.loadAll(user);
	}
}