package com.tpex.commonfiles;

import java.util.List;

import org.springframework.http.HttpStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApiResponseMessageForParDetailUpdateValidation extends ApiResponseMessage{
	
	public ApiResponseMessageForParDetailUpdateValidation(HttpStatus badRequest, String string) {
		super(badRequest, string);
	}
	
	public ApiResponseMessageForParDetailUpdateValidation(String statusCode, String message) {
		super(statusCode, message);
	}
	
	
	private List<String> partName;
	private List<String> partUsage;
	
	public ApiResponseMessageForParDetailUpdateValidation(HttpStatus statusCode,List<String> partName2, List<String> partUsage) {
		this.partName = partName2;
		this.partUsage = partUsage;
	}
	

}
