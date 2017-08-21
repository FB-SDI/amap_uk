package com.ge.predix.oilgas.servicemax.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ge.predix.oilgas.servicemax.service.ServiceMaxService;
import com.ge.predix.solsvc.model.servicemax.SVMXCaseCreateRequestVO;

@RestController
public class ServiceMaxController {

	@Autowired
	ServiceMaxService service;
	
	private static final Logger LOG = LoggerFactory.getLogger(ServiceMaxController.class);

	@RequestMapping(value = "case/create", method = RequestMethod.POST)
	public Object createCase(@RequestBody SVMXCaseCreateRequestVO requestVO) {

		return service.createCase(requestVO);
	}
	
	
	@RequestMapping(value = "/cases/serialNumber/{serialNumber:.+}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getCasesBySerialNumber(@PathVariable("serialNumber") String serialNumber )
	{
		LOG.info("getCasesBySerialNumber :" + serialNumber);
		return service.getCasesBySerialNumber(serialNumber);

	}
}
