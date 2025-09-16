package com.tpex.invoice.dto;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RenbanGroupCodeMasterSaveRequestDto implements Serializable {
	
    private static final long serialVersionUID = 1L;
	
	private String contDstCd;
	private String etdFromDate;
	private String etdToDate;
    
	private List<GroupCodeDetailsDto> goupdIdDetails;
	
	private String updateBy;
	private String updateDt;
	private String companyCode;

}
