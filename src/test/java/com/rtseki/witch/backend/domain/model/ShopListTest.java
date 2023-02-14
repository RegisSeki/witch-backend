package com.rtseki.witch.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class ShopListTest {

	@Autowired
	private TestEntityManager entityManager;
	
	ShopList shopList;
	User user;
	
	@BeforeEach
	void setup() {
		user = new User();
		user.setUserId(UUID.randomUUID().toString());
		user.setFirstname("Yuki");
		user.setLastname("Seki");
		user.setEmail("yuki@email.com");
		user.setPassword("123456");
		
		entityManager.persistAndFlush(user);
		
		shopList = new ShopList();
		shopList.setName("First shopping list");
		shopList.setUser(user);
		shopList.setStatus(Status.OPEN);
		shopList.setCreatedAt(Instant.now());
	}
	
	@Test
	@DisplayName("Create shop list")
	void testCreateShopList_whenGivenCorrectDetails_thenReturnStoredShopListDetails() {
		// Arrange
		
		// Act
		ShopList storedShopList = entityManager.persistAndFlush(shopList);
		
		// Assert
		assertTrue(storedShopList.getId() > 0);
		assertEquals(shopList, storedShopList);
	}
	
	@Test
	@DisplayName("Do not create shop list when repeated name for the same user")
	void testCreateShopList_whenGivenRepeatedDetails_thenThrowException() {
		// Arrange
		ShopList oldShopList = new ShopList();
		oldShopList.setName(shopList.getName());
		oldShopList.setUser(user);
		entityManager.persistAndFlush(oldShopList);
		
		// Act and Assert
		assertThrows(PersistenceException.class, () ->{
			entityManager.persistAndFlush(shopList);
		}, "Was expecting a PersistenceException to be thrown." );
	}
}
