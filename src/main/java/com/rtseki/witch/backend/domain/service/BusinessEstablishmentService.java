package com.rtseki.witch.backend.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.BusinessEstablishment;
import com.rtseki.witch.backend.domain.repository.BusinessEstablishmentRepository;

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
	
	private void checkDuplicatedName(BusinessEstablishment businessEstablishment) {
		boolean isBusinessEstablishmentExist = repository.findByComercialName(businessEstablishment.getComercialName())
			.stream().anyMatch(existProduct -> !existProduct.equals(businessEstablishment));
		
		if(isBusinessEstablishmentExist) {
			throw new BusinessException("Business Establishment name is already taken");
		}
	}
	
	private void checkDuplicatedName(BusinessEstablishment businessEstablishment, Long businessEstablishmentId) {
		Optional<BusinessEstablishment> existedBusinessEstablishment = repository.findByComercialName(businessEstablishment.getComercialName());
		
		if(existedBusinessEstablishment.isPresent() && existedBusinessEstablishment.get().getId() != businessEstablishmentId) {
			throw new BusinessException("Business Establishment name is already taken");
		}
	}
}
