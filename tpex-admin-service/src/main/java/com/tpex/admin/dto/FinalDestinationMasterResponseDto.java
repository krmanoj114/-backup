package com.tpex.admin.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinalDestinationMasterResponseDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

			
	private List<BuyerMasterDTO> buyer;
	
	private List<CurrencyMasterDTO> currency;
}
