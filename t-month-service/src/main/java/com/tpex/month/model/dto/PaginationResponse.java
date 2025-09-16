package com.tpex.month.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaginationResponse {

	private int pageNo; //current page start at 1
	private int pageSize;
	private int totalPages;
	private long totalItems;
	
	public int getPageNo() {
		return this.pageNo + 1; // add 1 to use for front-end
	}
}
