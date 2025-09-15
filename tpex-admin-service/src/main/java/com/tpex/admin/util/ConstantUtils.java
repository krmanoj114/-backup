package com.tpex.admin.util;

public class ConstantUtils {


	private ConstantUtils() {

	}

	// Common Messages(CM) Info
	public static final String INFO_CM_3001 = "INFO_CM_3001"; // No data found.
	public static final String INFO_CM_3002 = "INFO_CM_3002"; // {count} record deleted successfully.
	public static final String INFO_CM_3003 = "INFO_CM_3003"; // Information saved successfully.
	public static final String INFO_CM_3004 = "INFO_CM_3004"; // File Uploaded Successfully.
	public static final String INFO_CM_3005 = "INFO_CM_3005"; // File uploaded successfully with warning, please check
	// warning/error
	public static final String INFO_CM_3006 = "INFO_CM_3006"; // Record Deleted Successfully.
	public static final String INFO_CM_3007 = "INFO_CM_3007"; // Record Updated Successfully.
	public static final String INFO_CM_3008 = "INFO_CM_3008"; // No changes to save.
	public static final String INFO_CM_3009 = "INFO_CM_3009";
	public static final String ERR_CM_3021 = "ERR_CM_3021"; // Please select xlsx file to Upload.

	// Common Messages(CM) Warning
	public static final String WRN_CM_3001 = "WRN_CM_3001"; // It will delete {count} record. Do you wish to delete the
	// selected record?
	public static final String WARN_IN_3002 = "WARN_IN_3002"; // Effective From Month is same as current month.
	// Do you confirm to upload
	// Common Messages Status
	public static final String INFO_CM_3010 = "INFO_CM_3010"; // Error
	public static final String INFO_CM_3011 = "INFO_CM_3011"; // Success
	public static final String INFO_CM_3012 = "INFO_CM_3012"; // Processing
	public static final String INFO_CM_3013 = "INFO_CM_3013"; // Pending
	public static final String INFO_CM_3014 = "INFO_CM_3014";  //Default Flag <Regular> has be removed from Payment Term Code : <Payment Term code of existing record> and set to Payment Term Code : <Payment Term Code of latest record>. 
	public static final String INFO_CM_3015 = "INFO_CM_3015";  //Default Flag <CPO/SPO> has be removed from Payment Term Code : <Payment Term code of existing record> and set to Payment Term Code : <Payment Term Code of latest record>. 
	public static final String INFO_CM_3016 = "INFO_CM_3016";	// Combination of INFO_CM_3014 and INFO_CM_3015
	// Common Messages(CM) Error
	public static final String ERR_CM_3001 = "ERR_CM_3001"; // Please input mandatory information.
	public static final String ERR_CM_3002 = "ERR_CM_3002"; // Effective From Date should not be greater than Effective
	// To Date.
	public static final String ERR_CM_3003 = "ERR_CM_3003"; // Please select a record to copy.
	public static final String ERR_CM_3004 = "ERR_CM_3004"; // Please select record to delete.
	public static final String ERR_CM_3005 = "ERR_CM_3005"; // {errorCode} – Cannot delete selected record, please try
	// again or contact to the Administrator.
	public static final String ERR_CM_3006 = "ERR_CM_3006"; // {errorCode} – There is some issue in updating the
	// Information, please try again or contact to the
	// Administrator.
	public static final String ERR_CM_3007 = "ERR_CM_3007"; // ETD From should not be greater than ETD To.
	public static final String ERR_CM_3008 = "ERR_CM_3008"; // Duplicate record found for keyColumns
	public static final String ERR_CM_3009 = "ERR_CM_3009"; // Uploaded file template is not correct.
	public static final String ERR_CM_3010 = "ERR_CM_3010"; // Destination code is over the max limit.
	public static final String ERR_CM_3011 = "ERR_CM_3011"; // Destination does not exists in Final Destination Master.
	public static final String ERR_CM_3013 = "ERR_CM_3013"; // Effective Date From cannot be blank.
	public static final String ERR_CM_3014 = "ERR_CM_3014"; // Effective Date format is Invalid, Follow DD/MM/YYYY"
	public static final String ERR_CM_3015 = "ERR_CM_3015"; // Effective From Date must be greater than Effective To
	// Date of last record
	public static final String ERR_CM_3016 = "ERR_CM_3016"; // Effective From Date must be in continuation with
	// Effective To Date of last record
	public static final String ERR_CM_3017 = "ERR_CM_3017"; // {filename} not configured in database.
	public static final String ERR_CM_3018 = "ERR_CM_3018"; // File Not exist in path = filePath
	public static final String ERR_CM_3019 = "ERR_CM_3019"; // From Date & Time should not be greater than To Date &
	// Time.
	public static final String ERR_CM_3020 = "ERR_CM_3020"; // {errorCode} There is some issue in downloading the
	// Information, please try again or contact to the
	// Administrator.
	public static final String ERR_CM_3022 = "ERR_CM_3022"; // Monthly VPR is not available for selected yearMonth
	public static final String ERR_CM_3025 = "ERR_CM_3025"; //There is some issue in updating the Information, please try again or contact to the Administrator.
	public static final String ERR_CM_3023 = "ERR_CM_3023"; //Cannot delete selected record, please try again or contact to the Administrator.
	public static final String ERR_CM_3024 = "ERR_CM_3024"; // Van Date From should not be greater than Van Date To
	public static final String ERR_CM_3030 = "ERR_CM_3030"; //Wrong file selected.

