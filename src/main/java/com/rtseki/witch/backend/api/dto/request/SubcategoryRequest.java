package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubcategoryRequest {

	@NotNull
	private CategoryIdRequest category;
	
	@NotBlank
	private String name;
	
	private String description;
}
