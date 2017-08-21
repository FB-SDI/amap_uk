/**
 * 
 */
package com.ge.predix.solsvc.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.http.Header;
import org.codehaus.jettison.json.JSONObject;
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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.tags.TagsList;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AnalyticAttributes;
import com.ge.predix.solsvc.model.AnalyticsRequest;
//import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.AssetDataField;
import com.ge.predix.solsvc.model.AssetsRequest;
import com.ge.predix.solsvc.model.DataLearn;
import com.ge.predix.solsvc.model.DataPredict;
import com.ge.predix.solsvc.model.DataPoints;
import com.ge.predix.solsvc.model.DataResponseVO;
import com.ge.predix.solsvc.model.MLInputData;
import com.ge.predix.solsvc.model.MLOutputData;
import com.ge.predix.solsvc.model.MLResultData;
import com.ge.predix.solsvc.model.PredictedData;
import com.ge.predix.solsvc.model.TestF1Score;
import com.ge.predix.solsvc.model.ThresholdF1Score;
import com.ge.predix.solsvc.model.TrainF1Score;
import com.ge.predix.solsvc.model.TSData;
import com.ge.predix.solsvc.util.JsonMLUtil;
import com.ge.predix.solsvc.util.MLConstants;

import scala.util.parsing.json.JSON;

import com.ge.predix.solsvc.model.StatusResponseVO;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

/**
 * @author vvenkateswaran
 *
 */
@Service
public class MLService {
	

	private static final Logger LOG = LoggerFactory.getLogger(MLService.class);
	@Autowired
	private TimeseriesClient client;

	@Value(value = "${predix.asset.service.url}")
	private String predixAssetServiceUrl;

	@Value(value = "${predix.asset.service.zoneid}")
	private String assetZoneId;
	
	@Autowired
	private JsonMapper jsonMapper;
	
	//@Value(value = "${predix.asset.service.token}")
	private String token;

	
	/**
	 * Package ML training and prediction datasets
	 * 
	 * @param assetName
	 * @return MLInputData
	 */
	public String packageMLData(String assetName) {

		String predicturl = predixAssetServiceUrl + "/machine";
		String trainurl = predixAssetServiceUrl + "/" + assetName;
		//This is the Asset Service bearer token using Salazar UAA
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		token = tokenHeader.getValue();
		LOG.info("client token="+token);

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());


