/**
 * 
 */
/**
 * @author shrshroff
 *
 */
package com.ge.predix.gas.dhnotifications.controller;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ge.predix.solsvc.model.StatusResponseVO;
import com.ge.predix.gas.dhnotifications.service.AssetService;


@RestController
public class AssetController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AssetController.class);
	StatusResponseVO response;
	
	@Autowired
	AssetService assetService;
	
	/**
	 * IputParam
	 * OutputParam
	 */
	/*@RequestMapping(value = "/notificationDH", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public String ingestAssetData(@RequestBody Object updatedAssertData)
	{
		LOG.info("Notification DH called");
		//return assetService.getNotification();
		return "Success";		
	}*/
	
	
	@RequestMapping(value = "/notificationDH", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public String createNotificationCase(@RequestBody Object updatedAssertData)
	{
		LOG.info("Notification DH called....{}", updatedAssertData);
		
		return assetService.createNotificationCase(updatedAssertData);		
	}
}
