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
import org.junit.jupiter.api.Nested;
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
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponse;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponseList;
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
public class SubcategoryControllerTest {
	@Value("${server.port}")
	private int serverPort;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private AuthenticationService authService;
	
	@Autowired
	private SubcategoryRepository repository;

	@Autowired
	private CategoryRepository categoryRepository;
	
	private HttpHeaders headers;
	private SubcategoryResponse createdSubcategory;
	private Category createdCategory;

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
		
		Category category = new Category(1L, "Food", "Things to eat");
		createdCategory = categoryRepository.save(category);
	}
	
	@Test
	@DisplayName("Create Subcategory")
	@Order(1)
	void testCreateSubcategory_whenValidParams_thenReturnSubcategoryDetails() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "Cereal");
		subcategoryDetailsRequestJson.put("description", "To keep me happy");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<SubcategoryResponse> response = restTemplate.postForEntity("/api/v1/subcategories",
				requestEntity,
				SubcategoryResponse.class);
		
		createdSubcategory = response.getBody();
		
		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(subcategoryDetailsRequestJson.getString("name"), createdSubcategory.getName(),
				"Returned subcategory name seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("description"), createdSubcategory.getDescription(),
				"Returned subcategory description seems to be incorrect");
		assertEquals(categoryDetailsRequestJson.getString("id"), createdSubcategory.getCategory().getId().toString(),
				"Returned category id seems to be incorrect");
		assertFalse(createdSubcategory.getId() == null, "Subcategory id should not be null");
	}
	
	@Test
	@DisplayName("Do not create Subcategory when category parameter is missing")
	@Order(2)
	void testCreateSubcategory_whenMissingCategoryParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("name", "Bread");

		HttpEntity<String> requestEntity = new HttpEntity<>(subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/subcategories", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"category\""));
	}
	
	@Test
	@DisplayName("Do not create Subcategory when name parameter is missing")
	@Order(3)
	void testCreateSubcategory_whenMissingNameParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);

		HttpEntity<String> requestEntity = new HttpEntity<>(subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/subcategories", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create Subcategory when name parameter is blank")
	@Order(4)
	void testCreateSubcategory_whenNameParameterIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "");

		HttpEntity<String> requestEntity = new HttpEntity<>(subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/subcategories", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create Subcategory when name is already used")
	@Order(5)
	void testCreateSubcategory_whenSubcategoryNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "Cereal");

		HttpEntity<String> requestEntity = new HttpEntity<>(subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.postForEntity(
				"/api/v1/subcategories", requestEntity, String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Subcategory name is already taken"));
	}

	@Test
	@DisplayName("Find subcategory by id")
	@Order(6)
	void testFindSubcategoryById_whenGivenCorrectId_returnProperSubcategory() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<SubcategoryResponse> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<SubcategoryResponse>() {
				});
		SubcategoryResponse subcategoryResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(), 
				"HTTP Status code should be 200");
		assertEquals(subcategoryResponse.getName(), createdSubcategory.getName(),
				"Returned subcategory name seems to be incorrect");
		assertEquals(subcategoryResponse.getDescription(), createdSubcategory.getDescription(),
				"Returned subcategory description seems to be incorrect");
		assertEquals(subcategoryResponse.getCategory().getId(), createdSubcategory.getCategory().getId(),
				"Returned category id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not find subcategory by id")
	@Order(7)
	void testFindSubCategoryById_whenGivenIncorrectId_return404() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange("/api/v1/subcategories/10000",
				HttpMethod.GET,
				requestEntity, 
				String.class);

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
				"HTTP Status code should be 404");
        assertTrue(response.getBody().toString().contains(
        		"Resource not found. Id:"));
	}
	
	@Test
	@DisplayName("Update Subcategory name")
	@Order(8)
	void testUpdateSubcategory_whenValidParams_thenReturnSubcategoryDetails() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "Meat");
		subcategoryDetailsRequestJson.put("description", "Love barbecue");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<SubcategoryResponse> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<SubcategoryResponse>() {
				});
		
		SubcategoryResponse updatedSubcategory = response.getBody();
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(subcategoryDetailsRequestJson.getString("name"), updatedSubcategory.getName(),
				"Returned subcategory name seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("description"), updatedSubcategory.getDescription(),
				"Returned subcategory description seems to be incorrect");
		assertEquals(categoryDetailsRequestJson.getString("id"), updatedSubcategory.getCategory().getId().toString(),
				"Returned category id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Update Subcategory description keeping the same name")
	@Order(9)
	void testUpdateSubcategory_whenChangeOnlyDescription_thenReturnSubcategoryDetails() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "Meat");
		subcategoryDetailsRequestJson.put("description", "Love barbecue so much");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<SubcategoryResponse> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<SubcategoryResponse>() {
				});
		
		SubcategoryResponse updatedSubcategory = response.getBody();
		
		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(subcategoryDetailsRequestJson.getString("name"), updatedSubcategory.getName(),
				"Returned subcategory name seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("description"), updatedSubcategory.getDescription(),
				"Returned subcategory description seems to be incorrect");
		assertEquals(categoryDetailsRequestJson.getString("id"), updatedSubcategory.getCategory().getId().toString(),
				"Returned category id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not update Subcategory when name is repeated")
	@Order(10)
	void testUpdateSubcategory_whenNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		Subcategory subject = new Subcategory(null, "Healthy", null, createdCategory);
		repository.save(subject);
		
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", createdCategory.getId());
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", "Healthy");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);
		try {
			// Act
			ResponseEntity<String> response = restTemplate.exchange(
					"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
					String.class);
			
			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
					"HTTP Status code should be 400");
	        assertTrue(response.getBody().toString().contains(
	        		"Subcategory name is already taken"));
		} finally {
			repository.delete(subject);
		}
	}
	
	@Test
	@DisplayName("Do not update Subcategory when category param is missing")
	@Order(11)
	void testUpdateSubcategory_whenCategoryIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("name", "Dry Grains");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"category\""));
	}
	
	@Test
	@DisplayName("Do not update Subcategory when name is missing")
	@Order(12)
	void testUpdateSubcategory_whenNameIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not update Subcategory when name is blank")
	@Order(13)
	void testUpdateSubcategory_whenNameIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("id", "1");
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("category", categoryDetailsRequestJson);
		subcategoryDetailsRequestJson.put("name", " ");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				subcategoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Delete Subcategory")
	@Order(14)
	void testDeleteSubcategory_whenProvidedCorrectId_thenReturn204() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<Void> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.DELETE, requestEntity,
				Void.class);
		List<Subcategory> subcategories = repository.findAll();		
		
		// Assert
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
				"HTTP Status code should be 204");
		assertEquals(subcategories.size(), 0,
				"It should not have any category");
	}
	
	@Test
	@DisplayName("Do not delete category")
	@Order(15)
	void testDeleteSubcategory_whenProvidedInexistentId_thenReturn404() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/subcategories/" + createdSubcategory.getId(), HttpMethod.DELETE, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()
				,"HTTP Status code should be 404");
        assertTrue(response.getBody().toString().contains(
        		"Resource not found. Id:"));
	}
	
	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
	class SubcategoryControllerTestSubcategoryList{
		
		private final int totalSubcategories = 20;
		private final int defaultPageSize = 5;
		
		@BeforeAll
		void init() {
			for(int i = 0; i < totalSubcategories; i ++) {
				Subcategory subcategory = new Subcategory();
				Category category = new Category();
				category.setId(createdCategory.getId());
				subcategory.setCategory(category);
				subcategory.setName("Subcategory" + i);
				subcategory.setDescription("Subcategory description" + i);
				repository.save(subcategory);
			}
		}
		
		@Test
		@DisplayName("Find all subcategories with default pagination")
		@Order(16)
		void testFindAllSubcategories_whenDefaultPagination_thenReturnProperSubcategories() {
			// Arrange
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<SubcategoryResponseList> response = restTemplate.exchange(
					"/api/v1/subcategories", HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<SubcategoryResponseList>() {
					});
			SubcategoryResponseList subcategoryResponseList = response.getBody();
			
			//Assert
			assertEquals(totalSubcategories, subcategoryResponseList.getTotalElements(),
					"It should be the same number of elements");
			assertEquals(defaultPageSize, subcategoryResponseList.getPageSize(),
					"The number of the items that should be showed");
			assertEquals((totalSubcategories / defaultPageSize), subcategoryResponseList.getTotalPages());
			assertEquals(defaultPageSize, subcategoryResponseList.getSubcategories().size(),
					"The number of the items that should be showed");
			assertEquals(subcategoryResponseList.getSubcategories().get(0).getCategory().getId(),
					createdCategory.getId(),
					"The category id should be the same");
		}
	}
}