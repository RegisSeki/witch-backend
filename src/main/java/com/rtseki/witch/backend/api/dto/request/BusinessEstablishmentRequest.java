package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessEstablishmentRequest {
	@NotBlank
	private String comercialName;
	
	private String officialName;	
	private String officialRecord;
}
