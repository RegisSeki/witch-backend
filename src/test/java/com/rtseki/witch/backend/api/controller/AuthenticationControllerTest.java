package com.rtseki.witch.backend.api.controller;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
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
    
	@Test
	@DisplayName("Create User")
	@Order(1)
	void testCreateUser_whenValidParameters_returnToken() throws Exception {
		// Arrange
		
		JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Yuki");
        userDetailsRequestJson.put("lastname", "Seki");
        userDetailsRequestJson.put("email", "yuki@mail.com");
        userDetailsRequestJson.put("password","12345678");
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

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
		
		JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Yuki");
        userDetailsRequestJson.put("lastname", "Seki");
        userDetailsRequestJson.put("email", "yuki@mail.com");
        userDetailsRequestJson.put("password","12345678");
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act

	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, null);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Do not Create User with Firstname too short")
	@Order(3)
	void testCreateUser_whenFirstNameIsTooShort_return400() throws Exception {
		// Arrange
		
		JSONObject userDetailsRequestJson = new JSONObject();
        userDetailsRequestJson.put("firstname", "Y");
        userDetailsRequestJson.put("lastname", "Seki");
        userDetailsRequestJson.put("email", "newyuki@mail.com");
        userDetailsRequestJson.put("password","12345678");
		
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

        HttpEntity<String> request = new HttpEntity<>(userDetailsRequestJson.toString(), headers);
		
		// Act
	    ResponseEntity<String> response = testRestTemplate.postForEntity("/api/v1/auth/register",
	    		request, null);
	
		// Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
	}
	
	@Test
	@DisplayName("Authenticate User")
	@Order(4)
	void testAuthenticateUser_whenCorrectParameters_returnToken() throws JSONException {
		// Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "yuki@mail.com");
        loginCredentials.put("password","12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.postForEntity("/api/v1/auth/authenticate",
                request, AuthenticationResponse.class);
        
		String authenticatedUsertoken = response.getBody().toString();        
        
		// Assert
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(authenticatedUsertoken);
        Assertions.assertTrue(authenticatedUsertoken.contains("token"));
	}
	
	@Test
	@DisplayName("Do not authenticate User")
	@Order(5)
	void testAuthenticateUser_whenIncorrectParameters_return403() throws JSONException {
		// Arrange
        JSONObject loginCredentials = new JSONObject();
        loginCredentials.put("email", "wrong@mail.com");
        loginCredentials.put("password","12345678");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        
        HttpEntity<String> request = new HttpEntity<>(loginCredentials.toString(), headers);

        // Act
        ResponseEntity<AuthenticationResponse> response = testRestTemplate.postForEntity("/api/v1/auth/authenticate",
                request, AuthenticationResponse.class);
       
		// Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode(),
                "HTTP Status code 403 Forbidden should have been returned");
	}
}
