package com.tpex.invoice.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Tuple;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemLotPartPrcMstEntity;
import com.tpex.entity.OemLotSizeMstEntity;
import com.tpex.exception.DataNotMatchingException;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.LotPartPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPartPriceUpdateRequestDTO;
import com.tpex.invoice.dto.LotPartWarningsDto;
import com.tpex.invoice.dto.LotPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPriceMasterResponseDTO;
import com.tpex.invoice.dto.LotPriceUpdateRequestDTO;
import com.tpex.invoice.dto.PartPricePopupDetailsDto;
import com.tpex.invoice.service.LotPriceMasterService;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.LotPartPriceMasterRepository;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.repository.LotSizeMasterRepository;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.OemCurrencyMstRepository;
import com.tpex.util.ConstantProperties;
import com.tpex.util.ConstantUtils;

@Service
public class LotPriceMasterServiceImpl implements LotPriceMasterService {

	@Autowired
	LotPriceMasterRepository lotPriceMasterRepository;

	@Autowired
	LotPartPriceMasterRepository lotPartPriceMasterRepository;

	@Autowired
	CarFamilyMastRepository carFamilyMastRepository;


	@Autowired
	LotSizeMasterRepository lotSizeMasterRepository;

	@Autowired
	NoemPackSpecRepository noemPackSpecRepository;

	@Autowired
	OemCurrencyMstRepository oemCurrencyMstRepository;

	@Autowired
	ConstantProperties constantProperties;

	@Override
	public List<CarFamilyMasterEntity> getCarCodeList() {

		return carFamilyMastRepository.findAllByOrderByCarFmlyCodeAsc();
	}
	/*
	 * @author sravan getting LotPrice details by destination code and carfamily
	 * master dropdowns Tpex-431
	 */

