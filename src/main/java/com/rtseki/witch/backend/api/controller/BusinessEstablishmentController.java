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

import com.rtseki.witch.backend.api.assembler.BusinessEstablishmentAssembler;
import com.rtseki.witch.backend.api.dto.request.BusinessEstablishmentRequest;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponse;
import com.rtseki.witch.backend.api.dto.response.BusinessEstablishmentResponseList;
import com.rtseki.witch.backend.domain.model.BusinessEstablishment;
import com.rtseki.witch.backend.domain.service.BusinessEstablishmentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/business-establishments")
@RequiredArgsConstructor
public class BusinessEstablishmentController {
	
	@Autowired
	private final BusinessEstablishmentAssembler assembler;
	
	@Autowired
	private final BusinessEstablishmentService service;
	
	@PostMapping
	public ResponseEntity<BusinessEstablishmentResponse> create(@Valid @RequestBody BusinessEstablishmentRequest request) {
		BusinessEstablishment createdBusinessEstablishment = service.create(assembler.toModel(request));
		URI uri = ServletUriComponentsBuilder.
				fromCurrentRequest().
				path("/{id}").
				buildAndExpand(createdBusinessEstablishment.getId()).
				toUri();
		return ResponseEntity.created(uri).body(assembler.toResponse(createdBusinessEstablishment));
	}
	
	@GetMapping("/{businessEstablishmentId}")
	public ResponseEntity<BusinessEstablishmentResponse> findById(@PathVariable Long businessEstablishmentId) {
		BusinessEstablishment subject = service.findById(businessEstablishmentId);
		return ResponseEntity.ok().body(assembler.toResponse(subject));
	}
	
	@PutMapping("/{businessEstablishmentId}")
	public ResponseEntity<BusinessEstablishmentResponse> update(
			@Valid @RequestBody BusinessEstablishmentRequest request,
			@PathVariable Long businessEstablishmentId
		) {
		BusinessEstablishment subject = service.update(businessEstablishmentId, assembler.toModel(request));
		return ResponseEntity.ok().body(assembler.toResponse(subject));
	}
	
	@DeleteMapping("/{businessEstablishmentId}")
	public ResponseEntity<Void> delete(@PathVariable Long businessEstablishmentId) {
		service.delete(businessEstablishmentId);
		return ResponseEntity.noContent().build();
	}
	
	@GetMapping
	public BusinessEstablishmentResponseList findAll(@PageableDefault(size = 5, page = 0) Pageable pageable) {
		return assembler.toBusinessEstablishmentResponseList(service.findAll(pageable));
	}
}
