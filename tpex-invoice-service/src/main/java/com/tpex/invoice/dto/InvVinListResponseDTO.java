package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvVinListResponseDTO {
	private String INV_NO;
	private String INV_DT;
	private String NAME;
	private String ADD_1;
	private String ADD_2;
	private String ADD_3;
	private String ADD_4;
	private String LOT_NO;
	private String INV_VIN_NO;
}
