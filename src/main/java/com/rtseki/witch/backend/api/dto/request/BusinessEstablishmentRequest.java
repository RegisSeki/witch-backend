package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessEstablishmentRequest {
	@NotNull
	private String comercialName;
	
	private String officialName;	
	private String officialRecord;
}
