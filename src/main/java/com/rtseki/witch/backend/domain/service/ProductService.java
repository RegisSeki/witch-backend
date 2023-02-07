package com.rtseki.witch.backend.domain.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rtseki.witch.backend.domain.exception.BusinessException;
import com.rtseki.witch.backend.domain.exception.ResourceNotFoundException;
import com.rtseki.witch.backend.domain.model.Product;
import com.rtseki.witch.backend.domain.model.Subcategory;
import com.rtseki.witch.backend.domain.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

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
	
	public Product update(Long productId, Product product) {
		checkDuplicatedBarcode(product, productId);
		checkDuplicatedName(product, productId);
		try {
			Product entity = repository.getReferenceById(productId);			
			updateProductData(entity, product);
			loadSubcategoryData(entity);
			return repository.save(entity);
		} catch(EntityNotFoundException e) {
			throw new ResourceNotFoundException(productId);
		}
	}
	
	private void checkDuplicatedBarcode(Product product) {
		boolean isProductExist = repository.findByBarcode(product.getBarcode())
			.stream().anyMatch(existProduct -> !existProduct.equals(product));
		
		if(isProductExist) {
			throw new BusinessException("Product barcode is already register");
		}
	}
	
	private void checkDuplicatedBarcode(Product product, Long productId) {
		Optional<Product> existedProduct = repository.findByBarcode(product.getBarcode());
		
		if(existedProduct.isPresent() && existedProduct.get().getId() != productId) {
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
	
	private void checkDuplicatedName(Product product, Long productId) {
		Optional<Product> existedProduct = repository.findByName(product.getName());
		
		if(existedProduct.isPresent() && existedProduct.get().getId() != productId) {
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
	
	private void updateProductData(Product entity, Product product) {
		entity.setSubcategory(product.getSubcategory());
		entity.setBarcode(product.getBarcode());
		entity.setName(product.getName());
		entity.setDescription(product.getDescription());
	}
}
