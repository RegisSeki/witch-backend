package com.rtseki.witch.backend.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.rtseki.witch.backend.domain.repository.BusinessEstablishmentRepository;

import jakarta.persistence.PersistenceException;

@DataJpaTest
public class BusinessEstablishmentTest {

	@Autowired
	private TestEntityManager testEntityManager;
	
	@Autowired
	private BusinessEstablishmentRepository repository;
	
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
	
	@Test
	@DisplayName("Find business establishment by id")
	void testFindById_whenProvideCorrectId_thenReturnBusinessEstablishment() {
		// Arrange
		BusinessEstablishment createdSubject = testEntityManager.persistAndFlush(subject);
		
		// Act
		Optional<BusinessEstablishment> foundCategory = repository.findById(createdSubject.getId());
		
		// Assert
		assertEquals(foundCategory.get(), createdSubject);
	}
	
	@Test
	@DisplayName("Update business establishment")
	void testUpdateBusinessEstablishment_whenCorrectDetails_thenReturnUpdatedBusinessEstablishment() {
		// Arrange
		BusinessEstablishment createdSubject = testEntityManager.persistAndFlush(subject);
		createdSubject.setComercialName("Updated Business Establishment Comercial Name");
		
		// Act
		BusinessEstablishment updatedSubject = repository.save(createdSubject);
		
		// Act and Assert
		assertEquals(createdSubject, updatedSubject);
	}
	
	@Test
	@DisplayName("Delete business establishment")
	void testDeleteBusinessEstablishment_whenProvidedId_thenNothing() {
		// Arrange
		BusinessEstablishment createdSubject1 = testEntityManager.persistAndFlush(subject);
		BusinessEstablishment createdSubject2 = new BusinessEstablishment(null, "Oba Hortifruti", null, null);
		testEntityManager.persistAndFlush(createdSubject2);
		
		// Act
		repository.deleteById(createdSubject2.getId());
	    List<BusinessEstablishment> subjects = repository.findAll();
		
		//Assert
	    assertThat(subjects).hasSize(1).contains(createdSubject1);
	}
	
	@Test
	@DisplayName("Find all business establishments")
	void testFindAll_whenFindAll_thenReturnBusinessEstablishmentList() {
		// Arrange
		testEntityManager.persistAndFlush(subject);
		BusinessEstablishment subject1 = new BusinessEstablishment(null, "Walmart", null, null);
		testEntityManager.persistAndFlush(subject1);
		BusinessEstablishment subject2 = new BusinessEstablishment(null, "7 Eleven", null, null);
		testEntityManager.persistAndFlush(subject2);
		
		// Act
		List<BusinessEstablishment> subjects = repository.findAll();
		
		// Assert
		assertThat(subjects).hasSize(3).contains(subject, subject1, subject2);
	}
}
