package com.ge.predix.solsvc.util;

public interface TimeSeriesConstants {
	
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
	int TIME_INTERVAL_MIN_EXCEL= 120;
	
	String ASSET_TAG_NAME_INGEST = "tsIdentifier_ingest";
	String ASSET_TAG_NAME_DISCHARGE = "tsIdentifier_discharge";
	String ASSET_TAG_NAME_ENERGYCONSUMPTION = "tsIdentifier_energyConsumption";
	String ASSET_TAG_NAME_VIBRATION = "tsIdentifier_vibration";
	
	String TIMESERIES_ASSET_ATTR_URI = "uri";
	String TIMESERIES_ASSET_ATTR_SENSOR = "sensor";
	
	
	String TIMESERIES_MC_TAGNAME = "name";
	String TIMESERIES_MC_TIMESTAMP = "timestamp";
	String TIMESERIES_MC_VALUE = "value";
	String TIMESERIES_MC_QUALITY_NAME = "quality";
	String TIMESERIES_MC_QUALITY_GOOD = "GOOD";
	String TIMESERIES_MC_QUALITY_BAD = "BAD";
	
	String ASSET_DEFAULT_NAME= "machine";
	
	public final String TOGGLE_MACHINE = "machine";
	public final String TOGGLE_STORED = "stored";
	
	public final String TOGGLE_MACHINE_VALUE = "1";
	public final String TOGGLE_STORED_VALUE = "3";
	
	public final String DURATION_1D_AGO = "1d-ago";
	public final String DURATION_1W_AGO = "1w-ago";
	public final String DURATION_1M_AGO = "1m-ago";
	
	public final String INTAKE_TAG = "intake_tag";
	public final String DIGEST_TAG = "digest_tag";
	public final String HEAD_SUCTION_TAG = "head_suction_tag";
	public final String CAVITATION_PUMP9 = "Cavitation.pump9";
	public final String CAVITATION_PUMP11 = "Cavitation.pump11";
	
	public final String ANOMALY_TAG = "anomaly_tag";
	public final String NOTIFICATION_PUMP9 = "Notification.pump9";
	public final String NOTIFICATION_PUMP11 = "Notification.pump11";
	
}
