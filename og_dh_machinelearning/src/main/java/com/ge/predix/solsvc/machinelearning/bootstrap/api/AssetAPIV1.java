

package com.ge.predix.solsvc.machinelearning.bootstrap.api;

/**
 * Constants defining the Time Series URLs
 * @author predix -
 */
@SuppressWarnings("nls")
public final class AssetAPIV1 {
    /**
     * The main url for Querying training asset data
     */
    public static final String trainingURI = "/v1/training";
	/**
	 * The url for Querying Time Series for the last datapoint 
	 */
	public static final String latestdatapointsURI = "/v1/datapoints/latest";
	/**
	 * The url for getting a list of tags
	 */
	public static final String tagsURI = "/v1/tags";
	/**
	 * The url for doing aggregation queries (average over a sample interval) 
	 */
	public static final String aggregationsURI = "/v1/aggregations";

}
