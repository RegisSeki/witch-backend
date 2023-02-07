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
import com.rtseki.witch.backend.api.dto.response.ProductResponse;
import com.rtseki.witch.backend.api.dto.response.ProductResponseList;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.model.Product;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;
import com.rtseki.witch.backend.domain.repository.ProductRepository;
import com.rtseki.witch.backend.domain.repository.SubcategoryRepository;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
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
	private ProductRepository repository;
	
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
	
	@Test
	@DisplayName("Update Product barcode and name")
	@Order(11)
	void testUpdateProduct_whenValidParams_thenReturnProductDetails() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029000441");
		productDetailsRequestJson.put("name", "Sachê Wiskas Salmão 85g");
		productDetailsRequestJson.put("description", "Yuki preferred");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<ProductResponse> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<ProductResponse>() {
				});
		
		ProductResponse updatedProduct = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(productDetailsRequestJson.getString("barcode"), updatedProduct.getBarcode(),
				"Returned product barcode seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("name"), updatedProduct.getName(),
				"Returned product name seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("description"), updatedProduct.getDescription(),
				"Returned product description seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("id"), updatedProduct.getSubcategory().getId().toString(),
				"Returned product id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Update Product description keeping barcode and name")
	@Order(12)
	void testUpdateProduct_whenChangeOnlyDescription_thenReturnProductDetails() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029000441");
		productDetailsRequestJson.put("name", "Sachê Wiskas Salmão 85g");
		productDetailsRequestJson.put("description", "Yuki preferred for sure");
		
		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<ProductResponse> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				new ParameterizedTypeReference<ProductResponse>() {
				});
		
		ProductResponse updatedProduct = response.getBody();

		// Assert
		assertEquals(HttpStatus.OK, response.getStatusCode(),
				"HTTP Status code should be 201");
		assertEquals(productDetailsRequestJson.getString("barcode"), updatedProduct.getBarcode(),
				"Returned product barcode seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("name"), updatedProduct.getName(),
				"Returned product name seems to be incorrect");
		assertEquals(productDetailsRequestJson.getString("description"), updatedProduct.getDescription(),
				"Returned product description seems to be incorrect");
		assertEquals(subcategoryDetailsRequestJson.getString("id"), updatedProduct.getSubcategory().getId().toString(),
				"Returned product id seems to be incorrect");
	}
	
	@Test
	@DisplayName("Do not update Product when barcode is already used")
	@Order(13)
	void testUpdateProduct_whenBarcodeIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		Product subject = new Product(null, "7896029028391", "Petisco salmão Whiskas pote 40G", null, createdSubcategory);
		repository.save(subject);
		
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896029028391");
		productDetailsRequestJson.put("name", "Sachê Wiskas Salmão 85g");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);
		try {
			// Act
			ResponseEntity<String> response = restTemplate.exchange(
					"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
					String.class);
			
			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
					"HTTP Status code should be 400");
	        assertTrue(response.getBody().toString().contains(
	        		"Product barcode is already register"));
		} finally {
			repository.delete(subject);
		}
	}
	
	@Test
	@DisplayName("Do not update Product when name is already used")
	@Order(14)
	void testUpdateProduct_whenNameIsAlreadyUsed_thenReturn400() throws JSONException {
		// Arrange
		Product subject = new Product(null, "7896029028391", "Petisco salmão Whiskas pote 40G", null, createdSubcategory);
		repository.save(subject);
		
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896051111016");
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);
		try {
			// Act
			ResponseEntity<String> response = restTemplate.exchange(
					"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
					String.class);
			
			// Assert
			assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
					"HTTP Status code should be 400");
	        assertTrue(response.getBody().toString().contains(
	        		"Product name is already taken"));
		} finally {
			repository.delete(subject);
		}
	}
	
	@Test
	@DisplayName("Do not update Product when Subcategory param is missing")
	@Order(15)
	void testUpdateProduct_whenSubcategoryParamIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("barcode", "7896051111016");
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"subcategory\""));
	}
	
	@Test
	@DisplayName("Do not update Product when barcode param is missing")
	@Order(16)
	void testUpdateProduct_whenBarcodeParamIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"barcode\""));
	}
	
	@Test
	@DisplayName("Do not update Product when barcode param is blank")
	@Order(17)
	void testUpdateProduct_whenBarcodeIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "");
		productDetailsRequestJson.put("name", "Petisco salmão Whiskas pote 40G");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"barcode\""));
	}
	
	@Test
	@DisplayName("Do not update Product when name param is missing")
	@Order(18)
	void testUpdateProduct_whenNameParamIsMissing_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896051111016");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Do not update Product when name param is blank")
	@Order(19)
	void testUpdateProduct_whenNameIsBlank_thenReturn400() throws JSONException {
		// Arrange
		JSONObject subcategoryDetailsRequestJson = new JSONObject();
		subcategoryDetailsRequestJson.put("id", createdSubcategory.getId().toString());
		JSONObject productDetailsRequestJson = new JSONObject();
		productDetailsRequestJson.put("subcategory", subcategoryDetailsRequestJson);
		productDetailsRequestJson.put("barcode", "7896051111016");
		productDetailsRequestJson.put("name", " ");

		HttpEntity<String> requestEntity = new HttpEntity<>(
				productDetailsRequestJson.toString(), headers);

		// Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.PUT, requestEntity,
				String.class);
		
		// Assert
		assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode(),
				"HTTP Status code should be 400");
		assertTrue(response.getBody().toString().contains("\"name\":\"name\""));
	}
	
	@Test
	@DisplayName("Delete Product")
	@Order(20)
	void testDeleteSubcategory_whenProvidedCorrectId_thenReturn204() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<Void> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.DELETE, requestEntity,
				Void.class);
		List<Product> products = repository.findAll();		
		
		// Assert
		assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode(),
				"HTTP Status code should be 204");
		assertEquals(products.size(), 0,
				"It should not have any product");
	}
	
	@Test
	@DisplayName("Do not delete product")
	@Order(21)
	void testDeleteProduct_whenProvidedInexistentId_thenReturn404() {
		// Arrange 
		HttpEntity<String> requestEntity = new HttpEntity<String>(headers);
		
		//Act
		ResponseEntity<String> response = restTemplate.exchange(
				"/api/v1/products/" + createdProduct.getId(), HttpMethod.DELETE, requestEntity,
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
	class ProductControllerTestProductList{
		
		private final int totalProducts = 20;
		private final int defaultPageSize = 5;
		
		@BeforeAll
		void init() {
			for(int i = 0; i < totalProducts; i ++) {
				Product product = new Product();
				product.setSubcategory(createdSubcategory);
				product.setBarcode("barcode" + i);
				product.setName("Subcategory" + i);
				product.setDescription("Subcategory description" + i);
				repository.save(product);
			}
		}
		
		@Test
		@DisplayName("Find all products with default pagination")
		@Order(22)
		void testFindAllProducts_whenDefaultPagination_thenReturnProperProducts() {
			// Arrange
			HttpEntity<String> requestEntity = new HttpEntity<String>(headers);

			// Act
			ResponseEntity<ProductResponseList> response = restTemplate.exchange(
					"/api/v1/products", HttpMethod.GET, requestEntity,
					new ParameterizedTypeReference<ProductResponseList>() {
					});
			ProductResponseList productResponseList = response.getBody();
			
			//Assert
			assertEquals(totalProducts, productResponseList.getPageDetails().getTotalElements(),
					"It should be the same number of elements");
			assertEquals(defaultPageSize, productResponseList.getPageDetails().getPageSize(),
					"The number of the items that should be showed");
			assertEquals((totalProducts / defaultPageSize), productResponseList.getPageDetails().getTotalPages());
			assertEquals(defaultPageSize, productResponseList.getProducts().size(),
					"The number of the items that should be showed");
			assertEquals(productResponseList.getProducts().get(0).getSubcategory().getId(),
					createdProduct.getId(),
					"The subcategory id should be the same");
		}
	}
}
