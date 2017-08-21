/**
 * 
 */
package com.ge.predix.solsvc.service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.AssetsRequest;

/**
 * @author ramalapoli
 *
 */
@Service
public class AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetService.class);

	RestTemplate restTemplate = new RestTemplate();
	
	@Value(value = "${ingest.asset.data.url}")
	private String ingestAssertDataUrl;
	
	@Value(value = "${add.update.assert.data.url}")
	private String addUpdateAssertDataUrl;
	
	@Value(value = "${assert.data.url}")
	private String assertDataUrl;
	
	@Value(value = "${run.asset.analytics.url}")
	private String runAssertAnalyticsUrl;
	
	@Value(value = "${asset.data.ts.identifier.url}")
	private String assertDataTSIdentiferUrl;
	
	@Value(value = "${asset.data.filter.url}")
	private String assertDataFilterUrl;
	
	@Value(value = "${asset.data.asset.id.url}")
	private String assertDataByAssertIdUrl;
	
	@Value(value = "${asset.data.list.asset.id.url}")
	private String assertDataListByAssertIdUrl;

	/**
	 * IngestAssetData
	 * 
	 * @param assetName
	 * @param request
	 * @return
	 */
	public String ingestAssetData(String assetName, AssetsRequest[] request) {

	   HttpEntity<Object> entity = new HttpEntity<Object>(request);
	   ResponseEntity<String> response = restTemplate.exchange(ingestAssertDataUrl+"/"+assetName, HttpMethod.POST, entity, String.class);
	   
	   //TODO : below log is for testing purpose, remove after test
	   Map<Object, Object> mp  = (Map)getAssetDataByAssetId(assetName, "1");
	   Set<Entry<Object, Object>> es = mp.entrySet();
	   Iterator<Entry<Object, Object>> it = es.iterator();
	   while(it.hasNext()){
		   Entry<Object, Object> val = it.next();
		   LOG.info("ingestAssetData...value after insert:\n{}  ::  {}", val.getKey(), val.getValue());
	   }
	   
	     
	   return response.getBody();
	}
	
	public String ingestAssetData(String assetName, AssetsRequest request) {

		   HttpEntity<Object> entity = new HttpEntity<Object>(request);
		   ResponseEntity<String> response = restTemplate.exchange(addUpdateAssertDataUrl+"/"+assetName, HttpMethod.POST, entity, String.class);
		   return response.getBody();
		}

	/**
	 * getAssetDataByAssetName
	 * 
	 * @param assetName
	 * @return assetData
	 */
	public String getAssetDataByAssetName(String assetName) {
		LOG.info("getAssetDataByAssetName");
		LOG.info("AssetName :" + assetName);

		ResponseEntity<String> response = restTemplate.exchange(assertDataUrl+"/"+assetName, HttpMethod.GET, null, String.class);
		return response.getBody();

	}

	/**
	 * Method to fetch Asset ID based on Timeseries Identifier
	 * 
	 * @param tsIdentifier
	 * @return
	 */
	public Object getAssetDatabyTSIdentifier(String tsIdentifier) {

		LOG.info(" In getAssetDatabyTSIdentifier  and tiIdentifier is " + tsIdentifier);
		ResponseEntity<Object> response = restTemplate.exchange(assertDataTSIdentiferUrl+"/"+tsIdentifier, HttpMethod.GET, null, Object.class);
		return response.getBody();
	}

	/**
	 * PIOT-137 O&G : As an admin, i should be able to filter assets on the
	 * asset management page, so that i can quickly find and identify the
	 * relevant assets
	 * 
	 * Task : PIOT-230
	 * 
	 * Filter Options: 1. Manufacturer 2. Model Name
	 * 
	 * Model Name Data from Backend: List of assets with following attributes:
	 * Manufacturer Sr # Manufactured Date Model Name Machine HP Max Power
	 * Consumption Suction Pressure Required Current Latitude Current Longitude
	 * Last Maintenance Date
	 * 
	 * @param machineStatus
	 * 
	 * 
	 */
	public Object getAssetDatabyFilter(Optional<String> manufacturer, Optional<String> modelName,
			Optional<String> machineStatus, Optional<String> assetType) {

		
		StringBuilder url = new StringBuilder(assertDataFilterUrl);
		boolean hasParam = false;
		
		if(assetType!=null && assetType.isPresent()){
			if(hasParam){
				url.append("&assetType=").append(assetType.get());
			} 
			else {
				url.append("?assetType=").append(assetType.get());
				hasParam=true;
			}
		}
		
		if(manufacturer!=null && manufacturer.isPresent()){
			if(hasParam){
				url.append("&manufacturer=").append(manufacturer.get());
			} 
			else {
				url.append("?manufacturer=").append(manufacturer.get());
				hasParam=true;
			}
		}
		
		if(modelName!=null && modelName.isPresent()){
			if(hasParam){
				url.append("&modelName=").append(modelName.get());
			} 
			else {
				url.append("?modelName=").append(modelName.get());
				hasParam=true;
			}
		}
		
		if(machineStatus!=null && machineStatus.isPresent()){
			if(hasParam){
				url.append("&machineStatus=").append(machineStatus.get());
			} else {
				url.append("?machineStatus=").append(machineStatus.get());
			}
		}
		
		LOG.info("assertDataFilterUrl----------{}",url.toString());
		
		ResponseEntity<Object> response = restTemplate.exchange(url.toString(), HttpMethod.GET, null, Object.class);
		return response.getBody();
	}

	/**
	 * Get specific data for asset id for AssetData
	 * 
	 * @param assetName
	 * @param assetid
	 * @return assetid json
	 */
	public Object getAssetDataByAssetId(String assetName, String assetid) {
		LOG.info("getAssetDataByAssetId");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetId :" + assetid);

		StringBuilder url = new StringBuilder(assertDataByAssertIdUrl);
		url.append("/").append(assetName)
		.append("/").append(assetid);
		
		ResponseEntity<Object> response = restTemplate.exchange(url.toString(), HttpMethod.GET, null, Object.class);
		return response.getBody();
		
	}
	
	public Object getAssetDataListByAssetId(String assetName, String assetid) {
		LOG.info("getAssetDataByAssetId");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetId :" + assetid);

		StringBuilder url = new StringBuilder(assertDataListByAssertIdUrl);
		url.append("/").append(assetName)
		.append("/").append(assetid);
		
		ResponseEntity<AssetData[]> response = restTemplate.exchange(url.toString(), HttpMethod.GET, null, AssetData[].class);
		return response.getBody();
		
	}
	
}
