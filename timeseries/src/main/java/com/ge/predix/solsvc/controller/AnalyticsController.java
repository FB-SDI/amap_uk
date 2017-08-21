/**
 * 
 */
package com.ge.predix.solsvc.controller;

import javax.servlet.ServletContext;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.NotificationResponse;
import com.ge.predix.solsvc.model.TimeSeriesAnalyticsRequestVO;
import com.ge.predix.solsvc.service.AnalyticsService;
import com.ge.predix.solsvc.service.AssetService;

import okhttp3.OkHttpClient;
import okhttp3.Response;



/**
 * @author ramalapoli
 *
 */
@RestController
public class AnalyticsController {
	
	@Autowired
	private JsonMapper jsonMapper;
	public ServletContext context;
	
	@Autowired
	private AssetService assetService;

	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsController.class);
	
	@Autowired
	private AnalyticsService analyticsService;
	
	/**
	 * Get Analytics Average by Agregating TimeSeries Data Analytics.head.suction.{pumpid}
	 * The analytics data is inserted externally by following command
	 * @param pumpID
	 * @return Average
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
/*	
 * Commenting this as this is from old sulzer requirement.
 * @SuppressWarnings("unchecked")
	@RequestMapping(value = "/analytics/gauge/{tag}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String getAverageOfAnalyticsForPump(@PathVariable("tag") String pumpID) throws JsonProcessingException, JSONException {
		LOG.info("Inside gettimeSeriesData");
		String jsonResponseToClient = "";
		try {
			jsonResponseToClient =analyticsService.getAverageOfAnalyticsForPump(pumpID, "1h", "1h-ago");
		
		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --" , ex);
		}

		return jsonResponseToClient;
	}*/
	
	/**
	 * This method takes care of pulling data from timeseries and calling Analytics service and finally sending back notification to UI
	 * @return ArrayList<NotificationResponseModel>
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	
	/*	
	 * Commenting this as this is from old sulzer requirement.
	@SuppressWarnings("null")
	@RequestMapping(value = "/getNotifications", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public ArrayList<NotificationResponseModel> getNotification() throws JsonProcessingException, JSONException {
		
		LOG.info("getNotification started");
		ArrayList<NotificationResponseModel> notifyList = null;
		try {
			notifyList = analyticsService.getNotificationForAsset("pump9", "1y-ago", "1s-ago");
		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --" , ex);
		}
		return notifyList;	
	}
	*/
	
	
	/**PIOT-231 
	 * PIOT-120 - O&G : As an admin, i should be able to filter the notifications, so that i can quickly find and identify certain notification
	 * 
	 * @param criteria : In case to return 'AllNotifications' then, criteria=null
	 * 				 : In case of any specific status then, criteria='failure,main,preditive,nodata'
	 * @param startDate : startDate for the query
	 * @param endDate : endDate for the query
	 * @return
	 * List<NotificationResponse>
	 * NotificationResponse model has attributes as below:
	 * 	1. machineStatus
	 * 	2. statusIcon
	 * 	3. date
	 * 	4. message
	 * 	5. assetId
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("null")
	@RequestMapping(value = "/getNotificationsByCriteria", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public NotificationResponse getStatusNotifications(
										@RequestParam(value="criteria", required=false) String criteria,
										@RequestParam(value="startDate", required=false) String startDate,
										@RequestParam(value="endDate", required=false) String endDate) throws JsonProcessingException, JSONException {
		LOG.info("getStatusNotifications started.");
		
		NotificationResponse notifications = analyticsService.getMachineStatusNotifications(criteria, startDate, endDate);
		
		LOG.info("getStatusNotifications ended.");
		return notifications;
	}
	
	/**PIOT-206 
	 * PIOT-131 - O&G : activate analytics with the press of a button 
	 * 
	 * @param timeDuration
	 * @return Analytics
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/analytics/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public AssetData activateAnalytics(@PathVariable(value="assetName") String assetName,@PathVariable(value="assetId")String assetId,
			@RequestParam(value="timeDuration") String timeDuration)throws JsonProcessingException, JSONException {
		LOG.info("Inside activateAnalytics/Input Params:assetName "+assetName+" assetId:"+assetId+" timeDuration:"+timeDuration);
		String jsonResponseToClient = null ;
		String assetID = assetName+assetId;
		try {
			jsonResponseToClient = analyticsService.getAnalytics( assetName, assetId, timeDuration);
		
		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		
		try{
		//Calling machine learning data handler endpoint to update machine assets with predicted statuses
		String mlurl = "https://og-dh-machinelearning.run.aws-usw02-pr.ice.predix.io/classify/training";
		RestTemplate rtML = new RestTemplate();
		rtML.getMessageConverters().add(new StringHttpMessageConverter());
		HttpHeaders MLHeaders = new HttpHeaders();
		//assetHeaders.setContentType(MediaType.APPLICATION_JSON);
		MLHeaders.add("Connection", "keep-alive");
		HttpEntity MLEntity = new HttpEntity(MLHeaders);
		LOG.info("headers" + MLEntity.toString());
		ResponseEntity<String> MLResponseEntity = rtML.exchange(mlurl, HttpMethod.GET, MLEntity, String.class);
		LOG.info("ml="+MLResponseEntity.toString());
		} catch (Exception ex) {
			LOG.error("Error in calling ML data handler --" , ex);
		}
		
		AssetData[] assetData = (AssetData[]) assetService.getAssetDataListByAssetId(assetName, assetId);
		return  assetData[0] ;
	}
	
	/**PIOT-425 - Predictive Analytics	 * 
	 * 
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/analytics/train/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String activateTrainAnalytics(@PathVariable(value="assetName") String assetName,@PathVariable(value="assetId")String assetId,
			@RequestParam(value="timeDuration") String timeDuration)throws JsonProcessingException, JSONException {
		LOG.info("Inside activateTrainAnalytics/Input Params:assetName "+assetName+" assetId:"+assetId+" timeDuration:"+timeDuration);
		String jsonResponseToClient = null ;
		String assetID = assetName+assetId;
		try {
			jsonResponseToClient = analyticsService.getTrainAnalytics( assetID, timeDuration);
		
		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
	
	

	
	/**
	 * This method will execute and trigger the analytics to calculate the analytics and update the 'MachEfficiency1' timeseries with 
	 * processed data. Currently, it will process data for machine/1 (as it is hardcoded in analytics code), where it will get the 
	 * data for 'machineHosrePower' using below endpoint:
	 * https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetDataByAssetId/machine/1
	 * @return
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/analytics/efficiency", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  String executeEfficiencyAnalytics()throws JsonProcessingException, JSONException {
		LOG.info("Inside excuteEfficiencyAnalytics");
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = analyticsService.executeEfficiency();
		
		} catch (Exception ex) {
			LOG.error("Error in excuteEfficiencyAnalytics --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
	
	
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/analytics/getEfficiency/{range}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String executeEfficiencyAnalyticsUsingRange(@PathVariable("range") String rangeValue)throws JsonProcessingException, JSONException {
		LOG.info("Inside excuteEfficiencyAnalytics ....with rangeValue: {}", rangeValue);
		String jsonResponseToClient = null ;
		try {
			jsonResponseToClient = analyticsService.executeEfficiencyAnalytics(rangeValue);
		
		} catch (Exception ex) {
			LOG.error("Error in excuteEfficiencyAnalytics --" , ex);
		}
		LOG.info("jsonResponseToClient: "+jsonResponseToClient);
		return jsonResponseToClient;
	}
	
	
	/**
	 * 
	 * Get PredictedCavitation Failure for Asset ID
	 * 
	 * Currently getting latest data point from Timeseries as predictive analysis is not ready
	 * 
	 * @param assetName
	 * @param assetid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/predictedCavitationFailure/{assetName}/{assetId}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public  Object getPredictedCavitationFailure(
			@PathVariable(value="assetName") String assetName,
			@PathVariable(value="assetId")String assetId)throws Exception {
		LOG.info("Inside getPredictedCavitationFailureParams:assetName "+assetName+" assetId:"+assetId);
		Object jsonResponseToClient = null ;
		String assetID = assetName+assetId;
		try {
			jsonResponseToClient = analyticsService.getPredictedCavitationFailure(assetName, assetId);
		
		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --" , ex);
		}
		return jsonResponseToClient;
	}

	/**
	 * 
	 * Start analytics runtime target process
	 * 
	 * author: vvenkateswaran
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@Value(value ="${analytics.orchestration.configurationId}") String testruntimeID;
	@RequestMapping(value = "/analytics/target/{hours}", method = RequestMethod.GET,produces=MediaType.APPLICATION_JSON_VALUE)
	public int replaceArtifact(
			@PathVariable(value="hours") int hours)throws Exception {
		LOG.info("Inside replaceArtifact: hours "+hours);
		
		
		try{
		
		String token;	
		//String runtimeID = "791c5fee-fb71-48eb-8ec2-8d311a72b492";
		String runtimeID = "2b6dd65d-3fdb-445d-848c-b4b4aa5fc9ee";

		
		LOG.info("test="+testruntimeID);
		
		//String runtimeID = "04e68b16-c5a3-4bc1-840c-699b7da65056";
		String orchID = "2b6dd65d-3fdb-445d-848c-b4b4aa5fc9ee";
		String zoneID = "92daaff9-9654-483e-9ed5-ac0c81510504";
		String artifactID = "d74c9a02-2737-467d-872e-fa5e17fea054";
		String filename = 	 "\\deploy\\port-to-field.json\"\r\nContent-Type";
		//TODO Find Spring function to get Resource Path
		
		//get Oauth token
		OkHttpClient tokenclient = new OkHttpClient();
		okhttp3.MediaType tokenmediaType = okhttp3.MediaType.parse("application/x-www-form-urlencoded");
		okhttp3.RequestBody tokenbody = okhttp3.RequestBody.create(tokenmediaType, "grant_type=client_credentials&=");
		okhttp3.Request tokenrequest = new okhttp3.Request.Builder()
		  .url("https://53b73921-acac-43d0-bef2-a8ec78d216ec.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token")
		  .post(tokenbody)
		  .addHeader("authorization", "Basic dGVzdF9jbGllbnQ6dGVzdF9jbGllbnQ=")
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "4ad03a6a-c238-21ad-fdec-5bc2b1a90054")
		  .addHeader("content-type", "application/x-www-form-urlencoded")
		  .build();
		Response tokenresponse = tokenclient.newCall(tokenrequest).execute();
		LOG.info(tokenresponse.toString());
		String tokenrbody =  tokenresponse.body().string();
		JSONObject tokenjObj = new JSONObject(tokenrbody);
		token = tokenjObj.getString("access_token");
		LOG.info("token="+token);
		//String path = context.getContextPath();
		//LOG.info("context="+path);
		
		//Create runtime entry	
		/*
		OkHttpClient createclient = new OkHttpClient();
		okhttp3.MediaType createmediaType = okhttp3.MediaType.parse("application/json");
		okhttp3.RequestBody createbody = okhttp3.RequestBody.create(createmediaType, "{\r\n\t\"name\": \"efficiency-analytic-runtime-test\",\r\n\t\"author\": \"VV\",\r\n\t\"description\": \"test\"\r\n}");
		okhttp3.Request createrequest = new okhttp3.Request.Builder()
		  .url("https://predix-analytics-config-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations")
		  .post(createbody)
		  .addHeader("predix-zone-id", "92daaff9-9654-483e-9ed5-ac0c81510504")
		  .addHeader("content-type", "application/json")
		  .addHeader("authorization", "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIwYjZlOTM3YWQ3Yjk0YzY3ODcxMjZhZTJiNDkwNWU5ZiIsInN1YiI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsidGltZXNlcmllcy56b25lcy5lOGNiMjYwOC0yZmFmLTQxNjktYjFlOC1jMWY2Y2EwZmQ2NzYudXNlciIsInVhYS5yZXNvdXJjZSIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2LmluZ2VzdCIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2LnF1ZXJ5Iiwib3BlbmlkIiwidWFhLm5vbmUiLCJhbmFseXRpY3Muem9uZXMuOTJkYWFmZjktOTY1NC00ODNlLTllZDUtYWMwYzgxNTEwNTA0LnVzZXIiLCJhbmFseXRpY3Muem9uZXMuNDUyZTA4ZjgtYWI4Yy00NGE2LTkwM2ItOTA0NzgzMjVhZGYyLnVzZXIiLCJwcmVkaXgtYXNzZXQuem9uZXMuOTlkNTIyNzUtZWFjYy00NjBiLWIzMTQtOGNiZmM4MTJiZWM5LnVzZXIiXSwiY2xpZW50X2lkIjoidGVzdF9jbGllbnQiLCJjaWQiOiJ0ZXN0X2NsaWVudCIsImF6cCI6InRlc3RfY2xpZW50IiwiZ3JhbnRfdHlwZSI6ImNsaWVudF9jcmVkZW50aWFscyIsInJldl9zaWciOiJkZWVhZDdlYSIsImlhdCI6MTQ5MjE4MTMxMywiZXhwIjoxNDkyMjI0NTEzLCJpc3MiOiJodHRwczovLzUzYjczOTIxLWFjYWMtNDNkMC1iZWYyLWE4ZWM3OGQyMTZlYy5wcmVkaXgtdWFhLnJ1bi5hd3MtdXN3MDItcHIuaWNlLnByZWRpeC5pby9vYXV0aC90b2tlbiIsInppZCI6IjUzYjczOTIxLWFjYWMtNDNkMC1iZWYyLWE4ZWM3OGQyMTZlYyIsImF1ZCI6WyJhbmFseXRpY3Muem9uZXMuNDUyZTA4ZjgtYWI4Yy00NGE2LTkwM2ItOTA0NzgzMjVhZGYyIiwiYW5hbHl0aWNzLnpvbmVzLjkyZGFhZmY5LTk2NTQtNDgzZS05ZWQ1LWFjMGM4MTUxMDUwNCIsInVhYSIsIm9wZW5pZCIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2IiwicHJlZGl4LWFzc2V0LnpvbmVzLjk5ZDUyMjc1LWVhY2MtNDYwYi1iMzE0LThjYmZjODEyYmVjOSIsInRlc3RfY2xpZW50Il19.bHzB2nHydQnuvK21NT9orhT6g_ViwJ2lV3JgINTY_FqT885HH9jtYJamp0-bEGPuRPYWMl4c9Wl5K3X4VO0H3Xm0QsolIT-uCXty5hkftVCig1Xjn_A5T1mD6jg0zBbdxwqENFFY3ca976ox_HlYPqgVfbMzIghnhNUzapWAUhmnWnpfu8nZhjrywmoB8XZ7ChOTr-XUwrrtHN7Q5Ev28MZHc-am5Hs_TpeaJrfOvCoPi4Pzk1qKaDy3wn40iYAsuP6ye3SBZ0Zxliy8Zw9WzGdn4aVPq3Mk9mfHENSEYPkyxsVs4ZlYTYFt_3Fi7meF_GNSqzxpT-huEcfmgtZASQ")
		  .addHeader("cache-control", "no-cache")
		  .addHeader("postman-token", "51a2f2c0-e597-e6e6-0041-c2ff8c5a80f0")
		  .build();
		Response createresponse = createclient.newCall(createrequest).execute();
		LOG.info(createresponse.toString());
		String createrbody =  createresponse.body().string();
		JSONObject createjObj = new JSONObject(createrbody);
		runtimeID = createjObj.getString("id");
		LOG.info("runtimeID="+runtimeID);
		*/

		//target runtime and artifact
		OkHttpClient artifactclient = new OkHttpClient();
		okhttp3.MediaType artifactmediaType = okhttp3.MediaType.parse("text/plain");
		okhttp3.RequestBody artifactbody = okhttp3.RequestBody.create(artifactmediaType, "{\r\n\t\t'name': efficiency-timeseries-analytic,\r\n\t\t'supportedLanguage': lang,\r\n\t\t'version': 'v1',\r\n\t\t'author': 'VV'\r\n\t}");
		okhttp3.Request artifactrequest = new okhttp3.Request.Builder()
		  .url("https://predix-analytics-config-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations/"+runtimeID+"/artifacts")
		  .get()
		  .addHeader("predix-zone-id", zoneID)
		  .addHeader("content-type", "application/json")
		  .addHeader("authorization", "Bearer "+token)
		  .addHeader("cache-control", "no-cache")
		  .build();
		Response artifactresponse = artifactclient.newCall(artifactrequest).execute();
		String artifactrbody =  artifactresponse.body().string();
		LOG.info("runtime target response="+artifactrbody);
		JSONObject artifactjObj = new JSONObject(artifactrbody);
		String artStr = artifactjObj.getString("orchestrationArtifacts");
		JSONArray artifactjArray = new JSONArray(artStr);
		JSONObject artifactjObj2 = (JSONObject) artifactjArray.get(0);
		LOG.info("artifactjObj2="+artifactjObj2.toString());
		artifactID = artifactjObj2.getString("id");	
		LOG.info("artifactID="+artifactID);
		
		//delete targeted runtime artifact
		OkHttpClient deleteclient = new OkHttpClient();
		okhttp3.MediaType deletemediaType = okhttp3.MediaType.parse("text/plain");
		okhttp3.RequestBody deletebody = okhttp3.RequestBody.create(deletemediaType, "{\r\n\t\t'name': name,\r\n\t\t'supportedLanguage': lang,\r\n\t\t'version': 'v1',\r\n\t\t'author': 'FPT'\r\n\t}");
		okhttp3.Request deleterequest = new okhttp3.Request.Builder()
		  .url("https://predix-analytics-config-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations/artifacts/"+artifactID)
		  .delete(deletebody)
		  .addHeader("predix-zone-id", zoneID)
		  .addHeader("content-type", "application/json")
		  .addHeader("authorization", "Bearer "+token)
		  .addHeader("cache-control", "no-cache")
		  .build();
		Response deleteresponse = deleteclient.newCall(deleterequest).execute();
		String deleteStr = deleteresponse.body().string();
		LOG.info("runtime delete response="+deleteStr);
		
		
		//upload new runtime artifact
		OkHttpClient uploadclient = new OkHttpClient();
		okhttp3.MediaType uploadmediaType = okhttp3.MediaType.parse("multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW");
	    okhttp3.RequestBody uploadbody = okhttp3.RequestBody.create(uploadmediaType, "------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"orchestrationEntryId\"\r\n\r\n"+runtimeID+"\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"type\"\r\n\r\nportToFieldMap\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW\r\nContent-Disposition: form-data; name=\"file\"; filename=\"C:\\Users\\vvenkateswaran\\Documents\\Predix\\test-sulzer\\efficiency-timeseries-analytic\\deploy\\port-to-field.json\"\r\nContent-Type: application/json\r\n\r\n\r\n------WebKitFormBoundary7MA4YWxkTrZu0gW--");
		okhttp3.Request uploadrequest = new okhttp3.Request.Builder()
		  .url("https://predix-analytics-config-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations/artifacts")
		  .post(uploadbody)
		  .addHeader("predix-zone-id", zoneID)
		  .addHeader("content-type", "multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW")
		  .addHeader("authorization", "Bearer "+token)
		  .addHeader("cache-control", "no-cache")
		  .build();

		Response uploadresponse = uploadclient.newCall(uploadrequest).execute();
		String uploadStr = uploadresponse.body().string();
		LOG.info("artifact upload response="+uploadStr);
		
		}
		catch (Exception ex) {
			LOG.error("Error in replaceArtifact --" , ex);
		}	
		return hours;
	}
	
	@RequestMapping(value = "/analytics",method = RequestMethod.POST)
	public Object analystics(@RequestBody TimeSeriesAnalyticsRequestVO requestVO){
		
		return analyticsService.postAnalytics(requestVO);
	}
	
}
