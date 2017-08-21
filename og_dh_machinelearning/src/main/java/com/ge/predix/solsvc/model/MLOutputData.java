package com.ge.predix.solsvc.model;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;
import com.ge.predix.solsvc.model.MLResultData;
import com.ge.predix.solsvc.model.PredictedData;
import com.ge.predix.solsvc.model.TestF1Score;
import com.ge.predix.solsvc.model.ThresholdF1Score;
import com.ge.predix.solsvc.model.TrainF1Score;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"analyticId",
"status",
"message",
"inputData",
"result",
"createdTimestamp",
"updatedTimestamp"
})
public class MLOutputData {

@JsonProperty("analyticId")
private String analyticId;
@JsonProperty("status")
private String status;
@JsonProperty("message")
private String message;
@JsonProperty("inputData")
private String inputData;
@JsonProperty("result")
private String result;
@JsonProperty("createdTimestamp")
private String createdTimestamp;
@JsonProperty("updatedTimestamp")
private String updatedTimestamp;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("analyticId")
public String getAnalyticId() {
return analyticId;
}

@JsonProperty("analyticId")
public void setAnalyticId(String analyticId) {
this.analyticId = analyticId;
}

@JsonProperty("status")
public String getStatus() {
return status;
}

@JsonProperty("status")
public void setStatus(String status) {
this.status = status;
}

@JsonProperty("message")
public String getMessage() {
return message;
}

@JsonProperty("message")
public void setMessage(String message) {
this.message = message;
}

@JsonProperty("inputData")
public String getInputData() {
return inputData;
}

@JsonProperty("inputData")
public void setInputData(String inputData) {
this.inputData = inputData;
}

@JsonProperty("result")
public String getResult() {
return result;
}

@JsonProperty("result")
public void setResult(String result) {
this.result = result;
}

@JsonProperty("createdTimestamp")
public String getCreatedTimestamp() {
return createdTimestamp;
}

@JsonProperty("createdTimestamp")
public void setCreatedTimestamp(String createdTimestamp) {
this.createdTimestamp = createdTimestamp;
}

@JsonProperty("updatedTimestamp")
public String getUpdatedTimestamp() {
return updatedTimestamp;
}

@JsonProperty("updatedTimestamp")
public void setUpdatedTimestamp(String updatedTimestamp) {
this.updatedTimestamp = updatedTimestamp;
}

@Override
public String toString() {
return ToStringBuilder.reflectionToString(this);
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

}