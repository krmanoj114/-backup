package com.tpex.invoice.serviceimpl;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.dto.CommonMultiSelectDropdownDto;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.InsInvPartsDetailsEntity;
import com.tpex.entity.InsInvPartsDetailsIdEntity;
import com.tpex.entity.InvModuleDetailsEntity;
import com.tpex.entity.InvModuleDetailsIdEntity;
import com.tpex.entity.PrivilegeTypeEntity;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.InvoiceNumberResponseDTO;
import com.tpex.invoice.dto.InvoiceReCalculateRequestDto;
import com.tpex.invoice.dto.InvoiceReCalculationResponseDTO;
import com.tpex.invoice.dto.InvoiceRecalculatePart;
import com.tpex.invoice.service.InvoiceRecalculateService;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.InsInvPartsDetailsRepository;
import com.tpex.repository.InvModuleDetailsRepository;
import com.tpex.repository.PrivilegeTypeRepository;
import com.tpex.util.ConstantUtils;

@Service
@Transactional
public class InvoiceRecalculateServiceImpl implements InvoiceRecalculateService {

	@Autowired
	InsInvDetailsRepository insInvDetailsRepository;

	@Autowired
	InsInvPartsDetailsRepository insInvPartsDetailsRepository;
	
	@Autowired
	InvModuleDetailsRepository invModuleDetailsRepository;
	
	@Autowired
	PrivilegeTypeRepository privilegeTypeRepository;
	
	@Override
	public InvoiceNumberResponseDTO getDetailsByInvNo(String invNo, String companyCode) throws ParseException {

		InvoiceNumberResponseDTO response = new InvoiceNumberResponseDTO();

		Optional<InsInvDtlsEntity> insInvDetailsEntity = insInvDetailsRepository.findByIndInvNoAndCompanyCode(invNo,
				companyCode);
		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
		if (insInvDetailsEntity.isPresent() && insInvDetailsEntity.get() != null) {

			response.setInvoiceNumber(insInvDetailsEntity.get().getIndInvNo());
			response.setInvoiceType(insInvDetailsEntity.get().getIndLotPttrn());

			String etdd = insInvDetailsEntity.get().getIndEtd().toString();
			String etaa = insInvDetailsEntity.get().getIndEta().toString();

			SimpleDateFormat inputDateFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATABASE_DATE_FORMATE);
			SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.DEFAULT_DATE_FORMATE);

			
			String etd = outPutFormat.format(inputDateFormat.parse(etdd));
			String eta = outPutFormat.format(inputFormat.parse(etaa));
			response.setEtaFlag(LocalDate.parse(eta, DateTimeFormatter.ofPattern(ConstantUtils.DEFAULT_DATE_FORMATE)).isAfter(LocalDate.now()) ? "Y" : "N");

			response.setEta(eta);
			response.setEtd(etd);
			response.setImporterCode(insInvDetailsEntity.get().getIndFinalDst());
			response.setBuyerCode(insInvDetailsEntity.get().getIndBuyer());
			response.setCurrencyCode(insInvDetailsEntity.get().getIndPayCrncy());
			response.setPrivilegeCode(insInvDetailsEntity.get().getIndInvTyp());
			
			PrivilegeTypeEntity privilegeData = privilegeTypeRepository.findByPrivilegeCode(insInvDetailsEntity.get().getIndInvTyp());
			
			response.setPrivilegeCode(String.join(ConstantUtils.HYPHEN, privilegeData.getPrivilegeCode(), privilegeData.getPrivilegeName()));
			
			List<InsInvPartsDetailsEntity> listOfInvPartDetails = insInvPartsDetailsRepository.findByIdInpInvNo(invNo);
			
			Set<CommonMultiSelectDropdownDto> partNumbers = new HashSet<>();
			listOfInvPartDetails.stream().forEach(c -> partNumbers.add(new CommonMultiSelectDropdownDto(c.getId().getInpPartNo(),
					c.getId().getInpPartNo().replaceAll("(.{5})(.{5})(.*)", "$1-$2-$3").concat(c.getInpPartName()))));

