package com.tpex.admin.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.tpex.admin.dto.CPCSubmitProcessRequestDTO;
import com.tpex.admin.util.ConstantUtils;

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
			ValidationUtils.rejectIfEmpty(errors, "parameters", "parameters.required", ConstantUtils.ERR_CM_3001);
		}
	}

}
