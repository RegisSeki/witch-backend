package com.rtseki.witch.backend.api.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDetails {
	private int pageNumber;
	private Long totalElements;
	private int pageSize;
	private int totalPages;
}
