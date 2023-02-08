package com.rtseki.witch.backend.domain.service;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rtseki.witch.backend.commom.AuthenticationFacade;
import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.model.Role;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository repository;
	private final PasswordEncoder passwordEncoder;
	
	@Autowired
	private AuthenticationFacade auth;
	
	public User save(User user) {
			
		boolean isUsedEmail = repository.findByEmail(user.getEmail())
				.stream().anyMatch(existUser -> !existUser.equals(user));
		
		if (isUsedEmail) {
			throw new BusinessException("This email is already in use!");
		}
		
		User createdUser = new User();
				createdUser.setUserId(generateUserId());
				createdUser.setFirstname(user.getFirstname());
				createdUser.setLastname(user.getLastname());
				createdUser.setEmail(user.getEmail());
				createdUser.setPassword(passwordEncoder.encode(user.getPassword()));
				createdUser.setRole(Role.USER);
		
		try {
			repository.save(createdUser);
		}
		catch (ConstraintViolationException e) {
			throw new BusinessException("The parameters are not correct, check and try again!");
		}
		
		return createdUser;
	}
	
	private String generateUserId() {
		String userId;
		String existedUserId;
		
		do {
			userId = UUID.randomUUID().toString();
			
			existedUserId = repository.findByUserId(userId).toString();
		} while (existedUserId != null && userId == existedUserId);
			
		return userId;
	}
	
	public User getCurrentUser() {
		Authentication authentication = auth.getAuthentication();
		User currentUser = repository.findByEmailToCurrentUser(authentication.getName());
		return currentUser;
	}
}
