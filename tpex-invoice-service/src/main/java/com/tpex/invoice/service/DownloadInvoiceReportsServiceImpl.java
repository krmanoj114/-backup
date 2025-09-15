package com.tpex.invoice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRParameter;

//TODO: Need to remove this dummy class
@Service
public class DownloadInvoiceReportsServiceImpl implements DownloadInvoiceReportsService{

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Override
	public Object generateInvoiceReports(String loginUserId,String orderType,String invoiceNumber,String etd,String[] dest,String reportName,boolean isExcel) throws Exception{
          
		//Adding some dummy data for testing purpose
		List<DownloadInvoiceReportsRequestDTO> list = new ArrayList<>();

        DownloadInvoiceReportsRequestDTO dto =  new DownloadInvoiceReportsRequestDTO();
        dto.setOrderType(orderType);
		dto.setInvoiceNumber(invoiceNumber);
		dto.setReportName("Invoice Reports");
		list.add(dto);
		
        DownloadInvoiceReportsRequestDTO dto1 =  new DownloadInvoiceReportsRequestDTO();
        dto1.setOrderType(orderType);
        dto1.setInvoiceNumber(invoiceNumber);
        dto1.setReportName("Invoice Reports downlaoded");
		list.add(dto1);
        
		
		//Map<String, Object> parameters = new HashMap<>();
		Map<String, Object> parameters = getReportDynamicPrameters();

		//Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("setWhitePageBackground", false);
		config.put("invoiceGeneration.report.directory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("invoiceGeneration.report.format", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("invoiceGeneration.report.size.limit", 1);
		config.put("invoiceGeneration.storeInDB", "true");
        config.put("loginUserId", loginUserId);
		String fileTemplateName;
		fileTemplateName = reportName;
		String reportFormat = null;
		if(isExcel)
		{
			 reportFormat = "xlsx";
		}
		else
		{
			 reportFormat = "pdf";
		}
		

		Object fileLink = null;
		//fileLink = this.jasperReportService.generateReport(list,reportFormat, fileTemplateName,parameters, config);
		Object jasperResponse = jasperReportService.getJasperReportDownloadOnline(list, reportFormat, fileTemplateName, null, parameters, config);

        //return fileLink;
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
	 
}


