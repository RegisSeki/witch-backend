package com.rtseki.witch.backend.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BusinessEstablishmentResponse {
	private Long id;
	private String comercialName;
	private String officialName;	
	private String officialRecord;
}
