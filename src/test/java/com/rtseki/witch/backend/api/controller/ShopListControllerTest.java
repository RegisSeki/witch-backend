package com.rtseki.witch.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.rtseki.witch.backend.api.dto.response.AuthenticationResponse;
import com.rtseki.witch.backend.api.dto.response.ShopListResponse;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ShopListControllerTest {
	
	@Value("${server.port}")
	private int serverPort;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private AuthenticationService authService;
	
	private HttpHeaders headers;
	
	@BeforeAll
	void init() throws JSONException {
		User user1 = new User();
		user1.setFirstname("Regis");
		user1.setLastname("Seki");
		user1.setEmail("regis@mail.com");
		user1.setPassword("12345678");
		AuthenticationResponse authenticationResponse = authService.register(user1);
		
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(authenticationResponse.getToken());
	}
	
	@Test
	@DisplayName("Create ShopList")
	@Order(1)
	void testCreateShopList_whenValidParams_thenReturnShopListDetails() throws JSONException {
		// Arrange
		JSONObject shopListDetailsRequestJson = new JSONObject();
		shopListDetailsRequestJson.put("name", "First shopping list");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				shopListDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<ShopListResponse> response = testRestTemplate.postForEntity("/api/v1/shop-lists",
				requestEntity,
				ShopListResponse.class);
		
		ShopListResponse createdShopList = response.getBody();

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(shopListDetailsRequestJson.getString("name"), createdShopList.getName(),
				"Returned shopList name seems to be incorrect");
		assertFalse(createdShopList.getId() == null, "ShopList id should not be null");
	}
	
	@Test
	@DisplayName("Do not create ShopList")
	@Order(2)
	void testCreateShopList_whenNameParamIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject shopListDetailsRequestJson = new JSONObject();

		HttpEntity<String> requestEntity = new HttpEntity<>(
				shopListDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/shop-lists",
				requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create ShopList")
	@Order(3)
	void testCreateShopList_whenNameParamIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject shopListDetailsRequestJson = new JSONObject();
		shopListDetailsRequestJson.put("name", " ");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				shopListDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/shop-lists",
				requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create ShopList")
	@Order(4)
	void testCreateShopList_whenNameParamAlreadyTaken_thenReturn400() throws JSONException {
		// Arrange
		JSONObject shopListDetailsRequestJson = new JSONObject();
		shopListDetailsRequestJson.put("name", "First shopping list");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				shopListDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/shop-lists",
				requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Shop List name is already taken"));
	}
	
	@Test
	@DisplayName("Create ShopList")
	@Order(5)
	void testCreateShopList_whenNameParamIsAlreadyTakenButDifferentUser_thenReturnShopListDetails() throws JSONException {
		// Arrange
		User user2 = new User();
		user2.setFirstname("Luana");
		user2.setLastname("Seki");
		user2.setEmail("luana@mail.com");
		user2.setPassword("12345678");
		AuthenticationResponse authenticationResponse = authService.register(user2);

		headers.setBearerAuth(authenticationResponse.getToken());
		
		JSONObject shopListDetailsRequestJson = new JSONObject();
		shopListDetailsRequestJson.put("name", "First shopping list");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				shopListDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<ShopListResponse> response = testRestTemplate.postForEntity("/api/v1/shop-lists",
				requestEntity,
				ShopListResponse.class);
		
		ShopListResponse createdShopList = response.getBody();

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(shopListDetailsRequestJson.getString("name"), createdShopList.getName(),
				"Returned shopList name seems to be incorrect");
		assertFalse(createdShopList.getId() == null, "ShopList id should not be null");
	}
}
