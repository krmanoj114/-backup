package com.tpex.admin.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import com.tpex.admin.dto.CPCProcessLogsResponseDto;
import com.tpex.admin.dto.CPCProcessStatusRequestDto;
import com.tpex.admin.dto.CPCProcessStatusResponseDto;
import com.tpex.admin.dto.CPCSubmitProcessRequestDTO;
import com.tpex.admin.dto.CPCSystemNamesResponseDto;

import net.sf.jasperreports.engine.JRException;

public interface CommonProcessControlService {

	void submitProcess(CPCSubmitProcessRequestDTO request);

	CPCProcessStatusResponseDto processStatus(CPCProcessStatusRequestDto request);

	List<CPCSystemNamesResponseDto> systemNames(String userId);

	/**
	 * @param processControlId
	 * @param processId
	 * @return
	 */
	List<CPCProcessLogsResponseDto> processLogs(String processControlId, String processId);

	/**
	 * @param processControlId
	 * @param processId
	 * @return
	 * @throws IOException 
	 * @throws JRException 
	 * @throws FileNotFoundException 
	 * @throws Exception
	 */
	Object downloadProcessLogs(String processControlId, String processId) throws JRException, IOException;

}
