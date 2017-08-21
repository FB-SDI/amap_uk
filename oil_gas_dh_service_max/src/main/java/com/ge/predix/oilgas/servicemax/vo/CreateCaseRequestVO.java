package com.ge.predix.oilgas.servicemax.vo;

import java.util.List;

public class CreateCaseRequestVO {

	private List<CaseDetailsVO> records;

	public List<CaseDetailsVO> getRecords() {
		return records;
	}

	public void setRecords(List<CaseDetailsVO> records) {
		this.records = records;
	}

	@Override
	public String toString() {
		return "CreateCaseRequestVO [records=" + records + "]";
	}

}
