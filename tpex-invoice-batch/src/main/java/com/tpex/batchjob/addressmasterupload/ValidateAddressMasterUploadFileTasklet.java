package com.tpex.batchjob.addressmasterupload;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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
import com.tpex.repository.AddressMasterRepository;
import com.tpex.repository.OemProcessCtrlRepository;
import com.tpex.repository.TpexConfigRepository;
import com.tpex.util.BatchUtil;
import com.tpex.util.ConstantUtils;

@Component
public class ValidateAddressMasterUploadFileTasklet implements Tasklet {

	@Autowired
	private TpexConfigRepository tpexConfigRepository;
	
	@Autowired
	private AddressMasterRepository addressMasterRepository;
	
	@Autowired
	private OemProcessCtrlRepository oemProcessCtrlRepository;
	
	int defaultInvoiceAddressCount = 0;
	
	List<Map<String, Object>> csvList = new ArrayList<>();
	
	@Autowired
	private BatchUtil batchUtil;
	
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
					if (validateCsvData(workbook, fileName, jobParameters.getString("companyCode"))) {
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
	
	private boolean validateCsvData(Workbook workbook, String fileName, String companyCode) throws IOException {
		if (addressMasterRepository.countByInvoiceFlagAndCompanyCode("Y", companyCode) > 0) {
			defaultInvoiceAddressCount++;
		}
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
			row.createCell(23).setCellValue(errorList.isEmpty() ? null : String.join(",", errorList));
		}
		if(isError) {
			Row row = worksheet.getRow(0);
			Cell cell = row.createCell(23);
			cell.setCellValue("Error Reason");
			cell.setCellStyle(row.getCell(22).getCellStyle());
			StringBuilder filePath = new StringBuilder().append(tpexConfigRepository.findByName("invoiceGeneration.report.directory").getValue()).append("/").append(fileName);
			try (FileOutputStream outputStream = new FileOutputStream(filePath.toString())) {
	            workbook.write(outputStream);
	        } catch (IOException e) {
				workbook.close();
			}
		}
		workbook.close();
		return isError;
	}

	private List<String> validateCsvRow(Row row) {
		String companyCode = getStringCellValue(row.getCell(0));
		String branch = getStringCellValue(row.getCell(1));
		String companyName = getStringCellValue(row.getCell(2));
		String address1 = getStringCellValue(row.getCell(3));
		String address2 = getStringCellValue(row.getCell(4));
		String address3 = getStringCellValue(row.getCell(5));
		String address4 = getStringCellValue(row.getCell(6));
		String zipCode = getStringCellValue(row.getCell(7));
		String shortName = getStringCellValue(row.getCell(8));
		String sapAcCode = getStringCellValue(row.getCell(9));
		String consignee = getStringCellValue(row.getCell(10));
		String countryCode = getStringCellValue(row.getCell(11));
		String telephoneNo = getStringCellValue(row.getCell(12));
		String faxNo = getStringCellValue(row.getCell(13));
		String email = getStringCellValue(row.getCell(14));
		String telexNo = getStringCellValue(row.getCell(15));
		String contactPerson = getStringCellValue(row.getCell(16));
		String defaultInvoiceAddress = getStringCellValue(row.getCell(17));
		String scFlag = getStringCellValue(row.getCell(18));
		String scRemark = getStringCellValue(row.getCell(19));
		String employee1 = getStringCellValue(row.getCell(20));
		String employee2 = getStringCellValue(row.getCell(21));
		String employee3 = getStringCellValue(row.getCell(22));
		
		List<String> errorList = new ArrayList<>();
		
		errorList.addAll(validateCompanyCode(companyCode));
		errorList.addAll(validateBranch(branch));
		errorList.addAll(validateCompanyName(companyName));
		errorList.addAll(validateAddress(address1, address2, address3, address4, zipCode, countryCode));
		
		Set<String> csvDataList = new HashSet<>();
		if (!companyCode.isBlank() && !branch.isBlank() && csvDataList.contains(companyCode + branch)) {
			errorList.add(ConstantUtils.ERR_IN_1073);
		}
		csvDataList.add(companyCode + branch);
		
		if (shortName.isBlank())
			errorList.add("Short Name cannot be blank");
		
		if (shortName.length() > 15)
			errorList.add("Short Name cannot be more than 15 characters");
		
		if (sapAcCode.isBlank())
			errorList.add("SAP A/C Code cannot be blank");
		
		if (sapAcCode.length() > 4)
			errorList.add("SAP A/C Code cannot be more than 4 characters");
		
		if (consignee.length() > 15)
			errorList.add("Consignee cannot be more than 3 characters");
		
		if (telephoneNo.length() > 30)
			errorList.add("Telephone No. cannot be more than 30 characters");
		
		if (faxNo.length() > 15)
			errorList.add("Fax No. cannot be more than 15 characters");
		
		errorList.addAll(validateEmailAddress(email));
		
		if (telexNo.length() > 20)
			errorList.add("Telex No. cannot be more than 20 characters");
		
		if (contactPerson.length() > 30)
			errorList.add("Contact Person cannot be more than 30 characters");

		errorList.addAll(validateDefaultInvoiceAddress(defaultInvoiceAddress));
		errorList.addAll(validateSCFlag(scFlag, scRemark));
		errorList.addAll(validateEmployee(scFlag, employee1, employee2, employee3));
		
		if (companyCode.equals(consignee)) {
			errorList.add("Consignee Code cannot be same as Company Code.");
		}
		
		if (!consignee.isBlank() && addressMasterRepository.countByIdCmpCode(consignee) == 0) {
			errorList.add("Consignee Code doesn't exist in Address Master. Please check company code");
		}
		
		return errorList;
	}

