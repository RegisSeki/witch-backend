package com.rtseki.witch.backend.api.assembler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.CategoryRequest;
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.api.dto.response.CategoryResponseList;
import com.rtseki.witch.backend.domain.model.Category;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class CategoryAssembler {
	
	private ModelMapper modelMapper;

	public Category toRequest(CategoryRequest request) {
		return modelMapper.map(request, Category.class);
	}
	
	public CategoryResponse toResponse(Category category) {
		return modelMapper.map(category, CategoryResponse.class);
	}
	
	public CategoryResponseList toCategoryResponseList(Page<Category> categories) {
		
		CategoryResponseList result = new CategoryResponseList();
		List<CategoryResponse> categoryResponseList = new ArrayList<>();

		result.setPageNumber(categories.getNumber());
		result.setTotalElements(categories.getTotalElements());
		result.setPageSize(categories.getSize());
		result.setTotalPages(categories.getTotalPages());


		for (Category category : categories) {
			CategoryResponse categoryResponse = modelMapper.map(category, CategoryResponse.class);
			categoryResponseList.add(categoryResponse);
		}
		result.setCategories(categoryResponseList);
		
		return result;
	}
}