package com.ge.predix.oilgas.servicemax.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = Include.NON_NULL)
public class RecordVO {

	private AttributesVO attributes;
	private String SVMXCSerialNumberListc;
	private String SVMXCDescriptionc;
	private String errorCode;
	
	
	@JsonProperty(value = "Error_Code__c")
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	@JsonProperty(value = "attributes")
	public AttributesVO getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributesVO attributes) {
		this.attributes = attributes;
	}

	@JsonProperty(value = "SVMXC__Serial_Number_List__c")
	public String getSVMXCSerialNumberListc() {
		return SVMXCSerialNumberListc;
	}

	public void setSVMXCSerialNumberListc(String sVMXCSerialNumberListc) {
		SVMXCSerialNumberListc = sVMXCSerialNumberListc;
	}

	@JsonProperty(value = "SVMXC__Description__c")
	public String getSVMXCDescriptionc() {
		return SVMXCDescriptionc;
	}

	public void setSVMXCDescriptionc(String sVMXCDescriptionc) {
		SVMXCDescriptionc = sVMXCDescriptionc;
	}

	@Override
	public String toString() {
		return "RecordVO [attributes=" + attributes + ", SVMXCSerialNumberListc=" + SVMXCSerialNumberListc
				+ ", SVMXCDescriptionc=" + SVMXCDescriptionc + ", errorCode=" + errorCode + "]";
	}

}
