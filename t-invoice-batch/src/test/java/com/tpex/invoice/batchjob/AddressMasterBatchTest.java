package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
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

import com.tpex.entity.AddressMasterEntity;
import com.tpex.entity.AddressMasterIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.AddressMasterRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class AddressMasterBatchTest
{
	
	@Autowired
	private JobLauncher addressMasterJobLauncher;
	
	@Autowired
	@Qualifier("addressMasterUploadJob")
	private Job addressMasterUploadJob;
 
	@Bean
	public JobLauncherTestUtils addressMasterUtils(JobRepository jobRepository, JobLauncher addressMasterJobLauncher) {
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJobLauncher(addressMasterJobLauncher);
		jobLauncherTestUtils.setJob(addressMasterUploadJob);
		return jobLauncherTestUtils;
	}
 
    @MockBean
    private TpexConfigRepository tpexConfigRepository;
    
	@MockBean
	private RddDownLocDtlRepository rddDownLocDtlRepository;
	
	@MockBean
	private JasperReportService jasperReportService;
	
	@MockBean
	private AddressMasterRepository addressMasterRepository;
	
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
		config1.setValue("classpath:AddressMasterExcelTest.json");
		
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(config1);

    	TpexConfigEntity config2 = new TpexConfigEntity();
		config2.setId(1);
		config2.setName("invoiceGeneration.report.directory");
		config2.setValue("classpath:");
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(config2);

    	TpexConfigEntity config3 = new TpexConfigEntity();
		config3.setId(1);
		config3.setName("lot.part.price.error");
		config3.setValue("classpath:AddressMasterErrorTest.jrxml");
    	Mockito.when(tpexConfigRepository.findByName("address.master.upload.error")).thenReturn(config3);
    	
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

		Mockito.when(addressMasterRepository.countByIdCmpCode(Mockito.anyString())).thenReturn(1L);

		AddressMasterEntity addressMasterEntity = new AddressMasterEntity(new AddressMasterIdEntity("434", "1", "TMT"),
				"Upload Test", "Upload Test", "Upload Test", "Upload Test", "Upload Test", 
				"1100001", "Test", "E023", "433", "301", "2147483647", "2345678", "shikha@gmail.com", "2345", "test", "Y",
				"N", "Test remark",	"Test empl1", "Test empl2", "Test empl3", null, null, null);
		Mockito.when(addressMasterRepository.save(Mockito.any())).thenReturn(addressMasterEntity);
		
		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("fileName", "AddressMasterTest.xlsx");
        paramsBuilder.addString("processControlId", "1");
        
        JobExecution jobExecution = addressMasterJobLauncher.run(addressMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
         
    }
    
    @Test
    void verifyJobErrorResults() throws Exception
    {
    	TpexConfigEntity config1 = new TpexConfigEntity();
		config1.setId(1);
		config1.setName("123");
		config1.setValue("classpath:AddressMasterExcelTest.json");
		
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(config1);

    	TpexConfigEntity config2 = new TpexConfigEntity();
		config2.setId(1);
		config2.setName("invoiceGeneration.report.directory");
		config2.setValue("classpath:");
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(config2);

    	TpexConfigEntity config3 = new TpexConfigEntity();
		config3.setId(1);
		config3.setName("lot.part.price.error");
		config3.setValue("classpath:AddressMasterErrorTest.jrxml");
    	Mockito.when(tpexConfigRepository.findByName("address.master.upload.error")).thenReturn(config3);
    	
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

		Mockito.when(addressMasterRepository.countByIdCmpCode(Mockito.anyString())).thenReturn(0L);

		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("fileName", "AddressMasterTestError.xlsx");
        paramsBuilder.addString("processControlId", "1");

        JobExecution jobExecution = addressMasterJobLauncher.run(addressMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
         
    }
    
}
