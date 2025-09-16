package com.tpex.invoice.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.launch.JobLauncher;

import com.tpex.dto.UploadPxpPriceMasterJobDto;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.serviceimpl.PxpPriceMasterServiceImpl;

@ExtendWith(MockitoExtension.class)
class PxpPriceMasterServiceImplTest {

	@InjectMocks
	PxpPriceMasterServiceImpl pxpPriceMasterServiceImpl;

	@Mock
	JobLauncher jobLauncher;
	
	@Test
	void jobLotPartPriceTest() throws Exception {
		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		id.setBatchId("batchId");
		id.setProcessControlId(1);
		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setUserId("User ID");
		oemProcessCtrlEntity.setId(id);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(LocalDateTime.now()));
		jobLauncher.run(Mockito.any(), Mockito.any());
		
		UploadPxpPriceMasterJobDto uploadPxpPriceMasterJobDto = new UploadPxpPriceMasterJobDto();
		uploadPxpPriceMasterJobDto.setBatchName("BINF107");
		uploadPxpPriceMasterJobDto.setEffectiveFromDate("2022/06");
		uploadPxpPriceMasterJobDto.setEffectiveToDate("2022/07");
		uploadPxpPriceMasterJobDto.setFileName("pxppriceinputfile");
		uploadPxpPriceMasterJobDto.setOemProcessCtrlEntity(oemProcessCtrlEntity);
		uploadPxpPriceMasterJobDto.setCompanyCode("TMT");
		uploadPxpPriceMasterJobDto.setUserId("User ID");
        Assertions.assertDoesNotThrow(() -> pxpPriceMasterServiceImpl.pxpPriceMasterUploadBatchJob(uploadPxpPriceMasterJobDto));

	}
}
