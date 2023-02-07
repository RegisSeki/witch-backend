package com.rtseki.witch.backend.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rtseki.witch.backend.api.assembler.ProductAssembler;
import com.rtseki.witch.backend.api.dto.request.ProductRequest;
import com.rtseki.witch.backend.api.dto.response.ProductResponse;
import com.rtseki.witch.backend.domain.model.Product;
import com.rtseki.witch.backend.domain.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
	
	@Autowired
	private final ProductAssembler assembler;
	
	@Autowired
	private final ProductService service;
	
	@PostMapping
	public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
		Product createdProduct = service.create(assembler.toModel(request));
		URI uri = ServletUriComponentsBuilder.
				fromCurrentRequest().
				path("/{id}").
				buildAndExpand(createdProduct.getId()).
				toUri();
		return ResponseEntity.created(uri).body(assembler.toResponse(createdProduct));
	}
	
	@GetMapping("/{productId}")
	public ResponseEntity<ProductResponse> findById(@PathVariable Long productId) {
		Product product = service.findById(productId);
		return ResponseEntity.ok().body(assembler.toResponse(product));
	}
	
	@PutMapping("/{productId}")
	public ResponseEntity<ProductResponse> update(@PathVariable Long productId, 
			@Valid @RequestBody ProductRequest request) {
		Product product = service.update(productId, assembler.toModel(request));
		return ResponseEntity.ok().body(assembler.toResponse(product));
	}
}
