package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvVinListResponseDTO {
	private String invNo;
	private String invDt;
	private String name;
	private String add1;
	private String add2;
	private String add3;
	private String add4;
	private String lotNo;
	private String invVinNo;
}
