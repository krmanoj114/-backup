package com.tpex.month.model.dto;

import lombok.Data;

@Data
public class CommonPaginationResponse<T> {

	private PaginationResponse pagination;
	private T responseBody;
}
