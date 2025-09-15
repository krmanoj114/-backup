package com.tpex.month.validate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.annotation.Annotation;
import java.util.stream.Stream;

import javax.validation.ConstraintValidatorContext;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import javax.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;
import javax.validation.Payload;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tpex.month.model.dto.VesselBookingMasterSearchRequest;

@ExtendWith(MockitoExtension.class)
class DateRangeFromToValidatorTest {
	
	@InjectMocks
	private DateRangeFromToValidator dateRangeFromToValidator;
	@Mock
	private ConstraintValidatorContext context;
	@Mock
	private ConstraintViolationBuilder builder;
	@Mock
	private NodeBuilderCustomizableContext nodeBuilder;
	
	// for test only
	class TestImp implements DateRangeFromTo {

		@Override
		public Class<? extends Annotation> annotationType() {
			return null;
		}

		@Override
		public String message() {
			return "From date should not be over than To date.";
		}

		@Override
		public Class<?>[] groups() {
			return null;
		}

		@Override
		public Class<? extends Payload>[] payload() {
			return null;
		}

		@Override
		public String fromDate() {
			return "etdFrom";
		}

		@Override
		public String toDate() {
			return "etdTo";
		}
		
	}
	
	private static Stream<Arguments> isValidTestParam() {
	    return Stream.of(
	      Arguments.of("09/03/2022", "08/03/2022", false),
	      Arguments.of("09/03/2022", "09/03/2022", true),
	      Arguments.of("09/03/2022", "10/03/2022", true)
	    );
	}
	
	@ParameterizedTest
	@MethodSource("isValidTestParam")
	void isValidTest(String dateFrom, String dateTo, boolean valid) {
		VesselBookingMasterSearchRequest obj = new VesselBookingMasterSearchRequest();
		obj.setEtdFrom(dateFrom);
		obj.setEtdTo(dateTo);
		
		when(context.buildConstraintViolationWithTemplate(Mockito.any())).thenReturn(builder);
		when(builder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);
		
		dateRangeFromToValidator.initialize(new TestImp());
		assertEquals(valid, dateRangeFromToValidator.isValid(obj, context));
	}
	
	private static Stream<Arguments> isValidNoInputTestParam() {
	    return Stream.of(
	      Arguments.of("", ""),
	      Arguments.of("09/03/2022", ""),
	      Arguments.of("", "10/03/2022")
	    );
	}
	
	@ParameterizedTest
	@MethodSource("isValidNoInputTestParam")
	void isValidNoInputTest(String dateFrom, String dateTo) {
		VesselBookingMasterSearchRequest obj = new VesselBookingMasterSearchRequest();
		obj.setEtdFrom(dateFrom);
		obj.setEtdTo(dateTo);
		
		when(context.buildConstraintViolationWithTemplate(Mockito.any())).thenReturn(builder);
		when(builder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);
		
		dateRangeFromToValidator.initialize(new TestImp());
		assertTrue(dateRangeFromToValidator.isValid(obj, context));
	}
	
	@Test
	void isValidExceptionTest() {
		VesselBookingMasterSearchRequest obj = new VesselBookingMasterSearchRequest();
		obj.setEtdFrom("AAAA");
		obj.setEtdTo("BBBB");
		
		when(context.buildConstraintViolationWithTemplate(Mockito.any())).thenReturn(builder);
		when(builder.addPropertyNode(Mockito.anyString())).thenReturn(nodeBuilder);
		
		dateRangeFromToValidator.initialize(new TestImp());
		assertFalse(dateRangeFromToValidator.isValid(obj, context));
	}

}
