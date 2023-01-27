package com.rtseki.witch.backend.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubcategoryResponse {
	private Long id;
	private String name;
	private String description;
	private CategoryResponse category;
}
