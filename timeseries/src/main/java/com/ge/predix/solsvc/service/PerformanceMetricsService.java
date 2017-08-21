package com.ge.predix.solsvc.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.LinkedHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ge.predix.solsvc.model.PerformanceMetricsObject;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;

@Service
public class PerformanceMetricsService {

	
	@Autowired
	private AssetService assetService;
	
	@Autowired
	private TimeseriesClient client;
	
	private static final Logger LOG = LoggerFactory.getLogger(PerformanceMetricsService.class);

	public List<PerformanceMetricsObject> getPerformanceMetrics(Optional<String>  assetType) {

		List<PerformanceMetricsObject> performanceList = new ArrayList<PerformanceMetricsObject>();

		PerformanceMetricsObject performanceOptimally = new PerformanceMetricsObject();
		performanceOptimally.setLabel("Performing Optimally");
		
		PerformanceMetricsObject predictiveFailure = new PerformanceMetricsObject();
		predictiveFailure.setLabel("Failure Predicted");
		
		PerformanceMetricsObject failure = new PerformanceMetricsObject();
		failure.setLabel("Failure");
		
		PerformanceMetricsObject undergoingMaintanence = new PerformanceMetricsObject();
		undergoingMaintanence.setLabel("Undergoing Maintenance");
		
		PerformanceMetricsObject noData = new PerformanceMetricsObject();
		noData.setLabel("No Data");
		
		List<Map<String,Object>> listOfJsonObjects = (List<Map<String,Object>>)assetService.getAssetDatabyFilter(null, null, null, assetType);
		//TODO need to String to Json
		LOG.info("listOfJsonObjects--" + listOfJsonObjects);
		
		Iterator<Map<String, Object>> it = listOfJsonObjects.iterator();
		while (it.hasNext()) {
		    Map<String, Object> assetMap = it.next(); //so here you don't need a potentially unsafe cast
		    Object temp = assetMap.get("analyticAttributes");
			Map<String,String> machineStatusMap = (LinkedHashMap)temp;
			LOG.info("analyticAttributes:"+machineStatusMap.get("machineStatus"));
			
			String pumpType = (String) assetMap.get("pumpType");
			LOG.info("analyticAttributes:" + pumpType);
			
			if( !pumpType.equalsIgnoreCase(assetType.orElse(""))){
				continue;
			}
			
			String machineStatus = machineStatusMap.get("machineStatus");
			switch(machineStatus){	
			case "PERFORMING_OPTIMALLY":
				//Add to Performance Optimally
				performanceOptimally.setUnits(performanceOptimally.getUnits() + 1);
				break;
			case "PREDICTING_FAILURE":
				//
				predictiveFailure.setUnits(predictiveFailure.getUnits() + 1);
				break;
			case "FAILURE":
				//
				failure.setUnits(failure.getUnits() + 1);
				break;
			case "UNDERGOING_MAINTENANCE":
				//
				undergoingMaintanence.setUnits(undergoingMaintanence.getUnits() + 1);
				break;
				
			case "NO_DATA":
				//
				noData.setUnits(noData.getUnits() +1);
				break;	
			}
		}
		LOG.info("Perform List" + performanceList.toString());
		LOG.info("Undergoing Maintenance" + undergoingMaintanence.toString() );
		LOG.info("Metrics Ran");
		performanceList.add(performanceOptimally);
		performanceList.add(predictiveFailure);
		performanceList.add(failure);
		performanceList.add(undergoingMaintanence);
		
		performanceList.add(noData);
		
		return performanceList;

	}

	public List<PerformanceMetricsObject> getPerformanceMetrics() {
		List<PerformanceMetricsObject> performanceList = new ArrayList<PerformanceMetricsObject>();

		PerformanceMetricsObject performanceOptimally = new PerformanceMetricsObject();
		performanceOptimally.setLabel("Performing Optimally");
		
		PerformanceMetricsObject predictiveFailure = new PerformanceMetricsObject();
		predictiveFailure.setLabel("Failure Predicted");
		
		PerformanceMetricsObject failure = new PerformanceMetricsObject();
		failure.setLabel("Failure");
		
		PerformanceMetricsObject undergoingMaintanence = new PerformanceMetricsObject();
		undergoingMaintanence.setLabel("Undergoing Maintenance");
		
		PerformanceMetricsObject noData = new PerformanceMetricsObject();
		noData.setLabel("No Data");
		
		List<Map<String,Object>> listOfJsonObjects = (List<Map<String,Object>>)assetService.getAssetDatabyFilter(null, null, null, null);
		//TODO need to String to Json
		LOG.info("listOfJsonObjects--" + listOfJsonObjects);
		
		Iterator<Map<String, Object>> it = listOfJsonObjects.iterator();
		while (it.hasNext()) {
		    Map<String, Object> assetMap = it.next(); //so here you don't need a potentially unsafe cast
		    Object temp = assetMap.get("analyticAttributes");
			Map<String,String> machineStatusMap = (LinkedHashMap)temp;
			LOG.info("analyticAttributes:"+machineStatusMap.get("machineStatus"));
			String machineStatus = machineStatusMap.get("machineStatus");
			switch(machineStatus){	
			case "PERFORMING_OPTIMALLY":
				//Add to Performance Optimally
				performanceOptimally.setUnits(performanceOptimally.getUnits() + 1);
				break;
			case "PREDICTING_FAILURE":
				//
				predictiveFailure.setUnits(predictiveFailure.getUnits() + 1);
				break;
			case "FAILURE":
				//
				failure.setUnits(failure.getUnits() + 1);
				break;
			case "UNDERGOING_MAINTENANCE":
				//
				undergoingMaintanence.setUnits(undergoingMaintanence.getUnits() + 1);
				break;
				
			case "NO_DATA":
				//
				noData.setUnits(noData.getUnits() +1);
				break;	
			}
		}
	
		LOG.info("Metrics Ran");
		performanceList.add(performanceOptimally);
		performanceList.add(predictiveFailure);
		performanceList.add(failure);
		performanceList.add(undergoingMaintanence);
		performanceList.add(noData);
		return performanceList;
	}

}

/*
 * {"data":{"performanceMetrics"
[{"label":"Performance Optimally","units":1733,"percentage":74},
{"label":"Failure Predicted","units":1733,"percentage":20},
{"label":"Failure","units":1733,"percentage":20},
{"label":"Undergoing Maintanence","units":1733,"percentage":20},
{"label":"No Data","units":1733,"percentage":20}]}}
 */
/*{
    "uri": "/machine/1",
    "modelName": "API610",
    "manufacturer": "GreatCO",
    "serialNumber": "ER24123",
    "machineHorsePower": 100,
    "latitude": "45.207111",
    "longitude": "-0.381556",
    "suctionPressureRequired": 200,
    "maxPowerConsumption": 100,
    "lastMaintenanceDate": "1/1/2001",
    "manufacturedDate": "1/10/1997",
    "tsIdentifier_ingest": "Ingest.pump9",
    "tsIdentifier_discharge": "Discharge.pump9",
    "tsIdentifier_energyConsumption": "EnergyConsumption.pump9",
    "analyticAttributes": {
      "averagePowerConsumption": 90,
      "machineEfficiencyLevel": 85,
      "machineStatus": "PERFORMING_OPTIMALLY"
    }*/
