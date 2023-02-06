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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

import com.rtseki.witch.backend.api.dto.response.AuthenticationResponse;
import com.rtseki.witch.backend.api.dto.response.ProductResponse;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ProductControllerTest {
	@Value("${server.port}")
	private int serverPort;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AuthenticationService authService;
	
	@Autowired
	private SubcategoryRepository subcategoryRepository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	private HttpHeaders headers;
	private ProductResponse createdProduct;
	private Subcategory createdSubcategory;
	
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
		
		Category category = new Category(null, "Cat", "All the beloved pet needs");
		category = categoryRepository.save(category);
		
		Subcategory subcategory = new Subcategory(null, "Food", "To keep the little belly full", category);
		createdSubcategory = subcategoryRepository.save(subcategory);
	}
	
	@Test
	@DisplayName("Create Product")
	@Order(1)
	void testCreateProduct_whenValidParams_thenReturnProductDetails() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029017678");
		productDetailsRequestJson.put("name", "Sachê Sheba Gatos Filhotes Carne ao Molho 85g");
		productDetailsRequestJson.put("description", "Yuki preferred");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<ProductResponse> response = restTemplate.postForEntity("/api/v1/products",
				requestEntity,
				ProductResponse.class);
		
		createdProduct = response.getBody();
		
		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(productDetailsRequestJson.getString("barcode"), createdProduct.getBarcode(),
				"Returned product barcode seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("name"), createdProduct.getName(),
				"Returned product name seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("description"), createdProduct.getDescription(),
				"Returned product description seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("id"), createdProduct.getSubcategory().getId().toString(),
				"Returned product id seems to be incorrect");
		assertFalse(createdProduct.getId() == null, "Product id should not be null");
	}
	
	@Test
	@DisplayName("Do not create Product when subcategory parameter is missing")
	@Order(2)
	void testCreateProduct_whenMissingSubcategoryParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("barcode", "7896029028391");
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"subcategory\""));
	}
	
	@Test
	@DisplayName("Do not create Product when barcode parameter is missing")
	@Order(3)
	void testCreateProduct_whenMissingBarcodeParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"barcode\""));
	}
	
	@Test
	@DisplayName("Do not create Product when barcode parameter is blank")
	@Order(4)
	void testCreateProduct_whenBarcodeParameterBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "");
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"barcode\""));
	}
	
	@Test
	@DisplayName("Do not create Product when name parameter is missing")
	@Order(5)
	void testCreateProduct_whenMissingNameParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029028391");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create Product when name parameter is blank")
	@Order(6)
	void testCreateProduct_whenBlankNameParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029028391");
		productDetailsRequestJson.put("name", " ");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create Product when barcode is already registered")
	@Order(7)
	void testCreateProduct_whenBarcodeAlreadyRegistered_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029017678");
		productDetailsRequestJson.put("name", "Sachê Sheba Gatos Filhotes Carne ao Molho 85g");

		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Product barcode is already register"));
	}
	
	@Test
	@DisplayName("Do not create Product when name is already used")
	@Order(8)
	void testCreateProduct_whenNameIsUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029028391");
		productDetailsRequestJson.put("name", "Sachê Sheba Gatos Filhotes Carne ao Molho 85g");

		HttpEntity<String> requestEntity = new HttpEntity<>(productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/products", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Product name is already taken"));
	}
	
	@Test
	@DisplayName("Find product by id")
	@Order(9)
	void testFindProductById_whenGivenCorrectId_returnProperProduct() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<ProductResponse> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<ProductResponse>() {
				});
		ProductResponse productResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(), 
				"HTTP Status code should be 200");
		assertEquals(productResponse.getName(), createdProduct.getName(),
				"Returned product name seems to be incorrect");
		assertEquals(productResponse.getDescription(), createdProduct.getDescription(),
				"Returned product description seems to be incorrect");
		assertEquals(productResponse.getSubcategory().getId(), createdProduct.getSubcategory().getId(),
				"Returned subcategory id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not find product by id")
	@Order(10)
	void testFindProductById_whenGivenIncorrectId_return404() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/products/10000",
				HttpMethod.GET,
				requestEntity, 
				String.class);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
				"HTTP Status code should be 404");
        assertTrue(response.getBody().toString().contains(
        		"Resource not found. Id:"));
	}
}
