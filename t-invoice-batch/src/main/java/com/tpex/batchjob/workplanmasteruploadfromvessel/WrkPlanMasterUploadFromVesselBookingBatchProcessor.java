package com.tpex.batchjob.workplanmasteruploadfromvessel;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;
import com.tpex.entity.InvGenWorkPlanMstEntity;
import com.tpex.entity.InvGenWorkPlanMstIdEntity;
import com.tpex.repository.InvGenWorkPlanMstRepository;
import com.tpex.util.ConstantUtils;
import com.tpex.util.DateUtil;

public class WrkPlanMasterUploadFromVesselBookingBatchProcessor implements ItemProcessor<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity>{

	@Value("#{jobParameters['userId']}")
	private String userId;

	@Autowired
	InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;

	@Override
	public InvGenWorkPlanMstEntity process(WrkPlanMasterUploadBatchInputDto item) {
		InvGenWorkPlanMstEntity entity;
		Optional<InvGenWorkPlanMstEntity> findById = invGenWorkPlanMstRepository.findById(new InvGenWorkPlanMstIdEntity(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,item.getOriginalEtd()),item.getContDest(),item.getRenbanCode()));
		if(findById.isPresent()) {
			entity = findById.get();
		} else {
			entity = new InvGenWorkPlanMstEntity();
			entity.setId(new InvGenWorkPlanMstIdEntity(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,item.getOriginalEtd()),item.getContDest(),item.getRenbanCode()));
			entity.setIssueInvoiceDate(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,item.getIssueInvoiceDate()));
			entity.setFolderName(item.getFolderName());
			entity.setEtd1(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,item.getEtd1()));
			entity.setEta1(DateUtil.dateFromStringSqlDateFormate(ConstantUtils.DEFAULT_DATE_FORMATE,item.getEta1()));
			entity.setCreateBy(userId);
			entity.setCreateDate(Date.valueOf(LocalDate.now()));
		}
		entity.setLiner(item.getShipComp());
		entity.setBookingNo(item.getBookingNo());
		entity.setCont20(item.getCont20());
		entity.setCont40(item.getCont40());
		entity.setVessel1(item.getVessel1());
		entity.setBroker(item.getCustomBroker());
		entity.setUpdateBy(userId);
		entity.setUpdateDate(Date.valueOf(LocalDate.now()));

		return entity;
	}


}
