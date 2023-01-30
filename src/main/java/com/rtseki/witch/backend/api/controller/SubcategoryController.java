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

import com.rtseki.witch.backend.api.assembler.SubcategoryAssembler;
import com.rtseki.witch.backend.api.dto.request.SubcategoryRequest;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponse;
import com.rtseki.witch.backend.api.dto.response.SubcategoryResponseList;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.service.SubcategoryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/subcategories")
@RequiredArgsConstructor
public class SubcategoryController {
	@Autowired
	private final SubcategoryAssembler assembler;
	
	@Autowired
	private final SubcategoryService service;
	
	@PostMapping
	public ResponseEntity<SubcategoryResponse> create(@Valid @RequestBody SubcategoryRequest request) {
		Subcategory createdSubcategory = service.create(assembler.toModel(request));
		URI uri = ServletUriComponentsBuilder.
				fromCurrentRequest().
				path("/{id}").
				buildAndExpand(createdSubcategory.getId()).
				toUri();
		return ResponseEntity.created(uri).body(assembler.toResponse(createdSubcategory));	
	}
	
	@GetMapping("/{subcategoryId}")
	public ResponseEntity<SubcategoryResponse> findById(@PathVariable Long subcategoryId) {
		Subcategory subcategory = service.findById(subcategoryId);
		return ResponseEntity.ok().body(assembler.toResponse(subcategory));
	}
	
	@PutMapping("/{subcategoryId}")
	public ResponseEntity<SubcategoryResponse> update(@PathVariable Long subcategoryId,
			@Valid @RequestBody SubcategoryRequest request) {
		Subcategory subcategory = service.update(subcategoryId, assembler.toModel(request));
		return ResponseEntity.ok().body(assembler.toResponse(subcategory));
	}
	
	@DeleteMapping("/{subcategoryId}")
	public ResponseEntity<Void> delete(@PathVariable Long subcategoryId) {
		service.delete(subcategoryId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public SubcategoryResponseList findAll(@PageableDefault(size = 5, page = 0) Pageable pageable) {
		return assembler.toSubcategoryResponseList(service.findAll(pageable));
	}
}
