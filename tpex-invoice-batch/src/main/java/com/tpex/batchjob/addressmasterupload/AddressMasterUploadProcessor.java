package com.tpex.batchjob.addressmasterupload;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.tpex.dto.AddressMasterUploadBatchDto;
import com.tpex.entity.AddressMasterEntity;
import com.tpex.entity.AddressMasterIdEntity;

public class AddressMasterUploadProcessor implements ItemProcessor<AddressMasterUploadBatchDto, AddressMasterEntity> {

	@Value("#{jobParameters['companyCode']}")
	private String companyCode;
	
	@Value("#{jobParameters['userId']}")
	private String userId;
	
	@Override
	public AddressMasterEntity process(AddressMasterUploadBatchDto item) throws Exception {
		final AddressMasterEntity addressMasterEntity = new AddressMasterEntity();
		
		addressMasterEntity.setId(new AddressMasterIdEntity(item.getCompanyCode(), item.getBranch(), companyCode));
		addressMasterEntity.setName(item.getCompanyName());
		addressMasterEntity.setAddress1(item.getAddress1());
		addressMasterEntity.setAddress2(item.getAddress2());
		addressMasterEntity.setAddress3(item.getAddress3());
		addressMasterEntity.setAddress4(item.getAddress4());
		addressMasterEntity.setZip(item.getZipCode());
		addressMasterEntity.setCountryCode(item.getCountryCode());
		addressMasterEntity.setConsigneeCode(item.getConsignee());
		addressMasterEntity.setContactPerson(item.getContactPerson());
		addressMasterEntity.setInvoiceFlag(item.getDefaultInvoiceAddress());
		addressMasterEntity.setEmail(item.getEmail());
		addressMasterEntity.setEmployee1(item.getEmployee1());
		addressMasterEntity.setEmployee2(item.getEmployee2());
		addressMasterEntity.setEmployee3(item.getEmployee3());
		addressMasterEntity.setFaxNumber(item.getFaxNo());
		addressMasterEntity.setSapCode(item.getSapAcCode());
		addressMasterEntity.setScFlag(item.getScFlag());
		addressMasterEntity.setScRemarks(item.getScRemark());
		addressMasterEntity.setShortName(item.getShortName());
		addressMasterEntity.setTelephoneNumber(item.getTelephoneNo());
		addressMasterEntity.setTelexNumber(item.getTelexNo());
		addressMasterEntity.setUpdateBy(userId);
		addressMasterEntity.setUpdateDate(Timestamp.valueOf(LocalDateTime.now()));

		return addressMasterEntity;
	}

}
