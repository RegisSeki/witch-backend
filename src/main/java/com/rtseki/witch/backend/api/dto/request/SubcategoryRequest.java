package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SubcategoryRequest {
	private CategoryIdRequest category;
	
	@NotBlank
	private String name;
	
	private String description;
}
