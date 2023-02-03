package com.rtseki.witch.backend.api.dto.request;

import com.rtseki.witch.backend.domain.model.Subcategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductRequest {
	@NotNull
	private Subcategory subcategory;
	
	@NotBlank
	private String barcode;
	
	@NotBlank
	private String name;
	
	private String description;
}
