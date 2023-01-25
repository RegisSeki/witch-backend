package com.rtseki.witch.backend.api.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseList {
	private int pageNumber;
	private Long totalElements;
	private int pageSize;
	private int totalPages;
	
	private List<CategoryResponse> categories;
}
