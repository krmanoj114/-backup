package com.tpex.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConstantUtils {

	//Common Messages(CM) Info
	public static final String INFO_CM_3001 = "INFO_CM_3001"; //No data found.
	public static final String INFO_CM_3002 = "INFO_CM_3002"; //{count} record deleted successfully.
	public static final String INFO_CM_3003 = "INFO_CM_3003"; //Information saved successfully.
	public static final String INFO_CM_3004 = "INFO_CM_3004"; //File Uploaded Successfully.
	public static final String INFO_CM_3005 = "INFO_CM_3005"; //File uploaded successfully with warning, please check warning/error
	public static final String INFO_CM_3006 = "INFO_CM_3006"; //Record Deleted Successfully.
	public static final String INFO_CM_3007 = "INFO_CM_3007"; //Record Updated Successfully.
	public static final String INFO_CM_3008 = "INFO_CM_3008"; //No changes to save.

	

	public static final String BATCH_INVOICE_NO = "invoiceNo";
	public static final String BATCH_INVOICE_USER_ID = "userId";
	
	//Common Messages Status
	public static final String INFO_CM_3010 = "INFO_CM_3010"; //Error
	public static final String INFO_CM_3011 = "INFO_CM_3011"; //Success
	public static final String INFO_CM_3012 = "INFO_CM_3012"; //Processing
	public static final String INFO_CM_3013 = "INFO_CM_3013"; //Pending

	//Common Messages(CM) Error
	public static final String ERR_CM_3001 = "ERR_CM_3001"; //Please input mandatory information.
	public static final String ERR_CM_3002 = "ERR_CM_3002"; //Effective From Date should not be greater than Effective To Date.
	public static final String ERR_CM_3003 = "ERR_CM_3003"; //Please select a record to copy.
	public static final String ERR_CM_3004 = "ERR_CM_3004"; //Please select record to delete.
	public static final String ERR_CM_3005 = "ERR_CM_3005"; //{errorCode} – Cannot delete selected record, please try again or contact to the Administrator.
	public static final String ERR_CM_3006 = "ERR_CM_3006"; //{errorCode} – There is some issue in updating the Information, please try again or contact to the Administrator.
	public static final String ERR_CM_3007 = "ERR_CM_3007"; //ETD From should not be greater than ETD To.
	public static final String ERR_CM_3008 = "ERR_CM_3008"; //Duplicate record found for <keyColumns>
	public static final String ERR_CM_3009 = "ERR_CM_3009"; //Uploaded file template is not correct.
	public static final String ERR_CM_3010 = "ERR_CM_3010"; //Destination code is over the max limit.
	public static final String ERR_CM_3011 = "ERR_CM_3011"; //Destination does not exists in Final Destination Master.
	public static final String ERR_CM_3013 = "ERR_CM_3013"; //Effective Date From cannot be blank.
	public static final String ERR_CM_3014 = "ERR_CM_3014"; //Effective Date format is Invalid, Follow DD/MM/YYYY"
	public static final String ERR_CM_3015 = "ERR_CM_3015"; //Effective From Date must be greater than Effective To Date of last record
	public static final String ERR_CM_3016 = "ERR_CM_3016"; //Effective From Date must be in continuation with Effective To Date of last record
	public static final String ERR_CM_3017 = "ERR_CM_3017"; //{filename} not configured in database.
	public static final String ERR_CM_3018 = "ERR_CM_3018"; //File not exist in path = <filePath>
	public static final String ERR_CM_3019 = "ERR_CM_3019"; //From Date & Time should not be greater than To Date & Time.
	public static final String ERR_CM_3020 = "ERR_CM_3020"; //{errorCode} There is some issue in downloading the Information, please try again or contact to the Administrator.

	public static final String ERR_MN_4001 = "ERR_MN_4001"; //Please input YYYY/MM format.

	//Common Messages(CM) Warning
	public static final String WRN_CM_3001 = "WRN_CM_3001"; //It will delete {count} record. Do you wish to delete the selected record?

	public static final String BATCH_ID_NATCALUPLOAD = "NATCALUPLOAD";

	public static final String SCM_SES_DESC = "Haisen Running Number for ";

	//Invoice 1001
	public static final String ERR_IN_1001 = "ERR_IN_1001"; //Please check either regular or CPO/SPO
	public static final String ERR_IN_1004 = "ERR_IN_1004"; //There is some issue in updating Invoice Information, Please try again or contact to the administrator
	public static final String ERR_IN_1005 = "ERR_IN_1005"; //Duration to search the container result cannot be more than 10 days
	public static final String ERR_IN_1006 = "ERR_IN_1006"; //Invalid Invoice No. , So please enter valid information.
	public static final String ERR_IN_1007 = "ERR_IN_1007"; //Cannot print this report for SC invoice flag ‘N’
	public static final String ERR_IN_1008 = "ERR_IN_1008"; //Cannot create Haisen as Seq. No. for ETD and Haisen Code-TB has reached 999
	public static final String ERR_IN_1009 = "ERR_IN_1009"; //There is some issue in updating Part Price Information, Please try again or contact to application support team.
	public static final String ERR_IN_1010 = "ERR_IN_1010"; //Part Name cannot be Tilda(~)
	public static final String ERR_IN_1011 = "ERR_IN_1011"; //Price should be greater than Zero.
	public static final String ERR_IN_1012 = "ERR_IN_1012"; //Usage should be greater than Zero.
	public static final String ERR_IN_1013 = "ERR_IN_1013"; //Please select Invoice No. to search the result.

	public static final String ERR_IN_1015 = "ERR_IN_1015"; //Same Request already submitted and yet not completed. So cannot submit again
	public static final String ERR_IN_1016 = "ERR_IN_1016"; //ETD To cannot be less than ETD From.
	public static final String ERR_IN_1017 = "ERR_IN_1017"; //Duration to search the container result cannot be more than 10 days.
	public static final String ERR_IN_1018 = "ERR_IN_1018"; //ETD Date cannot be less than Invoice Date
	public static final String ERR_IN_1019 = "ERR_IN_1019"; //Port of Loading and Port of Discharge cannot be same
	public static final String ERR_IN_1020 = "ERR_IN_1020"; //Space not allowed in Ocean Voyage
	public static final String ERR_IN_1021 = "ERR_IN_1021"; //ETD date should not be greater than ETA date
	public static final String ERR_IN_1024 = "ERR_IN_1024"; //Van Date From {vanDateFrom} is a past date,so cannot be deleted.
	public static final String ERR_IN_1030 = "ERR_IN_1030"; //There is some issue in deleting the Information, please try again or contact to the Administrator.
	public static final String ERR_IN_1031 = "ERR_IN_1031"; //Booking No. already exists in Vessel Booking master for the respective Renban code in selected row.

	//Batch error messages
	public static final String ERR_IN_1040 = "Uploaded file template is not correct.";
	public static final String ERR_IN_1041 = "Currency Code cannot be blank.";
	public static final String ERR_IN_1042 = "Currency Code does not exist In Currency Master.";
	public static final String ERR_IN_1043 = "Currency Code should be same under same Importer code and car family code. <%s>";
	public static final String ERR_IN_1044 = "Lot Code cannot be blank.";
	public static final String ERR_IN_1045 = "Car Family Code cannot be blank.";
	public static final String ERR_IN_1046 = "Car Family Code does not exist in Car Family Master.";
	public static final String ERR_IN_1047 = "Importer Code cannot be blank.";
	public static final String ERR_IN_1048 = "Importer Code does not exist in Final Destination Master.";
	public static final String ERR_IN_1049 = "Part No. cannot be blank.";
	public static final String ERR_IN_1050 = "Part No. does not exist in Part Master";
	public static final String ERR_IN_1051 = "Part Name cannot be blank.";
	public static final String ERR_IN_1052 = "Part name different with Part master -[%s]";
	public static final String ERR_IN_1053 = "Price cannot be blank.";
	public static final String ERR_IN_1054 = "Price should be greater than zero.";
	public static final String ERR_IN_1055 = "Usage cannot be blank.";
	public static final String ERR_IN_1056 = "Usage value should be greater than zero.";
	public static final String ERR_IN_1057 = "Lot Code does not exist in the Lot Size Master, Please contact to the Administrator.";
	public static final String ERR_IN_1058 = "Cannot find this lot code in Packing Specification Master.";
	public static final String ERR_IN_1059 = "Cannot find this part no. in Packing Specification Master.";
	public static final String ERR_IN_1060 = "Part no. [%s] of packing spec doesn’t exist in upload file under [%s]";
	public static final String ERR_IN_1061 = "Lot usage in EXCEL file is not equal to Lot usage in Packing Spec &Lot Size Master";
	public static final String ERR_IN_1062 = "Lot size not available in Lot Size Master. Base on Key <%s>";
	public static final String ERR_IN_1063 = "Duplicate record found for %s";
	public static final String ERR_IN_1064 = "Destination code cannot be blank.";
	public static final String ERR_IN_1065 = "Re Exp. Code cannot be blank.";
	public static final String ERR_IN_1066 = "Re Exp. Code cannot be more than 1 characters.";
	public static final String ERR_IN_1067 = "Series Name cannot be blank";
	public static final String ERR_IN_1068 = "Series Name cannot be more than 15 characters.";
	public static final String ERR_IN_1069 = "Car family code doesn’t exist in Car Family Master.";
	public static final String ERR_IN_1070 = "Destination code doesn’t exist in Final Destination Master.";


	//Upload Work Plan Master Batch error messages
	public static final String ERR_IN_1090 = "Issue Invoice Date cannot be blank for Regular order";
	public static final String ERR_IN_1091 = "Issue Invoice Date format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1092 = "Issue Invoice date should not be over than Actual ETD date (ETD1) and should not be past date.";
	public static final String ERR_IN_1093 = "ETD1 cannot be blank.";
	public static final String ERR_IN_1094 = "ETD1 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1095 = "ETD1 must be less than ETA1";
	public static final String ERR_IN_1096 = "ETA1 cannot be blank.";
	public static final String ERR_IN_1097 = "ETA1 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1098 = "Customer Broker does not exist in Custom Broker Master.";
	public static final String ERR_IN_1099 = "Vessel1 no. of digit is over the max limit.";
	public static final String ERR_IN_1100 = "Voy1 no. of digit is over the max limit.";
	public static final String ERR_IN_1101 = "ETD2 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1102 = "ETD2 must be less than ETA2 ";
	public static final String ERR_IN_1103 = "ETA2 cannot be blank.";
	public static final String ERR_IN_1104 = "ETA2 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1105 = "Vessel2 no. of digit is over the max limit.";
	public static final String ERR_IN_1106 = "Voy2 no. of digit is over the max limit.";
	public static final String ERR_IN_1107 = "ETD3 cannot be blank.";
	public static final String ERR_IN_1108 = "ETD3 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1109 = "ETD3 must be less than ETA3 ";
	public static final String ERR_IN_1110 = "ETA3 cannot be blank.";
	public static final String ERR_IN_1111 = "ETA3 format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1112 = "Vessel3 no. of digit is over the max limit.";
	public static final String ERR_IN_1113 = "Voy3 no. of digit is over the max limit.";
	public static final String ERR_IN_1114 = "Folder Name (Inv Container Level) no. of digit is over the max limit.";
	public static final String ERR_IN_1115 = "Port of Loading does not exist in Port Master.";
	public static final String ERR_IN_1116 = "Port of Discharge does not exist in Port Master.";
	public static final String ERR_IN_1117 = "ETD2 cannot be blank.";

	//Upload Work Plan Master Batch warning messages
	public static final String WRN_IN_1118 = "This key (Original ETD, Container Destination, and Renban code) record does not exist in Work plan master, so cannot be updated.";
	public static final String WRN_IN_1119 = "Invoice already generated for this record , so cannot be updated.";


	public static final String INFO_IN_1001 = "INFO_IN_1001"; //Work Plan Master has been updated successfully
	public static final String INFO_IN_1002 = "INFO_IN_1002"; //Invoice Information has been updated successfully
	public static final String INFO_IN_1003 = "INFO_IN_1003"; //Information has been updated successfully.
	public static final String INFO_IN_1004 = "INFO_IN_1004"; //It will be Offline Download, So please check the File in “On Demand Download Report”
	public static final String INFO_IN_1005 = "INFO_IN_1005"; //Selected record deleted successfully.

	public static final String ETD_FROM="P_I_V_ETD_FROM";
	public static final String ETD_TO="P_I_V_ETD_TO";
	public static final String BOOKING_NO="P_I_V_BOOK_NO";
	public static final String COUNTRY_CODE="P_I_V_COUNTRY_CD";
	public static final String USER_ID="P_I_V_USER_ID";
	public static final String SIZE_PAGE_TO_CONTENT="setSizePageToContent";
	public static final String FORCE_LINE_BREAK_POLICY="setForceLineBreakPolicy";
	public static final String REPORT_DIRECTORY="reportDirectory";
	public static final String INCVOICE_GENERATION_REPORT_DIRECTORY="invoiceGeneration.report.directory";
	public static final String REPORT_FORMAT="reportFormat";
	public static final String INVOICE_GENERATION_REPORT_FORMAT="invoiceGeneration.report.format";
	public static final String REPORT_SIZE_LIMIT="reportSizeLimit";
	public static final String INVOICE_GENERATION_REPORT_SIZE_LIMIT="invoiceGeneration.report.size.limit";
	public static final String STORE_DB="storeInDB";
	public static final String LOGIN_USER_ID="loginUserId";
	public static final String TEST_USER="TestUser";
	public static final String RINS104_DG="RINS104_DG";
	public static final String RINS105="RINS105";
	public static final String RINS106="RINS106";
	public static final String FONT_SIZE_FIX_ENABLED="setFontSizeFixEnabled";
	public static final String RINS005="RINS005";
	public static final String RINS001A="RINS001A";
	public static final String RINS002="RINS002";
	public static final String RINS003B="RINS003B";
	public static final String RINS003D="RINS003D";
	public static final String INVOICE_NO = "P_I_V_INVOICE_NO";
	public static final String RINS002DG="RINS002DG";
	public static final String HAISEN_NO_ALREADY_EXIST = "Haisen no already exist.";
	public static final String HAISEN_NO_GENERATED = "Haisen no generated successfully: ";
	public static final String DEFAULT_DATE_FORMATE = "dd/MM/yyyy";
	public static final String DATE_FORMAT = "dd/MM/uuuu";
	public static final String DEFAULT_DATABASE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String COMPANYNAME = "TMT";

	public static final String SPACE = " ";
	public static final String YEAR_MONTH_INPUT ="yyyy/MM";
	public static final String YEAR_MONTH_OUTPUT ="yyyyMM";
	public static final String RINS003A = "RINS003A";

	public static final String ONE = "1";
	public static final String TWO = "2";
	public static final String THREE = "3";
	public static final String FOUR = "4";
	public static final String FIVE = "5";
	public static final String RINS002A = "RINS002A";
	public static final String FILEPATH ="filePath";
	public static final String REPORT_TYPEID="reportTypeId";
	public static final String REPORT_TYPES_JSON_FOR_INQUIRYSCREEN="reportTypesJsonForInquiryScreen";
	public static final String FILENAME="fileName";
	public static final String OUTSTREAM="outStream";
	public static final String CONTENTDISPOSITION="Content-Disposition";
	public static final String ATTACHMENT= "attachment; filename= ";
	public static final String RINS001 = "RINS001";

	public static final String PDF = "pdf";
	public static final String XLSX = "xlsx";

	//Process Log Details
	public static final String PL_STATUS_INFO = "Info";
	public static final String PL_STATUS_ERROR = "Error";
	public static final String PL_STATUS_WARNING = "Warning";
	public static final String PL_JB_STRT_MSG = "Job execution started";
	public static final String PL_JB_CMP_MSG = "Job execution completed";
	public static final String PL_JB_STOP_MSG = "Stopped job due to some error";
	public static final String PL_JB_SUMMARY_MSG = "Summary - Total records / Success / Error";
	public static final String PL_JB_PROC_ROW_MSG = "Processing row ";
	public static final String PL_JB_FILE_FND_MSG = "File found";
	public static final String PL_JB_READ_FILE_MSG = "Reading file";

	public static final String INVOICE_ALREADY_GENERATED = "Y";

	//Upload Country Code Origin Batch error messages
	public static final String ERR_IN_1080 = "Part No. cannot be blank.";
	public static final String ERR_IN_1081 = "Part does not exist in Part Master.";
	public static final String ERR_IN_1082 = "Country of Origin Code cannot be blank.";
	public static final String ERR_IN_1083 = "Country of Origin Code does not exist in Country Code master.";
	public static final String ERR_IN_1084 = "Van Date From cannot be blank.";
	public static final String ERR_IN_1085 = "Van Date To cannot be blank.";
	public static final String ERR_IN_1086 = "Van Date From cannot  be past date";
	public static final String ERR_IN_1087 = "Van Date To cannot not be past date";
	public static final String ERR_IN_1088 = "Van Date From format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1089 = "Van Date To format is Invalid, Follow DD/MM/YYYY";
	public static final String ERR_IN_1072 = "Van Date From cannot be greater than Van Date To";
	public static final String ERR_IN_1071 = "Duplicate record found for <Part No., Van Date From & Van Date To>";
	public static final String ERR_IN_1073 = "Duplicate record found";
	public static final String ERR_IN_1074 ="Van Date From and Van Date To overlapping is not allowed under same Part No.";

	public static final String IS_ERROR_FLAG = "isError";
	public static final String IS_WARNING_FLAG = "isWarning";

	public static final String YEAR_SLASH_MONTH_PATTERN = "^\\d{4}\\/\\d{2}$";

	public static final String BATCH_SUCCESS_MSG = "Batch initiated successfully.";
	public static final String BATCH_UPD_WPM_ERR_MSG = "Error in Updating Work Plan Master";
	public static final String UPD_BY_SCHEDULE = "TPEX";

	public static final int BATCH_STATUS_SUCCESS = 1;
	public static final int BATCH_STATUS_PROCESS = 2;
	public static final int BATCH_STATUS_ERROR = 0;
	public static final String TMT = "TMT";
	public static final String STM = "STM";
	public static final String KTL = "KTL";
	public static final String NYK = "NYK";
	public static final String MOL = "MOL";
	public static final String BINF023NAME = "Send Monthly Container Info to Broker";
	public static final String BINF023 = "BINF023";

	public static class Binf005 {
		private Binf005() {
		}
		public static final String GATEWAY_TAG = "##";
		public static final String HEAD  = "H ";
		public static final String FOOT  = "T ";
		public static final String H1 = "H1";
		public static final String D1 = "D1";
		public static final String T1 = "T1";
		public static final String H2 = "H2";
		public static final String D2 = "D2";
		public static final String T2 = "T2";
		public static final String H3 = "H3";
		public static final String D3 = "D3";
		public static final String T3 = "T3";
		public static final String H4 = "H4";
		public static final String D4 = "D4";
		public static final String T4 = "T4";
	}


	public static class Bins104 {
		private Bins104(){			
		}
		public static final String HEAD = "#H";
		public static final String FOOT = "#T";
		public static final String DATA = "D";
	}

	public static class Binf009 {
		private Binf009() {
		}
		public static final String GATEWAY_TAG = "##";
		public static final String HEAD  = "H ";
		public static final String FOOT  = "T ";
		public static final String DATA  = "D ";
	}

	//Pxp price master upload
	public static final String ERR_IN_1130 = "CFC does not exist in Car Family Master.";
	public static final String ERR_IN_1131 = "Part Name length is over limit.";
	public static final String ERR_IN_1132 = "FOB1 Amt cannot be blank.";
	public static final String ERR_IN_1133 = "FOB1 Amt should be in number.";
	public static final String ERR_IN_1134 = "Duplicate Record found under same Currency, CFC, IMP Code & Part (System utilized 1st record)";
	public static final String ERR_IN_1135 = "Part number :[%s] found in packing spec master but not found in this file.";
	public static final String ERR_IN_1136 = "Effective month from is earlier than existing record in Part Price Master.";
	public static final String ERR_IN_1137 = "Not found this Part in Packing Spec Master";

	public static final String BINS026 = "BINS026";
	public static final String BINF005 = "BINF005";
	public static final String BINS104 = "BINS104";
	public static final String ERR_TO_READ_MSG = "Error in Issue Invoice Date Calculation / Error in Getting the Folder from Renban Group Code Master";

	public static final String JOB_P_PROCESS_CTRL_ID = "processControlId";
	public static final String JOB_P_BATCH_ID = "batchId";
	public static final String JOB_P_BATCH_NAME = "batchName";
	public static final String JOB_P_USER_ID = "userId";
	public static final String JOB_P_START_AT = "startAt";
	public static final String JOB_P_SYSTEM_NM = "systemName";

	public static final String INVALID_TAG = "Invalid tag %s.";
	public static final String INVALID_VDS_LEN = "Invalid VDS length.";
	public static final String INVALID_PRIVILEGE_FLAG = "Invalid Privilege flag.";
	public static final String INCORRECT_OPR_EXP_COMP = "Operating Exporter Company Code incorrect.";
	public static final String INCORRECT_VAN_EXP_COMP = "Vanning Exporter Company Code incorrect.";
	public static final String X_DOC_CODE = "X_DOC_CODE";
	public static final String ISO_CONT_NOT_FOUND = "ISO container No. not found in ISO container master. [Container destination=%s, Container serial no.=%s, ISO container no.=%s]";
	public static final String ISO_CONT_NOT_SAME = "ISO No. from GPAC not same as ISO No. Master. [Container destination=%s, Container serial no.=%s, ISO container no.=%s]";
	public static final String INVALID_LOT_PATTERN = "Invalid Lot Pattern Value.";
	public static final String PART_SNM_NOT_EXIST = "Part Series name doesn’t exist in Importer Car Family Destination Master.";
	public static final String INCORRECT_MAINT_FLG = "Header MAINTENENCE FLAG incorrect.";
	public static final String INCORRECT_FNM_HEADER = "File Name Header should be %s instead %s.";
	public static final String FILE_CANNOT_OPEN = "File cannot be opened.";
	public static final String FILE_IS_EMPTY = "File is empty.";

	public static final String PARTNO_NOT_EXIST = "Seq No : %s - Part No. : %s, does not exist in Part Master.";
	public static final String PARTNO_TYPE_IS_IMPORT = "Seq No : %s - Part No. :%s, type is Import Part No.";
	public static final String MANDATORY_COLUMNS_NOT_AVAIL = "Seq No : %s -  %s : Not Available";

	public static final String MANDATORY_COLUMNS_BINF023_NOT_AVAIL = " %s  Not Available";
	public static final String NO_DATA_TO_SEND_SAP = "No Data Found to send to SAP system";
	public static final String NO_DATA_TO_BROKER = "No data to send to Custom Broker for this month";
	public static final String BINS006 = "BINS006";
	public static final String FILE_NOT_DELETED = "File not deleted.";


	public static final String BINF009 = "BINF009";
	public static final String PART_MTRL_QTY = "Part Material Quantity ";
	public static final String INVALID_QTY = " invalid qty.";
	public static final String PKG_QTY_BOX_INVALID="Packing Quantity By Box Invalid qty.";
	public static final String BOX_GROSS_WT_INVALID_QTY="Box Gross Weight invalid qty.";
	public static final String BOX_M3_INVALID_QTY="Box M3  invalid qty.";
	public static final String BOX_MTRL_INVALID_QTY="Box Material Quantity  invalid qty.";

	public static final String EFFECT_FROM_EFFECTIVE_TO_DATE_INVALID = "Effective From Date cannot be greater than Effective To Date.";
	public static final String ATIGA = "3";
	public static final String HSCODE_BLANK = "HS Code cannot be blank.";
	public static final String ORIGIN_CRITERIA_BLANK = "Origin Criteria cannot be blank.";
	public static final String EFFECTIVE_FROM_TO_DATE_OVERLAP = "Effective From and To Date cannot be overlapped.";
	public static final String BINS104_HEADER_START_WITH = "#";
	public static final String BINS104_UPLOAD_MESSAGE = "Upload Privilege Master Data from IXOS and SCMS systems";

	public static final String SIX = "6";
	public static final String SEVEN = "7";
	public static final String EIGHT = "8";
	public static final String NINE = "9";

	//part Master Upload
	public static final String ERR_IN_1200 ="Mandatory Field Not Entered";
	public static final String ERR_IN_1201 = "Value cannot be Zero";
	public static final String ERR_IN_1202 = "Part No should be of 12 Characters";
	public static final String ERR_IN_1203 = "Part Name should be of 40 Characters";
	public static final String ERR_IN_1204 = "Please input inhouse shop for Inhouse part.";
	public static final String ERR_IN_1205 = "Inhouse Shop doesn’t exist in Inhouse Shop Master.";
	public static final String ERR_IN_1206 = "PartType value can be only 1,2,3,4.";
	public static final String ERR_IN_1207 = "Part No cannot be duplicated.";

	public static final String ERROR_REASON ="Error Reason";
	public static final String ZERO = "0";
	public static final String BATCHSTATUS_FAILURE = "FAILED";
	public static final String BATCHSTATUS_PROCESS = "PROCESS";
	public static final String SAVE = "save";
	public static final String COMPANY_CODE = "companyCode";
	
	public static final String  OEM="OEM";
	public static final String  IXO="IXO";
	public static final String DA90030="DA90030";
	public static final String RECORD_LEN_OF_DATA_00063="00063";
	public static final String FILE_PATH = "filePath";
	public static final String FAILED = "FAILED";
	public static final String ERROR = "ERROR";
	public static final String YYYYMMDDHHMMSS="yyyyMMddHHmmss";
	public static final String YYYYMMDDHHMISS = "YYYYMMDDHHMISS";
	public static final String IXOS="IXOS";
	public static final String BINS011="BINS011";

	public static class Binf011 {
		private Binf011() {
		}
		public static final String GATEWAY_TAG = "#";
		public static final String HEAD  = "H";
		public static final String FOOT  = "T";
		public static final String DATA  = "D";
		public static final String DATE_FORMAT="YYYYMMDDHHMISS";
	}
	
	public static final String INVALID_INVOICE_ATIGA = "HS Code or Origin Criteria is blank for ATIGA Part -invNo :%s.";
	public static final String INVALID_INVOICE_HSCODE = "Invoice Part HS Code and IXOS HS Code mismatch -invNo :%s.";
	public static final String INVALID_INVOICE_ORIGIN_CRITERIA = "Invoice Part Origin Criteria and IXOS Origin Criteria mismatch :invNo :%s.";
	public static final String INVALID_INVOICE_NET_WEIGHT = "Part with more than one net weight found in Invoice. Please Recalculate Invoice. :%s";

	public static class Binf301 {
		private Binf301(){			
		}
		public static final String HEAD = "H";
		public static final String FOOT = "#";
		public static final String DATA = "D";
	}
	public static final String BINF301 = "BINF301";
	public static final String ENG_PLT_CD = "ENG_PLT_CD";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String EFFECT_REC_NOT_FOUND = "Effective Engine Parts Not received from UHOST.";

}

