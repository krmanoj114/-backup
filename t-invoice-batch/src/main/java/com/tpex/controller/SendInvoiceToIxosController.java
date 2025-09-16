package com.tpex.controller;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.batchjob.binf016.Binf016;
import com.tpex.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoiceIxosBatch")
@CrossOrigin("tpex-dev.tdem.toyota-asia.com")
public class SendInvoiceToIxosController {
	@Autowired
	private Binf016 binf016;

	private static final String COMPANY_CODE = "companyCode";
	private static final String INV_NUM = "invNum";
	private static final String HAISEN_NO = "haisenNo";
	private static final String HAISEN_YEAR = "haisenYear";

	@PostMapping(path = "/binf016SendInvoiceToIXOS")
	public ResponseEntity<String> binf016SendInvioceToIXOS(
			@RequestBody SendSeparateInvoiceToIXOSDto sendInvoiceToIXOSDto) {

		JobParameters jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
				.addString(COMPANY_CODE, sendInvoiceToIXOSDto.getCompanyCode())
				.addString("batchId", sendInvoiceToIXOSDto.getBatchId())
				.addString("batchName", sendInvoiceToIXOSDto.getBatchName())
				.addString("systemName", sendInvoiceToIXOSDto.getSystemName())
				.addString(INV_NUM, sendInvoiceToIXOSDto.getInvoiceNo())
				.addString(HAISEN_NO, sendInvoiceToIXOSDto.getHaisenNo())
				.addString(HAISEN_YEAR, sendInvoiceToIXOSDto.getHaisenYearMonth()).toJobParameters();
		try {
			binf016.binf016JobRunTMT(jobParameters);
		} catch (Exception e) {
			e.printStackTrace();
		}


		return new ResponseEntity<>(ConstantUtils.BATCH_SUCCESS_MSG, HttpStatus.OK);
	}

}




