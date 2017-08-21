

package com.ge.predix.gas.dhassert.config;

import com.ge.predix.solsvc.websocket.config.IWebSocketConfig;

/**
 * 
 * @author 212438846
 */
public interface ITimeseriesConfig extends IWebSocketConfig {


	/**
	 * @return -
	 */
	public abstract String getQueryUrl();



}