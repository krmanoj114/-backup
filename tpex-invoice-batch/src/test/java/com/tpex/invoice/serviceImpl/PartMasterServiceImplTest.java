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

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.serviceimpl.PartMasterServiceImpl;

@ExtendWith(MockitoExtension.class)
class PartMasterServiceImplTest {

	@InjectMocks
	PartMasterServiceImpl partMasterServiceImpl;

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
        Assertions.assertDoesNotThrow(() -> partMasterServiceImpl.partMasterUploadJob("BINS123", "partMasterInputFile", oemProcessCtrlEntity, "2023/01/01", "User ID"));

	}
}
