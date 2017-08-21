/**
 * 
 */
package com.ge.predix.gas.dhassert.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.gas.dhassert.client.TimeseriesClient;
import com.ge.predix.gas.dhassert.util.TimeSeriesConstants;
import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.AssetsRequest;
import com.ge.predix.solsvc.model.DataResponseVO;
import com.ge.predix.solsvc.model.StatusResponseVO;

/**
 * @author ramalapoli
 *
 */
@Service
public class AssetService {

	private static final Logger LOG = LoggerFactory.getLogger(AssetService.class);

	@Value(value = "${predix.asset.service.url}")
	private String predixAssetServiceUrl;

	@Value(value = "${predix.asset.service.zoneid}")
	private String assetZoneId;

	@Autowired
	private TimeseriesClient client;

	/**
	 * IngestAssetData
	 * 
	 * @param assetName
	 * @param request
	 * @return
	 */
	public String ingestAssetData(String assetName, AssetsRequest[] request) {

		try {

			String url = predixAssetServiceUrl + "/" + assetName;
			Header tokenHeader = client.getTimeseriesHeaders().get(0);
			String token = tokenHeader.getValue();

			LOG.info("Ingest asset token" + token);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			// Map<String, String> validateApikeyRequestMap = new
			// HashMap<String, String>();

			HttpHeaders validateHeaders = new HttpHeaders();
			validateHeaders.setContentType(MediaType.APPLICATION_JSON);
			validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
			validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);

			for (int x = 0; x < request.length; x = x + 1) {
				List<AssetsRequest> requestArray = new ArrayList<>();
				requestArray.add(request[x]);
				HttpEntity validityEntity = new HttpEntity(requestArray, validateHeaders);
				LOG.info("headers" + validityEntity.toString());
				ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, validityEntity, String.class);
				LOG.info(responseEntity.getBody());
			}

		} catch (Exception ex) {
			LOG.error("Error in ingestAssetData --", ex);
		}

		return "ok";
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

		String url = predixAssetServiceUrl + "/" + assetName;
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		// Map<String, String> validateApikeyRequestMap = new HashMap<String,
		// String>();

		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity validityEntity = new HttpEntity(validateHeaders);
		LOG.info("headers" + validityEntity.toString());

		ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.GET, validityEntity, String.class);

		return responseEntity.getBody().toString();

	}

	/**
	 * Method to fetch Asset ID based on Timeseries Identifier
	 * 
	 * @param tsIdentifier
	 * @return
	 */
	public Map<String, String> getAssetDatabyTSIdentifier(String tsIdentifier) {
		// TODO: Method to modified, and WIP

		LOG.info(" In getAssetDatabyTSIdentifier  and tiIdentifier is " + tsIdentifier);

		// default to model name
		String filterName = "modelName";

		if (tsIdentifier.toLowerCase().contains("ingest")) {

			filterName = TimeSeriesConstants.ASSET_TAG_NAME_INGEST;

		} else if (tsIdentifier.toLowerCase().contains("discharge")) {

			filterName = TimeSeriesConstants.ASSET_TAG_NAME_DISCHARGE;

		} else if (tsIdentifier.toLowerCase().contains("energyconsumption")) {

			filterName = TimeSeriesConstants.ASSET_TAG_NAME_ENERGYCONSUMPTION;
		}

		String url = predixAssetServiceUrl + "?filter=" + filterName + "=" + tsIdentifier;
		LOG.info("url   " + url);
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity validityEntity = new HttpEntity(validateHeaders);
		LOG.info("headers In getAssetDatabyTSIdentifier" + validityEntity.toString());

		ResponseEntity<List> filterResponseEntity = rt.exchange(url, HttpMethod.GET, validityEntity, List.class);

		LOG.info("responseEntity   " + filterResponseEntity.getBody().toString());
		Map<String, String> attributes = (Map<String, String>) filterResponseEntity.getBody().get(0);
		String assetName = attributes.get("collection");
		// String assetName = "pump9";
		LOG.info("assetName   " + assetName);
		// TODO - Hardcoding the value as Asset data seems to be corrupted
		assetName = "machine";
		url = predixAssetServiceUrl + "/" + assetName + "?filter=" + filterName + "=" + tsIdentifier;
		LOG.info("url   " + url);
		ResponseEntity<List> assetIdResponseEntity = rt.exchange(url, HttpMethod.GET, validityEntity, List.class);
		System.out.println("Called asset With Filter");
		Map<String, String> assetsRequest = (LinkedHashMap) assetIdResponseEntity.getBody().get(0);
		LOG.info("THE FINAL OBJECT IS   " + assetIdResponseEntity.getBody().toString());

		return assetsRequest;
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

		StringBuffer url = new StringBuffer();

		url.append(predixAssetServiceUrl).append("/").append(TimeSeriesConstants.ASSET_DEFAULT_NAME);

		if ((manufacturer != null && manufacturer.isPresent()) || (modelName != null && modelName.isPresent())
				|| (machineStatus != null && machineStatus.isPresent())
				|| (assetType != null && assetType.isPresent())) {

			url.append("?filter=( ");

			boolean moreThanOneFilter = false;

			if (manufacturer != null && manufacturer.isPresent()) {
				LOG.info("manufacturer is added to filter" + manufacturer);
				url.append(" manufacturer=").append(manufacturer.get());
				moreThanOneFilter = true;
			}

			if (modelName != null && modelName.isPresent()) {
				LOG.info("modelName is added to filter" + modelName);
				if (moreThanOneFilter) {
					url.append(" | ");
				}
				url.append(" modelName=").append(modelName.get());
				moreThanOneFilter = true;
			}

			if (machineStatus != null && machineStatus.isPresent()) {
				LOG.info("machineStatus is added to filter" + machineStatus);
				if (moreThanOneFilter) {
					url.append(" | ");
				}
				url.append(" analyticAttributes.machineStatus=").append(machineStatus.get());
				moreThanOneFilter = true;
			}

			if (assetType != null && assetType.isPresent()) {
				LOG.info("assetType is added to filter" + assetType);
				if (moreThanOneFilter) {
					url.append(" | ");
				}
				url.append(" pumpType=").append(assetType.get());
				moreThanOneFilter = true;
			}

			url.append(" )");
		}

		LOG.info("url" + url.toString());

		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		// Map<String, String> validateApikeyRequestMap = new HashMap<String,
		// String>();

		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity validityEntity = new HttpEntity(validateHeaders);
		LOG.info("headers" + validityEntity.toString());

		// ResponseEntity<String> responseEntity = rt.exchange(url.toString(),
		// HttpMethod.GET, validityEntity, String.class);
		ParameterizedTypeReference<List<Map<String, Object>>> responseType = new ParameterizedTypeReference<List<Map<String, Object>>>() {
		};
		ResponseEntity<List<Map<String, Object>>> responseEntity = rt.exchange(url.toString(), HttpMethod.GET,
				validityEntity, responseType);

		// Stub the data for now
		// List<AssetData> assetDataList =
		// JsonUtil.getDummyAssetDataForAssetManagementPage();

		return responseEntity.getBody();
		// return responseEntity.getBody().toString();

		// return assetDataList;
	}

	/**
	 * Get specific data for asset id for AssetData
	 * 
	 * @param assetName
	 * @param assetid
	 * @return assetid json
	 */
	public Map<String, String> getAssetDataByAssetId(String assetName, String assetid) {
		LOG.info("getAssetDataByAssetId");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetId :" + assetid);

		String url = predixAssetServiceUrl + "/" + assetName + "/" + assetid;
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		// Map<String, String> validateApikeyRequestMap = new HashMap<String,
		// String>();

		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity validityEntity = new HttpEntity(validateHeaders);
		LOG.info("headers" + validityEntity.toString());

		ResponseEntity<List> responseEntity = rt.exchange(url, HttpMethod.GET, validityEntity, List.class);

		Map<String, String> assetIDData = (LinkedHashMap) responseEntity.getBody().get(0);

		// AssetData assetData =
		// JsonUtil.getDummyAssetDataForAssetManagementPage().get(0);
		LOG.info("return" + assetIDData.toString());

		return assetIDData;

	}


	public AssetData[] getAssetDataListByAssetId(String assetName, String assetid) {
		LOG.info("getAssetDataByAssetId");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetId :" + assetid);

		String url = predixAssetServiceUrl + "/" + assetName + "/" + assetid;
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());

		// Map<String, String> validateApikeyRequestMap = new HashMap<String,
		// String>();

		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity validityEntity = new HttpEntity(validateHeaders);
		LOG.info("headers" + validityEntity.toString());

		// JSON to Java

		// JSON from URL to Object
		ResponseEntity<AssetData[]> response = null;
		AssetData[] obj = null;
		response = rt.exchange(url, HttpMethod.GET, validityEntity, AssetData[].class);
		obj = response.getBody();
		return obj;

	}


	/**PIOT-144
	O&G : As an admin, I want to be able to add new assets to the application, so that I can view their details and manage them
	*/
	public Object ingestAssetData(String assetName, AssetsRequest assetsRequest) {
		
		DataResponseVO dataResponseVO = new DataResponseVO();
		StatusResponseVO respVO = new StatusResponseVO();

		try {
			
			String url = predixAssetServiceUrl + "/" + assetName;
			Header tokenHeader = client.getTimeseriesHeaders().get(0);
			String token = tokenHeader.getValue();

			LOG.info("Ingest asset token" + token);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			//Map<String, String> validateApikeyRequestMap = new HashMap<String, String>();

			HttpHeaders validateHeaders = new HttpHeaders();
			validateHeaders.setContentType(MediaType.APPLICATION_JSON);
			validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
			validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, assetZoneId);
			
			List<AssetsRequest> assetsRequestList = new ArrayList<>();
			assetsRequestList.add(assetsRequest);
			
			HttpEntity validityEntity = new HttpEntity(assetsRequestList, validateHeaders);
			LOG.info("headers" + validityEntity.toString());
			ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, validityEntity, String.class);
			LOG.info(responseEntity.getBody());
			
			respVO.setResult("Success");
    		respVO.setMessage("Asset Created/Updated Successfully");

		} catch (Exception ex) {
			LOG.error("Error in ingestAssetData --" , ex);
			respVO.setResult("Failed");
    		respVO.setMessage("Failed to Create Asset. Please try again");
		}
		
		dataResponseVO.setData(respVO);
		
		return dataResponseVO;
		
		
	}
	
}
