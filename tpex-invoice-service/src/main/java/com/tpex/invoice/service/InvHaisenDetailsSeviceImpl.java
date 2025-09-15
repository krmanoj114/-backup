package com.tpex.invoice.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.ContainerDetailsResponseDTO;
import com.tpex.dto.NoemHaisenDetailDTO;
import com.tpex.dto.NoemHaisenDtlsEntityDTO;
import com.tpex.entity.InsInvDtlsEntity;
import com.tpex.entity.NoemHaisenDtlsEntity;
import com.tpex.entity.NoemHaisenDtlsIdEntity;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.InvoiceDetailsResponseDto;
import com.tpex.repository.InsInvContainerDtlsRepository;
import com.tpex.repository.InsInvDetailsRepository;
import com.tpex.repository.NoemHaisenDtlsRepository;
import com.tpex.repository.NoemRenbanBookDtlRepository;
import com.tpex.repository.NoemRenbanBookMstRepository;
import com.tpex.repository.OemParameterRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

@Service
@Transactional
public class InvHaisenDetailsSeviceImpl implements InvHaisenDetailsSevice{

	@Autowired
	InvSearchService invSearchService;

	@Autowired
	NoemHaisenDtlsRepository noemHaisenDtlsRepository;

	@Autowired
	InvGenWorkPlanMstService invGenWorkPlanMsteService;

	@Autowired
	InsInvDetailsRepository insInvDetailsRepository;

	@Autowired
	InsInvContainerDtlsRepository insInvContainerDtlsRepository;

	@Autowired
	NoemRenbanBookDtlRepository noemRenbanBookDtlRepository;

	@Autowired
	NoemRenbanBookMstRepository noemRenbanBookMstRepository;

	@Autowired
	OemParameterRepository oemParameterRepository;

	public static final String defaultDateFormate ="dd/MM/yyyy";

	Date currentDate = Date.valueOf(LocalDate.now()); 

	Timestamp currentTime = new Timestamp(System.currentTimeMillis());



	/*
	 * @author akshatha.m.e Update method for Haisen details by
	 * @input NoemHaisenDetailDTO
	 */

