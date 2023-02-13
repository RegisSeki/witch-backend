package com.rtseki.witch.backend.domain.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.model.ShopList;
import com.rtseki.witch.backend.domain.model.Status;
import com.rtseki.witch.backend.domain.model.User;
import com.rtseki.witch.backend.domain.repository.ShopListRespository;

@Service
public class ShopListService {

	@Autowired
	private ShopListRespository repository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	public ShopList create(ShopList shopList) {
		User currentUser = userService.getCurrentUser();
		shopList.setUser(currentUser);
		shopList.setStatus(Status.OPEN);
		shopList.setCreatedAt(Instant.now());
		return repository.save(shopList);
	}
}
