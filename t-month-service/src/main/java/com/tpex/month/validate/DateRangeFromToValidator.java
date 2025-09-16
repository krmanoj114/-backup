package com.tpex.month.validate;

import java.time.LocalDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;

import com.tpex.month.util.DateUtils;

public class DateRangeFromToValidator implements ConstraintValidator<DateRangeFromTo, Object> {

	private String fromDateField;
	private String toDateField;
	
	@Override
	public void initialize(DateRangeFromTo constraintAnnotation) {
		fromDateField = constraintAnnotation.fromDate();
		toDateField = constraintAnnotation.toDate();
	}

	@Override
	public boolean isValid(Object value, ConstraintValidatorContext context) {
		// disable default
		context.disableDefaultConstraintViolation();
		// address fromDateField for error message
		context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
				.addPropertyNode(fromDateField).addConstraintViolation();

		try {
			String fromDateStr = BeanUtils.getProperty(value, fromDateField);
			String toDateStr = BeanUtils.getProperty(value, toDateField);

			if (StringUtils.isNotBlank(fromDateStr) && StringUtils.isNotBlank(toDateStr)) {
				LocalDate fromDate = DateUtils.convertStringToLocalDate(fromDateStr);
				LocalDate toDate = DateUtils.convertStringToLocalDate(toDateStr);
				return fromDate.isBefore(toDate) || fromDate.isEqual(toDate);
			} else {
				return true;
			}
		} catch (Exception ex) {
			// ignore for return false
		}
		return false;
	}

}
