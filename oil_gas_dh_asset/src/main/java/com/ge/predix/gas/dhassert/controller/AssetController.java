/**
 * 
 */
package com.ge.predix.gas.dhassert.controller;

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

import com.ge.predix.gas.dhassert.service.AssetService;
import com.ge.predix.solsvc.model.AssetsRequest;


@RestController
public class AssetController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AssetController.class);
	
	@Autowired
	private AssetService assetService;
	
	/**
	 * IngestAssetData
	 * @param assetName
	 * @param assetsRequest
	 */
	@RequestMapping(value = "/ingestAssetData/{assetName}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public String ingestAssetData(@PathVariable("assetName") String assetName,@RequestBody AssetsRequest[] assetsRequest)
	{
		LOG.info("Start Ingest asset");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetRequest :" + assetsRequest);
		LOG.info("End Ingest asset");
		return assetService.ingestAssetData(assetName, assetsRequest);
	}
	
/*	@RequestMapping(value = "/ingestAssetData/{assetName}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object ingestAssetData(@PathVariable("assetName") String assetName,@RequestBody AssetsRequest assetsRequest)
	{
		LOG.info("Start Ingest asset");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetRequest :" + assetsRequest);
		LOG.info("End Ingest asset");
		return assetService.ingestAssetData(assetName, assetsRequest);
	}*/
	
	
	/**
	 **PIOT-144
		O&G : As an admin, I want to be able to add new assets to the application, so that I can view their details and manage them
	 * @param assetName
	 * @param assetsRequest
	 */
	@RequestMapping(value = "/addUpdateAssetData/{assetName}", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object ingestAssetDataById(@PathVariable("assetName") String assetName,
										@RequestBody AssetsRequest assetsRequest)
	{
		LOG.info("Start Ingest asset");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetRequest :" + assetsRequest);
		return assetService.ingestAssetData(assetName,assetsRequest);
	}
	
	/**
	 * 
	 * Task PIOT-182
	 * 
	 * User story: PIOT-124 O&G : As an admin, I want to view a list of all assets so that I can keep track of and maintain active assets
	 * 
	 * 	Manufacturer
		Sr #
		Manufactured Date
		Model Name
		Machine HP
		Max Power Consumption
		Suction Pressure Required
		Current Latitude
		Current Longitude
		Last Maintenance Date
	 * 
	 * 
	 * GetAssetData
	 * @param assetName
	 * @return assetData
	 */
	@RequestMapping(value = "/getAssetData/{assetName}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public String getAssetData(@PathVariable("assetName") String assetName)
	{
		LOG.info("getAssetData");
		LOG.info("AssetName :" + assetName);
		return assetService.getAssetDataByAssetName(assetName);

	}
	
	
	@RequestMapping(value = "/runAssetAnalytics/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Map<String, String> runAssetAnalytics(@PathVariable("assetName") String assetName,@PathVariable("assetId") String assetId)
	{
		LOG.info("runAssetAnalytics");
		LOG.info("AssetName :" + assetName);
		LOG.info("AssetId :" + assetId);
		//String results = analyticsService.executeEfficiency();
		//LOG.info(results);
		return assetService.getAssetDataByAssetId(assetName,assetId);
		

	}
	
	
	
	/**
	 * Method to fetch Asset ID based on Timeseries Identifier
	 * @param assetName
	 * @param assetid
	 * @return
	 */
	@RequestMapping(value = "/getAssetDatabyTSIdentifier/{tiIdentifier:.+}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAssetDatabyTSIdentifier(@PathVariable("tiIdentifier") String tiIdentifier)
	{
		LOG.info("getAssetDatabyTSIdentifier");
		LOG.info("tiIdentifier :" + tiIdentifier);
		return assetService.getAssetDatabyTSIdentifier(tiIdentifier);

	}
	
	
	
	/**
	 * PIOT-137 O&G : As an admin, i should be able to filter assets on the asset management page, 
	 * so that i can quickly find and identify the relevant assets
	 * 
	 * Task : PIOT-230
	 * 
	 * Filter Options:
	 * 1. Manufacturer
	 * 2. Model Name
	 * 
	 *  Model Name
	 *  Data from Backend:
 	 *  List of assets with following attributes:
		Manufacturer
		Sr #
		Manufactured Date
		Model Name
		Machine HP
		Max Power Consumption
		Suction Pressure Required
		Current Latitude
		Current Longitude
		Last Maintenance Date

	 * 
	 */
	@RequestMapping(value = "/getAssetDatabyFilter", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAssetDatabyFilter(
			@RequestParam("manufacturer") Optional<String> manufacturer,
			@RequestParam("modelName") Optional<String> modelName,
			@RequestParam("machineStatus") Optional<String> machineStatus,
			@PathVariable("assetType") Optional<String>  assetType
			){
		
		return assetService.getAssetDatabyFilter(manufacturer,modelName,machineStatus,assetType);
		
		//return null;
	}
	
	
	/**
	 * 
	 * 
	 *	PIOT-122 O&G : As an admin, i should be able to filter the assets on the map based on their operating condition, 
	 *	so that i can only view necessary assets on the map.
	 * 
	 * Task: PIOT-227
	 * 
	 *  Filter Options:
		Data to UI:
 		Asset Data with Latitude /Longitude/Country and Machine status.
 		
		 Machine Status 
		PERFORMING_OPTIMALLY - (green) If sensor readings are within limit of asset parameters (Suction Pressure>=NPSHR)
		PREDICTING_FAILURE - (Yellow) If If sensor readings are outside limit of asset parameters (Suction Pressure<NPSHR)
		FAILED  - (Red) (No power consumption)
		UNDERGOING_MAINTENANCE - (Blue) (This will have to be set on the asset page)
		NO_DATA - (Orange)
	 * 
	 * 
	 * @param machineStatus
	 * @return AssetData
	 */
	/*@RequestMapping(value = "/getAssetDataForMapbyFilter", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAssetDataForMapbyFilter(
			@RequestParam("machineStatus") Optional<String> machineStatus
			){
		
		return assetService.getAssetDataForMapbyFilter(machineStatus);
		
		//return null;
	}*/
	
	
	/**
	 * 
	 *	PIOT-121 O&G : As an admin, i should be able to search for assets using the asset Id from the dashboard, 
	 *		so that i can quickly view details about an asset


	 * 
	 * Task: PIOT-226
	 * 
	 * 
	 * *  Model Name
	 *  Data from Backend:
 	 *  List of assets with following attributes:
	 * 	Manufacturer
		Sr #
		Manufactured Date
		Model Name
		Machine HP
		Max Power Consumption
		Suction Pressure Required
		Current Latitude
		Current Longitude
		Last Maintenance Date
 	
	 * 
	 * @param assetName
	 * @return assetData
	 */
	@RequestMapping(value = "/getAssetDataByAssetId/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAssetDataByAssetId(@PathVariable("assetName") String assetName,@PathVariable("assetId") String assetid )
	{
		LOG.info("getAssetData");
		LOG.info("AssetName :" + assetName);
		return assetService.getAssetDataByAssetId(assetName,assetid);
		//return assetService.getAssetDataListByAssetId(assetName, assetid);

	}
	
	@RequestMapping(value = "/getAssetDataListByAssetId/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public Object getAssetDataListByAssetId(@PathVariable("assetName") String assetName,@PathVariable("assetId") String assetid )
	{
		LOG.info("getAssetData");
		LOG.info("AssetName :" + assetName);
		return assetService.getAssetDataListByAssetId(assetName,assetid);
		//return assetService.getAssetDataListByAssetId(assetName, assetid);

	}
	
}
