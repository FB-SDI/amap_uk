/**
 * 
 */
package com.ge.predix.solsvc.controller;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.Data;
import com.ge.predix.solsvc.model.OverallEfficiency;
import com.ge.predix.solsvc.model.OverallEfficiencyObject;
import com.ge.predix.solsvc.model.PerformanceMetrics;
import com.ge.predix.solsvc.model.PerformanceMetricsObject;
import com.ge.predix.solsvc.model.PumpData;
import com.ge.predix.solsvc.model.PumpType;
import com.ge.predix.solsvc.model.PumpTypeObject;
import com.ge.predix.solsvc.service.AssetService;
import com.ge.predix.solsvc.service.OverallEfficiencyService;
import com.ge.predix.solsvc.service.PerformanceMetricsService;
import com.ge.predix.solsvc.util.JsonUtil;

/**
 * @author ramalapoli
 *
 */
@RestController
public class DashboardController {

	@Autowired
	private JsonMapper jsonMapper;

	@Autowired
	private AssetService assetService;

	@Autowired
	private PerformanceMetricsService performanceMetricsService;

	@Autowired
	private OverallEfficiencyService overallEfficiencyService;

	private static final Logger LOG = LoggerFactory.getLogger(DashboardController.class);

	/**
	 * Returns Time Series Data for 3 sensors for specific use case to Sulzer
	 * for pump9
	 * 
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPerformanceMetric/{assetType}", method = RequestMethod.GET)
	public @ResponseBody Data getPerformanceMetricByType(@PathVariable("assetType") Optional<String> assetType) {
		LOG.info("Inside getPerformanceMetric");
		Data data = new Data();
		PerformanceMetrics performanceMetrics = new PerformanceMetrics();
		// String jsonResponseToClient =
		// "[{'name':'PerformanceOptimally','y':56.33},{'name':'Failure
		// Predicted','y':24.03},{'name':'Failure','y':10.38},{'name':'Undergoing
		// Maintenance','y':4.77},{'name':'No Data','y':1.11}]";
		List<PerformanceMetricsObject> performanceDataList = performanceMetricsService.getPerformanceMetrics(assetType);
		performanceMetrics.setPerformanceMetrics(performanceDataList);
		data.setData(performanceMetrics);
		return data;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getPerformanceMetric", method = RequestMethod.GET)
	public @ResponseBody Data getPerformanceMetric() {
		LOG.info("Inside getPerformanceMetric");
		Data data = new Data();
		PerformanceMetrics performanceMetrics = new PerformanceMetrics();
		// String jsonResponseToClient =
		// "[{'name':'PerformanceOptimally','y':56.33},{'name':'Failure
		// Predicted','y':24.03},{'name':'Failure','y':10.38},{'name':'Undergoing
		// Maintenance','y':4.77},{'name':'No Data','y':1.11}]";
		List<PerformanceMetricsObject> performanceDataList = performanceMetricsService.getPerformanceMetrics();
		performanceMetrics.setPerformanceMetrics(performanceDataList);
		data.setData(performanceMetrics);
		return data;
	}

	/**
	 * Returns Asset Type for specific use case to Sulzer Dashboard for pumps
	 * type PIOT -118
	 * 
	 * @return Json String
	 * @throws JsonProcesingException
	 * @throws JSONException
	 */
	@RequestMapping(value = "/getAssetType", method = RequestMethod.GET)
	public @ResponseBody PumpData getPumpData() {
		PumpData data = new PumpData();
		PumpType pumpType = new PumpType();
		List<PumpTypeObject> pumpDataList = JsonUtil.getDummyAssetTypeData();
		pumpType.setPumpType(pumpDataList);
		data.setData(pumpType);
		return data;

	}

	/**
	 * Returns Time Series Data for 3 sensors for specific use case to Sulzer
	 * for pump9
	 * 
	 * @return Json String
	 * @throws JsonProcessingException
	 * @throws JSONException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOverallEfficiency/{assetType}", method = RequestMethod.GET)
	public @ResponseBody OverallEfficiency getOverallEfficiencyByType(@PathVariable("assetType") Optional<String> assetType) {
		LOG.info("Inside getOverallEfficiency");
		OverallEfficiency overallefficiency = overallEfficiencyService.getOverallEfficiency(assetType);

		return overallefficiency;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/getOverallEfficiency", method = RequestMethod.GET)
	public @ResponseBody OverallEfficiency getOverallEfficiency() {
		LOG.info("Inside getOverallEfficiency");
		OverallEfficiency overallefficiency = overallEfficiencyService.getOverallEfficiency();

		return overallefficiency;
	}
}