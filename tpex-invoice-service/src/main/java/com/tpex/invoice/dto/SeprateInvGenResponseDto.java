package com.tpex.invoice.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeprateInvGenResponseDto implements Serializable{
	private String impCd;
	private String partNo;
	private String cfCd;
	private String series;
	private String modifiedPrivilage;
	private String originalPrivilage;
}
