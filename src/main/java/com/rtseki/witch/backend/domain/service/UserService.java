package com.rtseki.witch.backend.domain.service;

import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.model.Role;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.UserRepository;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	
	public User save(User user) {
			
		boolean isUsedEmail = userRepository.findByEmail(user.getEmail())
				.stream().anyMatch(existUser -> !existUser.equals(user));
		
		if (isUsedEmail) {
			throw new BusinessException("This email is already in use!");
		}
		
		User createdUser = User.builder()
				.userId(generateUserId())
				.firstname(user.getFirstname())
				.lastname(user.getLastname())
				.email(user.getEmail())
				.password(passwordEncoder.encode(user.getPassword()))
				.role(Role.USER)
				.build();
		
		try {
			userRepository.save(createdUser);
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
			
			existedUserId = userRepository.findByUserId(userId).toString();
		} while (existedUserId != null && userId == existedUserId);
			
		return userId;
	}
}
