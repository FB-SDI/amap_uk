package com.ge.predix.solsvc.model;

import java.io.Serializable;
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

/**
 * @author vvenkateswaran
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"EFF",
"CAV",
"VIB",
"GR",
"LLD"
})
public class DataPredict implements Serializable
{

@JsonProperty("EFF")
private List<Integer> eFF = null;
@JsonProperty("CAV")
private List<Integer> cAV = null;
@JsonProperty("VIB")
private List<Integer> vIB = null;
@JsonProperty("GR")
private List<Integer> gR = null;
@JsonProperty("LLD")
private List<Integer> lLD = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = 2201047249332554510L;

@JsonProperty("EFF")
public List<Integer> getEFF() {
return eFF;
}

@JsonProperty("EFF")
public void setEFF(List<Integer> eFF) {
this.eFF = eFF;
}

public DataPredict withEFF(List<Integer> eFF) {
this.eFF = eFF;
return this;
}

@JsonProperty("CAV")
public List<Integer> getCAV() {
return cAV;
}

@JsonProperty("CAV")
public void setCAV(List<Integer> cAV) {
this.cAV = cAV;
}

public DataPredict withCAV(List<Integer> cAV) {
this.cAV = cAV;
return this;
}

@JsonProperty("VIB")
public List<Integer> getVIB() {
return vIB;
}

@JsonProperty("VIB")
public void setVIB(List<Integer> vIB) {
this.vIB = vIB;
}

public DataPredict withVIB(List<Integer> vIB) {
this.vIB = vIB;
return this;
}

@JsonProperty("GR")
public List<Integer> getGR() {
return gR;
}

@JsonProperty("GR")
public void setGR(List<Integer> gR) {
this.gR = gR;
}

public DataPredict withGR(List<Integer> gR) {
this.gR = gR;
return this;
}

@JsonProperty("LLD")
public List<Integer> getLLD() {
return lLD;
}

@JsonProperty("LLD")
public void setLLD(List<Integer> lLD) {
this.lLD = lLD;
}

public DataPredict withLLD(List<Integer> lLD) {
this.lLD = lLD;
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

public DataPredict withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}