			List<CommonMultiSelectDropdownDto> sortedPartNos = partNumbers.stream().sorted(Comparator.comparing(CommonMultiSelectDropdownDto:: getValue)).collect(Collectors.toList());
			response.setPartNumber(sortedPartNos);
		}
		return response;

	}

	@Override
	public List<InvoiceReCalculationResponseDTO> getInvRecalculateDetails(String invoiceNumber, List<String> partNumber,
			String privilege, String companyCode) throws ParseException {

		String partNo = partNumber.stream().collect(Collectors.joining(","));
		List<Object[]> invPartDetails = insInvPartsDetailsRepository.getInvPartDetails(invoiceNumber, partNo, privilege,
				companyCode);

		List<InvoiceReCalculationResponseDTO> finalResponse = new ArrayList<>();

		if (!invPartDetails.isEmpty()) {

			for (Object[] obj : invPartDetails) {

				if (privilege.equalsIgnoreCase("PR")) {

					setPrivilegePrData(finalResponse, obj);
				}
				if (privilege.equalsIgnoreCase("PP")) {

					setPrivilegePpData(finalResponse, obj);
				}

				if (privilege.equalsIgnoreCase("PN")) {

					setPrivilegePnData(finalResponse, obj);

				}
				if (privilege.equalsIgnoreCase("PW")) {

					setPrivilegePwData(finalResponse, obj);
				}
			}
		}

		Comparator<InvoiceReCalculationResponseDTO> sorted = Comparator
				.comparing(InvoiceReCalculationResponseDTO::getCfCode)
				.thenComparing(InvoiceReCalculationResponseDTO::getCfSeries)
				.thenComparing(InvoiceReCalculationResponseDTO::getPartNo)
				.thenComparing(InvoiceReCalculationResponseDTO::getLot)
				.thenComparing(InvoiceReCalculationResponseDTO::getPakageMonth);
		finalResponse.sort(sorted);

		return finalResponse;
	}

	private void setPrivilegePrData(List<InvoiceReCalculationResponseDTO> finalResponse,
			Object[] obj) {
		InvoiceReCalculationResponseDTO response = new InvoiceReCalculationResponseDTO();

		if (obj[0] != null)
			response.setFlag(obj[0].toString());
		if (obj[1] != null)
			response.setCfCode(obj[1].toString());
		if (obj[2] != null)
			response.setCfSeries(obj[2].toString());
		if (obj[3] != null)
			response.setPartNo(obj[3].toString());
		if (obj[4] != null)
			response.setPartName(obj[4].toString());
		if (obj[5] != null)
			response.setLot(obj[5].toString());
		if (obj[6] != null)
			response.setPakageMonth(obj[6].toString());
		if (obj[7] != null)
			response.setInvAico(obj[7].toString());
		if (obj[8] != null)
			response.setIxosAico(obj[8].toString());
		if (obj[9] != null)
			response.setIncOriginCriteria(obj[9].toString());
		if (obj[10] != null)
			response.setIxosOriginCriteria(obj[10].toString());
		if (obj[11] != null)
			response.setInvHSCode(obj[11].toString());
		if (obj[12] != null)
			response.setIxosHSCode(obj[12].toString());

		finalResponse.add(response);
	}

	private void setPrivilegePpData(List<InvoiceReCalculationResponseDTO> finalResponse,
			Object[] obj) {
		InvoiceReCalculationResponseDTO response = new InvoiceReCalculationResponseDTO();

		if (obj[0] != null)
			response.setCfCode(obj[0].toString());
		if (obj[1] != null)
			response.setCfSeries(obj[1].toString());
		if (obj[2] != null)
			response.setPartName(obj[2].toString());
		if (obj[3] != null)
			response.setPartNo(obj[3].toString());
		if (obj[4] != null)
			response.setLot(obj[4].toString());
		if (obj[5] != null)
			response.setPakageMonth(obj[5].toString());
		if (obj[6] != null)
			response.setInvPartPrice(new DecimalFormat("###,###,##0.00").format(Double.parseDouble(obj[6].toString())));
		if (obj[7] != null)
			response.setPriceMaster(obj[7].toString());
		finalResponse.add(response);
	}

	private void setPrivilegePnData(List<InvoiceReCalculationResponseDTO> finalResponse,
			Object[] obj) {
		InvoiceReCalculationResponseDTO response = new InvoiceReCalculationResponseDTO();

		if (obj[0] != null)
			response.setPartNo(obj[0].toString());
		if (obj[1] != null)
			response.setPartName(obj[1].toString());
		if (obj[2] != null)
			response.setLot(obj[2].toString());
		if (obj[3] != null)
			response.setPakageMonth(obj[3].toString());
		if (obj[4] != null)
			response.setPartNameInPriceMaster(obj[4].toString());
		if (obj[5] != null)
			response.setPartNameInPartMaster(obj[5].toString());
		if (obj[6] != null)
			response.setCfCode(obj[6].toString());
		if (obj[7] != null)
			response.setCfSeries(obj[7].toString());

		finalResponse.add(response);
	}

	private void setPrivilegePwData(List<InvoiceReCalculationResponseDTO> finalResponse,
			Object[] obj) {
		InvoiceReCalculationResponseDTO response = new InvoiceReCalculationResponseDTO();
		
		if (obj[0] != null)
			response.setCfCode(obj[0].toString());
		if (obj[1] != null)
			response.setCfSeries(obj[1].toString());
		if (obj[2] != null)
			response.setPartNo(obj[2].toString());
		if (obj[3] != null)
			response.setLot(obj[3].toString());
		if (obj[4] != null)
			response.setPakageMonth(obj[4].toString());
		if (obj[5] != null)
			response.setBoxSize(obj[5].toString());
		if (obj[6] != null)
			response.setInvPartNetWeight(obj[6].toString());
		if (obj[7] != null)
			response.setRevPartNetWeight(obj[7].toString());
		if (obj[8] != null)
			response.setRevBoxtNetWeight(obj[8].toString());
		if (obj[9] != null)
			response.setInvBoxtNetWeight(obj[9].toString());
		if (obj[10] != null)
			response.setPartName(obj[10].toString());

		finalResponse.add(response);
	}

	@Override
	@Transactional
	public void recalculateInvoice(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO) {
		switch (invoiceReCalculationRequestDTO.getPrivilege()) {
			case "PR":
				recalculateInvoiceForPrivilege(invoiceReCalculationRequestDTO);
				break;
			case "PP":
				recalculateInvoiceForPartPrice(invoiceReCalculationRequestDTO);
				break;
			case "PN":
				recalculateInvoiceForPartName(invoiceReCalculationRequestDTO);
				break;
			case "PW":
				recalculateInvoiceForPartAndBoxWeight(invoiceReCalculationRequestDTO);
				break;
			default:
				//do nothing
				break;
		}
	}
	
	private void recalculateInvoiceForPrivilege(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO) {
		String result = insInvDetailsRepository.reCalculateInvoicePrivilage(invoiceReCalculationRequestDTO.getInvoiceNumber(), invoiceReCalculationRequestDTO.getCompanyCode(), invoiceReCalculationRequestDTO.getUserId());
		if (!result.isBlank()) {
			Map<String, Object> errorMessageParams = new HashMap<>();
			errorMessageParams.put(ConstantUtils.PART_NO, result);
			throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1122, errorMessageParams);
		}
	}

	private void recalculateInvoiceForPartPrice(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO) {
		List<Map<String, Object>> invPartList = insInvDetailsRepository.getInvPartsForPartPriceUpdate(
				invoiceReCalculationRequestDTO.getInvoiceNumber(), invoiceReCalculationRequestDTO.getCompanyCode());
		List<String> selectedPartList = invoiceReCalculationRequestDTO.getPartDetails().stream().map(m -> m.getPartNo().replace("-", "")).collect(Collectors.toList());
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		Double invAmount = Double.valueOf(0);
		for (Map<String, Object> invPart : invPartList) {
			if (invPart.get(ConstantUtils.PART_PRICE) != null && selectedPartList.contains(invPart.get(ConstantUtils.PARTNO).toString())) {
				//Update part price in Inv Part Details table
				Optional<InsInvPartsDetailsEntity> insInvPartsDetailsEntityOptional = insInvPartsDetailsRepository.findById(
						new InsInvPartsDetailsIdEntity(invPart.get(ConstantUtils.INV_NO).toString(), invPart.get(ConstantUtils.MOD_NO).toString(), 
								invPart.get(ConstantUtils.LOT_NO).toString(), invPart.get(ConstantUtils.PARTNO).toString(), invPart.get(ConstantUtils.BOX_NO).toString()));
				if (insInvPartsDetailsEntityOptional.isPresent()) {
					InsInvPartsDetailsEntity insInvPartsDetailsEntity = insInvPartsDetailsEntityOptional.get();
					insInvPartsDetailsEntity.setInpPrc(Double.valueOf(invPart.get(ConstantUtils.PART_PRICE).toString()));
					insInvPartsDetailsEntity.setInpUpdBy(invoiceReCalculationRequestDTO.getUserId());
					insInvPartsDetailsEntity.setInpUpdDate(Date.valueOf(LocalDate.now()));
					insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
					
					invAmount = Double.sum(invAmount, insInvPartsDetailsEntity.getInpUnitPerBox() * insInvPartsDetailsEntity.getInpPrc());
				}
			}
		}
		insInvPartsDetailsRepository.saveAll(insInvPartsDetailsEntityList);
		
		//Update Invoice price and PLS Flag
		Optional<InsInvDtlsEntity> insInvDtlsEntityOptional = insInvDetailsRepository.findById(invoiceReCalculationRequestDTO.getInvoiceNumber());
		if (insInvDtlsEntityOptional.isPresent()) {
			InsInvDtlsEntity insInvDtlsEntity = insInvDtlsEntityOptional.get();
			insInvDtlsEntity.setIndInvAmt(invAmount.toString());
			insInvDtlsEntity.setIndTnsoFlg("N");
			insInvDetailsRepository.save(insInvDtlsEntity);
		}
	}
	
	private void recalculateInvoiceForPartName(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO) {
		List<Map<String, Object>> invPartList = insInvDetailsRepository.getInvPartsForPartNameUpdate(
				invoiceReCalculationRequestDTO.getInvoiceNumber(), invoiceReCalculationRequestDTO.getCompanyCode());
		List<String> selectedPartList = invoiceReCalculationRequestDTO.getPartDetails().stream().map(m -> m.getPartNo().replace("-", "")).collect(Collectors.toList());
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		for (Map<String, Object> invPart : invPartList) {
			if (invPart.get(ConstantUtils.PARTNAME) != null && selectedPartList.contains(invPart.get(ConstantUtils.PARTNO).toString())) {
				//Update part price in Inv Part Details table
				Optional<InsInvPartsDetailsEntity> insInvPartsDetailsEntityOptional = insInvPartsDetailsRepository.findById(
						new InsInvPartsDetailsIdEntity(invPart.get(ConstantUtils.INV_NO).toString(), invPart.get(ConstantUtils.MOD_NO).toString(), 
						invPart.get(ConstantUtils.LOT_NO).toString(), invPart.get(ConstantUtils.PARTNO).toString(), invPart.get(ConstantUtils.BOX_NO).toString()));
				if (insInvPartsDetailsEntityOptional.isPresent()) {
					InsInvPartsDetailsEntity insInvPartsDetailsEntity = insInvPartsDetailsEntityOptional.get();
					insInvPartsDetailsEntity.setInpPartName(invPart.get(ConstantUtils.PARTNAME).toString());
					insInvPartsDetailsEntity.setInpUpdBy(invoiceReCalculationRequestDTO.getUserId());
					insInvPartsDetailsEntity.setInpUpdDate(Date.valueOf(LocalDate.now()));
					insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
				}
			}
		}
		insInvPartsDetailsRepository.saveAll(insInvPartsDetailsEntityList);
		
		Optional<InsInvDtlsEntity> insInvDtlsEntityOptional = insInvDetailsRepository.findById(invoiceReCalculationRequestDTO.getInvoiceNumber());
		if (insInvDtlsEntityOptional.isPresent()) {
			InsInvDtlsEntity insInvDtlsEntity = insInvDtlsEntityOptional.get();
			insInvDtlsEntity.setIndTnsoFlg("N");
			insInvDetailsRepository.save(insInvDtlsEntity);
		}
		
	}
	
	private void recalculateInvoiceForPartAndBoxWeight(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO) {
		List<InvoiceRecalculatePart> invoiceRecalculatePartList = invoiceReCalculationRequestDTO.getPartDetails();
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		for (InvoiceRecalculatePart invoiceRecalculatePart : invoiceRecalculatePartList) {
			if ((invoiceRecalculatePart.getRevBoxNetWeight() == null || invoiceRecalculatePart.getRevBoxNetWeight().isBlank() 
					|| (Double.valueOf(invoiceRecalculatePart.getRevBoxNetWeight()).compareTo(Double.valueOf(0)) == 0))
					&& (invoiceRecalculatePart.getRevPartNetWeight() == null || invoiceRecalculatePart.getRevPartNetWeight().isBlank() 
					|| (Double.valueOf(invoiceRecalculatePart.getRevPartNetWeight()).compareTo(Double.valueOf(0)) == 0))) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1120);
			}
			
			Double boxWeight = (invoiceRecalculatePart.getRevBoxNetWeight() == null || invoiceRecalculatePart.getRevBoxNetWeight().isBlank()) ? Double.valueOf(invoiceRecalculatePart.getInvBoxNetWeight()) : Double.valueOf(invoiceRecalculatePart.getRevBoxNetWeight());
			Double partNetWeight = (invoiceRecalculatePart.getRevPartNetWeight() == null || invoiceRecalculatePart.getRevPartNetWeight().isBlank()) ? Double.valueOf(invoiceRecalculatePart.getInvPartNetWeight()) : Double.valueOf(invoiceRecalculatePart.getRevPartNetWeight());
			if (Double.compare(boxWeight, partNetWeight * Double.valueOf(invoiceRecalculatePart.getBoxSize())) >= 0) {
				throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1121);
			}
			insInvPartsDetailsEntityList.addAll(updateInvoicePartsWeight(invoiceReCalculationRequestDTO, invoiceRecalculatePart));
		}

		insInvPartsDetailsRepository.saveAll(insInvPartsDetailsEntityList);
		
		Double totalGrossWt = updateInvoiceModuleWeight(invoiceReCalculationRequestDTO.getInvoiceNumber());
		updateInvoiceDetailsWeight(invoiceReCalculationRequestDTO.getInvoiceNumber(), totalGrossWt);
		
	}

	private List<InsInvPartsDetailsEntity> updateInvoicePartsWeight(InvoiceReCalculateRequestDto invoiceReCalculationRequestDTO, InvoiceRecalculatePart invoiceRecalculatePart) {
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntities = insInvPartsDetailsRepository.findByIdInpInvNoAndIdInpPartNo(
				invoiceReCalculationRequestDTO.getInvoiceNumber(), invoiceRecalculatePart.getPartNo().replace("-", ""));
		List<InsInvPartsDetailsEntity> insInvPartsDetailsEntityList = new ArrayList<>();
		if (!insInvPartsDetailsEntities.isEmpty()) {
			for (InsInvPartsDetailsEntity insInvPartsDetailsEntity : insInvPartsDetailsEntities) {
				if (invoiceRecalculatePart.getRevPartNetWeight() != null && !invoiceRecalculatePart.getRevPartNetWeight().isBlank()) {
					Double boxWeight = (Double.valueOf(invoiceRecalculatePart.getRevPartNetWeight()) * insInvPartsDetailsEntity.getInpUnitPerBox())
							+ (insInvPartsDetailsEntity.getInpNetWt() * insInvPartsDetailsEntity.getInpUnitPerBox());
					
					insInvPartsDetailsEntity.setInpNetWt(Double.valueOf(invoiceRecalculatePart.getRevPartNetWeight()));
					insInvPartsDetailsEntity.setInpBoxGrossWt(boxWeight);
					insInvPartsDetailsEntity.setInpUpdBy(invoiceReCalculationRequestDTO.getUserId());
					insInvPartsDetailsEntity.setInpUpdDate(Date.valueOf(LocalDate.now()));
					insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
					
				} else if (invoiceRecalculatePart.getRevBoxNetWeight() != null && !invoiceRecalculatePart.getRevBoxNetWeight().isBlank()) {
					insInvPartsDetailsEntity.setInpBoxGrossWt(Double.valueOf(invoiceRecalculatePart.getRevBoxNetWeight()));
					insInvPartsDetailsEntity.setInpUpdBy(invoiceReCalculationRequestDTO.getUserId());
					insInvPartsDetailsEntity.setInpUpdDate(Date.valueOf(LocalDate.now()));
					insInvPartsDetailsEntityList.add(insInvPartsDetailsEntity);
				}
				
			}
		}
		return insInvPartsDetailsEntityList;
	}

	private Double updateInvoiceModuleWeight(String invoiceNumber) {
		List<Map<String, Object>> invModuleList = invModuleDetailsRepository.getInvModuleForGrossWtUpdate(invoiceNumber);
		List<InvModuleDetailsEntity> invModuleDetailsEntityList = new ArrayList<>();
		Double totalGrossWt = Double.valueOf(0);
		for (Map<String, Object> invModule : invModuleList) {
			Optional<InvModuleDetailsEntity> invModuleDetailsEntityOptional = invModuleDetailsRepository.findById(
					new InvModuleDetailsIdEntity(invModule.get(ConstantUtils.INV_NO).toString(), invModule.get(ConstantUtils.MOD_NO).toString(), 
							invModule.get(ConstantUtils.LOT_NO).toString()));
			if (invModuleDetailsEntityOptional.isPresent()) {
				InvModuleDetailsEntity invModuleDetailsEntity = invModuleDetailsEntityOptional.get();
				invModuleDetailsEntity.setNetWt(Double.valueOf(invModule.get("GROSS_WT").toString()));
				Double modGrossWt = Double.valueOf(invModule.get("GROSS_WT").toString()) + invModuleDetailsEntity.getGrossWt();
				invModuleDetailsEntity.setGrossWt(modGrossWt);
				invModuleDetailsEntityList.add(invModuleDetailsEntity);
				totalGrossWt = totalGrossWt + modGrossWt;
			}
		}
		
		invModuleDetailsRepository.saveAll(invModuleDetailsEntityList);
		
		return totalGrossWt;
	}
	
	private void updateInvoiceDetailsWeight(String invoiceNumber, Double totalGrossWt) {
		Optional<InsInvDtlsEntity> insInvDtlsEntityOptional = insInvDetailsRepository.findById(invoiceNumber);
		if (insInvDtlsEntityOptional.isPresent()) {
			InsInvDtlsEntity insInvDtlsEntity = insInvDtlsEntityOptional.get();
			if ("P".equalsIgnoreCase(insInvDtlsEntity.getIndInvCat())) {
				insInvDtlsEntity.setIndNetWt(insInvPartsDetailsRepository.getInvPartsSumForPxP(invoiceNumber));
			} else if ("L".equalsIgnoreCase(insInvDtlsEntity.getIndInvCat())) {
				insInvDtlsEntity.setIndNetWt(insInvPartsDetailsRepository.getInvPartsSumForLot(invoiceNumber));
			}
			insInvDtlsEntity.setIndGrossWt(totalGrossWt.toString());
			insInvDtlsEntity.setIndTnsoFlg("N");
			insInvDetailsRepository.save(insInvDtlsEntity);
		}
		
	}

	

}
