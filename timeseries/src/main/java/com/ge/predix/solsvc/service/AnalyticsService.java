/**
 * 
 */
package com.ge.predix.solsvc.service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
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
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Aggregation;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AnalyticAttributes;
import com.ge.predix.solsvc.model.AnalyticsRequest;
import com.ge.predix.solsvc.model.AnalyticsRuntimeRequestVO;
import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.AssetDataField;
import com.ge.predix.solsvc.model.JsonData;
import com.ge.predix.solsvc.model.NotificationDataResponseVO;
import com.ge.predix.solsvc.model.NotificationDataVO;
import com.ge.predix.solsvc.model.NotificationResponse;
import com.ge.predix.solsvc.model.NotificationResponseModel;
import com.ge.predix.solsvc.model.NotificationVO;
import com.ge.predix.solsvc.model.TimeSeriesAnalyticsRequestVO;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.util.JsonUtil;
import com.ge.predix.solsvc.util.TimeSeriesConstants;

/**
 * @author ramalapoli
 *
 */
@Service
public class AnalyticsService {

	@Autowired
	private TimeseriesClient client;
	
	private static final Logger LOG = LoggerFactory.getLogger(AnalyticsService.class);
	
	@Autowired
	private AssetService assetService;
	@Autowired
	private JsonMapper jsonMapper;
	@Autowired
	private TimeSeriesService timeSeriesService;
	@Value(value = "${notification.demo.priority}")
	private String notifyPriority;
	@Value(value = "${notification.demo.fault}")
	private String notifyFault;
	@Value(value = "${notification.demo.info}")
	private String notifyInfo;
	@Value(value = "${notification.demo.email}")
	private String notifyEmail;
	@Value(value = "${notification.demo.text}")
	private String notifyText;

	@Value(value = "${predix.notification.execution.url}")
	private String notificationDHUrl;

	@Value(value = "${predix.analytics.execution.url}")
	private String predixAnalyticsExecutionServiceUrl;
	@Value(value = "${predix.analytics.runtime.zoneid}")
	private String runtimeZoneId;
	@Value(value = "${analytics.orchestration.configurationId}")
	private String orchestrationId;
	@Value(value = "${analytics.orchestration.TimeseriesInput}")
	private String Input_for_processing;
	@Value(value = "${analytics.orchestration.TimeseriesOutput}")
	private String Output_for_processing;

	@Value("#{${analytics.cavitation.machine}}")
	private Map<String, String> cavitationMachineMap;

	@Value("#{${analytics.cavitation.stored}}")
	private Map<String, String> cavitationStoredMap;

	@Value("#{${analytics.efficiency.machine}}")
	private Map<String, String> efficiencyMachineMap;

	@Value("#{${analytics.efficiency.stored}}")
	private Map<String, String> efficiencyStoredMap;

	RestTemplate restTemplate = new RestTemplate();

