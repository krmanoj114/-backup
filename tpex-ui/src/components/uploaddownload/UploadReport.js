import React, { useState } from "react";
import { TpexSelect } from "../../common/components/select/TpexSelect";
import ChooseFile from "../uploaddownload/ChooseFile";
import TpexSimpleButton from "../../common/components/button";
import { MIME_TYPE, MODULE_CONST } from "../../constants/constant";
import { LABEL_CONST } from "../../constants/label.constant.en";
import { formatedDate_yyyymm, getFormatedDate_Time } from "../../helpers/util";
import { getFileRequest, postRequest } from "../../services/axios-client";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const UploadReport = ({
  options,
  userId,
  processTable,
  setProcessTable,
  setIsLoading,
  findReports,
  reports,
  fetchdata
}) => {
  const [downloadFormatYear, setDownloadFormatYear] = useState(null);
  const [uploadYearApi, setUploadYearApi] = useState(false);
  const [uploadYear, setUploadYear] = useState(null);
  
  const [modalShow, setModalShow] = useState(false);
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");
  const [selectedUploadOptionId, setSelectedUploadOptionId] = useState("");
  const [masterLotPartPrice, setMasterLotPartPrice] = useState(false);
  
  const [effectiveMonth, setEffectiveMonth] = useState(null);
  const [effectiveFromMonth, setEffectiveFromMonth] = useState(null);
  const [effectiveToMonth, setEffectiveToMonth] = useState(null);
  const [uploadEffectiveMonth, setUploadEffectiveMonth] = useState(null);
  const [uploadEffectiveFromMonth, setUploadEffectiveFromMonth] = useState(null);
  const [uploadEffectiveToMonth, setUploadEffectiveToMonth] = useState(null);

  const onChangeUploadSelection = (event) => {
    setSelectedUploadOptionId(event.target.value);
    event.target.value !== "" && uploadInputNotEmpty(event.target.value);
  };

  const uploadInputNotEmpty = (inputVal) => {
    let uploadReportDetails = findReports(inputVal);
    parseInt(inputVal) === uploadReportDetails.reportId &&
      dropdownUpload(uploadReportDetails.reportName, inputVal);
  };

  const dropdownUpload = (reportNames, value) => {
    reportNames === "NationalCalendarMaster"
      ? uploadFetchCalendar(value, reports)
      : setUploadYearApi(false);

    reportNames === "Master-Country of Origin" &&
      console.log("you slected Master-Country of Origin from upload dropdown");

    reportNames === "Master - Lot Part Price"
      ? uploadFetchMasterLot(value, reports)
      : setMasterLotPartPrice(false);
  };

  const uploadFetchCalendar = (reportId, reports) => {
    let reportDetails = reports.find((x) => {
      return x.reportId === parseInt(reportId);
    });
    setIsLoading(true);
    postRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.FETCH_CALENDAR_API,
      {
        reportId: reportId,
        reportName: reportDetails.reportName,
        reportJsonPath: reportDetails.reportJsonPath,
      }
    )
      .then((response) => {
        response.data.columns[0].name &&
          response.data.columns[0].name === "Year" &&
          setUploadYearApi(true);
        setIsLoading(false);
      })
      .catch((e) => {
        setIsLoading(false);
        console.log(e.message);
      });
  };

  const uploadFetchMasterLot = (reportId, reports) => {
    let reportDetails = reports.find((x) => {
      return x.reportId === parseInt(reportId);
    });
    setIsLoading(true);
    postRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.FETCH_CALENDAR_API,
      {
        userId: userId,
        reportId: reportId,
        reportName: reportDetails.reportName,
        reportJsonPath: reportDetails.reportJsonPath,
      }
    )
      .then((response) => {
        response.data.columns[0].name &&
          response.data.columns[0].name === "Effective Month" &&
          setMasterLotPartPrice(true);
        setIsLoading(false);
      })
      .catch((e) => {
        setIsLoading(false);
        console.log(e.message);
      });
  };

  const handleDownloadFormatYear = yearData => {
    setDownloadFormatYear(yearData);

    if (yearData) {
      setUploadYear(yearData.getFullYear());
    } 
  };

  const handleEffectMonth = yyyymmData => {
    if (yyyymmData !== undefined && yyyymmData !== "") {
      setEffectiveMonth(yyyymmData);
      setUploadEffectiveMonth(yyyymmData.getMonth() + 1);
    }
  };

  const handleEffectFromMonth = (yyyymmFromMonth) => {
    if (yyyymmFromMonth !== undefined && yyyymmFromMonth !== "") {
      setEffectiveFromMonth(yyyymmFromMonth);
      let fromMonth =
        yyyymmFromMonth === null ? "" : formatedDate_yyyymm(yyyymmFromMonth);
      setUploadEffectiveFromMonth(fromMonth);
    }
  };

  const handleEffectToMonth = (yyyymmToMonth) => {
    if (yyyymmToMonth !== undefined && yyyymmToMonth !== "") {
      setEffectiveToMonth(yyyymmToMonth);
      let toMonth =
        yyyymmToMonth === null ? "" : formatedDate_yyyymm(yyyymmToMonth);
      setUploadEffectiveToMonth(toMonth);
    }
  };

  const mandatoryDisplay = () => {
    setStatus(LABEL_CONST.ERROR);
    setContent("ERR_CM_3001 : " + LABEL_CONST.ERR_CM_3001);
    setModalShow(true);
  };

  const nationalCalendarMasterDownloadFormat = (year) => {
    downloadFormatYear === null
      ? mandatoryDisplay()
      : fetchYearDownloadFormat(year);
  };

  const handleUploadDownloadFormat = (selectedUploadOptionId) => {
    if (selectedUploadOptionId === "") {
      mandatoryDisplay();
    } else if (selectedUploadOptionId === "1") {
      masterAddressDownloadFormat();
    } else if (selectedUploadOptionId === "2") {
      masterCountryOfOriginDownloadFormat();
    } else if (selectedUploadOptionId === "3") {
      masterLotPriceDownloadFormat();
    } else if (selectedUploadOptionId === "4") {
      downloadFormatYear === null || downloadFormatYear === ""
        ? mandatoryDisplay()
        : nationalCalendarMasterDownloadFormat(
            downloadFormatYear.getFullYear()
          );
    } else if (selectedUploadOptionId === "5") {
      masterPartPriceDownloadFormat();
    } else if (selectedUploadOptionId === "6") {
      masterCarFamilyDestinationDownloadFormat();
    }
  };

  const masterCountryOfOriginDownloadFormat = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    link.download = "CountryOfOrigin_" + getFormatedDate_Time(new Date());
    setIsLoading(true);
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD
        .UPLOAD_COUNTRY_OF_ORIGIN_TEMPLATE_API + `?userId=${userId}`
    )
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  const commonDownloadFile = (filename, baseUrl, apiUrl) => {
    setIsLoading(true);
    const link = document.createElement("a");
    link.download = filename;
    getFileRequest(baseUrl, apiUrl)
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((_e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  const masterCarFamilyDestinationDownloadFormat = () => {
    const filename = "CarFamilyDestination_" + getFormatedDate_Time(new Date());
    let baseUrl = MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL;
    let apiUrl =
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_CAR_FAMILY_MASTER_TEMPLATE +
      `?userId=${userId}`;
    commonDownloadFile(filename, baseUrl, apiUrl);
  };

  const masterPartPriceDownloadFormat = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    setIsLoading(true);
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD
        .UPLOAD_MASTER_PART_PRICE_DOWNLOAD_TEMPLATE_API + `?userId=${userId}`
    )
      .then((res) => {
        link.download = "PxpPartPriceMaster" + getFormatedDate_Time(new Date());
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  const masterAddressDownloadFormat = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    setIsLoading(true);
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD
        .UPLOAD_MASTER_ADDRESS_DOWNLOAD_TEMPLATE_API + `?userId=${userId}`
    )
      .then((res) => {
        link.download = res.headers.filename;
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  const masterLotPriceDownloadFormat = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    link.download = "MasterLotPartPrice" + getFormatedDate_Time(new Date());
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD
        .MASTER_LOTPARTPRICE_DOWNLOAD_FORMAT_API + `?userId=${userId}`
    )
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  const fetchYearDownloadFormat = (year) => {
    const link = document.createElement("a");
    link.download = "NationalCalendar_" + getFormatedDate_Time(new Date());
    setIsLoading(true);
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_FORMAT_API +
        `?year=${year}&userId=${userId}`
    )
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setUploadYear(year);
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_SUCCESSFULLY);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(LABEL_CONST.TEMPLATE_DOWNLOAD_REQUEST_FAILED);
        setModalShow(true);
      });
  };

  return (
    <>
      <form>
        <div className="row mt-12">
          <div className="col-3">
            <div className="custom-multiSelect mandatoryControl">
              <label>Report Name</label>
              <TpexSelect
                selected="Select report"
                options={options}
                onChangeSelection={onChangeUploadSelection}
                hasValue={selectedUploadOptionId}
              />
            </div>
          </div>
          <div className="col-4">
            {options.some((option) => {
              return option.id === selectedUploadOptionId;
            }) ? (
              <>
                {uploadYearApi && (
                  <>
                    <div className="col-5">
                      <div className="customDatePicker mandatoryControl">
                        <label>Year</label>
                        <TpexDatePicker
                          id="requestdate"
                          dateSelected={downloadFormatYear}
                          handleDateSelected={date => handleDownloadFormatYear(date)}
                          showYearPicker={true}
                          isDirectDatePicker={true}
                        />
                      </div>
                    </div>
                  </>
                )}
                <div className="col-5">
                  {masterLotPartPrice && (
                    <div className="custom-multiSelect mandatoryControl">
                      <label>Effective Month</label>
                      <TpexDatePicker
                        id="effectiveMonth"
                        dateSelected={effectiveMonth}
                        handleDateSelected={handleEffectMonth}
                        showMonthYearPicker={true}
                        isDirectDatePicker={true}
                      />
                    </div>
                  )}
                </div>
                <div className="row">
                  {selectedUploadOptionId === "5" && (
                    <>
                      <div className="col-6">
                        <div className="custom-multiSelect mandatoryControl">
                          <label>Effective From Month</label>
                          <TpexDatePicker
                            id="effectiveFromMonth"
                            dateSelected={effectiveFromMonth}
                            handleDateSelected={handleEffectFromMonth}
                            showMonthYearPicker={true}
                            isDirectDatePicker={true}
                          />
                        </div>
                      </div>
                      <div className="col-6">
                        <div className="custom-multiSelect mandatoryControl">
                          <label>Effective To Month</label>
                          <TpexDatePicker
                            id="effectiveToMonth"
                            dateSelected={effectiveToMonth}
                            handleDateSelected={handleEffectToMonth}
                            showMonthYearPicker={true}
                            isDirectDatePicker={true}
                          />
                        </div>
                      </div>
                    </>
                  )}
                </div>
              </>
            ) : (
              ""
            )}
          </div>
          <ChooseFile
            uploadYear={uploadYear}
            uploadEffectiveMonth={uploadEffectiveMonth}
            userId={userId}
            selectedUploadOptionId={selectedUploadOptionId}
            processTable={processTable}
            setProcessTable={setProcessTable}
            setIsLoading={setIsLoading}
            uploadYearApi={uploadYearApi}
            uploadEffectiveFromMonth={uploadEffectiveFromMonth}
            uploadEffectiveToMonth={uploadEffectiveToMonth}
            fetchdata={fetchdata}
          />
        </div>
        <div className="download-format">
          <TpexSimpleButton
            color="link-with-icon"
            text="Download Format"
            handleClick={() =>
              handleUploadDownloadFormat(selectedUploadOptionId)
            }
          />
          <i className="fileFormat"></i>
          <AlertModal
            show={modalShow}
            onHide={() => setModalShow(false)}
            status={status}
            content={content}
          />
        </div>
      </form>
    </>
  );
};

export default UploadReport;
