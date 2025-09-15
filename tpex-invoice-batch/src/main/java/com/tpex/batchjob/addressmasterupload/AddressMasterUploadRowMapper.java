package com.tpex.batchjob.addressmasterupload;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.AddressMasterUploadBatchDto;

public class AddressMasterUploadRowMapper implements RowMapper<AddressMasterUploadBatchDto> {

	@Override
	public AddressMasterUploadBatchDto mapRow(RowSet rowSet) throws Exception {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}
		
		AddressMasterUploadBatchDto addressMasterUploadBatchDto = new AddressMasterUploadBatchDto();
		addressMasterUploadBatchDto.setCompanyCode(checkAndGetCell(rowSet, 0));
		addressMasterUploadBatchDto.setBranch(checkAndGetCell(rowSet, 1));
		addressMasterUploadBatchDto.setCompanyName(checkAndGetCell(rowSet, 2));
		addressMasterUploadBatchDto.setAddress1(checkAndGetCell(rowSet, 3));
		addressMasterUploadBatchDto.setAddress2(checkAndGetCell(rowSet, 4));
		addressMasterUploadBatchDto.setAddress3(checkAndGetCell(rowSet, 5));
		addressMasterUploadBatchDto.setAddress4(checkAndGetCell(rowSet, 6));
		addressMasterUploadBatchDto.setZipCode(checkAndGetCell(rowSet, 7));
		addressMasterUploadBatchDto.setShortName(checkAndGetCell(rowSet, 8));
		addressMasterUploadBatchDto.setSapAcCode(checkAndGetCell(rowSet, 9));
		addressMasterUploadBatchDto.setConsignee(checkAndGetCell(rowSet, 10));
		addressMasterUploadBatchDto.setCountryCode(checkAndGetCell(rowSet, 11));
		addressMasterUploadBatchDto.setTelephoneNo(checkAndGetCell(rowSet, 12));
		addressMasterUploadBatchDto.setFaxNo(checkAndGetCell(rowSet, 13));
		addressMasterUploadBatchDto.setEmail(checkAndGetCell(rowSet, 14));
		addressMasterUploadBatchDto.setTelexNo(checkAndGetCell(rowSet, 15));
		addressMasterUploadBatchDto.setContactPerson(checkAndGetCell(rowSet, 16));
		addressMasterUploadBatchDto.setDefaultInvoiceAddress(checkAndGetCell(rowSet, 17));
		addressMasterUploadBatchDto.setScFlag(checkAndGetCell(rowSet, 18));
		addressMasterUploadBatchDto.setScRemark(checkAndGetCell(rowSet, 19));
		addressMasterUploadBatchDto.setEmployee1(checkAndGetCell(rowSet, 20));
		addressMasterUploadBatchDto.setEmployee2(checkAndGetCell(rowSet, 21));
		addressMasterUploadBatchDto.setEmployee3(checkAndGetCell(rowSet, 22));

		return addressMasterUploadBatchDto;
	}
	
	private String checkAndGetCell(RowSet rowSet, Integer index) {
        return rowSet.getProperties().size() > index ? rowSet.getCurrentRow()[index].trim() : "";
    }
}
