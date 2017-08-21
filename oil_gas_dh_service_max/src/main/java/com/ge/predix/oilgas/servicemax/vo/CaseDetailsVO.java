package com.ge.predix.oilgas.servicemax.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseDetailsVO {

	@Override
	public String toString() {
		return "CaseDetailsVO [attributes=" + attributes + ", subject=" + subject + ", origin=" + origin + ", status="
				+ status + ", SVMXCCaseLinesVO=" + SVMXCCaseLinesVO + "]";
	}

	private AttributesVO attributes;
	private String subject;
	private String origin;
	private String status;
	private SVMXCCaseLinesVO SVMXCCaseLinesVO;

	@JsonProperty(value = "attributes")
	public AttributesVO getAttributes() {
		return attributes;
	}

	public void setAttributes(AttributesVO attributes) {
		this.attributes = attributes;
	}

	@JsonProperty(value = "Subject")
	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	@JsonProperty(value = "Origin")
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	@JsonProperty(value = "Status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@JsonProperty(value = "SVMXC__Case_Lines__r")
	public SVMXCCaseLinesVO getSVMXCCaseLinesVO() {
		return SVMXCCaseLinesVO;
	}

	public void setSVMXCCaseLinesVO(SVMXCCaseLinesVO sVMXCCaseLinesVO) {
		SVMXCCaseLinesVO = sVMXCCaseLinesVO;
	}

}