	/**
	 * Get Analytics Average by Agregating TimeSeries Data
	 * Analytics.head.suction.{pumpid} The analytics data is inserted externally
	 * by following command
	 * 
	 * @param pumpID
	 * @param intervalTime
	 * @param startTime
	 * @return avg
	 */
	public String getAverageOfAnalyticsForPump(String pumpID, String intervalTime, String startTime) {

		try {

			LOG.info(": client.getTimeseriesHeaders()=" + client.getTimeseriesHeaders());
			LOG.info("Tags ---" + client.listTags(client.getTimeseriesHeaders()));

			List<Tag> listOfTags = new ArrayList<Tag>();
			Tag a = new Tag();
			a.setName(TimeSeriesConstants.TAG_NAME_SUCTION + pumpID);
			List<Aggregation> aggregations = new ArrayList<Aggregation>();
			Aggregation aggregation = new Aggregation();
			aggregation.setType("avg");
			aggregation.setInterval(intervalTime);
			aggregations.add(aggregation);
			a.setAggregations(aggregations);
			listOfTags.add(a);

			DatapointsQuery dpq = new DatapointsQuery();
			dpq.setStart(startTime);
			LOG.info("listOfTags=" + listOfTags);
			dpq.setTags(listOfTags);

			DatapointsResponse dpr = client.queryForDatapoints(dpq, client.getTimeseriesHeaders());
			LOG.info("dpr = " + dpr.toString());
			com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag tagValue = dpr.getTags().get(0);
			// List<Object> dprValuesList =
			// dpr.getTags().get(0).getResults().get(0).getValues();

			String avgResult = "0";
			if (tagValue != null && tagValue.getResults() != null && tagValue.getResults().size() > 0
					&& tagValue.getResults().get(0).getValues() != null
					&& tagValue.getResults().get(0).getValues().size() > 0) {

				// System.out.println("Zeroth Element" +
				// tagValue.getResults().get(0).getValues().get(0));
				List objs = (List) tagValue.getResults().get(0).getValues().get(0);
				System.out.println("Avg" + objs.get(1));
				avgResult = objs.get(1).toString();
			}

			JSONObject jsonObject = new JSONObject();
			jsonObject.put("avg", avgResult);
			return jsonObject.toString();

		} catch (Exception ex) {
			LOG.error("Error in getAverageOfAnalyticsForPump --", ex);
		}
		return null;
	}

