package com.tpex.batchjob.lotpartpriceupload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.BeanUtils;

import com.tpex.dto.LotPartPriceUploadBatchDto;
import com.tpex.entity.OemLotPartPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntity;

import lombok.Data;

@Data
public class LotPartPriceUploadBatchWriter implements ItemWriter<LotPartPriceUploadBatchDto> {

	private RepositoryItemWriter<OemLotPartPrcMstEntity> lotPartPrcMstEntityWriter;
    private RepositoryItemWriter<OemLotPrcMstEntity> lotPrcMstEntityWriter;

    public LotPartPriceUploadBatchWriter(RepositoryItemWriter<OemLotPartPrcMstEntity> lotPartPrcMstEntityWriter, RepositoryItemWriter<OemLotPrcMstEntity> lotPrcMstEntityWriter) {
        this.lotPartPrcMstEntityWriter = lotPartPrcMstEntityWriter;
        this.lotPrcMstEntityWriter = lotPrcMstEntityWriter;
    }
    
	@Override
	public void write(List<? extends LotPartPriceUploadBatchDto> items) throws Exception {
		
		List<OemLotPartPrcMstEntity> oemLotPartPrcMstEntityList = new ArrayList<>();
		List<OemLotPrcMstEntity> oemLotPrcMstEntityList = new ArrayList<>();
		
		for (LotPartPriceUploadBatchDto item : items) {
			OemLotPartPrcMstEntity oemLotPartPrcMstEntity = new OemLotPartPrcMstEntity();
			BeanUtils.copyProperties(item, oemLotPartPrcMstEntity);
			oemLotPartPrcMstEntityList.add(oemLotPartPrcMstEntity);
			
			OemLotPrcMstEntity oemLotPrcMstEntity = new OemLotPrcMstEntity();
			BeanUtils.copyProperties(item, oemLotPrcMstEntity);
			oemLotPrcMstEntityList.add(oemLotPrcMstEntity);
        }
		
		lotPartPrcMstEntityWriter.write(oemLotPartPrcMstEntityList);
		lotPrcMstEntityWriter.write(oemLotPrcMstEntityList);
		
	}

}
