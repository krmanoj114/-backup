package com.tpex.batchjob.pxpprice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.time.DateUtils;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import com.tpex.entity.PartPriceMasterEntity;
import com.tpex.entity.PartPriceMasterIdEntity;
import com.tpex.repository.NoemPackSpecRepository;
import com.tpex.repository.PartPriceMasterRepository;

/**
 * The Class PxpPriceMasterUploadWriter.
 */
public class PxpPriceMasterUploadWriter implements ItemWriter<PartPriceMasterEntity> {

	/** The noem pack spec repository. */
	@Autowired
	private NoemPackSpecRepository noemPackSpecRepository;
	
	/** The part price master repository. */
	@Autowired
	private PartPriceMasterRepository partPriceMasterRepository;
	
	/** The part price item writer. */
	private RepositoryItemWriter<PartPriceMasterEntity> partPriceItemWriter;

    /**
     * Instantiates a new pxp price master upload writer.
     *
     * @param partPriceItemWriter the part price item writer
     */
    public PxpPriceMasterUploadWriter(RepositoryItemWriter<PartPriceMasterEntity> partPriceItemWriter) {
        this.partPriceItemWriter = partPriceItemWriter;
    }
    
	/**
	 * Write.
	 *
	 * @param items the items
	 * @throws Exception the exception
	 */
	@Override
	public void write(List<? extends PartPriceMasterEntity> items) throws Exception {
		
		List<PartPriceMasterEntity> partPriceMasterEntityList = new ArrayList<>();
		List<PartPriceMasterEntity> finalPartPriceMasterEntityList = new ArrayList<>();

		for (PartPriceMasterEntity item : items) {
			PartPriceMasterIdEntity partPriceMasterIdEntity = item.getId();
			List<String> packingSpecPartNoList = noemPackSpecRepository.findPartNoList(partPriceMasterIdEntity.getCfCode(), partPriceMasterIdEntity.getDestCode(), partPriceMasterIdEntity.getPartNo(), partPriceMasterIdEntity.getEffFromMonth(), item.getEffToMonth());
			if (packingSpecPartNoList.isEmpty()) {
				partPriceMasterIdEntity.setPartNo(partPriceMasterIdEntity.getPartNo().concat("00"));
				item.setId(partPriceMasterIdEntity);
				partPriceMasterEntityList.add(item);
			} else {
				for (String packingSpecPartNo : packingSpecPartNoList) {
					partPriceMasterIdEntity.setPartNo(packingSpecPartNo);
					item.setId(partPriceMasterIdEntity);
					partPriceMasterEntityList.add(item);
				}
			}
        }
		
		if (!partPriceMasterEntityList.isEmpty()) {
			finalPartPriceMasterEntityList = checkTimeControl(partPriceMasterEntityList);
		}
		
		partPriceItemWriter.write(finalPartPriceMasterEntityList);
		
	}
	
	/**
	 * Check time control.
	 *
	 * @param partPriceMasterEntityList the part price master entity list
	 * @return the list
	 * @throws ParseException the parse exception
	 */
	private List<PartPriceMasterEntity> checkTimeControl(List<PartPriceMasterEntity> partPriceMasterEntityList) throws ParseException {
		List<PartPriceMasterEntity> finalPartPriceMasterEntityList = new ArrayList<>();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMM");
		for (PartPriceMasterEntity partPriceMasterEntity : partPriceMasterEntityList) {
			if (partPriceMasterRepository.countByCurrencyCodeAndIdCfCodeAndIdDestCodeAndIdPartNoAndIdEffFromMonthAndEffToMonth(
					partPriceMasterEntity.getCurrencyCode(), partPriceMasterEntity.getId().getCfCode(), 
					partPriceMasterEntity.getId().getDestCode(), partPriceMasterEntity.getId().getPartNo(), 
					partPriceMasterEntity.getId().getEffFromMonth().replace("/", ""), partPriceMasterEntity.getEffToMonth().replace("/", "")) > 0) {
				finalPartPriceMasterEntityList.add(partPriceMasterEntity);
			} else {
				PartPriceMasterEntity partPriceMaster = partPriceMasterRepository.findMaxControlRecordByCurrencyAndCfcAndImpAndPartNo(
						partPriceMasterEntity.getCurrencyCode(), partPriceMasterEntity.getId().getCfCode(), 
						partPriceMasterEntity.getId().getDestCode(), partPriceMasterEntity.getId().getPartNo());
				if (partPriceMaster != null) {
					if (formatter.parse(partPriceMasterEntity.getId().getEffFromMonth()).before(formatter.parse(partPriceMaster.getId().getEffFromMonth()))) {
						partPriceMasterRepository.delete(partPriceMaster);
					}
					List<PartPriceMasterEntity> partPriceMasterList = partPriceMasterRepository.findOldTimeControlRecords(
							partPriceMasterEntity.getCurrencyCode(), partPriceMasterEntity.getId().getCfCode(), 
							partPriceMasterEntity.getId().getDestCode(), partPriceMasterEntity.getId().getPartNo(), partPriceMasterEntity.getId().getEffFromMonth());
					for (PartPriceMasterEntity partPriceMst : partPriceMasterList) {
						partPriceMst.setEffToMonth(formatter.format(DateUtils.addMonths(formatter.parse(partPriceMasterEntity.getId().getEffFromMonth()), -1)));
						finalPartPriceMasterEntityList.add(partPriceMst);
					}
				}
				finalPartPriceMasterEntityList.add(partPriceMasterEntity);
			}
		}
		return finalPartPriceMasterEntityList;
	}

}
