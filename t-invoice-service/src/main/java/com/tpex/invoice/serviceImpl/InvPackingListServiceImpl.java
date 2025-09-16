package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.InsModuleDtlsDto;
import com.tpex.invoice.dto.InvPackingListResponseDTO;
import com.tpex.invoice.dto.PartDetailDataDTO;
import com.tpex.invoice.service.InvPackingListService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InvPackingListRepository;
import com.tpex.repository.TpexConfigRepository;

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
public class InvPackingListServiceImpl implements InvPackingListService {

	@Autowired
	JasperReportService jasperReportService;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	InvPackingListRepository invPackingListRepository;

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Object getInvPackingListRptDownload(String invNumber, String fileTemplateName,
			String reportFormat) throws FileNotFoundException, JRException {

		Object response = null;
		Map<String, Object> parameters = new HashMap<>();

		parameters.put("P_I_V_INVOICE_NO", invNumber);

		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put("setSizePageToContent", true);
		config.put("setForceLineBreakPolicy", false);
		config.put("reportDirectory", tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue());
		config.put("reportFormat", tpexConfigRepository.findByName("invoiceGeneration.report.format").getValue());
		config.put("storeInDB", "true");
		config.put("loginUserId", "TestUser");
		String fileFormat = StringUtils.isNotBlank(reportFormat)
				&& "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat : "pdf";
		String fileName = invNumber + "_PAC." + fileFormat;

		List<InvPackingListResponseDTO> invPackingListResponseDTOList = getPackingListData(invNumber);

		if ("xlsx".equals(fileFormat)) {
			response = jasperReportService.getJasperReportDownloadOnline(invPackingListResponseDTOList, fileFormat, fileTemplateName, fileName, parameters, config);
		
		}else {
			response = jasperReportService.getJasperReportDownloadOfflineV1(invPackingListResponseDTOList, fileFormat, fileTemplateName, parameters, config, 0, fileName);

		}
		return response;
	}
	
	private List<InvPackingListResponseDTO> getPackingListData(String invNumber) {

		List<InvPackingListResponseDTO> invPackingListResponseDTOList = new ArrayList<>();
		List<PartDetailDataDTO> partDetailDataDTOList = null;

		String companyCode = invPackingListRepository.getCompanyCode();
		if (companyCode == null) {
			throw new InvalidInputParametersException();
		}

		String compCode = invPackingListRepository.getCompCode(invNumber);

		String tmapthInvFlg = invPackingListRepository.getTmapthInvFlg(invNumber);
		
		Tuple invCompDetail = ("Y".equalsIgnoreCase(tmapthInvFlg)) ? invPackingListRepository.getInvCompDetailWhenFlgY(companyCode, compCode) : invPackingListRepository.getInvCompDetailWhenFlgN(companyCode);

		List<Tuple> partDetailData = invPackingListRepository.getPartDetailData(invNumber);

		partDetailDataDTOList = partDetailData.stream()
				.map(t -> new PartDetailDataDTO(t.get(0, String.class), t.get(1, String.class),
						t.get(2, BigDecimal.class), t.get(3, BigDecimal.class), t.get(4, String.class),
						t.get(5, String.class), t.get(6, String.class), t.get(7, String.class), t.get(8, String.class),
						t.get(9, String.class), t.get(10, BigDecimal.class))

				).collect(Collectors.toList());

		List<Tuple> list1 = invPackingListRepository.getData(invNumber);
		List<InsModuleDtlsDto> insModuleDtlsDtoList = null;

		insModuleDtlsDtoList = list1.stream()
				.map(t -> new InsModuleDtlsDto(String.valueOf(t.get(0, String.class)), t.get(1, BigDecimal.class),
						t.get(2, BigDecimal.class), new BigDecimal(t.get(3, BigInteger.class).toString())))
				.collect(Collectors.toList());

		Map<String, InsModuleDtlsDto> map = new HashMap<>();
		for (InsModuleDtlsDto mapObj : insModuleDtlsDtoList) {
			map.put(mapObj.getCfCdModNo(), mapObj);
		}
		if (!partDetailDataDTOList.isEmpty()) {
			invPackingListResponseDTOList = getInvPackingList(partDetailDataDTOList, invCompDetail, map, invNumber, companyCode);
		}
		return invPackingListResponseDTOList;
	}
	
