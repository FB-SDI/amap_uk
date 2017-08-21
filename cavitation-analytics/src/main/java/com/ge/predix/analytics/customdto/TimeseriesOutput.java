/*
 * Copyright (c) 2015 - 2016 General Electric Company. All rights reserved.
 *
 * The copyright to the computer software herein is the property of
 * General Electric Company. The software may be used and/or copied only
 * with the written permission of General Electric Company or in accordance
 * with the terms and conditions stipulated in the agreement/contract
 * under which the software has been supplied.
 */

package com.ge.predix.analytics.customdto;

import java.util.List;

public class TimeseriesOutput {

	protected List<Double> cavitation;

	protected List<String> time_stamp;

	public List<Double> getCavitation() {
		return cavitation;
	}

	public void setCavitation(List<Double> cavitation) {
		this.cavitation = cavitation;
	}

	public List<String> getTime_stamp() {
		return time_stamp;
	}

	public void setTime_stamp(List<String> time_stamp) {
		this.time_stamp = time_stamp;
	}
}
