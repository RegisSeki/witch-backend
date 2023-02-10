package com.rtseki.witch.backend.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.DatabaseException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.BusinessEstablishment;
import com.rtseki.witch.backend.domain.repository.BusinessEstablishmentRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class BusinessEstablishmentService {

	@Autowired
	private BusinessEstablishmentRepository repository;
	
	@Transactional
	public BusinessEstablishment create(BusinessEstablishment businessEstablishment) {
		checkDuplicatedName(businessEstablishment);
		return repository.save(businessEstablishment);
	}
	
	public BusinessEstablishment findById(Long businessEstablishmentId) {
		return repository.findById(businessEstablishmentId)
			.orElseThrow(() -> new ResourceNotFoundException(businessEstablishmentId));
	}
	
	public BusinessEstablishment update(Long businessEstablishmentId, BusinessEstablishment businessEstablishment) {
		try {
			checkDuplicatedName(businessEstablishment, businessEstablishmentId);
			BusinessEstablishment entity = repository.getReferenceById(businessEstablishmentId);
			updateBusinessEstablishmentData(entity, businessEstablishment);
			return repository.save(entity);
		} catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(businessEstablishmentId);
		}
	}
	
	public void delete(Long businessEstablishmentId) {
		try {
			repository.deleteById(businessEstablishmentId);
		} catch (EmptyResultDataAccessException e) {
			throw new ResourceNotFoundException(businessEstablishmentId);
		} catch (DataIntegrityViolationException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
	
	public Page<BusinessEstablishment> findAll(Pageable pageable) {
		return repository.findAll(pageable);
	}
	
	private void checkDuplicatedName(BusinessEstablishment businessEstablishment) {
		boolean isBusinessEstablishmentExist = repository.findByComercialName(businessEstablishment.getComercialName())
			.stream().anyMatch(existProduct -> !existProduct.equals(businessEstablishment));
		
		if(isBusinessEstablishmentExist) {
			throw new BusinessException("Business Establishment company name is already taken");
		}
	}
	
	private void checkDuplicatedName(BusinessEstablishment businessEstablishment, Long businessEstablishmentId) {
		Optional<BusinessEstablishment> existedBusinessEstablishment = repository.findByComercialName(businessEstablishment.getComercialName());
		
		if(existedBusinessEstablishment.isPresent() && existedBusinessEstablishment.get().getId() != businessEstablishmentId) {
			throw new BusinessException("Business Establishment company name is already taken");
		}
	}
	
	private void updateBusinessEstablishmentData(BusinessEstablishment entity, BusinessEstablishment businessEstablishment) {
		entity.setComercialName(businessEstablishment.getComercialName());
		entity.setOfficialName(businessEstablishment.getOfficialName());
		entity.setOfficialRecord(businessEstablishment.getOfficialRecord());
	}
}
