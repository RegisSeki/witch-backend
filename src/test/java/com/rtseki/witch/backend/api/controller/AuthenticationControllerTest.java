package com.rtseki.witch.backend.api.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rtseki.witch.backend.api.dto.request.UserRequest;

@Disabled
@WebMvcTest(controllers = AuthenticationController.class,
excludeAutoConfiguration = {SecurityAutoConfiguration.class})
public class AuthenticationControllerTest {

	@Autowired
	private MockMvc mockMvc;
	
	@Test
	@DisplayName("Create User")
	void testCreateUser_whenValidParameters_returnsToken() throws Exception {
		// Arrange
		UserRequest userRequest = new UserRequest();
		userRequest.setFirstname("Yuki");
		userRequest.setLastname("Seki");
		userRequest.setEmail("yuki@mail.com");
		userRequest.setPassword("12345678");
		
		RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/auth")
			.contentType(MediaType.APPLICATION_JSON)
			.accept(MediaType.APPLICATION_JSON)
			.content(new ObjectMapper().writeValueAsString(userRequest));
		
		// Act
		MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
		String responseAsString = mvcResult.getResponse().getContentAsString();
		
		// Assert
		Assertions.assertNotNull(responseAsString);
	}
}
