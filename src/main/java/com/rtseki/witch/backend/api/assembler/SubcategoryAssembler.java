package com.rtseki.witch.backend.api.assembler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.SubcategoryRequest;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponse;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponseList;
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
	
	public SubcategoryResponseList toSubcategoryResponseList(Page<Subcategory> subcategories) {
		SubcategoryResponseList result = new SubcategoryResponseList();
		List<SubcategoryResponse> subcategoryResponseList = new ArrayList<>();

		result.setPageNumber(subcategories.getNumber());
		result.setTotalElements(subcategories.getTotalElements());
		result.setPageSize(subcategories.getSize());
		result.setTotalPages(subcategories.getTotalPages());

		for (Subcategory subcategory : subcategories) {
			SubcategoryResponse categoryResponse = modelMapper.map(subcategory, SubcategoryResponse.class);
			subcategoryResponseList.add(categoryResponse);
		}
		result.setSubcategories(subcategoryResponseList);
		
		return result;
	}
}
