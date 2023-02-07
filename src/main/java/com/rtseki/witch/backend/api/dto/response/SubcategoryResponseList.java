package com.rtseki.witch.backend.api.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubcategoryResponseList {
	private PaginationDetails pageDetails;
	
	private List<SubcategoryResponse> subcategories;
}
