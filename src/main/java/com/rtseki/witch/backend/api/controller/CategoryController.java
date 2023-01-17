package com.rtseki.witch.backend.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rtseki.witch.backend.api.assembler.CategoryAssembler;
import com.rtseki.witch.backend.api.dto.request.CategoryRequest;
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryAssembler categoryAssembler;
	private final CategoryService categoryService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public CategoryResponse create(@RequestBody CategoryRequest categoryRequest) {
		Category newCategory = categoryAssembler.toDto(categoryRequest);
		Category createdCategory = categoryService.create(newCategory);
		
		return categoryAssembler.toResponse(createdCategory);		
	}
}
