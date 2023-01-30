package com.rtseki.witch.backend.api.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

	@NotBlank(message = "May not be blank")
	private String firstname;
	
	@NotBlank(message = "May not be blank")
	private String lastname;
	
	@Email
	private String email;
	
	@NotBlank(message = "May not be blank")
	private String password;
}
