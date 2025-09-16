package com.tpex.admin.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.tpex.admin.repository.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.tpex.admin.dto.CPCProcessList;
import com.tpex.admin.dto.CPCProcessLogsResponseDto;
import com.tpex.admin.dto.CPCProcessStatusRequestDto;
import com.tpex.admin.dto.CPCProcessStatusResponseDto;
import com.tpex.admin.dto.CPCSubmitProcessRequestDTO;
import com.tpex.admin.dto.CPCSystemNamesResponseDto;
import com.tpex.admin.dto.CommonDropdownDto;
import com.tpex.admin.dto.SendSeparateInvoiceToIXOSDto;
import com.tpex.admin.entity.FinalDestEntity;
import com.tpex.admin.entity.HaisenNoEntity;
import com.tpex.admin.entity.OemProcessCtrlEntity;
import com.tpex.admin.entity.OemProcessCtrlIdEntity;
import com.tpex.admin.entity.ProcessLogDtlsEntity;
import com.tpex.admin.entity.SystemDtlsEntity;
import com.tpex.admin.exception.InvalidInputParametersException;

import com.tpex.admin.util.ConstantUtils;
import com.tpex.admin.util.DateUtil;

import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;

@RequiredArgsConstructor
@Service
public class CommonProcessControlServiceImpl implements CommonProcessControlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(CommonProcessControlServiceImpl.class);

	private final JobLauncher jobLauncher;
	@Autowired
	@Qualifier("dummyJob")
	private final Job dummyJob;

	@Autowired
	private OemProcessCtrlRepository oemProcessCtrlRepository;

	@Autowired
	private SystemDtlsRepository systemDtlsRepository;

	@Autowired
	private ProcessLogDtlsRepository processLogDtlsRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private FinalDestRepository finalDestRepository;

	@Autowired
	private InvNoRepository invNoRepository;

	@Autowired
	private HaisenNumberRepository haisenNumberRepository;
	

	@Value("${invoice.batch.service.host}")
	String invoiceBatchServiceHost;

	@Autowired
	private WebClient webClient;
	

	@Async
	@Override
	public void submitProcess(CPCSubmitProcessRequestDTO request) {

		StringBuilder batchParameters = new StringBuilder();
		extractedRequestParameters(request, batchParameters);
		extracted(request, batchParameters);
		
		JobParameters jobParameters = null;
		if (!("BINS011".equals(request.getProcessId())) && !("PSINS036".equals(request.getProcessId()))) {
			OemProcessCtrlEntity oemProcessCtrlEntity = saveProcessDetails(request.getUserId(),
					batchParameters.toString(), "TPEX", request.getProcessId(), request.getProcessName());
			jobParameters = new JobParametersBuilder().addLong("startAt", System.currentTimeMillis())
					.addString("batchName", request.getProcessId()).addString("batchId", request.getProcessId())
					.addString("parameters", batchParameters.toString())
					.addString("processControlId", Integer.toString(oemProcessCtrlEntity.getId().getProcessControlId()))
					.addString("processId", request.getProcessId()).addString("processName", request.getProcessName())
					// Need to change when processing actual batch jobs
					.addString("fileName",
							tpexConfigRepository.findByName("submit.process.default.filename").getValue())
					.addString("filePath",
							tpexConfigRepository.findByName("submit.process.default.filepath").getValue())
					.toJobParameters();
		}
		
		
		try {
			SendSeparateInvoiceToIXOSDto sendSeparateInvoiceToIXOSDto = new SendSeparateInvoiceToIXOSDto();
			sendSeparateInvoiceToIXOSDto.setBatchName(request.getProcessId());
			sendSeparateInvoiceToIXOSDto.setUserId(request.getUserId());
			if ("BINS011".equals(request.getProcessId())) {
				sendSeparateInvoiceToIXOSDto.setInvoiceNo(request.getParameters().get(ConstantUtils.INVOICE_NO));
				sendSeparateInvoiceToIXOSDto.setCompanyCode(ConstantUtils.COMPANYNAME);
				webClient.post().uri(invoiceBatchServiceHost + "/invoiceBatch/bins011SendSeparateInvioceToIXOS")
						.bodyValue(sendSeparateInvoiceToIXOSDto).retrieve().bodyToMono(String.class).block();
			}else if("BINF023".equals(request.getProcessId())){
				sendSeparateInvoiceToIXOSDto.setVanMonth(request.getParameters().get(ConstantUtils.VANNING_MONTH).replace("/", ""));
				webClient.post().uri(invoiceBatchServiceHost + "/invoice/sendMonthlyContToBroker?vanMonth=" + sendSeparateInvoiceToIXOSDto.getVanMonth());

			}else if("BINF016".equals(request.getProcessId())){
				sendSeparateInvoiceToIXOSDto.setInvoiceNo(request.getParameters().get(ConstantUtils.INVOICE_NO));
				sendSeparateInvoiceToIXOSDto.setCompanyCode(ConstantUtils.COMPANYNAME);
				sendSeparateInvoiceToIXOSDto.setHaisenNo(request.getParameters().get(ConstantUtils.HAISEN_NO));
				sendSeparateInvoiceToIXOSDto.setHaisenYearMonth(request.getParameters().get(ConstantUtils.HAISEN_YEAR_MONTH).replace("/", ""));
				sendSeparateInvoiceToIXOSDto.setSystemName(ConstantUtils.SYSTEM_NAME);
				sendSeparateInvoiceToIXOSDto.setBatchId(request.getProcessId());
				sendSeparateInvoiceToIXOSDto.setBatchName(request.getProcessName());
				webClient.post().uri(invoiceBatchServiceHost + "/invoiceIxosBatch/binf016SendInvoiceToIXOS")
						.bodyValue(sendSeparateInvoiceToIXOSDto).retrieve().bodyToMono(String.class).block();

			}else if("PSINS036".equals(request.getProcessId())){
				sendSeparateInvoiceToIXOSDto.setInvoiceNo(request.getParameters().get(ConstantUtils.INVOICE_NO));
				sendSeparateInvoiceToIXOSDto.setUserId(request.getUserId());
				sendSeparateInvoiceToIXOSDto.setBatchName(request.getProcessId());
				sendSeparateInvoiceToIXOSDto.setCompanyCode(ConstantUtils.COMPANYNAME);
				webClient.post().uri(invoiceBatchServiceHost + "/seprateInvoiceBatch/psins036SendSeparateInvioce")
				.bodyValue(sendSeparateInvoiceToIXOSDto).retrieve().bodyToMono(String.class).block();
			}else {
				if (jobParameters != null) {
					jobLauncher.run(dummyJob, jobParameters);
				}	

			}
		} catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException
				| JobParametersInvalidException e) {
			LOGGER.debug(e.getMessage());
		}
	}

	private void extractedRequestParameters(CPCSubmitProcessRequestDTO request, StringBuilder batchParameters) {
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.INVOICE_NO)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.INVOICE_NO));
			batchParameters.append("|");
		}
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.HAISEN_NO)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.HAISEN_NO));
			batchParameters.append("|");
		}
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.HAISEN_YEAR_MONTH)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.HAISEN_YEAR_MONTH));
			batchParameters.append("|");
		}
	}

	private void extracted(CPCSubmitProcessRequestDTO request, StringBuilder batchParameters) {
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.ADDITIONAL_PARAMETERS)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.ADDITIONAL_PARAMETERS));
			batchParameters.append("|");
		}
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.VANNING_MONTH)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.VANNING_MONTH));
			batchParameters.append("|");
		}
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.ETD)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.ETD));
			batchParameters.append("|");
		}
		if (null != request.getParameters() && null != request.getParameters().get(ConstantUtils.DESTINATION_VALUE)) {
			batchParameters.append(request.getParameters().get(ConstantUtils.DESTINATION_VALUE));
		}
	}

	public OemProcessCtrlEntity saveProcessDetails(String userId, String batchParameter, String systemName,
			String processId, String processName) {

		OemProcessCtrlIdEntity id = new OemProcessCtrlIdEntity();
		OemProcessCtrlEntity entity = new OemProcessCtrlEntity();
		id.setBatchId(processId);
		id.setProcessControlId(oemProcessCtrlRepository.getIdOfProcessControl());
		entity.setId(id);
		entity.setParameter(batchParameter);
		entity.setSubmitTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setProgramId(processId);
		entity.setProgramName(processName);
		entity.setStatus(2); // Processing
		entity.setUserId(userId);
		entity.setDeamon("N");
		entity.setStartTime(new java.sql.Timestamp(System.currentTimeMillis()));
		entity.setSystemName(systemName);
		oemProcessCtrlRepository.save(entity);
		return entity;

	}

	@Override
	public CPCProcessStatusResponseDto processStatus(CPCProcessStatusRequestDto request) {

		if (StringUtils.isBlank(request.getUserId()) || StringUtils.isBlank(request.getFromDateTime())
				|| StringUtils.isBlank(request.getEndDateTime())) {

			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}

		// This is to avoid any space issue from UI
		Timestamp fromDate = DateUtil.dateFromStringDateFormate(ConstantUtils.DEFAULTDATEFORMAT,
				request.getFromDateTime().replace('_', ' '));
		Timestamp endDate = DateUtil.dateFromStringDateFormate(ConstantUtils.DEFAULTDATEFORMAT,
				request.getEndDateTime().replace('_', ' '));

		if (fromDate.compareTo(endDate) > 0) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3019);
		}

		List<OemProcessCtrlEntity> oemProcessCtrlEntity = null;

		if (StringUtils.isNotBlank(request.getProcessId()) && (StringUtils.isNotBlank(request.getSystemName()))) {

			oemProcessCtrlEntity = oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndProgramIdAndSystemName(
					fromDate, endDate, request.getProcessId(), request.getSystemName());

		} else if (StringUtils.isNotBlank(request.getProcessId())) {

			oemProcessCtrlEntity = oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndProgramId(fromDate, endDate,
					request.getProcessId());

		} else if (StringUtils.isNotBlank(request.getSystemName())) {

			oemProcessCtrlEntity = oemProcessCtrlRepository.findAllBySubmitTimeBetweenAndSystemName(fromDate, endDate,
					request.getSystemName());
		} else {

			oemProcessCtrlEntity = oemProcessCtrlRepository.findAllBySubmitTimeBetween(fromDate, endDate);
		}

		boolean hasAdminRole = hasAdminRole(request.getUserId());

		List<CPCProcessList> listofProcessBatch = oemProcessCtrlEntity.stream()
				.map(p -> new CPCProcessList(Integer.toString(p.getId().getProcessControlId()), p.getProgramId(),
						p.getProgramName(), DateUtil.convertToDatabaseColumn(p.getSubmitTime()),
						p.getStatus() == 0 ? ConstantUtils.STS_ERROR : getProcessStatus(p.getStatus()), p.getUserId(),
						p.getParameter(), p.getSystemName()))
				.collect(Collectors.toList());

		return new CPCProcessStatusResponseDto(hasAdminRole, listofProcessBatch);
	}

	private String getProcessStatus(int status) {
		if (status == 1) {
			return ConstantUtils.STS_SUCCESS;
		}
		return status == 2 ? ConstantUtils.STS_ERROR : ConstantUtils.STS_PROCESSING;
	}

	@Override
	public List<CPCSystemNamesResponseDto> systemNames(String userId) {
		String company = getCompanyByUserId();

		List<SystemDtlsEntity> systemDtlsEntities = systemDtlsRepository
				.findAllByIdCompanyCdOrderByIdSystemNameAsc(company);
		return systemDtlsEntities.stream()
				.map(s -> new CPCSystemNamesResponseDto(s.getId().getSystemName(), s.getId().getSystemName()))
				.collect(Collectors.toList());
	}

	private String getCompanyByUserId() {
		return ConstantUtils.COMPANYNAME;
	}

	private boolean hasAdminRole(String userId) {
		boolean hasAdminRole = false;
		if ("ROEM".equalsIgnoreCase(userId)) {
			hasAdminRole = true;
		}
		return hasAdminRole;
	}

	/**
	 * @param processControlId
	 * @param processId
	 * @return
	 */
	@Override
	public List<CPCProcessLogsResponseDto> processLogs(String processControlId, String processId) {
		return processLogsList(processControlId, processId);
	}

	/**
	 * @param processControlId
	 * @param processId
	 * @return
	 */
	private List<CPCProcessLogsResponseDto> processLogsList(String processControlId, String processId) {
		List<ProcessLogDtlsEntity> processLogDtlsEntities = processLogDtlsRepository
				.findAllByProcessControlIdAndProcessId(Integer.valueOf(processControlId), processId);
		return processLogDtlsEntities.stream()
				.map(s -> new CPCProcessLogsResponseDto(s.getId(), Integer.toString(s.getProcessControlId()),
						s.getProcessId(), s.getProcessName(), s.getCorrelationId(), s.getProcessLogMessage(),
						s.getProcessLogStatus(), DateUtil.convertToDatabaseColumn(s.getProcessLogDatetime())))
				.collect(Collectors.toList());
	}

	/**
	 * @param processControlId
	 * @param processId
	 * @return
	 * @throws IOException
	 * @throws JRException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	@Override
	public Object downloadProcessLogs(String processControlId, String processId) throws JRException, IOException {
		// Set parameters that need to pass to jrxml file
		Map<String, Object> parameters = getReportDynamicPrameters();

		String fileFormat = "xlsx";
		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportFormat", fileFormat);
		config.put("storeInDB", "true");

		String fileTemplateName = tpexConfigRepository.findByName("process.log.details.download.file.template.name")
				.getValue();
		return jasperReportService.getJasperReportDownloadOnline(processLogsList(processControlId, processId),
				fileFormat, fileTemplateName, parameters, config);
	}

	/**
	 * @return
	 */
	private Map<String, Object> getReportDynamicPrameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("headingFontSize",
				Integer.parseInt(tpexConfigRepository.findByName("jasper.report.headingFontSize").getValue()));
		parameters.put("detailFontSize",
				Integer.parseInt(tpexConfigRepository.findByName("jasper.report.detailFontSize").getValue()));
		parameters.put("headingFontColor",
				tpexConfigRepository.findByName("jasper.report.headingFontColor").getValue());
		parameters.put("detailFontColor", tpexConfigRepository.findByName("jasper.report.detailFontColor").getValue());
		parameters.put("headingBGColor", tpexConfigRepository.findByName("jasper.report.headingBGColor").getValue());
		parameters.put("detailBGColor", tpexConfigRepository.findByName("jasper.report.detailBGColor").getValue());
		parameters.put("detailVAlign", tpexConfigRepository.findByName("jasper.report.detailVAlign").getValue());
		return parameters;
	}

	public List<CommonDropdownDto> fetchDestCodeAndDestName() {
		List<FinalDestEntity> destList = finalDestRepository.findAllByOrderByDestinationCdAsc();
		return destList.stream().distinct()
				.map(t -> new CommonDropdownDto(t.getDestinationCd(), t.getDestinationName()))
				.collect(Collectors.toList());
	}

	public boolean isInvoiceExists(String invoiceNumber) {

		return invNoRepository.existsById(invoiceNumber);
	}

	public List<HaisenNoEntity> isHaisenExists(String haisenNumber) {

		return haisenNumberRepository.findHaisenNoById(haisenNumber);
	}

	public String findIxosFlag(String invoiceNumber) {

		return invNoRepository.findIxosNumber(invoiceNumber);
	}

	public String findIxosFlagForHaisenNo(String haisenNo) {

		return invNoRepository.findIxosNumberForHaisenNo(haisenNo);
	}

}
