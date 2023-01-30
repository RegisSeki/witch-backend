package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

	@NotBlank(message = "May not be blank")
	private String email;
	
	@NotBlank(message = "May not be blank")
	String password;
}
