package com.tpex.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tpex.invoice.dto.SearchInvHaisenDetailRequestDto;

@Component
public class SearchInvHaisenDetailRequestDtoValidation implements Validator{

	@Override
	public boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return SearchInvHaisenDetailRequestDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		SearchInvHaisenDetailRequestDto request = (SearchInvHaisenDetailRequestDto) target;
		if(request.isHasParameter()) {
			ValidationUtils.rejectIfEmpty(errors, "parameters", "parameters.required", "Mandatory field not entered");
		}
		
	}

}
