package com.tpex.month.validate;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.tpex.month.exception.InvalidInputParametersException;
import com.tpex.month.model.dto.MixedVesselBooking;
import com.tpex.month.model.dto.ValidList;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.repository.CustomRenbanBookMasterRepository;
import com.tpex.month.util.ConstantUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class VesselBookingMasterSaveValidator implements Validator {
	
	private static final String NESTED = "list[%d]";
	private static final String BOOK_NO = "bookingNo";

	private final CustomRenbanBookMasterRepository customRenbanBookMasterRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return ValidList.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		@SuppressWarnings("unchecked")
		ValidList<VesselBookingMasterSaveRequest> inputList = (ValidList<VesselBookingMasterSaveRequest>) target;

		checkDuplicateBookNo(errors, inputList);
	}

	public boolean isChange(VesselBookingMasterSaveRequest in) {
		return !replaceNullWithEmpty(in.getCustomBrokerCode()).equals(replaceNullWithEmpty(in.getOldCustomBrokerCode()))
				|| !replaceNullWithEmpty(in.getVessel1()).equals(replaceNullWithEmpty(in.getOldVessel1()))
				|| !replaceNullWithEmpty(in.getBookingNo()).equals(replaceNullWithEmpty(in.getOldBookingNo()));
	}

	private void checkDuplicateBookNo(Errors errors, ValidList<VesselBookingMasterSaveRequest> inputList) {
		// prepare input VanBook
		List<MixedVesselBooking> inputVanBook = inputList.stream().map(u -> {
			MixedVesselBooking m = new MixedVesselBooking();
			m.setVanningMonth(u.getVanningMonth());
			m.setBookingNo(u.getBookingNo());
			return m;
		}).collect(Collectors.toList());
		
		List<MixedVesselBooking> duplicateList = customRenbanBookMasterRepository.findDuplicateBookNo(inputVanBook);
		
		for (int i = 0; i < inputList.size(); i++) {
			VesselBookingMasterSaveRequest in = inputList.get(i);
			if (StringUtils.isBlank(in.getBookingNo()) || isNotChangeBookNo(in)) {
				// ignore bookingNo blank and not change
				continue;
			}
			// check duplicate input itself
			for (int j = 0; j < inputVanBook.size(); j++) {
				MixedVesselBooking inReq = inputVanBook.get(j);
				if (i != j && isSameBookNo(in, inReq)) {
					errors.setNestedPath(String.format(NESTED, i));
					errors.rejectValue(BOOK_NO, ConstantUtil.BLANK, ConstantUtil.ERR_MN_4002);
					throw new InvalidInputParametersException(ConstantUtil.ERR_MN_4002);
				}
			}
			// check duplicate database
			for (int j = 0; j < duplicateList.size(); j++) {
				MixedVesselBooking inDB = duplicateList.get(j);
				if (isSameBookNo(in, inDB)) {
					errors.setNestedPath(String.format(NESTED, i));
					errors.rejectValue(BOOK_NO, ConstantUtil.BLANK, ConstantUtil.ERR_MN_4002);
					throw new InvalidInputParametersException(ConstantUtil.ERR_MN_4002);
				}
			}
		}
	}
	
	private boolean isSameBookNo(VesselBookingMasterSaveRequest in, MixedVesselBooking chk) {
		return in.getVanningMonth().equals(chk.getVanningMonth())
				&& in.getBookingNo().equals(chk.getBookingNo());
	}
	
	private boolean isNotChangeBookNo(VesselBookingMasterSaveRequest in) {
		return in.getBookingNo().equals(in.getOldBookingNo());
	}
	
	private String replaceNullWithEmpty(String str) {
		return str == null ? ConstantUtil.BLANK : str ;
	}
}
