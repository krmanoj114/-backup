package com.tpex.month.model.dto;

import lombok.Data;

@Data
public class PaginationRequest {
	
	private int pageNo = 1; //current page start at 1
	private int pageSize = 250; //default value if not present
	
	public int getPageNo() {
		return this.pageNo - 1; // remove 1 to use for offset
	}
}
