package com.rtseki.witch.backend.api.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerTest {

    @Value("${server.port}")
    private int serverPort;
	
    @LocalServerPort
    private int localServerPort;
    
    @Autowired
    private TestRestTemplate testRestTemplate;
    
    private JSONObject userDetailsRequestJson;
    private JSONObject loginCredentials;
    private HttpHeaders headers;
    
    @BeforeEach
    void setup() throws JSONException {
    	userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Yuki");
        userDetailsRequestJson.put("lastname", "Seki");
        userDetailsRequestJson.put("email", "yuki@mail.com");
        userDetailsRequestJson.put("password","12345678");
        
    	loginCredentials = new JSONObject();
        loginCredentials.put("email", "yuki@mail.com");
        loginCredentials.put("password","12345678");
    	
    	headers = new HttpHeaders();
    	headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    }
    
	@Test
	@DisplayName("Create User")
	@Order(1)
	void testCreateUser_whenValidParameters_returnToken() throws Exception {
		// Arrange
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<AuthenticationResponse> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, AuthenticationResponse.class);
        
		String createdUserToken = response.getBody().toString();        
		// Assert
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(createdUserToken);
        Assertions.assertTrue(createdUserToken.contains("token"));
	}
	
	@Test
	@DisplayName("Do not Create User with existed email")
	@Order(2)
	void testCreateUser_whenInvalidParameters_return400() throws Exception {
		// Arrange
        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, String.class);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("\"title\":\"This email is already in use!\""));
	}
	
	@Test
	@DisplayName("Do not Create User, filed password name is not present")
	@Order(3)
	void testCreateUser_whenMissingPasswordParameter_return400() throws Exception {
		// Arrange
        userDetailsRequestJson.put("firstname", "Yuki");
        userDetailsRequestJson.remove("email");

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, String.class);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("\"name\":\"email\""));
	}
	
	@Test
	@DisplayName("Do not Create User, field first name is not present")
	@Order(4)
	void testCreateUser_whenMissingFirstnameParameter_return400() throws Exception {
		// Arrange
        userDetailsRequestJson.remove("firstname");
        userDetailsRequestJson.put("email", "newyuki@mail.com");

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, String.class);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("\"name\":\"firstname\""));
	}
	
	@Test
	@DisplayName("Do not Create User, parameter first name is blank")
	@Order(5)
	void testCreateUser_whenFirstnameBlank_return400() throws Exception {
		// Arrange
		userDetailsRequestJson.put("firstname", "");
        userDetailsRequestJson.put("email", "newyuki@mail.com");

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, String.class);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains("\"name\":\"firstname\""));
	}
	
	@Test
	@DisplayName("Do not Create User when Firstname too short")
	@Order(6)
	void testCreateUser_whenFirstNameIsTooShort_return400() throws Exception {
		// Arrange
        userDetailsRequestJson.put("firstname", "Y");
        userDetailsRequestJson.put("email", "newyuki@mail.com");

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, String.class);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().toString().contains(
        		"The parameters are not correct, check and try again!"));
	}
	
	@Test
	@DisplayName("Authenticate User")
	@Order(7)
	void testAuthenticateUser_whenCorrectParameters_returnToken() throws JSONException {
		// Arrange
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.postForEntity(
        		"/api/v1/auth/authenticate",
                request,
                AuthenticationResponse.class);
        
		String authenticatedUsertoken = response.getBody().toString();        
        
		// Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(authenticatedUsertoken);
        Assertions.assertTrue(authenticatedUsertoken.contains("token"));
	}
	
	@Test
	@DisplayName("Do not authenticate User")
	@Order(8)
	void testAuthenticateUser_whenIncorrectParameters_return403() throws JSONException {
		// Arrange
        loginCredentials.put("email", "wrong@mail.com");
      
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.postForEntity("/api/v1/auth/authenticate",
                request, AuthenticationResponse.class);
       
		// Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "HTTP Status code 403 Forbidden should have been returned");
	}
}