	public static final String BATCH_ID_NATCALUPLOAD = "NATCALUPLOAD";
	public static final String BATCH_ID_LOTPARTPRICEMASTER = "BINF106";

	// Common Download Upload
	public static final String INFO_AD_2001 = "INFO_AD_2001"; // It will be Offline Download, So please check the File
	// in “On Demand Download Report”
	public static final String INFO_AD_2011 = "NATCALUPLOAD";
	public static final String INFO_AD_2012 = "BINF106";

	public static final String ERR_AD_2001 = "ERR_AD_2001"; // No Records in file to process
	public static final String ERR_AD_2002 = "ERR_AD_2002"; // Could not create upload folder!
	public static final String ERR_AD_2006 = "ERR_AD_2006"; // File Format Not Correct
	public static final String ERR_AD_2007 = "ERR_AD_2007"; // Invalid Month
	public static final String ERR_AD_2008 = "ERR_AD_2008"; // Invalid Date
	public static final String ERR_AD_2009 = "ERR_AD_2009"; // Invalid Day
	public static final String ERR_AD_2010 = "ERR_AD_2010"; // Invalid Day, Only W or H allowed.
	public static final String ERR_AD_2011 = "ERR_AD_2011"; // Upload Request for - {year} does not match with
	// {excelYearValue} in file.
	public static final String ERR_AD_2012 = "ERR_AD_2012"; // Found duplicate records for dateStr
	public static final String ERR_AD_2013 = "ERR_AD_2013"; //Default Flag Regular cannot be set for more than 1 record = {paymenttermcode}.
	public static final String ERR_AD_2014 = "ERR_AD_2014"; //Default Flag CPO/SPO cannot be set for more than 1 record = {paymenttermcode}.

	// Common
	public static final String MESSAGE = "message";
	public static final String OFFLINE = "offline";
	public static final String STATUS = "status";
	public static final String STS_ERROR = "Error";
	public static final String STS_SUCCESS = "Success";
	public static final String STS_SUCCESS_WTH_WARN = "Success with warning";
	public static final String STS_PROCESSING = "Processing";
	public static final String STS_PENDING = "Pending";

	// Process Log Details
	public static final String PL_STATUS_INFO = "Info";
	public static final String PL_STATUS_ERROR = "Error";
	public static final String PL_JB_STRT_MSG = "Job execution started";
	public static final String PL_JB_CMP_MSG = "Job execution completed";
	public static final String PL_JB_STOP_MSG = "Stopped job due to some error";
	public static final String PL_JB_SUMMARY_MSG = "Summary - Total records / Success / Error";
	public static final String PL_JB_PROC_ROW_MSG = "Processing row ";
	public static final String PL_JB_FILE_FND_MSG = "File found";
	public static final String PL_JB_READ_FILE_MSG = "Reading file";

