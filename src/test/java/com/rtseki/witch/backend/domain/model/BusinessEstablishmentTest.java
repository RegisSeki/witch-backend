package com.rtseki.witch.backend.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class BusinessEstablishmentTest {

	@Autowired
	private TestEntityManager testEntityManager;
	
	 BusinessEstablishment subject;
	
	@BeforeEach
	void setup() {
		subject = new BusinessEstablishment();
		subject.setComercialName("Carrefour Dutra");
		subject.setOfficialName("Carrefour Comercio e Industria LTDA");
		subject.setOfficialRecord("45.543.915/0036-01");
	}
	
	@Test
	@DisplayName("Create Business Establishment")
	void testCreateBusinessEstablishment_whenGivenCorrectDetails_thenReturnStoredBusinessEstablishmentDetails() {
		// Arrange
		
		// Act
		BusinessEstablishment storedSubject = testEntityManager.persistAndFlush(subject);
		
		// Assert
		assertTrue(storedSubject.getId() > 0);
		assertEquals(subject.getComercialName(), storedSubject.getComercialName());
		assertEquals(subject.getOfficialName(), storedSubject.getOfficialName());
		assertEquals(subject.getOfficialRecord(), storedSubject.getOfficialRecord());
	}
	
	@Test
	@DisplayName("Do not create business establishment when repeated name")
	void testCreateBusinessEstablishment_whenGivenRepeatedDetails_thenThrowException() {
		// Arrange
		BusinessEstablishment oldSubject = new BusinessEstablishment();
		oldSubject.setComercialName(subject.getComercialName());
		testEntityManager.persistAndFlush(oldSubject);
		
		// Act and Assert
		assertThrows(PersistenceException.class, () ->{
			testEntityManager.persistAndFlush(subject);
		}, "Was expecting a PersistenceException to be thrown." );
	}
}
