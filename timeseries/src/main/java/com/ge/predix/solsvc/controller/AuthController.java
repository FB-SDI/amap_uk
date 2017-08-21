/**
 * 
 */
package com.ge.predix.solsvc.controller;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.ge.predix.solsvc.model.AssetsRequest;
import com.ge.predix.solsvc.model.security.AccessStatusResponse;
import com.ge.predix.solsvc.model.security.AccessTokenResponse;
import com.ge.predix.solsvc.model.security.LoginRequest;
import com.ge.predix.solsvc.util.TimeSeriesConstants;



/**
 * @author ramalapoli
 *
 */

@RestController
public class AuthController {
	
	private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);
	
	@RequestMapping(value = "/login", method = RequestMethod.POST,produces=MediaType.APPLICATION_JSON_VALUE)
	public AccessTokenResponse validateUser(@RequestBody LoginRequest loginRequest)
	{
		AccessTokenResponse accessTokenResponse =null;
		LOG.info("Start validateUser" + loginRequest);
		try{
			String accessTokenUrl = "https://53b73921-acac-43d0-bef2-a8ec78d216ec.predix-uaa.run.aws-usw02-pr.ice.predix.io/oauth/token";
		
			accessTokenResponse = getAccessToken(getBasicAuthCode("test_client", "test_client"),accessTokenUrl,loginRequest);
			AccessStatusResponse accessStatusResponse = new AccessStatusResponse();
			accessStatusResponse.setMessage("success");
			accessStatusResponse.setValue("success");
			accessTokenResponse.setStatus(accessStatusResponse);
		}catch(Exception ex){
			accessTokenResponse = new AccessTokenResponse();
			AccessStatusResponse accessStatusResponse = new AccessStatusResponse();
			accessStatusResponse.setMessage("Your attempt to sign on has failed.  You may have entered an invalid UserID or Password.");
			accessStatusResponse.setValue("Your attempt to sign on has failed.  You may have entered an invalid UserID or Password.");
			accessTokenResponse.setStatus(accessStatusResponse);
		}
		
		LOG.info("End validateUser");
		return accessTokenResponse;
	}
	
	public  AccessTokenResponse getAccessToken(String basicCode,String accessTokenUrl,LoginRequest loginRequest){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Basic "+basicCode);
        LOG.info("headers::::::::{}",headers);
        
        LinkedMultiValueMap<String, String> bodyMap = new LinkedMultiValueMap<String, String>();

        bodyMap.add("grant_type", "password");
        //bodyMap.add("client_id", "l7xxe1e2c0d2bbe64882b238f450b4e4a73b");
        //bodyMap.add("client_secret", "667f9d2f33ff45dfaf17424a8e7d85d5");
        bodyMap.add("username", loginRequest.getUserDetails().getUsername());
        bodyMap.add("password", loginRequest.getUserDetails().getPassword());

        
        
        HttpEntity<Object> entity = new HttpEntity<Object>(bodyMap, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AccessTokenResponse> respEntity=restTemplate.exchange(accessTokenUrl, HttpMethod.POST, entity, AccessTokenResponse.class);
        LOG.info("respEntity::::::::{}",respEntity.getBody());
        return respEntity.getBody();

  }

  public  String getBasicAuthCode(String clientId, String clientSecret) {
        String plainCreds = clientId + ":"
                    + clientSecret;
        byte[] plainCredsBytes = plainCreds.getBytes(StandardCharsets.UTF_8);
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        return new String(base64CredsBytes, StandardCharsets.UTF_8);
  }


}
