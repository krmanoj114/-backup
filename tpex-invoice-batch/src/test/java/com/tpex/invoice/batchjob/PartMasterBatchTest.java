package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
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

import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InhouseShopMasterRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class PartMasterBatchTest {

	@Autowired
	private JobLauncher partMasterJobLauncher;
	
	@Autowired
	@Qualifier("partMasterUploadJob")
	private Job partMasterUploadJob;
 
	@Bean
	public JobLauncherTestUtils partMasterUtils(JobRepository jobRepository, JobLauncher partMasterJobLauncher) {
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJobLauncher(partMasterJobLauncher);
		jobLauncherTestUtils.setJob(partMasterUploadJob);
		return jobLauncherTestUtils;
	}
 
    @MockBean
    private TpexConfigRepository tpexConfigRepository;
    
	@MockBean
	private RddDownLocDtlRepository rddDownLocDtlRepository;
	
	@MockBean
	private JasperReportService jasperReportService;
	
	@MockBean
	private PartMasterRespository partMasterRespository;
	
	@MockBean
	InhouseShopMasterRepository inhouseShopMasterRepository;
	
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
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(new TpexConfigEntity(1, "123", "classpath:PartMasterExcelTest.json", null));
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(new TpexConfigEntity(1, "invoiceGeneration.report.directory", "classpath:", null));
    	Mockito.when(tpexConfigRepository.findByName(Mockito.startsWith("jasper.report."))).thenReturn(new TpexConfigEntity(1, "1", "1", null));

		Mockito.when(partMasterRespository.findById(Mockito.any())).thenReturn(Optional.empty());
		Mockito.when(inhouseShopMasterRepository.countByInsShopCd(Mockito.any())).thenReturn(1L);
		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("fileName", "PartMasterUploadTest.xls");
        paramsBuilder.addString("processControlId", "1");
        
        JobExecution jobExecution = partMasterJobLauncher.run(partMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
         
    }
    
    @Test
    void verifyJobErrorResults() throws Exception
    {
    	Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(new TpexConfigEntity(1, "123", "classpath:PartMasterExcelTest.json", null));
    	Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(new TpexConfigEntity(1, "invoiceGeneration.report.directory", "classpath:", null));
    	Mockito.when(tpexConfigRepository.findByName(Mockito.startsWith("jasper.report."))).thenReturn(new TpexConfigEntity(1, "1", "1", null));

    	Mockito.when(partMasterRespository.findById(Mockito.any())).thenReturn(Optional.empty());
		Mockito.when(inhouseShopMasterRepository.countByInsShopCd(Mockito.any())).thenReturn(1L);

		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());
		
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null));
		
		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
        paramsBuilder.addString("batchName", "123");
        paramsBuilder.addString("companyCode", "TMT");
        paramsBuilder.addString("fileName", "PartMasterUploadError.xls");
        paramsBuilder.addString("processControlId", "1");

        JobExecution jobExecution = partMasterJobLauncher.run(partMasterUploadJob, paramsBuilder.toJobParameters());
        assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
         
    }

}
