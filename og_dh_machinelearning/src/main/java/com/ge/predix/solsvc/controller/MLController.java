/**
 * 
 */
package com.ge.predix.solsvc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.http.Header;
//import org.codehaus.jettison.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
//import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AssetsRequest;
import com.ge.predix.solsvc.model.TimeSeriesRequest;
import com.ge.predix.solsvc.service.MLService;
import com.ge.predix.solsvc.util.MLConstants;
import com.ge.predix.solsvc.model.MLInputData;

/**
 * @author vvenkateswaran
 *
 */
@RestController
public class MLController {

	private static final Logger LOG = LoggerFactory.getLogger(MLController.class);
	
	@Autowired
	private MLService mlService;
		
	
	/**
	 * IngestAssetData
	 * @param assetName
	 * @param assetsRequest
	 */
	@RequestMapping(value = "/ingestAssetData/{assetName}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public void ingestAssetData(@PathVariable("assetName") String assetName,@RequestBody AssetsRequest[] assetsRequest)
	{
		LOG.info("Start Ingest asset");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetRequest :" + assetsRequest);
		//mlService.updateMLAssetData(assetName, assetsRequest);
		LOG.info("End Ingest asset");
	}
	
	@RequestMapping(value = "/classify/{assetName}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object classifyMLAssetData(@PathVariable("assetName") String assetName)
	{
		LOG.info("getAssetData");
		LOG.info("AssetName :" + assetName);
		return mlService.packageMLData(assetName);
		
		
		//return mlService.ingestMLAssetData(assetName, mlService.packageMLData(assetName));
	}
	
	@RequestMapping(value = "/linearregression/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public String train(@PathVariable("assetName") String assetName, @PathVariable("assetId") String assetId)
	{
		LOG.info("getAssetData");
		LOG.info("assetName=" + assetName, "assetId="+assetId);
		return mlService.linearRegression(assetName, assetId);
	}
	
	/**
	@RequestMapping(value = "/generatetraindata/{", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object generateTrainData(@PathVariable("assetName") String assetName)
	{
		LOG.info("getAssetData");
		LOG.info("AssetName :" + assetName);
		return mlService.generateData(assetName);
		
		//return mlService.ingestMLAssetData(assetName, mlService.packageMLData(assetName));
	}
	
	**/
	
	
}
