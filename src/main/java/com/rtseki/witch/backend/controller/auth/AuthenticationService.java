package com.rtseki.witch.backend.controller.auth;

import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rtseki.witch.backend.config.JwtService;
import com.rtseki.witch.backend.model.user.Role;
import com.rtseki.witch.backend.model.user.User;
import com.rtseki.witch.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse register(RegisterRequest request) {
		var user = User.builder()
				.userId(UUID.randomUUID().toString())
				.firstname(request.getFirstname())
				.lastname(request.getLastname())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();
		
		userRepository.save(user);
		var jwtToken = jwtService.generateToken(user);
		
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