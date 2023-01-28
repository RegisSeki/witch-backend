package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubcategoryRequest {
	@NotNull
	private CategoryIdRequest category;
	@NotNull
	private String name;
	private String description;
}
