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
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.ge.predix.analytics.customdto.AdderResponse;
import com.ge.predix.analytics.customdto.AnalyticAttributes;
import com.ge.predix.analytics.customdto.Data;
import com.ge.predix.analytics.customdto.Machine;
import com.ge.predix.analytics.customdto.TimeseriesOutput;

public class EfficiencyCalculatorWithTimeseriesEntryPoint {

	public static void main(String[] args) {
		System.out.println("I ran");
		EfficiencyCalculatorWithTimeseriesEntryPoint eff = new EfficiencyCalculatorWithTimeseriesEntryPoint();
		String json = "{ \"data\": {\"time_series\": {\"intake\": [ 50.0,150.0, 125.0 ],\"time_stamp\": [\"1455733669601\",\"1455733669602\", \"1455733669603\" ] }}}";
		try {
			eff.calculateEfficiency(json);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// Logger logger =
	// LoggerFactory.getLogger(EfficiencyCalculatorWithTimeseriesEntryPoint.class);
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

	/**
	 * This method will use assetId=1(hardcoded) and will calculate efficiency
	 * on top of provided timeseries data using the horsePower value returned
	 * from assets endpoint call.
	 * 
	 * @param jsonStr
	 * @return
	 * @throws IOException
	 */
	public String calculateEfficiency(String jsonStr) throws IOException {

		// Get the asset data for machine=1 using endpoint
		// id =1 ; when it is calculated for values coming from machine
		// id =3 ; when it is calculated for values coming from stored values
		String machineId = "1";
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
		JsonNode node = mapper.readTree(jsonStr);
		JsonNode dataNode = node.get("data");
		JsonNode timeseriesNode = dataNode.get("time_series");
		JsonNode timestampNode = timeseriesNode.get("time_stamp");
		List<Double> results = null;
		List<String> timestampArray = null;
		if (timestampNode.isArray()) {
			ArrayNode timestamps = (ArrayNode) timestampNode;

			JsonNode number1Node = timeseriesNode.get("intake");
			ArrayNode number1Values = (ArrayNode) number1Node;

			results = new ArrayList<>();
			timestampArray = new ArrayList<>();

			for (int i = 0; i < timestamps.size(); i++) {
				timestampArray.add(timestamps.get(i).asText());
				results.add(100 * (1 - (number1Values.get(i).asDouble() / (hpValue * 0.89))));
				// I need average here
			}
		}
		
		System.out.println(results.toString());
		
		Double sum = calculateAverage(results);
		System.out.println(sum);
		AnalyticAttributes zip = obj.getAnalyticAttributes();
		zip.getMachineEfficiencyLevel();
		zip.setMachineEfficiencyLevel(sum);
		System.out.println("Zip MachEff:" + zip.getMachineEfficiencyLevel());

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
		data.setTime_series(output);
		output.setTime_stamp(timestampArray);
		output.setEfficiency(results);
		outputResponse.setData(data);

		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		System.out.println("End Result: "+mapper.writeValueAsString(outputResponse));
		return mapper.writeValueAsString(outputResponse);

	}

	private double calculateAverage(List<Double> results) {
		  Double sum = 0.0;
		  if(!results.isEmpty()) {
		    for (Double mark : results) {
		        sum += mark;
		    }
		    return sum.doubleValue() / results.size();
		  }
		  return sum;
		}
	/**
	 * This we can use when we pass 'hp' as an argument at runtime using
	 * inputModelMap
	 * 
	 * @param jsonStr
	 * @param inputModelMap
	 * @return
	 * @throws IOException
	 */
	public String calculateEfficiencyData(String jsonStr, Map<String, byte[]> inputModelMap) throws IOException {

		Double hpValue = Double.parseDouble(new String(inputModelMap.get("hp")));
		// logger.info("Threshold set to:" + hpValue);

		JsonNode node = mapper.readTree(jsonStr);
		JsonNode dataNode = node.get("data");
		JsonNode timeseriesNode = dataNode.get("time_series");
		JsonNode timestampNode = timeseriesNode.get("time_stamp");
		List<Double> results = null;
		List<String> timestampArray = null;
		if (timestampNode.isArray()) {
			ArrayNode timestamps = (ArrayNode) timestampNode;

			JsonNode number1Node = timeseriesNode.get("numberArray");
			ArrayNode number1Values = (ArrayNode) number1Node;

			results = new ArrayList<>();
			timestampArray = new ArrayList<>();

			for (int i = 0; i < timestamps.size(); i++) {
				timestampArray.add(timestamps.get(i).asText());
				results.add(100 * (1 - (number1Values.get(i).asDouble() / (hpValue * 0.89))));
			}
		}

		AdderResponse outputResponse = new AdderResponse();
		Data data = new Data();
		TimeseriesOutput output = new TimeseriesOutput();
		data.setTime_series(output);
		output.setTime_stamp(timestampArray);
		output.setEfficiency(results);
		outputResponse.setData(data);

		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

		return mapper.writeValueAsString(outputResponse);

	}
}