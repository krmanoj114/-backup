package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeprateInvGenResponseDto {
	private String impCd;
	private String partNo;
	private String cfCd;
	private String series;
	private String modifiedPrivilage;
	private String originalPrivilage;
}
