package com.rtseki.witch.backend.api.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponseList {
	private PaginationDetails pageDetails;
	
	private List<CategoryResponse> categories;
}
