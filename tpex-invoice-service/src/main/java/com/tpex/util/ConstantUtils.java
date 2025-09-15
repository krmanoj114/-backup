package com.tpex.util;

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

	// Common Messages Status
	public static final String INFO_CM_3010 = "INFO_CM_3010"; // Error
	public static final String INFO_CM_3011 = "INFO_CM_3011"; // Success
	public static final String INFO_CM_3012 = "INFO_CM_3012"; // Processing
	public static final String INFO_CM_3013 = "INFO_CM_3013"; // Pending

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

	public static final String ERR_CM_3021 = "ERR_CM_3021"; // {deletedRecords} record deleted successfully, and
															// {notDeletedRecord} record not deleted.

	public static final String ERR_CM_3026 = "ERR_CM_3026"; // Duplicate record found for same {keyColumns}.

	public static final String ERR_IN_3023 = "ERR_IN_3023"; // Part No.: {<Part No.1>, <Part No.2>,... }Invoice already
															// generated based on this price. It cannot be deleted.
	public static final String ERR_CM_3027 = "ERR_CM_3027"; // Vanning Date From should not be greater than Vanning Date
															// To.”
	public static final String ERR_CM_3029 = "ERR_CM_3029";// Seq No :<Seq No.>Vanning Date From : <Vanning date From> &
															// Vanning Date To <Vanning date To> under <Show key column
															// value separated by comma> should not be overlapped with
															// existing or new record in GRID
	// Common Messages(CM) Warning
	public static final String WRN_CM_3001 = "WRN_CM_3001"; // It will delete {count} record. Do you wish to delete the
															// selected record?

	public static final String BATCH_ID_NATCALUPLOAD = "NATCALUPLOAD";

	public static final String SCM_SES_DESC = "Haisen Running Number for ";

	// Invoice 1001
	public static final String ERR_IN_1001 = "ERR_IN_1001"; // Please check either regular or CPO/SPO
	public static final String ERR_IN_1004 = "ERR_IN_1004"; // There is some issue in updating Invoice Information,
															// Please try again or contact to the administrator
	public static final String ERR_IN_1005 = "ERR_IN_1005"; // Duration to search the container result cannot be more
															// than 10 days
	public static final String ERR_IN_1006 = "ERR_IN_1006"; // Invalid Invoice No. , So please enter valid information.
	public static final String ERR_IN_1007 = "ERR_IN_1007"; // Cannot print this report for SC invoice flag ‘N’
	public static final String ERR_IN_1008 = "ERR_IN_1008"; // Cannot create Haisen as Seq. No. for ETD and Haisen
															// Code-TB has reached 999
	public static final String ERR_IN_1009 = "ERR_IN_1009"; // There is some issue in updating Part Price Information,
															// Please try again or contact to application support team.
	public static final String ERR_IN_1010 = "ERR_IN_1010"; // Part Name cannot be Tilda(~)
	public static final String ERR_IN_1011 = "ERR_IN_1011"; // Price should be greater than Zero.
	public static final String ERR_IN_1012 = "ERR_IN_1012"; // Usage should be greater than Zero.
	public static final String ERR_IN_1013 = "ERR_IN_1013"; // Please select Invoice No. to search the result.

	public static final String ERR_IN_1015 = "ERR_IN_1015"; // Same Request already submitted and yet not completed. So
															// cannot submit again
	public static final String ERR_IN_1016 = "ERR_IN_1016"; // ETD To cannot be less than ETD From.
	public static final String ERR_IN_1017 = "ERR_IN_1017"; // Duration to search the container result cannot be more
															// than 10 days.
	public static final String ERR_IN_1018 = "ERR_IN_1018"; // ETD Date cannot be less than Invoice Date
	public static final String ERR_IN_1019 = "ERR_IN_1019"; // Port of Loading and Port of Discharge cannot be same
	public static final String ERR_IN_1020 = "ERR_IN_1020"; // Space not allowed in Ocean Voyage
	public static final String ERR_IN_1021 = "ERR_IN_1021"; // ETD date should not be greater than ETA date
	public static final String ERR_IN_1024 = "ERR_IN_1024"; // Van Date From {vanDateFrom} is a past date,so cannot be
															// deleted.
	public static final String ERR_IN_1030 = "ERR_IN_1030"; // There is some issue in deleting the Information, please
															// try again or contact to the Administrator.
	public static final String ERR_IN_1031 = "ERR_IN_1031"; // Booking No. already exists in Vessel Booking master for
															// the respective Renban code in selected row.

	public static final String ERR_IN_1036 = "ERR_IN_1036"; // Van Date From cannot be greater than Van Date To.
	public static final String ERR_IN_1037 = "ERR_IN_1037"; // Van Date From and Van Date To cannot be past date for new
															// records.
	public static final String ERR_IN_1038 = "ERR_IN_1038"; // Van Date From/To cannot overlap with existing record
															// {Re-Export Code: "Re-ExportCode"}.
	public static final String ERR_IN_1039 = "ERR_IN_1039"; // {Priority [N]} must have at least two privileges selected
	public static final String ERR_IN_1040 = "ERR_IN_1040"; // {Priority [N]} , Priority [N1]} -Duplication of selected
															// privileges flag is not allowed.

	public static final String WARN_IN_1040 = "WARN_IN_1040"; // {Priority [N]} More than 2 privilege types are
																// selected, Do you wish to Save changes?
	public static final String WARN_IN_1041 = "WARN_IN_1041"; // <Priority [N]> Mixed for [Privilege Name[1]] and
																// [Privilege Name [2]] and …, Do you wish to Save
																// changes?

	public static final String ERR_IN_1045 = "ERR_IN_1045"; // Cannot delete selected record, please try again or
															// contact to the Administrator.

	public static final String ERR_IN_1046 = "ERR_IN_1046"; // Effective From Month and Effective To Month cannot
															// overlap under same Car Family Code :
															// <carFamilyCode>,Importer Code : <importerCode>,& Part No.
															// :<partNo>
	public static final String ERR_IN_1047 = "ERR_IN_1047"; // Effective From Month cannot be past month.
	public static final String ERR_IN_1048 = "ERR_IN_1048"; // Effective To Month cannot be past month.
	public static final String ERR_IN_1049 = "ERR_IN_1049"; // Part No : <partNo> does not exists in Part master.
	public static final String ERR_IN_1050 = "ERR_IN_1050"; // Part No : <partNo> Part price can not be less then 0.
	public static final String ERR_IN_1051 = "ERR_IN_1051"; // Van Dates From : <Van Date From> and Van Date To : <Van
															// Date To> already exists in existing records.
	public static final String ERR_IN_1052 = "ERR_IN_1052"; // Please select at least 2 values to mix CFC.
	public static final String ERR_IN_1053 = "ERR_IN_1053"; // Please select at least 2 values to mix Exporter Code.
	public static final String ERR_IN_1054 = "ERR_IN_1054"; // Please select at least 2 values to mix Re-Exp Code.
	public static final String ERR_IN_1055 = "ERR_IN_1055"; // Please select at least 2 values to mix Line Code.
	public static final String ERR_IN_1056 = "ERR_IN_1056"; // Part No. or Part Name or Part Type not entered.

	public static final String ERR_IN_1120 = "ERR_IN_1120"; // Rev. Part Net Weight and Rev. Box Weight cannot be 0 or
															// blank.
	public static final String ERR_IN_1121 = "ERR_IN_1121"; // Box weight can't be less than sum of part weight.
	public static final String ERR_IN_1122 = "ERR_IN_1122"; // Privilege Part <partNo>, exists in Country Origin Master.

	public static final String INFO_IN_1001 = "INFO_IN_1001"; // Work Plan Master has been updated successfully
	public static final String INFO_IN_1002 = "INFO_IN_1002"; // Invoice Information has been updated successfully
	public static final String INFO_IN_1003 = "INFO_IN_1003"; // Information has been updated successfully.
	public static final String INFO_IN_1004 = "INFO_IN_1004"; // It will be Offline Download, So please check the File
																// in “On Demand Download Report”
	public static final String INFO_IN_1005 = "INFO_IN_1005"; // Selected record deleted successfully.

	public static final String INFO_IN_1006 = "INFO_IN_1006"; // Invoice has been updated successfully. Updated invoice
																// will be sent to IXOS automatically & please resend
																// invoice to PLS
	public static final String INFO_IN_1007 = "INFO_IN_1007"; //Privilege has been updated successfully, please generate invoice separation if needed.


	public static final String ETD_FROM = "P_I_V_ETD_FROM";
	public static final String ETD_TO = "P_I_V_ETD_TO";
	public static final String BOOKING_NO = "P_I_V_BOOK_NO";
	public static final String COUNTRY_CODE = "P_I_V_COUNTRY_CD";
	public static final String USER_ID = "P_I_V_USER_ID";
	public static final String SIZE_PAGE_TO_CONTENT = "setSizePageToContent";
	public static final String FORCE_LINE_BREAK_POLICY = "setForceLineBreakPolicy";
	public static final String REPORT_DIRECTORY = "reportDirectory";
	public static final String INCVOICE_GENERATION_REPORT_DIRECTORY = "invoiceGeneration.report.directory";
	public static final String REPORT_FORMAT = "reportFormat";
	public static final String INVOICE_GENERATION_REPORT_FORMAT = "invoiceGeneration.report.format";
	public static final String REPORT_SIZE_LIMIT = "reportSizeLimit";
	public static final String INVOICE_GENERATION_REPORT_SIZE_LIMIT = "invoiceGeneration.report.size.limit";
	public static final String STORE_DB = "storeInDB";
	public static final String LOGIN_USER_ID = "loginUserId";
	public static final String TEST_USER = "TestUser";
	public static final String RINS104_DG = "RINS104_DG";
	public static final String RINS105 = "RINS105";
	public static final String RINS106 = "RINS106";
	public static final String FONT_SIZE_FIX_ENABLED = "setFontSizeFixEnabled";
	public static final String RINS005 = "RINS005";
	public static final String RINS001A = "RINS001A";
	public static final String RINS002 = "RINS002";
	public static final String RINS003B = "RINS003B";
	public static final String RINS003D = "RINS003D";
	public static final String INVOICE_NO = "P_I_V_INVOICE_NO";
	public static final String RINS002DG = "RINS002DG";
	public static final String HAISEN_NO_ALREADY_EXIST = "Haisen no already exist.";
	public static final String HAISEN_NO_GENERATED = "Haisen no generated successfully: ";
	public static final String DEFAULT_DATE_FORMATE = "dd/MM/yyyy";
	public static final String COMPANYNAME = "TMT";
	public static final String DEFAULT_DATABASE_DATE_FORMATE = "yyyy-MM-dd";

	public static final String SPACE = " ";
	public static final String YEAR_MONTH_INPUT = "yyyy/MM";
	public static final String YEAR_MONTH_OUTPUT = "yyyyMM";
	public static final String RINS003A = "RINS003A";

	public static final String ONE = "1";
	public static final String TWO = "2";
	public static final String THREE = "3";
	public static final String FOUR = "4";
	public static final String FIVE = "5";
	public static final String RINS002A = "RINS002A";
	public static final String FILEPATH = "filePath";
	public static final String REPORT_TYPEID = "reportTypeId";
	public static final String REPORT_TYPES_JSON_FOR_INQUIRYSCREEN = "reportTypesJsonForInquiryScreen";
	public static final String REPORT_TYPES_JSON_FOR_PACKINGVANNING = "classpath:reportTypesOfPackingAndVanningScreen.json";
	public static final String FILENAME = "fileName";
	public static final String OUTSTREAM = "outStream";
	public static final String CONTENTDISPOSITION = "Content-Disposition";
	public static final String ATTACHMENT = "attachment; filename= ";

	public static final String DATEFORMAT = "dd/MM/yyyy";
	public static final String NEWDATEFORMAT = "ddMMyyyy";
	public static final String CARFMLYDESTMST_DOWNLOAD_DATE_FORMAT = "ddMMyyyyHHmmss";

	public static final String RINS001 = "RINS001";
	public static final String RINS0011B = "RINS0011B";
	public static final String GRAND_TOTAL = "Grand total";
	public static final String PART_MASTER = "Part_Master";
	public static final String CAR_FMLY_DEST_HEADER_COLOR_CODE = "DDEBF7";
	public static final String CAR_FMLY_DEST_HEADER_BORDER_COLOR_CODE = "000000";
	public static final String ARIAL = "Arial";
	public static final String CAR_FMLY_DEST_GRID_RIGHT_COLOR_CODE = "C8DAFB";
	public static final String CAR_FMLY_DEST_GRID_BOTTOM_COLOR_CODE = "DADADA";
	public static final String PDF = "pdf";
	public static final String XLSX = "xlsx";

	public static final String PRIVILEGETYPE_NON = "NON";
	public static final String CONFIRMED = "true";
	public static final String NOT_CONFIRMED = "false";
	public static final String BATCH_JOB_SIZE = "batchJobsize";
	public static final String REEXPORTERCODE_ALL = "ALL";

	public static final String FLAG = "N";
	public static final String DECIMAL_FORMATE = "#,##0.00";
	public static final String FORMATFREIGHTINSURANCE = "#,###,###,##0.00";

	public static final String PART_NO_NOT_EXIST = "Part No. does not exists";
	public static final String HYPHEN = "-";
	public static final String LATEST = "Latest";
	public static final String ALL = "All";
	public static final String FLAG_N = "N";
	public static final String FLAG_Y = "Y";
	public static final String ETD = "ETD";
	public static final String PCKMNTH = "PCKMNTH";
	public static final String PCK_MNTH = "Packing Month";
	public static final String ERROR_CODE = "errorCode";
	public static final String COUNT = "count";
	public static final String KEY_COLUMNS = "keyColumns";
	public static final String ERROR_FIELD = "ERROR_FIELD";

	public static final String EXCEPTION = "Exception: ";

	// Container Level Report
	public static final String RINS110 = "RINS110";
	public static final String CALIBRIFONT = "Calibri";
	public static final String POIDATEFORMATEFORCELL = "DD/MM/YYYY";
	public static final String POIDATEFORMATE = "DD/MM/YYYY";
	public static final String IMPORTER_CODE = "Importer Code";
	public static final String EXPORTER_CODE = "Exporter Code";
	public static final String MSP_ORDER_TYPE = "MSP Order Type";
	public static final String PACKING_MONTH = "Packing Month";
	public static final String CFC = "CFC";
	public static final String RE_EXPORTER_CODE = "Re-exporter Code";
	public static final String INVOICE_PRIVILEGE = "Invoice Privilege";
	public static final String CPO_SPO_RUNNING_NO = "CPO SPO Running No.";
	public static final String VANNING_MONTH = "Vanning Month";
	public static final String VANNING_PLANT = "Vanning Plant";
	public static final String VANNING_DATE = "Vanning Date";
	public static final String ETD_DATE = "ETD Date";
	public static final String ETA_DATE = "ETA Date";
	public static final String HAISEN_NO = "Haisen No.";
	public static final String INVOICE = "Invoice No.";
	public static final String INVOICE_DATE = "Invoice Date";
	public static final String AMOUNT_FOB_INVOICE = "Amount FOB(Invoice)";
	public static final String ORDER_TYPE = "Order Type";
	public static final String PACKING_PATTERN = "Packing Pattern";
	public static final String RENBAN_NO = "Renban No.";
	public static final String CONTAINER_SIZE = "Container Size";
	public static final String ISO_CONTAINER_NO = "ISO Container No.";
	public static final String SEAL_NO = "Seal No.";
	public static final String QTY_MODULE_TOTAL = "Qty Module(Total)";
	public static final String QTY_MODULE_ROUNDUSE = "Qty Module(RoundUse)";
	public static final String PART_BOX_MAT = "PART BOX MAT";
	public static final String NET_WEIGHT = "Net Weight";
	public static final String GROSS_WEIGHT = "Gross Weight";
	public static final String M3 = "M3";
	public static final String CONTAINER_FOB = "Container FOB";
	public static final String INVOICE_STATUS = "Invoice Status";
	public static final String DG_CONTAINER = "DG Container";
	public static final String BOOKING = "Booking No.";
	public static final String SIMPLEDATEFORMATINPUT = "dd/MM/yyyy";
	public static final String SIMPLEDATEFORMATOUT = "yyyy-MM-dd";
	public static final String SQL_DATE_FORMAT = "dd-MM-yyyy";
	public static final String POI_VAN_MONTH_FORMATE = "YYYYMM";
	public static final String SQL_VAN_MONTH = "yyyy-MM";
	public static final String INVOICE_CONTAINER_LEVEL_FILENAME = "Invoice_Container_Level";
	public static final String DEFAULT_DATABASE_DATE_FORMATE_FOR_POI = "yyyy-MM-dd";
	public static final String ARIALFONT = "Arial";
	public static final String INVOICE_DATA_DOWNLOAD = "Invoice data download";
	public static final String FILE_STORE_ADD = "Invoice_Container_Level.xlsx";
	public static final String SLASH = "\\";
	public static final String RINS012 = "RINS012";
	public static final Object RINS007 = "RINS007";

	public static final String INV_NO = "INV_NO";
	public static final String MOD_NO = "MOD_NO";
	public static final String LOT_NO = "LOT_NO";
	public static final String PARTNO = "PART_NO";
	public static final String BOX_NO = "BOX_NO";
	public static final String PART_PRICE = "PART_PRICE";
	public static final String PARTNAME = "PART_NAME";

	public static final String CMP_CODE = "cmpCode";
	public static final String PART_NO = "partNo";
	public static final String PART_NAME = "partName";
	public static final String PART_TYPE = "type";
	public static final String PART_INHOUSE_SHOP = "inhouseShop";
	public static final String PART_WEIGHT = "weight";
	public static final String PART_UPD_DT = "updateDate";
	public static final String PART_UPD_BY = "updateBy";
	public static final String PART_BATCH_UPD_DT = "batchUpdateDate";
	public static final String INVOICE_CONTAINER_LEVEL_EXTENSION = ".xlsx";

	public static final String IN_HOUSE_HIDDEN_FILE = "inHousehidden";

	public static final String COMPANY_CODE = "companyCode";

	public static final String INVOICE_TYPE = "invoiceType";
	public static final String DEST_CODE = "destCode";
	public static final String ETD_INVOICE_DATE = "etdDate";
	
	public static final String DEFAULT_DATE_FORMAT_FOR_RANBAN = "MM/dd/yyyy";
}