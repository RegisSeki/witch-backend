package com.rtseki.witch.backend.api.assembler;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.rtseki.witch.backend.api.dto.request.ProductRequest;
import com.rtseki.witch.backend.api.dto.response.ProductResponse;
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
}
