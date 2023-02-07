package com.rtseki.witch.backend.api.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubcategoryProductsResponse {
	private Long id;
	private String name;
	
	private List<ShortProductResponse> products;
}
