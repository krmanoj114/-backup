package com.tpex.invoice.batchjob;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

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

import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
import com.tpex.entity.ProcessLogDtlsEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.entity.TpexConfigEntity;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvGenWorkPlanMstRepository;
import com.tpex.repository.NoemCbMstRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.ProcessLogDtlsRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.DateUtil;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class WorkPlanMasterBatchTest {

	@Autowired
	private JobLauncher wrkPlanMasterJobLauncher;

	@Autowired
	@Qualifier("wrkPlanMasterUploadBatchJob")
	private Job wrkPlanMasterUploadBatchJob;

	@Bean
	public JobLauncherTestUtils wrkPlanMasterUtils(JobRepository jobRepository, JobLauncher wrkPlanMasterJobLauncher) {
		JobLauncherTestUtils jobLauncherTestUtils = new JobLauncherTestUtils();
		jobLauncherTestUtils.setJobRepository(jobRepository);
		jobLauncherTestUtils.setJobLauncher(wrkPlanMasterJobLauncher);
		jobLauncherTestUtils.setJob(wrkPlanMasterUploadBatchJob);
		return jobLauncherTestUtils;
	}

	@MockBean
	private TpexConfigRepository tpexConfigRepository;

	@MockBean
	private RddDownLocDtlRepository rddDownLocDtlRepository;

	@MockBean
	private JasperReportService jasperReportService;

	@MockBean
	private InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;

	@MockBean
	private NoemCbMstRepository noemCbMstRepository;

	@MockBean
	private OemPortMstRepository oemPortMstRepository;

	@MockBean
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@MockBean
	private ProcessLogDtlsRepository processLogDtlsRepository;
	
	private static final String DATE_FORMAT = "dd/MM/yyyy";


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
		config1.setValue("classpath:WrkPlanMasterExcelTest.json");

		Mockito.when(tpexConfigRepository.findByName("123")).thenReturn(config1);

		TpexConfigEntity config2 = new TpexConfigEntity();
		config2.setId(1);
		config2.setName("invoiceGeneration.report.directory");
		config2.setValue("classpath:");
		Mockito.when(tpexConfigRepository.findByName("invoiceGeneration.report.directory")).thenReturn(config2);

		TpexConfigEntity config3 = new TpexConfigEntity();
		config3.setId(1);
		config3.setName("work.plan.master.error");
		config3.setValue("classpath:WrkPlanMasterErrorTest.jrxml");
		Mockito.when(tpexConfigRepository.findByName("work.plan.master.error")).thenReturn(config3);

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

		Mockito.when(noemCbMstRepository.countByCbNm(Mockito.anyString())).thenReturn(1L);
		
		Mockito.when(oemPortMstRepository.countByCd(Mockito.anyString())).thenReturn(1L);
		
		Mockito.when(invGenWorkPlanMstRepository.getCountUsingKey(Mockito.any(),Mockito.anyString(),Mockito.anyString())).thenReturn(1L);
		
		//InvGenWorkPlanMstEntity entity = 
		//Mockito.when(invGenWorkPlanMstRepository.findById(Mockito.any()).thenReturn();
 
		
		Date originalEtd = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "30/05/9999");
		Date issueInvoiceDate = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "28/05/9999");
		Date etd1 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "03/06/9999");
		Date eta1 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "13/06/9999");
		Date etd2 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "04/06/9999");
		Date eta2 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "14/06/9999");
		Date etd3 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "05/06/9999");
		Date eta3 = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "15/06/9999");
		Date createDate = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "15/06/9999");
		Date updateDate = DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT, "15/06/9999");
		
		InvGenWorkPlanMstIdEntity idEntity = new 
				InvGenWorkPlanMstIdEntity(originalEtd,"812B","I, U, X");
		
		
		InvGenWorkPlanMstEntity wrkPlanMasterEntity = new InvGenWorkPlanMstEntity();
		wrkPlanMasterEntity.setId(idEntity);
		wrkPlanMasterEntity.setIssueInvoiceDate(issueInvoiceDate);
		wrkPlanMasterEntity.setEtd1(etd1);
		wrkPlanMasterEntity.setEta1(eta1);
		wrkPlanMasterEntity.setCont20(1);
		wrkPlanMasterEntity.setCont40(14);
		wrkPlanMasterEntity.setVessel1("GLEN CANYON");
		wrkPlanMasterEntity.setVoy1("012W");
		wrkPlanMasterEntity.setEtd2(etd2);
		wrkPlanMasterEntity.setEta2(eta2);
		wrkPlanMasterEntity.setVessel2("GLEN CANYON");	
		wrkPlanMasterEntity.setVoy2("012W");
		wrkPlanMasterEntity.setEtd3(etd3);	
		wrkPlanMasterEntity.setEta3(eta3);
		wrkPlanMasterEntity.setVessel3("TALASSA");
		wrkPlanMasterEntity.setVoy3("W056");
		wrkPlanMasterEntity.setFolderName("IMV");
		wrkPlanMasterEntity.setPortOfLoading("LCH");
		wrkPlanMasterEntity.setPortOfDischarge("KHI");
		wrkPlanMasterEntity.setCreateBy("TestUser");
		wrkPlanMasterEntity.setCreateDate(createDate);
		wrkPlanMasterEntity.setUpdateBy("TestUser");
		wrkPlanMasterEntity.setUpdateDate(updateDate);
		wrkPlanMasterEntity.setDsiTiNo("1");
		wrkPlanMasterEntity.setInvGenFlag(null);
		wrkPlanMasterEntity.setBroker("MOL");
		wrkPlanMasterEntity.setCompanyCode("TMT");
		
		
		Mockito.when(invGenWorkPlanMstRepository.save(Mockito.any())).thenReturn(wrkPlanMasterEntity);

		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);

		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
		paramsBuilder.addString("batchName", "123");
		paramsBuilder.addString("companyCode", "TMT");
		paramsBuilder.addString("fileName", "WorkPlanMasterTest.xlsx");
		paramsBuilder.addString("processControlId", "1");

		JobExecution jobExecution = wrkPlanMasterJobLauncher.run(wrkPlanMasterUploadBatchJob, paramsBuilder.toJobParameters());
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());

	}

	@Test
	void verifyJobErrorResults() throws Exception
	{
		TpexConfigEntity config1 = new TpexConfigEntity();
		config1.setId(1);
		config1.setName("123");
		config1.setValue("classpath:WrkPlanMasterExcelTest.json");

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
		Mockito.when(tpexConfigRepository.findByName("work.plan.master.error")).thenReturn(config3);

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

        Mockito.when(noemCbMstRepository.countByCbNm(Mockito.anyString())).thenReturn(0L);

		Mockito.when(oemProcessCtrlRepository.findByIdBatchIdAndIdProcessControlId(Mockito.anyString(), Mockito.anyInt())).thenReturn(new ArrayList<>());

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity(
				1, "1", "Upload Test", "Upload Test", "Upload Test", "1100001", 1, null, null, null, null);
		Mockito.when(processLogDtlsRepository.save(Mockito.any())).thenReturn(processLogDtlsEntity);

		JobParametersBuilder paramsBuilder = new JobParametersBuilder();
		paramsBuilder.addLong("startAt", System.currentTimeMillis());
		paramsBuilder.addString("batchName", "123");
		paramsBuilder.addString("companyCode", "TMT");
		paramsBuilder.addString("fileName", "WorkPlanMasterTestError.xlsx");
		paramsBuilder.addString("processControlId", "1");

		JobExecution jobExecution = wrkPlanMasterJobLauncher.run(wrkPlanMasterUploadBatchJob, paramsBuilder.toJobParameters());
		assertEquals(BatchStatus.FAILED, jobExecution.getStatus());

	}


}
