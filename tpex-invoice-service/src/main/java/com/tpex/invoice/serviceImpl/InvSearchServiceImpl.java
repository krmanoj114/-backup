package com.tpex.invoice.serviceimpl;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.SearchInvHaisenDetailResponse;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.InsProdGrpMstEntity;
import com.tpex.entity.OemCnshMst;
import com.tpex.entity.OemCurrencyMstEntity;
import com.tpex.entity.OemPmntTermMstEntity;
import com.tpex.entity.OemPortMstEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.CarFamilyDto;
import com.tpex.invoice.dto.ConsigneeCompanyNameDto;
import com.tpex.invoice.dto.CustomerCodeDtoObj;
import com.tpex.invoice.dto.InvoiceDetailsResponseDto;
import com.tpex.invoice.dto.InvoiceDetailsResponseWrapper;
import com.tpex.invoice.dto.NotifyCodeDtoObj;
import com.tpex.invoice.dto.PaymentTermObjDto;
import com.tpex.invoice.dto.PortGrpObjDto;
import com.tpex.invoice.dto.ProductGrpObjDto;
import com.tpex.invoice.dto.SearchByInvNoResponseDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;
import com.tpex.invoice.dto.SearchInvHaisenDetailResponseDto;
import com.tpex.invoice.dto.UpdateInvDetailsRequestDTO;
import com.tpex.invoice.dto.InvAttachedSheetResponseDto;
import com.tpex.invoice.service.InvSearchService;
import com.tpex.jasperreport.service.JasperReportService;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.InsProdGrpMstRepository;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.repository.OemCnshMstRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.repository.OemPmntTermMstRepository;
import com.tpex.repository.OemPortMstRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;
import com.tpex.util.Util;

import net.sf.jasperreports.engine.JRException;

@Service
@Transactional
@SuppressWarnings("squid:S3776")
public class InvSearchServiceImpl implements InvSearchService {

	@Autowired
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;

	@Autowired
	InsInvDetailsRepository insInvDetailsRepository;

	@Autowired
	InsProdGrpMstRepository insProdGrpMstRepository;

	@Autowired
	OemCnshMstRepository oemCnshMstRepository;

	@Autowired
	OemPortMstRepository oemPortMstRepository;

	@Autowired
	OemCurrencyMstRepository oemCurrencyMstRepository;

	@Autowired
	OemPmntTermMstRepository oemPmntTermMstRepository;

	@Autowired
	OemParameterRepository oemParameterRepository;

	@Autowired
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;

	@Autowired
	TpexConfigRepository tpexConfigRepository;

	@Autowired
	JasperReportService jasperReportService;

