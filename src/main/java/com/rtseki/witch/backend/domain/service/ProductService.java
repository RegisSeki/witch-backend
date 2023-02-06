package com.rtseki.witch.backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Product;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.repository.ProductRepository;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	@Autowired
	private SubcategoryService subcategoryService;
	
	@Transactional
	public Product create(Product product) {
		checkDuplicatedBarcode(product);
		checkDuplicatedName(product);
		loadSubcategoryData(product);
		return repository.save(product);
	}
	
	public Product findById(Long productId) {
		return repository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException(productId)); 
	}
	
	private void checkDuplicatedBarcode(Product product) {
		boolean isProductExist = repository.findByBarcode(product.getBarcode())
			.stream().anyMatch(existProduct -> !existProduct.equals(product));
		
		if(isProductExist) {
			throw new BusinessException("Product barcode is already register");
		}
	}
	
	private void checkDuplicatedName(Product product) {
		boolean isProductExist = repository.findByName(product.getName())
			.stream().anyMatch(existProduct -> !existProduct.equals(product));
		
		if(isProductExist) {
			throw new BusinessException("Product name is already taken");
		}
	}
	
	private void loadSubcategoryData(Product product) {
		Subcategory subcategory = null;
		try {
			subcategory = subcategoryService.findById(product.getSubcategory().getId());
		} catch(Exception e)  {
			throw new BusinessException(e.getMessage());
		}
		product.setSubcategory(subcategory);
	}
}
