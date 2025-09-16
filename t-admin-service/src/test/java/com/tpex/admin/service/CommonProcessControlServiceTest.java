package com.tpex.admin.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyList;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.assertj.core.api.ByteArrayAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.admin.dto.CPCProcessList;
import com.tpex.admin.dto.CPCProcessLogsResponseDto;
import com.tpex.admin.dto.CPCProcessStatusRequestDto;
import com.tpex.admin.dto.CPCProcessStatusResponseDto;
import com.tpex.admin.dto.CPCSystemNamesResponseDto;
import com.tpex.admin.dto.CommonDropdownDto;
import com.tpex.admin.entity.FinalDestEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
import com.tpex.admin.entity.ProcessLogDtlsEntity;
import com.tpex.admin.entity.SystemDtlsEntity;
import com.tpex.admin.entity.SystemDtlsIdEntity;
import com.tpex.admin.entity.TpexConfigEntity;
import com.tpex.admin.exception.InvalidInputParametersException;
import com.tpex.admin.repository.FinalDestRepository;
import com.tpex.admin.repository.OemProcessCtrlRepository;
import com.tpex.admin.repository.ProcessLogDtlsRepository;
import com.tpex.admin.repository.SystemDtlsRepository;
import com.tpex.admin.repository.TpexConfigRepository;



@ExtendWith(MockitoExtension.class)
class CommonProcessControlServiceTest {

	@Mock
	private SystemDtlsRepository systemDtlsRepository;

	@Mock
	private OemProcessCtrlRepository oemProcessCtrlRepository;

	@InjectMocks
	private CommonProcessControlServiceImpl commonProcessControlServiceImpl;

	@Mock
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Mock
	TpexConfigRepository tpexConfigRepository;

	@Mock
	private JasperReportServiceImpl reportServiceImpl;
	
	@Mock
	private FinalDestRepository finalDestRepository;
	
	private static String invoiceFromIxos = "Invoice from IXOS";
	private static String submitTime = "2023-03-09 00:00:00";
	private static String batchId = "D00001";


