package com.tpex.admin.service;

import java.util.List;
import java.util.Map;

import com.tpex.admin.dto.CodeMasterRequestDTO;
import com.tpex.admin.dto.FinalDestinationMasterResponseDto;
import com.tpex.admin.dto.ColumnsDTO;

/**
 * The Interface CommonCodeMasterService.
 */
public interface CommonCodeMasterService {

	List<Map<String, String>> codeMasterData(ColumnsDTO columnsModel, String cmpCd);

	void saveCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO);

	void deleteCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO);

	void updateCodeMaster(CodeMasterRequestDTO codeMasterRequestDTO);
	
	public FinalDestinationMasterResponseDto finalDestinationDtls(String cmpCd);
	
	List<Map<String, String>> codeMasterDataFdm(ColumnsDTO columnsModel);
	
	Map<String, Object> savePaymentTermMaster(CodeMasterRequestDTO codeMasterRequestDTO, boolean isSave);

}
