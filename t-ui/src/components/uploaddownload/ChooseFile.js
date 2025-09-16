import axios from "axios";
import { MODULE_CONST } from "../../constants/constant";
import TpexSimpleButton from "../../common/components/button";
import React, { useState } from "react";
import Table from "../uploaddownload/Table";
import { useNavigate } from "react-router-dom";
import { LABEL_CONST } from "../../constants/label.constant.en";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import {
  createMessage,
  createMesssageReplacer,
  getCurrent_yyyymm,
} from "../../helpers/util";

const ChooseFile = ({
  setIsLoading,
  uploadYear,
  userId,
  selectedUploadOptionId,
  processTable,
  setProcessTable,
  uploadEffectiveMonth,
  uploadEffectiveFromMonth,
  uploadEffectiveToMonth,
  fetchdata
}) => {
  const [chooseFile, setChooseFile] = useState("");
  const [modalShow, setModalShow] = useState(false);
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");
  const [processName, setProcessName] = useState("");
  const navigate = useNavigate();
  let cmpCode = "TMT";
  let currentyyyymm = getCurrent_yyyymm();

  const errModal = () => {
    setStatus(LABEL_CONST.ERROR);
    setContent(LABEL_CONST.SELECT_MANDATORY_INFO);
    setModalShow(true);
  };
  const errModalUiCodeAndErr = () => {
    setStatus(LABEL_CONST.ERROR);
    setContent("ERR_CM_3001 : " + LABEL_CONST.ERR_CM_3001);
    setModalShow(true);
  };

  const successModal = () => {
    setIsLoading(false);
    setStatus(LABEL_CONST.INFORMATION);
    setContent(LABEL_CONST.UPLOAD_REQUEST_SUBMITTED_SUCCESSFULLY);
    setModalShow(true);
  };

  const selectXlModal = () => {
    setChooseFile("");
    setStatus(LABEL_CONST.ERROR);
    setContent(LABEL_CONST.PLEASE_SELECT_XLSX_FILE_TO_UPLOAD);
    setModalShow(true);
  };

  const processHandleChange = (event) => {
    if(event.target.files.length){
      const fileType = event.target.files[0].name.slice(event.target.files[0].name.lastIndexOf(".") + 1);      
      validateFileForXls(fileType) ? setChooseFile(event.target.files[0]) : selectXlModal();
    }
  }
  const handleChange = (event) => {
    if (selectedUploadOptionId === "5") {
      setChooseFile(event.target.files[0]);
    } else {      
      processHandleChange(event);
    }
  };

  const selectedNationalCalMaster = () => {
    const formData = new FormData();
    const url = `${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL}${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_API}&userId=${userId}&year=${uploadYear}`;
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    setIsLoading(true);
    axios
      .post(url, formData, headers)
      .then((response) => {
        const { data } = response;
        
        setProcessName(data[0].processName);

        const newData = [...data];
        
        const addedIdData = newData.map((listofProcessCtrl, index) => ({
          ...listofProcessCtrl,
          processId: listofProcessCtrl.processId + index,
        }));

        if (addedIdData.length) {
          successModal();
        }

        setProcessTable(addedIdData);
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
      });
  };

  function catchErrorHandle(error) {
    if (
      error.response.data.errorMessageParams &&
      Object.keys(error.response.data.errorMessageParams).length > 0
    ) {
      const messageAfterReplace = createMesssageReplacer(
        error.response.data.errorMessageParams,
        error.response.data.exception
      );
      showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
    } else {
      showAlertMsg(
        LABEL_CONST.ERROR,
        createMessage(error.response.data.exception)
      );
    }
  }

  function showAlertMsg(status, content) {
    setStatus(status);
    setContent(content);
    setModalShow(true);
  }

  const selectedMasterLotPartPriceApi = () => {
    const formData = new FormData();
    const url =
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL +
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_LOTPARTPRICE_MASTER +
      `?batchName=BINF106&userId=${userId}&month=${uploadEffectiveMonth}`;
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    setIsLoading(true);
    axios
      .post(url, formData, headers)
      .then((response) => {
        response && successModal();
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
      });
  };

  const selectedMasterAddressApi = () => {
    const formData = new FormData();
    const url =
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL +
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_MASTER_ADDRESS_API +
      `?batchName=BINS125&userId=${userId}&companyCode=${cmpCode}`;
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    setIsLoading(true);
    axios
      .post(url, formData, headers)
      .then((response) => {
        response && successModal();
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
      });
  };

  const currentMonthWarning = () => {
    setStatus(LABEL_CONST.WARNING);
    setContent(LABEL_CONST.EFFECTIVE_FROM_MONTH_SAME_CURRENT_MONTH);
    setModalShow(true);
  };

  const passMonthWarning = () => {
    setStatus(LABEL_CONST.WARNING);
    setContent(LABEL_CONST.EFFECTIVE_FROM_MONTH_PASS_MONTH);
    setModalShow(true);
  };

  const selectedMasterPartPriceValidation = () => {
    if (chooseFile === "" || chooseFile === undefined) {
      errModalUiCodeAndErr();
    } else if (
      chooseFile.name.slice(chooseFile.name.lastIndexOf(".") + 1) !== "xlsx" &&
      chooseFile.name.slice(chooseFile.name.lastIndexOf(".") + 1) !== "xls"
    ) {
      selectXlModal();
    } else if (uploadEffectiveFromMonth === currentyyyymm) {
      currentMonthWarning();
    } else if (
      uploadEffectiveFromMonth &&
      uploadEffectiveFromMonth < currentyyyymm
    ) {
      passMonthWarning();
    } else {
      selectedMasterPartPriceApi();
    }
  };

  const selectedMasterPartPriceApi = () => {
    const formData = new FormData();
    const url =
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL +
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_MASTER_PART_PRICE_API +
      `?batchName=BINS107&userId=${userId}&effectiveFrom=${uploadEffectiveFromMonth}&effectiveTo=${uploadEffectiveToMonth}`;
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    setIsLoading(true);
    axios
      .post(url, formData, headers)
      .then((response) => {
        response && successModal();
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
      });
  };

  const selectedMasterCountryOfOriginValidation = () => {
    if (chooseFile === "" || chooseFile === undefined) {
      errModalUiCodeAndErr();
    } else if (
      chooseFile.name.slice(chooseFile.name.lastIndexOf(".") + 1) !== "xlsx" &&
      chooseFile.name.slice(chooseFile.name.lastIndexOf(".") + 1) !== "xls"
    ) {
      selectXlModal();
    } else {
      selectedMasterCountyOfOriginApi();
    }
  };

  const getMessage = (messageConst) => {    
    switch(messageConst) {
      case 'ERR_AD_4003' : 
        return LABEL_CONST.ERR_AD_4003;
      case 'INFO_CM_3004' : 
        return LABEL_CONST.INFO_CM_3004;
      case 'INFO_AD_4004' : 
        return LABEL_CONST.INFO_AD_4004;
      case 'INFO_AD_4005' : return LABEL_CONST.INFO_AD_4005;
      default : return '';
    }
  }

  const masterOfCountryOriginSuccessModal = (response) => {
    setIsLoading(false);    
    const statusMessage =  response.data.statusMessage;
    const label = statusMessage === 'ERR_AD_4003' ? LABEL_CONST.ERROR : LABEL_CONST.INFORMATION;
    const message = getMessage(statusMessage);
    showAlertMsg(label, message) 
  }

  const selectedMasterCountyOfOriginApi = () => {
    const formData = new FormData();
    const url =
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL +
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_COUNTRY_OF_ORIGIN_API +
      `?batchName=BINS029&userId=${userId}&companyCode=${cmpCode}`;
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    setIsLoading(true);
    axios
      .post(url, formData, headers)
      .then((response) => {
        response && masterOfCountryOriginSuccessModal(response);
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
      });
  };

  const uploadNationalCalanderMaster = () => {
    uploadYear === "" || uploadYear === null || chooseFile === ""
      ? errModal()
      : selectedNationalCalMaster();
  };

  const uploadMasterLotPartPrice = () => {
    chooseFile === ""
      ? errModalUiCodeAndErr()
      : selectedMasterLotPartPriceApi();
  };

  const uploadMasterAddress = () => {
    chooseFile === "" ? errModalUiCodeAndErr() : selectedMasterAddressApi();
  };

  const uploadMasterPartPrice = () => {
    chooseFile === ""
      ? errModalUiCodeAndErr()
      : selectedMasterPartPriceValidation();
  };

  const uploadMasterCountryOfOrigin = () => {
    chooseFile === "" ? errModalUiCodeAndErr() : selectedMasterCountryOfOriginValidation();
  };

  const getFileType = () => chooseFile.name.slice(chooseFile.name.lastIndexOf(".") + 1);

  const validateFileForXls = (fileType) => fileType === "xlsx" || fileType === "xls" ? true : false;

  const carFamilyMasterFileUpload = () => {
    validateFileForXls(getFileType()) ? carFamilyMasterUploadApi() : selectXlModal();
  };

  const fileUpload = async (url, formData, headers) => axios.post(url, formData, headers).then(response => response );

  const commonUpload = (baseUrl, apiUrl) => {
    setIsLoading(true);
    const url = baseUrl + apiUrl;
    const formData = new FormData();    
    formData.append("file", chooseFile);
    const headers = {
      "Content-Type": "multipart/form-data",
    };
    
    fileUpload(url, formData, headers)
    .then(response => {
      response && successModal();
    }).catch((error) => {
      setIsLoading(false);
      catchErrorHandle(error);
    });
  }

  const carFamilyMasterUploadApi = () => {
    let baseUrl = MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL;
    let apiUrl = MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_CAR_FAMILY_MASTER_API + `&userId=${userId}&companyCode=${cmpCode}`;

    commonUpload(baseUrl, apiUrl);
  }

  const uploadCarFamilyMaster = () => {
    chooseFile === "" || chooseFile === undefined ? errModalUiCodeAndErr() : carFamilyMasterFileUpload();
  };


  const handleUploadSubmit = (event) => {
    event.preventDefault();
    selectedUploadOptionId === "" && errModalUiCodeAndErr();
    selectedUploadOptionId === "2" && uploadMasterCountryOfOrigin();
    selectedUploadOptionId === "1" && uploadMasterAddress();
    selectedUploadOptionId === "3" && uploadMasterLotPartPrice();
    selectedUploadOptionId === "4" && uploadNationalCalanderMaster();
    selectedUploadOptionId === "5" && uploadMasterPartPrice();
    selectedUploadOptionId === "6" && uploadCarFamilyMaster();
  };

  const handleDownloadFileHere = async (event) => {
    event.preventDefault();
    navigate("/inquiry-for-process-status", {
      state: { processName: processName }
    });
  };

  return (
    <>
      <div className="col-3 align-self-end">
        <div className="input-group custom-upload-button d-flex justify-content-end">
          <input
            className="form-control"
            id="formFileSm"
            type="file"
            name="file"
            onChange={handleChange}
          />
          <label className="input-group-text" htmlFor="formFileSm">
            Browse
          </label>
        </div>
      </div>
      <div className="form-group col-2 align-self-end">
        <div className="d-flex justify-content-end">
          <TpexSimpleButton
            color="btn btn-primary"
            text="Upload"
            leftmargin="2"
            handleClick={handleUploadSubmit}
          />
          <TpexSimpleButton
            color="btn btn-primary"
            text="Refresh"
            leftmargin="2"
            handleClick={fetchdata}
          />
          <AlertModal
            handleClick={() => {
              selectedMasterPartPriceApi();
              setModalShow(false);
            }}
            show={modalShow}
            onHide={() => setModalShow(false)}
            status={status}
            content={content}
          />
          {processName && (
            <div className="upload-status">
              <a
                href="/"
                color="outline-primary"
                onClick={handleDownloadFileHere}
              >
                View Upload Status
              </a>
            </div>
          )}
        </div>
      </div>
      {processTable.length !== 0 ? (
        <Table uploadResponse={processTable} setIsLoading={setIsLoading} />
      ) : (
        <Table />
      )}
    </>
  );
};

export default ChooseFile;
