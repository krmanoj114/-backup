package com.tpex.invoice.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tpex.dto.NoemHaisenDetailDTO;
import com.tpex.exception.InvalidInputParametersException;
import com.tpex.invoice.dto.InvoiceDetailsResponseDto;
import com.tpex.invoice.service.InvHaisenDetailsSevice;
import com.tpex.util.ConstantUtils;
import com.tpex.util.Util;



/**
 * The Class InvHaisenDetailsController.
 */

@RestController
@RequestMapping("/invoice")
@CrossOrigin
public class InvHaisenDetailsController {

	@Autowired
	InvHaisenDetailsSevice invHaisenDetailsSevice;

	/**
	 * @author akshatha.m.e
	 * @param NoemHaisenDetailDTO
	 * @return Success Message
	 * @throws ParseException 
	 * @throws JsonProcessingException 
	 * @throws Exception
	 */

	@PostMapping("/saveInvHaisenDetails")
	public ResponseEntity<List<String>> saveInvoiceHaisenDetails(@RequestParam String userId, @RequestBody List<NoemHaisenDetailDTO> inputRequest) throws ParseException, JsonProcessingException  {

		List<String> message;

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

		for(NoemHaisenDetailDTO input : inputRequest) {

			if(!Util.nullCheck(input.getEtd()) || !Util.nullCheck(input.getEta()) || !Util.nullCheck(input.getPortOfLoading()) || 
					!Util.nullCheck(input.getPortOfDischarge()) || !Util.nullCheck(input.getOceanVessel()) || !Util.nullCheck(input.getOceanVoyage())) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
			}

			java.util.Date etd = formatter.parse(input.getEtd());  
			java.util.Date eta = formatter.parse(input.getEta());     

			if (input.getEta() != null && input.getEtd() != null && etd.compareTo(eta) > 0) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1021);
			}

			if(input.getPortOfDischarge().equals(input.getPortOfLoading())){
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1019);
			}

			if(StringUtil.isBlank(input.getOceanVoyage()) || input.getOceanVoyage().contains(" ")) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1020);
			}
		}

		message = invHaisenDetailsSevice.saveHaisenDetails(userId,inputRequest);

		return new ResponseEntity<>(message,HttpStatus.OK);
	}
	
	
	@PutMapping("/updateInvoiceDetails")
	public ResponseEntity<List<String>> updateInvoiceDetails(@RequestParam String userId,@RequestBody List<InvoiceDetailsResponseDto> invoiceDetailsResponseDto) throws ParseException, JsonProcessingException {

		List<String> message;
		
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");


		for(InvoiceDetailsResponseDto input : invoiceDetailsResponseDto) {

			if(!Util.nullCheck(input.getEtaDate()) || !Util.nullCheck(input.getPortOfLoading()) || 
					!Util.nullCheck(input.getPortOfDischarge()) || !Util.nullCheck(input.getOceanVessel()) || !Util.nullCheck(input.getOceanVoyage())) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_CM_3001);
			}
			
			java.util.Date etd = formatter.parse(input.getEtdDate());  
			java.util.Date invDate = formatter.parse(input.getInvoiceDate());  
			
			if (input.getEtdDate() != null && input.getInvoiceDate() != null && invDate.compareTo(etd) > 0) {
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1018);
			}
			

			if(input.getPortOfDischarge().equals(input.getPortOfLoading())){
				throw new InvalidInputParametersException(ConstantUtils.ERR_IN_1019);
			}

		}

		message = invHaisenDetailsSevice.updateInvoiceDetails(userId,invoiceDetailsResponseDto);

		return new ResponseEntity<>(message,HttpStatus.OK);

	}

}