	@Override
	public List<LotPriceMasterResponseDTO> getLotPriceDetails(@Valid LotPriceMasterRequestDTO lotPriceMasterRequestDTO)
			throws ParseException {
		List<LotPriceMasterResponseDTO> lotPriceMasterResponseDTO = new ArrayList<>();

		String carFamilyObj = lotPriceMasterRequestDTO.getCarFamily();
		String[] carFamilySplit = carFamilyObj.split("-");
		String carFamily = carFamilySplit[0];
		String finalDestinationObj = lotPriceMasterRequestDTO.getFinalDestination();
		String[] destSplit = finalDestinationObj.split("-");
		String finalDestination = destSplit[0];
		String eFM = lotPriceMasterRequestDTO.getEffectiveFromMonth();

		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_INPUT);
		SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_OUTPUT);

		String effectiveFromMonth = outPutFormat.format(inputFormat.parse(eFM));
		try {

			if (StringUtils.isNotBlank(carFamily) && StringUtils.isNotBlank(finalDestination) && StringUtils.isNotBlank(effectiveFromMonth)) {
				List<Tuple> oemLotPrcMstEntity = lotPriceMasterRepository.findLotPriceDetails(carFamily,
						finalDestination, effectiveFromMonth);
				lotPriceMasterResponseDTO = oemLotPrcMstEntity.stream()
						.map(u -> new LotPriceMasterResponseDTO(u.get(0, String.class), u.get(1, String.class), u.get(2, String.class), u.get(3, BigDecimal.class), u.get(4, String.class), u.get(5, String.class)))
						.collect(Collectors.toList());
			}
		} catch (Exception e) {
			throw new MyResourceNotFoundException(ConstantUtils.INFO_CM_3001);
		}

		return lotPriceMasterResponseDTO;
	}
	/*
	 * @author Sravan
	 * Tepex-447
	 */
	@Override
	public List<PartPricePopupDetailsDto> getPartPricePopupDetails(@Valid LotPartPriceMasterRequestDTO request) throws ParseException {

		List<PartPricePopupDetailsDto> partPricePopupDetailsDto= new ArrayList<>();
		String eFM = request.getEffectiveFromMonth();
		String eTM = request.getEffectiveToMonth();

		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_INPUT);
		SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_OUTPUT);

		String effectiveFromMonth = outPutFormat.format(inputFormat.parse(eFM));
		String effectiveToMonth = outPutFormat.format(inputFormat.parse(eTM));

		String carFamilyObj = request.getCarFamily();
		String[] carFamilySplit = carFamilyObj.split("-");
		String carFamily = carFamilySplit[0];

		String finalDestinationObj = request.getFinalDestination();
		String[] destSplit = finalDestinationObj.split("-");
		String finalDestination = destSplit[0];
		String lotCode=request.getLotCode();
		String currencyObj = request.getCurrency();
		String[] currencysplit = currencyObj.split("-");
		String currency = currencysplit[0];

		if (effectiveFromMonth !=null && !effectiveFromMonth.equals("") && effectiveToMonth !=null && !effectiveToMonth.equals("") && carFamily != null && !carFamily.equals("") && finalDestination != null && !finalDestination.equals("") && lotCode != null && !lotCode.equals("") && currency != null && !currency.equals("") ) {
			List<OemLotPartPrcMstEntity> listofPartPriceDetails = lotPartPriceMasterRepository.findLotPartPriceDetails(effectiveFromMonth, effectiveToMonth, carFamily, finalDestination, lotCode, currency);

			partPricePopupDetailsDto = listofPartPriceDetails.stream().map(u -> new PartPricePopupDetailsDto(u.getPartPriceNo(), u.getPartPriceName(), u.getPartPricePrc(), u.getPartPriceUsage())).collect(Collectors.toList());

		}
		return partPricePopupDetailsDto;
	}

	/*
	 * @author Sravan
	 * Tepex-448 Updating the PartPricePopup Details
	 */

	@Override
	@Transactional(rollbackFor = Throwable.class)
	public LotPartWarningsDto updateLotPartDetails(@Valid LotPartPriceUpdateRequestDTO request) throws Exception {

		Double lotPrice;
		String eFM = request.getEffectiveFromMonth();
		String eTM = request.getEffectiveToMonth();

		SimpleDateFormat inputFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_INPUT);
		SimpleDateFormat outPutFormat = new SimpleDateFormat(ConstantUtils.YEAR_MONTH_OUTPUT);

		String effectiveFromMonth = outPutFormat.format(inputFormat.parse(eFM));

		String effectiveToMonth = outPutFormat.format(inputFormat.parse(eTM));

		String carFamilyObj = request.getCarFamily();
		String[] carFamilySplit = carFamilyObj.split("-");
		String carFamily = carFamilySplit[0];

		String finalDestinationObj = request.getFinalDestination();
		String[] destSplit = finalDestinationObj.split("-");
		String finalDestination = destSplit[0];
		String lotCode = request.getLotCode();
		String currencyObj = request.getCurrency();
		String[] currencysplit = currencyObj.split("-");
		String currency = currencysplit[0];

		LotPartWarningsDto lotPartWarnings=new LotPartWarningsDto();
		List<String> partNamePartNos=new ArrayList<>();
		List<String> partUsagePartNos=new ArrayList<>();

		for(LotPriceUpdateRequestDTO lotPriceUpdateRequestDTO :request.getData()) {

			String updateBy=lotPriceUpdateRequestDTO.getUpdateBy();
			LocalDate updateDate=LocalDate.now();
			String partNoWith = lotPriceUpdateRequestDTO.getPartNumber();
			String partNo = partNoWith.replace("-", "");
			String partName= lotPriceUpdateRequestDTO.getPartName();
			String trimmedPartName = partName.trim();

			//Changing the PartPrice value String to Double
			String partPriceAsString = lotPriceUpdateRequestDTO.getPartPrice().replace(",", "");
			double partPrice=Double.parseDouble(partPriceAsString);

			Double partusage = lotPriceUpdateRequestDTO.getPartUsage();

			OemLotPartPrcMstEntity oemLotPartPrcMstEntity = lotPartPriceMasterRepository.findNameByReq(carFamily, finalDestination, lotCode, currency,
					effectiveFromMonth, effectiveToMonth, partNo);

			if (!oemLotPartPrcMstEntity.getPartPriceName().equals(trimmedPartName)) {

				if (trimmedPartName.contains("~")) {

					throw new MyResourceNotFoundException(ConstantUtils.ERR_IN_1010);
				}

				if(!request.isPartNameConfirmation()) {

					String part1=partNo.substring(0,5);
					String part2=partNo.substring(5,10);
					String part3=partNo.substring(10);
					String updatedPartNo=part1+"-"+part2+"-"+part3;

					partNamePartNos.add(updatedPartNo);
					lotPartWarnings.setPartNameList(partNamePartNos);
				}
				else if(request.isPartNameConfirmation() || request.isPartusageConfirmation()) {

					int count =	lotPartPriceMasterRepository.updatePartPriceName(trimmedPartName, effectiveFromMonth, effectiveToMonth,
							finalDestination, carFamily, currency, lotCode, partNo, updateBy, updateDate);

					if(count == 0)
						throw new DataNotMatchingException(ConstantUtils.ERR_IN_1009);

				}
			}


			if (!oemLotPartPrcMstEntity.getPartPriceUsage().equals(partusage)) {

				if(partusage<=0) {

					throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1012);
				} else {
					
						Double calValUsage;

						List<Integer> listOfQtyBox = noemPackSpecRepository.findSpecDetails(carFamily,
								finalDestination, lotCode, partNo, effectiveFromMonth, effectiveToMonth);

						Integer sumOfQty = listOfQtyBox.stream().reduce(0, (a, b) -> a + b);
						
						List<OemLotSizeMstEntity> findLotSizeWithPartNo = lotSizeMasterRepository.findByCarFamilyCodeAndLotModImpAndLotCodeAndPartNumber(carFamily, finalDestination,
								lotCode, partNo);
						
						double sumOfLotSizeWithPartNo = findLotSizeWithPartNo.stream().filter(e->e !=null && e.getLotSizeCode() != null).mapToDouble(OemLotSizeMstEntity::getLotSizeCode).sum();
						
						if (findLotSizeWithPartNo.isEmpty() || findLotSizeWithPartNo == null) {
							List<OemLotSizeMstEntity> findLotSizeWithoutPartNo = lotSizeMasterRepository.findByCarFamilyCodeAndLotModImpAndLotCode(carFamily, finalDestination, lotCode);

							double sumOfLotSizeWithoutPartNo = findLotSizeWithoutPartNo.stream().filter(e->e !=null && e.getLotSizeCode() != null).mapToDouble(OemLotSizeMstEntity::getLotSizeCode).sum();
							
							calValUsage = sumOfQty / sumOfLotSizeWithoutPartNo;
						
							if(findLotSizeWithoutPartNo.isEmpty() || findLotSizeWithoutPartNo == null ) {

								throw new DataNotMatchingException(ConstantUtils.ERR_IN_1009);
							}
					} else {

						calValUsage = sumOfQty / sumOfLotSizeWithPartNo;
					}

					if (Double.compare(calValUsage, partusage) != 0) {
						if(!request.isPartusageConfirmation()) {

							String part1=partNo.substring(0,5);
							String part2=partNo.substring(5,10);
							String part3=partNo.substring(10);
							String updatedPartNo=part1+"-"+part2+"-"+part3;

							partUsagePartNos.add(updatedPartNo);
							lotPartWarnings.setPartUsageList(partUsagePartNos);
						}
						else if(request.isPartNameConfirmation() || request.isPartusageConfirmation()) {
							try {
								lotPartPriceMasterRepository.updatePartPriceUsage(partusage, partPrice,
										effectiveFromMonth, effectiveToMonth, finalDestination, carFamily, currency,
										lotCode, partNo, updateBy, updateDate);

								List<OemLotPartPrcMstEntity> partPriceMasterEntityList = lotPartPriceMasterRepository.findLotPartPriceDetails(effectiveFromMonth, effectiveToMonth, carFamily, finalDestination, lotCode, currency);

								lotPrice = partPriceMasterEntityList.stream().mapToDouble(a -> a.getPartPricePrc() * a.getPartPriceUsage()).sum();
								lotPrice = roundDouble(lotPrice, 2);

								lotPriceMasterRepository.updateLotPrice(carFamily, finalDestination, lotCode, currency,
										effectiveFromMonth, effectiveToMonth, lotPrice, updateBy, updateDate);
							}
							catch (Exception e) {

								throw new DataNotMatchingException(ConstantUtils.ERR_IN_1009);
							}
						}	
					}
				}
			}
			if (!oemLotPartPrcMstEntity.getPartPricePrc().equals(partPrice) && lotPartWarnings == null) {

				if(partPrice <= 0) {

					throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1011);
				} else {
					try {
						lotPartPriceMasterRepository.updatePartPriceInPartMst(partPrice, effectiveFromMonth, effectiveToMonth,
								finalDestination, carFamily, currency, lotCode, partNo, updateBy, updateDate);

						List<OemLotPartPrcMstEntity> partPriceMasterEntityList = lotPartPriceMasterRepository.findLotPartPriceDetails(effectiveFromMonth, effectiveToMonth, carFamily, finalDestination, lotCode, currency);

						lotPrice = partPriceMasterEntityList.stream().mapToDouble(a -> a.getPartPricePrc() * a.getPartPriceUsage()).sum();
						lotPrice = roundDouble(lotPrice, 2);

						lotPriceMasterRepository.updateLotPrice(carFamily, finalDestination, lotCode, currency,
								effectiveFromMonth, effectiveToMonth, lotPrice, updateBy, updateDate);
					}
					catch (Exception e) {

						throw new DataNotMatchingException(ConstantUtils.ERR_IN_1009);
					}
				}
			}
		}

		return lotPartWarnings;
	}
	private static double roundDouble(double d, int places) {
		BigDecimal bigDecimal = new BigDecimal(Double.toString(d));
		bigDecimal = bigDecimal.setScale(places, RoundingMode.HALF_UP);
		return bigDecimal.doubleValue();
	}

}