	public static final String OUTSTREAM = "outStream";
	public static final String FILENAME = "fileName";
	public static final String INVOICE_GENERATION_REPORT_FORMAT = "invoiceGeneration.report.format";
	public static final String DATE_HOUR = "ddMMyyyy_HHmmss";
	public static final String DATE = "ddMMyyyy";

	public static final String END_PAGE_INDEX = "setEndPageIndex";
	public static final String START_PAGE_INDEX = "setStartPageIndex";
	public static final String PAGE_INDEX = "setPageIndex";
	public static final String HYPER_LINK = "setIgnoreHyperlink";
	public static final String OFFSET_X = "setOffsetX";
	public static final String OFFSET_Y = "setOffsetY";
	public static final String OVERRIDE_HINTS = "setOverrideHints";

	public static final String INDAYS = "InDays";
	public static final String INMONTH = "InMonth";

	public static final String INDAY = "In Days";
	public static final String INMONTHS = "In Month";

	public static final String YYYYMM = "yyyy/MM";
	public static final String EXCEPTION = "Exception: ";

	public static final String DETAILBGCOLOR = "detailBGColor";

	public static final String DETAILFONTCOLOR = "detailFontColor";

	public static final String DETAILFONTSIZE = "detailFontSize";

	public static final String ATTACHMENT_FILENAME = "attachment; filename=";
	public static final String CONTENT_DISPOSITION = "Content-Disposition";
	public static final String USERID = "userId";
	public static final String PART_PRICE_NUMBER = "531530206000";

	// Common Upload-Download Excel-Country of Origin
	public static final String CALIBRIFONT = "Calibri";
	public static final String PART_NO = "*Part no.";
	public static final String PART_NAME = "Part Name";
	public static final String COUNTRY_OF_ORIGIN_CODE_COOC = "*Country of Origin Code";
	public static final String VAN_DATE_FROM = "*Van Date From";
	public static final String VAN_DATE_TO = "*Van Date To";
	public static final String XX12 = "xx..(12)";
	public static final String XX40 = "xx..(40)";
	public static final String CODE_COUNTRY_NAME = "Code+ Country Name";
	public static final String POIDATEFORMATE = "DD/MM/YYYY";
	public static final String POIDATEFORMATEFORCELL = "DD/MM/YYYY";
	public static final String COUNTRY_OF_ORIGIN_CODE = "Country of Origin code";
	public static final String COUNTRY_NAME = "Country Name";
	public static final String SQL_DATE_FORMAT = "yyyy-MM-dd";

	public static final String DETAILVALIGN = "detailVAlign";
	public static final String FALSE = "false";
	public static final String HEADINGBGCOLOR = "headingBGColor";
	public static final String HEADINGFONTCOLOR = "headingFontColor";
	public static final String HEADINGFONTSIZE = "headingFontSize";
	public static final String JASPER_REPORT_DETAILBGCOLOR = "jasper.report.detailBGColor";
	public static final String JASPER_REPORT_DETAILFONTCOLOR = "jasper.report.detailFontColor";
	public static final String JASPER_REPORT_DETAILFONTSIZE = "jasper.report.detailFontSize";
	public static final String JASPER_REPORT_DETAILVALIGN = "jasper.report.detailVAlign";
	public static final String JASPER_REPORT_HEADINGBGCOLOR = "jasper.report.headingBGColor";
	public static final String JASPER_REPORT_HEADINGFONTCOLOR = "jasper.report.headingFontColor";
	public static final String JASPER_REPORT_HEADINGFONTSIZE = "jasper.report.headingFontSize";
	public static final String LOGIN_USER_ID = "loginUserId";
	public static final String SET_DETECT_CELL_TYPE = "setDetectCellType";
	public static final String FORCE_LINE_BREAK_POLICY = "setForceLineBreakPolicy";
	public static final String SIZE_PAGE_TO_CONTENT = "setSizePageToContent";
	public static final String SET_WHITE_PAGE_BACKGROUND = "setWhitePageBackground";
	public static final String INVOICE_GENERATION_REPORT_DIRECTORY = "invoiceGeneration.report.directory";
	public static final String INVOICE_GENERATION_STORE_DB = "invoiceGeneration.storeInDB";
	public static final String NATIONAL_CALENDAR_REPORT_SIZE_LIMIT = "nationalCalendar.report.size.limit";

