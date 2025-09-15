export const API_TIMEOUT = 200000;
export const API_WITH_CREDENTIAL = false;
export const RED_COLOR = '#EB0A1E';
export const DEFAULT_GRID_RECORD = 10;
export const REGEX_DD_YY_MMMM = /^\d{2}([./-])\d{2}\1\d{4}$/;
export const REGEX_YYYY_MM = /^\d{4}\1\d{2}$/;
export const {
   REACT_APP_API_BASE_URL: ADMIN_SERVICE,
   REACT_APP_API_BASE_URL2: INVOICE_SERVICE,
   REACT_APP_API_BASE_URL3: MONTH_SERVICE,
   REACT_APP_API_BASE_URL4: DAILY_SERVICE,
   REACT_APP_API_BASE_URL5: INVOICE_BATCH,
} = process.env;
export const MODULE_CONST = {
   ON_DEMAND_DOWNLOAD: {
      API_BASE_URL: ADMIN_SERVICE,
      ADD_API: "",
      EDIT_API: "",
      DELETE_API: "",
      DOWNLOAD_API: "onDemandDownload/reportDownload",
      REPORT_STATUS: "onDemandDownload/reportNamesAndStatus",
      GRID_API: "onDemandDownload/reports",
      DEFAULT_USER: "TestUser"
   },
   CODE_MASTER: {
      API_BASE_URL: ADMIN_SERVICE,
      ADD_API: "codeMaster/saveCodeMaster",
      EDIT_API: "codeMaster/updateCodeMaster",
      DELETE_API: "codeMaster/deleteCodeMaster",
      DOWNLOAD_API: "",
      REPORT_STATUS: "",
      GRID_API: "",
      LIST: "codeMaster/codeMasterNames",
      API_PATH: "codeMaster/",
      CODE_MASTER_DETAILS: "codeMaster/codeMasterName",
      CURR_BUYER_DROPDOWN_API: "codeMaster/finalDestinationMaster",
   },
   INVOICE_GENERATION: {
      API_BASE_URL: INVOICE_SERVICE,
      ADD_API: "",
      EDIT_API: "",
      DELETE_API: "",
      DOWNLOAD_API: "invoice/downloadInvoiceGenPlan",
      SAVE_API: "invoice/saveInvoice",
      DESTINATION_API: "invoice/destCodeAndDestName",
      SEARCH_API: "invoice/invoiceDetails",
      GRID_API: ""
   },
   COMMON_DOWNLOAD_UPLOAD: {
      API_BASE_URL: ADMIN_SERVICE,
      API_BASE_URL_INVOICE: INVOICE_SERVICE,
      FETCH_REPORT_API: "commonUploadDownload/reportsAndprocessDetails",
      FETCH_CALENDAR_API:"commonUploadDownload/report",
      DOWNLOAD_REPORT_LOT_PART_SHORTAGE_API:"commonUploadDownload/downloadLotPartShortageReport",
      FETCH_MASTER_COUNTRY_CODE_NAME_API:"commonUploadDownload/countrycodename",
      FETCH_LOT_PART_SHORTAGE_CAR_DESTINATION_API:"invoice/carFamilyAndDestinationMaster",
      DOWNLOAD_API: "commonUploadDownload/downlaodNatCalMaster",
      DOWNLOAD_FORMAT_API: "commonUploadDownload/downloadNatCalMasterTemplate",
      DOWNLOAD_MASTER_ADDRESS_API:"commonUploadDownload/downloadAddressMaster",
      DOWNLOAD_CAR_FAMILY_DESTINATION_API:"commonUploadDownload/downloadCarFmlyDestMasterReport",
      DOWNLOAD_COUNTRY_OF_ORIGIN_MASTER_API:"commonUploadDownload/downloadCountryofOriginMaster",
      DOWNLOAD_FILE_PATH_API:"onDemandDownload/reportDownload",
      MASTER_LOTPARTPRICE_DOWNLOAD_FORMAT_API:"commonUploadDownload/downloadLotPartPriceMstTemplate",
      UPLOAD_API:"commonUploadDownload/uplaodNatCalMaster?batchName=NationalCalendarexcelBatchReceiving",
      UPLOAD_LOTPARTPRICE_MASTER:"commonUploadDownload/uplaodLotPartPriceMaster",
      UPLOAD_MASTER_ADDRESS_DOWNLOAD_TEMPLATE_API:"commonUploadDownload/downloadAddressMasterTemplate",
      UPLOAD_COUNTRY_OF_ORIGIN_API:"commonUploadDownload/UploadcountryCodeOrigin",
      UPLOAD_COUNTRY_OF_ORIGIN_TEMPLATE_API:"commonUploadDownload/downloadCountryofOriginTemplate",
      UPLOAD_MASTER_ADDRESS_API:"commonUploadDownload/uplaodAddressMaster",
      UPLOAD_PART_MASTER_API:"commonUploadDownload/uploadPartMaster",
      UPLOAD_MASTER_PART_PRICE_DOWNLOAD_TEMPLATE_API:"commonUploadDownload/downloadPxpPartPriceTemplate",
      UPLOAD_MASTER_PART_PRICE_API:"commonUploadDownload/uplaodPxpPartPriceMaster",
      UPLOAD_INVOICE_GENERATION_API: "commonUploadDownload/uploadWrkPlanMaster?batchName=BINS027",
      DOWNLOAD_CAR_FAMILY_MASTER_TEMPLATE :"commonUploadDownload/downloadCarFamilyDestinationMaster",
      UPLOAD_CAR_FAMILY_MASTER_API :"commonUploadDownload/uplaodCarFamilyDestinationMaster?batchName=BINS126"
   },
   HAISEN_DETAILS_RETRIEVAL: {
      API_BASE_URL: process.env.REACT_APP_API_BASE_URL2,
      FETCH_SEARCH_API: "invoice/searchInvoice",
      FETCH_SAVE_HAISEN_API:"invoice/saveInvHaisenDetails",
      FETCH_INVOICE_DETAILS:"invoice/getInvDtlsByHaisenNo",
      SAVE_INVOICE_DETAILS:"invoice/updateInvoiceDetails",
   },
   INVOICE_RECALCULATION: {
      API_BASE_URL: INVOICE_SERVICE,
      FETCH_SEARCH_API: "invoice/invoiceDetailsByInv",
      SEARCH_API: "invoice/invrecalculatedetails",
      SAVE_API: "invoice/recalculateInvoice"
   },
   INVOICE_SHIPPING_REPORT: {
      API_BASE_URL: INVOICE_SERVICE,
      API_BASE_URL_REPORT: INVOICE_SERVICE,
      REPORT_PATH: "invoice/downloadInvoiceReports",
      REPORT_TYPES_DESTINATION: "invoice/destinationAndShippingReports",
      PRINT_REPORT: "invoice/downloadInvoiceReports",
      DOWNLOAD_PDF_XLS: "invoice/downloadExportedReports"
   },
   SHIPPING_CONTAINER_RESULT : {
      API_BASE_URL: INVOICE_SERVICE,
      DEST_API: "invoice/destCodeAndDestName",
      RENBAN_API: "invoice/renbanCodesByContDstCode",
      GRID_API: "invoice/invShippingContainerResults"      
   },
   INVOICE_MAINTENANCE : {
      API_BASE_URL: INVOICE_SERVICE,
      INVOICE_LIST_API: "invoice/orderTypeAndInvoiceNo",
      INVOICE_SEARCH_API: "invoice/searchByInvoiceNo",
      INVOICE_UPDATE_API: "invoice/updateInvDetailsByInvNo"
   },
   LOT_PRICE_MASTER : {
      API_BASE_URL: INVOICE_SERVICE,
      GRID_API: "invoice/searchLotPriceMasterDetails",
      CAR_FAMILY_DEST: "invoice/destinationAndCarfamily",
      PART_PRICE_API: "invoice/searchLotPartPricePopupDetails",
      ADD_API: "",
      EDIT_API: "invoice/UpdateLotPartPricePopupDetails",
      DELETE_API: "",
      DOWNLOAD_API: ""
   },
   INQUIRY_PROCESS_STATUS_CONTROL : {
      API_BASE_URL: ADMIN_SERVICE,
      INQUIRY_LIST_API: "commonProcessControl/processNames",
      INQUIRY_LIST_SELECTION_API: "commonProcessControl/processName",
      INQUIRY_LIST_SUBMIT_API: "commonProcessControl/submitProcess",
      INQUIRY_LIST_SYSTEM_API: "commonProcessControl/systemNames",
      INQUIRY_LIST_PROCESS_API: "commonProcessControl/processStatus",
      INQUIRY_LIST_PROCESS_LOGS_API: "commonProcessControl/processLogs",
      INQUIRY_LIST_DOWNLOAD_PROCESS_LOGS_API: "commonProcessControl/downloadProcessLogs",
      INQUIRY_LIST_DOWNLOAD_ERR_PROCESS_LOGS_API: "commonProcessControl/exportProcessLogs"
   },
   VESSEL_BOOKING_MASTER : {
      API_BASE_URL: MONTH_SERVICE,
      TPEX_INVOICE_BATCH: INVOICE_BATCH,
      WORK_PLAN_MASTER_API: "invoice/uplaodWrkPlanMasterFromVesselBooking",
      VESSEL_BOOKING_DEST : "month/dropdown/finalDstAndShipComp",
      SEARCH_API: "month/vesselBookingMaster/search",
      SAVE_API: "month/vesselBookingMaster/save",
      UPLOAD_API: "",
      DOWNLOAD_API: "month/vesselBookingMaster/download"
   },
   SHIPPING_CONTROL_MASTER_LIST: {
      API_BASE_URL: INVOICE_SERVICE,
      ONLOAD_LIST_API: "invoice/shippingControlMasterList",
      BUYER_SELECTED_LIST_API: "invoice/consigneeAndNotifyPartyByBuyer",
      SAVE_API: "invoice/saveShippingControlMaster",
      DELETE_API: "invoice/deleteShippingControlMaster"
   },
   MIX_PRIVILEGE_MASTER : {
      API_BASE_URL: INVOICE_SERVICE,
      GRID_API: "invoice/fetchMixPrivilegeDetails",
      CAR_FAMILY_DEST: "invoice/destinationAndCarfamily",
      SAVE_API: "invoice/saveMixPrivilegeMaster",
      DELETE_API: "invoice/deleteMixPrivilegeMaster",
   },
   PXP_PART_PRICE_MAINTENANCE: {
      API_BASE_URL: INVOICE_SERVICE,
      CAR_FAMILY_DEST: "invoice/destinationAndCarfamilyCodes?userId=",
      GRID_API: "invoice/partPriceMasterList",
      ADD_API: "invoice/savePxpPartPriceMaster",
      EDIT_API: "invoice/updatePxpPartPriceMaster",
      DELETE_API: "invoice/deletePxpPartPriceMaster",
      DOWNLOAD_API: "invoice/downloadPartPriceMasterDetails",
      GET_PARTNAME: "invoice/partName/"
   },
   RENBANE_GROUP_CODE_MASTER : {
      API_BASE_URL: INVOICE_SERVICE,
      RENBANE_CODE_MASTER_DEST : "invoice/destCodeAndDestName",
      SEARCH_API: "invoice/renbanGroupCodeByDestination",
      SAVE_API: "invoice/saveRenbanCodeMaster",
      UPDATE_API: "invoice/updateRenbanCodeMaster",
      UPLOAD_API: "",
      DELETE_API: "invoice/renbanGroupCode",
      DOWNLOAD_API: "month/vesselBookingMaster/download"
   },
   ISO_CONTAINER : {
      API_BASE_URL: process.env.REACT_APP_API_BASE_URL4,
      PLANT_DEST_CODE : "isoContainer/plantAndDestinationCode",
      SEARCH_API : "isoContainer/plantAndDestinationCodeSearch",
      SAVE_API : "isoContainer/add"
   },
   INVOICE_SETUP_MASTER: {
      API_BASE_URL: INVOICE_SERVICE,
      IMPORTER_CODE_API: "invoice/destCodeAndDestName",
      SEARCH_API: "invoice/invoiceSetupMaster",
      DELETE_API: "invoice/invoiceSetupMaster",
      SAVE_API: "invoice/invoiceSetupMaster",
   },
   ENGINE_PART_MASTER: {
      API_BASE_URL: DAILY_SERVICE,
      IMPORTER_CODE_API: "enginePartMaster/destinationCode",
      SEARCH_API: "enginePartMaster/enginePartMasterSearch",
      SAVE_API: "enginePartMaster/saveEnginePartMaster",
      DELETE_API: "enginePartMaster/deleteEnginePartMaster",
   },
   RETURNABLE_PACKING_MASTER: {
      API_BASE_URL: INVOICE_SERVICE,
      ONLOAD_API: "invoice/packingPlantAndImporterCode",
      SEARCH_API: "invoice/returnablePackingMasterList",
      DELETE_API: "invoice/deleteReturnablePackingMasterList",
      SAVE_API: "invoice/saveReturnablePackingMasterList"
   },
   COMPANY_CODE: {
      API_BASE_URL: process.env.REACT_APP_API_BASE_URL,
      DROP_DOWN_URL: "dropdown/companyAndPlant"
   },
   PART_MASTER_INQUIRY: {
      API_BASE_URL : process.env.REACT_APP_API_BASE_URL2,
      SEARCH_URL : "invoice/partmaster/search",
      Add_URL : "invoice/partmaster/save",
      EDIT_URL : "invoice/partmaster/update",
      DELETE_URL : "invoice/partmaster/delete",
      INHOUSE_SHOP : "invoice/partmaster/inhouseshop?companyCode="
   },
   MANUAL_INVOICE_GENERATION : {
      API_BASE_URL: process.env.REACT_APP_API_BASE_URL2,
      INVOICE_LIST_API: "invoice/searchManualInvoice",
   },
   PACKING_VANNING_INSTRUCTION : {
      API_BASE_URL: process.env.REACT_APP_API_BASE_URL2,
      CONTAINER_DESTINATION_LIST_API: "invoice/Packing/destination",
      CUSTOM_LABEL_API: "/invoice/Packing/customLabel",
      VANNING_PLANT_API: "/invoice/Packing/vanningPlant",
      PACKAGING_PLANT_API: "/invoice/Packing/packingPlant",
      IMPORTER_CODE_API: "/invoice/Packing/importerCode",
   }
};
export const MIME_TYPE = {
   csv: "text/csv",
   doc: "application/msword",
   docx: "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
   pdf: "application/pdf",
   txt: "text/plain",
   xls: "application/vnd.ms-excel",
   xlsx: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
   xml: "application/xml",
   zip: "application/zip"
};
export const CODE_MASTER_ID = {
   CAR_FAMILY_MASTER: "1",
   COUNTRY_CODE_FOR_CEO: "2",
   CURRENCY_CODE_MASTER: "3",
   FINAL_DEST_MASTER: "4",
   PAYMENT_TERM_MASTER: "5",
   PORT_MASTER: "6"
};