	private List<String> validateCompanyCode(String companyCode) {
		List<String> errorList = new ArrayList<>();
		if (companyCode.isBlank())
			errorList.add("Company code cannot be blank");
		
		if (companyCode.length() > 3)
			errorList.add("Company code cannot be more than 3 characters");
		
		return errorList;
	}
	
	private List<String> validateBranch(String branch) {
		List<String> errorList = new ArrayList<>();
		if (branch.isBlank())
			errorList.add("Branch cannot be blank");
		
		if (branch.length() > 3)
			errorList.add("Branch cannot be more than 3 characters");
		
		return errorList;
	}
	
	private List<String> validateCompanyName(String companyName) {
		List<String> errorList = new ArrayList<>();
		if (companyName.isBlank())
			errorList.add("Company Name cannot be blank");
		
		if (companyName.length() > 80)
			errorList.add("Company Name cannot be more than 80 characters");
		
		return errorList;
	}
	
	private List<String> validateAddress(String address1, String address2, String address3, String address4, String zipCode, String countryCode) {
		List<String> errorList = new ArrayList<>();
		if (address1.isBlank())
			errorList.add("Address1 cannot be blank");
		
		if (address1.length() > 40)
			errorList.add("Address1 cannot be more than 40 characters");
		
		if (address2.length() > 40)
			errorList.add("Address2 cannot be more than 40 characters");
		
		if (address3.length() > 40)
			errorList.add("Address3 cannot be more than 40 characters");
		
		if (address4.length() > 40)
			errorList.add("Address4 cannot be more than 40 characters");
		
		if (zipCode.length() > 10)
			errorList.add("Zip Code cannot be more than 10 characters");
		
		if (countryCode.isBlank())
			errorList.add("Country code cannot be blank");
		
		if (countryCode.length() > 3)
			errorList.add("Country code cannot be more than 3 characters");
		
		return errorList;
	}
	
	private List<String> validateEmailAddress(String email) {
		List<String> errorList = new ArrayList<>();
		
		if (!email.isBlank() && !Pattern.compile("^(.+)@(\\S+)$").matcher(email).matches())
			errorList.add("Email Address is not valid");
		
		if (email.length() > 60)
			errorList.add("Email Address cannot be more than 60 characters");
		
		return errorList;
	}
	
	private List<String> validateDefaultInvoiceAddress(String defaultInvoiceAddress) {
		List<String> errorList = new ArrayList<>();
		
		if (!(defaultInvoiceAddress.isBlank() || defaultInvoiceAddress.equalsIgnoreCase("Y") || defaultInvoiceAddress.equalsIgnoreCase("N")))
			errorList.add("Invoice Address can have only value ‘Y’ or ‘N’ or blank");
		
		if (defaultInvoiceAddress.equalsIgnoreCase("Y")) {
			defaultInvoiceAddressCount++;
			if (defaultInvoiceAddressCount > 1)
				errorList.add("Default Invoice Address can be ‘Y’ for only one record under login company.");
		}
		
		return errorList;
	}

	private List<String> validateSCFlag(String scFlag, String scRemark) {
		List<String> errorList = new ArrayList<>();
		
		if (scFlag.isBlank())
			errorList.add("SC Flag cannot be blank");
		
		if (scFlag.length() > 1)
			errorList.add("SC Flag cannot be more than 1 characters");
		
		if (!(scFlag.equalsIgnoreCase("Y") || scFlag.equalsIgnoreCase("N")))
			errorList.add("SC Flag can have only value ‘Y’ or ‘N’");
		
		if (scFlag.equalsIgnoreCase("Y") && scRemark.isBlank())
			errorList.add("SC Remark cannot be blank when SC Flag = Y");
		
		if (scRemark.length() > 400)
			errorList.add("SC Remark cannot be more than 400 characters");
		
		return errorList;
	}
	
	private List<String> validateEmployee(String scFlag, String employee1, String employee2,
			String employee3) {
		List<String> errorList = new ArrayList<>();
		
		if (scFlag.equalsIgnoreCase("Y") && employee1.isBlank())
			errorList.add("Employee 1 cannot be blank when SC Flag = Y");
		
		if (employee1.length() > 50)
			errorList.add("Employee 1 cannot be more than 50 characters");
		
		if (employee2.length() > 50)
			errorList.add("Employee 2 cannot be more than 50 characters");
		
		if (employee3.length() > 50)
			errorList.add("Employee 3 cannot be more than 50 characters");
		
		return errorList;
	}

	private String getStringCellValue(Cell cell) {
		if (cell == null || cell.getCellType() == CellType.BLANK) {
			return "";
		}
	    if (cell.getCellType().equals(CellType.STRING)) {
	    	return cell.getStringCellValue().trim();
	    } else {
	    	return String.valueOf((long) cell.getNumericCellValue()).trim();
	    }
    }

}
