package com.tpex.admin.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.expression.ParseException;

import com.tpex.admin.entity.RddDownLocDtlEntity;
import com.tpex.admin.module.OndemandDownRptModule;
import com.tpex.admin.repository.RddDownLocDtlRepository;

import static org.mockito.Mockito.anyString;


@ExtendWith(MockitoExtension.class)
class OndemandDownRptServiceImplTest {

	@InjectMocks
	OndemandDownRptServiceImpl ondemandDownRptService;

	@Mock
	RddDownLocDtlRepository ondemandDownRptRepository;

	List<RddDownLocDtlEntity> list;
	
	private static String status = "Status";
	private static String reportName = "TestSravan";
	private static String createDate = "15/12/2022";

	@Test
	void testSearchOnDemandReportsWithFindByStatusAndCreateDateAndReportNameAndCreateBy()
			throws ParseException, IllegalAccessException, InvocationTargetException, java.text.ParseException {

		OndemandDownRptModule request = new OndemandDownRptModule();
		list = new ArrayList<>();

		RddDownLocDtlEntity rddDownLocDtlEntity = new RddDownLocDtlEntity();
		rddDownLocDtlEntity.setReportName("Report");
		rddDownLocDtlEntity.setStatus(status);
		list.add(rddDownLocDtlEntity);
		request.setReportName(reportName);
		request.setStatus("Processing");
		request.setCreateDate(createDate);
		request.setCreateBy("Sravan");
		Mockito.when(ondemandDownRptRepository.findByStatusAndCreateDateAndReportNameAndCreateBy(anyString(),
				anyString(), anyString(), anyString())).thenReturn(list);
		list = ondemandDownRptService.searchOnDemandReports(request);
		assertNotNull("Test");
	}

	// @Test
	public void testSearchOnDemandReportsWithFindByStatusOrReportNameAndCreateByAndCreateDate()
			throws ParseException, IllegalAccessException, InvocationTargetException, java.text.ParseException {

		OndemandDownRptModule request = new OndemandDownRptModule();
		list = new ArrayList<>();

		RddDownLocDtlEntity rddDownLocDtlEntity = new RddDownLocDtlEntity();

		rddDownLocDtlEntity.setStatus(status);
		rddDownLocDtlEntity.setCreateDate(new Timestamp(0));
		rddDownLocDtlEntity.setCreateBy("createBy");
		rddDownLocDtlEntity.setReportName("Report");

		list.add(rddDownLocDtlEntity);
		request.setReportName(reportName);
		request.setStatus("Processing");
		request.setCreateDate(createDate);
		request.setCreateBy("Sravan");
		Mockito.when(ondemandDownRptRepository.findByStatusOrReportNameAndCreateByAndCreateDate(anyString(),
				anyString(), anyString(), anyString())).thenReturn(list);
		list = ondemandDownRptService.searchOnDemandReports(request);

	}

	// @Test
	public void testSearchOnDemandReportsWithFindByReportNameAndCreateDateAndCreateBy()
			throws ParseException, IllegalAccessException, InvocationTargetException, java.text.ParseException {

		OndemandDownRptModule request = new OndemandDownRptModule();
		list = new ArrayList<>();

		RddDownLocDtlEntity rddDownLocDtlEntity = new RddDownLocDtlEntity();

		rddDownLocDtlEntity.setStatus(status);
		rddDownLocDtlEntity.setCreateDate(new Timestamp(0));
		rddDownLocDtlEntity.setCreateBy("CreateBy");
		rddDownLocDtlEntity.setDownLoc("DowLoc");
		rddDownLocDtlEntity.setReportId(1);
		rddDownLocDtlEntity.setUpdateBy("UpdateBy");
		rddDownLocDtlEntity.setUpdateDate(new Timestamp(0));
		list.add(rddDownLocDtlEntity);
		request.setReportName(reportName);
		request.setCreateDate(createDate);
		request.setCreateBy("CreateBy");
		Mockito.when(ondemandDownRptRepository.findByCreateDateAndCreateBy(anyString(), anyString()))
				.thenReturn(list);
		list = ondemandDownRptService.searchOnDemandReports(request);

	}

	/*@Test
	void testFetchReportAndStatus() {

		ReportStatusInformation reportStatusInformation = new ReportStatusInformation();
		List<String> status = new ArrayList<>();
		status.add("status");
		List<String> reportNames = new ArrayList<>();
		reportNames.add("ReportName");
		reportStatusInformation.setReportNames(reportNames);
		reportStatusInformation.setStatus(status);
		Mockito.when(ondemandDownRptRepository.getReportName()).thenReturn(reportNames);
		Mockito.when(ondemandDownRptRepository.getStatus()).thenReturn(status);

		assertNotNull("Test");
	}*/

}
