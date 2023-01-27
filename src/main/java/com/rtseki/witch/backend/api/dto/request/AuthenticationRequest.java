package com.rtseki.witch.backend.api.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthenticationRequest {

	private String email;
	String password;
}
