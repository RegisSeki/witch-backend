package com.rtseki.witch.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.rtseki.witch.backend.api.dto.response.AuthenticationResponse;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponse;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponseList;
import com.rtseki.witch.backend.domain.model.BusinessEstablishment;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.BusinessEstablishmentRepository;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BusinessEstablishmentControllerTest {
	@Value("${server.port}")
	private int serverPort;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AuthenticationService authService;
	
	@Autowired
	private BusinessEstablishmentRepository repository;
	
	private HttpHeaders headers;
	private BusinessEstablishmentResponse createdSubject;
	
	@BeforeAll
	void init() throws JSONException {
		User user = new User();
		user.setFirstname("Yuki");
		user.setLastname("Seki");
		user.setEmail("yuki@mail.com");
		user.setPassword("12345678");
		AuthenticationResponse authenticationResponse = authService.register(user);
		
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(authenticationResponse.getToken());
	}
	
	@Test
	@DisplayName("Create Business Establishment")
	@Order(1)
	void testCreateBusinessEstablishment_whenValidParams_thenReturnBusinessEstablishmentDetails() throws JSONException {
		// Arrange
		JSONObject subjectDetailsRequestJson = new JSONObject();
		subjectDetailsRequestJson.put("comercialName", "Shopping Jardim Oriente");
		subjectDetailsRequestJson.put("officialName", "Associacao Shopping Jardim Oriente");
		subjectDetailsRequestJson.put("officialRecord", "29.877.151/0001-04");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subjectDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<BusinessEstablishmentResponse> response = restTemplate.postForEntity("/api/v1/business-establishments",
				requestEntity,
				BusinessEstablishmentResponse.class);
		
		createdSubject = response.getBody();

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(subjectDetailsRequestJson.getString("comercialName"), createdSubject.getComercialName(),
				"Returned comercial name seems to be incorrect");
		assertEquals(subjectDetailsRequestJson.getString("officialName"), createdSubject.getOfficialName(),
				"Returned official name seems to be incorrect");
		assertEquals(subjectDetailsRequestJson.getString("officialRecord"), createdSubject.getOfficialRecord(),
				"Returned official record seems to be incorrect");
		assertFalse(createdSubject.getId() == null, "BusinessEstablishment id should not be null");
	}
	
	@Test
	@DisplayName("Do not create Business Establishment")
	@Order(2)
	void testCreateBusinessEstablishment_whenParamIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subjectDetailsRequestJson = new JSONObject();

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subjectDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/business-establishments",
				requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"comercialName\""));;
	}
	
	@Test
	@DisplayName("Do not create Business Establishment")
	@Order(3)
	void testCreateBusinessEstablishment_whenParamComercialNameIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subjectDetailsRequestJson = new JSONObject();
		subjectDetailsRequestJson.put("comercialName", "");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subjectDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/business-establishments",
				requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"comercialName\""));;
	}
	
	@Test
	@DisplayName("Do not create Business Establishment")
	@Order(4)
	void testCreateBusinessEstablishment_whenComercialNameAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subjectDetailsRequestJson = new JSONObject();
		subjectDetailsRequestJson.put("comercialName", "Shopping Jardim Oriente");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subjectDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity("/api/v1/business-establishments",
				requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Business Establishment company name is already taken"));
	}
	
	@Test
	@DisplayName("Find business establishment by id")
	@Order(5)
	void testFindBusinessEstablishmentById_whenGivenCorrectId_returnProperBusinessEstablishment() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<BusinessEstablishmentResponse> response = restTemplate.exchange(
				"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<BusinessEstablishmentResponse>() {
				});
		
		BusinessEstablishmentResponse subjectResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(), 
				"HTTP Status code should be 200");
		assertEquals(subjectResponse.getComercialName(), createdSubject.getComercialName(),
				"Returned business establishment comercial name seems to be incorrect");
		assertEquals(subjectResponse.getOfficialName(), createdSubject.getOfficialName(),
				"Returned business establishment official name seems to be incorrect");
		assertEquals(subjectResponse.getOfficialRecord(), createdSubject.getOfficialRecord(),
				"Returned business establishment official record seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not find business establishment by id")
	@Order(6)
	void testFindBusinessEstablishmentById_whenGivenIncorrectId_thenReturn404() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/business-establishments/10000", HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<String>() {
				});

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
				"HTTP Status code should be 404");
        assertTrue(response.getBody().toString().contains(
        		"Resource not found. Id:"));
	}
	
	@Test
	@DisplayName("Update business establishment not required param - official name")
	@Order(7)
	void testUpdateBusinessEstablishment_whenNoRequiredParamChange_thenReturnUpdatedBusinessEstablishment() throws JSONException {
		// Arrange
		JSONObject updateSubjectJson = new JSONObject();
		updateSubjectJson.put("comercialName", "Shopping Jardim Oriente");
		updateSubjectJson.put("officialName", "Associacao Shopping Jardim Oriente - SJC");

		HttpEntity<String> requestEntity = new HttpEntity<>(updateSubjectJson.toString(), headers);

		// Act
		ResponseEntity<BusinessEstablishmentResponse> response = restTemplate.exchange(
				"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<BusinessEstablishmentResponse>() {
				});
		BusinessEstablishmentResponse updatedSubjectResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 200");
		assertEquals(updateSubjectJson.getString("comercialName"), updatedSubjectResponse.getComercialName(),
				"Returned comercial name seems to be incorrect");
		assertEquals(updateSubjectJson.getString("officialName"), updatedSubjectResponse.getOfficialName(),
				"Returned official name seems to be incorrect");
		assertEquals(createdSubject.getId(), updatedSubjectResponse.getId(),
				"Returned business establishment id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Update business establishment required param - comercial name")
	@Order(8)
	void testUpdateBusinessEstablishment_whenRequiredParamChange_thenReturnUpdatedBusinessEstablishment() throws JSONException {
		// Arrange
		JSONObject updateSubjectJson = new JSONObject();
		updateSubjectJson.put("comercialName", "Shibata Jardim Oriente");
		updateSubjectJson.put("officialName", "Associacao Shopping Jardim Oriente - SJC");

		HttpEntity<String> requestEntity = new HttpEntity<>(updateSubjectJson.toString(), headers);

		// Act
		ResponseEntity<BusinessEstablishmentResponse> response = restTemplate.exchange(
				"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<BusinessEstablishmentResponse>() {
				});
		BusinessEstablishmentResponse updatedSubjectResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 200");
		assertEquals(updateSubjectJson.getString("comercialName"), updatedSubjectResponse.getComercialName(),
				"Returned comercial name seems to be incorrect");
		assertEquals(updateSubjectJson.getString("officialName"), updatedSubjectResponse.getOfficialName(),
				"Returned official name seems to be incorrect");
		assertEquals(createdSubject.getId(), updatedSubjectResponse.getId(),
				"Returned business establishment id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not update business establishment when was already used")
	@Order(9)
	void testUpdateBusinessEstablishment_whenCompanyNameWasAlreadyUsed_thenReturn400() throws JSONException {
		BusinessEstablishment nameTakerSubject = new BusinessEstablishment(null, "Carrefour Dutra", null, null);
		try {
			// Arrange
			nameTakerSubject = repository.save(nameTakerSubject);
			JSONObject updateSubjectJson = new JSONObject();
			updateSubjectJson.put("comercialName", nameTakerSubject.getComercialName());
	
			HttpEntity<String> requestEntity = new HttpEntity<>(updateSubjectJson.toString(), headers);
	
			// Act
			ResponseEntity<String> response = restTemplate.exchange(
					"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.PUT, requestEntity,
					new ParameterizedTypeReference<String>() {
					});
	
			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
					"HTTP Status code should be 400");
	        assertTrue(response.getBody().toString().contains(
	        		"Business Establishment company name is already taken"));
		}finally {
			repository.delete(nameTakerSubject);
		}
	}
	
	@Test
	@DisplayName("Delete business establishment")
	@Order(10)
	void testDeleteBusinessEstablishment_whenProvidedCorrectId_thenReturn204() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<Void> response = restTemplate.exchange(
				"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.DELETE, requestEntity,
				Void.class);
		List<BusinessEstablishment> subjects = repository.findAll();		
		
		// Assert
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
				"HTTP Status code should be 204");
		assertEquals(subjects.size(), 0,
				"It should not have any business establishment");
	}
	
	@Test
	@DisplayName("Do not delete business establishment")
	@Order(11)
	void testDeleteBusinessEstablishment_whenProvidedIncorrectId_thenReturn404() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/business-establishments/" + createdSubject.getId(), HttpMethod.DELETE, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()
				,"HTTP Status code should be 404");
        assertTrue(response.getBody().toString().contains(
        		"Resource not found. Id:"));
	}
	
	@Test
	@DisplayName("Find all business establishments")
	@Order(12)
	void testFindAllBusinessEstablishments_whenDefaultPagination_thenReturnProperBusinessEstablishments() {
		// Arrange
		int totalSubjects = 10;
		int defaultPageSize = 5;
		
		for(int i = 0; i < totalSubjects; i ++) {
			BusinessEstablishment subject = new BusinessEstablishment();
			subject.setComercialName("ComercialName" + i);
			repository.save(subject);
		}
		
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<BusinessEstablishmentResponseList> response = restTemplate.exchange(
				"/api/v1/business-establishments", HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<BusinessEstablishmentResponseList>() {
				});
		BusinessEstablishmentResponseList subjectResponseList = response.getBody();
		
		//Assert
		assertEquals(totalSubjects, subjectResponseList.getPageDetails().getTotalElements(),
				"It should be the same number of elements");
		assertEquals(defaultPageSize, subjectResponseList.getPageDetails().getPageSize(),
				"The number of the items that should be showed");
		assertEquals((totalSubjects / defaultPageSize), subjectResponseList.getPageDetails().getTotalPages());
		assertEquals(defaultPageSize, subjectResponseList.getEstablishments().size(),
				"The number of the items that should be showed");
	}
}
