package com.tpex.batchjob.carfamilydestination;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import com.tpex.entity.CarFamilyDestinationMasterIdEntity;
import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.repository.CarFamilyDestinationMasterRepository;
import com.tpex.repository.CarFamilyMastRepository;
import com.tpex.repository.OemFnlDstMstRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;

@Component
public class CarFamilyDestMasterUploadValidator implements Tasklet {

	@Autowired
	private BatchUtil batchUtil;
	
	@Autowired
	private CarFamilyDestinationMasterRepository carFamilyDestinationMasterRepository;
	
	@Autowired
	private CarFamilyMastRepository carFamilyMastRepository;
	
	@Autowired
	private OemFnlDstMstRepository oemFnlDstMstRepository;
	
	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;
	
	@Autowired
	private TpexConfigRepository tpexConfigRepository;
	
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();
		
		if (stepExecution.isPresent()) {
			JobParameters jobParameters = stepExecution.get().getJobParameters();
			String fileName = jobParameters.getString("fileName");
			String filePath = batchUtil.getInputFile(jobParameters.getString("batchName"), fileName);
			File inputFile = ResourceUtils.getFile(filePath);
			Workbook workbook = batchUtil.getWorkbook(fileName, inputFile);
			Sheet worksheet = workbook.getSheetAt(0);
			
			if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
				if (validateCsvData(workbook, fileName)) {
					stepExecution.get().setExitStatus(new ExitStatus("FAILED"));
					Optional<OemProcessCtrlEntity> oemProcessCtrlEntityOptional = oemProcessCtrlRepository.findById(new OemProcessCtrlIdEntity(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID), Integer.parseInt(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID))));
					if (oemProcessCtrlEntityOptional.isPresent()) {
						OemProcessCtrlEntity oemProcessCtrlEntity = oemProcessCtrlEntityOptional.get();
						oemProcessCtrlEntity.setErrorFilePath(tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue() + "/" + fileName);
						oemProcessCtrlRepository.save(oemProcessCtrlEntity);
					}
				} else {
					stepExecution.get().setExitStatus(new ExitStatus("PROCESS"));
				}
			}
			workbook.close();
		}
		
		return null;
	}
	
	private boolean validateCsvData(Workbook workbook, String fileName) throws IOException {
		Sheet worksheet = workbook.getSheetAt(0);
		Iterator<Row> itr = worksheet.iterator(); //iterating over excel file  
		itr.next();
		itr.next();
		
		boolean isError = false;
		while (itr.hasNext()) {
			Row row = itr.next();
			List<String> errorList = validateCsvRow(row);
			if (!isError && !errorList.isEmpty()) {
				isError = true;
			}
			row.createCell(4).setCellValue(errorList.isEmpty() ? null : String.join(",", errorList));
		}
		
		if(isError) {
			Row row = worksheet.getRow(0);
			Cell cell = row.createCell(4);
			cell.setCellValue("Error Reason");
			cell.setCellStyle(row.getCell(3).getCellStyle());
			StringBuilder filePath = new StringBuilder().append(tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue()).append("/").append(fileName);
			try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
	            workbook.write(outputStream);
	        } catch (IOException e) {
	        	workbook.close();
			}
		}
		return isError;
	}

	private List<String> validateCsvRow(Row row) {
		String cfCode = batchUtil.getStringCellValue(row.getCell(0));
		String dstCode = batchUtil.getStringCellValue(row.getCell(1));
		String reExpCode = batchUtil.getStringCellValue(row.getCell(2));
		String srsName = batchUtil.getStringCellValue(row.getCell(3));
		
		List<String> errorList = new ArrayList<>();
		
		if (cfCode.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1045);
		
		if (dstCode.isBlank()) {
			errorList.add(ConstantUtils.ERR_IN_1064);
		} else {
			dstCode = dstCode.split("-")[0];
		}
		
		if (reExpCode.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1065);
		
		if (reExpCode.length() > 1)
			errorList.add(ConstantUtils.ERR_IN_1066);
		
		if (srsName.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1067);
		
		if (srsName.length() > 15)
			errorList.add(ConstantUtils.ERR_IN_1068);
		
		if (!cfCode.isBlank() && carFamilyMastRepository.countByCarFmlyCode(cfCode) == 0)
			errorList.add(ConstantUtils.ERR_IN_1069);
		
		if (!dstCode.isBlank() && oemFnlDstMstRepository.countByFdDstCd(dstCode) == 0)
			errorList.add(ConstantUtils.ERR_IN_1070);
		
		if (!cfCode.isBlank() && !dstCode.isBlank() && !reExpCode.isBlank() && carFamilyDestinationMasterRepository.findById(new CarFamilyDestinationMasterIdEntity(cfCode, dstCode, reExpCode)).isPresent())
			errorList.add(String.format(ConstantUtils.ERR_IN_1063, cfCode + ", " + dstCode + ", " + reExpCode));

		return errorList;
	}

}
