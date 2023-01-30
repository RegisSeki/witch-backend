package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.SubcategoryRequest;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponse;
import com.rtseki.witch.backend.domain.model.Subcategory;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class SubcategoryAssembler {

	private ModelMapper modelMapper;	
	
	public Subcategory toModel(SubcategoryRequest request) {
		return modelMapper.map(request, Subcategory.class);
	}
	
	public SubcategoryResponse toResponse(Subcategory subcategory) {
		return modelMapper.map(subcategory, SubcategoryResponse.class);
	}
}
