package com.tpex.admin.service;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.tpex.admin.repository.OemProcessCtrlRepository;
import com.tpex.admin.repository.OemProgDtlsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.admin.dto.ProcessBatchDTO;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.exception.InvalidInputParametersException;

import com.tpex.admin.util.ConstantUtils;


@Service
public class RetryServiceImpl implements RetryService{

	@Autowired
	OemProgDtlsRepository oemProgDtlsRepository;

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Map<String, Object> getBatchStatus(String userId,String batchId) throws InvalidInputParametersException{

		Map<String, Object> output = new HashMap<>();

		entityManager.clear();

		ProcessBatchDTO processBatchDTO = fetchProcessDetails(userId,batchId);

		if(processBatchDTO.getStatus().equals("Processing")) {
			throw new InvalidInputParametersException("exception");
		}else {
			output.put("batchStatus", processBatchDTO.getStatus());
			output.put("warningFlag", processBatchDTO.getParameter());
			return output;
		}

	}

	private ProcessBatchDTO fetchProcessDetails(String userId, String batchId) {

		OemProcessCtrlEntity entity = oemProcessCtrlRepository.findProcessControl(userId,batchId);

		ProcessBatchDTO dto = new ProcessBatchDTO();
		dto.setParameter(entity.getParameter());
		dto.setStatus(getProcessStatus(entity.getStatus()));

		return dto;
	}

	private String getProcessStatus(int status) {
		if (status == 1) {
			return ConstantUtils.STS_SUCCESS;
		}
		return status == 0 ? ConstantUtils.STS_ERROR : ConstantUtils.STS_PROCESSING;
	}
}