	public static final String ATTACHEMENTFILE_COUNTRY_OF_ORIGIN = "attachment;filename=CountryOfOrigin";

	public static final String COUNTRYNAME = "COUNTRY_NAME";
	public static final String COUNTRYCODE = "COUNTRY_CODE";
	public static final String KEYCOLUMNS = "keyColumns";
	public static final String MORTHANTENRECORD = "Only 10 records can be added at a time.";
	public static final String DUPLICATE_REC_FOR_PRODUCT_GRP_CD = "Duplicate record found for ";
	public static final String DUPLICATERECORD = "Duplicate record found for Car Family Code ";
	public static final String DUPLICATERECORDCARMASTER = "Car Family Code ";
	public static final String DUPLICATERECORDC = "Currency Code ";
	public static final String DUPLICATERECORDPORT = "Port Code ";
	public static final String DUPLICATERECORDPAYMENTTERM = "Duplicate record found for Payment Term  Code";
	public static final String DUPLICATERECORDFINALDESTINATION = "Duplicate record found for Destination Code ";
	public static final String DUPLICATERECORDFORSHIPPING = "Duplicate record found for ";

	public static final String DEFAULTDATEFORMAT = "dd/MM/yyyy HH:mm";
	public static final String COMPANYNAME = "TMT";
	public static final String PROCESS_CONTROL_ID = "processControlId";

	public static final String BATCH_JOB_SIZE = "batchJobsize";
	public static final String DELIMITER = "/";
	public static final String CD = "CD";
	public static final String PROD_GRP_CD = "PROD_GRP_CD";
	public static final String DST_CD = "DST_CD"; 

	// Common Upload-Download Excel-Car Family Destination Master

	public static final String CAR_FAMILY_CODE = "*Car Family Code";
	public static final String DESTINATION = "*Destination";
	public static final String RE_EXPORTER_CODE = "*Re Exp. Code";
	public static final String SERIES_NAME = "*Series Name";
	public static final String CFC = "CFC";
	public static final String DESTINATION_1 = "Destination";
	public static final String XX1 = "xx..(1)";
	public static final String XX15 = "xx..(15)";
	public static final String CAR_FAMILY_CODE_1 = "Car Family Code";
	public static final String SERIES_NAME_1 = "Series Name";
	public static final String DESTINATION_CODE = "Destination Code";
	public static final String DESTINATION_NAME = "Destination name";
	public static final String ATTACHEMENTFILE_CAR_FAMILY_DESTINATION = "attachment;filename=CarFamilyDestinationMaster";
	public static final String CAR_FAMILY_DESTINATION_FILENAME = "CarFamilyDestinationMaster";

	public static final String EXCEL_FORMAT = ".xlsx";
	public static final String CARFMLYDESTMST_DOWNLOAD_DATE_FORMAT = "ddMMyyyyHHmmss";

	public static final String SIMPLEDATEFORMATINPUT = "dd/MM/yyyy";
	public static final String SIMPLEDATEFORMATOUT = "yyyy-MM-dd";

	public static final String PAYMENT_TERM_CODE = "Payment Term Master";
	public static final String REGULAR = "REGULAR";
	public static final String REGULARINFO = "Regular";

	public static final String CPO_SPO = "CPO_SPO";

	public static final String FILE_NOT_FOUND = "Error File not found";

	public static final String BINF023 = "BINF023";

	public static final Integer CELLRANGE = 50;
	public static final Integer ROWINDEXINITIAL = 2;

	public static final String COUNTRY_OF_ORIGIN_FILENAME = "CountryOfOrigin";

	public static final String CFC_HIDDEN_SHEET_NAME = "cfcHidden";
	public static final String DESTINATION_HIDDEN_SHEET_NAME = "destHidden";

	public static final String ARIAL = "Arial";
	public static final String CAR_FMLY_DEST_HEADER_COLOR_CODE = "EDFBFF";
	public static final String CAR_FMLY_DEST_HEADER_BORDER_COLOR_CODE = "A9EBFF";
	public static final String CAR_FMLY_DEST_GRID_RIGHT_COLOR_CODE = "C8DAFB";
	public static final String CAR_FMLY_DEST_GRID_BOTTOM_COLOR_CODE = "DADADA";
	public static final String CAR_FMLY_CODE_FORMULA = "cfcHidden!$A$1:$A$";
	public static final String DESTINATION_FORMULA = "destHidden!$A$1:$A$";

