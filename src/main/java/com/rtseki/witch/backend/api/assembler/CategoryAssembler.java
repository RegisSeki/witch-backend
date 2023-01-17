package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.CategoryRequest;
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.domain.model.Category;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CategoryAssembler {
	
	private ModelMapper modelMapper;

	public Category toDto(CategoryRequest request) {
		return modelMapper.map(request, Category.class);
	}
	
	public CategoryResponse toResponse(Category category) {
		return modelMapper.map(category, CategoryResponse.class);
	}
}