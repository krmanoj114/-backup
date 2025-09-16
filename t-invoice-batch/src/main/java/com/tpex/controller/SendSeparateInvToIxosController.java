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

import com.tpex.batchjob.bins011.Bins011;
import com.tpex.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoiceBatch")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class SendSeparateInvToIxosController {

	@Autowired
	private Bins011 bins011;

	private static final String COMPANY_CODE = "companyCode";
	private static final String INV_NUM = "invNum";

	@PostMapping(path = "/bins011SendSeparateInvioceToIXOS")
	public ResponseEntity<String> bins011SendSeparateInvioceToIXOS(
			@RequestBody SendSeparateInvoiceToIXOSDto sendSeparateInvoiceToIXOSDto) {
		
		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.addString(COMPANY_CODE, sendSeparateInvoiceToIXOSDto.getCompanyCode())
				.addString(ConstantUtils.JOB_P_BATCH_ID, sendSeparateInvoiceToIXOSDto.getBatchName())
				.addString(ConstantUtils.JOB_P_BATCH_NAME, sendSeparateInvoiceToIXOSDto.getBatchName())
				.addString(ConstantUtils.JOB_P_SYSTEM_NM, ConstantUtils.IXOS)
				.addString(INV_NUM, sendSeparateInvoiceToIXOSDto.getInvoiceNo()).toJobParameters();
		
		try {
			bins011.bins011JobRunTMT(jobParameters);
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			e.printStackTrace();
		}

		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}
