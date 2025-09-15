package com.tpex.invoice.service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Tuple;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.invoice.dto.DownloadInvoiceReportsRequestDTO;
import com.tpex.invoice.dto.WorkInstrReportDTO;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvContainerDtlsRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.TpexConfigRepository;

@Service
public class WorkInstructionReportServiceImpl implements WorkInstructionReportService{


	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	private JasperReportService jasperReportService;

	@Autowired
	private InsInvContainerDtlsRepository insInvContainerDtlsRepository;

	@Autowired
	OemParameterRepository oemParameterRepository;


	@SuppressWarnings("unused")
	@Override
	public Object getRegularWrkInstructionRptDownload(String etd,String countryCd,String bookingNo,String userId, String reportFormat, String reportId) throws Exception {

		List<WorkInstrReportDTO> resultlist = new ArrayList<>();

		if(bookingNo.isBlank())
			bookingNo = null;

		SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

		String formatETD = outputFormat.format(inputFormat.parse(etd));


		List<Tuple> list = insInvContainerDtlsRepository.getListOfBookingNum(formatETD,bookingNo,countryCd);


		for(Tuple obj : list) {

			String bookingNum = null;
			String haisenNo = null;
			String haisenYrMnth = null;

			if(obj.get(0)!=null)
				bookingNum= obj.get(0).toString();
			if(obj.get(1)!=null)
				haisenNo= obj.get(1).toString();
			if(obj.get(2)!=null)
				haisenYrMnth= obj.get(2).toString();



			Tuple bookingDetails = insInvContainerDtlsRepository.getDataForBookingNum(bookingNum,haisenNo,haisenYrMnth);

			Integer invoiceCount = insInvContainerDtlsRepository.getInvoiceDataCount(bookingNum,haisenNo,haisenYrMnth);

			Integer rRackCount = insInvContainerDtlsRepository.getRRackDataCount(bookingNum,haisenNo,haisenYrMnth);

			String parameter = oemParameterRepository.getParameter();

			List rBoxDataList = insInvContainerDtlsRepository.getRBoxDataCount(bookingNum,haisenNo,haisenYrMnth,parameter);

			String rBoxData = (String) rBoxDataList.stream().collect(Collectors.joining(","));

			Tuple nonDGCount = insInvContainerDtlsRepository.getNonDgCount(bookingNum,haisenNo,haisenYrMnth);

			Tuple dGCount = insInvContainerDtlsRepository.getDgCount(bookingNum,haisenNo,haisenYrMnth);

			Object model = insInvContainerDtlsRepository.getDataForSeries(bookingNum,haisenNo,haisenYrMnth);

			List<Tuple> invoiceData = insInvContainerDtlsRepository.getInvoiceData(bookingNum,haisenNo,haisenYrMnth);

			SimpleDateFormat month_date = new SimpleDateFormat("MMM-yy");
			SimpleDateFormat iformat = new SimpleDateFormat("yyyyMM");

			DecimalFormat formatter = new DecimalFormat("###,###,##0.00");

			List<WorkInstrReportDTO> dtoList = new ArrayList<>();

			if(invoiceData!=null && invoiceData.size()!=0) {

				for(Tuple t : invoiceData) {

					WorkInstrReportDTO dto = new WorkInstrReportDTO();

					if(t.get(0,String.class) != null)
						dto.setIICD_INV_NO(t.get(0).toString());
					if(t.get(1)!=null)
						dto.setINM_MOD_NO(Integer.parseInt(t.get(1).toString()));
					if(t.get(2)!=null) {
						String invoiceAmnt = t.get(2).toString();
						dto.setIND_INV_AMT(formatter.format(Double.parseDouble(invoiceAmnt)));
					}
					if(t.get(3)!=null)
						dto.setINP_SERIES(t.get(3).toString());
					if(t.get(4)!=null) {
						String month_name = month_date.format(iformat.parse(t.get(4).toString()));
						dto.setINP_PCK_MTH(month_name.toUpperCase());
					}
					if(t.get(5)!=null) 
						dto.setINP_AICO_NAME(t.get(5).toString());

					dtoList.add(dto);
				}

			}

			if(dtoList.size()!=0) {
				for(WorkInstrReportDTO r : dtoList) {

					r.setIICD_BKG_NO(bookingNum);
					r.setIND_HAISEN_NO(haisenNo);

					if(bookingDetails!=null) {
						if(bookingDetails.get(1)!=null)
							r.setIND_FINAL_DST(bookingDetails.get(1).toString());
						if(bookingDetails.get(2)!=null)
							r.setIICD_VAN_PLNT_CD(bookingDetails.get(2).toString().toUpperCase());
						if(bookingDetails.get(3)!=null)
							r.setINM_PCK_MTH(bookingDetails.get(3).toString().toUpperCase());
						if(bookingDetails.get(4)!=null)
							r.setIND_ETD(bookingDetails.get(4).toString().toUpperCase());
						if(bookingDetails.get(5)!=null)
							r.setIND_ETA(bookingDetails.get(5).toString().toUpperCase());
						if(bookingDetails.get(6)!=null)
							r.setIND_VANNING_DT(bookingDetails.get(6).toString().toUpperCase());
						if(bookingDetails.get(7)!=null)
							r.setIND_SHIPPING_COMP(bookingDetails.get(7).toString());
						if(bookingDetails.get(8)!=null)
							r.setIND_VESSEL_NAME_OCEAN(bookingDetails.get(8).toString());
						if(bookingDetails.get(9)!=null)
							r.setIND_VOYAGE_NO_OCEAN(bookingDetails.get(9).toString());
						if(bookingDetails.get(0)!=null)
							r.setIND_BUYER(bookingDetails.get(0).toString());
					}

					if(invoiceCount!=null)
						r.setINV_COUNT(invoiceCount);

					if(rRackCount!=null)
						r.setRACK_COUNT(String.valueOf(rRackCount));

					if(nonDGCount!=null) {
						if(nonDGCount.get(0)!=null)
							r.setNON_DG_40(nonDGCount.get(0).toString());
						if(nonDGCount.get(1)!=null)
							r.setNON_DG_20(nonDGCount.get(1).toString());
					}

					if(dGCount!=null) {
						if(dGCount.get(0)!=null)
							r.setDG_40(dGCount.get(0).toString());
						if(dGCount.get(1)!=null)
							r.setDG_20(dGCount.get(1).toString());
					}

					if(model!=null)
						r.setMODEL(model.toString());

					if(rBoxData!=null)
						r.setRBOXDATA_CNT(String.valueOf(rBoxData));
					//r.setRBOXDATA_CNT(String.valueOf(rRackCount));

					resultlist.add(r);
				}
			}
			else
			{

				WorkInstrReportDTO rpt = new WorkInstrReportDTO();

				rpt.setIICD_BKG_NO(bookingNum);
				rpt.setIND_HAISEN_NO(haisenNo);

				if(bookingDetails!=null) {
					if(bookingDetails.get(1)!=null)
						rpt.setIND_FINAL_DST(bookingDetails.get(1).toString());
					if(bookingDetails.get(2)!=null)
						rpt.setIICD_VAN_PLNT_CD(bookingDetails.get(2).toString().toUpperCase());
					if(bookingDetails.get(3)!=null)
						rpt.setINM_PCK_MTH(bookingDetails.get(3).toString().toUpperCase());
					if(bookingDetails.get(4)!=null)
						rpt.setIND_ETD(bookingDetails.get(4).toString().toUpperCase());
					if(bookingDetails.get(5)!=null)
						rpt.setIND_ETA(bookingDetails.get(5).toString().toUpperCase());
					if(bookingDetails.get(6)!=null)
						rpt.setIND_VANNING_DT(bookingDetails.get(6).toString().toUpperCase());
					if(bookingDetails.get(7)!=null)
						rpt.setIND_SHIPPING_COMP(bookingDetails.get(7).toString());
					if(bookingDetails.get(8)!=null)
						rpt.setIND_VESSEL_NAME_OCEAN(bookingDetails.get(8).toString());
					if(bookingDetails.get(9)!=null)
						rpt.setIND_VOYAGE_NO_OCEAN(bookingDetails.get(9).toString());
					if(bookingDetails.get(0)!=null)
						rpt.setIND_BUYER(bookingDetails.get(0).toString());
				}

				if(invoiceCount!=null)
					rpt.setINV_COUNT(invoiceCount);

				if(rRackCount!=null)
					rpt.setRACK_COUNT(String.valueOf(rRackCount));

				if(nonDGCount!=null) {
					if(nonDGCount.get(0)!=null)
						rpt.setNON_DG_40(nonDGCount.get(0).toString());
					if(nonDGCount.get(1)!=null)
						rpt.setNON_DG_20(nonDGCount.get(1).toString());
				}

				if(dGCount!=null) {
					if(dGCount.get(0)!=null)
						rpt.setDG_40(dGCount.get(0).toString());
					if(dGCount.get(1)!=null)
						rpt.setDG_20(dGCount.get(1).toString());
				}

				if(model!=null)
					rpt.setMODEL(model.toString());

				if(rBoxData!=null)
					rpt.setRBOXDATA_CNT(String.valueOf(rBoxData));
				//r.setRBOXDATA_CNT(String.valueOf(rRackCount));

				resultlist.add(rpt);

			}
		}

		Object jasperResponse = null;
		Map<String, Object> parameters = new HashMap<>();
		String reportDownloadUrl = null;

		parameters.put("P_I_V_ETD_FROM", etd);
		parameters.put("P_I_V_ETD_TO", etd);
		parameters.put("P_I_V_BOOK_NO", bookingNo);
		parameters.put("P_I_V_COUNTRY_CD", countryCd);
		parameters.put("P_I_V_USER_ID", userId);
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";
		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		if(StringUtils.isNotBlank(fileFormat) && fileFormat.equals("xlsx")) {
			config.put("setFontSizeFixEnabled", true);
		}
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("reportSizeLimit",
				tpexConfigRepository.findByName("invoiceGeneration.report.size.limit").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");
		//String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat :"pdf";

		SimpleDateFormat reqFormat = new SimpleDateFormat("ddMMMyy");
		String ETDDate = reqFormat.format(inputFormat.parse(etd));

		String fileName = null;

		if(bookingNo!=null) {
			fileName = "WorkInstruction"+"_"+countryCd+"_"+"ETD"+ETDDate.toUpperCase()+"_"+bookingNo+"."+fileFormat;
		}else {
			fileName = "WorkInstruction"+"_"+countryCd+"_"+"ETD"+ETDDate.toUpperCase()+"."+fileFormat;
		}

		StringBuilder sb = new StringBuilder().append(String.valueOf(config.get("reportDirectory"))).append("/").append(fileName);

		String reportName = "RINS105";

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(resultlist, fileFormat, reportName, fileName, parameters, config);
		}else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(resultlist, fileFormat, reportName, parameters, config, 0, sb, fileName);

		}
		return jasperResponse;
	}

}
