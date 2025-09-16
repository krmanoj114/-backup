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

import com.tpex.batchjob.binf016.Binf016;
import com.tpex.controller.SendInvoiceToIxosController;
import com.tpex.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;

@ExtendWith(MockitoExtension.class)
class SendInvoiceToIxosControllerTest {

	@InjectMocks
	SendInvoiceToIxosController sendInvoiceToIxosController;

	@Mock
	Binf016 binf016;

	@Test
	void jobAddressMasterTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));

		SendSeparateInvoiceToIXOSDto sendInvoiceToIXOSDtoo = new SendSeparateInvoiceToIXOSDto();
		sendInvoiceToIXOSDtoo.setBatchName("BINF016");
		sendInvoiceToIXOSDtoo.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		sendInvoiceToIXOSDtoo.setCompanyCode("TMT");
		sendInvoiceToIXOSDtoo.setInvoiceNo("KR23102179");
		sendInvoiceToIXOSDtoo.setUserId("User ID");

		ResponseEntity<String> result = sendInvoiceToIxosController
				.binf016SendInvioceToIXOS(sendInvoiceToIXOSDtoo);

		assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(result.getBody()).isNotNull();
	}
}