	@SuppressWarnings({ "null", "unlikely-arg-type" })
	@Override
	public List<String> saveHaisenDetails(String userId,List<NoemHaisenDetailDTO> inputDto)
			throws Exception {

		boolean isNewHaisenGenerated = false;
		List<String> newHaisenNogenerated = new LinkedList<>();
		List<String> existingHaisenNoList = new LinkedList<>();
		List<String> response = new LinkedList<>() ;
		String commaSeparatedInvNo = null;
		Date currentDate = Date.valueOf(LocalDate.now());

		for(NoemHaisenDetailDTO dto :inputDto) {

			String haisenYear = dto.getHaisenYearMonth();

			NoemHaisenDtlsEntity entity = new NoemHaisenDtlsEntity();
			NoemHaisenDtlsIdEntity idEntity = new NoemHaisenDtlsIdEntity();

			idEntity.setEtdDate(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEtd()));
			idEntity.setBuyer(dto.getBuyer());
			idEntity.setDepPort(dto.getPortOfLoading());
			idEntity.setDstPort(dto.getPortOfDischarge());
			idEntity.setVesselOcean(dto.getOceanVessel());
			idEntity.setVoyNo(dto.getOceanVoyage());

			Optional<NoemHaisenDtlsEntity> haisenNoandYrMnth= noemHaisenDtlsRepository.findById(idEntity);
			String newHaisenNo = null;
			String newHaisenYrMnth = null;
			boolean isSaved = false;


			haisenYear = haisenYear.replace("/", "");
			dto.setHaisenYearMonth(haisenYear);

			if(haisenNoandYrMnth.isPresent()){

				NoemHaisenDtlsEntity haisenentity = haisenNoandYrMnth.get();

				String inputFeederVessel = dto.getFeederVessel().get(0);
				String inputFeederVoyage = dto.getFeederVoyage().get(0);

				Date inputEta = DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEta());
				LocalDate inputEtaLd = DateUtil.convertSqlDateToLocalDateOfEntityAttribute(inputEta);

				newHaisenNo = haisenentity.getHaisenNo();
				newHaisenYrMnth = haisenentity.getHaisenYearMonth();

				boolean flag = false;

				if( (dto.getHaisenNo().equals(newHaisenNo) &&
						dto.getHaisenYearMonth().equals(newHaisenYrMnth)) ) {

					List<InsInvDtlsEntity> insInvDtlsEntity = insInvDetailsRepository.findByIndHaisenNoAndIndHaisenYearMonth(dto.getHaisenNo(), dto.getHaisenYearMonth());

					//Updating feeder details if any change in Haisen and Invoice tables
					for(InsInvDtlsEntity invoice : insInvDtlsEntity) {

						if((!inputFeederVessel.equals(invoice.getIndVesselNameFeeder())) || (!inputFeederVoyage.equals(invoice.getIndVoyageNoFeeder()))){

							invoice.setIndVesselNameFeeder(inputFeederVessel);
							invoice.setIndVoyageNoFeeder(inputFeederVoyage);

							insInvDetailsRepository.save(invoice);

							isSaved = true;
						}
					}

					if((!inputFeederVessel.equals(haisenentity.getVesselFeeder())) || (!inputFeederVoyage.equals(haisenentity.getVoyageFeeder())) || (inputEtaLd.compareTo(haisenentity.getEtaDate())!=0)){

						haisenentity.setVesselFeeder(inputFeederVessel);
						haisenentity.setVoyageFeeder(inputFeederVoyage);
						haisenentity.setEtaDate(inputEta);
						noemHaisenDtlsRepository.save(haisenentity);

						isSaved = true;
					}


					if(isSaved){

						response.add(ConstantUtils.INFO_CM_3007);
						flag=false;

					}else {
						flag = true;
					}


				}

				//flag is set to true if there was no changes in modifying fields 
				if(flag) {
					throw new InvalidInputParametersException(ConstantUtils.INFO_CM_3008);
				}

				if(!isSaved) {
					existingHaisenNoList.add(newHaisenNo);

					List<String> invNoLinkedToHaiseList = insInvDetailsRepository.getInvNo(dto.getHaisenNo(),dto.getHaisenYearMonth());

					commaSeparatedInvNo = invNoLinkedToHaiseList.stream().collect(Collectors.joining("],[", "[", "]"));

					String message  ="Invoice No." + commaSeparatedInvNo +" combine to Haisen No."+ "["+newHaisenNo+"]";
					response.add(message);

					//Update invoice table with the new Haisen No and Month
					updateInvDetails(userId,dto,newHaisenNo,newHaisenYrMnth);

					//Select the container details(SUMs)
					ContainerDetailsResponseDTO containerDetailsResponseDTO = getContainerDetails(newHaisenNo,newHaisenYrMnth);

					Integer noOf20FtCont = Integer.valueOf(containerDetailsResponseDTO.getNoOf20ftContainer());
					Integer noOf40FtCont = Integer.valueOf(containerDetailsResponseDTO.getNoOf40ftContainer());
					BigDecimal containerEffeciency = new BigDecimal(containerDetailsResponseDTO.getContainerEfficiency());
					Integer lclVol = containerDetailsResponseDTO.getLclVolume();
					Date eta = DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEta());

					// Update the Haisen Dtls table with the above selected information
					updateHaisenDetails(newHaisenNo,newHaisenYrMnth,noOf20FtCont, noOf40FtCont,containerEffeciency, lclVol,
							userId, currentDate, eta);

					//If old and new haisen no. / yr are same do nothing
					if( !(dto.getHaisenNo().equals(newHaisenNo) &&
							dto.getHaisenYearMonth().equals(newHaisenYrMnth)) ) {

						//Update the old Haisen No and Year Month details to Zero.
						updateHaisenDetails(dto.getHaisenNo(),dto.getHaisenYearMonth(),0, 0,new BigDecimal(0), 0,
								userId, currentDate,DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEta()) );

						//Update the TNSO flag
						updateInvoiceTNSO(dto.getHaisenNo(),dto.getHaisenYearMonth(),newHaisenNo,newHaisenYrMnth,userId);

					}
				}
			} else {
				
				NoemHaisenDtlsEntityDTO noemHaisenDtlsEntityDTO = new NoemHaisenDtlsEntityDTO();
				noemHaisenDtlsEntityDTO.setBuyer(idEntity.getBuyer());
				noemHaisenDtlsEntityDTO.setContainerEffeciency(new BigDecimal(dto.getContainerEfficiency()));
				noemHaisenDtlsEntityDTO.setDepPort(idEntity.getDepPort());
				noemHaisenDtlsEntityDTO.setDstPort(idEntity.getDstPort());
				noemHaisenDtlsEntityDTO.setEtaDate(dto.getEta());
				noemHaisenDtlsEntityDTO.setEtdDate(dto.getEtd());
				noemHaisenDtlsEntityDTO.setHaisenNo(dto.getHaisenNo());
				noemHaisenDtlsEntityDTO.setHaisenYearMonth(dto.getHaisenYearMonth());
				noemHaisenDtlsEntityDTO.setLclVol(null);
				noemHaisenDtlsEntityDTO.setNoOf20FtContainer(dto.getNoOf20ftContainer());
				noemHaisenDtlsEntityDTO.setNoOf40FtContainer(dto.getNoOf40ftContainer());
				noemHaisenDtlsEntityDTO.setShipCoNM(dto.getShipCompName());
				noemHaisenDtlsEntityDTO.setUpdatedBy(userId);
				noemHaisenDtlsEntityDTO.setUpdatedDate(currentDate);
				noemHaisenDtlsEntityDTO.setVesselFeeder(dto.getFeederVessel().get(0));
				noemHaisenDtlsEntityDTO.setVesselOcean(idEntity.getVesselOcean());
				noemHaisenDtlsEntityDTO.setVoyNo(idEntity.getVoyNo());

				Map<String, Object> haisenNoResponse = invGenWorkPlanMsteService.generateHaisenNo(noemHaisenDtlsEntityDTO);

				newHaisenNo = (String)haisenNoResponse.get("haisenNo");
				newHaisenYrMnth = (String)haisenNoResponse.get("haisenYrMth");

				newHaisenNogenerated.add(newHaisenNo);

				//Update invoice table with the new Haisen No and Month
				updateInvDetails(userId,dto,newHaisenNo,newHaisenYrMnth);

				//Select the container details(SUMs)
				ContainerDetailsResponseDTO containerDetailsResponseDTO = getContainerDetails(newHaisenNo,newHaisenYrMnth);

				//Insert the new Haisen Data into the Haisen Details table.
				entity.setId(idEntity);
				entity.setHaisenNo(newHaisenNo);
				entity.setHaisenYearMonth(newHaisenYrMnth);
				entity.setEtaDate(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEta()));
				entity.setVesselFeeder(dto.getFeederVessel().get(0));
				entity.setVoyageFeeder(dto.getFeederVoyage().get(0));
				entity.setShipCoNM(dto.getShipCompName());
				entity.setNoOf20FtContainer(Integer.valueOf(containerDetailsResponseDTO.getNoOf20ftContainer()));
				entity.setNoOf40FtContainer(Integer.valueOf(containerDetailsResponseDTO.getNoOf40ftContainer()));
				entity.setContainerEffeciency(new BigDecimal(containerDetailsResponseDTO.getContainerEfficiency()));
				entity.setLclVol(containerDetailsResponseDTO.getLclVolume());
				entity.setUpdatedBy(userId);
				entity.setUpdatedDate(currentDate);

				noemHaisenDtlsRepository.save(entity);

				//Update the old Haisen No and Year Month details to Zero.
				updateHaisenDetails(dto.getHaisenNo(),dto.getHaisenYearMonth(),0, 0,new BigDecimal(0), 0,
						userId, currentDate, DateUtil.dateFromStringSqlDateFormate(defaultDateFormate,dto.getEta()));

				//Update the TNSO Flag
				updateInvoiceTNSO(dto.getHaisenNo(),dto.getHaisenYearMonth(),newHaisenNo,newHaisenYrMnth,userId);

				isNewHaisenGenerated = true;
			}


		}

		if(isNewHaisenGenerated) {

			String commaSeparatedHaisenNo = newHaisenNogenerated.stream().collect(Collectors.joining("],[", "[", "]"));

			String message = "New Haisen no "+commaSeparatedHaisenNo+" was generated";
			response.add(message);

		}

		return response;
	}

	//update Haisen Details
	private void updateHaisenDetails(String haisenNo, String haisenYearMonth, Integer noOf20FtCont,
			Integer noOf40FtCont, BigDecimal containerEffeciency, Integer lclVol, String userId, Date currentDate,
			Date eta) {

		noemHaisenDtlsRepository.updateHaisenDtls(haisenNo,haisenYearMonth,noOf20FtCont,noOf40FtCont,containerEffeciency,lclVol,userId,currentDate,eta);
	}

	//get container information 
	public ContainerDetailsResponseDTO getContainerDetails(String newHaisenNo, String newHaisenYrMnth) throws JsonProcessingException {

		Object response = insInvContainerDtlsRepository.getContainerDetails(newHaisenNo,newHaisenYrMnth);

		ContainerDetailsResponseDTO containerDetails = new ContainerDetailsResponseDTO();

		ObjectMapper mapper1 = new ObjectMapper();
		String newJsonData1 = mapper1.writeValueAsString(response).replace("\"", "");
		String[] fieldArray = newJsonData1.split(",");

		if (fieldArray[0].replace("[", "") != null && !"null".equals(fieldArray[0].replace("[", ""))) {
			containerDetails.setNoOf20ftContainer(fieldArray[0].replace("[", ""));
		}
		if (fieldArray[1] != null && !"null".equals(fieldArray[1])) {
			containerDetails.setNoOf40ftContainer(fieldArray[1]);
		}
		DecimalFormat df = new DecimalFormat("0.00");
		if (fieldArray[2].replace("]", "") != null && !"null".equals(fieldArray[2].replace("]", ""))) {
			containerDetails.setContainerEfficiency(df.format(Double.valueOf(fieldArray[2].replace("]", ""))));
		} else if (fieldArray[2].equalsIgnoreCase("null")) {
			containerDetails.setContainerEfficiency("0.00");
		}

		Integer lclVolume = insInvContainerDtlsRepository.getlclVolume(newHaisenNo,newHaisenYrMnth);
		containerDetails.setLclVolume(lclVolume);

		return containerDetails;
	}

	//Update invoice table with the new Haisen No and Month
	public void updateInvDetails(String userId,NoemHaisenDetailDTO dto,String newHaisenNo,String newHaisenYrMnth) throws Exception {

		List<InsInvDtlsEntity> insInvDtlsEntity = insInvDetailsRepository.findByIndHaisenNoAndIndHaisenYearMonth(dto.getHaisenNo(), dto.getHaisenYearMonth());

		LocalDateTime time = convertStringToTimestamp(dto.getEtd()).toLocalDateTime();

		for(InsInvDtlsEntity inv:insInvDtlsEntity) {
			if(inv != null) {
				if(time!= null && StringUtils.isNoneBlank(dto.getEtd()) &&  inv.getIndInvDt()!= null && (inv.getIndInvDt().compareTo(time) > 0)){
					throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1018);
				}

				inv.setIndHaisenNo(newHaisenNo);
				inv.setIndHaisenYearMonth(newHaisenYrMnth);
				inv.setIndEtd(convertStringToTimestamp(dto.getEtd()));
				inv.setIndEta(convertStringToTimestamp(dto.getEta()));
				inv.setIndDepPortCd(dto.getPortOfDischarge());
				inv.setIndDestPortCd(dto.getPortOfLoading());
				inv.setIndVesselNameFeeder(dto.getFeederVessel().get(0));
				inv.setIndVoyageNoFeeder(dto.getFeederVoyage().get(0));
				inv.setIndvesselnameocean(dto.getOceanVessel());
				inv.setIndvoyagenoocean(dto.getOceanVoyage());
				inv.setIndUpdBy(userId);
				inv.setIndUpdDt(currentTime);

				insInvDetailsRepository.save(inv);
			}
		}

	}	

	//Update the TNSO flag to 'N'
	public void updateInvoiceTNSO(String oldHaisenNo,String oldHaisenyrMnth,String newHaisenNo,String newHaisenYrMnth,String userId) {

		List<InsInvDtlsEntity> entity = insInvDetailsRepository.getInvTnso(oldHaisenNo,oldHaisenyrMnth,newHaisenNo,newHaisenYrMnth);

		for(InsInvDtlsEntity inv:entity) {
			inv.setIndTnsoFlg("N");
			inv.setIndUpdBy(userId);
			inv.setIndUpdDt(currentTime);

			insInvDetailsRepository.save(inv);
		}

	}

	public Timestamp convertStringToTimestamp(String str_date) throws ParseException { 
		java.sql.Timestamp timeStampDate = null;
		DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy"); 
		if(str_date != null && StringUtils.isNotBlank(str_date)) {
			java.util.Date date =  formatter.parse(str_date); 
			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
			timeStampDate = new Timestamp(sqlDate.getTime()); 
		}
		return timeStampDate; 

	} 

	@Override
	public List<String> updateInvoiceDetails(String userId,List<InvoiceDetailsResponseDto> invoiceDetailsResponseDto) throws Exception {
		List<String> response = new LinkedList<>();

		for (InvoiceDetailsResponseDto dto : invoiceDetailsResponseDto) {

			NoemHaisenDtlsEntity entity = new NoemHaisenDtlsEntity();
			NoemHaisenDtlsIdEntity idEntity = new NoemHaisenDtlsIdEntity();


			idEntity.setEtdDate(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, dto.getEtdDate()));
			idEntity.setBuyer(dto.getBuyer());
			idEntity.setDepPort(dto.getPortOfLoading());
			idEntity.setDstPort(dto.getPortOfDischarge());
			idEntity.setVesselOcean(dto.getOceanVessel());
			idEntity.setVoyNo(dto.getOceanVoyage());

			String invNo = dto.getInvoiceNo();

			Optional<NoemHaisenDtlsEntity> haisenNoandYrMnth = noemHaisenDtlsRepository.findById(idEntity);
			String newHaisenNo = null;
			String newHaisenYrMnth = null;

			if (haisenNoandYrMnth.isPresent()) {

				NoemHaisenDtlsEntity haisenentity = haisenNoandYrMnth.get();

				newHaisenNo = haisenentity.getHaisenNo();
				newHaisenYrMnth = haisenentity.getHaisenYearMonth();

				Object HaisenResponse = insInvDetailsRepository.getHaisenNo(invNo);

				ObjectMapper mapper = new ObjectMapper();
				DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				mapper.setDateFormat(df);

				String newJsonData1 = mapper.writeValueAsString(HaisenResponse).replace("\"", "");

				String[] fieldArray = newJsonData1.split(",");


				SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");


				String oldHaisenNo = null;
				String oldHaisenYrMnth = null;
				Date existingEta = null;
				String existingPol = null;
				String existingPoD = null;
				String existingOcceanVes = null;
				String existingOcceanVoy = null;

				if (fieldArray[0].replace("[", "") != null && !"null".equals(fieldArray[0].replace("[", ""))) {
					oldHaisenNo = fieldArray[0].replace("[", "");
				}
				if (fieldArray[1] != null && !"null".equals(fieldArray[1])) {
					oldHaisenYrMnth = fieldArray[1];
				}
				if (fieldArray[2] != null && !"null".equals(fieldArray[2])) {
					String formatDate = outputFormat.format(inputFormat.parse(fieldArray[2]));
					existingEta = DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, formatDate);
				}
				if (fieldArray[3] != null && !"null".equals(fieldArray[3])) {
					existingPoD = fieldArray[3];
				}
				if (fieldArray[4] != null && !"null".equals(fieldArray[4])) {
					existingPol = fieldArray[4];
				}
				if (fieldArray[5] != null && !"null".equals(fieldArray[5])) {
					existingOcceanVes = fieldArray[5];
				}
				if (fieldArray[6].replace("]", "") != null && !"null".equals(fieldArray[6].replace("]", ""))) {
					existingOcceanVoy = fieldArray[6].replace("]", "");
				}

				Date eta = DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, dto.getEtaDate());
				String portOfLoading = dto.getPortOfLoading();
				String portOfDischarge = dto.getPortOfDischarge();
				String oceanVessel = dto.getOceanVessel();
				String oceanVoyage = dto.getOceanVoyage();


				if (existingEta != null && eta != null && (existingEta.compareTo(eta) == 0) && existingPol != null && existingPol.equals(portOfLoading) && existingPoD != null && existingPoD.equals(portOfDischarge)
						&& existingOcceanVes != null && existingOcceanVes.equals(oceanVessel) && existingOcceanVoy != null && existingOcceanVoy.equals(oceanVoyage)) {
					throw new InvalidInputParametersException(ConstantUtils.INFO_CM_3007);
				}


				updateInvoiceTNSO(oldHaisenNo, oldHaisenYrMnth, newHaisenNo, newHaisenYrMnth, userId);

				//ETA,Ocean Vessel,Ocean Voyage,port of loading,port of discharge


				//Update invoice table with the new Haisen No and Month
				updateInvoiceTable(invNo, newHaisenNo, newHaisenYrMnth, eta, oceanVessel, oceanVoyage, portOfLoading, portOfDischarge, userId);

				// Update the Haisen Dtls table with the above selected information
				updateHaisenforInvoiceChange(newHaisenNo, newHaisenYrMnth, eta, portOfLoading, portOfDischarge, oceanVessel, oceanVoyage, userId);


				//String commaSeparatedInvNo = invoiceDetailsResponseDto.stream().collect(Collectors.joining("],[", "[", "]"));

				String message = "Invoice No." + "[" + invNo + "]" + " combine to Haisen No." + "[" + newHaisenNo + "]";
				response.add(message);
			} else {

				NoemHaisenDtlsEntityDTO noemHaisenDtlsEntityDTO = new NoemHaisenDtlsEntityDTO();
				noemHaisenDtlsEntityDTO.setBuyer(idEntity.getBuyer());
				noemHaisenDtlsEntityDTO.setContainerEffeciency(BigDecimal.valueOf(0.0));
				noemHaisenDtlsEntityDTO.setDepPort(idEntity.getDepPort());
				noemHaisenDtlsEntityDTO.setDstPort(idEntity.getDstPort());
				noemHaisenDtlsEntityDTO.setEtaDate(dto.getEtaDate());
				noemHaisenDtlsEntityDTO.setEtdDate(dto.getEtdDate());
				noemHaisenDtlsEntityDTO.setHaisenNo(dto.getHaisenNo());
				noemHaisenDtlsEntityDTO.setHaisenYearMonth(newHaisenYrMnth);
				noemHaisenDtlsEntityDTO.setLclVol(0);
				noemHaisenDtlsEntityDTO.setNoOf20FtContainer(0);
				noemHaisenDtlsEntityDTO.setNoOf40FtContainer(0);
				noemHaisenDtlsEntityDTO.setShipCoNM(dto.getShipCompName());
				noemHaisenDtlsEntityDTO.setUpdatedBy(userId);
				noemHaisenDtlsEntityDTO.setUpdatedDate(currentDate);
				noemHaisenDtlsEntityDTO.setVesselFeeder(dto.getFeederVessel());
				noemHaisenDtlsEntityDTO.setVesselOcean(dto.getOceanVessel());
				noemHaisenDtlsEntityDTO.setVoyNo(idEntity.getVoyNo());
				
				Map<String, Object> haisenNoResponse = invGenWorkPlanMsteService.generateHaisenNo(noemHaisenDtlsEntityDTO);

				newHaisenNo = (String) haisenNoResponse.get("haisenNo");
				newHaisenYrMnth = (String) haisenNoResponse.get("haisenYrMth");

				//newHaisenNogenerated.add(newHaisenNo);

				Date eta = DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, dto.getEtaDate());
				String portOfLoading = dto.getPortOfLoading();
				String portOfDischarge = dto.getPortOfDischarge();
				String oceanVessel = dto.getOceanVessel();
				String oceanVoyage = dto.getOceanVoyage();

				//Update invoice table with the new Haisen No and Month
				updateInvoiceTable(invNo, newHaisenNo, newHaisenYrMnth, eta, oceanVessel, oceanVoyage, portOfLoading, portOfDischarge, userId);


				//Insert the new Haisen Data into the Haisen Details table.
				entity.setId(idEntity);
				entity.setHaisenNo(newHaisenNo);
				entity.setHaisenYearMonth(newHaisenYrMnth);
				entity.setEtaDate(DateUtil.dateFromStringSqlDateFormate(defaultDateFormate, dto.getEtaDate()));
				entity.setVesselFeeder(dto.getFeederVessel());
				entity.setShipCoNM(dto.getShipCompName());
				entity.setNoOf20FtContainer(0);
				entity.setNoOf40FtContainer(0);
				entity.setContainerEffeciency(BigDecimal.valueOf(0.0));
				entity.setLclVol(0);
				entity.setUpdatedBy(userId);
				entity.setUpdatedDate(currentDate);

				noemHaisenDtlsRepository.save(entity);

				//String commaSeparatedHaisenNo = newHaisenNogenerated.stream().collect(Collectors.joining("],[", "[", "]"));

				String message = "New Haisen no " + "[" + newHaisenNo + "]" + " was generated";
				response.add(message);
			}
		}
		return response;
	}

	private void updateHaisenforInvoiceChange(String newHaisenNo, String newHaisenYrMnth, Date eta, String portOfLoading,
			String portOfDischarge, String oceanVessel, String oceanVoyage, String userId) {
		Date updDt = currentDate;
		noemHaisenDtlsRepository.updateHaisenforInvoiceChange(newHaisenNo,newHaisenYrMnth,eta,portOfLoading,portOfDischarge,oceanVessel,oceanVoyage,userId,updDt);

	}

	private void updateInvoiceTable(String invNo, String haisenNo,String haisenYrMnth,Date eta,String oceanVessel,String oceanVoyage,String portOfLoading, String portOfDischarge, String userId) {

		Date  updt = currentDate;

		insInvDetailsRepository.updateInvoiceTable(invNo, haisenNo,haisenYrMnth,eta, oceanVessel, oceanVoyage, portOfLoading, portOfDischarge, userId, updt);		
	} 

}
