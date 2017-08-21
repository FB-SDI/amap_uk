package com.ge.predix.solsvc.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.predix.solsvc.model.OverallEfficiency;
import com.ge.predix.solsvc.model.OverallEfficiencyObject;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

@Service
public class OverallEfficiencyService {

	@Autowired
	private AssetService assetService;

	@Autowired
	private TimeseriesClient client;

	private static final Logger LOG = LoggerFactory.getLogger(OverallEfficiencyService.class);

	public OverallEfficiency getOverallEfficiency(Optional<String> assetType) {
		OverallEfficiency overallefficiency = new OverallEfficiency();

		OverallEfficiencyObject overallefficiencyobj = new OverallEfficiencyObject();

		List<Integer> results = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listOfJsonObjects = (List<Map<String, Object>>) assetService
				.getAssetDatabyFilter(null, null, null, assetType);
		// TODO need to String to Json
		LOG.info("listOfJsonObjects--" + listOfJsonObjects);
		Iterator<Map<String, Object>> it = listOfJsonObjects.iterator();
		while (it.hasNext()) {
			Map<String, Object> assetMap = it.next(); // so here you don't need
														// a potentially unsafe
														// cast
			Object temp = assetMap.get("analyticAttributes");			
			Map<String, Integer> machineEfficiencyLevelMap = (LinkedHashMap) temp;
			LOG.info("analyticAttributes:" + machineEfficiencyLevelMap.get("machineEfficiencyLevel"));
			int machineEfficiencyLevel = machineEfficiencyLevelMap.get("machineEfficiencyLevel");
			LOG.info("analyticAttributes:" + machineEfficiencyLevelMap.get("machineStatus"));

			Map<String, String> machineStatusLevelMap = (LinkedHashMap) temp;
			String machineStatus = machineStatusLevelMap.get("machineStatus");
			LOG.info("analyticAttributes:" + machineStatusLevelMap.get("machineStatus"));
			
			String pumpType = (String) assetMap.get("pumpType");
			LOG.info("analyticAttributes:" + pumpType);
			if (machineStatus == "NO_DATA" || machineStatus == "UNDERGOING_MAINTENANCE") {

			} else {				
				if( pumpType.equalsIgnoreCase(assetType.orElse(""))){
					results.add(machineEfficiencyLevel);
				}				
			}
		}

		System.out.println(results.toString());

		int sum = (int) calculateAverage(results);
		System.out.println(sum);
		overallefficiencyobj.setOverallEfficeincy(sum);
		overallefficiency.setData(overallefficiencyobj);

		return overallefficiency;
	}

	private double calculateAverage(List<Integer> results) {
		Double sum = 0.0;
		if (!results.isEmpty()) {
			for (Integer mark : results) {
				sum += mark;
			}
			return sum.doubleValue() / results.size();
		}
		return sum;
	}

	public OverallEfficiency getOverallEfficiency() {
		OverallEfficiency overallefficiency = new OverallEfficiency();

		OverallEfficiencyObject overallefficiencyobj = new OverallEfficiencyObject();

		int x = 0;
		List<Integer> results = new ArrayList<>();
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listOfJsonObjects = (List<Map<String, Object>>) assetService
				.getAssetDatabyFilter(null, null, null, null);
		// TODO need to String to Json
		LOG.info("listOfJsonObjects--" + listOfJsonObjects);
		Iterator<Map<String, Object>> it = listOfJsonObjects.iterator();
		while (it.hasNext()) {
			Map<String, Object> assetMap = it.next(); // so here you don't need
														// a potentially unsafe
														// cast
			Object temp = assetMap.get("analyticAttributes");
			Map<String, Integer> machineEfficiencyLevelMap = (LinkedHashMap) temp;
			LOG.info("analyticAttributes:" + machineEfficiencyLevelMap.get("machineEfficiencyLevel"));
			int machineEfficiencyLevel = machineEfficiencyLevelMap.get("machineEfficiencyLevel");

			Map<String, String> machineStatusLevelMap = (LinkedHashMap) temp;
			String machineStatus = machineStatusLevelMap.get("machineStatus");
			LOG.info("analyticAttributes:" + machineStatusLevelMap.get("machineStatus"));
			if (machineStatus.equals("NO_DATA") || machineStatus.equals("UNDERGOING_MAINTENANCE")) {

			} else {
				results.add(machineEfficiencyLevel);
			}
		}

		System.out.println(results.toString());

		int sum = (int) calculateAverage(results);
		System.out.println(sum);
		overallefficiencyobj.setOverallEfficeincy(sum);
		overallefficiency.setData(overallefficiencyobj);

		return overallefficiency;
	}
}
