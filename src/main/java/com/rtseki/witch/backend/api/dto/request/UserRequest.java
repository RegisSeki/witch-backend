package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

	@NotBlank
	private String firstname;
	
	@NotBlank
	private String lastname;
	
	@Email
	@NotBlank
	private String email;
	
	@NotBlank
	private String password;
}
