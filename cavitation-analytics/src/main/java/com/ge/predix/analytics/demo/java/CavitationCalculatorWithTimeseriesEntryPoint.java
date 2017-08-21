/*
 * Copyright (c) 2015 - 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.analytics.demo.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ge.predix.analytics.customdto.AdderResponse;
import com.ge.predix.analytics.customdto.AnalyticAttributes;
import com.ge.predix.analytics.customdto.Data;
import com.ge.predix.analytics.customdto.Machine;
import com.ge.predix.analytics.customdto.TimeseriesOutput;

public class CavitationCalculatorWithTimeseriesEntryPoint {

	public static void main(String[] args) {
		System.out.println("I ran");
		CavitationCalculatorWithTimeseriesEntryPoint eff = new CavitationCalculatorWithTimeseriesEntryPoint();
		
//		String json = "{\"data\": {\"time_series\": {\"intake\": {\"value\": [10, 10, 10],\"time_stamp\": [1489107364000, 1489107364001, 1489107364002]},\"discharge\": {\"value\": [10, 10, 9],\"time_stamp\": [1489107364000, 1489107364001, 1489107364002]}}}}";
		String json = "{\"data\": {  \"time_series\": {    \"intake\": [250,30,100    ],    \"discharge\": [150,100,40    ],    \"time_stamp\": [\"1455733669601\",\"1455733669602\",\"1455733669603\"    ]  }}}";
		String discharge = "{ \"data\": {\"time_series\": {\"discharge\": [ 20.0,200.0, 455.0 ],\"time_stamp\": [\"1455733669601\",\"1455733669602\", \"1455733669603\" ] }}}";
		
		try {
			eff.cavitationCalculator(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	 Logger logger =  LoggerFactory.getLogger(CavitationCalculatorWithTimeseriesEntryPoint.class);
	ObjectMapper mapper = new ObjectMapper();

	/*
	 * public String calculateEfficiency(String jsonStr) throws IOException {
	 * 
	 * JsonNode node = mapper.readTree(jsonStr); JsonNode dataNode =
	 * node.get("data"); JsonNode timeseriesNode = dataNode.get("time_series");
	 * JsonNode timestampNode = timeseriesNode.get("time_stamp"); List<Double>
	 * results = null; List<String> timestampArray = null; if
	 * (timestampNode.isArray()) { ArrayNode timestamps = (ArrayNode)
	 * timestampNode;
	 * 
	 * JsonNode number1Node = timeseriesNode.get("numberArray1"); ArrayNode
	 * number1Values = (ArrayNode) number1Node;
	 * 
	 * JsonNode number2Node = timeseriesNode.get("numberArray2"); ArrayNode
	 * number2Values = (ArrayNode) number2Node;
	 * 
	 * results = new ArrayList<>(); timestampArray = new ArrayList<>();
	 * 
	 * for (int i = 0; i < timestamps.size(); i++) {
	 * timestampArray.add(timestamps.get(i).asText());
	 * results.add(number1Values.get(i).asDouble() +
	 * number2Values.get(i).asDouble()); } }
	 * 
	 * AdderResponse outputResponse = new AdderResponse(); Data data = new
	 * Data(); TimeseriesOutput output = new TimeseriesOutput();
	 * data.setTime_series(output); output.setTime_stamp(timestampArray);
	 * output.setSum(results); outputResponse.setData(data);
	 * 
	 * mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
	 * 
	 * return mapper.writeValueAsString(outputResponse);
	 * 
	 * }
	 */

	private double calculateAverage(List<Double> results) {
		Double sum = 0.0;
		if (!results.isEmpty()) {
			for (Double mark : results) {
				sum += mark;
			}
			return sum.doubleValue() / results.size();
		}
		return sum;
	}

	/**
	 * This method will use assetId=1(hardcoded) and will calculate efficiency
	 * on top of provided timeseries data using the horsePower value returned
	 * from assets endpoint call.
	 * 
	 * @param jsonStr
	 * @return
	 * @throws IOException
	 */
	public String cavitationCalculator(String jsonStr) throws IOException {

		// Get the asset data for machine=1 or 3 using endpoint
		// id =1 ; when it is calculated for values coming from machine
		// id =3 ; when it is calculated for values coming from stored values
		String machineId = "3";
		String url = "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/getAssetDataByAssetId/machine/"
				+ machineId;

		// JSON to Java
		ObjectMapper mapper = new ObjectMapper();
		// JSON from URL to Object
		Machine obj = mapper.readValue(new URL(url), Machine.class);
		// logger.info("horsepowere values for machine:" +
		// obj.getMachineHorsePower());
		Double hpValue = (double) obj.getMachineHorsePower();
		System.out.println("horsepowere values for machine:" + obj.getMachineHorsePower());
		logger.info("horsepowere values for machine coming from assets: {}",obj.getMachineHorsePower());
		if( jsonStr==null ){
			return "";
		}
		JsonNode node = mapper.readTree(jsonStr);
		JsonNode dataNode = node.get("data");
		JsonNode timeseriesNode = dataNode.get("time_series");
		JsonNode intakeNode = timeseriesNode.get("intake");
		JsonNode dischargeNode = timeseriesNode.get("discharge");
		JsonNode timestampNode = timeseriesNode.get("time_stamp");
		
		
		List<Double> results = null;
		List<String> timestampArray = null;
		if (timestampNode.isArray()) {
			ArrayNode timestamps = (ArrayNode) timeseriesNode.get("time_stamp");
			ArrayNode number1Values = (ArrayNode) timeseriesNode.get("intake");
			ArrayNode number1ValuesDischarge = (ArrayNode) timeseriesNode.get("discharge");
			
			
			results = new ArrayList<>();
			timestampArray = new ArrayList<>();

			for (int i = 0; i < timestamps.size(); i++) {
				timestampArray.add(timestamps.get(i).asText());
				System.out.println("Intake" + number1Values.get(i).asDouble());
				System.out.println("Discharge" + number1ValuesDischarge.get(i).asDouble());
				results.add( (number1ValuesDischarge.get(i).asDouble() - number1Values.get(i).asDouble()) * 0.45 + 23 );
				// I need average here ( discharge pressure – Ingest Pressure *
				// 0.45 + 23)
			}
		}

		System.out.println(results.toString());

		Double sum = calculateAverage(results);
		System.out.println(sum);
		logger.info("Aggr. value after running analytics: {}", sum);
		AnalyticAttributes zip = obj.getAnalyticAttributes();
		// zip.getMachineEfficiencyLevel();
		zip.setCavitation(sum);
		System.out.println("Image Url" + obj.getImage_url());
		// zip.setMachineEfficiencyLevel(sum);
		System.out.println("Zip MachEff:" + zip.getCavitation());

		String assetUrlstr = "https://timeseries-test1.run.aws-usw02-pr.ice.predix.io/ingestAssetData/machine";
		HttpClient httpClient = HttpClientBuilder.create().build();

		try {

			HttpPost request = new HttpPost(assetUrlstr);

			String jsonInString = mapper.writeValueAsString(obj);
			StringEntity params = new StringEntity("[" + jsonInString + "]");
			System.out.println(jsonInString);
			request.addHeader("Content-Type", "application/json; charset=UTF-8");
			request.setEntity(params);
			HttpResponse response = httpClient.execute(request);
			System.out.println("response" + response.toString());
		} catch (Exception e) {

		}

		AdderResponse outputResponse = new AdderResponse();
		Data data = new Data();
		TimeseriesOutput output = new TimeseriesOutput();
		output.setTime_stamp(timestampArray);
		output.setCavitation(results);
		data.setTime_series(output);
		outputResponse.setData(data);

		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		System.out.println("End Result: " + mapper.writeValueAsString(outputResponse));
		return mapper.writeValueAsString(outputResponse);

	}
}