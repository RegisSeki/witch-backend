package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryIdRequest {
	
	@NotBlank(message = "May not be blank")
	private Long id;
}
