package com.ge.predix.solsvc.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author vvenkateswaran
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"data_learn",
"data_predict",
"label",
"score_threshold_train",
"score_threshold_test",
"hyper_parameter_tune"
})
public class MLInputData implements Serializable
{

@JsonProperty("data_learn")
private DataLearn dataLearn;
@JsonProperty("data_predict")
private DataPredict dataPredict;
@JsonProperty("label")
private String label;
@JsonProperty("score_threshold_train")
private double scoreThresholdTrain;
@JsonProperty("score_threshold_test")
private double scoreThresholdTest;
@JsonProperty("hyper_parameter_tune")
private String hyperParameterTune;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = 2700830808572340493L;

@JsonProperty("data_learn")
public DataLearn getDataLearn() {
return dataLearn;
}

@JsonProperty("data_learn")
public void setDataLearn(DataLearn dataLearn) {
this.dataLearn = dataLearn;
}

public MLInputData withDataLearn(DataLearn dataLearn) {
this.dataLearn = dataLearn;
return this;
}

@JsonProperty("data_predict")
public DataPredict getDataPredict() {
return dataPredict;
}

@JsonProperty("data_predict")
public void setDataPredict(DataPredict dataPredict) {
this.dataPredict = dataPredict;
}

public MLInputData withDataPredict(DataPredict dataPredict) {
this.dataPredict = dataPredict;
return this;
}

@JsonProperty("label")
public String getLabel() {
return label;
}

@JsonProperty("label")
public void setLabel(String label) {
this.label = label;
}

public MLInputData withLabel(String label) {
this.label = label;
return this;
}

@JsonProperty("score_threshold_train")
public double getScoreThresholdTrain() {
return scoreThresholdTrain;
}

@JsonProperty("score_threshold_train")
public void setScoreThresholdTrain(double scoreThresholdTrain) {
this.scoreThresholdTrain = scoreThresholdTrain;
}

public MLInputData withScoreThresholdTrain(double scoreThresholdTrain) {
this.scoreThresholdTrain = scoreThresholdTrain;
return this;
}

@JsonProperty("score_threshold_test")
public double getScoreThresholdTest() {
return scoreThresholdTest;
}

@JsonProperty("score_threshold_test")
public void setScoreThresholdTest(double scoreThresholdTest) {
this.scoreThresholdTest = scoreThresholdTest;
}

public MLInputData withScoreThresholdTest(double scoreThresholdTest) {
this.scoreThresholdTest = scoreThresholdTest;
return this;
}

@JsonProperty("hyper_parameter_tune")
public String getHyperParameterTune() {
return hyperParameterTune;
}

@JsonProperty("hyper_parameter_tune")
public void setHyperParameterTune(String hyperParameterTune) {
this.hyperParameterTune = hyperParameterTune;
}

public MLInputData withHyperParameterTune(String hyperParameterTune) {
this.hyperParameterTune = hyperParameterTune;
return this;
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

public MLInputData withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}