package com.tpex.invoice.service;

import java.sql.Date;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.tpex.dto.InvGenWorkPlanMstDTO;
import com.tpex.dto.NoemHaisenDtlsEntityDTO;
import com.tpex.dto.NoemRenbanSetupMstDTO;
import com.tpex.dto.ShippingContResultDTO;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
import com.tpex.entity.NoemCbMstEntity;
import com.tpex.entity.NoemHaisenDtlsEntity;
import com.tpex.entity.NoemHaisenDtlsIdEntity;
import com.tpex.entity.NoemRenbanSetupMstEntity;
import com.tpex.entity.NoemSeqCtrlMstEntity;
import com.tpex.entity.NoemSeqCtrlMstIdEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.entity.OemParameterEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.entity.RddDownLocDtlEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.ShippingContSearchInputDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvGenWorkPlanMstRepository;
import com.tpex.repository.NoemCbMstRepository;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.repository.NoemRenbanSetupMstRepository;
import com.tpex.repository.NoemSeqCtrlMstRepository;
import com.tpex.repository.NoemVprDlyContRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.RddDownLocDtlRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantProperties;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;
import com.tpex.util.Util;

import net.sf.jasperreports.engine.JRParameter;

@Service
public class InvGenWorkPlanMstServiceImpl implements InvGenWorkPlanMstService {

	@Autowired
	InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;

	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;

	@Autowired
	private OemPortMstRepository oemPortMstRepository;

	@Autowired
	private NoemCbMstRepository noemCbMstRepository;
	
	@Autowired
	private NoemRenbanSetupMstRepository noemRenbanSetupMstRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	ConstantProperties constantProperties;
	
	@Autowired
	private RddDownLocDtlRepository rddDownLocDtlRepository;
	
	
	@Autowired
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;
	
	@Autowired
	OemParameterRepository oemParameterRepository;
	
	@Autowired
	NoemSeqCtrlMstRepository noemSeqCtrlMstRepository;

	public static final String defaultDateFormate ="dd/MM/yyyy";

	
	@Override
	public boolean saveInvoice(List<InvGenWorkPlanMstDTO> listInvGenWorkPlanMstDTO) throws Exception {
		boolean isSaved = false;
		for(InvGenWorkPlanMstDTO module :listInvGenWorkPlanMstDTO) {
			InvGenWorkPlanMstIdEntity id = new InvGenWorkPlanMstIdEntity();
			if(module != null && module.getId() !=null) {
			id.setOriginalEtd(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getId().getOriginalEtd()));
			id.setRenbanCode(module.getId().getRenbanCode());
			id.setBookingNo(module.getId().getBookingNo());	
			id.setLiner(module.getId().getLiner());
			id.setContDest(module.getId().getContDest());
			InvGenWorkPlanMstEntity entity =null;
			Optional<InvGenWorkPlanMstEntity> optionOfInvGenWorkPlanMstEntity = invGenWorkPlanMstRepository.findById(id);
			if(optionOfInvGenWorkPlanMstEntity.isPresent()) {
				entity= optionOfInvGenWorkPlanMstEntity.get();
				if("N".equalsIgnoreCase(entity.getInvGenFlag())) {

					if(Util.nullCheck(module.getBroker()))
					entity.setBroker(module.getBroker());
					
					if(Util.nullCheck(module.getEtd1()))
					entity.setEtd1(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEtd1()));
					
					if(Util.nullCheck(module.getIssueInvoiceDate()))
					entity.setIssueInvoiceDate(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getIssueInvoiceDate()));

					if(module.getCont20() != null)
					entity.setCont20(module.getCont20());
					
					if(module.getCont40() != null)
					entity.setCont40(module.getCont40());
					
					
					if(Util.nullCheck(module.getDsiTiNo()))
					entity.setDsiTiNo(module.getDsiTiNo());
					
					
					if(Util.nullCheck(module.getEta1()))
					entity.setEta1(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEta1()));
					
					if(Util.nullCheck(module.getEta2()))
					entity.setEta2(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEta2()));
					
