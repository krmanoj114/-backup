package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;

import com.tpex.entity.OemLotPartPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntity;
import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.LotPartPriceMasterRepository;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.repository.LotSizeMasterRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class LotPartPriceBatchTest
{
 
    @Autowired
	private JobLauncher lotPartPriceJobLauncher;
	
	@Autowired
	@Qualifier("lotPartPriceUploadBatchJob")
	private Job lotPartPriceUploadBatchJob;
 
	@Bean
	public JobLauncherTestUtils lotPartPriceUtils(JobRepository jobRepository, JobLauncher lotPartPriceJobLauncher) {
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJobLauncher(lotPartPriceJobLauncher);
		jobLauncherTestUtils.setJob(lotPartPriceUploadBatchJob);
		return jobLauncherTestUtils;
	}
 
    @MockBean
    private TpexConfigRepository tpexConfigRepository;
    
	@MockBean
	private RddDownLocDtlRepository rddDownLocDtlRepository;
	
	@MockBean
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@MockBean
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@MockBean
	private OemCurrencyMstRepository oemCurrencyMstRepository;
	
	@MockBean
	private LotSizeMasterRepository lotSizeMasterRepository;
	
	@MockBean
	private PartMasterRespository partMasterRespository;
	
	@MockBean
	private JasperReportService jasperReportService;
	
	@MockBean
	private NoemPackSpecRepository noemPackSpecRepository;
	
	@MockBean
	private LotPartPriceMasterRepository lotPartPriceMasterRepository;
	
	@MockBean
	private LotPriceMasterRepository lotPriceMasterRepository;
	
	@MockBean
	OemProcessCtrlRepository oemProcessCtrlRepository;
	
	@MockBean
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	@BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }
 
    @Test
    void verifyJobResults() throws Exception
    {
    	TpexConfigEntity config1 = new TpexConfigEntity();
		config1.setId(1);
		config1.setName("123");
		config1.setValue("classpath:LotPartPriceMasterExcelTest.json");
		
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(config1);

    	TpexConfigEntity config2 = new TpexConfigEntity();
		config2.setId(1);
		config2.setName("invoiceGeneration.report.directory");
		config2.setValue("classpath:");
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(config2);

    	TpexConfigEntity config3 = new TpexConfigEntity();
		config3.setId(1);
		config3.setName("lot.part.price.error");
		config3.setValue("classpath:LotPartPriceMasterError.jrxml");
    	Mockito.when(tpexConfigRepository.findByName("lot.part.price.error")).thenReturn(config3);
    	
    	TpexConfigEntity config4 = new TpexConfigEntity();
		config4.setId(1);
		config4.setName("1");
		config4.setValue("1");
    	Mockito.when(tpexConfigRepository.findByName(Mockito.startsWith("jasper.report."))).thenReturn(config4);

		HashMap<String, Object> map = new HashMap<>();
		map.put("message", "INFO_IN_1004");
		map.put("status", "offline");
		
		Mockito.doNothing().when(jasperReportService).getJasperReportDownloadOffline(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyInt(), Mockito.any());

		RddDownLocDtlEntity saveRddDownLocDtlEntity = new RddDownLocDtlEntity();
		saveRddDownLocDtlEntity.setReportId(0);
		saveRddDownLocDtlEntity.setStatus("Success");
		Mockito.when(jasperReportService.saveOfflineDownloadDetail(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(saveRddDownLocDtlEntity);

		Mockito.when(carFamilyMastRepository.countByCarFmlyCode(Mockito.anyString())).thenReturn(1L);
		Mockito.when(oemFnlDstMstRepository.countByFdDstCd(Mockito.anyString())).thenReturn(1L);
		Mockito.when(oemCurrencyMstRepository.countByCrmCd(Mockito.anyString())).thenReturn(1L);
		Mockito.when(partMasterRespository.countByPartNo(Mockito.anyString())).thenReturn(1L);
		Mockito.when(partMasterRespository.countByPartNoAndPartName(Mockito.anyString(), Mockito.anyString())).thenReturn(1L);
		Mockito.when(lotSizeMasterRepository.countByLotCode(Mockito.anyString())).thenReturn(1L);
		Mockito.when(noemPackSpecRepository.getCountUsingLotKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1L);
		Mockito.when(noemPackSpecRepository.getCountUsingLotPartKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(1L);

		List<String> noemVprPkgSpecEntities = new ArrayList<>();
		noemVprPkgSpecEntities.add("90178T001500");
		Mockito.when(noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsLotCdAndNvpsModImpCd(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(noemVprPkgSpecEntities);

		List<Integer> listOfQtyBox = new ArrayList<>();
		listOfQtyBox.add(1);
		Mockito.when(noemPackSpecRepository.findPckSpecDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(listOfQtyBox);

		Mockito.when(lotSizeMasterRepository.findById(Mockito.any())).thenReturn(Optional.empty());

		OemLotSizeMstEntity oemLotSizeMstEntity = new OemLotSizeMstEntity();
		oemLotSizeMstEntity.setLotSizeCode((double) 1);
		Mockito.when(lotSizeMasterRepository.findTopByLotModImpAndCarFamilyCodeAndLotCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(oemLotSizeMstEntity);

		OemLotPartPrcMstEntity oemLotPartPrcMstEntity = new OemLotPartPrcMstEntity("824W", "704B", "AP", "USD", "90178T001500", 10.2, null, null, "NUT, FLANGE", "202108", "202108", (double) 1, "TMT");
		Mockito.when(lotPartPriceMasterRepository.save(Mockito.any())).thenReturn(oemLotPartPrcMstEntity);
		
		OemLotPrcMstEntity oemLotPrcMstEntity = new OemLotPrcMstEntity("824W", "704B", "AP", "USD", 10.2, null, null, "202108", "202108", null, "TMT");
		Mockito.when(lotPriceMasterRepository.save(Mockito.any())).thenReturn(oemLotPrcMstEntity);
		
		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
        JobExecution jobExecution = lotPartPriceJobLauncher.run(lotPartPriceUploadBatchJob, defaultJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
         
    }
    
    @Test
    void verifyJobErrorResults() throws Exception
    {
    	TpexConfigEntity config1 = new TpexConfigEntity();
		config1.setId(1);
		config1.setName("123");
		config1.setValue("classpath:LotPartPriceMasterExcelTest.json");
		
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(config1);

    	TpexConfigEntity config2 = new TpexConfigEntity();
		config2.setId(1);
		config2.setName("invoiceGeneration.report.directory");
		config2.setValue("classpath:");
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(config2);

    	TpexConfigEntity config3 = new TpexConfigEntity();
		config3.setId(1);
		config3.setName("lot.part.price.error");
		config3.setValue("classpath:LotPartPriceMasterError.jrxml");
    	Mockito.when(tpexConfigRepository.findByName("lot.part.price.error")).thenReturn(config3);
    	
    	TpexConfigEntity config4 = new TpexConfigEntity();
		config4.setId(1);
		config4.setName("1");
		config4.setValue("1");
    	Mockito.when(tpexConfigRepository.findByName(Mockito.startsWith("jasper.report."))).thenReturn(config4);

		HashMap<String, Object> map = new HashMap<>();
		map.put("message", "INFO_IN_1004");
		map.put("status", "offline");
		
		Mockito.doNothing().when(jasperReportService).getJasperReportDownloadOffline(Mockito.anyList(), Mockito.anyString(), Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap(), Mockito.anyInt(), Mockito.any());

		RddDownLocDtlEntity saveRddDownLocDtlEntity = new RddDownLocDtlEntity();
		saveRddDownLocDtlEntity.setReportId(0);
		saveRddDownLocDtlEntity.setStatus("Success");
		Mockito.when(jasperReportService.saveOfflineDownloadDetail(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(saveRddDownLocDtlEntity);

		Mockito.when(carFamilyMastRepository.countByCarFmlyCode(Mockito.anyString())).thenReturn(0L);
		Mockito.when(oemFnlDstMstRepository.countByFdDstCd(Mockito.anyString())).thenReturn(0L);
		Mockito.when(oemCurrencyMstRepository.countByCrmCd(Mockito.anyString())).thenReturn(0L);
		Mockito.when(partMasterRespository.countByPartNo(Mockito.anyString())).thenReturn(0L);
		Mockito.when(partMasterRespository.countByPartNoAndPartName(Mockito.anyString(), Mockito.anyString())).thenReturn(0L);
		Mockito.when(lotSizeMasterRepository.countByLotCode(Mockito.anyString())).thenReturn(0L);
		Mockito.when(noemPackSpecRepository.getCountUsingLotKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(0L);
		Mockito.when(noemPackSpecRepository.getCountUsingLotPartKey(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(0L);

		List<String> noemVprPkgSpecEntities = new ArrayList<>();
		noemVprPkgSpecEntities.add("90178T001500");
		Mockito.when(noemPackSpecRepository.findPartNoByNvpsCfCdAndNvpsLotCdAndNvpsModImpCd(Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(noemVprPkgSpecEntities);

		List<Integer> listOfQtyBox = new ArrayList<>();
		listOfQtyBox.add(1);
		Mockito.when(noemPackSpecRepository.findPckSpecDetails(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn(listOfQtyBox);

		Mockito.when(lotSizeMasterRepository.findById(Mockito.any())).thenReturn(Optional.empty());

		OemLotSizeMstEntity oemLotSizeMstEntity = new OemLotSizeMstEntity();
		oemLotSizeMstEntity.setLotSizeCode((double) 1);
		Mockito.when(lotSizeMasterRepository.findTopByLotModImpAndCarFamilyCodeAndLotCode(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(oemLotSizeMstEntity);

		OemLotPartPrcMstEntity oemLotPartPrcMstEntity = new OemLotPartPrcMstEntity("824W", "704B", "AP", "USD", "90178T001500", 10.2, null, null, "NUT, FLANGE", "202108", "202108", (double) 1, "TMT");
		Mockito.when(lotPartPriceMasterRepository.save(Mockito.any())).thenReturn(oemLotPartPrcMstEntity);
		
		OemLotPrcMstEntity oemLotPrcMstEntity = new OemLotPrcMstEntity("824W", "704B", "AP", "USD", 10.2, null, null, "202108", "202108", null, "TMT");
		Mockito.when(lotPriceMasterRepository.save(Mockito.any())).thenReturn(oemLotPrcMstEntity);
		
		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
        JobExecution jobExecution = lotPartPriceJobLauncher.run(lotPartPriceUploadBatchJob, defaultJobParameters());
        assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
         
    }
    
    private JobParameters defaultJobParameters() {
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("effectiveDate", "2021/08");
        paramsBuilder.addString("fileName", "LotPartPriceMasterTest.xlsx");
        paramsBuilder.addString("processControlId", "1");
        return paramsBuilder.toJobParameters();
	}
 
}