	/**
	 * This method takes care of pulling data from timeseries and calling
	 * Analytics service and finally sending back notification to UI
	 * 
	 * @param assetName
	 * @param startTime
	 * @param endTime
	 * @return ArrayList<NotificationResponseModel>
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	public ArrayList<NotificationResponseModel> getNotificationForAsset(String assetName, String startTime,
			String endTime) throws JsonProcessingException, JSONException {

		LOG.info("getNotification started");

		Map<String, String> analyticsResponse = new HashMap<>();
		NotificationResponseModel notifyModel = new NotificationResponseModel();
		ArrayList<NotificationResponseModel> notifyList = new ArrayList<NotificationResponseModel>();
		LOG.info(": client.getTimeseriesHeaders()=" + client.getTimeseriesHeaders());
		try {
			DatapointsLatestQuery dlq = new DatapointsLatestQuery();

			List<Tag> listOfTags = new ArrayList<Tag>();

			Tag a = new Tag();

			a.setName(TimeSeriesConstants.TAG_NAME_NOTIFICATION + assetName);
			// a.add( TimeSeriesConstants.TAG_NAME_NOTIFICATION+ assetName);
			listOfTags.add(a);
			// dlq.setTags(listOfTags);
			DatapointsQuery datapoints = new DatapointsQuery();
			datapoints.setTags(listOfTags);
			datapoints.setStart(startTime);
			datapoints.setEnd(endTime);

			DatapointsResponse dpr = client.queryForDatapoints(datapoints, client.getTimeseriesHeaders());
			LOG.info("dpr = " + dpr.toString());
			List<com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag> tagListResp = dpr.getTags();
			com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag tagResp = null;
			com.ge.predix.entity.timeseries.datapoints.queryresponse.Results result = null;
			List<com.ge.predix.entity.timeseries.datapoints.queryresponse.Results> resultList = null;

			if (tagListResp != null) {
				tagResp = tagListResp.get(0);
				resultList = tagResp.getResults();
				LOG.info("resultList = " + resultList);
				if (resultList != null) {
					int i = 0;
					result = resultList.get(0);
					LOG.info("result = " + result);
					LOG.info("Object Count" + i);
					if (result.getValues() != null) {

						for (Object objList : result.getValues()) {
							LOG.info("Object Count" + i);
							i++;
							List<Object> obj = (List<Object>) objList;
							Date date = new Date((Long) obj.get(0));
							SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TimeSeriesConstants.DATE_FOMAT_GETDATA);
							String finalDate = DATE_FORMAT.format(date);
							LOG.info("Today in dd MMM format : " + date);
							notifyModel.setDate(finalDate);
							notifyModel.setId(Integer.toString((int) obj.get(1)));
							notifyModel.setName(assetName);
							prepareNotificationModel(notifyModel);
							LOG.info("Today in dd MMM format : " + notifyModel.toString());
							notifyList.add(notifyModel);
							notifyModel = new NotificationResponseModel();
							analyticsResponse.put(assetName, assetName);
						}
					}
				}

				LOG.info("tagResp = " + tagResp.toString());
			}

			LOG.info("ANalytics response=" + analyticsResponse);

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return notifyList;
	}

	private void prepareNotificationModel(NotificationResponseModel notifyModel) {

		notifyModel.setFault(notifyFault);
		notifyModel.setPriority(notifyPriority);
		notifyModel.setInfo(notifyInfo);
		notifyModel.setEmail(notifyEmail);
		notifyModel.setText(notifyText);

	}

	/**
	 * PIOT-231 PIOT-120 - O&G : As an admin, i should be able to filter the
	 * notifications, so that i can quickly find and identify certain
	 * notification
	 * 
	 * <<<<<<< HEAD
	 * 
	 * @param criteria
	 *            : In case to return 'AllNotifications' then, criteria=null :
	 *            In case of any specific status then,
	 *            criteria='failure,main,preditive,nodata'
	 * @param startDate
	 *            : startDate for the query
	 * @param endDate
	 *            : endDate for the query
	 * @return List<NotificationResponse> NotificationResponse model has
	 *         attributes as below: 1. machineStatus 2. statusIcon 3. date 4.
	 *         message 5. assetId =======
	 * @param criteria
	 *            : In case to return 'AllNotifications' then, criteria=null :
	 *            In case of any specific status then,
	 *            criteria='failure,main,preditive,nodata'
	 * @param startDate
	 *            : startDate for the query
	 * @param endDate
	 *            : endDate for the query
	 * @return List<NotificationResponse> NotificationRespo nse model has
	 *         attributes as below: 1. machineStatus 2. statusIcon 3. date 4.
	 *         message 5. assetId >>>>>>>
	 *         1938aa8fb9c2a2ea95669fa42105b5d58b7c2d5c
	 * 
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	public NotificationResponse getMachineStatusNotifications(final String criteria, final String startDate,
			final String endDate) {
		LOG.info("getMachineStatusNotifications started");
		NotificationResponse notifyResponse = null;
		String criteriaFilterStr = null;
		List<JsonData> JsonDataList = new ArrayList<JsonData>();

		NotificationResponse resp = new NotificationResponse();

		NotificationDataResponseVO data = new NotificationDataResponseVO();

		List<NotificationDataVO> notificationDataList = new LinkedList<NotificationDataVO>();
		NotificationDataVO notificationData = null;
		List<NotificationVO> notificationList = null;
		NotificationVO item = null;
		try {
			if (!StringUtils.isEmpty(criteria)) {
				criteriaFilterStr = criteria.replaceAll("'", "");
				String statusFilters[] = criteriaFilterStr.split(",");

				// TODO: Need to use the filter values in the actual query to
				// get the response

			} else {
			} // TODO: if status if empty then make query based on only
				// startDate and endDate
			List<String> tags = new ArrayList<String>();
			tags.add("Notification.pump9");
			DatapointsResponse dpr = timeSeriesService.queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR,
					20, tags);

			// String jsonResponseToClient
			// =JsonUtil.getJsonDataForGetTimeSeriesData(response, jsonMapper);

			// System.out.print("Response:" +
			// response.getTags().get(0).getResults());
			// System.out.print("Json:" + jsonResponseToClient);
			for (int q = 0; q < dpr.getTags().size(); q++) {
				System.out.println("Count: " + q);
				List<Integer> dataList = new ArrayList<Integer>();
				com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag tagValue = dpr.getTags().get(q);
				List<Object> dprValuesList = dpr.getTags().get(q).getResults().get(0).getValues();
				LOG.info("dprValuesList=" + dprValuesList);
				System.out.println("dprValuesList=" + dprValuesList);
				System.out.println("Size: " +dprValuesList.size());
				for (Object objList : dprValuesList) {
					
					List<Object> obj = (List<Object>) objList;
					Date date = new Date((Long) obj.get(0));
				//	System.out.println("Date: "+ date);
					
					notificationData = new NotificationDataVO();
					notificationList = new LinkedList<NotificationVO>();
					item = new NotificationVO();
					item.setAssetId("SUZ-CUS-834 ");
					
					double value;
					try {
					value = (double) obj.get(1);
					}catch(Exception e){
						value = (int) obj.get(1);
					}
				//	System.out.println(value);
					
					if (isBetween(value, 1, 82)) {
						 item.setStatusIcon("blue");
						 item.setMachineStatus("PERFORMING_OPTIMALLY");
						 item.setMessage("Pump SUZ-CUS-834 is Performing Optimally");
						 System.out.println("OPT 2");
						}
						else if (isBetween(value, 83, 500)) {
							 item.setStatusIcon("yellow");
							 item.setMachineStatus("PREDICTING_FAILURE");
							 item.setMessage("Predicting Failure in pump SUZ-CUS-834");
							 System.out.println("Predict");
						}
						else{
							 item.setStatusIcon("red");
							 item.setMachineStatus("FAILURE");
							 item.setMessage("Failure in pump SUZ-CUS-834");
							 System.out.println("Failure");
						}
					item.setMessageTime(date.toString());
					notificationList.add(item);
					notificationData.setDateStamp(date.toString());
					notificationData.setNotificationList(notificationList);
					notificationDataList.add(notificationData);
				}
			}


			data.setNotificationData(notificationDataList);
			resp.setData(data);
			notifyResponse = resp;
		} catch (Exception e) {
			e.printStackTrace();
		}

		LOG.info("getMachineStatusNotifications end");
		return notifyResponse;
	}

	/**
	 * This method implements PIOT-206 Earth which is the part of PIOT-131
	 */