	/**
	 * Method for search record based on search criteria
	 * 
	 * @param etdate , etaDate, buyer and regular or CPO/SPO
	 */
	DateFormat objFormate = new SimpleDateFormat("dd/MM/yyyy");
	DateFormat objFormateYear = new SimpleDateFormat("yyyy/mm");
	SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public SearchInvHaisenDetailResponse fetchHaisenDetail(
			SearchInvHaisenDetailRequestDto searchInvHaisenDetailRequestDto, String regular, String cpo, String spo)
			throws ParseException {

		SearchInvHaisenDetailResponse resp = null;

		LocalDate etdFrom = searchInvHaisenDetailRequestDto.getEtdFrom();
		LocalDate etdTo = searchInvHaisenDetailRequestDto.getEtdTo();
		String buyer = searchInvHaisenDetailRequestDto.getBuyer();

		if (buyer.equalsIgnoreCase("")) {
			buyer = null;
		}
		if (etdTo == null) {
			etdTo = LocalDate.now();
		}
		List<Object[]> response = null;
		if (etdFrom != null) {
			response = noemHaisenDtlsRepository.getHaisenDetails(etdFrom, etdTo, buyer, regular, cpo, spo);
		}

		if (!response.isEmpty()) {
			resp = new SearchInvHaisenDetailResponse();
		}
		List<SearchInvHaisenDetailResponseDto> mainObj = new ArrayList<>();

		Map<String, String> map = new HashMap<>();
		List<OemPortMstEntity> emPortMstObj = oemPortMstRepository.findAll();
		List<PortGrpObjDto> listdto = new ArrayList<>();
		populatePartDetails(map, emPortMstObj, listdto);

		if (response != null && !response.isEmpty()) {
			for (Object[] obj : response) {

				SearchInvHaisenDetailResponseDto dto = new SearchInvHaisenDetailResponseDto();

				if (obj[0] != null)
					dto.setHaisenYearMonth(obj[0].toString().substring(0, 4) + "/" + obj[0].toString().substring(4, 6));
				if (obj[1] != null)
					dto.setHaisenNo(obj[1].toString());

				String formatEtdDate = objFormate.format(inputFormat.parse(obj[2].toString()));
				Date etdDate = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE, formatEtdDate);

				String formatEtaDate = objFormate.format(inputFormat.parse(obj[3].toString()));
				Date etaDate = DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE, formatEtaDate);

				dto.setEtd(objFormate.format(etdDate));
				dto.setEta(objFormate.format(etaDate));

				if (obj[4] != null)
					dto.setBuyer(obj[4].toString());
				if (obj[5] != null)
					dto.setOceanVessel(obj[5].toString());
				if (obj[6] != null)
					dto.setOceanVoyage(obj[6].toString());
				if (obj[7] != null)
					dto.setNoOf20ftContainer(Integer.valueOf(obj[7].toString()));
				if (obj[8] != null)
					dto.setNoOf40ftContainer(Integer.valueOf(obj[8].toString()));
				DecimalFormat df = new DecimalFormat("0.00");
				if (obj[9] != null)
					dto.setContainerEfficiency(df.format(Double.valueOf(obj[9].toString())));
				if (obj[10] != null)
					dto.setShipCompName(obj[10].toString());
				if (obj[11] != null) {
					String haisenPortOfLoadingCode = obj[11].toString();
					dto.setPortOfLoading(haisenPortOfLoadingCode + "-" + map.get(haisenPortOfLoadingCode));
				}
				if (obj[12] != null) {
					String haisenPortOfDischargeCode = obj[12].toString();
					dto.setPortOfDischarge(haisenPortOfDischargeCode + "-" + map.get(haisenPortOfDischargeCode));
				}
				mainObj.add(dto);
			}
		}

		List<SearchInvHaisenDetailResponseDto> resultDto = new ArrayList<>();

		for (SearchInvHaisenDetailResponseDto dt : mainObj) {
			List<String> list1 = new ArrayList<>();
			List<String> list2 = new ArrayList<>();

			List<Object[]> feederDeails = noemHaisenDtlsRepository.getfeederDetailsForHaisenNo(dt.getHaisenNo(),
					dt.getHaisenYearMonth().replace("/", ""), buyer, regular, cpo, spo);

			for (Object[] fieldArray : feederDeails) {
				if (fieldArray[2] != null && !"null".equals(fieldArray[2])) {
					list1.add(fieldArray[2].toString());
				}
				if (fieldArray[3] != null && !"null".equals(fieldArray[3])) {
					list2.add(fieldArray[3].toString());
				}
				if (feederDeails.size() > 1) {
					dt.setFlag(true);
					break;
				}
			}

			dt.setFeederVessel(list1);
			dt.setFeederVoyage(list2);
			resultDto.add(dt);
		}

		resp.setListInvHaisenDetailResponse(resultDto);
		resp.setPortGrpObj(listdto);

		return resp;
	}

	/**
	 * the methd search data based on invoice no
	 */
	@Override
	public SearchByInvNoResponseDto searchByInvNo(String envNo) {

		if (envNo == null) {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1013);
		}
		SearchByInvNoResponseDto response = null;
		String dischargePortCode = null;
		String loadingPortCode = null;
		String currencyCode = null;
		String payTermCode = null;
		String productGrpCode = null;
		String indBuyer = null;

		Optional<InsInvDtlsEntity> insInvDetailsEntity = insInvDetailsRepository.findById(envNo);

		if (insInvDetailsEntity.isPresent() && insInvDetailsEntity.get() != null) {
			response = getSearchByInvNoResponseDto(insInvDetailsEntity);
			dischargePortCode = insInvDetailsEntity.get().getIndDepPortCd();
			loadingPortCode = insInvDetailsEntity.get().getIndDestPortCd();
			currencyCode = insInvDetailsEntity.get().getIndPayCrncy();
			payTermCode = insInvDetailsEntity.get().getIndPayTerm();
			productGrpCode = insInvDetailsEntity.get().getIndProdGrpCd();
			indBuyer = insInvDetailsEntity.get().getIndBuyer();
		}

		// tpex 301
		String cntryOfOriginflg = oemParameterRepository.getCountryOfOriginFlg();
		if (response != null) {
			response.setCountryOfOriginFlg(cntryOfOriginflg);
		}
		if (dischargePortCode != null) {
			OemPortMstEntity oemPortMstEntity = oemPortMstRepository.findBydepPortCd(dischargePortCode);
			if (oemPortMstEntity != null && StringUtils.isNotBlank(oemPortMstEntity.getName())) {
				response.setPortOfDischarge(dischargePortCode + "-" + oemPortMstEntity.getName());
			}
		}
		if (loadingPortCode != null) {
			OemPortMstEntity oemPortMstEntity1 = oemPortMstRepository.findBydepPortCd(loadingPortCode);
			if (oemPortMstEntity1 != null && StringUtils.isNotBlank(oemPortMstEntity1.getName())) {
				response.setPortOfLoading(loadingPortCode + "-" + oemPortMstEntity1.getName());
			}
		}
		if (currencyCode != null) {
			Optional<OemCurrencyMstEntity> oemCurrencyMstEntity = oemCurrencyMstRepository.findById(currencyCode);
			if (oemCurrencyMstEntity.isPresent() && oemCurrencyMstEntity.get() != null
					&& StringUtils.isNotBlank(oemCurrencyMstEntity.get().getCrmDesc())
					&& StringUtils.isNotBlank(oemCurrencyMstEntity.get().getCrmCd())) {
				response.setCurrencyCode(
						oemCurrencyMstEntity.get().getCrmCd() + "-" + oemCurrencyMstEntity.get().getCrmDesc());
			}
		}
		if (payTermCode != null) {
			Optional<OemPmntTermMstEntity> oemPmntTermMstEntity = oemPmntTermMstRepository.findById(payTermCode);
			if (oemPmntTermMstEntity.isPresent() && oemPmntTermMstEntity.get() != null
					&& StringUtils.isNotBlank(oemPmntTermMstEntity.get().getPtCd())
					&& StringUtils.isNotBlank(oemPmntTermMstEntity.get().getPtDesc())) {
				response.setPaymentTerm(
						oemPmntTermMstEntity.get().getPtCd() + "-" + oemPmntTermMstEntity.get().getPtDesc());
			}
		}
		List<OemPmntTermMstEntity> oemPmntTermMsObj = oemPmntTermMstRepository.findAll();
		List<PaymentTermObjDto> paymentTermObjDtoList = getPaymentTermObjDtos(oemPmntTermMsObj);
		if (productGrpCode != null) {
			Optional<InsProdGrpMstEntity> insProdGrpMstEntity = insProdGrpMstRepository.findById(productGrpCode);
			if (insProdGrpMstEntity.isPresent() && insProdGrpMstEntity.get() != null
					&& StringUtils.isNotBlank(insProdGrpMstEntity.get().getIpgProdGrpCd())
					&& StringUtils.isNotBlank(insProdGrpMstEntity.get().getIpgProdGrpDesc())) {
				response.setProductGrp(insProdGrpMstEntity.get().getIpgProdGrpCd() + "-"
						+ insProdGrpMstEntity.get().getIpgProdGrpDesc());
			}
		}
		List<InsProdGrpMstEntity> insProdGrpMstEntity = insProdGrpMstRepository.findAllByOrderByIpgProdGrpCd();
		List<ProductGrpObjDto> productGrpObjDtoList = getProdGroup(insProdGrpMstEntity);

		if (indBuyer != null) {
			List<Object[]> authEmp = oemCnshMstRepository.getAuthEmp(indBuyer);
			List<String> list = populateCnshMst(authEmp);
			response.setScAuthorize(list);
		}

		Tuple custCode = insInvDetailsRepository.getCustCode(envNo);

		List<Tuple> carFamily = insInvPartsDetailsRepository.getCarFamily(envNo);
		List<CarFamilyDto> carFamilylist = carFamily.stream()
				.map(t -> new CarFamilyDto(t.get(0, String.class), t.get(1, String.class), t.get(2, String.class)))
				.collect(Collectors.toList());
		if (response != null) {
			response.setPaymentTermObj(paymentTermObjDtoList);
			response.setProductGrpObj(productGrpObjDtoList);
			response.setCarFamilyDto(carFamilylist);
		}

		// CustomerCode Dropdown
		List<OemCnshMst> cnsgCode = oemCnshMstRepository.findAll();
		List<CustomerCodeDtoObj> listdto4 = new ArrayList<>();

		for (OemCnshMst obj : cnsgCode) {
			CustomerCodeDtoObj commonDto1 = new CustomerCodeDtoObj();
			commonDto1.setIndCust(obj.getId().getCmpCd());
			commonDto1.setIndCustNm(obj.getCmpName());
			commonDto1.setIndCustAddr1(obj.getCmpAdd1());
			commonDto1.setIndCustAddr2(obj.getCmpAdd2());
			commonDto1.setIndCustAddr3(obj.getCmpAdd3());
			commonDto1.setIndCustAddr4(obj.getCmpAdd4());
			listdto4.add(commonDto1);
		}
		// notify dropdown
		List<Object[]> listObject5 = oemCnshMstRepository.getCnsgData(envNo);
		List<NotifyCodeDtoObj> listdto5 = new ArrayList<>();
		if (listObject5 != null && !listObject5.isEmpty()) {
			for (Object[] obj : listObject5) {
				NotifyCodeDtoObj commonDto4 = new NotifyCodeDtoObj();
				if (obj[0] != null)
					commonDto4.setIndNotifyName(obj[0].toString());
				if (obj[1] != null)
					commonDto4.setIndNotifyAddr1(obj[1].toString());
				if (obj[2] != null)
					commonDto4.setIndNotifyAddr2(obj[2].toString());
				if (obj[3] != null)
					commonDto4.setIndNotifyAddr3(obj[3].toString());
				if (obj[4] != null)
					commonDto4.setIndNotifyAddr4(obj[4].toString());
				if (obj[5] != null)
					commonDto4.setIndNotify(obj[5].toString());
				listdto5.add(commonDto4);
			}
		}
		// consignee dropdown
		List<Object[]> listObject3 = oemCnshMstRepository.getCnsgData(envNo);
		List<ConsigneeCompanyNameDto> listdto2 = new ArrayList<>();
		if (listObject3 != null && !listObject3.isEmpty()) {
			for (Object[] obj : listObject3) {
				ConsigneeCompanyNameDto commonDto3 = new ConsigneeCompanyNameDto();

				if (obj[0] != null)
					commonDto3.setCmpName(obj[0].toString());
				if (obj[1] != null)
					commonDto3.setCmpAdd1(obj[1].toString());
				if (obj[2] != null)
					commonDto3.setCmpAdd2(obj[2].toString());
				if (obj[3] != null)
					commonDto3.setCmpAdd3(obj[3].toString());
				if (obj[4] != null)
					commonDto3.setCmpAdd4(obj[4].toString());
				if (obj[5] != null)
					commonDto3.setCmpCd(obj[5].toString());
				listdto2.add(commonDto3);
			}
		}
		Tuple consigneeCode = insInvDetailsRepository.getBuyerCode(envNo);
		StringBuilder address = new StringBuilder();
		StringBuilder address1 = new StringBuilder();
		StringBuilder address2 = new StringBuilder();
		if (response != null) {
			response.setNotifyrcodeObj1(listdto5);
			response.setCmpCd((String) consigneeCode.get(0));
			response.setCmpNm((String) consigneeCode.get(1));
			response.setCustomerCode((String) custCode.get(0));
			response.setCustomerCodeName((String) custCode.get(0) + "-" + (String) custCode.get(1));
			response.setNotifyCode((String) custCode.get(2));
			response.setNotifyName((String) custCode.get(3));
			// cnsg address or buyer address
			if (consigneeCode.get(2) != null) {
				address2.append((String) consigneeCode.get(2));
			}
			if (consigneeCode.get(3) != null) {
				address2.append(ConstantUtils.SPACE);
				address2.append((String) consigneeCode.get(3));
			}
			if (consigneeCode.get(4) != null) {
				address2.append(ConstantUtils.SPACE);
				address2.append((String) consigneeCode.get(4));
			}
			if (consigneeCode.get(5) != null) {
				address2.append(ConstantUtils.SPACE);
				address2.append((String) consigneeCode.get(5));
			}
			response.setCnsgCodeAddress(address2.toString());
			if (custCode.get(4) != null) {
				address1.append((String) custCode.get(4));
			}
			if (custCode.get(5) != null) {
				address1.append(ConstantUtils.SPACE);
				address1.append((String) custCode.get(5));
			}
			if (custCode.get(6) != null) {
				address1.append(ConstantUtils.SPACE);
				address1.append((String) custCode.get(6));
			}
			if (custCode.get(7) != null) {
				address1.append(ConstantUtils.SPACE);
				address1.append((String) custCode.get(7));
			}
			response.setCustomerCodeAddress(address1.toString());

			if (custCode.get(8) != null) {
				address.append((String) custCode.get(8));
			}
			if (custCode.get(9) != null) {
				address.append(ConstantUtils.SPACE);
				address.append((String) custCode.get(9));
			}
			if (custCode.get(10) != null) {
				address.append(ConstantUtils.SPACE);
				address.append((String) custCode.get(10));
			}
			if (custCode.get(11) != null) {
				address.append((String) custCode.get(11));
			}
			response.setNotifyCodeAddress(address.toString());
			response.setCompanyNameObj(listdto2);
			response.setCustomercodeObj1(listdto4);

		}
		return response;
	}

	private static List<String> populateCnshMst(List<Object[]> authEmp) {
		StringBuilder address3 = new StringBuilder();
		List<String> list = new ArrayList<>();
		if (authEmp != null && !authEmp.isEmpty()) {
			for (Object[] fieldArray : authEmp) {

				if (fieldArray[0] != null) {
					list.add(fieldArray[0].toString());
				}
				if (fieldArray[1] != null) {
					list.add(fieldArray[1].toString());
				}
				if (fieldArray[2] != null) {
					list.add(fieldArray[2].toString());
				}
				if (fieldArray[5] != null) {
					address3.append(fieldArray[5].toString());
				}
				if (fieldArray[6] != null) {
					address3.append(ConstantUtils.SPACE);
					address3.append(fieldArray[6].toString());
				}
				if (fieldArray[7] != null) {
					address3.append(ConstantUtils.SPACE);
					address3.append(fieldArray[7].toString());
				}
				if (fieldArray[8] != null) {
					address3.append(ConstantUtils.SPACE);
					address3.append(fieldArray[8].toString());
				}
				list.add(address3.toString());

			}

		}
		return list;
	}

	private static List<ProductGrpObjDto> getProdGroup(List<InsProdGrpMstEntity> insProdGrpMstEntity) {
		List<ProductGrpObjDto> productGrpObjDtoList = new ArrayList<>();
		for (InsProdGrpMstEntity obj : insProdGrpMstEntity) {
			ProductGrpObjDto commonDto = new ProductGrpObjDto();
			BeanUtils.copyProperties(obj, commonDto);
			productGrpObjDtoList.add(commonDto);
		}
		return productGrpObjDtoList;
	}

	private static List<PaymentTermObjDto> getPaymentTermObjDtos(List<OemPmntTermMstEntity> oemPmntTermMsObj) {
		List<PaymentTermObjDto> paymentTermObjDtoList = new ArrayList<>();
		for (OemPmntTermMstEntity obj : oemPmntTermMsObj) {
			PaymentTermObjDto commonDto = new PaymentTermObjDto();
			BeanUtils.copyProperties(obj, commonDto);
			paymentTermObjDtoList.add(commonDto);
		}
		return paymentTermObjDtoList;
	}

	private static SearchByInvNoResponseDto getSearchByInvNoResponseDto(
			Optional<InsInvDtlsEntity> insInvDetailsEntity) {
		SearchByInvNoResponseDto response = new SearchByInvNoResponseDto();
		if (insInvDetailsEntity.isPresent()) {
			response.setInvDate(DateUtil.toDate(insInvDetailsEntity.get().getIndInvDt()));
			response.setEtdDate(DateUtil.toDate(insInvDetailsEntity.get().getIndEtd()));
			response.setEtaDate(DateUtil.toDate(insInvDetailsEntity.get().getIndEta()));

			if (insInvDetailsEntity.get().getIndInvTyp().equals("4")
					&& insInvDetailsEntity.get().getIndOrdTyp().equals("R")
					&& insInvDetailsEntity.get().getIndLotPttrn().equals("P")) {
				response.setInvType("MPMI");
			} else {
				response.setInvType("Non-MPMI");
			}

			response.setScInv(insInvDetailsEntity.get().getIndScInvFlag());
			response.setFeederVessel(insInvDetailsEntity.get().getIndVesselNameFeeder());
			response.setFeederVoyage(insInvDetailsEntity.get().getIndVoyageNoFeeder());

			response.setOceanVessel(insInvDetailsEntity.get().getIndvesselnameocean());
			response.setOceanVoyage(insInvDetailsEntity.get().getIndvoyagenoocean());

			DecimalFormat df = new DecimalFormat(ConstantUtils.FORMATFREIGHTINSURANCE);
			if (insInvDetailsEntity.get().getIndFreight() != null) {
				response.setFreight(df.format(insInvDetailsEntity.get().getIndFreight()));
			} else {
				response.setFreight(ConstantUtils.FORMATFREIGHTINSURANCE);
			}
			if (insInvDetailsEntity.get().getIndInsurance() != null) {
				response.setInsurance(df.format(insInvDetailsEntity.get().getIndInsurance()));
			} else {
				response.setInsurance(ConstantUtils.FORMATFREIGHTINSURANCE);
			}

			// tpex 301 start
			response.setShipingMark1(insInvDetailsEntity.get().getIndMark1());
			response.setShipingMark2(insInvDetailsEntity.get().getIndMark2());
			response.setShipingMark3(insInvDetailsEntity.get().getIndMark3());
			response.setShipingMark4(insInvDetailsEntity.get().getIndMark4());
			response.setShipingMark5(insInvDetailsEntity.get().getIndMark5());
			response.setShipingMark6(insInvDetailsEntity.get().getIndMark6());
			response.setShipingMark7(insInvDetailsEntity.get().getIndMark7());
			response.setShipingMark8(insInvDetailsEntity.get().getIndMark8());

			response.setDescription1(insInvDetailsEntity.get().getIndGoodsDesc1());
			response.setDescription2(insInvDetailsEntity.get().getIndGoodsDesc2());
			response.setDescription3(insInvDetailsEntity.get().getIndGoodsDesc3());
			response.setDescription4(insInvDetailsEntity.get().getIndGoodsDesc4());
			response.setDescription5(insInvDetailsEntity.get().getIndGoodsDesc5());
			response.setDescription6(insInvDetailsEntity.get().getIndGoodsDesc6());
		}
		return response;
	}

	/**
	 * @author Mohd.Javed method for get invoice details by haisen No
	 * @throws JsonProcessingException
	 */
	@Override
	public InvoiceDetailsResponseWrapper getInvoiceDetails(String haisenNo, String haisenYear)
			throws JsonProcessingException {

		haisenYear = haisenYear.replace("/", "");
		List<Object> response = noemHaisenDtlsRepository.getInvDetails(haisenNo, haisenYear);
		if (response.isEmpty() || response == null) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}
		InvoiceDetailsResponseWrapper resp = new InvoiceDetailsResponseWrapper();
		List<InvoiceDetailsResponseDto> mainObj = new ArrayList<>();
		Map<String, String> map = new HashMap<>();
		List<OemPortMstEntity> emPortMstObj = oemPortMstRepository.findAllByOrderByCd();
		List<PortGrpObjDto> listdto = new ArrayList<>();
		populatePartDetails(map, emPortMstObj, listdto);
		for (Object obj : response) {

			ObjectMapper objMapper = new ObjectMapper();
			String jsonStringObj = objMapper.writeValueAsString(obj).replace("\"", "");

			InvoiceDetailsResponseDto dto = new InvoiceDetailsResponseDto();
			String[] fieldArray = jsonStringObj.split(",");

			if (fieldArray[0] != null && !"null".equals(fieldArray[0].replace("[", ""))) {
				dto.setHaisenNo(fieldArray[0].replace("[", ""));
			}
			if (fieldArray[1] != null && !"null".equals(fieldArray[1])) {
				dto.setEtdDate(DateUtil.stringDate(fieldArray[1]));
			}
			if (fieldArray[2] != null && !"null".equals(fieldArray[2])) {
				dto.setEtaDate(DateUtil.stringDate(fieldArray[2]));
			}
			if (fieldArray[3] != null && !"null".equals(fieldArray[3])) {
				dto.setOceanVessel(fieldArray[3]);
			}
			if (fieldArray[4] != null && !"null".equals(fieldArray[4])) {
				dto.setOceanVoyage(fieldArray[4]);
			}
			if (fieldArray[5] != null && !"null".equals(fieldArray[5])) {
				dto.setInvoiceNo(fieldArray[5]);
			}
			if (fieldArray[6] != null && !"null".equals(fieldArray[6])) {
				dto.setInvoiceDate(DateUtil.stringDate(fieldArray[6]));
			}
			if (fieldArray[7] != null && !"null".equals(fieldArray[7])) {
				dto.setInvoiceAmount(Util.twoDecimal(Double.valueOf(fieldArray[7])));
			}
			if (fieldArray[8] != null && !"null".equals(fieldArray[8])) {
				dto.setInvoiceM3(Util.threeDecimal(Double.valueOf(fieldArray[8])));
			}
			if (fieldArray[9] != null && !"null".equals(fieldArray[9])) {
				dto.setFeederVessel(fieldArray[9]);
			}
			if (fieldArray[10] != null && !"null".equals(fieldArray[10])) {
				dto.setFeederVoyage(fieldArray[10]);
			}

			if (fieldArray[11] != null && !"null".equals(fieldArray[11])) {
				dto.setBuyer(fieldArray[11]);
			}
			if (fieldArray[12] != null && !"null".equals(fieldArray[12])) {
				dto.setShipCompName(fieldArray[12]);
			}
			if (fieldArray[13] != null && !"null".equals(fieldArray[13])) {
				dto.setPortOfLoading(fieldArray[13] + "-" + map.get(fieldArray[13]));
			}
			if (fieldArray[14].replace("]", "") != null && !"null".equals(fieldArray[14].replace("]", ""))) {
				dto.setPortOfDischarge(
						fieldArray[14].replace("]", "") + "-" + map.get(fieldArray[14].replace("]", "")));
			}
			mainObj.add(dto);
		}
		resp.setPortGrpObjDto(listdto);
		resp.setInvoiceDetailsResponseDto(mainObj);
		return resp;
	}

	private static void populatePartDetails(Map<String, String> map, List<OemPortMstEntity> emPortMstObj,
			List<PortGrpObjDto> listdto) {
		for (OemPortMstEntity portObj : emPortMstObj) {
			PortGrpObjDto commonDto = new PortGrpObjDto();
			BeanUtils.copyProperties(portObj, commonDto);
			map.put(commonDto.getCd(), commonDto.getName());
			listdto.add(commonDto);
		}
	}

	/**
	 * This method for Updating the result Payment Term is substrin the payment term
	 * is getting from two tables Payment term code and description we need to trim
	 * the description and save only Payment term code in INS_INV_DTLS table
	 * 
	 * @author Sravan Update method f invoice details by Invoice No
	 * @input UpdateInvDetailsRequestDTO
	 * 
	 *        Modified for tpex-144
	 * @author Akshatha.E to update shipping mark and goods description details
	 */

	@Override
	public void updateInvDetailsByInvNo(UpdateInvDetailsRequestDTO updateRequest) {

		String invNo = updateRequest.getInvNo().split("-")[0];
		String scAuthorise = updateRequest.getScAuthorize();
		String product = updateRequest.getProductGrpObj();
		String[] productsplit = product.split("-");
		String productGroup = productsplit[0];
		String payment = updateRequest.getPaymentTermObj();
		String[] paymentcode = payment.split("-");
		String paymentTerm = paymentcode[0];
		String custCodeName = updateRequest.getCustCode();
		String[] cust = custCodeName.split("-");
		String custCode = cust[0];
		String custName = cust[1];
		String consineeName = updateRequest.getConsineeName();
		String notifyPartyName = updateRequest.getNotifyPartyName();

		String shippingMark1 = updateRequest.getIndMark1();
		String shippingMark2 = updateRequest.getIndMark2();
		String shippingMark3 = updateRequest.getIndMark3();
		String shippingMark4 = updateRequest.getIndMark4();
		String shippingMark5 = updateRequest.getIndMark5();
		String shippingMark7 = updateRequest.getIndMark7();
		String shippingMark8 = updateRequest.getIndMark8();
		String goodsDesc1 = updateRequest.getIndGoodsDesc1();
		String goodsDesc2 = updateRequest.getIndGoodsDesc2();
		String goodsDesc3 = updateRequest.getIndGoodsDesc3();
		String goodsDesc4 = updateRequest.getIndGoodsDesc4();
		String goodsDesc5 = updateRequest.getIndGoodsDesc5();
		String goodsDesc6 = updateRequest.getIndGoodsDesc6();

		if (productGroup != null && !productGroup.equals("") && paymentTerm != null && !paymentTerm.equals("")
				&& custCode != null && !custCode.equals("")) {

			Optional<InsInvDtlsEntity> insInvDtlsEntityOptional = insInvDetailsRepository.findById(invNo);
			if (insInvDtlsEntityOptional.isPresent()) {
				InsInvDtlsEntity insInvDtlsEntity = insInvDtlsEntityOptional.get();
				insInvDtlsEntity.setIndInvNo(invNo);
				insInvDtlsEntity.setIndScAuthEmp(scAuthorise);
				insInvDtlsEntity.setIndProdGrpCd(productGroup);
				insInvDtlsEntity.setIndPayTerm(paymentTerm);
				insInvDtlsEntity.setIndCust(custCode);
				insInvDtlsEntity.setIndCustNm(custName);
				OemCnshMst consignee = oemCnshMstRepository.findTopByCmpName(consineeName);
				setInvoiceDetails(consineeName, notifyPartyName, insInvDtlsEntity, consignee);
				insInvDtlsEntity.setIndNotifyName(notifyPartyName);
				insInvDtlsEntity.setIndMark1(shippingMark1);
				insInvDtlsEntity.setIndMark2(shippingMark2);
				insInvDtlsEntity.setIndMark3(shippingMark3);
				insInvDtlsEntity.setIndMark4(shippingMark4);
				insInvDtlsEntity.setIndMark5(shippingMark5);
				insInvDtlsEntity.setIndMark7(shippingMark7);
				insInvDtlsEntity.setIndMark8(shippingMark8);
				insInvDtlsEntity.setIndGoodsDesc1(goodsDesc1);
				insInvDtlsEntity.setIndGoodsDesc2(goodsDesc2);
				insInvDtlsEntity.setIndGoodsDesc3(goodsDesc3);
				insInvDtlsEntity.setIndGoodsDesc4(goodsDesc4);
				insInvDtlsEntity.setIndGoodsDesc5(goodsDesc5);
				insInvDtlsEntity.setIndGoodsDesc6(goodsDesc6);
				insInvDtlsEntity.setIndFreight(updateRequest.getFreight().isBlank() ? Double.valueOf(0)
						: Double.valueOf(updateRequest.getFreight().replace(",", "")));
				insInvDtlsEntity.setIndInsurance(updateRequest.getInsurance().isBlank() ? Double.valueOf(0)
						: Double.valueOf(updateRequest.getInsurance().replace(",", "")));
				insInvDetailsRepository.save(insInvDtlsEntity);
			}
		} else {
			throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1004);
		}
	}

	private void setInvoiceDetails(String consineeName, String notifyPartyName, InsInvDtlsEntity insInvDtlsEntity,
			OemCnshMst consignee) {
		if (consignee != null) {
			insInvDtlsEntity.setIndBuyer(consignee.getId().getCmpCd());
		}
		insInvDtlsEntity.setIndBuyerNm(consineeName);
		OemCnshMst notifyParty = oemCnshMstRepository.findTopByCmpName(notifyPartyName);
		if (notifyParty != null) {
			insInvDtlsEntity.setIndNotify(notifyParty.getId().getCmpCd());
		}
	}

	/*
	 * pins003 Anurag Tpex 189 This Method produces Invoice Attached Sheet Details
	 * based on Company_code & User_ID & Incoice_no. *
	 * 
	 */

	public Object downloadPINS103(String cmpCd, String templateId, String invoiceNo, String userId, String reportFormat)
			throws FileNotFoundException, JRException {
		// ****************download report start/
		final Double insUnitPerBo = 0d;
		final Double insPartW = 0d;
		final Double insGrossW = 0d;
		final Double insMeasurem = 0d;
		String insShipma4 = "";
		String insShipma5 = "";
		Object jasperResponse = null;

		List<Object> oprObj = insInvPartsDetailsRepository.getOprParaVal();
		String oprValFlg = oprObj.get(0).toString();
		if (oprValFlg.equals("N")) {
			templateId = ConstantUtils.RINS003A;
		}

		List<Tuple> querydataforpins003 = insInvPartsDetailsRepository.getQuerydataforpins003(invoiceNo);
		Tuple tmapthInvFlgobj = insInvPartsDetailsRepository.getScRemarks(invoiceNo);

		String tmapthInvFlg = tmapthInvFlgobj == null || tmapthInvFlgobj.get(0, String.class) == null ? ""
				: tmapthInvFlgobj.get(0, String.class);

		String scRemarks = tmapthInvFlgobj == null || tmapthInvFlgobj.get(1, String.class) == null ? ""
				: tmapthInvFlgobj.get(1, String.class);

		Tuple invCompDet = null;

		if ("Y".equalsIgnoreCase(tmapthInvFlg)) {
			invCompDet = insInvPartsDetailsRepository.getInvCompDetailsWhenFlgY(cmpCd, invoiceNo);
		} else {
			invCompDet = insInvPartsDetailsRepository.getInvCompDetailWhenFlgN(cmpCd, invoiceNo);
		}
		String invCompDetail1 = null;
		String invCompDetail2 = null;
		String invCompDetail3 = null;
		String invCompDetail4 = null;
		String invCompDetail5 = null;

		if (invCompDet != null) {
			invCompDetail2 = invCompDet.get(1, String.class);
			invCompDetail3 = invCompDet.get(2, String.class);
			invCompDetail4 = invCompDet.get(3, String.class);
			invCompDetail1 = invCompDet.get(0, String.class);
			invCompDetail5 = invCompDet.get(4, String.class);
		}

		final String invCompDetail6 = invCompDetail1;
		final String invCompDetail7 = invCompDetail2;
		final String invCompDetail8 = invCompDetail3;
		final String invCompDetail9 = invCompDetail4;
		final String invCompDetail10 = invCompDetail5;

		List<InvAttachedSheetResponseDto> response = querydataforpins003.stream()
				.map(t -> new InvAttachedSheetResponseDto(invCompDetail6, // INS_CNSG_NAME
						invCompDetail7, // INS_CNSG_ADD1
						invCompDetail8, // INS_CNSG_ADD2
						invCompDetail9, // INS_CNSG_ADD3
						invCompDetail10, // INS_CNSG_ADD4
						invoiceNo, // INS_INV_NO
						t.get(0, String.class), // INV_DT
						t.get(1, String.class), // PART_NO
						insUnitPerBo, // INS_UNIT_PER_BOX
						t.get(2, BigDecimal.class).intValue(), // SUM_OF_TOTAL_UNIT
						t.get(3, String.class), // ICO_FLG
						t.get(4, BigDecimal.class).doubleValue(), // PRICE
						t.get(5, String.class), // PRT_NAME
						insPartW, // INS_PART_WT
						insGrossW, // INS_GROSS_WT
						insMeasurem, // INS_MEASUREMENT
						insShipma4, // INS_SHIPMARK_4
						insShipma5, // INS_SHIPMARK_5
						t.get(6, String.class), // CF_CD
						t.get(7, String.class), // SERIES
						t.get(8, String.class), // CRM_CURR
						t.get(9, String.class), // ICO_DESC
						t.get(10, String.class), // CO_CD
						t.get(11, String.class), // INP_ORG_CRITERIA
						scRemarks, // SC_REMARK
						t.get(12, String.class), // IND_SC_AUTH_EMP
						t.get(13, BigDecimal.class).doubleValue(), // SORT_SEQ
						t.get(14, String.class) // HS_CD
				)).collect(Collectors.toList());

		// Set Configuration Properties
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("P_I_V_INVOICE_NO", invoiceNo);
		parameters.put("P_I_V_USER_ID", userId);
		// Set configuration properties
		Map<String, Object> config = new HashMap<>();
		config.put(ConstantUtils.SIZE_PAGE_TO_CONTENT, true); // "setSizePageToContent"
		config.put(ConstantUtils.FORCE_LINE_BREAK_POLICY, false); // "setForceLineBreakPolicy"
		config.put(ConstantUtils.REPORT_DIRECTORY,
				tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue()); // "reportDirectory"
																													// //"invoiceGeneration.report.directory"
		config.put(ConstantUtils.REPORT_FORMAT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_FORMAT).getValue());// "reportFormat"
																											// //invoiceGeneration.report.format"
		config.put(ConstantUtils.REPORT_SIZE_LIMIT,
				tpexConfigRepository.findByName(ConstantUtils.INVOICE_GENERATION_REPORT_SIZE_LIMIT).getValue()); // "reportSizeLimit"
																													// //"invoiceGeneration.report.size.limit"
		config.put(ConstantUtils.STORE_DB, "true");// "storeInDB"
		config.put(ConstantUtils.LOGIN_USER_ID, "TestUser"); // "loginUserId"
		String fileFormat = StringUtils.isNotBlank(reportFormat) && "xlsx".equalsIgnoreCase(reportFormat) ? reportFormat
				: "pdf";

		String fileName = invoiceNo + "_ATT" + "." + fileFormat;

		if ("xlsx".equals(fileFormat)) {
			jasperResponse = jasperReportService.getJasperReportDownloadOnline(response, fileFormat, templateId,
					fileName, parameters, config);

		} else {
			jasperResponse = jasperReportService.getJasperReportDownloadOfflineV1(response, fileFormat, templateId,
					parameters, config, 0, fileName);
		}
		return jasperResponse;
	}
}
