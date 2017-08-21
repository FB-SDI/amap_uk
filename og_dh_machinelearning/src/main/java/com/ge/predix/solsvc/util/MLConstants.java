package com.ge.predix.solsvc.util;

public interface MLConstants {
	
	String SULZER = "sulzer";
	String DATA_POINTS_START_1YEAR="1y-ago";
	String DATA_POINTS_START_1MONTH="1mm-ago";
	String DATE_FOMAT_GETDATA="dd MMM";
	String CRITICAL="CRITICAL";
	String WARNING="WARNING";
	String NONE="NONE";
	String HEADER_AUTHORIZATION = "Authorization";
	String HEADER_PREDIX_ZONEID = "Predix-Zone-Id";
	String TAG_NAME_SUCTION = "Analytics.head.suction.";
	String TAG_NAME_NOTIFICATION = "Notification.";
	
	String TIMESERIES_DATA_EXCEL_NAME ="TimeSeriesDataIngestion.xlsx";
	int ONE_MINUTE_IN_SECONDS=60;
	int TIME_INTERVAL_MIN_EXCEL= 30;
	
	String ASSET_TAG_NAME_INGEST = "tsIdentifier_ingest";
	String ASSET_TAG_NAME_DISCHARGE = "tsIdentifier_discharge";
	String ASSET_TAG_NAME_ENERGYCONSUMPTION = "tsIdentifier_energyConsumption";
	String ASSET_TAG_NAME_VIBRATION = "tsIdentifier_vibration";
	String ASSET_TAG_NAME_DAYSTOFAILURE = "tsIdentifier_daysToFailure";
	
	String TIMESERIES_ASSET_ATTR_URI = "uri";
	String TIMESERIES_ASSET_ATTR_SENSOR = "sensor";
	
	
	String TIMESERIES_MC_TAGNAME = "name";
	String TIMESERIES_MC_TIMESTAMP = "timestamp";
	String TIMESERIES_MC_VALUE = "value";
	String TIMESERIES_MC_QUALITY_NAME = "quality";
	String TIMESERIES_MC_QUALITY_GOOD = "GOOD";
	String TIMESERIES_MC_QUALITY_BAD = "BAD";
	
	String ASSET_DEFAULT_NAME= "training";
}
