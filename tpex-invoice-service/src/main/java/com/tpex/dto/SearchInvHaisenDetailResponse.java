package com.tpex.dto;

import java.io.Serializable;
import java.util.List;

import com.tpex.invoice.dto.PortGrpObjDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchInvHaisenDetailResponse implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private List<SearchInvHaisenDetailResponseDto> searchInvHaisenDetailResponse;
	private List<PortGrpObjDto> portGrpObj;
}
