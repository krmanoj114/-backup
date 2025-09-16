package com.tpex.invoice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MixPrivilegeMstRequestDto {
	
	String privMstId;
	String dstCode;
	String crFmlyCode;
	String reExpCode;
	String etdFrom;
	String etdTo;
	String cmpCd;

}
