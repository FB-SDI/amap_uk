package com.ge.predix.oilgas.servicemax.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AttributesVO {

	private String type;
	private String referenceId;

	@JsonProperty(value = "type")
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty(value = "referenceId")
	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public String toString() {
		return "AttributesVO [type=" + type + ", referenceId=" + referenceId + "]";
	}

}
