package com.tpex.batchjob.carfamilydestination;

import java.time.LocalDate;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.CarFamilyDstMasterUploadBatchDto;
import com.tpex.entity.CarFamilyDestinationMasterEntity;
import com.tpex.entity.CarFamilyDestinationMasterIdEntity;
import com.tpex.util.DateUtil;

public class CarFamilyDestMasterUploadProcessor implements ItemProcessor<CarFamilyDstMasterUploadBatchDto, CarFamilyDestinationMasterEntity> {

	@Value("#{jobParameters['companyCode']}")
	private String companyCode;
	
	@Value("#{jobParameters['userId']}")
	private String userId;
	
	@Override
	public CarFamilyDestinationMasterEntity process(CarFamilyDstMasterUploadBatchDto item) throws Exception {
		final CarFamilyDestinationMasterEntity carFamilyDestinationMasterEntity = new CarFamilyDestinationMasterEntity();
		
		carFamilyDestinationMasterEntity.setId(new CarFamilyDestinationMasterIdEntity(item.getCarFmlyCode(), item.getDestinationCode().split("-")[0], item.getReExporterCode()));
		carFamilyDestinationMasterEntity.setSrsName(item.getSrsName());
		carFamilyDestinationMasterEntity.setCompanyCode(companyCode);
		carFamilyDestinationMasterEntity.setUpdatedBy(userId);
		carFamilyDestinationMasterEntity.setUpdatedDate(DateUtil.convertToSqlDate(LocalDate.now()).toString());
		return carFamilyDestinationMasterEntity;
	}

}
