package com.tpex.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.batchjob.psins036.Psins036;
import com.tpex.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/seprateInvoiceBatch")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class SeprateInvoiceController {
	@Autowired
	private Psins036 psins036;

	@PostMapping(path = "/psins036SendSeparateInvioce")
	public ResponseEntity<String> psins036SendSeparateInvioce(
			@RequestBody SendSeparateInvoiceToIXOSDto sendSeparateInvoiceToIXOSDto) {

		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.addString(ConstantUtils.BATCH_INVOICE_NO, sendSeparateInvoiceToIXOSDto.getInvoiceNo())
				.addString(ConstantUtils.JOB_P_USER_ID, sendSeparateInvoiceToIXOSDto.getUserId())				
				.addString(ConstantUtils.COMPANY_CODE, sendSeparateInvoiceToIXOSDto.getCompanyCode())
				.addString(ConstantUtils.JOB_P_BATCH_ID, sendSeparateInvoiceToIXOSDto.getBatchName())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, sendSeparateInvoiceToIXOSDto.getBatchName())
				.addString(ConstantUtils.JOB_P_SYSTEM_NM, ConstantUtils.IXOS)
				.toJobParameters();	
		
		try {
			psins036.psins036JobRunTMT(jobParameters);
			
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}
