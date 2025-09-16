package com.tpex.batchjob.workplanmasterupload;


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

public class WrkPlanMasterUploadBatchProcessor implements ItemProcessor<WrkPlanMasterUploadBatchInputDto, InvGenWorkPlanMstEntity>{

	@Value("#{jobParameters['issueInvoiceDate']}")
	private String issueInvoiceDate;

	@Value("#{jobParameters['userId']}")
	private String userId;

	public static final String DATE_FORMAT = ConstantUtils.DEFAULT_DATE_FORMATE;

	@Autowired
	InvGenWorkPlanMstRepository invGenWorkPlanMstRepository;


	@Override
	public InvGenWorkPlanMstEntity process(WrkPlanMasterUploadBatchInputDto item) throws Exception {
		final InvGenWorkPlanMstEntity entity = new InvGenWorkPlanMstEntity();

		if (invGenWorkPlanMstRepository.getCountUsingKey(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getOriginalEtd()), item.getContDest(),item.getRenbanCode()) == 0) {
			return null;
		}

		Optional<InvGenWorkPlanMstEntity> invEntity = invGenWorkPlanMstRepository.findById(new InvGenWorkPlanMstIdEntity(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getOriginalEtd()),item.getContDest(),item.getRenbanCode()));
		if(invEntity.isPresent() && invEntity.get().getInvGenFlag()!=null && invEntity.get().getInvGenFlag().equalsIgnoreCase(ConstantUtils.INVOICE_ALREADY_GENERATED)) {
			return null;
		}
		
		Integer cont20 = null;
		Integer cont40 = null;	
		
		if(invEntity.isPresent()) {
			 cont20 = invEntity.get().getCont20();
			 cont40 = invEntity.get().getCont40();
		}

		entity.setId(new InvGenWorkPlanMstIdEntity(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getOriginalEtd()),item.getContDest(),item.getRenbanCode()));
		entity.setBookingNo(item.getBookingNo());
		entity.setLiner(item.getShipComp());
		entity.setIssueInvoiceDate(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getIssueInvoiceDate()));
		entity.setEtd1(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEtd1()));
		entity.setEta1(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEta1()));
		entity.setCont20(cont20);
		entity.setCont40(cont40);
		entity.setVessel1(item.getVessel1());
		entity.setVoy1(item.getVoyage1());
		entity.setEtd2(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEtd2()));
		entity.setEta2(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEta2()));
		entity.setVessel2(item.getVessel2());
		entity.setVoy2(item.getVoyage2());
		entity.setEtd3(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEtd3()));
		entity.setEta3(DateUtil.dateFromStringSqlDateFormate(DATE_FORMAT,item.getEta3()));
		entity.setVessel3(item.getVessel3());
		entity.setVoy3(item.getVoyage3());
		entity.setFolderName(item.getFolderName());
		entity.setPortOfLoading(item.getPortOfLoading());
		entity.setPortOfDischarge(item.getPortOfDischarge());
		entity.setDsiTiNo(item.getDsiTiNo());
		entity.setBroker(item.getCustomBroker());

		return entity;
	}


}
