import React, { useState } from "react";
import { TpexSelect } from "../../common/components/select/TpexSelect";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { useNavigate } from "react-router-dom";
import { ADMIN_SERVICE, MIME_TYPE, MODULE_CONST } from "../../constants/constant";
import { LABEL_CONST } from "../../constants/label.constant.en";
import {
  getFileRequest,
  postRequest,
  getRequest,
} from "../../services/axios-client";
import {
  createMessage,
  createMesssageReplacer,
  formatedDate,
  formatedDate_mm_yyyy,
  formatedDate_yyyy_mm,
  formatedDateNumbers,
  getFileExtension,
  getFormatedDate_Time,
} from "../../helpers/util";
import { TpexMultiSelectSeach } from "../../common/components/multiselect/multiselect";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const DownloadReport = ({
  options,
  findReports,
  reports,
  userId,
  setIsLoading,
}) => {
  const [selectedOptionId, setSelectedOptionId] = useState("");
  const [downYearFlag, setDownYearFlag] = useState(true);
  const [downloadYear, setDownYear] = useState(null);
  const [modalShow, setModalShow] = useState(false);
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");
  const [dwonloadResponce, setDwonloadResponce] = useState([]);
  const [downloadYearApi, setDownloadYearApi] = useState(false);
  const [masterCountryApi, setMasterCountryApi] = useState(false);
  const [lotPartShortage, setlotPartShortage] = useState(false);
  const [partNo, setPartNo] = useState("");
  const [invoiceno, setInvoiceno] = useState(null);
  const [countryCode, setCountryCode] = useState([]);
  
  const [vanDateFrom, setVanDateFrom] = useState(null);
  const [vanDateTo, setVanDateTo] = useState(null);
  const [etdMonth, setEtdMonth] = useState(null);
  const [packingMonth, setPackingMonth] = useState(null);
  const [etd, setEtd] = useState(null);
  
  const [carFamily, setCarFamily] = useState([]);
  const [revisionno, setRevisionno] = useState("Latest");
  const [destination, setDestination] = useState([]);
  const [destinationList, setDestinationList] = useState([]);
  const [carFamilyList, setCarFamilyList] = useState([]);
  const [revisionnoList, setRevisionnoList] = useState([]);
  const [downloadVanDateFrom, setDownloadVanDateFrom] = useState('');
  const [downloadVanDateTo, setDownloadVanDateTo] = useState('');

  const [multiSelectedList, setMultiSelectedList] = useState([]);
  const navigate = useNavigate();
  let cmpCode = "TMT";

  const handleDownloadFileHere = async (event) => {
    event.preventDefault();
    let selectedReportName = reports.find((x) => {
      return x.reportId === parseInt(selectedOptionId);
    });
    navigate("/ondemanddownload", {
      state: { reportName: selectedReportName.reportName },
    });
  };

  const handleDownloadYear = (data) => {
    setDownYear(data);
    setDownYearFlag(true);
  };

  const errModalUiCodeAndErr = () => {
    setStatus(LABEL_CONST.ERROR);
    setContent("ERR_CM_3001 : " + LABEL_CONST.ERR_CM_3001);
    setModalShow(true);
  };

  const downloadNationalCalanderMaster = () => {
    downloadYear === "" || downloadYear === null
      ? errModalUiCodeAndErr()
      : selectedNationalCalMasterDownLoad(downloadYear.getFullYear());
  };

  const handleDownloadSubmit = (event) => {
    event.preventDefault();
    selectedOptionId === "" && errModalUiCodeAndErr();
    selectedOptionId === "1" && downloadFetchMasterAddress();
    selectedOptionId === "2" && downloadFetchMasterCountryOfOrigin();
    selectedOptionId === "4" && downloadNationalCalanderMaster();
    selectedOptionId === "6" && downloadMasterCarFamilyDestination();
    selectedOptionId === "7" && downloadReportLotPartShortage();
  };

  const downloadReportLotPartShortage = () => {
    //validation check on required field

    if ((etdMonth === null || etdMonth === undefined) || (revisionno === "" || revisionno === null || revisionno === undefined)) {
      errModalUiCodeAndErr();
    }
    else {
      let payload = {
        reportName: "Lotpartshortage",
        etdMonth: formatedDate_mm_yyyy(etdMonth),
        revisionNo: revisionno,
        destination: destination ? destination.value : null,
        carFamilyCode: carFamily ? carFamily.value : null,
        pkgMonth: formatedDate_yyyy_mm(packingMonth),
        etdDate: etd ? formatedDate(etd) : null,
        invoiceNo: invoiceno

      }
      setIsLoading(true);
      postRequest(
        MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
        MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_REPORT_LOT_PART_SHORTAGE_API,
        payload
      )
        .then((response) => {
          response.status === 200 && setIsLoading(false);
          setDwonloadResponce(response);
          showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO);
        })
        .catch((e) => {
          let errorDetailsCode = e?.response?.data?.exception;
          setIsLoading(false);
          setStatus(LABEL_CONST.ERROR);
          setContent(LABEL_CONST[errorDetailsCode]);
          setModalShow(true);
        });
    }
  }

  const downloadFileCommonUtility = (url, headers) => {
    let fileNameToDownload = "";
    fetch(url, headers)
      .then((response) => {
        fileNameToDownload = response.headers.get("filename") || ""
        if (response.headers.get("Content-Type") === 'application/json') {
          return response.json();
        } else {
          return response.blob();
        }
      }).then(
        data => {
          setIsLoading(false);
          if (data.type === 'application/octet-stream') {
            const fileExt = getFileExtension(fileNameToDownload);
            let link = document.createElement("a");
            link.href = URL.createObjectURL(
              new Blob([data], { type: MIME_TYPE[fileExt] })
            );
            link.download = fileNameToDownload;
            link.click();
            showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO);
          } else {
            setIsLoading(false);
            catchErr(data);
          }
        }
      ).catch(function (error) {
        console.log('download file error', error);
        showAlertMsg(LABEL_CONST.ERROR, createMessage(error.message));
      }).finally(() => {
        setIsLoading(false);
      });
  }

  const downloadMasterCarFamilyDestination = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    link.download = "CarFamilyDestinationMaster_" + getFormatedDate_Time(new Date());
    const url = `${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL}${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_CAR_FAMILY_DESTINATION_API}?cmpCode=${cmpCode}&userId=${userId}`
    downloadFileCommonUtility(url, { headers: { 'Accept': '*/*', 'Content-Type': 'application/json' } })
  };

  function downloadFetchMasterCountryOfOrigin() {
    setIsLoading(true);
    let multiSelectCodesList = multiSelectedList.map(list => {
      let val = list.value.split("-")
      return val[0];
    });
    const apiUrl = MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL +
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_COUNTRY_OF_ORIGIN_MASTER_API +
      `?vanDateFrom=${downloadVanDateFrom}&partPriceNo=${partNo}&countryOfOriginCode=${multiSelectCodesList}&vanDateTo=${downloadVanDateTo}`
      downloadFileCommonUtility(apiUrl,{headers: {
        'Accept': '*/*',
        'Content-Type': 'application/json'}})
  }

  function catchErr(data) {
    if (data.errorMessageParams && Object.keys(data.errorMessageParams).length > 0) {
      const messageAfterReplace = createMesssageReplacer(data.errorMessageParams, data.exception);
      showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
    } else {
      showAlertMsg(data.exception === 'INFO_CM_3001' ? LABEL_CONST.INFORMATION : LABEL_CONST.ERROR, createMessage(data.exception));
    }
  }


  const showAlertMsg = (status, content) => {
    setStatus(status);
    setContent(content);
    setModalShow(true);
  };

  const downloadFetchMasterAddress = () => {
    setIsLoading(true);
    const link = document.createElement("a");
    link.download = "AddressMaster_" + formatedDateNumbers(new Date());
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_MASTER_ADDRESS_API +
      `?cmpCode=${cmpCode}&userId=${userId}`
    )
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        setContent(
          `Error code: ${e.response.status} - ${LABEL_CONST.DOWNLOAD_REQUEST_FAILED}`
        );
        setModalShow(true);
      });
  };

  const selectedNationalCalMasterDownLoad = (year) => {
    const link = document.createElement("a");
    setIsLoading(true);
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_API +
      `?year=${year}&userId=${userId}`
    )
      .then((res) => {
        let filename = res.headers.file_name;
        link.download = filename.split(".")[0];
        const fileExt = getFileExtension(res.headers.file_name);
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE[fileExt] })
        );
        setIsLoading(false);
        link.click();
        setDwonloadResponce(res);
        setStatus(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatus(LABEL_CONST.ERROR);
        console.log(e);
        setContent(
          `Error code: ${e.response.status} - ${LABEL_CONST.DOWNLOAD_REQUEST_FAILED}`
        );
        setModalShow(true);
      });
  };

  const onChangeDownloadSelection = (event) => {
    setSelectedOptionId(event.target.value);
    event.target.value !== "" && downloadInputNotEmpty(event.target.value);
  };

  const downloadInputNotEmpty = (valInput) => {
    let downReportDetails = findReports(valInput);
    parseInt(valInput) === downReportDetails.reportId &&
      dropdownDownload(downReportDetails.reportName, valInput);
  };

  const dropdownDownload = (reportName, value) => {
    reportName === "NationalCalendarMaster"
      ? downloadFetchCalendar(value, reports)
      : setDownloadYearApi(false);

    reportName === "Master-Country of Origin"
      ? downloadFetchMasterCountry()
      : setMasterCountryApi(false);

    reportName === "Lot Part Shortage Information"
      ? getCarFamilyDestinations()
      : setlotPartShortage(false);
  };

  const downloadFetchCalendar = (reportId, reports) => {
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
          setDownloadYearApi(true);
        setIsLoading(false);
      })
      .catch((e) => {
        setIsLoading(false);
        console.log(e.message);
      });
  };

  const downloadFetchMasterCountry = () => {
    setMasterCountryApi(true);
    setIsLoading(true);
    getRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.FETCH_MASTER_COUNTRY_CODE_NAME_API
    )
      .then((response) => {
        setCountryCode(response.data);
        setIsLoading(false);
      })
      .catch((e) => {
        setIsLoading(false);
        console.log(e.message);
      });
  };

  function getCarFamilyDestinations() {
    setIsLoading(true);
    setlotPartShortage(true);
    getRequest(MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL_INVOICE, MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.FETCH_LOT_PART_SHORTAGE_CAR_DESTINATION_API).then(dataRes => {
      setDestinationList(dataRes.data.destinationList);
      setCarFamilyList(dataRes.data.carFamilyList);
      setRevisionnoList(dataRes.data.revisionNo);
      setIsLoading(false);
    })
      .catch(function (error) {
        console.log('getCarFamilyDestinations =>', error.message);
        setIsLoading(false);
      });
  }

  let optionsCountryCode = countryCode.map((option) => {
    let { countryCodeName: id, countryCodeName: name } = option;
    return {
      value: id.toString(),
      label: name.replace(/([a-z](?=[A-Z]))/g, "$1 "),
    };
  });

  const handleVanDateFrom = (VanDtFrom) => {
    if (VanDtFrom !== undefined && VanDtFrom !== "") {
      setVanDateFrom(VanDtFrom);
      let fromVanDate = VanDtFrom === null ? "" : formatedDate(VanDtFrom);
      setDownloadVanDateFrom(fromVanDate);
    }
  };

  const handleVanDateTo = (VanDtTo) => {
    if (VanDtTo !== undefined && VanDtTo !== "") {
      setVanDateTo(VanDtTo);
      let toVanDate = VanDtTo === null ? "" : formatedDate(VanDtTo);
      setDownloadVanDateTo(toVanDate);
    }
  };

  const handleEtdMonth = (etdMonth) => {
    if (etdMonth !== undefined && etdMonth !== "") {
      setEtdMonth(etdMonth);
    }
  };

  const handlePackingMonth = (packingMonth) => {
    if (packingMonth !== undefined && packingMonth !== "") {
      setPackingMonth(packingMonth);
    }
  };

  const handleEtd = (etd) => {
    if (etd !== undefined && etd !== "") {
      setEtd(etd);
    }
  };

  const handleSelectedOptions = (e, name) => {
    setMultiSelectedList(e);
    if (name === "destination") {
      setDestination(e);
    }

    if (name === "carFamily") {
      setCarFamily(e);
    }

    if (name === "revisionno") {
      const ddName = e.target.name;
      const ddValue = e.target.value;
      if (ddName === 'revisionno') {
        setRevisionno(ddValue);
      }
    }
  };

  return (
    <>
      <form>
        <div className="row mt-12 mb-20">
          <div className="col-3">
            <div className="custom-multiSelect mandatoryControl">
              <label>Report Name</label>
              <TpexSelect
                selected="Select report"
                options={options}
                onChangeSelection={onChangeDownloadSelection}
                hasValue={selectedOptionId}
              />
            </div>
          </div>
          <div className="col-7">
            {options.some((option) => {
              return option.id === selectedOptionId;
            }) ? (
              <>
                <div className="col-4">
                  {downloadYearApi && (
                    <div className="customDatePicker mandatoryControl">
                      <label>Year</label>
                      <TpexDatePicker
                        id="requestdate"
                        className={`form-control ${!downYearFlag ? "red-border" : ""}`}
                        dateSelected={downloadYear}
                        handleDateSelected={(date) => handleDownloadYear(date)}
                        dateFormat="yyyy"
                        showYearPicker={true}
                        isDirectDatePicker={true}
                      />
                    </div>
                  )}
                </div>
                <div className="row">
                  {masterCountryApi && (
                    <>
                      <div className="col-3">
                        <div>
                          <label>PartNo.</label>
                          <input
                            type="text"
                            className="form-control"
                            id="partno"
                            name="partno"
                            required="required"
                            maxLength="12"
                            defaultValue={partNo}
                            onChange={(e) => setPartNo(e.target.value)}
                          />
                        </div>
                      </div>
                      <div className="col-3">
                        <div>
                          <label>Country of Origin Code</label>
                          <TpexMultiSelectSeach
                            searchUrl={""}
                            handleSelectedOptions={(e) =>
                              handleSelectedOptions(e)
                            }
                            name="country"
                            noOptionsText="Search..."
                            value={multiSelectedList}
                            isMulti={true}
                            id="country"
                            serverSide={false}
                            staticValues={optionsCountryCode}
                            BASE_URL={ADMIN_SERVICE}
                          />
                        </div>
                      </div>
                      <div className="col-3">
                        <div>
                          <label>Van Date From</label>
                          <TpexDatePicker
                            id="vanDateFrom"
                            dateFormat="dd/MM/yyyy"
                            dateSelected={vanDateFrom}
                            handleDateSelected={date => handleVanDateFrom(date)}
                          />
                        </div>
                      </div>
                      <div className="col-3">
                        <div>
                          <label>Van Date To</label>
                          <TpexDatePicker
                            id="vanDateTo"
                            dateFormat="dd/MM/yyyy"
                            dateSelected={vanDateTo}
                            handleDateSelected={date => handleVanDateTo(date)}
                          />
                        </div>
                      </div>
                    </>
                  )}
                </div>
                <div className="row">

                  {
                    lotPartShortage && (
                      <>
                        <div className="form-group col-3">
                          <div className="customDatePicker mandatoryControl">
                            <label>ETD Month</label>
                            <TpexDatePicker
                              id="etdMonth"
                              dateSelected={etdMonth}
                              handleDateSelected={date => handleEtdMonth(date)}
                              showMonthYearPicker={true}
                              isDirectDatePicker={true}
                            />
                          </div>
                        </div>
                        <div className="form-group col-3">
                          <div className="customDatePicker">
                            <label>Packing Month</label>
                            <TpexDatePicker
                              dateFormat="yyyy/MM"
                              dateSelected={packingMonth}
                              id="packingMonth"
                              handleDateSelected={(date) => handlePackingMonth(date)}
                              showMonthYearPicker={true}
                              isDirectDatePicker={true}
                            />
                          </div>
                        </div>
                        <div className="form-group col-3">
                          <div className="customDatePicker">
                            <label>ETD</label>
                            <TpexDatePicker
                              dateFormat="dd/MM/yyyy"
                              dateSelected={etd}
                              id="etd"
                              handleDateSelected={(date) => handleEtd(date)}
                            />
                          </div>
                        </div>
                        <div className="col-3">
                          <div className="custom-multiSelect ">
                            <label htmlFor="destination">{LABEL_CONST.DESTINATION}</label>
                            <TpexMultiSelectSeach
                              searchUrl={''}
                              handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                              name="destination"
                              noOptionsText="Search..."
                              value={destination}
                              isMulti={false}
                              id="partShortageDestination"
                              serverSide={false}
                              staticValues={destinationList}
                              BASE_URL={ADMIN_SERVICE}
                            />
                          </div>
                        </div>
                      </>
                    )
                  }
                </div>
                <div className="row">

                  {
                    lotPartShortage && (
                      <>
                        <div className="col-3">
                          <div>
                            <label>Invoice No.</label>
                            <input
                              type="text"
                              className="form-control"
                              id="invoiceno"
                              name="invoiceno"
                              maxLength="10"
                              defaultValue={invoiceno}
                              onChange={(e) => setInvoiceno(e.target.value)}
                            />
                          </div>
                        </div>
                        <div className="col-3">
                          <div className="custom-multiSelect">
                            <label htmlFor="carFamily">{LABEL_CONST.CAR_FAMILY}</label>
                            <TpexMultiSelectSeach
                              searchUrl={''}
                              handleSelectedOptions={e => handleSelectedOptions(e, 'carFamily')}
                              name="carFamily"
                              noOptionsText="Search..."
                              value={carFamily}
                              isMulti={false}
                              id="partShortageCarFamily"
                              serverSide={false}
                              staticValues={carFamilyList}
                              BASE_URL={ADMIN_SERVICE}
                            />
                          </div>
                        </div>
                        <div className="col-3">
                          <div className="custom-multiSelect mandatoryControl">
                            <label htmlFor="revisionno">{LABEL_CONST.REVISION_NO}</label>
                            <TpexSelect
                              id="revisionNo"
                              onChangeSelection={e => handleSelectedOptions(e, 'revisionno')}
                              hasValue={revisionno}
                              moduleName="revisionno"
                              blankRequired={false}
                              options={revisionnoList} />
                          </div>
                        </div>
                      </>
                    )
                  }
                </div>
              </>
            ) : (
              ""
            )}
          </div>
          <div className="col-2 align-self-end">
            <div className="d-flex justify-content-end">
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleDownloadSubmit}
              >
                Download
              </button>
              <AlertModal
                show={modalShow}
                onHide={() => setModalShow(false)}
                status={status}
                content={content}
              />
            </div>
          </div>
        </div>
        <div className="download-link">
          {dwonloadResponce.length !== 0 ? (
            <span className="report-links">
              <a href="/" onClick={handleDownloadFileHere}>
                Download File
              </a>
              <i className="downloadIcon"></i>
            </span>
          ) : (
            ""
          )}
        </div>
      </form>
    </>
  );
};

export default DownloadReport;
