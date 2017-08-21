package com.ge.predix.solsvc.model;

import java.util.HashMap;
import java.util.List;
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
"CAV",
"GR",
"LLD",
"VIB",
"EFF",
"STATUS"
})
public class PredictedData {

@JsonProperty("CAV")
private List<Float> cAV = null;
@JsonProperty("GR")
private List<Float> gR = null;
@JsonProperty("LLD")
private List<Float> lLD = null;
@JsonProperty("VIB")
private List<Float> vIB = null;
@JsonProperty("EFF")
private List<Float> eFF = null;
@JsonProperty("STATUS")
private List<String> sTATUS = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();

@JsonProperty("CAV")
public List<Float> getCAV() {
return cAV;
}

@JsonProperty("CAV")
public void setCAV(List<Float> cAV) {
this.cAV = cAV;
}

@JsonProperty("GR")
public List<Float> getGR() {
return gR;
}

@JsonProperty("GR")
public void setGR(List<Float> gR) {
this.gR = gR;
}

@JsonProperty("LLD")
public List<Float> getLLD() {
return lLD;
}

@JsonProperty("LLD")
public void setLLD(List<Float> lLD) {
this.lLD = lLD;
}

@JsonProperty("VIB")
public List<Float> getVIB() {
return vIB;
}

@JsonProperty("VIB")
public void setVIB(List<Float> vIB) {
this.vIB = vIB;
}

@JsonProperty("EFF")
public List<Float> getEFF() {
return eFF;
}

@JsonProperty("EFF")
public void setEFF(List<Float> eFF) {
this.eFF = eFF;
}

@JsonProperty("STATUS")
public List<String> getSTATUS() {
return sTATUS;
}

@JsonProperty("STATUS")
public void setSTATUS(List<String> sTATUS) {
this.sTATUS = sTATUS;
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