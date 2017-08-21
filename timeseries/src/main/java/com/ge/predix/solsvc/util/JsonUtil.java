/**
 * 
 */
package com.ge.predix.solsvc.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ge.predix.entity.timeseries.datapoints.queryresponse.DatapointsResponse;
import com.ge.predix.solsvc.ext.util.JsonMapper;
import com.ge.predix.solsvc.model.AssetData;
import com.ge.predix.solsvc.model.JsonData;
import com.ge.predix.solsvc.model.JsonResponse;
import com.ge.predix.solsvc.model.NotificationDataResponseVO;
import com.ge.predix.solsvc.model.NotificationDataVO;
import com.ge.predix.solsvc.model.NotificationResponse;
import com.ge.predix.solsvc.model.NotificationVO;
import com.ge.predix.solsvc.model.PerformanceMetrics;
import com.ge.predix.solsvc.model.PerformanceMetricsObject;
import com.ge.predix.solsvc.model.PumpTypeObject;
import com.ge.predix.solsvc.model.YData;
import com.ge.predix.solsvc.model.AssetGraphData;
import com.ge.predix.solsvc.model.DataPoints;
import com.ge.predix.solsvc.model.ResponseAssetGraphData;

/**
 * @author ramalapoli
 *
 */
public class JsonUtil {

	private static final Logger LOG = LoggerFactory.getLogger(JsonUtil.class);
	private static YData yData;

	public static String getJsonDataForGetTimeSeriesData(DatapointsResponse dpr, JsonMapper jsonMapper) {

		Set<String> xData = new LinkedHashSet<>();
		List<JsonData> JsonDataList = new ArrayList<JsonData>();

		for (int q = 0; q < dpr.getTags().size(); q++) {
			List<Integer> data = new ArrayList<Integer>();
			com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag tagValue = dpr.getTags().get(q);
			List<Object> dprValuesList = dpr.getTags().get(q).getResults().get(0).getValues();
			LOG.info("dprValuesList=" + dprValuesList);
			yData = new YData();
			int index = 0;

			for (Object objList : dprValuesList) {

				List<Object> obj = (List<Object>) objList;
				Date date = new Date((Long) obj.get(0));

				if (q == 0) {
					JsonData json = new JsonData();
					json.setX(date);
					//int object = (int) obj.get(1);
					
					try {
						int object = (int) obj.get(1);
						json.setDischargePressure(Integer.toString(object));
					}catch(ClassCastException cce) {
						Double object = (Double) obj.get(1);
						json.setDischargePressure(Double.toString(object));
					}
					
					JsonDataList.add(json);
				} else if (q == 1) {
					int object = (int) obj.get(1);
					JsonData json = null;
					try {
						json = JsonDataList.get(index);
						json.setEnergyConsumption(Integer.toString(object));
						JsonDataList.set(index, json);
					} catch (IndexOutOfBoundsException exception) {
						// json.setY1("0");
						// JsonDataList.set(index,json); //
						// handleTheExceptionSomehow(exception);
					}

				} else if (q == 2) {
					int object = (int) obj.get(1);
					JsonData json = null;
					try {
						json = JsonDataList.get(index);
						json.setIntakePressure(Integer.toString(object));
						JsonDataList.set(index, json);
					} catch (IndexOutOfBoundsException exception) {
						// JsonDataList.set(index,json); //
						// handleTheExceptionSomehow(exception);
					}
				} else {

				}
				SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(TimeSeriesConstants.DATE_FOMAT_GETDATA);
				String finalDate = DATE_FORMAT.format(date);
				// LOG.info("Today in dd MMM format : " + date);

				xData.add(finalDate);
				// LOG.info("xData=" + xData);
				int datapointVal = 0;
				datapointVal = (int) obj.get(1);

				data.add(datapointVal);
				// LOG.info("data=" + data);
				yData.setData(data);
				index++;

			}
			yData.setName(tagValue.getName());

		}

		JsonResponse jsonResponse = new JsonResponse();
		jsonResponse.setxData(xData);

		// jsonResponse.setJsonData();

		return jsonMapper.toJson(JsonDataList);

	}