		HttpHeaders assetHeaders = new HttpHeaders();
		assetHeaders.setContentType(MediaType.APPLICATION_JSON);
		assetHeaders.add(MLConstants.HEADER_AUTHORIZATION, token);
		assetHeaders.add(MLConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity assetEntity = new HttpEntity(assetHeaders);
		LOG.info("headers" + assetEntity.toString());

		ResponseEntity<List> trainResponseEntity = rt.exchange(trainurl, HttpMethod.GET, assetEntity, List.class);
		ResponseEntity<List> predictResponseEntity = rt.exchange(predicturl, HttpMethod.GET, assetEntity, List.class);
		LOG.info("train="+trainResponseEntity.toString());
		LOG.info("predict="+predictResponseEntity.toString());
			
		//Set up lists for various attributes for learn and predict
		DataLearn learn = new DataLearn();	
		DataPredict predict = new DataPredict();
		MLInputData input = new MLInputData();
		List<Integer> effL = new ArrayList();
		List<Integer> cavL = new ArrayList();
		List<Integer> vibL = new ArrayList();
		List<Integer> grL = new ArrayList();
		List<Integer> lldL = new ArrayList();
		List<String> statusL = new ArrayList(); 
		
		//Prediction data objects don't contain statuses
		List<Integer> effP = new ArrayList();
		List<Integer> cavP = new ArrayList();
		List<Integer> vibP = new ArrayList();
		List<Integer> grP = new ArrayList();
		List<Integer> lldP = new ArrayList();
		
		//Map<String, String> assetIDData = (LinkedHashMap) responseEntity.getBody();
		//String assetIDData = responseEntity.getBody().get(0).toString();
		
		
		//Loop through training asset data and add all values to lists for individual attributes
		for (int i=0; i < trainResponseEntity.getBody().size(); i++)
		{
			Map<String, Object> attributes = (Map<String, Object>) trainResponseEntity.getBody().get(i);
			LOG.info(attributes.toString());
			effL.add(Integer.parseInt(attributes.get("efficiency").toString()));
			cavL.add(Integer.parseInt(attributes.get("cavitation").toString()));
			vibL.add(Integer.parseInt(attributes.get("vibration").toString()));
			grL.add(0);
			lldL.add(0);
			statusL.add(attributes.get("label").toString());
		}
		//LOG.info("eff="+effL.toString());
		//LOG.info("cav="+cavL.toString());
		//LOG.info("vib="+vibL.toString());
		//LOG.info("status="+statusL.toString());
		
		//Set values for training data
		learn.setEFF(effL);
		learn.setCAV(cavL);
		learn.setVIB(vibL);
		learn.setGR(grL);
		learn.setLLD(lldL);
		learn.setSTATUS(statusL);
		
		//Loop though machine asset data and assign required attribute values to lists
		for (int i=0; i < predictResponseEntity.getBody().size(); i++)
		{
			Map<String, Object> attributes = (Map<String, Object>) predictResponseEntity.getBody().get(i);
			
			String attrsStr = attributes.get("analyticAttributes").toString();
			Map<String,String> attrMap = splitToMap(attrsStr, ", ", "=");
			
			effP.add(Integer.parseInt(attrMap.get("machineEfficiencyLevel")));
			cavP.add(Integer.parseInt(attrMap.get("cavitation")));
			vibP.add(0);
			grP.add(0);
			lldP.add(0);
		}
		
	    predict.setEFF(effP);
		predict.setCAV(cavP);
		predict.setVIB(vibP);
		predict.setGR(grP);
		predict.setLLD(lldP);
		
		
		input.setDataLearn(learn);
		input.setDataPredict(predict);
		input.setLabel("STATUS");
		input.setScoreThresholdTrain(0.15);
		input.setScoreThresholdTest(0.15);
		input.setHyperParameterTune("false");
		
		// AssetData assetData =
		// JsonUtil.getDummyAssetDataForAssetManagementPage().get(0);		
		
		try {
			LOG.info("Start executeML Analytics");
			String catalogId = "92daaff9-9654-483e-9ed5-ac0c81510504";
			String runtimeId = "452e08f8-ab8c-44a6-903b-90478325adf2";
			String zoneId = "https://53b73921-acac-43d0-bef2-a8ec78d216ec.predix-uaa.run.aws-usw02-pr.ice.predix.io";
			String analyticId = "2b7cc591-337e-47ee-9469-7f8f04682711";
			String executeurl = "https://predix-analytics-catalog-release.run.aws-usw02-pr.ice.predix.io/api/v1/catalog/analytics/"+analyticId+"/execution";
			

			//This is the Analytics bearer token using GSAHU UAA
			//String token2 = "Bearer eyJhbGciOiJSUzI1NiIsImtpZCI6ImxlZ2FjeS10b2tlbi1rZXkiLCJ0eXAiOiJKV1QifQ.eyJqdGkiOiIzMzU2NWE2MGY5N2M0MDAxODFjMDA4MGQ0M2I3MzZhNiIsInN1YiI6InRlc3RfY2xpZW50Iiwic2NvcGUiOlsidGltZXNlcmllcy56b25lcy5lOGNiMjYwOC0yZmFmLTQxNjktYjFlOC1jMWY2Y2EwZmQ2NzYudXNlciIsInVhYS5yZXNvdXJjZSIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2LmluZ2VzdCIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2LnF1ZXJ5Iiwib3BlbmlkIiwidWFhLm5vbmUiLCJhbmFseXRpY3Muem9uZXMuOTJkYWFmZjktOTY1NC00ODNlLTllZDUtYWMwYzgxNTEwNTA0LnVzZXIiLCJhbmFseXRpY3Muem9uZXMuNDUyZTA4ZjgtYWI4Yy00NGE2LTkwM2ItOTA0NzgzMjVhZGYyLnVzZXIiLCJwcmVkaXgtYXNzZXQuem9uZXMuOTlkNTIyNzUtZWFjYy00NjBiLWIzMTQtOGNiZmM4MTJiZWM5LnVzZXIiXSwiY2xpZW50X2lkIjoidGVzdF9jbGllbnQiLCJjaWQiOiJ0ZXN0X2NsaWVudCIsImF6cCI6InRlc3RfY2xpZW50IiwiZ3JhbnRfdHlwZSI6ImNsaWVudF9jcmVkZW50aWFscyIsInJldl9zaWciOiJkZWVhZDdlYSIsImlhdCI6MTQ5NjIxNzU3NywiZXhwIjoxNDk2MjYwNzc3LCJpc3MiOiJodHRwczovLzUzYjczOTIxLWFjYWMtNDNkMC1iZWYyLWE4ZWM3OGQyMTZlYy5wcmVkaXgtdWFhLnJ1bi5hd3MtdXN3MDItcHIuaWNlLnByZWRpeC5pby9vYXV0aC90b2tlbiIsInppZCI6IjUzYjczOTIxLWFjYWMtNDNkMC1iZWYyLWE4ZWM3OGQyMTZlYyIsImF1ZCI6WyJhbmFseXRpY3Muem9uZXMuNDUyZTA4ZjgtYWI4Yy00NGE2LTkwM2ItOTA0NzgzMjVhZGYyIiwiYW5hbHl0aWNzLnpvbmVzLjkyZGFhZmY5LTk2NTQtNDgzZS05ZWQ1LWFjMGM4MTUxMDUwNCIsInVhYSIsIm9wZW5pZCIsInRpbWVzZXJpZXMuem9uZXMuZThjYjI2MDgtMmZhZi00MTY5LWIxZTgtYzFmNmNhMGZkNjc2IiwicHJlZGl4LWFzc2V0LnpvbmVzLjk5ZDUyMjc1LWVhY2MtNDYwYi1iMzE0LThjYmZjODEyYmVjOSIsInRlc3RfY2xpZW50Il19.bN8fgVc9uITDcdn_poZSRTtx-1_IcldU0jOnibQak8p9Uo9vFpUx6rHGKsjIAfO1Y1uffe5qEe6HKp58ydxtzDVVk8XWTRa5oWnchVLlQhTbZjckZzj97ONy5MnJm0TwwO7MyofUgmL5AH1FzPcz-x7LveL1XH9rsyKnhjcQ_mKiStr8kqAO2XqMs8hf8uwO-MQR7CbnbB54gmBIg7N-Hs0qkLApGDDl2PA7dSz8dtZorKPNwr96SLuYSUZOhR7Aym0JV02lJtUoNED4xZeIfiRVaHg7u7aZBm4bgB95TePL5sVJCj9PnsQT92CAk9Me6AKoWOTf0LPoC2JXmDKlrw";
			
			RestTemplate rt2 = new RestTemplate();
			rt2.getMessageConverters().add(new StringHttpMessageConverter());


			HttpHeaders executeHeaders = new HttpHeaders();
			executeHeaders.setContentType(MediaType.APPLICATION_JSON);
			executeHeaders.add(MLConstants.HEADER_AUTHORIZATION, token);
			executeHeaders.add(MLConstants.HEADER_PREDIX_ZONEID, catalogId);
			
			HttpEntity<String> executeEntity = new HttpEntity(input, executeHeaders);
			LOG.info("entity="+executeEntity.toString());
			ResponseEntity<MLOutputData> executeResponseEntity = rt.exchange(executeurl, HttpMethod.POST, executeEntity, MLOutputData.class);
			LOG.info("execute="+executeResponseEntity.toString());	
			
			MLOutputData outputML = executeResponseEntity.getBody();
			ObjectMapper mapper = new ObjectMapper();
			MLResultData resultML = null;
			resultML = mapper.readValue(outputML.getResult(), MLResultData.class);
			List<String> predictedStatusML = resultML.getPredictedData().getSTATUS();
		
			//Map<String, Object> execAttrs = (Map<String, Object>) executeResponseEntity.getBody();
			//LOG.info("result="+execAttrs.get("result"));
						
			AssetsRequest[] assetMLList = new AssetsRequest[predictResponseEntity.getBody().size()];
			
			
						
			//Loop though machine asset data and assign status values
			for (int i=0; i < predictResponseEntity.getBody().size(); i++)
			{
				AnalyticAttributes attrML = new AnalyticAttributes();
				AssetsRequest assetML = new AssetsRequest();
				Map<String, Object> attributesML = (Map<String, Object>) predictResponseEntity.getBody().get(i);
				
				//Assign new statuses to asset machine data
				assetML.setUri(attributesML.get("uri").toString());
				assetML.setModelName(attributesML.get("modelName").toString());
				assetML.setManufacturer(attributesML.get("manufacturer").toString());
				assetML.setPumpType(attributesML.get("pumpType").toString());
				assetML.setSerialNumber(attributesML.get("serialNumber").toString());
				assetML.setMachineHorsePower(Integer.parseInt(attributesML.get("machineHorsePower").toString()));
				assetML.setLatitude(attributesML.get("latitude").toString());
				assetML.setLongitude(attributesML.get("longitude").toString());
				assetML.setImage_url(attributesML.get("image_url").toString());
				assetML.setSuctionPressureRequired(Integer.parseInt(attributesML.get("suctionPressureRequired").toString()));
				assetML.setMaxPowerConsumption(Integer.parseInt(attributesML.get("maxPowerConsumption").toString()));
				assetML.setLastMaintenanceDate(attributesML.get("lastMaintenanceDate").toString());
				assetML.setManufacturedDate(attributesML.get("manufacturedDate").toString());
				assetML.setCapacity(Integer.parseInt(attributesML.get("capacity").toString()));
				assetML.setTsIdentifier_ingest(attributesML.get("tsIdentifier_ingest").toString());
				assetML.setTsIdentifier_discharge(attributesML.get("tsIdentifier_discharge").toString());
				assetML.setTsIdentifier_energyConsumption(attributesML.get("tsIdentifier_energyConsumption").toString());
				assetML.setTsIdentifier_vibration(attributesML.get("tsIdentifier_vibration").toString());
				assetML.setTsIdentifier_days_to_failure(attributesML.get("tsIdentifier_daystofailure").toString());
				
				
				//Extract analytics attribute, split, and parse into new AssetsRequest
				String attrsStr = attributesML.get("analyticAttributes").toString();
				Map<String,String> attrMap = splitToMap(attrsStr, ", ", "=");
				effP.add(Integer.parseInt(attrMap.get("machineEfficiencyLevel")));
				cavP.add(Integer.parseInt(attrMap.get("cavitation")));
				attrML.setAveragePowerConsumption(Integer.parseInt(attrMap.get("averagePowerConsumption")));
				attrML.setCavitation(Double.parseDouble(attrMap.get("cavitation")));
				attrML.setMachineEfficiencyLevel(Integer.parseInt(attrMap.get("machineEfficiencyLevel")));
				attrML.setError_rate(Integer.parseInt(attrMap.get("error_rate")));
				attrML.setSlope(Integer.parseInt(attrMap.get("slope")));
				
				//LOG.info("uri="+attributesML.get("uri").toString()+"status="+predictedStatusML.get(i).toString());
				String statusTemp = predictedStatusML.get(i).toString();
				
				if (statusTemp.equals("OPT"))
				{
					attrML.setMachineStatus("PERFORMING_OPTIMALLY");
				}
				else if (statusTemp.equals("PREDFAIL"))
				{
					attrML.setMachineStatus("PREDICTING_FAILURE");
				}
				else if (statusTemp.equals("FAIL"))
				{
					attrML.setMachineStatus("FAILURE");	
				}
				else
				{
					attrML.setMachineStatus("NULL");
				}
				
				
				assetML.setAnalyticAttributes(attrML);
				assetMLList[i] = assetML;
				//LOG.info(assetMLList[i].toString());
				attrML = null;
				assetML = null;
				statusTemp = null;
			}
			
			
		
			//LOG.info("attrs="+assetML.getAnalyticAttributes());
			//ingestMLAssetData(assetName, assetMLList);
						
			RestTemplate rt3 = new RestTemplate();
			rt3.getMessageConverters().add(new StringHttpMessageConverter());

			HttpHeaders assetHeaders2 = new HttpHeaders();
			assetHeaders2.setContentType(MediaType.APPLICATION_JSON);
			assetHeaders2.add(MLConstants.HEADER_AUTHORIZATION, token);
			assetHeaders2.add(MLConstants.HEADER_PREDIX_ZONEID, assetZoneId);
			//LOG.info("headers" + assetEntity2.toString());
			//LOG.info("headers" + assetEntity2.getBody().toString());
			//LOG.info(assetMLList.toString());
			
			//Post to Asset
			HttpEntity<Object> assetentity = new HttpEntity<Object>(assetMLList, assetHeaders2);
			ResponseEntity<String> response = rt3.exchange("https://predix-asset.run.aws-usw02-pr.ice.predix.io/v1/machine", HttpMethod.POST, assetentity, String.class);
			
			return "SUCCESS";	
			//return resultML.getPredictedData().toString();
			
			//return executeResponseEntity.getBody().toString();
			} catch (Exception ex) {
				LOG.error("Error in executeML --", ex);
				return null;
			}
	}
	
