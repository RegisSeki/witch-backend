package com.rtseki.witch.backend.api.assembler;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.ProductRequest;
import com.rtseki.witch.backend.api.dto.response.PaginationDetails;
import com.rtseki.witch.backend.api.dto.response.ProductResponse;
import com.rtseki.witch.backend.api.dto.response.ProductResponseList;
import com.rtseki.witch.backend.domain.model.Product;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ProductAssembler {

	private ModelMapper modelMapper;
	
	public Product toModel(ProductRequest request) {
		return modelMapper.map(request, Product.class);
	}
	
	public ProductResponse toResponse(Product product) {
		return modelMapper.map(product, ProductResponse.class);
	}
	
	public ProductResponseList toProductResponseList(Page<Product> products) {
		ProductResponseList result = new ProductResponseList();
		List<ProductResponse> productResponseList = new ArrayList<>();
		PaginationDetails pageDetails = new PaginationDetails();
		
		pageDetails.setPageNumber(products.getNumber());
		pageDetails.setTotalElements(products.getTotalElements());
		pageDetails.setPageSize(products.getSize());
		pageDetails.setTotalPages(products.getTotalPages());
		result.setPageDetails(pageDetails);

		for (Product product : products) {
			ProductResponse productResponse = modelMapper.map(product, ProductResponse.class);
			productResponseList.add(productResponse);
		}
		
		result.setProducts(productResponseList);
		
		return result;
	}
}
