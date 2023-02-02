package com.rtseki.witch.backend.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rtseki.witch.backend.api.assembler.CategoryAssembler;
import com.rtseki.witch.backend.api.dto.request.CategoryRequest;
import com.rtseki.witch.backend.api.dto.response.CategoryResponse;
import com.rtseki.witch.backend.api.dto.response.CategoryResponseList;
import com.rtseki.witch.backend.api.dto.response.CategorySubcategoriesResponse;
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.service.CategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	@Autowired
	private final CategoryAssembler assembler;
	
	@Autowired
	private final CategoryService service;
	
	@PostMapping
	public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
		Category createdCategory = service.create(assembler.toModel(categoryRequest));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdCategory.getId()).toUri();
		return ResponseEntity.created(uri).body(assembler.toResponse(createdCategory));		
	}

	@GetMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> findById(@PathVariable Long categoryId) {
		Category category = service.findById(categoryId);
		return ResponseEntity.ok().body(assembler.toResponse(category));
	}
			
	@PutMapping("/{categoryId}")
	public ResponseEntity<CategoryResponse> update(@PathVariable Long categoryId, @Valid @RequestBody CategoryRequest categoryRequest) {
		Category category = service.update(categoryId, assembler.toModel(categoryRequest));
		return ResponseEntity.ok().body(assembler.toResponse(category));
	}
	
	@DeleteMapping("/{categoryId}")
	public ResponseEntity<Void> delete(@PathVariable long categoryId) {
		service.delete(categoryId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public CategoryResponseList findAll(@PageableDefault(size = 5, page = 0) Pageable pageable) {
		return assembler.toCategoryResponseList(service.findAll(pageable));
	}
	
	@GetMapping("/{categoryId}/subcategories")
	public ResponseEntity<CategorySubcategoriesResponse> findSubcategories(@PathVariable Long categoryId) {
		Category category = service.findById(categoryId);
		return ResponseEntity.ok().body(assembler.toSubcategoriesResponse(category));
	}
}
