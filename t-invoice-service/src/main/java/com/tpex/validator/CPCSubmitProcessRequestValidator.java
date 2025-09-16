package com.tpex.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tpex.dto.CPCSubmitProcessRequestDTO;

@Component
public class CPCSubmitProcessRequestValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return CPCSubmitProcessRequestDTO.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		CPCSubmitProcessRequestDTO request = (CPCSubmitProcessRequestDTO) target;
		if(request.isHasParameter()) {
			ValidationUtils.rejectIfEmpty(errors, "parameters", "parameters.required", "please select mandatory parameter");
		}
	}

}
