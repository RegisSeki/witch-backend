package com.rtseki.witch.backend.api.dto.response;

import com.rtseki.witch.backend.domain.model.Status;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShopListResponse {
	private Long id;
	private String name;
	private Status status;
}
