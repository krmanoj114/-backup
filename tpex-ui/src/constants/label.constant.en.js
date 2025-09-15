export const LABEL_CONST = {
  // Common
  SELECT_MANDATORY_INFO: "Please input mandatory information.",
  UPDATE_MANDATORY_INFO: "Please input mandatory information.",
  SELECT_MANDATORY_PARA: "Please input mandatory parameter.",
  DOWNLOAD_REQUEST_FAILED: "Report download request submission Failed.",
  TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY:
    "Report Template Downloaded Successfully.",
  TEMPLATE_DOWNLOAD_REQUEST_FAILED:
    "Report Template Download Failed. Retry or Contact Admin.",
  SUBMITTED_SUCCESSFULLY_INFO: "Report Downloaded Successfully.",
  NO_DATA_FOUND: "No data found.",
  ETD_FROM: "ETD From",
  ETD_TO: "ETD To",
  SEARCH: "Search",
  UPDATE_WORK_PLAN_MASTER: "Update Work Plan Master",
  EXPORT_XLS: "Export to Excel",
  INFORMATION: "Information",
  ERROR: "Error",
  WARNING: "Warning",
  //code-message
  ERR_CM_3018: "File Not exist in path = {filePath}",
  ERR_CM_3001: "Please input mandatory information.",
  INFO_CM_3001: "No data found.",

  // Invoice & Shipping Report
  OTHER_GROUP_SELECTION_ERR:
    "Only Invoice DG Packing List, DG Declaration Report and Work Instruction Report can be selected together.",
  INVOICE_SHIPPING_REPORT: "Invoice & Shipping Report",
  ORDER_TYPE: "Order Type",
  DOWNLOAD_PDF: "Download PDF",
  CPO: "CPO",
  SPO: "SPO",
  INVOICE_NUMBER: "Invoice Number",
  DESTINATION: "Destination",
  REPORT_TYPE: "Report Type",
  PRINT: "Print",
  NO_PDF_REPORT: "PDF Report is not available for this Report Type.",
  ERR_IN_1006: "Invalid Invoice No. , So please enter valid information.",
  ERR_CM_3017: "{filename} not configured in database.",
  ERR_IN_1007: "Cannot print this report for SC invoice flag ‘N’",
  

  // Shipping container result
  ESTIMATE_COMP_INV: "Estimate/Compare Invoice",
  CALCULATE_ESTIMATE: "Calculate Estimate",
  CONT_DEST: "Container Destination",
  SHIP_CONT_RESULT: "Shipping Container Result",
  BOOKING_NO: "Booking No.",
  RENBAN_CODE: "Renban Code",
  DATE_LESS_THAN_ERROR: "ETD To cannot be less than ETD From.",
  ERR_IN_1016: "ETD To cannot be less than ETD From.",
  ERR_IN_1017:
    "Duration to search the container result cannot be more than 10 days.",

  // Invoice Maintenance keys
  INVOICE_MAINTENANCE: "Invoice Maintenance",
  REGULAR: "Regular",
  CPO_SPO: "CPO/SPO",
  INVOICE_NO: "Invoice No.",
  INVOICE_DATE: "Invoice Date",
  ETD: "ETD",
  ETA: "ETA",
  INVOICE_TYPE: "Invoice Type",
  SC_INVOICE: "SC Invoice",
  SC_AUTHORIZED: "SC Authorized",
  OCEAN_VESSEL: "Ocean Vessel",
  FEEDER_VESSEL: "Feeder Vessel",
  OCEAN_VOYAGE: "Ocean Voyage",
  FEEDER_VOYAGE: "Feeder Voyage",
  PRODUCT_GROUP: "Product Group",
  PORT_OF_LOADING: "Port of Loading",
  PORT_OF_DISCHARGE: "Port of Discharge",
  FREIGHT: "Freight",
  INSURANCE: "Insurance",
  CURRENCY_CODE: "Currency Code",
  PAYMENT_TERM: "Payment Term",
  SEARCH_MANDATORY_ERROR: "Please select Invoice No. to search the result.",
  SEARCH_MODIFY_WARN:
    "There is unsaved data on screen, do you want to continue with search?",
  SEARCH_DOWNLOAD_MODIFY_WARN:
    "There is unsaved data on screen, do you want to continue with download?",
  CODE: "Code",
  NAME: "Name",
  CUSTOMER_DETAILS: "Customer Details",
  NOTIFY_PARTY_DETAILS: "Notify Party Details",
  CONSIGNEE_DETAILS: "Consignee Details",
  SHIPPING_MARKS: "Shipping Marks",
  SHIPPING_MARK_1: "Shipping Mark 1",
  SHIPPING_MARK_2: "Shipping Mark 2",
  SHIPPING_MARK_3: "Shipping Mark 3",
  SHIPPING_MARK_4: "Shipping Mark 4",
  SHIPPING_MARK_5: "Shipping Mark 5",
  SHIPPING_MARK_6: "Shipping Mark 6",
  SHIPPING_MARK_7: "Shipping Mark 7",
  SHIPPING_MARK_8: "Shipping Mark 8",
  DESCRIPTION_OF_GOODS: "Description of Goods",
  DESCRIPTION_1: "Description 1",
  DESCRIPTION_2: "Description 2",
  DESCRIPTION_3: "Description 3",
  DESCRIPTION_4: "Description 4",
  DESCRIPTION_5: "Description 5",
  DESCRIPTION_6: "Description 6",
  ORDER_AND_CAR_FAMILY_DETAILS: "Order and Car Family Details",
  ORDER_NO: "Order No.",
  CAR_FAMILY: "Car Family",
  REVISION_NO: "Revision No.",
  SAVE_WITHOUT_SEARCH:
    "Please search the Invoice No. first to save the record.",
  NO_CHANGE_TO_SAVE: "No changes to save.",
  SAVE_CHANGE_CONFIRMATION: "Do you want to save the changes?",

  ERR_IN_1013: "Please select Invoice No. to search the result.",
  INFO_IN_1002: "Invoice Information has been updated successfully",
  ERR_IN_1004:
    "There is some issue in updating Invoice Information, Please try again or contact to the administrator",

  // ondemand download report
  ON_DEMAND_DOWNLOAD: "On Demand Download Report",
  REQUEST_DATE: "Request Date",
  REPORT_STATUS: "Report Status",
  REPORT_NAME: "Report Name",

  // Lot price master
  LOT_PRICE_MAINTENANCE: "Lot Price Master",
  PART_PRICE: "Part Price",
  SELECT_ROW: "Please select a row item in Grid to open Lot Part Price screen.",
  SELECT_ONE_ITEM: "Only one record can be selected to open Lot Part Price.",
  EFFECTIVE_FROM_MONTH: "Effective From Month",
  EFFECTIVE_TO_MONTH: "Effective To Month",
  LOT_CODE: "Lot Code",
  CURRENCY: "Currency",
  PART_NO: "Part No.",
  PART_NAME: "Part Name",
  PART_USAGE: "Part Usage",
  SAVE: "Save",
  LOT_PART_PRICE_MASTER: "Lot Part Price Master",
  HYPERLINK_ERROR: "Hyperlink to Error file",
  BROWSE: "Browse",
  SELECT_LOT_MASTER_FILE: "Select Lot Part Price Master File (.xlsx)",
  FROM_MONTH: "From Month",
  TO_MONTH: "To Month",
  UPLOAD: "Upload",
  PART_NAME_WARNING_1: "Part name mismatch with part master.",
  PART_NAME_CONFIRMATION: "Do you want to save?",
  PART_USAGE_WARNING_1:
    "Part Usage Mismatch with G-PAC Packing spec and Lot Size information",
  ERR_IN_1010: "Part Name cannot be Tilda(~)",
  ERR_IN_1011: "Price should be greater than Zero.",
  ERR_IN_1012: "Usage should be greater than Zero.",
  ERR_IN_1009:
    "There is some issue in updating Part Price Information, Please try again or contact to application support team.",

  //Invoice Operation & Haisen Details Retrieval
  DO_YOU_WISH_TO_PROCEED_WITHOUT_SAVE: "Do you wish to proceed without Save?",
  SELECT_HAISEN_NO_TO_SEE_INVOICE_DETAILS:
    "Select Haisen no. to see invoice details",
  PLEASE_SAVE_HAISEN_DETAILS_BEFORE_PROCEEDING:
    "Please Save Haisen Details before proceeding",
  NO_CHANGES_TO_SAVE: "No changes to Save",
  SEARCH_HAS_NOT_BEEN_PERFORMED: "Search has not been performed",
  DO_YOU_WISH_TO_SAVE_CHANGES: "Do you wish to save changes?",
  ERR_IN_1001: "Please check either regular or CPO/SPO",
  ERR_CM_3007: "ETD From should not be greater than ETD To.",
  ETD_TO_LESS_THAN_ETD_FROM: "ETD To should not be less than ETD From.",
  ERR_IN_1019: "Port of Loading and Port of Discharge cannot be same",
  ERR_IN_1020: "Space not allowed in Ocean Voyage",
  ERR_IN_1021: "ETD date should not be greater than ETA date",
  ERR_IN_1018: "ETD Date cannot be less than Invoice Date",

  //Common Download and Upload
  PLEASE_SELECT_XLSX_FILE_TO_UPLOAD: "Please select xlsx or xls file to Upload",
  UPLOAD_FILE_HAS_NOT_SELECTED: "Upload file has not selected",
  UPLOAD_REQUEST_SUBMITTED_SUCCESSFULLY:
    "Upload Request submitted successfully",
  EFFECTIVE_FROM_MONTH_SAME_CURRENT_MONTH:
    "Effective From Month is same as current month.",
  EFFECTIVE_FROM_MONTH_PASS_MONTH: "Effective From Month is pass month.",
  INFO_AD_2001:
    "It will be Offline Download, So please check the File in “On Demand Download Report”",
  ERR_AD_2001: "No Records in file to process",
  ERR_AD_2006: "File Format Not Correct",
  ERR_AD_2007: "Invalid Month",
  ERR_AD_2008: "Invalid Date",
  ERR_AD_2009: "Invalid Day",
  ERR_AD_2010: "Invalid Day, Only W or H allowed.",
  ERR_AD_2011: `Upload Request for - {year} does not match with {excelYearValues} in file.`,
  ERR_AD_2012: "Found duplicate records for {dateStr}",
  ERR_CM_3009: "Uploaded file template is not correct.",
  ERR_CM_3002:
    "Effective From Month cannot be greater than Effective To Month.",
  WARN_IN_3002:
    "Effective From Month is same as current month Do you confirm to upload",
  ERR_CM_3030 :"Wrong file selected.",

  // Inquiry for Process Status and Control keys
  INQUIRY_FOR_PROCESS_STATUS_AND_CONTROL:
    "Inquiry for Process Status and Control",
  INTERFACE_DROPDOWN: "Interface Dropdown",
  ADDITIONAL_PARAMETERS: "Additional Parameters",
  BATCH_SUCCESS: "Batch Submitted Successfully",
  BATCH_FAIL: "Batch Submission Failed",
  PROCESS_STATUS_ERROR_MESS: "Process Status And Error Message",
  PROCESS_NAME_DROPDOWN: "Process Name Dropdown",
  FROM_DATE_TIME: "From Date and Time",
  END_DATE_TIME: "End Date and Time",
  SYSTEM_NAME: "System Name",
  CREATED_BY: "Created By:",
  RE_CALCULATE: "Re-Calculate",
  ALL_PROCESS: "All Process",
  OWNER_PROCESS: "Owner Process",
  PROCESS_LOG_DETAILS: "Process Log Details",
  PROCESS_LOG_DETAILS_EXPORTED: "Log details exported in excel",
  DOWNLOAD_PROCESS_LOG_FAILED: "Download Failure",

  INFO_CM_3011: "Success",
  ERR_CM_3019: "From Date & Time should not be greater than To Date & Time.",

  // code master
  RECORD_DELETED_CONFIRMATION:
    "It will delete {noOfRecord} record. Do you wish to delete the selected record?",
  RECORD_EDIT_FAILED: "Record(s) edit failed!",
  CODE_MASTER_MAINTENANCE: "Code Master Maintenance",
  INVOICE_RECALCULATION: "Invoice Recalculation",
  ISO_CONTAINER: "ISO Container No. Master-Search",
  ADD_MAX_FORM_ERR: "Only {noOfRecord} records can be added at a time.",
  TEXT_LENGTH_ERROR: "Text length should be between {minValue} and {maxValue}",
  TEXT_LENGTH_LESS_ERROR:
    "{columnName} length cannot be less than {maxValue} characters",
  NUMBER_LENGTH_ERROR: "Number should be between {minValue} and {maxValue}",
  INFO_CM_3003: "Information saved successfully.",
  INFO_CM_3007: "Information saved successfully.",
  INFO_CM_3006: "{count} record deleted successfully.",
  ERR_CM_3004: "Please select record to delete.",
  ERR_CM_3023: "Cannot delete selected record, please try again or contact to the Administrator.",
  ERR_CM_3024: "Duplicate record found for Country Code {countryCodes}",
  ERR_CM_3025: "There is some issue in updating the Information, please try again or contact to the Administrator.",
  ERR_AD_2013: "cannot be set for more than 1 record (Check Payment Term Code : ",
  DEFAULT_FLAG: "Default Flag",
  //Shipping Control Master
  SHIPPING_CONTROL_MASTER: "Shipping Control Master",
  // vessel booking master
  VANNING_MONTH: "Vanning Month",
  TRY_AGAIN_ERROR: "Please try again.",
  ETD_FROM_DATE_LESS_THAN_ETD_TO_DATE:
    "ETD From date should not be over than ETD To date",
  SHIP_COMP: "Ship Comp.",
  NO_RECORD_FOR_DOWNLOAD: "Record is not available to Download.",
  INFO_MN_4001:
    "It will be Offline Download, So please check the File in shared folder “Vessel Booking”",
  ERR_MN_4001: "Please input YYYY/MM format.",
  ERR_MN_4002: "Booking no. already exists",
  
  //Invoice generation work plan master
  INFO_IN_1001: "Work Plan Master has been updated successfully",
  ERR_CM_3006:
    "{errorCode} - There is some issue in updating the Information, please try again or contact to the Administrator.",
  INVOICE_DATE_OVER_ETD_DATE:
    "Issue Invoice date should not be over than Actual ETD date (ETD1) and should not be past date.",
  ETD1_OVER_ETA1: "ETD1 cannot be more than ETA1",
  ETD2_OVER_ETA2: "ETD2 cannot be more than ETA2",
  ETD3_OVER_ETA3: "ETD3 cannot be more than ETA3",
  MODIFY_RECORD: "Please modify the record displayed in the table.",
  NO_DATA_FOR_DOWNLOAD: "No data on screen to download.",
  INVOICE_DATE_OVER_ETD_FROM:
    "Please select Issue Invoice Date From or ETD Date from to search the result",
  SIX_MONTH_LATER_INVOICE_DATE:
    "Please select max 6 Month Back date from the Current System date in Issue Invoice Date From.",
  SIX_MONTH_LATER_ETD_DATE:
    "Please select max 6 Month Back date from the Current System date in ETD Date From.",
  INVOICE_FROM_DATE_OVER_TO_DATE:
    "Issue Invoice Date From cannot be greater than Issue Invoice Date To",
  ETD_FROM_OVER_TO_DATE: "ETD Date From cannot be greater than ETD Date To",
  ERR_IN_1015:
    "Same Request already submitted and yet not completed. So cannot submit again",
  INFO_IN_1004:
    "It will be Offline Download, So please check the File in “On Demand Download Report",

  ERR_CM_3003: "Please select a record to copy.",
  SELECT_ONE_RECORD_COPY: "Only one record can be selected for copy.",

  // Mix Privilege Master
  MIX_PRIVILEGE_MASTER: "Mix Privilege Master",
  IMPORTER_CODE: "Importer Code",
  EXPORTER_CODE: "Exporter Code",
  CAR_FAMILY_CODE: "Car Family Code",
  ERR_IN_1024:
    "Van Date From {vanDateFrom} is a past date, so cannot be deleted.",
  ERR_CM_3005:
    "{errorCode} - Cannot delete selected record, please try again or contact to the Administrator.",
  INFO_CM_3002: "{count} record deleted successfully.",
  ERR_IN_1036: "Van Date From cannot be greater than Van Date To.",
  ERR_IN_1037:
    "Van Date From and Van Date To cannot be past date for new records.",
  ERR_IN_1038:
    "Van Date From/To cannot overlap with existing record {Re-ExporterCode}",
  ERR_IN_1039: "Priority [{N}] must have at least two privileges selected.",
  ERR_IN_1040:
    "Priority [{N}], Priority [{N1}] - Duplication of selected privileges flag is not allowed.",
  WARN_IN_1040:
    "Priority [{N}] More than 2 privilege types are selected, Do you wish to Save changes?",
  //WARN_IN_1041: "Priority [{N}] Mixed for [Privilege Name[{1}]] and [Privilege Name [{2}]] and …, Do you wish to Save changes?",
  WARN_IN_1041:
    "Priority [{N}] Mixed for [Privilege Name[{1}]], Do you wish to Save changes?",
  INFO_CM_3008: "No changes to save.",

  // Pxp Part Price Maintenance
  DOWNLOAD: "Download",
  PXP_PART_PRICE_MAINTENANCE: "PxP Part Price Maintenance",
  EFFECTIVE_MONTH: "Effective Month",
  ERR_CM_3020:
    "{errorCode} There is some issue in downloading the Information, please try again or contact to the Administrator.",
  ERR_CM_3021:
    "{deletedRecords} record deleted successfully, and {notDeletedRecord} record not deleted.",
  ERR_IN_1047: "Effective From Month cannot be past month.",
  ERR_IN_1048: "Effective To Month cannot be past month.",
  ERR_IN_1049: "Part No : {partNo} does not exists in Part master.",
  ERR_IN_1046:
    "Effective From Month and Effective To Month cannot overlap under same {keyColumns}",
  ERR_IN_1050: "Part No : {partNo} Part price can not be less then 0.",
  ERR_IN_3023: "Invoice already generated based on this price. It cannot be deleted.",

  // Renbane Group Code Master
  CONTAINER_DESITNATION: "Container Destination",
  ERR_CM_3008: "Duplicate record found for {keyColumns}",
  INFO_IN_1005 : "Selected record deleted successfully.",
  ERR_IN_1031 : "Booking No. already exists in Vessel Booking master for the respective Renban code in selected row.",
  ERR_IN_1030 : "There is some issue in deleting the Information, please try again or contact to the Administrator.",

  // Returnable Packing Master
  RETURNABLE_PACKING_MASTER: "Returnable Packaging Master",
  RETURNABLE_TYPE: "Returnable Type",
  MODULE: "Module",
  BOX_INNER_MAT: "Box/Inner Material",
  MODULE_TYPE_MAT: "Module type/Material Code",
  VANNING_DATE_FROM: "Vanning Date From",
  VANNING_DATE_TO: "Vanning Date To",
  PACKING_PLANT: "Packing Plant",

  // Invoice Setup Master
  INVOICE_SETUP_MASTER: "Invoice Setup Master",
  SETUP_TYPE: "Setup Type",
  P_X_P: "PxP",
  LOT: "Lot",
  ERR_CM_3022: "Monthly VPR is not available for selected [{monthYear}].",
  INFO_CM_3014_1: " has been removed from Payment Term Code : ",
  INFO_CM_3014_2: " and set to Payment Term Code : ",
  ENGINE_PART_MASTER: "Engine Part Master",

  //ICO Container
  ERR_CM_3080:"Booking no or Shipping Company is blank, please maintain vessel booking master.",
  ERR_CM_3081:"ISO Container No cannot be duplicated within same ETD and Destination.",
  ERR_CM_3082:"Seal No cannot be duplicated within same ETD and Destination.",
  ERR_CM_3083:"Mandatory Field Not Entered",
  
  //DUPLICATERECORDPAYMENTTERM : "Duplicate record found for Payment Term  Code {paymenttermcode}"
  INFO_AD_4002: "File uploaded successfully with some warning, please check error/warning file in Inquiry for Process Status and Control Screen.",
  ERR_AD_4003: "File upload failed, please check error/warning file in Inquiry for Process Status and Control screen.",
  INFO_CM_3004: "File Uploaded Successfully.",
  ERR_FDM_1001: "Destination code must be 4 characters/digits.",
  ERR_IN_1052: "Please select at least 2 values to mix CFC.",
  ERR_IN_1053: "Please select at least 2 values to mix Exporter Code.",
  ERR_IN_1054: "Please select at least 2 values to mix Re-Exp Code.",
  ERR_IN_1055: "Please select at least 2 values to mix Line Code.",
  ERR_IN_1051: "Van Dates From : {vanDateFrom} and Van Date To : {vanDateTo} already exists in existing records.",
  ERR_FILE_NOT_SELECTED: "File is not selected to upload.",
  ERR_CM_3026: "Duplicate record found for same {keyColumns}.",
  OVERLAPPING_ETD_FROM_AND_TO: "Overlapping of ETD From & ETD To is not allowed.",
  ETD_FROM_NOT_IN_CONTINUATION: "ETD From is not Continuation with previous ETD To, do you wish to save changes?",
  INFO_AD_4004: "Maximum Attempts exceeded.",
  INFO_AD_4005: "File Uploading in progress.",
  ERR_CM_3027: "Vanning Date From should not be greater than Vanning Date To.",
  ERR_CM_3028: "Van Date From should not be greater than Van Date To",
  ERR_DLY_1049: "Part Number [{partNo}] does not match with Part Master.",
  WARN_INVOICE_RECALCULATION: "ETA is already passed. Do you want to continue?",
  PART_MASTER_INQUIRY : "Part Master Inquiry",
  INPUT_ATLEAST_ONE_SEARCH : "Please input at least one  searching key.",
  INFO_IN_1006: "Invoice has been updated successfully. Updated invoice will be sent to IXOS automatically & please resend invoice to PLS",
  ERR_IN_1120: "Rev. Part Net Weight and Rev. Box  Weight cannot be 0 or blank.",
  ERR_IN_1121: "Box weight can't be less than sum of part weight.",
  TEXT_LENGTH_MAX_ERROR: "{columnName} should be {maxValue} characters",
  VALUE_CAN_NOT_BE: "Value cannot be {value}",
  ERR_AD_4004: "Invoice No. is Invalid.",
  ERR_AD_4005: "Haisen No. is Invalid.",
  MANDATORY_FIELD : "Mandatory Field Not Entered",
  WARN_AD_3003: "Invoice No. already sent to IXOS, Do you want to resend the Invoice.",
  WARN_AD_3004: "One or more Invoice No. under respective Haisen no. criteria already sent to IXOS, Do you want to resend the Invoice.",
  INFO_IN_1007: "Privilege has been updated successfully, please generate invoice separation if needed.",
  ERR_IN_1122: "Privilege Part {partNo} exists in Country Origin Master.",
  WARN_PART_NO: "ALL Part No. under this invoice {invoiceNo} will be re- calculated, Do you wish to process next?",
  ERR_CM_3029: "Seq No. {seqNo} Vanning Date From: {vanningDateFrom} & Vanning Date To: {vanningDateTo} should not be overlapped with existing new record in Grid."
};
