package com.tpex.batchjob.partmasterupload;

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

import com.tpex.entity.OemProcessCtrlEntity;
import com.tpex.entity.OemProcessCtrlIdEntity;
import com.tpex.repository.InhouseShopMasterRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.PartMasterRespository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;

@Component
public class PartMasterUploadValidator implements Tasklet{

	@Autowired
	private BatchUtil batchUtil;

	@Autowired
	PartMasterRespository partMasterRespository;

	@Autowired
	OemProcessCtrlRepository oemProcessCtrlRepository;

	@Autowired
	private TpexConfigRepository tpexConfigRepository;

	@Autowired
	InhouseShopMasterRepository inhouseShopMasterRepository;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		Optional<StepExecution> stepExecution = chunkContext.getStepContext().getStepExecution().getJobExecution()
				.getStepExecutions().stream().findFirst();

		if (stepExecution.isPresent()) {
			JobParameters jobParameters = stepExecution.get().getJobParameters();
			String fileName = jobParameters.getString(ConstantUtils.FILENAME);
			String filePath = batchUtil.getInputFile(jobParameters.getString(ConstantUtils.JOB_P_BATCH_NAME), fileName);
			File inputFile = ResourceUtils.getFile(filePath);
			Workbook workbook = batchUtil.getWorkbook(fileName, inputFile);
			Sheet worksheet = workbook.getSheetAt(0);

			if (!(worksheet.getPhysicalNumberOfRows() == 0 || worksheet.getPhysicalNumberOfRows() == 1)) {
				if (validateCsvData(workbook, fileName)) {
					stepExecution.get().setExitStatus(new ExitStatus(ConstantUtils.BATCHSTATUS_FAILURE));
					Optional<OemProcessCtrlEntity> oemProcessCtrlEntityOptional = oemProcessCtrlRepository.findById(new OemProcessCtrlIdEntity(jobParameters.getString(ConstantUtils.JOB_P_BATCH_ID), Integer.parseInt(jobParameters.getString(ConstantUtils.JOB_P_PROCESS_CTRL_ID))));
					if (oemProcessCtrlEntityOptional.isPresent()) {
						OemProcessCtrlEntity oemProcessCtrlEntity = oemProcessCtrlEntityOptional.get();
						oemProcessCtrlEntity.setErrorFilePath(tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue() + "/" + fileName);
						oemProcessCtrlRepository.save(oemProcessCtrlEntity);
					}
				} else {
					stepExecution.get().setExitStatus(new ExitStatus(ConstantUtils.BATCHSTATUS_PROCESS));
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
			row.createCell(5).setCellValue(errorList.isEmpty() ? null : String.join(",", errorList));
		}
		
		worksheet.autoSizeColumn(5);

		if(isError) {
			Row row = worksheet.getRow(0);
			Cell cell = row.createCell(5);
			cell.setCellValue(ConstantUtils.ERROR_REASON);
			cell.setCellStyle(row.getCell(3).getCellStyle());
			StringBuilder filePath = new StringBuilder().append(tpexConfigRepository.findByName(ConstantUtils.INCVOICE_GENERATION_REPORT_DIRECTORY).getValue()).append("/").append(fileName);
			try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
				workbook.write(outputStream);
			} catch (IOException e) {
				workbook.close();
			}
		}
		return isError;
	}

	private List<String> validateCsvRow(Row row) {
		String partNo = batchUtil.getStringCellValue(row.getCell(0));
		String partName = batchUtil.getStringCellValue(row.getCell(1));
		String partType = batchUtil.getStringCellValue(row.getCell(2));
		String inHouseShop = batchUtil.getStringCellValue(row.getCell(3));
		String partWeight = batchUtil.getStringCellValue(row.getCell(4));

		List<String> errorList = new ArrayList<>();

		//--------Mandatory Check--------------//
		validateMandatory(partNo, partName, partType, partWeight, errorList);

		validateForZeroValue(partNo, partName, partType, inHouseShop, partWeight, errorList);

		//--------Part No length Check--------------//

		if(partNo.length()!=12)
			errorList.add(ConstantUtils.ERR_IN_1202);

		//---------------Part Name Check---------------------------

		if(partName.length() > 40)
			errorList.add(ConstantUtils.ERR_IN_1203);

		//-------------Duplicate Record------------------

		if (!partNo.isBlank() && partMasterRespository.findById(partNo).isPresent())
			errorList.add(ConstantUtils.ERR_IN_1207);

		//-------------Part Type Value Check------------------
		if (!partType.isBlank() && (Integer.parseInt(partType) <=0 || Integer.parseInt(partType) > 5))
			errorList.add(ConstantUtils.ERR_IN_1206);

		//----------Inhouse Shop Not entered correctly.--------------------
		if (!inHouseShop.isBlank() && inhouseShopMasterRepository.countByInsShopCd(inHouseShop) == 0)
			errorList.add(ConstantUtils.ERR_IN_1205);

		//----------Inshouse Not Entered when part type is inhouse.--------------------
		if(!partType.isBlank() && Integer.parseInt(partType) == 3 && inHouseShop.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1204);

		return errorList;
	}

	private void validateForZeroValue(String partNo, String partName, String partType, String inHouseShop,
			String partWeight, List<String> errorList) {

		if(!partNo.isBlank() && partNo.equals(ConstantUtils.ZERO))
			errorList.add(ConstantUtils.ERR_IN_1201);
        
		if(!partName.isBlank() && partName.equals(ConstantUtils.ZERO))
			errorList.add(ConstantUtils.ERR_IN_1201);

		if(!partType.isBlank() && partType.equals(ConstantUtils.ZERO))
			errorList.add(ConstantUtils.ERR_IN_1201);

		if(!inHouseShop.isBlank() && inHouseShop.equals(ConstantUtils.ZERO))
			errorList.add(ConstantUtils.ERR_IN_1201);

		if(!partWeight.isBlank() && partWeight.equals(ConstantUtils.ZERO))
			errorList.add(ConstantUtils.ERR_IN_1201);
	}

	private void validateMandatory(String partNo, String partName, String partType, String partWeight,
			List<String> errorList) {
		if (partNo.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1200);

		if (partName.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1200);

		if (partType.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1200);

		if (partWeight.isBlank())
			errorList.add(ConstantUtils.ERR_IN_1200);
	}

}