	public static List<PerformanceMetricsObject> getDummyPerformanceMetrics() {
		List<PerformanceMetricsObject> performanceList = new ArrayList<PerformanceMetricsObject>();

		PerformanceMetricsObject per1 = new PerformanceMetricsObject();

		per1.setLabel("Performance Optimally");
		per1.setUnits(1733);
		per1.setPercentage(74);
		performanceList.add(per1);

		PerformanceMetricsObject per2 = new PerformanceMetricsObject();

		per2.setLabel("Failure Predicted");
		per2.setUnits(1733);
		per2.setPercentage(20);
		performanceList.add(per2);

		PerformanceMetricsObject per3 = new PerformanceMetricsObject();

		per3.setLabel("Failure");
		per3.setUnits(1733);
		per3.setPercentage(20);
		performanceList.add(per3);

		PerformanceMetricsObject per4 = new PerformanceMetricsObject();

		per4.setLabel("Undergoing Maintanence");
		per4.setUnits(1733);
		per4.setPercentage(20);
		performanceList.add(per4);

		PerformanceMetricsObject per5 = new PerformanceMetricsObject();

		per5.setLabel("No Data");
		per5.setUnits(1733);
		per5.setPercentage(20);
		performanceList.add(per5);

		return performanceList;

	}


	public static NotificationResponse getDataForNotification() {
	NotificationResponse resp = new NotificationResponse();
	NotificationDataResponseVO data = new NotificationDataResponseVO();
	List<NotificationDataVO> notificationDataList = new LinkedList<NotificationDataVO>();
	NotificationDataVO notificationData = null;
	List<NotificationVO> notificationList = null;
	NotificationVO item = null;

	// first value
	notificationData = new NotificationDataVO();
	notificationList = new LinkedList<NotificationVO>();
	item = new NotificationVO();
	item.setAssetId("SUZ-CUS-834");
	item.setMachineStatus("PREDICTING_FAILURE");
	item.setMessage("Failure predicted in pump SUZ-CUS-834");
	item.setStatusIcon("yellow");
	item.setMessageTime("03:00 AM");
	notificationList.add(item);

	item = new NotificationVO();
	item.setAssetId("SUZ-CUS2-834");
	item.setMachineStatus("FAILURE");
	item.setMessage("Pump SUZ-CUS2-834 has failed");
	item.setMessageTime("05:00 AM");
	item.setStatusIcon("red");
	notificationList.add(item);

	item = new NotificationVO();
	item.setAssetId("SUZ-CUS2-834");
	item.setMachineStatus("UNDERGOING_MAINTENANCE");
	item.setMessage("Maintenance has been started on Pump SUZ-CUS2-834");
	item.setMessageTime("10:00 PM");
	item.setStatusIcon("blue");
	notificationList.add(item);

	// notificationData.setDateStamp("16/01/2017");
	notificationData.setDateStamp("04/12/2017");
	notificationData.setNotificationList(notificationList);
	notificationDataList.add(notificationData);

	// second value
	notificationData = new NotificationDataVO();
	notificationList = new LinkedList<NotificationVO>();
	item = new NotificationVO();
	item.setAssetId("SUZ-CUS-834");
	item.setMachineStatus("PREDICTING_FAILURE");
	item.setMessage("Failure predicted in pump SUZ-CUS-834");
	item.setStatusIcon("yellow");
	item.setMessageTime("05:00 PM");
	notificationList.add(item);

	item = new NotificationVO();
	item.setAssetId("SUZ-CUS2-834");
	item.setMachineStatus("FAILURE");
	item.setMessage("Pump SUZ-CUS2-834 has failed");
	item.setStatusIcon("red");
	item.setMessageTime("08:00 PM");
	notificationList.add(item);

	item = new NotificationVO();
	item.setAssetId("SUZ-CUS2-834");
	item.setMachineStatus("UNDERGOING_MAINTENANCE");
	item.setMessage("Maintenance has been started on Pump SUZ-CUS2-834");
	item.setStatusIcon("blue");
	item.setMessageTime("11:20 PM");
	notificationList.add(item);

	// notificationData.setDateStamp("15/01/2017");
	notificationData.setDateStamp("04/10/2017");
	notificationData.setNotificationList(notificationList);
	notificationDataList.add(notificationData);

	data.setNotificationData(notificationDataList);
	resp.setData(data);
	return resp;
}

