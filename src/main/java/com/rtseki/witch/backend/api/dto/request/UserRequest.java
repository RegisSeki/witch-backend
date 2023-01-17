package com.rtseki.witch.backend.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {

	private String firstname;
	private String lastname;
	private String email;
	private String password;
}
