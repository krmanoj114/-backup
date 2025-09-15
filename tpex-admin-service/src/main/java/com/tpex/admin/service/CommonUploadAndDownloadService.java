package com.tpex.admin.service;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.DecoderException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.tpex.admin.dto.ContainerRequisitionRequestDTO;
import com.tpex.admin.dto.LotPartShortageReportsRequestDto;
import com.tpex.admin.dto.ProcessBatchDTO;
import com.tpex.admin.dto.ReportNameDTO;
import com.tpex.admin.dto.UploadCountryOfOriginJobDto;
import com.tpex.admin.dto.UploadWrkPlanMasterJobDto;
import com.tpex.admin.entity.OemProcessCtrlEntity;

import net.sf.jasperreports.engine.JRException;

/**
 * The Interface CommonUploadAndDownloadService.
 */
public interface CommonUploadAndDownloadService {

	List<OemProcessCtrlEntity> fetchProcessDetails(String userId);

	InputStream downloadTemplate(List<ReportNameDTO> listOfReportNames, Integer reportId, String sheetName)
			throws IOException;

	Object downloadNatCalMast(String year, String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException;

	void jobServiceReceiving(String batchName, String fileName, OemProcessCtrlEntity oemProcessCtrlEntity);

	OemProcessCtrlEntity saveProcessDetails(String userId, String batchParameter);

	Map<String, String> getProcessNameDetails();

	String getProcessNameById(String string);

	Object downloadLotPartPriceMast(String loginUserId, boolean isdownloadTemplate) throws JRException, IOException;

	Object downloadPxpPartPriceMast(String loginUserId, boolean isdownloadTemplate) throws JRException, IOException;

	OemProcessCtrlEntity saveProcessDetailsLot(String userId, String batchParameter);

	ResponseEntity<List<ProcessBatchDTO>> batchProcess(String batchName, String userId, String effectiveFrom,
			String effectiveTo, MultipartFile file) throws IOException;

	Object downloadAddressMaster(String cmpCode, String loginUserId, boolean isdownloadTemplate)
			throws JRException, IOException;

	byte[] getCountryofOriginMastListReport(String partPriceNo, List<String> countryOfCountryCode, String vanDateFrom,
			String vanDateTo) throws IOException, DecoderException, ParseException;

	OemProcessCtrlEntity saveProcessCtrlDetail(String userId, String batchParameter, String batchId);

	Object downloadAddressMasterTemplate(String userId) throws JRException, IOException;

	void uploadAddressMaster(String batchName, String originalFilename, OemProcessCtrlEntity oemProcessCtrlEntity,
			String companyCode, String userId);


	byte[] getCarFmlyDestnMstListReport(HttpServletResponse reponse, String cmpCode, String userId)
			throws IOException, DecoderException, ParseException;

	List<ProcessBatchDTO> uplaodCarFamilyDestinationMaster(String batchName, MultipartFile file, String companyCode,
			String userId) throws IOException;

	byte[] downloadCarFamilyDestinationMaster(String loginUserId) throws IOException;

	byte[] downloadCountryOfOriginTemplate(String userId, HttpServletResponse response) throws JRException, IOException;


	public Object generateLostPartShortageReport(LotPartShortageReportsRequestDto request)
			throws JRException, IOException, ParseException;

	String uploadWrkPlanMasterBatchServiceCall(UploadWrkPlanMasterJobDto uploadWrkPlanMasterJobDto);

	String countryCodeofOriginBatchServiceCall(UploadCountryOfOriginJobDto uploadCountryOfOriginJobDto);
	
	String uploadPartMasterBatchServiceCall(String batchName, MultipartFile file, String companyCode,
			String userId);

	Object generateContainerRequisitionSheet(ContainerRequisitionRequestDTO request) throws ParseException, JRException, IOException;
}
