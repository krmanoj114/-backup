package com.tpex.invoice.controller;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tpex.commonfiles.ApiResponseMessage;
import com.tpex.commonfiles.ApiResponseMessageForParDetailUpdateValidation;
import com.tpex.dto.CarFmlyMstDto;
import com.tpex.dto.DestinationAndCarFamilyDTO;
import com.tpex.dto.OemFnlDstMstDto;
import com.tpex.entity.CarFamilyMasterEntity;
import com.tpex.entity.OemFnlDstMstEntity;
import com.tpex.exception.DataNotMatchingException;
import com.tpex.exception.InvalidFileException;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.exception.MyResourceNotFoundException;
import com.tpex.invoice.dto.LotPartPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPartPriceUpdateRequestDTO;
import com.tpex.invoice.dto.LotPartWarningsDto;
import com.tpex.invoice.dto.LotPriceMasterFinalResponseDTO;
import com.tpex.invoice.dto.LotPriceMasterRequestDTO;
import com.tpex.invoice.dto.LotPriceMasterResponseDTO;
import com.tpex.invoice.dto.LotPriceUpdateRequestDTO;
import com.tpex.invoice.dto.PartPricePopupDetailsDto;
import com.tpex.invoice.dto.PartPricePopupFinalDetailsDto;
import com.tpex.invoice.service.InvGenWorkPlanMstService;
import com.tpex.invoice.service.LotPriceMasterService;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.util.ConstantProperties;
import com.tpex.util.ConstantUtils;

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class LotPriceMasterController {

	@Autowired
	InvGenWorkPlanMstService invGenWorkPlanMsteService;

	@Autowired
	LotPriceMasterService lotPriceMasterService;

	@Autowired
	LotPriceMasterRepository lotPriceMasterRepository;
	
	@Autowired
	ConstantProperties constantProperties;
	
	/*
	 * @author sravan On Load destination code and carfamily master dropdown
	 * Tpex-431
	 */
	DecimalFormat decimalFormat=new DecimalFormat(ConstantUtils.DECIMAL_FORMATE);

	@GetMapping(value = "/destinationAndCarfamily")
	public ResponseEntity<DestinationAndCarFamilyDTO> onLoadDestinationAndCarMaster() {
		DestinationAndCarFamilyDTO destinationAndCarFamilyDTO = new DestinationAndCarFamilyDTO();
		List<OemFnlDstMstEntity> destList = invGenWorkPlanMsteService.destinationCodeList();
		List<OemFnlDstMstDto> destCodeAndName = destList.stream()
				.map(u -> new OemFnlDstMstDto(u.getFdDstNm(), u.getFdDstCd())).collect(Collectors.toList());

		List<CarFamilyMasterEntity> carList = lotPriceMasterService.getCarCodeList();
		List<CarFmlyMstDto> carFmlyCodeAndName = carList.stream()
				.map(u -> new CarFmlyMstDto(u.getCarFmlyCode(), u.getCarFmlySrsName())).collect(Collectors.toList());
		destinationAndCarFamilyDTO.setDestinations(destCodeAndName);
		destinationAndCarFamilyDTO.setCarFmly(carFmlyCodeAndName);

		return new ResponseEntity<>(destinationAndCarFamilyDTO, HttpStatus.OK);
	}

	/*
	 * @author sravan Search LotPrice details based on FinalDestination,
	 * CarFamilyMaster and EffectiveMonth Tpex-431
	 */
	@PostMapping(value = "/searchLotPriceMasterDetails")
	public ResponseEntity<List<LotPriceMasterFinalResponseDTO>> searchLotPriceDetails(
			@Valid @RequestBody LotPriceMasterRequestDTO lotPriceMasterRequestDTO) throws ParseException {

		List<LotPriceMasterResponseDTO> list = lotPriceMasterService.getLotPriceDetails(lotPriceMasterRequestDTO);

		List<LotPriceMasterFinalResponseDTO> listOfLotPriceMasterResponseDTOs = new ArrayList<>();
		
		  String effectfrom=null; String effectTo=null; String effectiveFromMonth=null;
		  String effectiveToMonth=null;
		 
		for(LotPriceMasterResponseDTO listOfLotPriceMasterResponse: list) {
			LotPriceMasterFinalResponseDTO lotPriceMasterResponseDTO = new LotPriceMasterFinalResponseDTO();
			
			  effectfrom=listOfLotPriceMasterResponse.getEffectiveFromMonth();
			  effectTo=listOfLotPriceMasterResponse.getEffectiveToMonth();
			
			  lotPriceMasterResponseDTO.setLotCode(listOfLotPriceMasterResponse.getLotCode());
				lotPriceMasterResponseDTO.setLotPrice(decimalFormat.format(listOfLotPriceMasterResponse.getLotPrice()));
			  lotPriceMasterResponseDTO.setCurrency(listOfLotPriceMasterResponse.getCurrency());
			  lotPriceMasterResponseDTO.setCurreDesc(listOfLotPriceMasterResponse.getCurreDesc());
			 lotPriceMasterResponseDTO.setEffectiveFromMonth(listOfLotPriceMasterResponse.getEffectiveFromMonth());
			lotPriceMasterResponseDTO.setEffectiveToMonth(listOfLotPriceMasterResponse.getEffectiveToMonth());
			
			SimpleDateFormat inputFormat =new SimpleDateFormat("yyyyMM"); 
			SimpleDateFormat outPutFormat =new SimpleDateFormat("yyyy/MM");
			  
			  effectiveFromMonth = outPutFormat.format(inputFormat.parse(effectfrom));
			  effectiveToMonth = outPutFormat.format(inputFormat.parse(effectTo));
			  
			  lotPriceMasterResponseDTO.setEffectiveFromMonth(effectiveFromMonth);
			  lotPriceMasterResponseDTO.setEffectiveToMonth(effectiveToMonth);
			  listOfLotPriceMasterResponseDTOs.add(lotPriceMasterResponseDTO);
		}

		return new ResponseEntity<>(listOfLotPriceMasterResponseDTOs, HttpStatus.OK);
	}
	/*
	 * @Author r.1.reddy click on partPrice and popup the part price detils 
	 * Tpex-447
	*/
	@PostMapping(value = "/searchLotPartPricePopupDetails")
	public ResponseEntity<List<PartPricePopupFinalDetailsDto>> getPartPriceDetails(@Valid @RequestBody LotPartPriceMasterRequestDTO request) throws ParseException{
		
		List<PartPricePopupDetailsDto> partPriceList=lotPriceMasterService.getPartPricePopupDetails(request);
		List<PartPricePopupFinalDetailsDto> response=new ArrayList<>();
		
		for(PartPricePopupDetailsDto list1: partPriceList) {
			PartPricePopupFinalDetailsDto partPricePopupDetailsDto = new PartPricePopupFinalDetailsDto();
			partPricePopupDetailsDto.setPartName(list1.getPartName());
			
			String part1=list1.getPartNumber().substring(0,5);
			String part2=list1.getPartNumber().substring(5,10);
			String part3=list1.getPartNumber().substring(10);
			String updatedPartNumber=part1+"-"+part2+"-"+part3;
			
			partPricePopupDetailsDto.setPartNumber(updatedPartNumber);
			partPricePopupDetailsDto.setPartPrice(decimalFormat.format(list1.getPartPrice()));
			int usage=list1.getPartUsage().intValue();
			partPricePopupDetailsDto.setPartUsage(usage);
		
			response.add(partPricePopupDetailsDto);
		
	}
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/*
	 * @Author r.1.reddy Update Popup the part price detils into database 
	 * Tpex-448
	 */

	@PutMapping("/UpdateLotPartPricePopupDetails")
	public ResponseEntity<?> updateLotPriceAndPartPriceDetails(@Valid @RequestBody LotPartPriceUpdateRequestDTO request) throws Exception{

		try {
			List<LotPriceUpdateRequestDTO> dtoList=request.getData();
			for(LotPriceUpdateRequestDTO lotPriceUpdateRequestDTO: dtoList) {

				if(StringUtils.isBlank(lotPriceUpdateRequestDTO.getPartNumber()) || StringUtils.isBlank(lotPriceUpdateRequestDTO.getPartName()) || lotPriceUpdateRequestDTO.getPartPrice() == null || lotPriceUpdateRequestDTO.getPartPrice().equals("")) {
				
					return	new ResponseEntity<>(new ApiResponseMessageForParDetailUpdateValidation(HttpStatus.BAD_REQUEST, ConstantUtils.ERR_CM_3011), HttpStatus.BAD_REQUEST);
				}
			}
			LotPartWarningsDto missMatchPartNumbers = lotPriceMasterService.updateLotPartDetails(request);
			
				if(missMatchPartNumbers.getPartNameList() != null && !missMatchPartNumbers.getPartNameList().isEmpty())	{
				return new ResponseEntity<>(new ApiResponseMessageForParDetailUpdateValidation(HttpStatus.OK, missMatchPartNumbers.getPartNameList(), null), HttpStatus.OK);
				}
				if(missMatchPartNumbers.getPartUsageList() != null && !missMatchPartNumbers.getPartUsageList().isEmpty()) {
					
					return new ResponseEntity<>(new ApiResponseMessageForParDetailUpdateValidation(HttpStatus.OK, null, missMatchPartNumbers.getPartUsageList()), HttpStatus.OK);
			}
			
		} catch (InvalidInputParametersException | InvalidFileException | MyResourceNotFoundException e) {

			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.BAD_REQUEST,e.getMessage()), HttpStatus.BAD_REQUEST); 

		}catch (DataNotMatchingException e) {

			return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.INTERNAL_SERVER_ERROR, ConstantUtils.ERR_IN_1009), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new ApiResponseMessage(HttpStatus.OK, ConstantUtils.INFO_CM_3003), HttpStatus.OK);

	}

}
