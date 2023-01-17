package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CategoryRequest {

	@NotNull
	private String name;
	
	@NotNull
	private String description;
}
