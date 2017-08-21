package com.ge.predix.oilgas.servicemax.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ge.predix.oilgas.servicemax.util.Constant;
import com.ge.predix.oilgas.servicemax.vo.AttributesVO;
import com.ge.predix.oilgas.servicemax.vo.AuthTokenResponseVO;
import com.ge.predix.oilgas.servicemax.vo.CaseDetailsVO;
import com.ge.predix.oilgas.servicemax.vo.CreateCaseRequestVO;
import com.ge.predix.oilgas.servicemax.vo.RecordVO;
import com.ge.predix.oilgas.servicemax.vo.SVMXCCaseLinesVO;
import com.ge.predix.solsvc.model.servicemax.AssetDetailsVO;
import com.ge.predix.solsvc.model.servicemax.SVMXCaseCreateRequestVO;
import com.ge.predix.solsvc.model.servicemax.SVMXCaseDetailsVO;

@Service
public class ServiceMaxService {

	private static final Logger log = LoggerFactory.getLogger(ServiceMaxService.class);

	RestTemplate restTemplate = new RestTemplate();

	@Value("${servicemax.auth.token.url}")
	private String authTokenUrl;

	@Value("${servicemax.grant.type}")
	private String grantType;
	@Value("${servicemax.client.id}")
	private String clientId;
	@Value("${servicemax.client.secret}")
	private String clientSecret;
	@Value("${servicemax.username}")
	private String userName;
	@Value("${servicemax.password}")
	private String password;
	@Value("${servicemax.create.case.url}")
	private String createCaseUrl;
	@Value("${servicemax.retrieve.case.serialNumber}")
	private String getCasesByserialNumberURL;

	private String getAuthToken() {

		log.info("Getting the oauth token from service max");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
		params.add(Constant.GRANT_TYPE, grantType);
		params.add(Constant.CLIENT_ID, clientId);
		params.add(Constant.CLIENT_SECRET, clientSecret);
		params.add(Constant.USERNAME, userName);
		params.add(Constant.PASSWORD, password);

		HttpHeaders headers = new HttpHeaders();
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params,
				headers);
		ResponseEntity<AuthTokenResponseVO> response = restTemplate.postForEntity(authTokenUrl, request,
				AuthTokenResponseVO.class);

		return response.getBody().getBearerToken();

	}

	public Object createCase(SVMXCaseCreateRequestVO requestVO) {

		log.info("Started creating the case in Service Max");

		int count =2;
		CaseDetailsVO caseDetails = new CaseDetailsVO();

		AttributesVO att = new AttributesVO();
		att.setReferenceId("ref1");
		att.setType(Constant.CASE);

		caseDetails.setAttributes(att);
		//caseDetails.setSubject(requestVO.getSubject()); 
		caseDetails.setOrigin(Constant.PREDIX);
		caseDetails.setStatus(Constant.NEW);

		List<RecordVO> records = new ArrayList<>();
		for(AssetDetailsVO caseDetail : requestVO.getCaseDetails()){
			
			AttributesVO caseAttr = new AttributesVO();
			caseAttr.setType(Constant.SVMXC__CASE_LINE__C);
			caseAttr.setReferenceId("ref"+count);
			
			RecordVO rec = new RecordVO();
			rec.setAttributes(caseAttr);
			rec.setSVMXCSerialNumberListc(caseDetail.getSerialNumber());
			//rec.setSVMXCDescriptionc(caseDetail.getDescription());  //TODO: Commented as per the latest spec Dt: May-8-2017
			rec.setErrorCode(caseDetail.getErrorCode());  //Added on May-8-2017
			records.add(rec);
			count++;
		}

		SVMXCCaseLinesVO svmx = new SVMXCCaseLinesVO();
		svmx.setRecords(records);

		caseDetails.setSVMXCCaseLinesVO(svmx);

		CreateCaseRequestVO cc = new CreateCaseRequestVO();
		cc.setRecords(new ArrayList<CaseDetailsVO>(Arrays.asList(caseDetails)));

		HttpEntity<Object> entity = new HttpEntity<Object>(cc, getHttpHeader());
		log.info("Posting data to service max and req Object is  :=== > " + cc);
		ResponseEntity<Object> response = restTemplate.exchange(createCaseUrl, HttpMethod.POST, entity, Object.class);

		log.info("Posting data to service max completed");
		log.info("Response from service max {}", response.getBody());

		return response.getBody();

	}

	private HttpHeaders getHttpHeader() {

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.set("Authorization", Constant.OAUTH + " " + getAuthToken());

		return header;
	}
	
	public Object getCasesBySerialNumber(String serialNumber) {
		
		
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.set("Accept",MediaType.APPLICATION_JSON_VALUE); 
		header.set("Authorization", Constant.OAUTH + " " + getAuthToken());
		HttpEntity<Object> entity = new HttpEntity<Object>(header);
		String query = "select+Id,+subject,+caseNumber,+origin,+(select+Predix_Serial_Number__c,+SVMXC__Description__c+from+SVMXC__Case_Lines__r)+from+case+where+origin='Predix'+and+Id+in+(+select+SVMXC__Case__c+from+SVMXC__Case_Line__c+where+Predix_Serial_Number__c='###')";
		query = query.replace("###", serialNumber);
		Map<String, String> uriParams = new HashMap<String, String>();
		uriParams.put("q", query);
		
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(getCasesByserialNumberURL);
		
		log.info("Retrieving case details from service max");
		ResponseEntity<SVMXCaseDetailsVO> response = restTemplate.exchange(builder.buildAndExpand(uriParams).toUri(), HttpMethod.GET, entity, SVMXCaseDetailsVO.class);
		log.info("Respnse received from service max: {}", response.getBody());
		return response.getBody();
	}

}
