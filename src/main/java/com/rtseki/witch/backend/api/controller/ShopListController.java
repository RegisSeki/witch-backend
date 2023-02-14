package com.rtseki.witch.backend.api.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.rtseki.witch.backend.api.assembler.ShopListAssembler;
import com.rtseki.witch.backend.api.dto.request.ShopListRequest;
import com.rtseki.witch.backend.api.dto.response.ShopListResponse;
import com.rtseki.witch.backend.api.dto.response.ShopListResponseList;
import com.rtseki.witch.backend.domain.model.ShopList;
import com.rtseki.witch.backend.domain.service.ShopListService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/shop-lists")
@RequiredArgsConstructor
public class ShopListController {
	
	@Autowired
	private ShopListService service;
	
	@Autowired
	private ShopListAssembler assembler;
	
	@PostMapping
	public ResponseEntity<ShopListResponse> create(@Valid @RequestBody ShopListRequest request) {
		ShopList createdShopList = service.create(assembler.toModel(request));
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").
				buildAndExpand(createdShopList.getId()).toUri();
		
		return ResponseEntity.created(uri).body(assembler.toResponse(createdShopList));
	}
	
	@GetMapping
	public ShopListResponseList findAll(@PageableDefault(size = 5, page = 0) Pageable pageable) {
		return assembler.toShopListResponseList(service.findAll(pageable));
	}
}
