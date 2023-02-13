package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.ShopListRequest;
import com.rtseki.witch.backend.api.dto.response.ShopListResponse;
import com.rtseki.witch.backend.domain.model.ShopList;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class ShopListAssembler {
	
	private ModelMapper modelMapper;
	
	public ShopList toModel(ShopListRequest request) {
		return modelMapper.map(request, ShopList.class);
	}
	
	public ShopListResponse toResponse(ShopList shopList) {
		return modelMapper.map(shopList, ShopListResponse.class);
	}
}
