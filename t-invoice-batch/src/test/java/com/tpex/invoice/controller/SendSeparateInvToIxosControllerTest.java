package com.tpex.invoice.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.tpex.batchjob.bins011.Bins011;
import com.tpex.controller.SendSeparateInvToIxosController;
import com.tpex.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;

@ExtendWith(MockitoExtension.class)
class SendSeparateInvToIxosControllerTest {

	@InjectMocks
	SendSeparateInvToIxosController sendSeparateInvToIxosController;

	@Mock
	Bins011 bins011;

	@Test
	void jobAddressMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		SendSeparateInvoiceToIXOSDto sendSeparateInvoiceToIXOSDtoo = new SendSeparateInvoiceToIXOSDto();
		sendSeparateInvoiceToIXOSDtoo.setBatchName("BINF125");
		sendSeparateInvoiceToIXOSDtoo.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		sendSeparateInvoiceToIXOSDtoo.setCompanyCode("TMT");
		sendSeparateInvoiceToIXOSDtoo.setInvoiceNo("KR23102179");
		sendSeparateInvoiceToIXOSDtoo.setUserId("User ID");

		ResponseEntity<String> result = sendSeparateInvToIxosController
				.bins011SendSeparateInvioceToIXOS(sendSeparateInvoiceToIXOSDtoo);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}
