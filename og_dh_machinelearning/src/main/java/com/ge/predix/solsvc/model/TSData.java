
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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author vvenkateswaran
 *
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
"TSDATA"
})
public class TSData implements Serializable
{

@JsonProperty("TSDATA")
private List<List<Double>> tsDATA = null;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -2916362165802383953L;

@JsonProperty("TSDATA")
public List<List<Double>> getTSDATA() {
return tsDATA;
}

@JsonProperty("TSDATA")
public void setTSDATA(List<List<Double>> tsDATA) {
this.tsDATA = tsDATA;
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

@Override
public int hashCode() {
return new HashCodeBuilder().append(tsDATA).append(additionalProperties).toHashCode();
}

@Override
public boolean equals(Object other) {
if (other == this) {
return true;
}
if ((other instanceof TSData) == false) {
return false;
}
TSData rhs = ((TSData) other);
return new EqualsBuilder().append(tsDATA, rhs.tsDATA).append(additionalProperties, rhs.additionalProperties).isEquals();
}

}
