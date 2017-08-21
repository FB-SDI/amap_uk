/**
 * 
 */
/**
 * @author shrshroff
 *
 */
package com.ge.predix.gas.dhnotifications.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.solsvc.model.AnalyticAttributes;
import com.ge.predix.solsvc.model.StatusResponseVO;
import com.ge.predix.solsvc.model.servicemax.AssetDetailsVO;
import com.ge.predix.solsvc.model.servicemax.SVMXCaseCreateRequestVO;

@Service
public class AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetService.class);

	RestTemplate restTemplate = new RestTemplate();

	@Value("${predix.servicemax.create.url}")
	private String createCaseUrl;

	StatusResponseVO response;

	public String getNotification() {
		response.setMessage("Success");
		return response.getMessage();

	}

	public String createNotificationCase(Object updatedAssertData) {
		LOG.info("Inside createNotificationCase....");
		Map<String, String> assetData = (Map<String, String>) updatedAssertData;
		try {

			/*
			 * Set<Entry<String, String>> entrySet = assetData.entrySet();
			 * Iterator<Entry<String, String>> it = entrySet.iterator();
			 * 
			 * while(it.hasNext()){ Entry<String, String> entryItem= it.next();
			 * LOG.info(entryItem.getKey()+" :: "+ entryItem.getValue()); }
			 */
			SVMXCaseCreateRequestVO requestVO = getCaseCreateRequest(assetData);
			LOG.info("requestVO :: {}", requestVO.toString());
			LOG.info("createCaseUrl :: {}", createCaseUrl);

			HttpEntity<Object> entity = new HttpEntity<Object>(requestVO);
			// ResponseEntity<Object> response =
			// restTemplate.exchange(createCaseUrl, HttpMethod.POST, entity,
			// Object.class);

			if (response != null) {
				// String stringResp = response.getBody();
				// LOG.info("createNotificationCase...response:{}",
				// response.getBody());
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		LOG.info("exit createNotificationCase....");

		LOG.info("Assest Data" + assetData.toString());

		ObjectMapper mapper = new ObjectMapper();
		LOG.info("AA Data");
		//Map<String, Object> assetMap = it.next(); //so here you don't need a potentially unsafe cast
	    Object temp = assetData.get("analyticAttributes");
	
	    Map<String,Integer> machineStatusMap = (LinkedHashMap)temp;
		//LOG.info("analyticAttributes:"+machineStatusMap.get("machineEfficiencyLevel").toString());
		int machineStatus =  machineStatusMap.get("machineEfficiencyLevel");
		
		try {
			// Times Series Object
			// Add record to Timeseries
			System.out.println("Effe" + machineStatus);
		//	AnalyticAttributes analytics = mapper.readValue(assetData.get("analyticAttributes"),AnalyticAttributes.class);
		//	LOG.info("Cavitation" + analytics.getCavitation().toString());
		} catch (Exception e) {
			LOG.info("Excpetion" + e.toString());
			e.printStackTrace();
		}
		return "Success";
	}

	private SVMXCaseCreateRequestVO getCaseCreateRequest(Map<String, String> assetData) {
		SVMXCaseCreateRequestVO requestVO = new SVMXCaseCreateRequestVO();
		List<AssetDetailsVO> assetDetails = new ArrayList<AssetDetailsVO>();
		AssetDetailsVO assetDetail = new AssetDetailsVO();
		requestVO.setSubject("Machine Efficiency is low.");
		String assetSerialNo = assetData.get("serialNumber");
		assetDetail.setDescription("Machine Efficiency is low. Please, check the asset data.");
		assetDetail.setSerialNumber(assetSerialNo);
		assetDetail.setErrorCode(getErrorCode(assetData));
		assetDetails.add(assetDetail);
		requestVO.setCaseDetails(assetDetails);

		return requestVO;
	}

	// TODO: Need to get the error Code Dynamically , for now Hardcoded
	private String getErrorCode(Map<String, String> assetData) {

		return "PREDIX-0004";

	}

}