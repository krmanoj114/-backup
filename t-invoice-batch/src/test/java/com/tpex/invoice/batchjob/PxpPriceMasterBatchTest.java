package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import com.tpex.entity.PartMasterEntity;
import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.PartPriceMasterRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.TpexConfigRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PxpPriceMasterBatchTest {
	
	@Autowired
	private JobLauncher pxpPriceMasterJobLauncher;
	
	@Autowired
	@Qualifier("pxpPriceMasterUploadJob")
	private Job pxpPriceMasterUploadJob;
 
	@Bean
	public JobLauncherTestUtils pxpPriceMasterUtils(JobRepository jobRepository, JobLauncher pxpPriceMasterJobLauncher) {
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJobLauncher(pxpPriceMasterJobLauncher);
		jobLauncherTestUtils.setJob(pxpPriceMasterUploadJob);
		return jobLauncherTestUtils;
	}
 
    @MockBean
    private TpexConfigRepository tpexConfigRepository;
	
	@MockBean
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@MockBean
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@MockBean
	OemProcessCtrlRepository oemProcessCtrlRepository;
	
	@MockBean
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	@MockBean
	private OemCurrencyMstRepository oemCurrencyMstRepository;
	
	@MockBean
	private PartMasterRespository partMasterRespository;
	
	@MockBean
	private NoemPackSpecRepository noemPackSpecRepository;
	
	@MockBean
	private PartPriceMasterRepository partPriceMasterRepository;
	
	@BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
 
    @Test
    void verifyJobResults() throws Exception
    {
    	when(tpexConfigRepository.findByName("123")).thenReturn(new TpexConfigEntity(1, "123", "classpath:PxpPriceMasterUploadTest.json", null));
    	when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(new TpexConfigEntity(1, "invoiceGeneration.report.directory", "classpath:", null));
    	
		when(oemCurrencyMstRepository.countByCrmCd(Mockito.any())).thenReturn(1L);
		when(carFamilyMastRepository.countByCarFmlyCode(Mockito.any())).thenReturn(1L);
		when(oemFnlDstMstRepository.countByFdDstCd(Mockito.any())).thenReturn(1L);

		when(partMasterRespository.findById(Mockito.any())).thenReturn(Optional.empty());
		
		List<String> packingSpecPartNoList = new ArrayList<>();
		packingSpecPartNoList.add("338200D61000");
		when(noemPackSpecRepository.findPartNoList(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(packingSpecPartNoList);

		when(partMasterRespository.countByPartNo(Mockito.any())).thenReturn(1L);

		List<String> packSpecPartNoList = new ArrayList<>();
		packSpecPartNoList.add("90162T000100");
		packSpecPartNoList.add("90179T000800");
		when(noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsModImpCd(Mockito.any(), Mockito.any())).thenReturn(packSpecPartNoList);
		
		when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
		when(partPriceMasterRepository.countByCurrencyCodeAndIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(0L);
		
		PartPriceMasterEntity partPriceMaster = new PartPriceMasterEntity();
		partPriceMaster.setId(new PartPriceMasterIdEntity("481W", "303D", "338200D61000", "202205"));
		partPriceMaster.setCmpCd("TMT");
		partPriceMaster.setEffToMonth("202205");
		partPriceMaster.setPartName("GASKET, EXHAUST PIPE");
		partPriceMaster.setCurrencyCode("USD");
		partPriceMaster.setPartPrice(2.55);
		
		when(partPriceMasterRepository.findMaxControlRecordByCurrencyAndCfcAndImpAndPartNo(
				Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(partPriceMaster);
		
		doNothing().when(partPriceMasterRepository).delete(Mockito.any());
		
		PartPriceMasterEntity partPriceMasterEntity = new PartPriceMasterEntity();
		partPriceMaster.setId(new PartPriceMasterIdEntity("481W", "303D", "338200D61000", "202205"));
		partPriceMaster.setCmpCd("TMT");
		partPriceMaster.setEffToMonth("202205");
		partPriceMaster.setPartName("GASKET, EXHAUST PIPE");
		partPriceMaster.setPartPrice(2.55);
		partPriceMaster.setCurrencyCode("USD");
		List<PartPriceMasterEntity> partPriceMasterEntities = new ArrayList<>();
		partPriceMasterEntities.add(partPriceMasterEntity);
		when(partPriceMasterRepository.findOldTimeControlRecords(Mockito.any(), Mockito.any(), Mockito.any(), 
				Mockito.any(), Mockito.any())).thenReturn(partPriceMasterEntities);
		
		PartPriceMasterEntity partPriceMstEntity = new PartPriceMasterEntity();
		partPriceMaster.setId(new PartPriceMasterIdEntity("481W", "303D", "338200D61000", "202206"));
		partPriceMaster.setCmpCd("TMT");
		partPriceMaster.setEffToMonth("202207");
		partPriceMaster.setPartName("GASKET, EXHAUST PIPE");
		partPriceMaster.setCurrencyCode("USD");
		partPriceMaster.setPartPrice(8.2);
		when(partPriceMasterRepository.save(partPriceMstEntity)).thenReturn(partPriceMstEntity);
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("effectiveFromDate", "2022/06");
        paramsBuilder.addString("effectiveToDate", "2022/07");
        paramsBuilder.addString("fileName", "PxpPriceMasterTest.xlsx");
        paramsBuilder.addString("processControlId", "1");
        
        JobExecution jobExecution = pxpPriceMasterJobLauncher.run(pxpPriceMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }
    
    @Test
    void verifyJobErrorResults() throws Exception
    {
    	when(tpexConfigRepository.findByName("123")).thenReturn(new TpexConfigEntity(1, "123", "classpath:PxpPriceMasterUploadTest.json", null));
    	when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(new TpexConfigEntity(1, "invoiceGeneration.report.directory", "classpath:", null));

    	when(oemCurrencyMstRepository.countByCrmCd(Mockito.any())).thenReturn(0L);
		when(carFamilyMastRepository.countByCarFmlyCode(Mockito.any())).thenReturn(0L);
		when(oemFnlDstMstRepository.countByFdDstCd(Mockito.any())).thenReturn(0L);
		
		PartMasterEntity partMasterEntity = new PartMasterEntity();
		partMasterEntity.setPartNo("338200D610");
		partMasterEntity.setPartName("GASKET, EXHAUST PIPE");
		when(partMasterRespository.findById(Mockito.any())).thenReturn(Optional.of(partMasterEntity));
		
		List<String> packingSpecPartNoList = new ArrayList<>();
		packingSpecPartNoList.add("338200D61000");
		when(noemPackSpecRepository.findPartNoList(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(packingSpecPartNoList);

		when(partMasterRespository.countByPartNo(Mockito.any())).thenReturn(0L);

		List<String> packSpecPartNoList = new ArrayList<>();
		packSpecPartNoList.add("90162T000100");
		packSpecPartNoList.add("90179T000800");
		when(noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsModImpCd(Mockito.any(), Mockito.any())).thenReturn(packSpecPartNoList);
		
		when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		when(processLogDtlsRepository.save(Mockito.any())).thenReturn(new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null));
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("effectiveFromDate", "202206");
        paramsBuilder.addString("effectiveToDate", "202207");
        paramsBuilder.addString("fileName", "PxpPriceMasterTestError.xlsx");
        paramsBuilder.addString("processControlId", "1");

        JobExecution jobExecution = pxpPriceMasterJobLauncher.run(pxpPriceMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
    }
    
}
