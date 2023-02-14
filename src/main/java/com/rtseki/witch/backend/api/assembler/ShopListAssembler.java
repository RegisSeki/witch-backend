package com.rtseki.witch.backend.api.assembler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.ShopListRequest;
import com.rtseki.witch.backend.api.dto.response.PaginationDetails;
import com.rtseki.witch.backend.api.dto.response.ShopListResponse;
import com.rtseki.witch.backend.api.dto.response.ShopListResponseList;
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
	
	public ShopListResponseList toShopListResponseList(Page<ShopList> shopLists) {
		ShopListResponseList result = new ShopListResponseList();
		List<ShopListResponse> shopListResponseList = new ArrayList<>();
		PaginationDetails pageDetails = new PaginationDetails();
		
		pageDetails.setPageNumber(shopLists.getNumber());
		pageDetails.setTotalElements(shopLists.getTotalElements());
		pageDetails.setPageSize(shopLists.getSize());
		pageDetails.setTotalPages(shopLists.getTotalPages());
		result.setPageDetails(pageDetails);

		for (ShopList shopList : shopLists) {
			ShopListResponse shopListResponse = modelMapper.map(shopList, ShopListResponse.class);
			shopListResponseList.add(shopListResponse);
		}
		result.setShopLists(shopListResponseList);
		
		return result;
	}
}
