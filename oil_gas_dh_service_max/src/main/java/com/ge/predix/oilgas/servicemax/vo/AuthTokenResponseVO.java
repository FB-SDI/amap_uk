package com.ge.predix.oilgas.servicemax.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AuthTokenResponseVO {

	private String bearerToken;

	@JsonProperty(value = "access_token")
	public String getBearerToken() {
		return bearerToken;
	}

	public void setBearerToken(String bearerToken) {
		this.bearerToken = bearerToken;
	}

}
