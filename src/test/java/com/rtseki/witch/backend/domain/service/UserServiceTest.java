package com.rtseki.witch.backend.domain.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.UserRepository;

@Disabled
@SpringBootTest
public class UserServiceTest {		
	
	@InjectMocks
	private UserService userService;
	
	@Mock
	private UserRepository userRepository;
	
	private User user;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		
		user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setFirstname("Regis");
		user.setLastname("Seki");
		user.setEmail("regis@email.com");
		user.setPassword("123456");
	}
	
	@Test
	void whenCreate_thenReturnCreatedUser() {
		// Arrange
		when(userRepository.save(any())).thenReturn(user);
		
		// Act
		User createdUser = userService.save(user);
		
		// Assert
		assertNotNull(createdUser);
		
	}
}
