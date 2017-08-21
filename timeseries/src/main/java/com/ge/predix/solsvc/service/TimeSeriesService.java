/**
 * 
 */
package com.ge.predix.solsvc.service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.Body;
import com.ge.predix.entity.timeseries.datapoints.ingestionrequest.DatapointsIngestion;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.DatapointsQuery;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.Tag;
import com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.DatapointsLatestQuery;
import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.entity.timeseries.tags.TagsList;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.TimeSeriesRequest;
import com.ge.predix.solsvc.timeseries.bootstrap.client.TimeseriesClient;
import com.ge.predix.solsvc.util.JsonUtil;
import com.ge.predix.solsvc.util.TimeSeriesConstants;


/**
 * @author ramalapoli
 *
 */
@Service
public class TimeSeriesService {

	@Autowired
	private TimeseriesClient client;
	private static final Logger LOG = LoggerFactory.getLogger(TimeSeriesService.class);
	@Autowired
	private JsonMapper jsonMapper;
	@Autowired
	private AssetService assetService;
	
	/**
	 * Timeseries Data ingestion service
	 * 
	 * 
	 * Task: PIOT-188
	 *	(PIOT-133 O&G : We would like to connect an asset tag from data coming from Predix to time series)
     *
	 * 
	 */
	public String ingestTimeSeriesData(DatapointsIngestion datapointsIngestion){
		
		try {
			
			String jsonToSend = this.jsonMapper.toJson(datapointsIngestion);
			LOG.info("TimeSeries JSON : " + jsonToSend);
			if ( datapointsIngestion.getBody() == null || datapointsIngestion.getBody().size() < 1 )
			{
				LOG.error("datapointsIngestion request is empty ");
				return "false";
			}
			
			LOG.info("Creating connection ");
			client.createConnectionToTimeseriesWebsocket();
			LOG.info("connected");
			client.postDataToTimeseriesWebsocket(datapointsIngestion);
			LOG.info("postDataToTimeseriesWebsocket----");
			//LOG.info(client.getTimeseriesHeaders());
			LOG.info("Tags ---" + client.listTags(client.getTimeseriesHeaders()));

			TagsList s = client.listTags(client.getTimeseriesHeaders());

			for (String tag : s.getResults()) {
				LOG.info("tag --" + tag);
			}

		} catch (Exception ex) {
			LOG.error("Error in ingestTimeSeriesData --" , ex);
		}
		
		return "ok";
	}
	
	
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
	 * Timeseries Data ingestion service from Excel File
	 * 
	 * 
	 * Task: PIOT-218
	 *	(PIOT-145 O&G : As an admin, I should be able to upload a data file, so that i can run analytics on itPIOT)
	 * 
	 * 
	 * @param excelName
	 * @param timeInterval
	 * @throws Exception
	 */
	public void ingestTimeSeriesDataFromExcel(String excelName, int timeInterval) throws Exception{
		
		
		LOG.info("ingestTimeSeriesDataFromExcel ");
		TimeSeriesRequest request = new TimeSeriesRequest();
		long currentTimeStampValue = System.currentTimeMillis();
		LOG.info("currentTimeStampValue =" + currentTimeStampValue);
		List<Body> bodyList = new ArrayList<>();
		Body dataIngestionBody = new Body();
		Workbook workbook = null;
		Sheet firstSheet;
		InputStream is = null;
		try{
			is = RestController.class.getResourceAsStream("/" 
															+ TimeSeriesConstants.TIMESERIES_DATA_EXCEL_NAME);
			workbook = new XSSFWorkbook(is);
			firstSheet = workbook.getSheetAt(0);	
			LOG.info("firstSheet.getPhysicalNumberOfRows() =" + firstSheet.getPhysicalNumberOfRows());
			for(int x =0;x < 3; x++){//Column
				List<Object> datapoints = new ArrayList<>();
				for(int z = 0 ; z < firstSheet.getPhysicalNumberOfRows(); z++){//Row
					LOG.info("Cell = "+firstSheet.getRow(z).getCell(x));
					List<Object> datapoint = new ArrayList<Object>();
					if(z == 0){
						String tagName = firstSheet.getRow(0).getCell(x).getStringCellValue();
						dataIngestionBody.setName(tagName);
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
							dataIngestionBody.setAttributes(attributeDataMap);
						}
						
					}else{
						datapoint.add(currentTimeStampValue - (z * timeInterval) );
						Cell cell = firstSheet.getRow(z).getCell(x);

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							datapoint.add(cell.getStringCellValue());
							LOG.info("String = "+cell.getStringCellValue());
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							datapoint.add(cell.getBooleanCellValue());
							LOG.info("Boolean" + cell.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_NUMERIC:
							LOG.info("Numeric =" + (int)cell.getNumericCellValue());
							datapoint.add((int)cell.getNumericCellValue());
							break;
						default:
							LOG.info("adding as is cell =" + cell);
							datapoint.add(cell);
							break;
						}
						LOG.info("firstSheet.getRow(z).getCell(3)=" + (int)firstSheet.getRow(z).getCell(3).getNumericCellValue()); 
						datapoint.add((int)firstSheet.getRow(z).getCell(3).getNumericCellValue());
						datapoints.add(datapoint);	
					}
				}
				dataIngestionBody.setDatapoints(datapoints);
				bodyList.add(dataIngestionBody);
				request.setBody(bodyList);
				request.setMessageId(String.valueOf(System.currentTimeMillis()));
				LOG.info("request =" + request.toString());
				DatapointsIngestion dp = new DatapointsIngestion();
				dp.setMessageId(request.getMessageId());
				dp.setBody(request.getBody());
				ingestTimeSeriesData(dp);
				LOG.info("End of Row" + x);
			}
		}catch(Exception ex){
			LOG.error("ingestTimeSeriesDataFromExcel",ex);
		}finally{
			workbook.close();
			is.close();
		}
	}
	
	
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
	
	
	private boolean containsAKeyword(String word, List<String> keywords){
		   for(String keyword : keywords){
		      if(word.contains(keyword)){
		         return true;
		      }
		   }
		   return false; // Never found match.
	}


	/**
	 *  Returns Time Series Data for pump based on the Asset name and Id
	 * @param dataPointsStart1year
	 * @param i
	 * @param assetName
	 * @param assetid
	 * @return
	 */
	public DatapointsResponse queryForDatapoints(String startDuration, int taglimit, String assetName, String assetid) {
		
		Map<String,String> assetIdData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName, assetid);
		
		String tsIdentifier = assetIdData.get("tsIdentifier");
		
		LOG.info("tsIdentifier --" + tsIdentifier);
		
		List<String> searchTagList = new ArrayList<String>();
		searchTagList.add(tsIdentifier);
		
		DatapointsResponse dpr = queryForDatapoints(startDuration, taglimit, searchTagList);
		
		return dpr;
	}
	
	
	

	/**
	 * GetDischargePRessureGraph
	 * 
	 * This method implements PIOT-191 Earth which is the part of PIOT-139
	 */

	public String getDischargePRessureGraph(String assetName,String assetId){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();

		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside Service class IF ");

		Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);
		LOG.info("assetIDData: "+assetIDData);

		List<String> searchTagList = new ArrayList<String>();
		searchTagList.add(assetIDData.get(TimeSeriesConstants.ASSET_TAG_NAME_DISCHARGE));	
		searchTagList.add(assetIDData.get(TimeSeriesConstants.ASSET_TAG_NAME_INGEST));


		LOG.info("callingTimeseries service");
		DatapointsResponse dpr = queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 2000, searchTagList);
		LOG.info("Response DRP:"+dpr.toString());

		jsonResponseToClient =JsonUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside Service class Esle ");
		return jsonResponseToClient;
		}
	}

	/**
	 * GetEnergyConsumptionGraph
	 * This method implements PIOT-191 Earth which is the part of PIOT-139
	 */
	
	public String getEnergyConsumptionGraph(String assetName,String assetId){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();


		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside Service class IF ");

		Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);

		List<String> searchTagList = new ArrayList<String>();

		searchTagList.add(assetIDData.get(TimeSeriesConstants.ASSET_TAG_NAME_ENERGYCONSUMPTION));


		LOG.info("callingTimeseries service");
		DatapointsResponse dpr = queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 2000, searchTagList);
		LOG.info("Response DRP:"+dpr.toString());
		jsonResponseToClient =JsonUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside Service class Esle ");
		return jsonResponseToClient;
		}
	}	
	
	
	
	
	/**
	 * Get Latest DataPoints for Time Series Tag
	 * 
	 * 
	 * @param searchTag
	 * @return
	 */
	public DatapointsResponse querylatestDatapointsForTimeseriesTag(String searchTag){

		DatapointsResponse response = null;
		
		try {
			
			DatapointsLatestQuery dpQuery = buildLatestDatapointsQueryRequest(searchTag);
			response = client.queryForLatestDatapoint(dpQuery, client.getTimeseriesHeaders());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return response;
	}
	
	
	private DatapointsLatestQuery buildLatestDatapointsQueryRequest(String searchTag) {
		DatapointsLatestQuery datapointsLatestQuery = new DatapointsLatestQuery();

		com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag tag = new com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag();
		tag.setName(searchTag);
		List<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag> tags = new ArrayList<com.ge.predix.entity.timeseries.datapoints.queryrequest.latest.Tag>();
		tags.add(tag);
		datapointsLatestQuery.setTags(tags);
		return datapointsLatestQuery;
	}
	
	
	/**
	 * getVibrationGraph
	 * PIOT-500 O&G : Asset Detail : Vibration input should display as a time series graph in Asset Detail pagePIOT-536
	 */
	
	public String getVibrationGraph(String assetName,String assetId){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();


		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside Service class IF ");

		Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);

		List<String> searchTagList = new ArrayList<String>();

		searchTagList.add(assetIDData.get(TimeSeriesConstants.ASSET_TAG_NAME_VIBRATION));


		LOG.info("callingTimeseries service");
		DatapointsResponse dpr = queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 20000, searchTagList);
		LOG.info("Response DRP:"+dpr.toString());
		jsonResponseToClient =JsonUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside Service class Esle ");
		return jsonResponseToClient;
		}
	}	
	
	
	
	public String getTimeseriesGraph(String assetName,String assetId, String tsTagIdentifier){

		// code for getting data from timeseries Instance

		String jsonResponseToClient = new String();


		if(!(assetName == null) && !(assetId == null) )
		{	LOG.info("Inside getTimeseriesGraph Service class IF ");

		Map<String,String> assetIDData = (Map<String, String>) assetService.getAssetDataByAssetId(assetName,assetId);

		List<String> searchTagList = new ArrayList<String>();

		searchTagList.add(assetIDData.get(tsTagIdentifier));


		LOG.info("callingTimeseries service in getTimeseriesGraph()");
		DatapointsResponse dpr = queryForDatapoints(TimeSeriesConstants.DATA_POINTS_START_1YEAR, 20000, searchTagList);
		LOG.info("Response DRP:"+dpr.toString());
		jsonResponseToClient =JsonUtil.getJsonDataForAssetGraphs(dpr, jsonMapper);
		return jsonResponseToClient;
		}

		else{LOG.info("Inside getTimeseriesGraph of Service class Else ");
		return jsonResponseToClient;
		}
	}	
}
