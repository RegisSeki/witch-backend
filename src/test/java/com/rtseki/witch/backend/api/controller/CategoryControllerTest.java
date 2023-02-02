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
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.api.dto.response.CategoryResponseList;
import com.rtseki.witch.backend.api.dto.response.CategorySubcategoriesResponse;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;
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
		user.setFirstname("Regis");
		user.setLastname("Seki");
		user.setEmail("regis@mail.com");
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
	@DisplayName("Do not create Category when name parameter is missing")
	@Order(2)
	void testCreateCategory_whenMissingCategoryNameParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.remove("name");
		categoryDetailsRequestJson.put("description", "Things to eat");

		HttpEntity<String> requestEntity = new HttpEntity<>(categoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity(
				"/api/v1/categories", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not create Category when name parameter is empty")
	@Order(3)
	void testCreateCategory_whenEmptyCategoryNameParameter_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("name", "");
		categoryDetailsRequestJson.put("description", "Things to eat");

		HttpEntity<String> requestEntity = new HttpEntity<>(categoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity(
				"/api/v1/categories", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}

	@Test
	@DisplayName("Do not create Category when name is already used")
	@Order(4)
	void testCreateCategory_whenCategoryNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject categoryDetailsRequestJson = new JSONObject();
		categoryDetailsRequestJson.put("name", "Food");
		categoryDetailsRequestJson.put("description", "Things to eat");

		HttpEntity<String> requestEntity = new HttpEntity<>(categoryDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.postForEntity(
				"/api/v1/categories", requestEntity, String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Category name is already taken"));
	}

	@Test
	@DisplayName("Find category by id")
	@Order(5)
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
	@Order(6)
	void testFindCategoryById_whenGivenIncorrectId_return404() {
		// Arrange
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.exchange("/api/v1/categories/10000",
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
	@DisplayName("Update category")
	@Order(7)
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
	@Order(8)
	void testUpdateCategory_whenCategoryNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		JSONObject updateCategoryJson = new JSONObject();
		updateCategoryJson.put("name", "Eletronic Games");

		HttpEntity<String> requestEntity = new HttpEntity<>(updateCategoryJson.toString(), headers);

		// Act
		ResponseEntity<String> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.PUT, requestEntity,
				String.class);

		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
        assertTrue(response.getBody().toString().contains(
        		"Category name is already taken"));
	}
	
	@Test
	@DisplayName("Delete category")
	@Order(9)
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
	@Order(10)
	void testDeleteCategory_whenProvidedIncorrectId_thenReturn404() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<String> response = testRestTemplate.exchange(
				"/api/v1/categories/" + createdCategory.getId(), HttpMethod.DELETE, requestEntity,
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
		@Order(11)
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
		@Order(12)
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
		@Order(13)
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
	
	@Nested
	@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
	@TestInstance(TestInstance.Lifecycle.PER_CLASS)
	@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
	class CategoryControllerTestSubcategoriesAssociated{
		
		@Autowired
		private SubcategoryRepository subcategoryRepository;
		
		Category category;
		int totalSubcategoriesAssociated = 3;
		Category categoryToDeleteTest;
		Subcategory subcategoryToDeleteTest;
		
		@BeforeAll
		void init() {
			category = new Category(null, "Category", "Category description");
			category = categoryRepository.save(category);
			
			for(int i = 0; i < totalSubcategoriesAssociated; i ++) {
				Subcategory subcategory = new Subcategory();
				subcategory.setCategory(category);
				subcategory.setName("Subcategory" + i);
				subcategory.setDescription("Subcategory description" + i);
				subcategoryRepository.save(subcategory);
			}
			
			categoryToDeleteTest = new Category(null, "categoryToDeleteTest", null);
			categoryToDeleteTest = categoryRepository.save(categoryToDeleteTest);
			
			subcategoryToDeleteTest = new Subcategory(null, "SubcategoryToDeleteTest", 
					null, categoryToDeleteTest);
			subcategoryToDeleteTest = subcategoryRepository.save(subcategoryToDeleteTest);
		}
		
		@Test
		@DisplayName("Find category by id with all subcategories associated")
		@Order(14)
		void testFindCategoryById_whenNeedSubcategories_thenReturnCategoryAndSubcategoriesList() {
			// Arrange
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<CategorySubcategoriesResponse> response = testRestTemplate.exchange(
					"/api/v1/categories/" + category.getId() + "/subcategories",
					HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<CategorySubcategoriesResponse>() {
					});
			CategorySubcategoriesResponse categorySubcategoriesList = response.getBody();
			
			//Assert
			assertEquals(categorySubcategoriesList.getSubcategories().size(), totalSubcategoriesAssociated,
					"It should be the same number totalSubcategoriesAssociated");
			assertEquals(categorySubcategoriesList.getId(), category.getId(),
					"It should be the same Category Id");
		}
		
		@Test
		@DisplayName("Do not delete Category with Associated Subcategory")
		@Order(15)
		void testDeleteCategory_whenSubcategoryAssociated_thenReturn404() {
			// Arrange 
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			
			//Act
			ResponseEntity<String> response = testRestTemplate.exchange(
					"/api/v1/categories/" + categoryToDeleteTest.getId(), HttpMethod.DELETE, requestEntity,
					String.class);
			
			// Assert
			assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
					"HTTP Status code should be 404");
	        assertTrue(response.getBody().toString().contains(
	        		"Association data is present, delete associated subcategories first"));
		}
		
		@Test
		@DisplayName("Delete Category when do not have associated Subcategory")
		@Order(16)
		void testDeleteCategory_whenNoSubcategoryAssociated_thenReturn204() {
			// Arrange 
			subcategoryRepository.delete(subcategoryToDeleteTest);
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
			
			//Act
			ResponseEntity<String> response = testRestTemplate.exchange(
					"/api/v1/categories/" + categoryToDeleteTest.getId(), HttpMethod.DELETE, requestEntity,
					String.class);
			
			// Assert
			assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
					"HTTP Status code should be 204");
		}
	}
}
