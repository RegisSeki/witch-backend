package com.rtseki.witch.backend.api.dto.response;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductResponseList {
	private PaginationDetails pageDetails;
	
	private List<ProductResponse> products;
}
