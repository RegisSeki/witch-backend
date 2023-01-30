package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

	@NotBlank
	private String email;
	
	@NotBlank
	String password;
}