					if(Util.nullCheck(module.getEta3()))
					entity.setEta3(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEta3()));
					
					if(Util.nullCheck(module.getEtd2()))
					entity.setEtd2(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEtd2()));
					
					if(Util.nullCheck(module.getEtd3()))
					entity.setEtd3(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, module.getEtd3()));

					
					if(Util.nullCheck(module.getPortOfDischarge()))
					entity.setPortOfDischarge(module.getPortOfDischarge());
					
					if(Util.nullCheck(module.getPortOfLoading()))
					entity.setPortOfLoading(module.getPortOfLoading());
					
					if(Util.nullCheck(module.getFolderName()))
					entity.setFolderName(module.getFolderName());
					
					if(Util.nullCheck(module.getUpdateBy()))
					entity.setUpdateBy(module.getUpdateBy());
					
					entity.setUpdateDate(DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, module.getUpdateDate()));
					
					if(Util.nullCheck(module.getVessel1()))
					entity.setVessel1(module.getVessel1());
					
					if(Util.nullCheck(module.getVessel2()))
					entity.setVessel2(module.getVessel2());
					
					if(Util.nullCheck(module.getVessel3()))
					entity.setVessel3(module.getVessel3());
					
					if(Util.nullCheck(module.getVoy1()))
					entity.setVoy1(module.getVoy1());
					
					if(Util.nullCheck(module.getVoy2()))
					entity.setVoy2(module.getVoy2());
					
					if(Util.nullCheck(module.getVoy3()))
					entity.setVoy3(module.getVoy3());
					
					invGenWorkPlanMstRepository.save(entity);
					isSaved = true;
				}
			}
			}
		}
		return isSaved;
	}

	@Override
	public Page<InvGenWorkPlanMstEntity> fetchInvoiceDetails(int pageNo, int pageSize, String invoiceFromDate,String invoiceToDate,String etd1fromDate , String  etd1ToDate, List<String> dstCode) throws InvalidInputParametersException,ParseException {

		LocalDate currentDate = LocalDate.now();
		LocalDate currentDateMinus6Months = currentDate.minusMonths(6);
		DateTimeFormatter formatter_1 = DateTimeFormatter.ofPattern(defaultDateFormate);

		List<Order> orders = new ArrayList<Order>();
		orders.add(new Order(Sort.Direction.ASC, "issueInvoiceDate"));
		orders.add(new Order(Sort.Direction.ASC, "etd1"));
		orders.add(new Order(Sort.Direction.ASC, "idContDest"));
		orders.add(new Order(Sort.Direction.ASC, "idLiner"));
		orders.add(new Order(Sort.Direction.ASC, "Broker"));
		orders.add(new Order(Sort.Direction.ASC, "idRenbanCode"));

		Pageable paging = PageRequest.of(pageNo, pageSize, Sort.by(orders));

		Page<InvGenWorkPlanMstEntity> pagedResult = null;
		
		validateInvGenSearchAndDownloadRequest(invoiceFromDate, etd1fromDate, currentDateMinus6Months, formatter_1);

		if(getInvoiceDateNullCheck(invoiceFromDate, etd1fromDate)) {
			if(checkDstCode(dstCode)) {
				pagedResult =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode,
						paging);
			}else {
				pagedResult =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndEtd1Between(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						paging);
			}
		} else if(StringUtils.isNotEmpty(invoiceFromDate) && !"null".equalsIgnoreCase(invoiceFromDate.trim()) && (StringUtils.isEmpty(etd1fromDate)) || "null".equalsIgnoreCase(etd1fromDate.trim()) ) {
			if(checkDstCode(dstCode)) {
				pagedResult =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						dstCode,
						paging);
			}else {
				pagedResult =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetween(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						paging);
			}
		} else if(getInvoiceFromDateNullCheck(invoiceFromDate, etd1fromDate) ) {
			if(checkDstCode(dstCode)) {
				pagedResult =  invGenWorkPlanMstRepository.findByEtd1BetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode,
						paging);
			}else {
				pagedResult =  invGenWorkPlanMstRepository.findByEtd1Between(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						paging);
			}
		} 
		return pagedResult;
	}
	
	private void validateInvGenSearchAndDownloadRequest(String invoiceFromDate, String etd1fromDate,
			LocalDate currentDateMinus6Months, DateTimeFormatter formatter_1) {
		if((StringUtils.isBlank(invoiceFromDate) || "null".equalsIgnoreCase(invoiceFromDate)) && (StringUtils.isBlank(etd1fromDate) || "null".equalsIgnoreCase(etd1fromDate))) {

			throw new InvalidInputParametersException("Either Invoice Date From or ETD Date From is be required for search operation");
		}
		else if((StringUtils.isNotBlank(invoiceFromDate) && !"null".equalsIgnoreCase(invoiceFromDate)) || (StringUtils.isNotBlank(etd1fromDate) && !"null".equalsIgnoreCase(etd1fromDate)))
		{
			if(StringUtils.isNotBlank(invoiceFromDate))
			{
				if (LocalDate.parse(invoiceFromDate, formatter_1).isBefore(currentDateMinus6Months)) {
					throw new InvalidInputParametersException("Allowed to select only max 6 Month Back date from the current date");
				}

			}
			else
			{
				if (LocalDate.parse(etd1fromDate, formatter_1).isBefore(currentDateMinus6Months)) {
					throw new InvalidInputParametersException("Allowed to select only max 6 Month Back date from the current date");
				}

			}
		}
	}


	@Override
	public List<OemFnlDstMstEntity> destinationCodeList() {
		return oemFnlDstMstRepository.findAllByOrderByFdDstCdAsc();   
	}
	
	//Return count of rows to decide online/offline download
	@Override
	public Map<String, Integer> getCountReportDataToDownload(String invoiceFromDate, String invoiceToDate,
			String etd1fromDate, String etd1ToDate, List<String> dstCode, String createdBy) throws ParseException {
		int rowCount = 0;
		Map<String, Integer> response = new HashMap<>();
		
		RddDownLocDtlEntity rddDownLocDtlEntity = rddDownLocDtlRepository.findTopByReportNameAndCreateByOrderByReportIdDesc("InvoiceGenerationWorkPlan", createdBy);
		if(rddDownLocDtlEntity != null && "Processing".equalsIgnoreCase(rddDownLocDtlEntity.getStatus())) {
			response.put("statusCode", 250);
			return response;
		}
		
		//Get configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("storeInDB", "false");
		config.put("loginUserId", createdBy);
		
		String fileTemplateName = "InvoiceGenerationWorkPlan";
		String fileFormat = (String) config.get("reportFormat");
        String fileName = jasperReportService.getFileName(fileTemplateName, fileFormat);
    	StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);
            
		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, sb);
				
		if(getInvoiceDateNullCheck(invoiceFromDate, etd1fromDate) ) {
			if(checkDstCode(dstCode)) {
				rowCount =  invGenWorkPlanMstRepository.findCountByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode);
			} else {
				rowCount = invGenWorkPlanMstRepository.findCountByIssueInvDateAndEtd1(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate));
			}
		} else if(getEtd1AndInvoiceNullCheck(invoiceFromDate, etd1fromDate) ) {
			if(checkDstCode(dstCode)) {
				rowCount =  invGenWorkPlanMstRepository.findCountByIssueInvoiceDateBetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						dstCode);
			}else {
				rowCount = invGenWorkPlanMstRepository.findCountByIssueInvDate(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate));
				
			}
		} else if(getInvoiceFromDateNullCheck(invoiceFromDate, etd1fromDate)) {
			if(checkDstCode(dstCode)) {
				rowCount =  invGenWorkPlanMstRepository.findCountByEtd1BetweenAndIdContDestIn(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode);
			} else {
				rowCount =  invGenWorkPlanMstRepository.findCountByEtd1Between(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate));
			}
		}
		response.put("rowCount", rowCount);
		response.put("savedReportId", savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0);
		return response;
	}
	
	@Async
	@Override
	public Object downloadReportOffline(String invoiceFromDate,String invoiceToDate,String etd1fromDate , String  etd1ToDate, List<String> dstCode, String fileFormat, String loginUserId, int savedReportId) throws Exception {

		List<InvGenWorkPlanMstEntity> invGenWorkPlanMstEntityList = new ArrayList<>();
		Object jasperResponse = null;
		
		//Set parameters that need to pass to jrxml file
		Map<String, Object> parameters = getReportDynamicPrameters();

		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("reportSizeLimit", tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", loginUserId);
		
		String fileTemplateName = "InvoiceGenerationWorkPlan";
		fileFormat = getFileFormat(fileFormat, config);
		
		String fileName = jasperReportService.getFileName(fileTemplateName, fileFormat);
		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);
        
		RddDownLocDtlEntity savedRddDownLocDtlEntity = jasperReportService.saveOfflineDownloadDetail(fileTemplateName, config, sb);
		int reportId = savedRddDownLocDtlEntity != null ? savedRddDownLocDtlEntity.getReportId() :0;
		invGenWorkPlanMstEntityList = getReportDataToDownload(invoiceFromDate, invoiceToDate, etd1fromDate, etd1ToDate, dstCode, invGenWorkPlanMstEntityList);
    	
    	
    	//If file size is greater than configured size then store file in directory and return path
    	jasperReportService.getJasperReportDownloadOffline(invGenWorkPlanMstEntityList, fileFormat, fileTemplateName, parameters, config, reportId, sb);
    	jasperResponse = fileTemplateName+"."+fileFormat;
	
    	return jasperResponse;
	}

	private String getFileFormat(String fileFormat, Map<String, Object> config) {
		fileFormat = (fileFormat == null || fileFormat.equals("")) ?  (String) config.get("reportFormat") : fileFormat;
		return fileFormat;
	}
	
	@Override
	public Object downloadReportOnline(String invoiceFromDate,String invoiceToDate,String etd1fromDate , String  etd1ToDate, List<String> dstCode, String fileFormat, String loginUserId) throws Exception {

		List<InvGenWorkPlanMstEntity> invGenWorkPlanMstEntityList = new ArrayList<>();
		Object jasperResponse = null;
		
		invGenWorkPlanMstEntityList = getReportDataToDownload(invoiceFromDate, invoiceToDate, etd1fromDate, etd1ToDate,
				dstCode, invGenWorkPlanMstEntityList);

		//Set parameters that need to pass to jrxml file
		Map<String, Object> parameters = getReportDynamicPrameters();

		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("reportSizeLimit", tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", loginUserId);
		
		String fileTemplateName = "InvoiceGenerationWorkPlan";
		fileFormat = getFileFormat(fileFormat, config);
		String fileName = jasperReportService.getFileName(fileTemplateName, fileFormat);
        	
    	jasperResponse = jasperReportService.getJasperReportDownloadOnline(invGenWorkPlanMstEntityList, fileFormat, fileTemplateName, fileName, parameters, config);
	
        return jasperResponse;
	}

	private Map<String, Object> getReportDynamicPrameters() {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put(JRParameter.IS_IGNORE_PAGINATION, Boolean.TRUE);
		parameters.put("headingFontSize", Integer.parseInt((String)tpexConfigRepository.findByName("jasper.report.headingFontSize").getValue()));
		parameters.put("detailFontSize", Integer.parseInt((String)tpexConfigRepository.findByName("jasper.report.detailFontSize").getValue()));
		parameters.put("headingFontColor", tpexConfigRepository.findByName("jasper.report.headingFontColor").getValue());
		parameters.put("detailFontColor", tpexConfigRepository.findByName("jasper.report.detailFontColor").getValue());
		parameters.put("headingBGColor", tpexConfigRepository.findByName("jasper.report.headingBGColor").getValue());
		parameters.put("detailBGColor", tpexConfigRepository.findByName("jasper.report.detailBGColor").getValue());
		parameters.put("detailVAlign", tpexConfigRepository.findByName("jasper.report.detailVAlign").getValue());
		return parameters;
	}
	
	private List<InvGenWorkPlanMstEntity> getReportDataToDownload(String invoiceFromDate, String invoiceToDate,
			String etd1fromDate, String etd1ToDate, List<String> dstCode,
			List<InvGenWorkPlanMstEntity> invGenWorkPlanMstEntityList) throws ParseException {
		if(getInvoiceDateNullCheck(invoiceFromDate, etd1fromDate) ) {
			if(checkDstCode(dstCode)) {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndEtd1BetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode);
			} else {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndEtd1BetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate));
			}
		} else if(getEtd1AndInvoiceNullCheck(invoiceFromDate, etd1fromDate) ) {
			if(checkDstCode(dstCode)) {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate),
						dstCode);
			}else {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByIssueInvoiceDateBetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceFromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, invoiceToDate));
			}
		} else if(getInvoiceFromDateNullCheck(invoiceFromDate, etd1fromDate)) {
			if(checkDstCode(dstCode)) {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByEtd1BetweenAndIdContDestInOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate),
						dstCode);
			} else {
				invGenWorkPlanMstEntityList =  invGenWorkPlanMstRepository.findByEtd1BetweenOrderByIssueInvoiceDateAscEtd1AscIdContDestAscIdLinerAscBrokerAscIdRenbanCodeAsc(
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1fromDate),
						DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etd1ToDate));
			}
		}
		return invGenWorkPlanMstEntityList;
	}

	private boolean checkDstCode(List<String> dstCode) {
		return !CollectionUtils.isEmpty(dstCode);
	}

	private boolean getInvoiceFromDateNullCheck(String invoiceFromDate, String etd1fromDate) {
		return (StringUtils.isEmpty(invoiceFromDate) || "null".equalsIgnoreCase(invoiceFromDate.trim())) && StringUtils.isNotEmpty(etd1fromDate) && !"null".equalsIgnoreCase(etd1fromDate.trim());
	}

	private boolean getEtd1AndInvoiceNullCheck(String invoiceFromDate, String etd1fromDate) {
		return StringUtils.isNotEmpty(invoiceFromDate) && !"null".equalsIgnoreCase(invoiceFromDate.trim()) && (StringUtils.isEmpty(etd1fromDate) || "null".equalsIgnoreCase(etd1fromDate.trim()));
	}

	private boolean getInvoiceDateNullCheck(String invoiceFromDate, String etd1fromDate) {
		return StringUtils.isNotEmpty(invoiceFromDate) && !"null".equalsIgnoreCase(invoiceFromDate.trim()) && StringUtils.isNotEmpty(etd1fromDate) && !"null".equalsIgnoreCase(etd1fromDate.trim());
	}




	@Override
	public List<NoemCbMstEntity> getBrokerDetails() {
		return noemCbMstRepository.findAllByOrderByCbNm();
	}

	@Override
	public List<OemPortMstEntity> getPortOfLoadingAndDischargeDetails() {
		return oemPortMstRepository.findAllByOrderByCd();
	}

	@Override
	public List<NoemRenbanSetupMstDTO> fetchRenbanCodesByContDstCd(String contDstCode) {
		//Find data from Renban setup master based on container destination code
		List<NoemRenbanSetupMstEntity> renbanCodeList = noemRenbanSetupMstRepository.findByIdContDstCdOrderByIdContGrpCdAsc(contDstCode);
		//Fetch list of distinct Renban code (Container group code)
		List<NoemRenbanSetupMstDTO> renbanCodes = renbanCodeList.stream().map(u -> new NoemRenbanSetupMstDTO(u.getId().getContGrpCd(), u.getId().getContGrpCd())).distinct().collect(Collectors.toList());
		return renbanCodes;
	}
	
	
	@Autowired
	private NoemVprDlyContRepository noemVprDlyContRepository;
	
	/**
	 * Shipping results.
	 *
	 * @param shippingContSearchInputDTO the shipping cont search input DTO
	 * @return the list
	 * @throws Exception the exception
	 */
	@Override
	public List<ShippingContResultDTO> shippingResults(ShippingContSearchInputDTO shippingContSearchInputDTO) throws Exception {
		
		if(shippingContSearchInputDTO.getBookingNo().isBlank())
			shippingContSearchInputDTO.setBookingNo(null);
		
		if(shippingContSearchInputDTO.getRenbanCodes().isEmpty() || shippingContSearchInputDTO.getRenbanCodes().size()==0)
			shippingContSearchInputDTO.setRenbanCodes(null);
		
		validateShippingResultsRequest(shippingContSearchInputDTO);
		
		//Find data from Renban setup master based on container destination code
		List<Map<String, Object>> noemVprDlyContList = noemVprDlyContRepository.findDetails(shippingContSearchInputDTO.getContainerDestination(), 
				DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, shippingContSearchInputDTO.getEtdFrom()),
				DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, shippingContSearchInputDTO.getEtdTo()),
				shippingContSearchInputDTO.getBookingNo(),
				shippingContSearchInputDTO.getRenbanCodes(),
				shippingContSearchInputDTO.getRenbanCodes() == null || shippingContSearchInputDTO.getRenbanCodes().isEmpty() ? "Y" : "N"); //For now using this flag for handling empty renban codes list
		
		List<ShippingContResultDTO> shippingContResultDTOList = noemVprDlyContList.stream().map(u -> new ShippingContResultDTO(
					Integer.valueOf(u.get("SR_NO").toString()), 
					u.get("NVDC_ETD_1") != null ? u.get("NVDC_ETD_1").toString() : "", 
					u.get("CONT_RENBAN_NO") != null ? u.get("CONT_RENBAN_NO").toString() : "",
					u.get("NVDC_CONT_SIZE") != null ? u.get("NVDC_CONT_SIZE").toString() : "", 
					u.get("DG_FLG") != null ? u.get("DG_FLG").toString() : "", 
					u.get("VAN_DT") != null ? u.get("VAN_DT").toString() : "", 
					u.get("INV_DATA_CAME") != null ? u.get("INV_DATA_CAME").toString() : "", 
					u.get("INV_GEN_FLG") != null ? u.get("INV_GEN_FLG").toString() : "",
					u.get("NICM_BOOKING_NO") != null ? u.get("NICM_BOOKING_NO").toString() : "",
					u.get("NICM_ISO_CONT_NO") != null ? u.get("NICM_ISO_CONT_NO").toString() : "",
					u.get("NVDC_SEAL_NO") != null ? u.get("NVDC_SEAL_NO").toString() : "")
			)
			.distinct().collect(Collectors.toList());

		return shippingContResultDTOList;
	}
	
	/**
	 * Validate shipping results request.
	 *
	 * @param shippingContSearchInputDTO the shipping cont search input DTO
	 * @throws Exception the exception
	 */
	private void validateShippingResultsRequest(ShippingContSearchInputDTO shippingContSearchInputDTO) throws Exception {
		String etdFrom = shippingContSearchInputDTO.getEtdFrom();
		String etdTo = shippingContSearchInputDTO.getEtdTo();
		String contDestination = shippingContSearchInputDTO.getContainerDestination();
		if(StringUtils.isBlank(etdFrom) || etdFrom == null || StringUtils.isBlank(etdTo) || etdTo == null || StringUtils.isBlank(contDestination) || contDestination == null) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
		}
		
		Date etdFromDate = DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etdFrom);
		Date etdToDate = DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, etdTo);
		
		if(etdFromDate.compareTo(etdToDate) > 0) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1016);
		}
		
		long diffInDays = ChronoUnit.DAYS.between(etdFromDate.toLocalDate(), etdToDate.toLocalDate());
		if(diffInDays > 10) {
			throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1017);
		}
	}
	
	
	
	
	/**
	 * Generate haisen no.
	 *
	 * @param noemHaisenDtlsIdEntity the noem haisen dtls id entity
	 * @return the map
	 * @throws Exception the exception
	 */
	public Map<String, Object> generateHaisenNo(NoemHaisenDtlsEntityDTO noemHaisenDtlsEntity) throws Exception {
		int lintSeqNo = 0;
		String haisenCd = null;
		String haisenYearMth = null;
		String lstrSeqCd = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		Map<String, Object> resultMap = new HashMap<>();
		
		//Check Haisen detail table with PK
		//if exist - do nothing
		//if not exist - get haisen no with etd month in haisen dtl table
		//if not exist - Select “Haisen code 1” from OEM_PARAMETER as prefix, then assign sequence no.001
		//if exist - Get maximum existing Haisen no. 
		//If existing maximum haisen no. seq doesn’t equal to 999, then assign haisen seq no. + 1
		//If existing maximum haisen no. seq is equal to 999, get new haisen prefix from parameter “Haisen code 2” or “Haisen code 3” in sequence, then assign new haisen no. 001 (eg. TA001 or TB001

		NoemHaisenDtlsIdEntity noemHaisenDtlsIdEntity = new NoemHaisenDtlsIdEntity();
		noemHaisenDtlsIdEntity.setBuyer(noemHaisenDtlsEntity.getBuyer());
		noemHaisenDtlsIdEntity.setDepPort(noemHaisenDtlsEntity.getDepPort());
		noemHaisenDtlsIdEntity.setDstPort(noemHaisenDtlsEntity.getDstPort());
		noemHaisenDtlsIdEntity.setEtdDate(DateUtil.dateFromStringDateFormateforInvoiceDate(defaultDateFormate, noemHaisenDtlsEntity.getEtdDate()));
		noemHaisenDtlsIdEntity.setVesselOcean(noemHaisenDtlsEntity.getVesselOcean());
		noemHaisenDtlsIdEntity.setVoyNo(noemHaisenDtlsEntity.getVoyNo());
		if (noemHaisenDtlsRepository.findById(noemHaisenDtlsIdEntity).isEmpty()) {
			Optional<OemParameterEntity> oemParameterEntity = oemParameterRepository.findById("HAISEN_CODE_1");
			
			if (oemParameterEntity.isPresent()) {
				haisenCd = oemParameterEntity.get().getOprParaVal();
	        } else {
                throw new Exception(constantProperties.getErrorHaisenNoConfigurationMissing());
	        }
			
			haisenYearMth = formatter.format(LocalDate.parse(noemHaisenDtlsEntity.getEtdDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy"))); 
	        
	        lstrSeqCd = "HAISEN_1";
	        Optional<NoemSeqCtrlMstEntity> noemSeqCtrlMstEntity = noemSeqCtrlMstRepository.findById(new NoemSeqCtrlMstIdEntity(haisenYearMth, "INS", lstrSeqCd));
	        
	        if (noemSeqCtrlMstEntity.isPresent()) {
	        	lintSeqNo = noemSeqCtrlMstEntity.get().getScmSeqNo();
	        }
	        if (lintSeqNo == 999) {
	        	Optional<OemParameterEntity> oemParameterEntity1 = oemParameterRepository.findById("HAISEN_CODE_2");
	        	if (oemParameterEntity1.isPresent()) {
					haisenCd = oemParameterEntity1.get().getOprParaVal();
	        	} else {
	                throw new Exception(constantProperties.getErrorHaisenNoConfigurationMissing());
		        }
		        
	        	lstrSeqCd = "HAISEN_2";
		        NoemSeqCtrlMstIdEntity noemSeqCtrlMstIdEntity1 = new NoemSeqCtrlMstIdEntity(haisenYearMth, "INS", lstrSeqCd);
		        Optional<NoemSeqCtrlMstEntity> noemSeqCtrlMstEntity1 = noemSeqCtrlMstRepository.findById(noemSeqCtrlMstIdEntity1);
		        if (noemSeqCtrlMstEntity1.isPresent()) {
		        	lintSeqNo = noemSeqCtrlMstEntity1.get().getScmSeqNo();
		        } else {
		        	NoemSeqCtrlMstEntity createNoemCtrlMstEntity = new NoemSeqCtrlMstEntity();
		        	createNoemCtrlMstEntity.setId(noemSeqCtrlMstIdEntity1);
		        	createNoemCtrlMstEntity.setScmSeqDesc(ConstantUtils.SCM_SES_DESC + haisenCd);
		        	createNoemCtrlMstEntity.setScmSeqNo(0);
		        	setScmUpd(createNoemCtrlMstEntity);
		        	
                    lintSeqNo = 0;
                }
		        
		        if (lintSeqNo == 999) {
		        	Optional<OemParameterEntity> oemParameterEntity2 = oemParameterRepository.findById("HAISEN_CODE_3");
		        	if (oemParameterEntity2.isPresent()) {
						haisenCd = oemParameterEntity2.get().getOprParaVal();
		        	} else {
		                throw new Exception(constantProperties.getErrorHaisenNoConfigurationMissing());
			        }
			        
		        	lstrSeqCd = "HAISEN_3";
			        NoemSeqCtrlMstIdEntity noemSeqCtrlMstIdEntity2 = new NoemSeqCtrlMstIdEntity(haisenYearMth, "INS", lstrSeqCd);
			        Optional<NoemSeqCtrlMstEntity> noemSeqCtrlMstEntity2 = noemSeqCtrlMstRepository.findById(noemSeqCtrlMstIdEntity2);
			        if (noemSeqCtrlMstEntity2.isPresent()) {
			        	lintSeqNo = noemSeqCtrlMstEntity2.get().getScmSeqNo();
			        } else {
			        	NoemSeqCtrlMstEntity createNoemSeqCtrlMstEntity = new NoemSeqCtrlMstEntity();
			        	createNoemSeqCtrlMstEntity.setId(noemSeqCtrlMstIdEntity2);
			        	createNoemSeqCtrlMstEntity.setScmSeqDesc(ConstantUtils.SCM_SES_DESC + haisenCd);
			        	setScmUpd(createNoemSeqCtrlMstEntity);
			        	
	                    lintSeqNo = 0;
	                }
			        
			        if(lintSeqNo == 999){
                        throw new Exception(constantProperties.getErrorHaisenNoLimitReached());
                    }
		        }
	        }
	        
	        //increment he sequence no
            lintSeqNo = lintSeqNo + 1;
            
            //lpad seqNo with 0 for 3 digit no
            String haisenNo = haisenCd + String.format("%03d", lintSeqNo);
            
            resultMap.put("haisenNo", haisenNo);
            resultMap.put("haisenYrMth", haisenYearMth);
            
	        Optional<NoemSeqCtrlMstEntity> updateNoemSeqCtrlMstEntity = noemSeqCtrlMstRepository.findById(new NoemSeqCtrlMstIdEntity(haisenYearMth, "INS", lstrSeqCd));
	        if (updateNoemSeqCtrlMstEntity.isPresent()) {
        		NoemSeqCtrlMstEntity noemCtrlMstEntity = updateNoemSeqCtrlMstEntity.get();
        		noemCtrlMstEntity.setScmSeqNo(lintSeqNo);
        		noemSeqCtrlMstRepository.save(noemCtrlMstEntity);
        	} else {
        		NoemSeqCtrlMstEntity noemCtrlMstEntity = new NoemSeqCtrlMstEntity();
        		noemCtrlMstEntity.setId(new NoemSeqCtrlMstIdEntity(haisenYearMth, "INS", lstrSeqCd));
        		noemCtrlMstEntity.setScmSeqNo(lintSeqNo);
        		noemCtrlMstEntity.setScmSeqDesc(ConstantUtils.SCM_SES_DESC + haisenCd);
        		setScmUpd(noemCtrlMstEntity);
        	}
	        
	        NoemHaisenDtlsEntity createNoemHaisenDtlsEntity = new NoemHaisenDtlsEntity();
	        createNoemHaisenDtlsEntity.setId(noemHaisenDtlsIdEntity);
	        createNoemHaisenDtlsEntity.setHaisenNo(haisenNo);
	        createNoemHaisenDtlsEntity.setHaisenYearMonth(haisenYearMth);
	        createNoemHaisenDtlsEntity.setEtaDate(Date.valueOf(LocalDate.now()));
	        createNoemHaisenDtlsEntity.setShipCoNM(noemHaisenDtlsEntity.getShipCoNM());
	        createNoemHaisenDtlsEntity.setNoOf20FtContainer(noemHaisenDtlsEntity.getNoOf20FtContainer());
	        createNoemHaisenDtlsEntity.setNoOf40FtContainer(noemHaisenDtlsEntity.getNoOf40FtContainer());
	        createNoemHaisenDtlsEntity.setLclVol(noemHaisenDtlsEntity.getLclVol());
	        createNoemHaisenDtlsEntity.setUpdatedBy(ConstantUtils.TEST_USER);
	        createNoemHaisenDtlsEntity.setUpdatedDate(Date.valueOf(LocalDate.now()));
	        noemHaisenDtlsRepository.save(createNoemHaisenDtlsEntity);
		}
		
		return resultMap;
	}

	private void setScmUpd(NoemSeqCtrlMstEntity createNoemSeqCtrlMstEntity) {
		createNoemSeqCtrlMstEntity.setScmSeqNo(0);
		//TODO: Need to change this value. Get current user from session and set it here. This will be done when authentication will be implemented
		createNoemSeqCtrlMstEntity.setScmUpdBy("Test User");
		createNoemSeqCtrlMstEntity.setScmUpdDt(Date.valueOf(LocalDate.now()));
		noemSeqCtrlMstRepository.save(createNoemSeqCtrlMstEntity);
	}

}
