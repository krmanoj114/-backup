package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import com.tpex.invoice.dto.PackingListCustomBrokerDto;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.PackingListCustomBrokerRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;

import net.sf.jasperreports.engine.JRException;

@Configuration
public class PackingListCustomBrokerServiceImpl {
	@Autowired
	ResourceLoader resourceLoader;
	
	@Autowired
	TpexConfigRepository tpexConfigRepository;
	

	@PersistenceContext
	private EntityManager manager;
	
	@Autowired
	PackingListCustomBrokerRepository packingListCustomBrokerRepository;
	
	@Autowired
	JasperReportService jasperReportService;
	
	public Object generateReport(String invNumber,String reportName,String pidUserId, String reportFormat) throws FileNotFoundException, JRException {

		Object jasperResponse = null;
        Map<String,Object> parameters=new HashMap<>();
		
		parameters.put("P_I_V_INVOICE_NO", invNumber);
		parameters.put("pidUserId",pidUserId);
		
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true);
	    config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false);
		config.put(ConstantUtils.REPORT_DIRECTORY,tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue());
		config.put(ConstantUtils.REPORT_FORMAT,tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());
        config.put(ConstantUtils.STORE_DB, "false");
		config.put(ConstantUtils.LOGIN_USER_ID, ConstantUtils.TEST_USER);
		List<PackingListCustomBrokerDto> response = new ArrayList<>();
		packingListCustomBrokerRepository.getPINS002AData(invNumber);
		List<Object[]> itemData=packingListCustomBrokerRepository.getINSPACKLISTTEMPData();
		if (itemData != null && !itemData.isEmpty()) {
		for(Object[] obj : itemData) {
			response.add(mapPackingListCustomBrokerDtoData(obj));
		}
		}

        String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat : "pdf";
		
		String fileName = null;
        fileName = invNumber + "_"+ "PAC_CB" + "." + fileFormat;	
		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, reportName, fileName, parameters, config);
		}else {
			jasperResponse=jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, reportName, parameters, config, 0, fileName);
		}
		return jasperResponse;
				
	}
	
	private PackingListCustomBrokerDto mapPackingListCustomBrokerDtoData(Object[] obj) {
		PackingListCustomBrokerDto commonDto = new PackingListCustomBrokerDto();
		Predicate<Object> checkNull = i -> (i != null && !"null".equals(i));
		setInsCngsDetails(obj, commonDto, checkNull);
		setInvoiceDetails(obj, commonDto, checkNull);
		setPartDetails(obj, commonDto, checkNull);
		if (checkNull.test(obj[9])) {
		commonDto.setInsSumTotUnit(Double.valueOf(obj[9].toString()));
		}
		
		if (checkNull.test(obj[10])) {
		commonDto.setInsIcoFlag(obj[10].toString());
		}
		if (checkNull.test(obj[11])) {
		commonDto.setInsPartPrice(Double.valueOf(obj[11].toString()));
		}
		commonDto.setInsUnitPerBox(Double.valueOf(obj[12].toString()));
		
		commonDto.setInsPartWt(Double.valueOf(obj[13].toString()));
		if (checkNull.test(obj[14])) {
		commonDto.setInsGrossWt(Double.valueOf(obj[14].toString()));
		}
		if (checkNull.test(obj[15])) {
		commonDto.setInsMeasurement(Double.valueOf(obj[15].toString()));
		}
		if (checkNull.test(obj[16])) {
		commonDto.setInsShipmark4(obj[16].toString());
		}
		if (checkNull.test(obj[17])) {
		commonDto.setInsShipmark5(obj[17].toString());
		}
		if (checkNull.test(obj[18])) {
		commonDto.setShipMarkGp(obj[18].toString());
		}
		if (checkNull.test(obj[19])) {
		commonDto.setCaseMod(obj[19].toString());
		}
		commonDto.setInsCfCd(obj[20].toString());
		if (checkNull.test(obj[21])) {
		commonDto.setInsSrsName(obj[21].toString());
		}
		if (checkNull.test(obj[22])) {
		commonDto.setInsNoOfCases(Double.valueOf(obj[22].toString()));
		}
		
		return commonDto;
	}

	private void setPartDetails(Object[] obj, PackingListCustomBrokerDto commonDto, Predicate<Object> checkNull) {
		if (checkNull.test(obj[7])) {
		commonDto.setInsPartNo(obj[7].toString());
		}
		if (checkNull.test(obj[8])) {
		commonDto.setInsPartName(obj[8].toString());
		}
	}

	private void setInvoiceDetails(Object[] obj, PackingListCustomBrokerDto commonDto, Predicate<Object> checkNull) {
		if (checkNull.test(obj[5])) {
		commonDto.setInsInvNo(obj[5].toString());
		}
		if (checkNull.test(obj[6])) {
		commonDto.setInsInvDt(obj[6].toString());
		}
	}

	private void setInsCngsDetails(Object[] obj, PackingListCustomBrokerDto commonDto, Predicate<Object> checkNull) {
		if (checkNull.test(obj[0]))
			commonDto.setInsCnsgName(obj[0].toString());
		if (checkNull.test(obj[1]))
			commonDto.setInsCnsgAdd1(obj[1].toString());
		if (checkNull.test(obj[2]))
			commonDto.setInsCnsgAdd2(obj[2].toString());
		if (checkNull.test(obj[3]))
			commonDto.setInsCnsgAdd3(obj[3].toString());
		if (checkNull.test(obj[4])) {
			commonDto.setInsCnsgAdd4(obj[4].toString());
		}
	}
}
	

