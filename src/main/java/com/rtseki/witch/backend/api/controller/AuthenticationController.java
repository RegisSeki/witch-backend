package com.rtseki.witch.backend.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.rtseki.witch.backend.api.assembler.UserAssembler;
import com.rtseki.witch.backend.api.model.request.AuthenticationRequest;
import com.rtseki.witch.backend.api.model.request.UserRequest;
import com.rtseki.witch.backend.api.model.response.AuthenticationResponse;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.service.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final UserAssembler userAssembler;
	
	@PostMapping("/register")
	@ResponseStatus(HttpStatus.CREATED)
	public AuthenticationResponse register(@RequestBody UserRequest request) {
		User newUser = userAssembler.toModel(request);
		return authenticationService.register(newUser);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
		return ResponseEntity.ok(authenticationService.authenticate(request));
	}
}