	private List<InvPackingListResponseDTO> getInvPackingList(List<PartDetailDataDTO> partDetailDataDTOList, Tuple invCompDetail, Map<String, InsModuleDtlsDto> map, String invNumber, String companyCode) {
		List<InvPackingListResponseDTO> invPackingListResponseDTOList = new ArrayList<>();
		String invCompDetail1 = invCompDetail.get(0, String.class);
		String invCompDetail2 = invCompDetail.get(1, String.class);
		String invCompDetail3 = invCompDetail.get(2, String.class);
		String invCompDetail4 = invCompDetail.get(3, String.class);
		String invCompDetail5 = invCompDetail.get(4, String.class);
		
		int lnCtr = 0;
		String lnPrevMod = null;
		String lvPrevCfCd = null;
		BigDecimal grossWeight = new BigDecimal("0");
		BigDecimal measurement = new BigDecimal("0");
		BigDecimal noOfModule = new BigDecimal("0");
		
		for (PartDetailDataDTO partDetailDataDTO : partDetailDataDTOList) {

			if (lnCtr == 0) {
				lnPrevMod = partDetailDataDTO.getShipMark();
				lvPrevCfCd = partDetailDataDTO.getCfCd();
				String key = partDetailDataDTO.getCfCd() + "-" + partDetailDataDTO.getShipMark();
				grossWeight = getGrossWeight(map, key);
				measurement = getMeasurement(map, key);
				noOfModule = getNoOfModule(map, key);
			}

			if ((lnPrevMod != null && !lnPrevMod.equals(partDetailDataDTO.getShipMark()))
					|| (lvPrevCfCd != null && !lvPrevCfCd.equals(partDetailDataDTO.getCfCd()))) {

				String key = partDetailDataDTO.getCfCd() + "-" + partDetailDataDTO.getShipMark();
				grossWeight = getGrossWeight(map, key);
				measurement = getMeasurement(map, key);
				noOfModule = getNoOfModule(map, key);
			}
			if (partDetailDataDTO.getPartWt() == null) {
				noOfModule = new BigDecimal("0");
			}

			InvPackingListResponseDTO invPackingListResponseDTO = new InvPackingListResponseDTO();

			invPackingListResponseDTO.setCnsgName(invCompDetail1);
			invPackingListResponseDTO.setCnsgAdd1(invCompDetail2);
			invPackingListResponseDTO.setCnsgAdd2(invCompDetail3);
			invPackingListResponseDTO.setCnsgAdd3(invCompDetail4);
			invPackingListResponseDTO.setCnsgAdd4(invCompDetail5);
			invPackingListResponseDTO.setInvNo(invNumber);
			invPackingListResponseDTO.setInvDt(partDetailDataDTO.getInvDt());
			invPackingListResponseDTO.setPartNo(partDetailDataDTO.getPartNo());
			invPackingListResponseDTO.setUnitPerBox(partDetailDataDTO.getUnitBox().intValue());
			invPackingListResponseDTO.setSumTotalUnit(partDetailDataDTO.getSumTotalUnit().intValue());
			invPackingListResponseDTO.setPartName(partDetailDataDTO.getPartName());
			invPackingListResponseDTO.setPartWt(partDetailDataDTO.getPartWt().doubleValue());
			invPackingListResponseDTO.setGrossWt(grossWeight.doubleValue());
			invPackingListResponseDTO.setMeasurement(measurement.doubleValue());
			invPackingListResponseDTO.setShipMark4(partDetailDataDTO.getShipMark());
			invPackingListResponseDTO.setShipMark5(partDetailDataDTO.getShipMark4());
			invPackingListResponseDTO.setShipMarkGp(partDetailDataDTO.getShipMark());
			invPackingListResponseDTO
					.setCaseMod((partDetailDataDTO.getShipMark().substring(0, 3).equals(companyCode))
							? (partDetailDataDTO.getShipMark().substring(0, 3) + "-"
									+ partDetailDataDTO.getShipMark().substring(3, 4) + "-"
									+ partDetailDataDTO.getShipMark().substring(4, 5) + "-"
									+ partDetailDataDTO.getShipMark().substring(5))
							: partDetailDataDTO.getShipMark());
			invPackingListResponseDTO.setCfCd(partDetailDataDTO.getCfCd());
			invPackingListResponseDTO.setSrsName(partDetailDataDTO.getSeries());
			invPackingListResponseDTO.setNoOfCases(noOfModule.intValue());

			lnPrevMod = partDetailDataDTO.getShipMark();
			lvPrevCfCd = partDetailDataDTO.getCfCd();
			lnCtr = lnCtr + 1;

			invPackingListResponseDTOList.add(invPackingListResponseDTO);
		}
		return invPackingListResponseDTOList;
	}
	
	private BigDecimal getGrossWeight(Map<String, InsModuleDtlsDto> map, String key) {
		return map.get(key) != null && map.get(key).getGrossWt() != null ? map.get(key).getGrossWt() : new BigDecimal("0");
	}
	
	private BigDecimal getMeasurement(Map<String, InsModuleDtlsDto> map, String key) {
		return map.get(key) != null && map.get(key).getTotalM3() != null ? map.get(key).getTotalM3()	: new BigDecimal("0");
	}
	
	private BigDecimal getNoOfModule(Map<String, InsModuleDtlsDto> map, String key) {
		return map.get(key) != null && map.get(key).getCCount() != null ? map.get(key).getCCount() : new BigDecimal("0");
	}

}
