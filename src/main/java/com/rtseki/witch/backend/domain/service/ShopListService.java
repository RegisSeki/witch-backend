package com.rtseki.witch.backend.domain.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.model.ShopList;
import com.rtseki.witch.backend.domain.model.Status;
import com.rtseki.witch.backend.domain.repository.ShopListRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShopListService {

	@Autowired
	private ShopListRepository repository;
	
	@Autowired
	private UserService userService;
	
	@Transactional
	public ShopList create(ShopList shopList) {
		shopList.setUser(userService.getCurrentUser());
		checkDuplicatedName(shopList);
		shopList.setStatus(Status.OPEN);
		shopList.setCreatedAt(Instant.now());
		return repository.save(shopList);
	}
	
	public Page<ShopList> findAll(Pageable pageable) {
		return repository.findAllByUser_Id(pageable, userService.getCurrentUser().getId());
	}
	
	private void checkDuplicatedName(ShopList shopList) {
		boolean isShopListExist = repository.findByNameAndUser_Id(shopList.getName(), shopList.getUser().getId())
			.stream().anyMatch(existShopList -> !existShopList.equals(shopList));
		
		if(isShopListExist) {
			throw new BusinessException("Shop List name is already taken");
		}
	}
}
