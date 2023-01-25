package com.rtseki.witch.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

import com.rtseki.witch.backend.api.dto.response.AuthenticationResponse;
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.api.dto.response.CategoryResponseList;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CategoryControllerTest {

	@Value("${server.port}")
	private int serverPort;

	@LocalServerPort
	private int localServerPort;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@Autowired
	private AuthenticationService authService;
	
	@Autowired
	private CategoryRepository categoryRepository;

	private HttpHeaders headers;
	private CategoryResponse createdCategory;

	@BeforeAll
	void init() throws JSONException {
		User user = new User();
		user.setFirstname("Yuki");
		user.setLastname("Yuki");
		user.setEmail("yuki@mail.com");
		user.setPassword("12345678");
		AuthenticationResponse authenticationResponse = authService.register(user);
		
		headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.setBearerAuth(authenticationResponse.getToken());
	}

	@Test
	@DisplayName("Create Category")
	@Order(1)
	void testCreateCategory_whenValidParams_thenReturnCategoryDetails() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("name", "Food");
		categoryDetailsRequestJson.put("description", "Things to eat");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				categoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<CategoryResponse> response = testRestTemplate.postForEntity("/api/v1/categories",
				requestEntity,
				CategoryResponse.class);
		
		createdCategory = response.getBody();

		// Assert
		assertEquals(HttpStatus.CREATED, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(categoryDetailsRequestJson.getString("name"), createdCategory.getName(),
				"Returned category name seems to be incorrect");
		assertEquals(categoryDetailsRequestJson.getString("description"), createdCategory.getDescription(),
				"Returned category description seems to be incorrect");
		assertFalse(createdCategory.getId() == null, "Category id should not be null");
	}

	@Test
	@DisplayName("Do not create Category when name is already used")
	@Order(2)
	void testCreateCategory_whenCategoryNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("name", "Food");
		categoryDetailsRequestJson.put("description", "Things to eat");

		HttpEntity<String> requestEntity = new HttpEntity<>(categoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/categories", requestEntity, null);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
	}

	@Test
	@DisplayName("Find category by id")
	@Order(3)
	void testFindCategoryById_whenGivenCorrectId_returnProperCategory() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<CategoryResponse> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.GET, requestEntity,
				new ParameterizedTypeReference<CategoryResponse>() {
				});
		CategoryResponse categoryResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(), 
				"HTTP Status code should be 200");
		assertEquals(categoryResponse.getName(), createdCategory.getName(),
				"Returned category name seems to be incorrect");
		assertEquals(categoryResponse.getDescription(), createdCategory.getDescription(),
				"Returned category description seems to be incorrect");
	}

	@Test
	@DisplayName("Do not find category by id")
	@Order(4)
	void testFindCategoryById_whenGivenIncorrectId_return404() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<CategoryResponse> response = testRestTemplate.exchange("/api/v1/categories/10000",
				HttpMethod.GET, 
				requestEntity, 
				new ParameterizedTypeReference<CategoryResponse>() {
				});

		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode(),
				"HTTP Status code should be 404");
	}

	@Test
	@DisplayName("Update category")
	@Order(5)
	void testUpdateCategory_whenValidParams_thenReturnUpdatedCategory() throws JSONException {
		// Arrange
		JSONObject updateCategoryJson = new JSONObject();
		updateCategoryJson.put("name", "Eletronic Games");
		updateCategoryJson.put("description", "For fun moments");

		HttpEntity<String> requestEntity = new HttpEntity<>(updateCategoryJson.toString(), headers);

		// Act
		ResponseEntity<CategoryResponse> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<CategoryResponse>() {
				});
		CategoryResponse updatedCategoryResponse = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 200");
		assertEquals(updateCategoryJson.getString("name"), updatedCategoryResponse.getName(),
				"Returned category name seems to be incorrect");
		assertEquals(updatedCategoryResponse.getDescription(), updatedCategoryResponse.getDescription(),
				"Returned category description seems to be incorrect");
		assertEquals(createdCategory.getId(), updatedCategoryResponse.getId(),
				"Returned category id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not update category")
	@Order(6)
	void testUpdateCategory_whenCategoryNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject updateCategoryJson = new JSONObject();
		updateCategoryJson.put("name", "Eletronic Games");

		HttpEntity<String> requestEntity = new HttpEntity<>(updateCategoryJson.toString(), headers);

		// Act
		ResponseEntity<CategoryResponse> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<CategoryResponse>() {
				});

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
	}
	
	@Test
	@DisplayName("Delete category")
	@Order(7)
	void testDeleteCategory_whenProvidedCorrectId_thenReturn204() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<Void> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.DELETE, requestEntity,
				Void.class);
		List<Category> categories = categoryRepository.findAll();		
		
		// Assert
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
				"HTTP Status code should be 204");
		assertEquals(categories.size(), 0,
				"It should not have any category");
	}
	
	@Test
	@DisplayName("Do not delete category")
	@Order(8)
	void testDeleteCategory_whenProvidedIncorrectId_thenReturn404() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<Void> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.DELETE, requestEntity,
				Void.class);
		
		// Assert
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode()
				,"HTTP Status code should be 404");
	}
	
	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	class CategoryControllerTestCategoryList{
		
		private final int totalCategories = 20;
		private final int defaultPageSize = 5;
		
		@BeforeAll
		void init() {
			for(int i = 0; i < totalCategories; i ++) {
				Category category = new Category();
				category.setName("Category" + i);
				category.setDescription("Category description" + i);
				categoryRepository.save(category);
			}
		}
		
		@Test
		@DisplayName("Find all categories with default pagination")
		@Order(9)
		void testFindAllCategories_whenDefaultPagination_thenReturnProperCategories() {
			// Arrange
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<CategoryResponseList> response = testRestTemplate.exchange(
					"/api/v1/categories", HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<CategoryResponseList>() {
					});
			CategoryResponseList categoryResponseList = response.getBody();
			
			//Assert
			assertEquals(totalCategories, categoryResponseList.getTotalElements(),
					"It should be the same number of elements");
			assertEquals(defaultPageSize, categoryResponseList.getPageSize(),
					"The number of the items that should be showed");
			assertEquals((totalCategories / defaultPageSize), categoryResponseList.getTotalPages());
			assertEquals(defaultPageSize, categoryResponseList.getCategories().size(),
					"The number of the items that should be showed");
		}
		
		@Test
		@DisplayName("Find all categories with custom size pagination")
		@Order(10)
		void testFindAllCategories_whenCustomSizePaginationValue_thenReturnProperCategories() {
			// Arrange
			int customSize = 10;
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<CategoryResponseList> response = testRestTemplate.exchange(
					"/api/v1/categories?size=" + customSize,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<CategoryResponseList>() {
					});
			CategoryResponseList categoryResponseList = response.getBody();
			
			//Assert
			assertEquals(totalCategories, categoryResponseList.getTotalElements(),
					"It should be the same number of elements");
			assertEquals(customSize, categoryResponseList.getPageSize(),
					"The number of the items that should be showed check the custom size at the arrange area of the test");
			assertEquals((totalCategories / customSize), categoryResponseList.getTotalPages());
			assertEquals(customSize, categoryResponseList.getCategories().size(),
					"The number of the items that should be showed");	
		}
		
		@Test
		@DisplayName("Find all categories with custom page pagination")
		@Order(11)
		void testFindAllCategories_whenCustomPagePaginationValue_thenReturnProperCategories() {
			// Arrange
			int customPage = 1;
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<CategoryResponseList> response = testRestTemplate.exchange(
					"/api/v1/categories?page=" + customPage,
					HttpMethod.GET,
					requestEntity,
					new ParameterizedTypeReference<CategoryResponseList>() {
					});
			CategoryResponseList categoryResponseList = response.getBody();
			//Assert
			assertEquals(totalCategories, categoryResponseList.getTotalElements(),
					"It should be the same number of elements");
			assertEquals(defaultPageSize, categoryResponseList.getPageSize(),
					"The number of the items that should be showed");			
			assertEquals(customPage, categoryResponseList.getPageNumber(),
					"The page should be the exactly customPage");
			assertEquals(defaultPageSize, categoryResponseList.getCategories().size(),
					"The number of the items that should be showed");
		}
	}
	
}
