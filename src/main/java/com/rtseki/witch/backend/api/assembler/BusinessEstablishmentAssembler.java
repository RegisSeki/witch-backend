package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.BusinessEstablishmentRequest;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponse;
import com.rtseki.witch.backend.domain.model.BusinessEstablishment;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class BusinessEstablishmentAssembler {
	
	private ModelMapper modelMapper;
	
	public BusinessEstablishment toModel(BusinessEstablishmentRequest request) {
		return modelMapper.map(request, BusinessEstablishment.class);
	}
	
	public BusinessEstablishmentResponse toResponse(BusinessEstablishment businessEstablishment) {
		return modelMapper.map(businessEstablishment, BusinessEstablishmentResponse.class);
	}
}
