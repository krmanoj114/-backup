package com.tpex.batchjob.pxpprice;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.PxpPriceMasterUploadBatchDto;

/**
 * The Class PxpPriceMasterUploadRowMapper.
 */
public class PxpPriceMasterUploadRowMapper implements RowMapper<PxpPriceMasterUploadBatchDto> {

	/**
	 * Map row.
	 *
	 * @param rowSet the row set
	 * @return the pxp price master upload batch dto
	 */
	@Override
	public PxpPriceMasterUploadBatchDto mapRow(RowSet rowSet) {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}
		
		String[] row = rowSet.getCurrentRow();
		PxpPriceMasterUploadBatchDto pxpPriceMasterUploadBatchDto = new PxpPriceMasterUploadBatchDto();
		pxpPriceMasterUploadBatchDto.setCurrencyCode(row[0].trim());
		pxpPriceMasterUploadBatchDto.setCfCode(row[1].trim());
		pxpPriceMasterUploadBatchDto.setDestCode(row[2].trim());
		pxpPriceMasterUploadBatchDto.setPartNo(row[3].trim());
		pxpPriceMasterUploadBatchDto.setPartName(row[4].trim());
		pxpPriceMasterUploadBatchDto.setPartPrice(row[5].trim());
		return pxpPriceMasterUploadBatchDto;
	}
}
