package com.rtseki.witch.backend.api.assembler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.BusinessEstablishmentRequest;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponse;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponseList;
import com.rtseki.witch.backend.api.dto.response.PaginationDetails;
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
	
	public BusinessEstablishmentResponseList toBusinessEstablishmentResponseList(Page<BusinessEstablishment> establishments) {
		BusinessEstablishmentResponseList result = new BusinessEstablishmentResponseList();
		List<BusinessEstablishmentResponse> subjectResponseList = new ArrayList<>();
		PaginationDetails pageDetails = new PaginationDetails();
		
		pageDetails.setPageNumber(establishments.getNumber());
		pageDetails.setTotalElements(establishments.getTotalElements());
		pageDetails.setPageSize(establishments.getSize());
		pageDetails.setTotalPages(establishments.getTotalPages());
		result.setPageDetails(pageDetails);

		for (BusinessEstablishment subject : establishments) {
			BusinessEstablishmentResponse subjectResponse = modelMapper.map(subject, BusinessEstablishmentResponse.class);
			subjectResponseList.add(subjectResponse);
		}
		result.setEstablishments(subjectResponseList);
		
		return result;
	}
}
