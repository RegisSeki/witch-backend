package com.rtseki.witch.backend.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortProductResponse {
	private Long id;
	private String barcode;
	private String name;
	private String description;
}
