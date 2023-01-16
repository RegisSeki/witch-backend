package com.rtseki.witch.backend.domain.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.rtseki.witch.backend.api.dto.request.AuthenticationRequest;
import com.rtseki.witch.backend.api.dto.response.AuthenticationResponse;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final UserService userService;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(User user) {

		User createdUser = userService.save(user); 
		
		var jwtToken = jwtService.generateToken(createdUser);
		
		return AuthenticationResponse.builder().token(jwtToken).build();
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
					request.getEmail(),
					request.getPassword()
				)
			);
		
		var user = userRepository.findByEmail(request.getEmail())
				.orElseThrow();
		
		var jwtToken = jwtService.generateToken(user);
		
		return AuthenticationResponse.builder().token(jwtToken).build();
	}
}