	/**
	 * splitToMap
	 * 
	 * @param source
	 * @param entriesSeparator
	 * @param keyValueSeparator
	 * @return Map<String, String>
	 */
	public static Map<String, String> splitToMap(String source, String entriesSeparator, String keyValueSeparator) {
	    Map<String, String> map = new HashMap<String, String>();
	    source = source.substring(1,source.length()-1);
	    //LOG.info(source);
	    String[] entries = source.split(entriesSeparator);
	    //LOG.info("entries="+entries.toString());
	    for (int i=0;i<entries.length;i++) 
	    {
	        String entry = entries[i];
	        //LOG.info("current entry="+entry);
	        String[] keyValue = entry.split("=");
	        //LOG.info("keyValue="+keyValue.toString());
	        //LOG.info("key="+keyValue[0]);
	        //LOG.info("value="+keyValue[1]);
	        //handle null values in Asset if they appear
	        if (keyValue[1]!=null)
	        {
	        	map.put(keyValue[0], keyValue[1]);
	        }
	        else
	        {
	        	map.put(keyValue[0], "0");
	        }
	    }
	    return map;
	}
	
	/**
	 * linearTrain
	 * 
	 * @return
	 **/
	 
	public String linearRegression(String assetName, String assetId){
		
        
      //Get pre-existing Asset data
        String assetUrl = predixAssetServiceUrl + "/"+ assetName;
		//This is the Asset Service bearer token using Salazar UAA
		Header tokenHeader = client.getTimeseriesHeaders().get(0);
		token = tokenHeader.getValue();
		LOG.info("client token="+token);
		

		RestTemplate rt = new RestTemplate();
		rt.getMessageConverters().add(new StringHttpMessageConverter());


		HttpHeaders assetHeaders = new HttpHeaders();
		assetHeaders.setContentType(MediaType.APPLICATION_JSON);
		assetHeaders.add(MLConstants.HEADER_AUTHORIZATION, token);
		assetHeaders.add(MLConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		HttpEntity assetEntity = new HttpEntity(assetHeaders);
		LOG.info("headers" + assetEntity.toString());
		
		ResponseEntity<List> assetResponseEntity = rt.exchange(assetUrl+"/"+assetId, HttpMethod.GET, assetEntity, List.class);
		LOG.info("asset="+assetResponseEntity.toString());
        
		AssetsRequest[] assetLRList = new AssetsRequest[assetResponseEntity.getBody().size()];
        AnalyticAttributes attrLR = new AnalyticAttributes();
		AssetsRequest assetLR = new AssetsRequest();
		Map<String, Object> attributesLR = (Map<String, Object>) assetResponseEntity.getBody().get(0);
        
		//Get tags for vibration and daystofailure from Asset
		String vibtag = attributesLR.get("tsIdentifier_vibration").toString();
        String daystag = attributesLR.get("tsIdentifier_days_to_failure").toString();
        
        String vibrationData = getTSData(assetName, assetId, vibtag);
        String daysData = getTSData(assetName, assetId, daystag);
        LOG.info(vibrationData);
        LOG.info(daysData);
        //return vibrationData;
        
        
        ObjectMapper mapper = new ObjectMapper();
		TSData vib = null;
		TSData days = null;
		try {
			vib = mapper.readValue(vibrationData, TSData.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			days = mapper.readValue(daysData, TSData.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//LOG.info("vibration data="+vib.getIngestPump9().toString());
		

        //DataPoints data = getVibrationTSData("test", "test");
        //Get the data from Timeseries
        //
        // This is the train data. 
        //
        //
        //double[] timeseries_x = {1,2,3,2,1,3,9,6,7,4,4,5,6,6};
        //double[] timeseries_y = {1,2,3,4,2,5,2,3,4,5,6,7,8,4};
		
		List<List<Double>> vibList = vib.getTSDATA();
		List<List<Double>> daysList = days.getTSDATA();
		
		int n = 0;
        double[] x = new double[vibList.size()];
        double[] y = new double[daysList.size()];
        
		double[] timeseries_x = new double[vibList.size()];
		double[] timeseries_y = new double[daysList.size()];
		
		//iterate up to size of y
		for (int i=0; i<daysList.size(); i++)
		{
			timeseries_x[i] = vibList.get(i).get(1);
			timeseries_y[i] = daysList.get(i).get(1);
			//Note, could be different sizes of x and y - y is the limiter
		}
		
		
        LOG.info("first element x="+timeseries_x[0]+"last element"+timeseries_x[19999]+"vibData size="+vibList.size()+"daysData size="+daysList.size()+"array length="+timeseries_x.length);
        LOG.info("first element y="+timeseries_y[0]+"last element"+timeseries_y[19999]+"vibData size="+vibList.size()+"daysData size="+daysList.size()+"array length="+timeseries_y.length);
        
        //return "SUCCESS";
        
        /////////////////
      
        
        // first pass: read in data, compute xbar and ybar
        double sumx = 0.0, sumy = 0.0, sumx2 = 0.0;
//        while(!StdIn.isEmpty()) {
//            x[n] = StdIn.readDouble();
//            y[n] = StdIn.readDouble();
//            sumx  += x[n];
//            sumx2 += x[n] * x[n];
//            sumy  += y[n];
//            n++;
//        }
        
        for (int i = 0; i < timeseries_y.length; i++) {
            x[n] = timeseries_x[n];
            y[n] = timeseries_y[n];
            sumx  += x[n];
            sumx2 += x[n] * x[n];
            sumy  += y[n];
            n++;
            //LOG.info("inside loop, n="+n);
        }
        double xbar = sumx / n;
        double ybar = sumy / n;

        // second pass: compute summary statistics
        double xxbar = 0.0, yybar = 0.0, xybar = 0.0;
        for (int i = 0; i < n; i++) {
            xxbar += (x[i] - xbar) * (x[i] - xbar);
            yybar += (y[i] - ybar) * (y[i] - ybar);
            xybar += (x[i] - xbar) * (y[i] - ybar);
        }
        double beta1 = xybar / xxbar;
        double beta0 = ybar - beta1 * xbar;

        // print results
        System.out.println("y   = " + beta1 + " * x + " + beta0);

        
               
        // analyze results
        int df = n - 2;
        double rss = 0.0;      // residual sum of squares
        double ssr = 0.0;      // regression sum of squares
        for (int i = 0; i < n; i++) {
            double fit = beta1*x[i] + beta0;
            rss += (fit - y[i]) * (fit - y[i]);
            ssr += (fit - ybar) * (fit - ybar);
        }
        double R2    = ssr / yybar;
        double svar  = rss / df;
        double svar1 = svar / xxbar;
        double svar0 = svar/n + xbar*xbar*svar1;
        System.out.println("R^2                 = " + R2);
        System.out.println("std error of beta_1 = " + Math.sqrt(svar1));
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));
        svar0 = svar * sumx2 / (n * xxbar);
        System.out.println("std error of beta_0 = " + Math.sqrt(svar0));

        System.out.println("SSTO = " + yybar);
        System.out.println("SSE  = " + rss);
        System.out.println("SSR  = " + ssr);
        
        
        //Update Asset with beta0 and beta1
        
        
		
		//Assign new statuses to asset machine data
		assetLR.setUri(attributesLR.get("uri").toString());
		assetLR.setModelName(attributesLR.get("modelName").toString());
		assetLR.setManufacturer(attributesLR.get("manufacturer").toString());
		assetLR.setPumpType(attributesLR.get("pumpType").toString());
		assetLR.setSerialNumber(attributesLR.get("serialNumber").toString());
		assetLR.setMachineHorsePower(Integer.parseInt(attributesLR.get("machineHorsePower").toString()));
		assetLR.setLatitude(attributesLR.get("latitude").toString());
		assetLR.setLongitude(attributesLR.get("longitude").toString());
		assetLR.setImage_url(attributesLR.get("image_url").toString());
		assetLR.setSuctionPressureRequired(Integer.parseInt(attributesLR.get("suctionPressureRequired").toString()));
		assetLR.setMaxPowerConsumption(Integer.parseInt(attributesLR.get("maxPowerConsumption").toString()));
		assetLR.setLastMaintenanceDate(attributesLR.get("lastMaintenanceDate").toString());
		assetLR.setManufacturedDate(attributesLR.get("manufacturedDate").toString());
		assetLR.setCapacity(Integer.parseInt(attributesLR.get("capacity").toString()));
		assetLR.setTsIdentifier_ingest(attributesLR.get("tsIdentifier_ingest").toString());
		assetLR.setTsIdentifier_discharge(attributesLR.get("tsIdentifier_discharge").toString());
		assetLR.setTsIdentifier_energyConsumption(attributesLR.get("tsIdentifier_energyConsumption").toString());
		assetLR.setTsIdentifier_vibration(attributesLR.get("tsIdentifier_vibration").toString());
		assetLR.setTsIdentifier_days_to_failure(attributesLR.get("tsIdentifier_days_to_failure").toString());
		//TODO: uncomment above when the constructor is updated by backend team
	
		//Extract analytics attribute, split, and parse into new AssetsRequest
		String attrsStr = attributesLR.get("analyticAttributes").toString();
		Map<String,String> attrMap = splitToMap(attrsStr, ", ", "=");
		
		
		//Assign Linear Regression beta 0 and beta 1 to power consumption and cavitation
		attrLR.setAveragePowerConsumption(Integer.parseInt(attrMap.get("averagePowerConsumption")));
		attrLR.setCavitation(Double.parseDouble(attrMap.get("cavitation")));
		attrLR.setMachineStatus(attrMap.get("machineStatus"));
		attrLR.setMachineEfficiencyLevel(Integer.parseInt(attrMap.get("machineEfficiencyLevel")));
		attrLR.setError_rate((int)beta0);
		attrLR.setSlope((int)beta1);
				
		assetLR.setAnalyticAttributes(attrLR);
		assetLRList[0] = assetLR;
		LOG.info(assetLRList.toString());
		
		//return assetLRList;
		
		//Post to Asset
		RestTemplate rt2 = new RestTemplate();
		rt2.getMessageConverters().add(new StringHttpMessageConverter());

		HttpHeaders assetHeaders2 = new HttpHeaders();
		assetHeaders2.setContentType(MediaType.APPLICATION_JSON);
		assetHeaders2.add(MLConstants.HEADER_AUTHORIZATION, token);
		assetHeaders2.add(MLConstants.HEADER_PREDIX_ZONEID, assetZoneId);
		//LOG.info("headers" + assetEntity2.toString());
		//LOG.info("headers" + assetEntity2.getBody().toString());
		//LOG.info(assetMLList.toString());
		
		//Post to Asset
		HttpEntity<Object> assetLRentity = new HttpEntity<Object>(assetLRList, assetHeaders2);
		//ResponseEntity<String> response = rt2.exchange(assetUrl, HttpMethod.POST, assetentity, String.class);
		ResponseEntity<String> response = rt.exchange(assetUrl, HttpMethod.POST, assetLRentity, String.class);
		//ResponseEntity<List> assetResponseEntity = rt.exchange(assetUrl, HttpMethod.GET, assetEntity, List.class);
		LOG.info("asset="+response.toString());

		return "y = beta1*x + beta0; beta1="+beta1+" beta0="+beta0;
		
	}

	
	/**
	 * queryForDatapoints
	 * 
	 * 
	 */
	public DatapointsResponse queryForDatapoints(String startDuration, int taglimit,List<String> searchTagList){
		
		DatapointsResponse response = null;
		
		
		try {
			
			DatapointsQuery dpq = new DatapointsQuery();
			dpq.setStart(startDuration);
			dpq.setTags(getTagListFromTimeSeries(searchTagList,taglimit));
			response = client.queryForDatapoints(dpq, client.getTimeseriesHeaders());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}
	


	/**
	 * getTagListFromTimeSeries
	 * 
	 * 
	 */
	private List<Tag> getTagListFromTimeSeries(List<String> searchTagList,int taglimit){
		
		
		TagsList s = client.listTags(client.getTimeseriesHeaders());
		List<Tag> listOfTags = new ArrayList<Tag>();
		int count = 0;
		
		for (String tag : s.getResults()) {
			LOG.info("tag --" + tag);
			Tag a = new Tag();
			a.setName(tag);
			if (containsAKeyword(tag, searchTagList)) {
				a.setLimit(taglimit);
				listOfTags.add(a);
				count++;
				LOG.info("count = " + count);
			}
		}
		
		return listOfTags;
		
	}
	

	/**
	 * containsAKeyword
	 * 
	 * 
	 */
	private boolean containsAKeyword(String word, List<String> keywords){
		   for(String keyword : keywords){
		      if(word.contains(keyword)){
		         return true;
		      }
		   }
		   return false; // Never found match.
	}

	
	
	/**
	 * GetVibrationTSData
	 * 
	 * 
	 */

	public String getTSData(String assetName,String assetId, String tag){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();

		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside Service class IF ");

		//Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);
		//LOG.info("assetIDData: "+assetIDData);

		List<String> searchTagList = new ArrayList<String>();
		//searchTagList.add("VibrationTest.pump99");
		//TODO: pass in string name from asset
		searchTagList.add(tag);
		//searchTagList.add(assetIDData.get(MLConstants.ASSET_TAG_NAME_DISCHARGE));	
		//searchTagList.add(assetIDData.get(MLConstants.ASSET_TAG_NAME_INGEST));


		LOG.info("callingTimeseries service");
		DatapointsResponse dpr = queryForDatapoints(MLConstants.DATA_POINTS_START_1YEAR, 20000, searchTagList);
		//TODO: Increase range and number of data points - 200,000 points per request, several years - 5 years ago
		LOG.info("Response DRP:"+dpr.toString());
	
		jsonResponseToClient =JsonMLUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside getTSData");
		return jsonResponseToClient;
		}
	}
	
	/**
	 * GetDaysToFailureTSData
	 * 
	 * 
	 */

	public String getDaysToFailureTSData(String assetName,String assetId){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();

		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside Service class IF ");

		//Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);
		//LOG.info("assetIDData: "+assetIDData);

		List<String> searchTagList = new ArrayList<String>();
		//searchTagList.add(assetIDData.get(MLConstants.ASSET_TAG_NAME_DISCHARGE));	
		//searchTagList.add(assetIDData.get(MLConstants.ASSET_TAG_NAME_INGEST));


		LOG.info("callingTimeseries service");
		DatapointsResponse dpr = queryForDatapoints(MLConstants.DATA_POINTS_START_1YEAR, 20000, searchTagList);
		LOG.info("Response DRP:"+dpr.toString());

		jsonResponseToClient =JsonMLUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside getDaysToFailure ");
		return jsonResponseToClient;
		}
	}
	
	
	/**
	 * generateTrainData
	 * 
	 * @param assetName
	 * @param request
	 * @return
	 
	public List<TrainingDataResponse> generateTrainData(TrainingDataParam trainDataParam){
		List<TrainingDataResponse> trainData = null;
		try{
		
			trainData = trainDataGenerator.generateTrainData(trainDataParam);
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return trainData;
	}
	**/
	
	/**
	 * IngestAssetData
	 * 
	 * @param assetName
	 * @param request
	 * @return
	 */
	public String ingestMLAssetData(String assetName, AssetsRequest[] request) {

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
			validateHeaders.add(MLConstants.HEADER_AUTHORIZATION, token);
			validateHeaders.add(MLConstants.HEADER_PREDIX_ZONEID, assetZoneId);

			for (int x = 0; x < request.length; x = x + 1) {
				List<AssetsRequest> requestArray = new ArrayList<>();
				requestArray.add(request[x]);
				HttpEntity validityEntity = new HttpEntity(requestArray, validateHeaders);
				LOG.info("headers" + validityEntity.toString());
				ResponseEntity<String> responseEntity = rt.exchange(url, HttpMethod.POST, validityEntity, String.class);
				LOG.info("response="+responseEntity.getBody());
			}

		} catch (Exception ex) {
			LOG.error("Error in ingestAssetData --", ex);
		}

		return "ok";
	}
}
