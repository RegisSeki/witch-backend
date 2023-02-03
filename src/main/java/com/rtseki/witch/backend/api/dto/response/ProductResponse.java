package com.rtseki.witch.backend.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponse {
	private Long id;
	private String barcode;
	private String description;
	private SubcategoryShortResponse subcategory;
}
