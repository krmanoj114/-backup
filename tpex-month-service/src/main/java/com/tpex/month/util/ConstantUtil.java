package com.tpex.month.util;

public class ConstantUtil {

	private ConstantUtil() {
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

	// Common Messages(CM) Warning
	public static final String WRN_CM_3001 = "WRN_CM_3001"; // It will delete {count} record. Do you wish to delete the
															// selected record?

	// Common Messages Status
	public static final String INFO_CM_3010 = "INFO_CM_3010"; // Error
	public static final String INFO_CM_3011 = "INFO_CM_3011"; // Success
	public static final String INFO_CM_3012 = "INFO_CM_3012"; // Processing
	public static final String INFO_CM_3013 = "INFO_CM_3013"; // Pending

	public static final String ERR_MN_4001 = "ERR_MN_4001"; // Please input YYYY/MM format.
	public static final String ERR_MN_4002 = "ERR_MN_4002"; // Booking no. already exists

	public static final String INFO_MN_4001 = "INFO_MN_4001"; // It will be Offline Download, So please check the File
																// in shared folder “Vessel Booking”

	public static final String YEAR_SLASH_MONTH_PATTERN = "^\\d{4}\\/\\d{2}$";
	public static final String BLANK = "";
	public static final String WHITESPACE = " ";
	public static final String SLASH = "/";
	public static final String YES = "Y";
	public static final String NO = "N";
	public static final String MSG = "message";
	public static final String STATUS = "status";
	public static final String OFFLINE = "offline";

	public static final String VESSEL_BOOK_PATH = "vesselbookingmaster.download.directory";
	public static final String VESSEL_BOOK_LIMIT = "vesselbookingmaster.download.limit";

	public static final String ARIAL_FONT = "Arial";

	public static final String ERR_CM_3021 = "ERR_CM_3021"; // Please select xlsx file to Upload.
	public static final String ERR_AD_2001 = "ERR_AD_2001"; // No Records in file to process
	public static final String EXCEL_FORMAT = ".xlsx";
	public static final String BATCH_ID_VESSEL_BOOKING_MST = "BINS0294";
	public static final String VESSEL_BOOKING_MASTER_UPLOAD = "Vessel Booking Master Upload";
	public static final String TPEX = "TPEX";
}
