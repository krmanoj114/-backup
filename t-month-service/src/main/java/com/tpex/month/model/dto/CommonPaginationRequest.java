package com.tpex.month.model.dto;

import javax.validation.Valid;

import lombok.Data;

@Data
public class CommonPaginationRequest<T> {

	private PaginationRequest pagination = new PaginationRequest();
	
	@Valid
	private T requestBody;
}
