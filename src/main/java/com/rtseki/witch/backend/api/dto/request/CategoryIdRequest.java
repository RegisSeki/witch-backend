package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryIdRequest {
	@NotNull
	private Long id;
}