	@BeforeEach
	void setUp(){
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void systemNamesTest() {
		List<SystemDtlsEntity> systemDtlsEntityList = new ArrayList<>();

		SystemDtlsEntity systemDtlsEntity = new SystemDtlsEntity();
		systemDtlsEntity.setId(new SystemDtlsIdEntity("TMT", "TPEX"));

		systemDtlsEntityList.add(systemDtlsEntity);

		Mockito.when(systemDtlsRepository.findAllByIdCompanyCdOrderByIdSystemNameAsc(anyString()))
				.thenReturn(systemDtlsEntityList);

		List<CPCSystemNamesResponseDto> systemNamesList = commonProcessControlServiceImpl.systemNames("ROEM");

		assertThat(systemNamesList).isNotNull();
		

	}

	@Test
	void processStatusTest() throws InvalidInputParametersException {

		List<OemProcessCtrlEntity> oemProcessCtrlEntityList = new ArrayList<>();

		OemProcessCtrlEntity oemProcessCtrlEntity = new OemProcessCtrlEntity();
		oemProcessCtrlEntity.setId(new OemProcessCtrlIdEntity(batchId, 1));
		oemProcessCtrlEntity.setProgramId(batchId);
		oemProcessCtrlEntity.setProgramName(invoiceFromIxos);
		oemProcessCtrlEntity.setSubmitTime(Timestamp.valueOf(submitTime));
		oemProcessCtrlEntity.setStatus(1);
		oemProcessCtrlEntity.setSystemName("TPEX");
		oemProcessCtrlEntity.setUserId("ROEM");

		oemProcessCtrlEntityList.add(oemProcessCtrlEntity);

		CPCProcessStatusRequestDto processStatusRequestDto = new CPCProcessStatusRequestDto();
		processStatusRequestDto.setFromDateTime("09/03/2023_00:00");
		processStatusRequestDto.setEndDateTime("10/03/2023_00:00");
		processStatusRequestDto.setUserId("ROEM");

		CPCProcessStatusResponseDto processStatusResponseDto = new CPCProcessStatusResponseDto();
		processStatusResponseDto.setHasAdminRole(true);

		List<CPCProcessList> processList = new ArrayList<>();
		CPCProcessList cpcProcess = new CPCProcessList();
		cpcProcess.setProcessControlId("1");
		cpcProcess.setProcessId(batchId);
		cpcProcess.setProcessName(invoiceFromIxos);
		cpcProcess.setOwner("ROEM");
		cpcProcess.setProcessSubmitTime(Timestamp.valueOf(submitTime));
		cpcProcess.setStatus("Success");
		cpcProcess.setSystemName("TPEX");
		processList.add(cpcProcess);

		processStatusResponseDto.setProcessList(processList);

		Mockito.when(oemProcessCtrlRepository.findAllBySubmitTimeBetween(any(), any()))
				.thenReturn(oemProcessCtrlEntityList);

		CPCProcessStatusResponseDto cpcProcessStatusResponseDto = commonProcessControlServiceImpl
				.processStatus(processStatusRequestDto);

		assertThat(cpcProcessStatusResponseDto).isNotNull();
		assertEquals(processStatusResponseDto, cpcProcessStatusResponseDto);

		processStatusRequestDto.setProcessId(batchId);
		Mockito.when(oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndProgramId(any(), any(),
				any())).thenReturn(oemProcessCtrlEntityList);

		CPCProcessStatusResponseDto cpcProcessStatusResponseDto1 = commonProcessControlServiceImpl
				.processStatus(processStatusRequestDto);

		assertThat(cpcProcessStatusResponseDto1).isNotNull();
		assertEquals(processStatusResponseDto, cpcProcessStatusResponseDto1);

		processStatusRequestDto.setProcessId(null);
		processStatusRequestDto.setSystemName("TPEX");
		Mockito.when(oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndSystemName(any(),any(),
				any())).thenReturn(oemProcessCtrlEntityList);

		CPCProcessStatusResponseDto cpcProcessStatusResponseDto2 = commonProcessControlServiceImpl
				.processStatus(processStatusRequestDto);

		assertThat(cpcProcessStatusResponseDto2).isNotNull();
		assertEquals(processStatusResponseDto, cpcProcessStatusResponseDto2);

		processStatusRequestDto.setProcessId(batchId);
		processStatusRequestDto.setSystemName("TPEX");
		Mockito.when(oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndProgramIdAndSystemName(any(),
				any(), any(), any())).thenReturn(oemProcessCtrlEntityList);

		CPCProcessStatusResponseDto cpcProcessStatusResponseDto3 = commonProcessControlServiceImpl
				.processStatus(processStatusRequestDto);

		assertThat(cpcProcessStatusResponseDto3).isNotNull();
		assertEquals(processStatusResponseDto, cpcProcessStatusResponseDto3);
	}

	@Test
	void processLogsTest() throws Exception {

		List<ProcessLogDtlsEntity> processLogDtlsEntityList = new ArrayList<>();

		ProcessLogDtlsEntity processLogDtlsEntity = new ProcessLogDtlsEntity();
		processLogDtlsEntity.setId(1);
		processLogDtlsEntity.setProcessControlId(1);
		processLogDtlsEntity.setProcessId(batchId);
		processLogDtlsEntity.setCorrelationId("95425b87e917f808");
		processLogDtlsEntity.setProcessName(invoiceFromIxos);
		processLogDtlsEntity.setProcessLogMessage(null);
		processLogDtlsEntity.setProcessLogStatus("Info");
		processLogDtlsEntity.setProcessLogDatetime(Timestamp.valueOf(submitTime));

		processLogDtlsEntityList.add(processLogDtlsEntity);

		List<CPCProcessLogsResponseDto> cpcProcessLogsResponseDtoList = new ArrayList<>();
		CPCProcessLogsResponseDto cpcProcessLogsResponseDto = new CPCProcessLogsResponseDto();
		cpcProcessLogsResponseDto.setProcessId(batchId);
		cpcProcessLogsResponseDto.setProcessName(invoiceFromIxos);
		cpcProcessLogsResponseDto.setLogTime(Timestamp.valueOf(submitTime));
		cpcProcessLogsResponseDto.setStatus("Info");
		cpcProcessLogsResponseDto.setCorrelationId("95425b87e917f808");
		cpcProcessLogsResponseDto.setLogMessage(null);
		cpcProcessLogsResponseDto.setProcessLogId(1);
		cpcProcessLogsResponseDto.setRequestId("1");
		cpcProcessLogsResponseDtoList.add(cpcProcessLogsResponseDto);

		Mockito.when(
				processLogDtlsRepository.findAllByProcessControlIdAndProcessId(anyInt(), anyString()))
				.thenReturn(processLogDtlsEntityList);

		List<CPCProcessLogsResponseDto> cpcProcessLogsResponse = commonProcessControlServiceImpl.processLogs("1",
				batchId);

		assertThat(cpcProcessLogsResponse).isNotNull();
		assertEquals(cpcProcessLogsResponse, cpcProcessLogsResponseDtoList);

	}

	@Test
	void downloadProcessLogsTest() throws Exception {

		TpexConfigEntity config = new TpexConfigEntity();
		config.setId(1);
		config.setName("process.log.details.download.file.template.name");
		config.setValue("1");

		Mockito.when(tpexConfigRepository.findByName(anyString())).thenReturn(config);

		HashMap<String, Object> map = new HashMap<>();
		map.put("outStream", ByteArrayAssert.class );
		map.put("fileName", "Process Log Details_15032023_211550");

		Mockito.when(reportServiceImpl.getJasperReportDownloadOnline(anyList(), anyString(),
				anyString(), anyMap(), anyMap())).thenReturn(map);

		Object downloadProcessLogsObj = commonProcessControlServiceImpl.downloadProcessLogs("1", batchId);

		assertThat(downloadProcessLogsObj).isNotNull();
		assertEquals(map, downloadProcessLogsObj);

	}
	
	@Test
	void fetchDestCodeAndDestNameTest(){
		List<FinalDestEntity> destList = new ArrayList<>();

		FinalDestEntity finalDestEntity = new FinalDestEntity();
		finalDestEntity.setDestinationCd("0005");
		finalDestEntity.setDestinationName("CAMBODIA");
		destList.add(finalDestEntity);
		Mockito.when(finalDestRepository.findAllByOrderByDestinationCdAsc()).thenReturn(destList);
		
		List<CommonDropdownDto> result = commonProcessControlServiceImpl.fetchDestCodeAndDestName();
		assertThat(result).isNotNull();
	
	}
}
