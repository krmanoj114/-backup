package com.tpex.batchjob.lotpartpriceupload;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tpex.dto.BatchJobDTO;
import com.tpex.entity.OemLotPrcMstEntity;
import com.tpex.entity.OemLotPrcMstEntityID;
import com.tpex.repository.LotPriceMasterRepository;
import com.tpex.util.TpexConfigurationUtil;

/**
 * The Class CalculateLotPriceTasklet.
 */
@Component
public class CalculateLotPriceTasklet implements Tasklet {

	/** The tpex configuration util. */
	@Autowired
	private TpexConfigurationUtil tpexConfigurationUtil;
	
	/** The lot price master repository. */
	@Autowired
	private LotPriceMasterRepository lotPriceMasterRepository;
	
	/**
	 * Execute.
	 *
	 * @param contribution the contribution
	 * @param chunkContext the chunk context
	 * @return the repeat status
	 * @throws Exception the exception
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();
		
		if (stepExecution.isPresent()) {
			JobParameters jobParameters = stepExecution.get().getJobParameters();
			if (jobParameters.getString("batchName") != null) {
				String filePath = tpexConfigurationUtil.getFilePath(jobParameters.getString("batchName"));
				File file = ResourceUtils.getFile(filePath);
				if (!file.exists())
					throw new FileNotFoundException("File Not exist in path = " + filePath);
				
				ObjectMapper objMapper = new ObjectMapper();
				objMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				BatchJobDTO batchJobDTO = objMapper.readValue(file, new TypeReference<BatchJobDTO>() {});
				File inputFile = ResourceUtils.getFile(batchJobDTO.getBatchJobInputFilePath() + jobParameters.getString("fileName"));
				XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(inputFile));
				XSSFSheet worksheet = workbook.getSheetAt(0);
				
				List<Map<String, Object>> csvDataList = new ArrayList<>();
				if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
					Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
					itr.next();
					while (itr.hasNext()) {  
						Row row = itr.next();
						Map<String, Object> csvDataRow = new HashMap<>();
						csvDataRow.put("currencyCode", row.getCell(0).getStringCellValue());
						csvDataRow.put("lot", row.getCell(1).getStringCellValue());
						csvDataRow.put("cfcCode", row.getCell(2).getStringCellValue());
						csvDataRow.put("impCode", row.getCell(3).getStringCellValue());
						csvDataRow.put("partNo", row.getCell(4).getStringCellValue());
						csvDataRow.put("partName", row.getCell(5).getStringCellValue());
						csvDataRow.put("firstOfPrice", getDoubleCellValue(row.getCell(6)));
						csvDataRow.put("usage", getDoubleCellValue(row.getCell(7)));
						csvDataList.add(csvDataRow);
					}
					
					updateLotPrice(csvDataList, jobParameters.getString("effectiveDate"));
				}
				workbook.close();
			}
		}
		return RepeatStatus.FINISHED;
	}
	
	/**
	 * Update lot price.
	 *
	 * @param csvDataList the csv data list
	 * @param effectiveDate the effective date
	 */
	@SuppressWarnings("unchecked")
	private void updateLotPrice(List<Map<String, Object>> csvDataList, String effectiveDate) {
		Map<Object, Double> lotPriceGroupList = csvDataList.stream().collect(
	    		Collectors.groupingBy(m -> Arrays.asList(m.get("cfcCode"), m.get("impCode"), m.get("lot"), m.get("currencyCode")), 
                Collectors.summingDouble(m -> (double) m.get("firstOfPrice") * (double) m.get("usage"))));
	    
		for (Entry<Object, Double> entry : lotPriceGroupList.entrySet()) {
			List<String> key = (List<String>) entry.getKey();
			Optional<OemLotPrcMstEntity> oemLotPrcMstEntityOptional = lotPriceMasterRepository.findById(
					new OemLotPrcMstEntityID(key.get(0), key.get(1), 
							key.get(2), key.get(3), effectiveDate));
			
			if (oemLotPrcMstEntityOptional.isPresent()) {
				OemLotPrcMstEntity oemLotPrcMstEntity = oemLotPrcMstEntityOptional.get();
				oemLotPrcMstEntity.setLotPrice(entry.getValue());
				lotPriceMasterRepository.save(oemLotPrcMstEntity);
			}
		}
	}
	
	/**
	 * Gets the double cell value.
	 *
	 * @param cell the cell
	 * @return the double cell value
	 */
	private double getDoubleCellValue(Cell cell) {
	    if (cell.getCellType().equals(CellType.NUMERIC)) {
	    	return cell.getNumericCellValue();
	    } else {
	    	return Double.valueOf(cell.getStringCellValue());
	    }
    }

}