	public static String getJsonDataForAssetGraphs(DatapointsResponse dpr, JsonMapper jsonMapper) {

		Set<String> xData = new LinkedHashSet<>();
		List<JsonData> JsonDataList = new ArrayList<JsonData>();
		StringBuffer sb = new StringBuffer("{");
		ResponseAssetGraphData responeGraphObj = new ResponseAssetGraphData();
		int temp1=0;
		for (int q = 0; q < dpr.getTags().size(); q++) {
			List<Integer> data = new ArrayList<Integer>();
			com.ge.predix.entity.timeseries.datapoints.queryresponse.Tag tagValue = dpr.getTags().get(q);
			List<Object> dprValuesList = dpr.getTags().get(q).getResults().get(0).getValues();
			String tagName = tagValue.getName();
			if(temp1!=0)
			{
				sb.append(",");
			}
			else
			{
				temp1=1;
			}
			
			LOG.info("TagNAme=" + tagValue.getName());
			sb.append("\""+tagName+"\":["); 
			//assetGraphdataObj.setKey(tagName);
			/*if (q == 0)
				assetGraphdataObj.setColor("#fa845c");
			else if (q == 1)
				assetGraphdataObj.setColor("#f9cc59");
			else
				assetGraphdataObj.setColor("#9ee65f");*/
			int temp=0;
			for (Object objList : dprValuesList) {

				/*List<Object> obj = (List<Object>) objList;
				Long  dateInMillisec = (Long) obj.get(0);
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.setTimeInMillis(dateInMillisec);
				LOG.info("Calendar time:"+calendar.getTime().toString());
				String datetemp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss,SSS").format(calendar.getTime());
				LOG.info("datetemp:"+datetemp);
				int object = (int) obj.get(1);
				LOG.info("Data Points: " + datetemp + "  ::::: " + Integer.toString(object));
				assetGraphdataObj.setvaluestoList(new DataPoints(datetemp, Integer.toString(object)));*/
				
				//New format as per the UI
				if(temp!=0)
				{
					sb.append(",");
				}
				else
				{
					temp=1;
				}
				@SuppressWarnings("unchecked")
				List<Object> obj = (List<Object>) objList;
				/*Long date = (Long) obj.get(0);
				
				int object = (int) obj.get(1);
				sb.append("["+date+","+Integer.toString(object)+"]");*/
				sb.append("["+obj.get(0)+","+obj.get(1)+"]");
				
			}
			sb.append("]");
			//responeGraphObj.setOverallDiscahrgeGraphDatatolist(assetGraphdataObj);
		}
		sb.append("}");

		return sb.toString();

	}
	/**
	 * PIOT-118 - O&G : As an admin, I want to be able to select machine segments on the dashboard, 
	 * so that i can get an overview about them
	 * 
	 * Filter Options : Pump Type
	 */

	public static List<PumpTypeObject> getDummyAssetTypeData() {
		List<PumpTypeObject> pumpList = new ArrayList<PumpTypeObject>();

		PumpTypeObject pump1 = new PumpTypeObject();

		pump1.setLabel("Select Asset Type");
		pump1.setValue("");
		pumpList.add(pump1);
		
		PumpTypeObject pump3 = new PumpTypeObject();

		pump3.setLabel("Axial Flow");
		pump3.setValue("AXIAL_FLOW");
		pumpList.add(pump3);

		PumpTypeObject pump2 = new PumpTypeObject();

		pump2.setLabel("Barrel");
		pump2.setValue("BARREL");
		pumpList.add(pump2);

		return pumpList;

	}

}
