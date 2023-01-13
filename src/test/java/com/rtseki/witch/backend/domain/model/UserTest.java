package com.rtseki.witch.backend.domain.model;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.PersistenceException;
import jakarta.validation.ConstraintViolationException;

@DataJpaTest
public class UserTest {
	
	@Autowired
	private TestEntityManager testEntityManager;
	
	User user;
	
	@BeforeEach
	void setup() {
		user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setFirstname("Yuki");
		user.setLastname("Seki");
		user.setEmail("yuki@email.com");
		user.setPassword("123456");
	}
	
	@Test
	void testUser_whenValidUserDetailsProvided_shouldReturnStoredUserDetails() {
		// Arrange
		
		// Act
		User storedUser = testEntityManager.persistAndFlush(user);
		
		// Assert
		Assertions.assertTrue(storedUser.getId() > 0);
		Assertions.assertEquals(user.getUserId(), storedUser.getUserId());
		Assertions.assertEquals(user.getFirstname(), storedUser.getFirstname());
		Assertions.assertEquals(user.getLastname(), storedUser.getLastname());
		Assertions.assertEquals(user.getEmail(), storedUser.getEmail());
		Assertions.assertEquals(user.getPassword(), storedUser.getPassword());
	}
	
	@Test
	void testUser_whenFirstnameIsTooLong_shouldThrowException() {
		// Arrange
		user.setFirstname("ThisIsAExampleForANameThatIsBiggerThan50Characters_");
		
		// Act and Assert
		Assertions.assertThrows(ConstraintViolationException.class, () ->{
			testEntityManager.persistAndFlush(user);
		}, "Was expecting a ConstraintViolationException to be thrown." );
	}
	
	@Test
	void testUser_whenFirstnameIsTooShort_shouldThrowException() {
		// Arrange
		user.setFirstname("Ab");
		
		// Act and Assert
		Assertions.assertThrows(ConstraintViolationException.class, () ->{
			testEntityManager.persistAndFlush(user);
		}, "Was expecting a ConstraintViolationException to be thrown." );
	}
	
	@Test
	void testUser_whenFirstnameIsBlank_shouldThrowException() {
		// Arrange
		user.setFirstname(null);
		
		// Act and Assert
		Assertions.assertThrows(ConstraintViolationException.class, () ->{
			testEntityManager.persistAndFlush(user);
		}, "Was expecting a ConstraintViolationException to be thrown." );
	}
	
	@Test
	void testUser_whenExistingUserIdProvided_shouldThrowException() {
		// Arrange
		// Create and persist a user
		User oldUser = new User();
		oldUser.setUserId("1");
		oldUser.setFirstname("Yuki");
		oldUser.setLastname("Seki");
		oldUser.setEmail("yuki@email.com");
		oldUser.setPassword("123456");
		testEntityManager.persistAndFlush(oldUser);
		
		// Update context user with the same old user userId
		user.setUserId("1");
		
		// Act and Assert
		Assertions.assertThrows(PersistenceException.class, () ->{
			testEntityManager.persistAndFlush(user);
		}, "Was expecting a PersistenceException to be thrown." );
	}
	
	@Test
	void testUser_whenExistingEmailProvided_shouldThrowException() {
		// Arrange
		// Create and persist a user
		User oldUser = new User();
		oldUser.setUserId("100");
		oldUser.setFirstname("Yuki");
		oldUser.setLastname("Seki");
		oldUser.setEmail("yuki@email.com");
		oldUser.setPassword("123456");
		testEntityManager.persistAndFlush(oldUser);
		
		// Update context user with the same old user email
		user.setEmail("yuki@email.com");
		
		// Act and Assert
		Assertions.assertThrows(PersistenceException.class, () ->{
			testEntityManager.persistAndFlush(user);
		}, "Was expecting a PersistenceException to be thrown." );
	}
}
