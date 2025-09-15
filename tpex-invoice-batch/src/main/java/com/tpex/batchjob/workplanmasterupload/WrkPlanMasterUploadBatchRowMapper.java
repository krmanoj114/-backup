package com.tpex.batchjob.workplanmasterupload;

import org.springframework.batch.extensions.excel.RowMapper;
import org.springframework.batch.extensions.excel.support.rowset.RowSet;

import com.tpex.dto.WrkPlanMasterUploadBatchInputDto;

public class WrkPlanMasterUploadBatchRowMapper implements RowMapper<WrkPlanMasterUploadBatchInputDto>{


	@Override
	public WrkPlanMasterUploadBatchInputDto mapRow(RowSet rowSet) throws Exception {
		if (rowSet == null || rowSet.getCurrentRow() == null) {
			return null;
		}

		WrkPlanMasterUploadBatchInputDto wrkPlanMasterUploadBatchInputDto = new WrkPlanMasterUploadBatchInputDto();
		wrkPlanMasterUploadBatchInputDto.setDsiTiNo(rowSet.getCurrentRow()[0]);
		wrkPlanMasterUploadBatchInputDto.setIssueInvoiceDate(rowSet.getCurrentRow()[1]);
		wrkPlanMasterUploadBatchInputDto.setOriginalEtd(rowSet.getCurrentRow()[2]);
		wrkPlanMasterUploadBatchInputDto.setContDest(rowSet.getCurrentRow()[3]);
		wrkPlanMasterUploadBatchInputDto.setEtd1(rowSet.getCurrentRow()[4]);
		wrkPlanMasterUploadBatchInputDto.setEta1(rowSet.getCurrentRow()[5]);
		wrkPlanMasterUploadBatchInputDto.setCont20(Integer.parseInt(rowSet.getCurrentRow()[6]));
		wrkPlanMasterUploadBatchInputDto.setCont40(Integer.parseInt(rowSet.getCurrentRow()[7]));
		wrkPlanMasterUploadBatchInputDto.setRenbanCode(rowSet.getCurrentRow()[8]);
		wrkPlanMasterUploadBatchInputDto.setShipComp(rowSet.getCurrentRow()[9]);
		wrkPlanMasterUploadBatchInputDto.setCustomBroker(rowSet.getCurrentRow()[10]);
		wrkPlanMasterUploadBatchInputDto.setVessel1(rowSet.getCurrentRow()[11]);
		wrkPlanMasterUploadBatchInputDto.setVoyage1(rowSet.getCurrentRow()[12]);
		wrkPlanMasterUploadBatchInputDto.setEtd2(rowSet.getCurrentRow()[13]);
		wrkPlanMasterUploadBatchInputDto.setEta2(rowSet.getCurrentRow()[14]);
		wrkPlanMasterUploadBatchInputDto.setVessel2(rowSet.getCurrentRow()[15]);
		wrkPlanMasterUploadBatchInputDto.setVoyage2(rowSet.getCurrentRow()[16]);
		wrkPlanMasterUploadBatchInputDto.setEtd3(rowSet.getCurrentRow()[17]);
		wrkPlanMasterUploadBatchInputDto.setEta3(rowSet.getCurrentRow()[18]);
		wrkPlanMasterUploadBatchInputDto.setVessel3(rowSet.getCurrentRow()[19]);
		wrkPlanMasterUploadBatchInputDto.setVoyage3(rowSet.getCurrentRow()[20]);
		wrkPlanMasterUploadBatchInputDto.setBookingNo(rowSet.getCurrentRow()[21]);
		wrkPlanMasterUploadBatchInputDto.setFolderName(rowSet.getCurrentRow()[22]);
		wrkPlanMasterUploadBatchInputDto.setPortOfLoading(rowSet.getCurrentRow()[23]);
		wrkPlanMasterUploadBatchInputDto.setPortOfDischarge(rowSet.getCurrentRow()[24]);

		return wrkPlanMasterUploadBatchInputDto;
	}
}