	public String getAnalytics(String assetName, String pumpId, String timeDuration) {

		// TODO: Add the code for getting data from analytics service based on
		// the switch value and remove the hard-coding
		AssetData[] assetData = (AssetData[]) assetService.getAssetDataListByAssetId(assetName, pumpId);
		if (timeDuration.equals("8h-ago")) {
			LOG.info("Inside 8h-ago");

			AnalyticAttributes analyticsAttrb = new AnalyticAttributes();
			analyticsAttrb.setCavitation((double) -50);
			analyticsAttrb.setMachineStatus("FAILURE");
			analyticsAttrb
					.setAveragePowerConsumption(assetData[0].getAnalyticAttributes().getAveragePowerConsumption());
			analyticsAttrb.setMachineEfficiencyLevel(assetData[0].getAnalyticAttributes().getMachineEfficiencyLevel());
			assetData[0].setAnalyticAttributes(analyticsAttrb);

			updateAsset(assetData[0]);

		} else if (timeDuration.equals("4h-ago")) {
			LOG.info("Inside 4h-ago");
			AnalyticAttributes analyticsAttrb = new AnalyticAttributes();
			analyticsAttrb.setCavitation((double) 50);
			analyticsAttrb.setMachineStatus("PREDICTING_FAILURE");
			analyticsAttrb
					.setAveragePowerConsumption(assetData[0].getAnalyticAttributes().getAveragePowerConsumption());
			analyticsAttrb.setMachineEfficiencyLevel(assetData[0].getAnalyticAttributes().getMachineEfficiencyLevel());
			assetData[0].setAnalyticAttributes(analyticsAttrb);

			updateAsset(assetData[0]);
		} else {
			LOG.info("Inside  else 2h-ago");
			AnalyticAttributes analyticsAttrb = new AnalyticAttributes();
			analyticsAttrb.setCavitation((double) 250);
			analyticsAttrb.setMachineStatus("PERFORMING_OPTIMALLY");
			analyticsAttrb
					.setAveragePowerConsumption(assetData[0].getAnalyticAttributes().getAveragePowerConsumption());
			analyticsAttrb.setMachineEfficiencyLevel(assetData[0].getAnalyticAttributes().getMachineEfficiencyLevel());
			assetData[0].setAnalyticAttributes(analyticsAttrb);

			updateAsset(assetData[0]);
		}

		if (!(pumpId == null) && !(timeDuration == null)) {
			LOG.info("Inside Service class IF ");
			return "Sucess";
		}

		else {
			LOG.info("Inside Service class Esle ");
			return "Failure";
		}
	}

