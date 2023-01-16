package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.UserRequest;
import com.rtseki.witch.backend.domain.model.User;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class UserAssembler {
	
	private ModelMapper modelMapper;

	public User toDto(UserRequest request) {
		return modelMapper.map(request, User.class);
	}
}
