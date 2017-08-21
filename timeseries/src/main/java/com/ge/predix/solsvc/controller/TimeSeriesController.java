/**
 * 
 */
package com.ge.predix.solsvc.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jettison.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.ResponseAssetGraphData;
import com.ge.predix.solsvc.model.TimeSeriesRequest;
import com.ge.predix.solsvc.service.AssetService;
import com.ge.predix.solsvc.service.TimeSeriesService;
import com.ge.predix.solsvc.util.JsonUtil;
import com.ge.predix.solsvc.util.TimeSeriesConstants;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author ramalapoli
 *
 */
@RestController
public class TimeSeriesController {
	
	@Autowired
	private JsonMapper jsonMapper;

	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesController.class);
	
	@Autowired
	private TimeSeriesService timeSeriesService;
	
	@Autowired
	private AssetService assetService;
	
	private static final String            PARAM_TRANSFER_ID         = "transferId";                               //$NON-NLS-1$
    private static final String            PARAM_RIVER_NAME          = "riverName";                                //$NON-NLS-1$
    private static final String            PARAM_CONTENT_TYPE        = "contentType";                              //$NON-NLS-1$
    private static final String            PARAM_CONTENT_DISPOSITION = "contentDisposition";                       //$NON-NLS-1$
    private static final String            PARAM_CONTENT_DESCRIPTION = "contentDescription";                       //$NON-NLS-1$
    private static final String            PARAM_TIMESTAMP           = "timestamp";                                //$NON-NLS-1$
    private static final String            PARAM_DATA                = "data";  
    
	/**
	 * Method to post data to TimeSeries
	 * @param request
	 * @return String
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value="/postTimeSeriesData",method=RequestMethod.POST)
	public String ingestTimeSeriesData(@RequestBody TimeSeriesRequest request)
	{
		LOG.info("ingestTimeSeriesData ******" + request);
		try {
			DatapointsIngestion dp = new DatapointsIngestion();
			dp.setMessageId(request.getMessageId());
			dp.setBody(request.getBody());
			return timeSeriesService.ingestTimeSeriesData(dp);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return "false";
	}
	
	/**
	 * Returns Time Series Data for 3 sensors for specific use case to Sulzer for pump9
	 * 
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gettimeSeriesData", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String gettimeSeriesDataForGraph() throws JsonProcessingException, JSONException {
		LOG.info("Inside gettimeSeriesData");
		String jsonResponseToClient = "";
		try {
			
			jsonResponseToClient = gettimeSeriesDataForGraphForPump("pump9");
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsonResponseToClient;
	}
	
	/**
	 * Returns Time Series Data 3 sensors  for specific pump
	 * 
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/gettimeSeriesData/pump/{pumpName}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String gettimeSeriesDataForGraphForPump(@PathVariable("pumpName") String pumpName) throws JsonProcessingException, JSONException {
		LOG.info("Inside gettimeSeriesData");
		String jsonResponseToClient = "";
		try {
			
			List<String> searchTagList = new ArrayList<String>();
			searchTagList.add("Discharge."+pumpName);
			searchTagList.add("EnergyConsumption."+pumpName);
			searchTagList.add("Ingest" + pumpName);
			
			DatapointsResponse dpr = timeSeriesService.queryForDatapoints("3h-ago", 20000, searchTagList);
			LOG.info("dpr = " + dpr.toString());
			
			jsonResponseToClient =JsonUtil.getJsonDataForGetTimeSeriesData(dpr, jsonMapper);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsonResponseToClient;
	}
	
	
	
	/**
	 * Returns Time Series Data for pump based on the Tag Name passed
	 * @param singleTag
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/timeSeriesData/{tagName}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String gettimeSeriesDatabyTag(@PathVariable("tagName") String singleTag) throws JsonProcessingException, JSONException {
		
		LOG.info("Inside gettimeSeriesData");
		String jsonResponseToClient = "";
		try {
			
			List<String> searchTagList = new ArrayList<String>();
			searchTagList.add(singleTag);
			
			DatapointsResponse dpr = timeSeriesService.queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 20000, searchTagList);
			LOG.info("dpr = " + dpr.toString());
			
			jsonResponseToClient =JsonUtil.getJsonDataForGetTimeSeriesData(dpr, jsonMapper);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsonResponseToClient;
		
	}
	
	/**
	 * Timeseries Data ingestion service from Excel File with 30 min interval
	 * 
	 * Task: PIOT-218
	 *	(PIOT-145 O&G : As an admin, I should be able to upload a data file, so that i can run analytics on itPIOT)
	 * 
	 * 
	 * @throws JSONException
	 * @throws IOException
	 */
	@RequestMapping(value = "/ingestDataFromExcel", method = RequestMethod.GET)
	public @ResponseBody void ingestDataFromExcel() throws JSONException, IOException {
		LOG.info("ingestDataFromExcel ");
		try{
			//int timeIntervalInSeconds = TimeSeriesConstants.TIME_INTERVAL_MIN_EXCEL*TimeSeriesConstants.ONE_MINUTE_IN_SECONDS;
			int timeIntervalInSeconds = 2*60*60*1000;
			timeSeriesService.ingestTimeSeriesDataFromExcel(TimeSeriesConstants.TIMESERIES_DATA_EXCEL_NAME,timeIntervalInSeconds );
		}catch(Exception ex){
			LOG.error("ingestDataFromExcel",ex);
		}
	}
	
	
	/**
	 * Returns Time Series Data for pump based on the Asset name and Id
	 * @param singleTag
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/timeSeriesDataByAsset/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public String timeSeriesDataByAsset(@PathVariable("assetName") String assetName,@PathVariable("assetId") String assetid )
	{
		
		LOG.info("Inside gettimeSeriesData");
		String jsonResponseToClient = "";
		try {
			
			DatapointsResponse dpr = timeSeriesService.queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 20000,assetName,assetid);
			LOG.info("dpr = " + dpr.toString());
			
			jsonResponseToClient =JsonUtil.getJsonDataForGetTimeSeriesData(dpr, jsonMapper);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return jsonResponseToClient;
	}
	
	/**
	 * Task: PIOT-188
	 *	(PIOT-133 O&G : We would like to connect an asset tag from data coming from Predix to time series)
     *
	 * 
	 * This method would be directly listening to the Predix Machinne
	 * MultipartFile data would having details about the Sensor 
	 * 
	 */
	@ResponseBody
    @RequestMapping(value = "/v1/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public String save(@RequestParam(value = "transferId") String transferId,
            @RequestParam(value = "riverName") String riverName,
            @RequestParam(value = PARAM_CONTENT_TYPE) String contentType,
            @RequestParam(value = PARAM_CONTENT_DISPOSITION, required = false) String contentDisposition,
            @RequestParam(value = PARAM_CONTENT_DESCRIPTION, required = false) String contentDescription,
            @RequestParam(value = PARAM_TIMESTAMP) String timestamp,
            @RequestParam(value = PARAM_DATA) MultipartFile data)
            throws Exception
	{
		LOG.info("Request parameters:" +  //$NON-NLS-1$
				"\n\t" + PARAM_TRANSFER_ID + ": " + transferId +  //$NON-NLS-1$ //$NON-NLS-2$
				"\n\t" + PARAM_RIVER_NAME + ": " + riverName +  //$NON-NLS-1$ //$NON-NLS-2$                
				"\n\t" + PARAM_CONTENT_TYPE + ": " + contentType +  //$NON-NLS-1$ //$NON-NLS-2$
				"\n\t" + PARAM_CONTENT_DISPOSITION + ": " + contentDisposition +  //$NON-NLS-1$ //$NON-NLS-2$
				"\n\t" + PARAM_CONTENT_DESCRIPTION + ": " + contentDescription +  //$NON-NLS-1$ //$NON-NLS-2$
				"\n\t" + PARAM_TIMESTAMP + ": " + timestamp + //$NON-NLS-1$ //$NON-NLS-2$
				"\n\t" + PARAM_DATA + data
				);

		ByteArrayInputStream stream = new   ByteArrayInputStream(data.getBytes());
		String myString = IOUtils.toString(stream, "UTF-8");
		LOG.info(myString);
		CharSequence newString = myString.substring(1, (myString.length() - 1 ));
		JSONObject jsonObj = new JSONObject(newString.toString());
		LOG.info("jsonObj = " + jsonObj.toString());
		
		String messageId=transferId+System.currentTimeMillis();
		String tagName = jsonObj.getString(TimeSeriesConstants.TIMESERIES_MC_TAGNAME);
		
		DatapointsIngestion dp=new DatapointsIngestion();
		dp.setMessageId(String.valueOf(System.currentTimeMillis()));
		List<Body> bodyList=new ArrayList<Body>();
		List<Object> datapoint1 = new ArrayList<Object>();
		
		datapoint1.add(jsonObj.get(TimeSeriesConstants.TIMESERIES_MC_TIMESTAMP));
		datapoint1.add(jsonObj.get(TimeSeriesConstants.TIMESERIES_MC_VALUE));
		String quality = jsonObj.getString(TimeSeriesConstants.TIMESERIES_MC_QUALITY_NAME);
		if(StringUtils.isNotBlank(quality) && quality.toUpperCase().contains(TimeSeriesConstants.TIMESERIES_MC_QUALITY_GOOD)){
			datapoint1.add(3); // quality
		}else if(StringUtils.isNotBlank(quality) && quality.toUpperCase().contains(TimeSeriesConstants.TIMESERIES_MC_QUALITY_BAD)){
			datapoint1.add(0); // quality
		}else{
			datapoint1.add(1); // quality
		}

		List<Object> datapoints = new ArrayList<Object>();
		datapoints.add(datapoint1);
		
		Map<String,String> assetDataMap = (Map<String, String>) assetService.getAssetDatabyTSIdentifier(tagName);
		com.ge.predix.entity.util.map.Map attributeDataMap = new com.ge.predix.entity.util.map.Map();
		if(assetDataMap!=null && !assetDataMap.isEmpty()){
			//loop a Map
			for (Map.Entry<String, String> entry : assetDataMap.entrySet()) {
				if(entry.getKey().equals(TimeSeriesConstants.TIMESERIES_ASSET_ATTR_URI)){
					attributeDataMap.put(entry.getKey(), entry.getValue());
				}else if(String.valueOf(entry.getValue()).equalsIgnoreCase(tagName)){
					attributeDataMap.put(TimeSeriesConstants.TIMESERIES_ASSET_ATTR_SENSOR, entry.getKey());
				}
			}
		}
		Body body=new Body();
		body.setName(tagName);
		body.setDatapoints(datapoints);
		body.setAttributes(attributeDataMap);
		bodyList.add(body);
		
		dp.setMessageId(messageId);
		dp.setBody(bodyList);
		
		timeSeriesService.ingestTimeSeriesData(dp);
		
		return "Success";
    }
	
	
	/**PIOT-191 
	 * PIOT-139 - O&G : view a graphical representation of intake, discharge pressure
	 * 					and energy consumption parameters over the past month 
	 * @param timeDuration
	 * @return Analytics
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getDischargePressureGraph/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String viewGraphDischargePressure(@PathVariable(value="assetName") String assetName,@PathVariable(value="assetId") String assetId)throws JsonProcessingException, JSONException {
		LOG.info("Inside viewGraphDischargePressure /Input Params:assetName "+assetName+" assetId:"+assetId);
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = timeSeriesService.getDischargePRessureGraph( assetName,assetId);
			//jsonResponseToClient = jsonResponseToClient.replaceAll("dischargePressure", "y");
		} catch (Exception ex) {
			LOG.error("Error in viewGraphDischargePressure --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		
		return jsonResponseToClient;
	}
	
	/**PIOT-191 
	 * PIOT-139 - O&G : view a graphical representation of intake, discharge pressure
	 * 					and energy consumption parameters over the past month 
	 * @param timeDuration
	 * @return Analytics
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getEnergyConsumptionGraph/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String viewGraphEnergyConsumption(@PathVariable(value="assetName") String assetName,@PathVariable(value="assetId") String assetId)throws JsonProcessingException, JSONException {
		LOG.info("Inside viewGraphEnergyConsumption/Input Params:assetName "+assetName+" assetId:"+assetId);
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = timeSeriesService.getEnergyConsumptionGraph( assetName,assetId);
		
		} catch (Exception ex) {
			LOG.error("Error in viewGraphEnergyConsumption --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
	
	
	
	/**
	 * getVibrationGraph
	 * PIOT-500 O&G : Asset Detail : Vibration input should display as a time series graph in Asset Detail pagePIOT-536
	 * @param timeDuration
	 * @return Analytics
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getVibrationGraph/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String getVibrationGraph(@PathVariable(value="assetName") String assetName,@PathVariable(value="assetId") String assetId)throws JsonProcessingException, JSONException {
		LOG.info("Inside getVibrationGraph/Input Params:assetName "+assetName+" assetId:"+assetId);
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = timeSeriesService.getVibrationGraph( assetName,assetId);
		
		} catch (Exception ex) {
			LOG.error("Error in getVibrationGraph --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
	
	
	/**
	 * This operation will retun timeseries data for specific identifier
	 * @param assetName
	 * @param assetId
	 * @param tsTagIdentifier
	 * @return
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getTimeseriesGraph/{assetName}/{assetId}/{identifier}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String getTimeseriesGraph(@PathVariable(value="assetName") String assetName,
										@PathVariable(value="assetId") String assetId, 
										@PathVariable(value="identifier") String tsTagIdentifier) throws JsonProcessingException, JSONException {
		LOG.info("Inside getTimeseriesGraph/Input Params:assetName "+assetName+" :: assetId:"+assetId+" ::tsTagIdentifier:"+tsTagIdentifier);
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = timeSeriesService.getTimeseriesGraph( assetName, assetId, tsTagIdentifier);
		
		} catch (Exception ex) {
			LOG.error("Error in getGraph --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
}
