package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.builder.SimpleStepBuilder;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.RepositoryItemWriter;

import com.tpex.batchjob.JobCompletionNotificationListener;
import com.tpex.batchjob.workplanmasteruploadfromvessel.WrkPlanMasterUploadFromVesselBookingBatchConfig;
import com.tpex.batchjob.workplanmasteruploadfromvessel.WrkPlanMasterUploadFromVesselBookingBatchProcessor;
import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
import com.tpex.repository.CustomVesselBookingRepository;
import com.tpex.repository.InvGenWorkPlanMstRepository;

@ExtendWith(MockitoExtension.class)
class UploadWorkPlanMasterFromVesselTest {

	@InjectMocks
	WrkPlanMasterUploadFromVesselBookingBatchConfig batchConf;
	
	@Mock
	private JobBuilderFactory jobBuilderFactory;
	@Mock
	private StepBuilderFactory stepBuilderFactory;
	@Mock
	private InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;
	@Mock
	private CustomVesselBookingRepository customVesselBookingRepository;
	@Mock
	private JobCompletionNotificationListener jobCompletionNotificationListener;
	@Mock
	private StepBuilder stepBuilder;
	@Mock
    private SimpleStepBuilder<Object, Object> simpleStepBuilder;
	
	@InjectMocks
	WrkPlanMasterUploadFromVesselBookingBatchProcessor processor;
	
	@SuppressWarnings("unchecked")
	@Test
	void wrkPlanMasterUploadFromVesselBookingBatchJobTest() {
		when(stepBuilderFactory.get(anyString())).thenReturn(stepBuilder);
		when(stepBuilder.chunk(anyInt())).thenReturn(simpleStepBuilder);
		when(simpleStepBuilder.reader(any(RepositoryItemReader.class))).thenReturn(simpleStepBuilder);
		when(simpleStepBuilder.processor(any(ItemProcessor.class))).thenReturn(simpleStepBuilder);
		when(simpleStepBuilder.writer(any(RepositoryItemWriter.class))).thenReturn(simpleStepBuilder);
		when(simpleStepBuilder.listener(any(StepExecutionListener.class))).thenReturn(simpleStepBuilder);
		
		assertDoesNotThrow(() -> batchConf.processWrkPlanMasterFromVesselBookingUploadBatch());
	}
	
	@Test
	void processorIns() {
		WrkPlanMasterUploadBatchInputDto in = mockInput();
		
		when(invGenWorkPlanMstRepository.findById(any(InvGenWorkPlanMstIdEntity.class))).thenReturn(Optional.empty());
		
		InvGenWorkPlanMstEntity res = processor.process(in);
		
		assertEquals("TestBookNo", res.getBookingNo());
	}

	@Test
	void processorUpd() {
		WrkPlanMasterUploadBatchInputDto in = mockInput();
		InvGenWorkPlanMstEntity ent = new InvGenWorkPlanMstEntity();
		InvGenWorkPlanMstIdEntity entId = new InvGenWorkPlanMstIdEntity(Date.valueOf(LocalDate.now()), "999A", "A,B");
		ent.setBookingNo("TestBookNo");
		ent.setLiner("Ship");
		ent.setEtd1(Date.valueOf(LocalDate.now()));
		ent.setEta1(Date.valueOf(LocalDate.now()));
		ent.setId(entId);
		
		Optional<InvGenWorkPlanMstEntity> opt = Optional.of(ent);
		
		when(invGenWorkPlanMstRepository.findById(any(InvGenWorkPlanMstIdEntity.class))).thenReturn(opt);
		
		InvGenWorkPlanMstEntity res = processor.process(in);
		
		assertEquals("999A", res.getId().getContDest());
	}
	
	private WrkPlanMasterUploadBatchInputDto mockInput() {
		WrkPlanMasterUploadBatchInputDto in = new WrkPlanMasterUploadBatchInputDto();
		in.setBookingNo("TestBookNo");
		in.setContDest("999A");
		in.setOriginalEtd("10/10/2010");
		in.setShipComp("SHIP");
		in.setRenbanCode("A,B");
		in.setEtd1("11/11/2011");
		in.setEta1("12/12/2012");
		return in;
	}
}
