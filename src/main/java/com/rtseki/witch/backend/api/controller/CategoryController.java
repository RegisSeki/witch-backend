package com.rtseki.witch.backend.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
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
import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.service.CategoryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

	@Autowired
	private final CategoryAssembler categoryAssembler;
	
	@Autowired
	private final CategoryService categoryService;
	
	@PostMapping
	public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest categoryRequest) {
		Category newCategory = categoryAssembler.toDto(categoryRequest);
		Category createdCategory = categoryService.create(newCategory);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdCategory.getId()).toUri();
		return ResponseEntity.created(uri).body(categoryAssembler.toResponse(createdCategory));		
	}
	
	@GetMapping
	public Page<Category> findAll(@PageableDefault(size = 5, page = 0) Pageable pageable) {
		return categoryService.findAll(pageable);
	}
	
	@GetMapping("/{categoryId}")
	public ResponseEntity<Category> findById(@PathVariable Long categoryId) {
		Category category = categoryService.findById(categoryId);
		return ResponseEntity.ok().body(category);
	}
	
	@PutMapping("/{categoryId}")
	public ResponseEntity<Category> update(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) {
		Category category = categoryService.update(categoryId, categoryAssembler.toDto(categoryRequest));
		return ResponseEntity.ok().body(category);
	}
}
