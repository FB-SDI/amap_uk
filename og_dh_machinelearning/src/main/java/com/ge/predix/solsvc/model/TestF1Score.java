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

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"micro_f1_score",
"macro_f1_score",
"weighted_f1_score"
})
public class TestF1Score {

@JsonProperty("micro_f1_score")
private String microF1Score;
@JsonProperty("macro_f1_score")
private String macroF1Score;
@JsonProperty("weighted_f1_score")
private String weightedF1Score;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("micro_f1_score")
public String getMicroF1Score() {
return microF1Score;
}

@JsonProperty("micro_f1_score")
public void setMicroF1Score(String microF1Score) {
this.microF1Score = microF1Score;
}

@JsonProperty("macro_f1_score")
public String getMacroF1Score() {
return macroF1Score;
}

@JsonProperty("macro_f1_score")
public void setMacroF1Score(String macroF1Score) {
this.macroF1Score = macroF1Score;
}

@JsonProperty("weighted_f1_score")
public String getWeightedF1Score() {
return weightedF1Score;
}

@JsonProperty("weighted_f1_score")
public void setWeightedF1Score(String weightedF1Score) {
this.weightedF1Score = weightedF1Score;
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