	public static final String FILENAME_COO = "filename";

	public static final String PAYMENTTERINFOMMSG = "it should has 1 Payment Term Code set as Default Flag";


	public static final String REPORT_DIRECTORY = "reportDirectory";
	public static final String INCVOICE_GENERATION_REPORT_DIRECTORY = "invoiceGeneration.report.directory";
	public static final String REPORT_FORMAT = "reportFormat";
	public static final String REPORT_SIZE_LIMIT = "reportSizeLimit";
	public static final String INVOICE_GENERATION_REPORT_SIZE_LIMIT = "invoiceGeneration.report.size.limit";
	public static final String STORE_DB = "storeInDB";

	public static final String TEST_USER = "TestUser";
	public static final String P_I_V_ETD_MONTH = "P_I_V_ETD_MONTH";
	public static final String P_I_V_DEST = "P_I_V_DEST";
	public static final String P_I_V_CF = "P_I_V_CF";
	public static final String P_I_V_PKG_MNTH = "P_I_V_PKG_MNTH";
	public static final String P_I_V_ETD = "P_I_V_ETD";
	public static final String ATTACHMENT = "attachment; filename= ";

	public static final String SYSTEM_NAME = "TPEX";


	public static final String INFO_AD_4002 = "INFO_AD_4002"; //File uploaded successfully with some warning, please check error/warning file in Inquiry for Process Status and Control Screen.
	public static final String ERR_AD_4003 = "ERR_AD_4003"; //File upload failed, please check error/warning file in Inquiry for Process Status and Control screen.
	public static final String INFO_AD_4004 = "INFO_AD_4004"; //Maximum Attempts exceeded.
	public static final String INFO_AD_4005 = "INFO_AD_4005"; //File Uploading in progress

	public static final String SQL_UPDATE = "UPDATE ";
	public static final String SQL_SET = " SET ";
	public static final String SQL_WHERE = " WHERE ";
	public static final String SQL_SYSDATE = "'SYSDATE'";
	public static final String SYSDATE_FUNCTION = "SYSDATE()";
	public static final String PAYMENTTERM_CODE = "paymenttermcode";
	public static final String BINF024 = "BINF024";
	public static final String BINF005 = "BINF005"; 
	public static final String TARGET_CONTAINER = "TargetContainer";
	public static final String DST_CD_PRIMARY = "DST_CD_PRIMARY";
	
	public static final String DATEWITHTIME = "dd/MM/yyyy HH:mm:ss";
	public static final String NATIONAL_CALENDAR_REPORT_FORMAT = "nationalCalendar.report.format";
	
	public static final String PXP_PARTPRICE_REPORT_FORMAT = "pxppartprice.report.format";
	public static final String LOT_PARTPRICE_REPORT_FORMAT = "lotpartprice.report.format";
	public static final String ADDRESS_MASTER_REPORT_FORMAT = "addressmaster.report.format";
	
	public static final String BATCH_STATUS="batchStatus";
	public static final String ERR_AD_4004 = "ERR_AD_4004";//Invoice No. is Invalid
	public static final String ERR_AD_4005 = "ERR_AD_4005";//Haisen No. is Invalid
	public static final String WARN_AD_3003 = "WARN_AD_3003";//IXOS flag Y or N.

	public static final String INVOICE_NO = "invoiceNo";
	public static final String HAISEN_NO = "haisenNo";
	public static final String HAISEN_YEAR_MONTH = "haisenYearMonth";
	public static final String ADDITIONAL_PARAMETERS = "additionalParameters";
	public static final String VANNING_MONTH = "vanningMonth";
	public static final String ETD = "etd";
	public static final String DESTINATION_VALUE = "destination";
	
	public static final String WARN_AD_3004 = "WARN_AD_3004";//For Haisen Number.
		
	public static final String DATE_SQL = "yyyy-MM-dd";
	
	
	

}
