package com.tpex.month.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.tpex.month.model.dto.CommonResponse;
import com.tpex.month.model.dto.VesselBookingMasterSaveRequest;
import com.tpex.month.model.entity.RenbanBookDetailEntity;
import com.tpex.month.model.entity.RenbanBookMasterEntity;
import com.tpex.month.model.entity.RenbanBookMasterHistoryEntity;
import com.tpex.month.model.entity.RenbanBookMasterId;
import com.tpex.month.model.repository.RenbanBookDetailRespository;
import com.tpex.month.model.repository.RenbanBookMasterHistoryRepository;
import com.tpex.month.model.repository.RenbanBookMasterRespository;
import com.tpex.month.util.ConstantUtil;
import com.tpex.month.util.DateUtils;
import com.tpex.month.validate.VesselBookingMasterSaveValidator;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VesselBookingMasterSaveService {

	private final VesselBookingMasterSaveValidator vesselBookingMasterSaveValidator;
	private final RenbanBookMasterRespository renbanBookMasterRespository;
	private final RenbanBookDetailRespository renbanBookDetailRespository;
	private final RenbanBookMasterHistoryRepository renbanBookMasterHistoryRepository;

	public CommonResponse saveVesselBookingMaster(List<VesselBookingMasterSaveRequest> request) {
		int cnt = 0;
		for (VesselBookingMasterSaveRequest mst : request) {
			boolean change = vesselBookingMasterSaveValidator.isChange(mst);
			if (change || mst.isUpdate()) {
				LocalDate etd1 = DateUtils.convertStringToLocalDate(mst.getEtd1());

				String user = "USER";

				RenbanBookMasterId objId = new RenbanBookMasterId();
				objId.setDestinationCode(mst.getDestinationCode());
				objId.setEtd1(etd1);
				objId.setGroupId(mst.getGroupId());
				objId.setShippingCompany(mst.getShippingCompany());
				objId.setVanningMonth(mst.getVanningMonth());

				RenbanBookMasterEntity ent = null;
				Optional<RenbanBookMasterEntity> opt = renbanBookMasterRespository.findById(objId);
				boolean exists = opt.isPresent();
				if (exists) {
					ent = opt.get();
					// delete all detail under groupId
					renbanBookDetailRespository.deleteAllUnderGroupId(ent.getVanningMonth(), ent.getDestinationCode(),
							ent.getEtd1(), ent.getShippingCompany(), ent.getGroupId());
				} else {
					ent = prepareToInsertMaster(mst, etd1);
				}
				// insert/update master
				saveMaster(mst, ent, user);
				// insert history if any change (customBroker or vessel or bookingNo) and exists
				// already in DB
				insertHistory(mst, etd1, user, change && exists);

				String renbanCode = mst.getRenbanCode();
				insertDetails(mst, etd1, user, renbanCode);
				cnt++;
			}
		}
		CommonResponse commonResponse = new CommonResponse();
		commonResponse.setStatus("Success");
		if (cnt == 0) {
			commonResponse.setMessage(ConstantUtil.INFO_CM_3008);
		} else {
			commonResponse.setMessage(ConstantUtil.INFO_CM_3003);
		}
		return commonResponse;
	}

	private void insertHistory(VesselBookingMasterSaveRequest mst, LocalDate etd1, String user, boolean insert) {
		if (insert) {
			RenbanBookMasterHistoryEntity hist = new RenbanBookMasterHistoryEntity();
			hist.setDestinationCode(mst.getDestinationCode());
			hist.setEtd1(etd1);
			hist.setGroupId(mst.getGroupId());
			hist.setShippingCompany(mst.getShippingCompany());
			hist.setVanningMonth(mst.getVanningMonth());
			hist.setEta(DateUtils.convertStringToLocalDate(mst.getFinalEta()));
			hist.setVanEndDate(DateUtils.convertStringToLocalDateTime(mst.getVanEndDate()));
			hist.setCbFlag(ConstantUtil.NO);
			hist.setCont20(Integer.parseInt(mst.getNoOfContainer20ft()));
			hist.setCont40(Integer.parseInt(mst.getNoOfContainer40ft()));
			hist.setCustomBrokerCode(mst.getCustomBrokerCode());
			hist.setVessel1(mst.getVessel1());
			hist.setBookingNo(mst.getBookingNo());
			hist.setUpdateBy(user);
			hist.setUpdateDate(LocalDateTime.now());
			hist.setPrevCustomBrokerCode(mst.getOldCustomBrokerCode());
			hist.setPrevVessel1(mst.getOldVessel1());
			hist.setPrevBookingNo(mst.getOldBookingNo());
			hist.setRenbanCode(mst.getRenbanCode());

			renbanBookMasterHistoryRepository.save(hist);
		}
	}

	private void insertDetails(VesselBookingMasterSaveRequest mst, LocalDate etd1, String user, String renbanCode) {
		if (StringUtils.isNotBlank(renbanCode)) {
			renbanCode = renbanCode.replaceAll("\\s", ConstantUtil.BLANK);
			String[] contGrpCode = renbanCode.split(",");
			for (int i = 0; i < contGrpCode.length; i++) {
				if (StringUtils.isNotBlank(contGrpCode[i])) {
					RenbanBookDetailEntity entDtl = new RenbanBookDetailEntity();
					entDtl.setVanningMonth(mst.getVanningMonth());
					entDtl.setDestinationCode(mst.getDestinationCode());
					entDtl.setEtd1(etd1);
					entDtl.setShippingCompany(mst.getShippingCompany());
					entDtl.setGroupId(mst.getGroupId());
					entDtl.setContainerGroupCode(contGrpCode[i]);
					entDtl.setUpdateBy(user);
					entDtl.setUpdateDate(LocalDateTime.now());

					renbanBookDetailRespository.save(entDtl);
				}
			}
		}
	}

	private RenbanBookMasterEntity saveMaster(VesselBookingMasterSaveRequest mst, RenbanBookMasterEntity ent,
			String user) {
		ent.setCbFlag(ConstantUtil.NO);
		ent.setCont20(Integer.parseInt(mst.getNoOfContainer20ft()));
		ent.setCont40(Integer.parseInt(mst.getNoOfContainer40ft()));
		ent.setCustomBrokerCode(mst.getCustomBrokerCode());
		ent.setVessel1(mst.getVessel1());
		ent.setBookingNo(mst.getBookingNo());
		ent.setUpdateBy(user);
		ent.setUpdateDate(LocalDateTime.now());

		return renbanBookMasterRespository.save(ent);
	}

	private RenbanBookMasterEntity prepareToInsertMaster(VesselBookingMasterSaveRequest mst, LocalDate etd1) {
		RenbanBookMasterEntity ent = new RenbanBookMasterEntity();
		ent.setDestinationCode(mst.getDestinationCode());
		ent.setEtd1(etd1);
		ent.setGroupId(mst.getGroupId());
		ent.setShippingCompany(mst.getShippingCompany());
		ent.setVanningMonth(mst.getVanningMonth());
		ent.setEta(DateUtils.convertStringToLocalDate(mst.getFinalEta()));
		ent.setVanEndDate(DateUtils.convertStringToLocalDateTime(mst.getVanEndDate()));
		return ent;
	}
}
