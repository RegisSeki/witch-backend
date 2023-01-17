package com.rtseki.witch.backend.domain.service;

import org.springframework.stereotype.Service;

import com.rtseki.witch.backend.domain.model.Category;
import com.rtseki.witch.backend.domain.repository.CategoryRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CategoryService {

	private CategoryRepository categoryRepository;
	
	public Category create(Category category) {
		return categoryRepository.save(category);
	}
}
