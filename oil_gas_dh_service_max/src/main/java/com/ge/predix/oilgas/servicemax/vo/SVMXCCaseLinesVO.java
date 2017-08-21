package com.ge.predix.oilgas.servicemax.vo;

import java.util.List;

public class SVMXCCaseLinesVO {

	private List<RecordVO> records;

	public List<RecordVO> getRecords() {
		return records;
	}

	public void setRecords(List<RecordVO> records) {
		this.records = records;
	}

	@Override
	public String toString() {
		return "SVMXCCaseLinesVO [records=" + records + "]";
	}

}