	private void updateAsset(AssetData asset) {

		String assetUrlstr = "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/ingestAssetData/machine";
		HttpClient httpClient = HttpClientBuilder.create().build();
		ObjectMapper mapper = new ObjectMapper();
		try {

			HttpPost request = new HttpPost(assetUrlstr);

			String jsonInString = mapper.writeValueAsString(asset);
			StringEntity params = new StringEntity("[" + jsonInString + "]");
			System.out.println(jsonInString);
			request.addHeader("Content-Type", "application/json; charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			System.out.println("response" + response.toString());
		} catch (Exception e) {

		}
	}

	/**
	 * This method implements PIOT-131
	 */

	public String getTrainAnalytics(String pumpId, String timeDuration) {

		// stub

		return "Success";
	}

	public String executeEfficiency() {
		LOG.info("Start executeEfficiency Analytics");
		try {

			String url = predixAnalyticsExecutionServiceUrl;
			Header tokenHeader = client.getTimeseriesHeaders().get(0);
			String token = tokenHeader.getValue();

			LOG.info("analytics token" + token);

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			HttpHeaders validateHeaders = new HttpHeaders();
			List<MediaType> list = new ArrayList<MediaType>();
			list.add(MediaType.APPLICATION_JSON);
			validateHeaders.setAccept(list);
			validateHeaders.setContentType(MediaType.APPLICATION_JSON);
			validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
			validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, runtimeZoneId);

			AnalyticsRequest request = new AnalyticsRequest();
			request.setOrchestrationConfigurationId(orchestrationId);
			AssetDataField dataField = new AssetDataField();
			dataField.setInputTimeseriesTag(Input_for_processing);
			dataField.setOutputTimeseriesTag(Output_for_processing);
			request.setAssetDataFieldsMap(dataField);

			HttpEntity entity = new HttpEntity(request, validateHeaders);
			LOG.info("headers executeEfficiency: " + entity.toString());
			ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, entity, String.class);
			LOG.info(responseEntity.getBody());

		} catch (Exception ex) {
			LOG.error("Error in executeEfficiency --", ex);
		}

