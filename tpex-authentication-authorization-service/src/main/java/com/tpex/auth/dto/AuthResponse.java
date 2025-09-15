package com.tpex.auth.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {

	private String token;
	private String errorCd;
	private String errorMsg;
	private String uniqueName;
	private String userId;
	private String userFirstName;
	private String companyDefault;
	private List<CompanyDropdownDTO> companyDropdown;
	private List<FunctionPermissionDTO> functionList;
	
	public void setError(MessageErrorSC2 messageErrorSC2) {
		this.errorCd = messageErrorSC2.getMsgID();
		this.errorMsg = messageErrorSC2.getMsgDesc();
	}
}