		return "SUCCESS";
	}

	public String executeEfficiencyAnalytics(String rangeValue) {
		LOG.info("Start executeEfficiencyAnalytics ");
		InputStream is = null;
		try {
			Header tokenHeader = client.getTimeseriesHeaders().get(0);
			String token = tokenHeader.getValue();

			String url = "https://predix-analytics-execution-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations/artifacts";

			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			is = RestController.class.getResourceAsStream("/" + "port-to-field-map-for-efficiency.json");

			MultiValueMap<String, Object> valueMap = new LinkedMultiValueMap<String, Object>();
			// valueMap.add("Content-Type", "image/jpeg");
			valueMap.add("file", is);
			valueMap.add("orchestrationEntryId", "af715ffa-2244-4e70-b2f5-3c866a639e93");
			valueMap.add("type", "portToFieldMap");
			valueMap.add("name", "efficiency-calculator");
			valueMap.add("description", "Asset with efficiency analytics");
			valueMap.add("stepId", "EfficiencyCalculatorOrchestration");

			LOG.info("ExecuteEfficiencyAnalytics ...file loaded, creating header");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
			headers.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, runtimeZoneId);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(valueMap, headers);
			// String response = restTemplate.postForObject(uploadFilesUrl,
			// requestEntity, String.class);

			// HttpEntity entity = new HttpEntity(validateHeaders);
			LOG.info("headers executeEfficiencyAnalytics: " + requestEntity.toString());
			ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, requestEntity, String.class);
			LOG.info(responseEntity.getBody());

		} catch (Exception ex) {
			LOG.error("Error in executeEfficiencyAnalytics --", ex);
		}

		return "SUCCESS";
	}

	public String removeOldPortToFieldMap(String rangeValue) {
		LOG.info("Start removeOldPortToFieldMap ");
		InputStream is = null;
		try {
			Header tokenHeader = client.getTimeseriesHeaders().get(0);
			String token = tokenHeader.getValue();

			String url = "https://predix-analytics-execution-release.run.aws-usw02-pr.ice.predix.io/api/v2/config/orchestrations/artifacts/1a82f46b-5481-47f9-8744-3f3bc04491a2";
			RestTemplate rt = new RestTemplate();
			rt.getMessageConverters().add(new StringHttpMessageConverter());

			LOG.info("removeOldPortToFieldMap ...file loaded, creating header");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.MULTIPART_FORM_DATA);
			headers.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
			headers.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, runtimeZoneId);

			HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(headers);
			// String response = restTemplate.postForObject(uploadFilesUrl,
			// requestEntity, String.class);

			// HttpEntity entity = new HttpEntity(validateHeaders);
			LOG.info("headers removeOldPortToFieldMap: " + requestEntity.toString());
			ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, requestEntity, String.class);
			LOG.info(responseEntity.getBody());

		} catch (Exception ex) {
			LOG.error("Error in removeOldPortToFieldMap --", ex);
		}

		return "SUCCESS";
	}

	/**
	 * 
	 * Get PredictedCavitation Failure for Asset ID
	 * 
	 * Currently getting latest data point from Timeseries as predictive
	 * analysis is not ready
	 * 
	 * @param assetName
	 * @param assetid
	 * @return
	 */
	public Map<String, String> getPredictedCavitationFailure(String assetName, String assetId) {

		Map<String, String> cavitationFailureAttributes = new LinkedHashMap<String, String>();

		try {

			Map<String, String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,
					assetId);
			LOG.info("assetIDData: " + assetIDData);
			DatapointsResponse response = timeSeriesService
					.querylatestDatapointsForTimeseriesTag(assetIDData.get(TimeSeriesConstants.ASSET_TAG_NAME_INGEST));

			if (response != null) {

				List<Object> dprValuesList = response.getTags().get(0).getResults().get(0).getValues();

				if (dprValuesList != null && !dprValuesList.isEmpty()) {

					List<Object> obj = (List<Object>) dprValuesList.get(0);
					int object = (int) obj.get(1);
					cavitationFailureAttributes.put("CavitationFailureNumber", Integer.toString(object));
				}

			}

		} catch (Exception ex) {
			LOG.error("Error in getPredictedCavitationFailure --", ex);
		}

		return cavitationFailureAttributes;
	}

	@SuppressWarnings("unchecked")
	public Object postAnalytics(TimeSeriesAnalyticsRequestVO requestVO) {

		String duration = requestVO.getDuration().replaceAll("-", "");
		String cavConfigId = null, effConfigId = null;
		String assetId = null;
		Map<String, String> cavitationDataMap = new HashMap<>();
		Map<String, String> efficiencyDataMap = new HashMap<>();
		if (requestVO.getToggle().equalsIgnoreCase(TimeSeriesConstants.TOGGLE_MACHINE)) {
			assetId = TimeSeriesConstants.TOGGLE_MACHINE_VALUE;
			cavConfigId = cavitationMachineMap.get(TimeSeriesConstants.TOGGLE_MACHINE + duration);
			effConfigId = efficiencyMachineMap.get(TimeSeriesConstants.TOGGLE_MACHINE + duration);
			cavitationDataMap.put(TimeSeriesConstants.HEAD_SUCTION_TAG, TimeSeriesConstants.CAVITATION_PUMP9);
			efficiencyDataMap.put(TimeSeriesConstants.ANOMALY_TAG, TimeSeriesConstants.NOTIFICATION_PUMP9);
		}

		if (requestVO.getToggle().equalsIgnoreCase(TimeSeriesConstants.TOGGLE_STORED)) {
			assetId = TimeSeriesConstants.TOGGLE_STORED_VALUE;
			cavConfigId = cavitationStoredMap.get(TimeSeriesConstants.TOGGLE_STORED + duration);
			effConfigId = efficiencyStoredMap.get(TimeSeriesConstants.TOGGLE_STORED + duration);
			cavitationDataMap.put(TimeSeriesConstants.HEAD_SUCTION_TAG, TimeSeriesConstants.CAVITATION_PUMP11);
			efficiencyDataMap.put(TimeSeriesConstants.ANOMALY_TAG, TimeSeriesConstants.NOTIFICATION_PUMP11);
		}

		Map<String, String> assetData = (Map<String, String>) assetService
				.getAssetDataByAssetId(TimeSeriesConstants.TOGGLE_MACHINE, assetId);

		cavitationDataMap.put(TimeSeriesConstants.INTAKE_TAG, assetData.get(TimeSeriesConstants.ASSET_TAG_NAME_INGEST));
		cavitationDataMap.put(TimeSeriesConstants.DIGEST_TAG,
				assetData.get(TimeSeriesConstants.ASSET_TAG_NAME_DISCHARGE));

		efficiencyDataMap.put(TimeSeriesConstants.INTAKE_TAG, assetData.get(TimeSeriesConstants.ASSET_TAG_NAME_ENERGYCONSUMPTION));

		AnalyticsRuntimeRequestVO cavitation = new AnalyticsRuntimeRequestVO();
		cavitation.setOrchestrationConfigurationId(cavConfigId);
		cavitation.setAssetDataFieldsMap(cavitationDataMap);

		AnalyticsRuntimeRequestVO efficiency = new AnalyticsRuntimeRequestVO();
		efficiency.setOrchestrationConfigurationId(effConfigId);
		efficiency.setAssetDataFieldsMap(efficiencyDataMap);

		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		String token = tokenHeader.getValue();
		HttpHeaders validateHeaders = new HttpHeaders();
		validateHeaders.setContentType(MediaType.APPLICATION_JSON);
		validateHeaders.add(TimeSeriesConstants.HEADER_AUTHORIZATION, token);
		validateHeaders.add(TimeSeriesConstants.HEADER_PREDIX_ZONEID, runtimeZoneId);


		HttpEntity<Object> cavitationEntity = new HttpEntity<>(cavitation, validateHeaders);
		ResponseEntity<Object> cavitationResp = restTemplate.exchange(predixAnalyticsExecutionServiceUrl,
				HttpMethod.POST, cavitationEntity, Object.class);

		HttpEntity<Object> efficiencyEntity = new HttpEntity<>(efficiency, validateHeaders);
		ResponseEntity<Object> efficiencyResp = restTemplate.exchange(predixAnalyticsExecutionServiceUrl,
				HttpMethod.POST, efficiencyEntity, Object.class);

		LOG.info("Response from analytics execution service for cavitation {}", cavitationResp.getBody());
		LOG.info("Response from analytics execution service for efficiency{}", efficiencyResp.getBody());
		
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

		Object updatedAssertData = assetService.getAssetDataByAssetId(TimeSeriesConstants.TOGGLE_MACHINE, assetId);

		// Calling Notification Service
		LOG.info("Calling Notification Service");
		HttpHeaders notificationHeaders = new HttpHeaders();
		notificationHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> notificationEntity = new HttpEntity<>(updatedAssertData, validateHeaders);
		ResponseEntity<String> notificationResp = restTemplate.exchange(notificationDHUrl, HttpMethod.POST,
				notificationEntity, String.class);
		LOG.info("Notification Service Response:" + notificationResp.getBody());

		return updatedAssertData;
	}
	
	public static boolean isBetween(double x, int i, int j) {
		  return i <= x && x <= j;
		}
}
