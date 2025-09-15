import React, { useState } from "react";
import TpexSimpleButton from "../../common/components/button";
import { getFileRequest } from "../../services/axios-client";
import { MIME_TYPE, MODULE_CONST } from "../../constants/constant";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { LABEL_CONST } from "../../constants/label.constant.en";

import "../../styles/table.css";

const INITIAL_STATE = [
  {
    processId: "NATCALUPLOAD1",
    processName: "DA90020 invoice data to iXOS",
    processSubmitTime: "Nov 14,2022 10:30",
    status: "Processing",
    userId: "Coforge",
    parameter: "null",
  },
  {
    processId: "NATCALUPLOAD2",
    processName: "DA90030 invoice data to iXOS",
    processSubmitTime: "Nov 14,2022 10:30",
    status: "Success",
    userId: "Coforge",
    parameter: "null",
  },
  {
    processId: "NATCALUPLOAD3",
    processName: "DA90040 invoice data to iXOS",
    processSubmitTime: "Nov 14,2022 10:30",
    status: "SuccessWithWarnings",
    userId: "Coforge",
    parameter: "null",
  },
  {
    processId: "NATCALUPLOAD4",
    processName: "DA90050 invoice data to iXOS",
    processSubmitTime: "Nov 14,2022 10:30",
    status: "Error",
    userId: "Coforge",
    parameter: "null",
  },
];
const capitalize = (word) => {
  if(word === "ErrorWarningFile"){
    const error = Array.from('ErrorWarningFile');
    error.splice(5, 0, '/');
    const errorText = word[0].toUpperCase() + error.join('').slice(1).replace(/([a-z](?=[A-Z]))/g, "$1 ");
    return errorText;
  }else{
    return (
      word[0].toUpperCase() + word.slice(1).replace(/([a-z](?=[A-Z]))/g, "$1 ")
    );
  }

  
};

const Table = ({ uploadResponse, setIsLoading }) => {
  const [modalShow, setModalShow] = useState(false);
  const [statuses, setStatuses] = useState("");
  const [content, setContent] = useState("");

  let headings = INITIAL_STATE.map((row) => {
    let {
      processName: Process,
      processSubmitTime: SubmitTime,
      status,
      filePath: ErrorWarningFile,
      userId: Owner,
      parameter: BatchParameter
      
    } = row;
    return { Process, SubmitTime, status, ErrorWarningFile, Owner, BatchParameter };
  });

  const renderHeader = () => {
    return (
      <tr>
        {Object.keys(headings[0]).map((key) => (
          <th key={key}>{capitalize(key)}</th>
        ))}
      </tr>
    );
  };

  let data;
  if (
    uploadResponse &&
    uploadResponse !== undefined &&
    uploadResponse.length > 0
  ) {
    data = uploadResponse.map((row) => {
      let {
        processId,
        processName,
        processSubmitTime,
        status,
        userId,
        parameter,
        filePath,
      } = row;
      return {
        processId,
        processName,
        processSubmitTime,
        status,
        userId,
        parameter,
        filePath,
      };
    });
  }

  const checkStatus = (status) => <span>{status}</span>

  const getStatusClass = (status) => {
    const statusClass = (status === "Success with warning") ? 'Warning' : status;
    return `${statusClass}-status`;
  }

  const renderUsers = () => {
    return data.map(
      ({
        processId,
        processName,
        processSubmitTime,
        status,
        userId,
        parameter,
        filePath,
      }) => {
        return (
          <tr key={processId}>
            <td>{processName}</td>
            <td>{processSubmitTime}</td>
            <td className={getStatusClass(status)}>{checkStatus(status)}</td>
            <td>
              {filePath !== null && (
                <TpexSimpleButton
                  color="outline-primary"
                  text="File"
                  handleClick={() => handleErrorLoagBtn(filePath)}
                />
              )}
            </td>
            <td>{userId}</td>
            <td>{parameter}</td>
            
          </tr>
        );
      }
    );
  };

  const handleErrorLoagBtn = (filePath) => {
    setIsLoading(true);
    const link = document.createElement("a");
    let filename = filePath.split("/").pop();
    link.download = filename;
    getFileRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.DOWNLOAD_FILE_PATH_API +
        `?filePath=${filePath}`
    )
      .then((res) => {
        link.href = URL.createObjectURL(
          new Blob([res.data], { type: MIME_TYPE.xlsx })
        );
        setIsLoading(false);
        link.click();
        setStatuses(LABEL_CONST.INFORMATION);
        setContent(LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO);
        setModalShow(true);
      })
      .catch((e) => {
        setIsLoading(false);
        setStatuses(LABEL_CONST.ERROR);
        setContent(
          `Error code: ${e.response.status} - ${LABEL_CONST.DOWNLOAD_REQUEST_FAILED}`
        );
        setModalShow(true);
      });
  };

  return (
    <div className="report-panel grid-panel">
      <div className="grid-table">
        <div className="row g-0">
          <div className="col">
            <div className="table-responsive">
              <table className="table tpexTable uploadTable">
                <thead className="text-nowrap">{renderHeader()}</thead>
                <tbody>
                  {uploadResponse &&
                  uploadResponse !== undefined &&
                  uploadResponse.length > 0 ? (
                    renderUsers()
                  ) : (
                    <tr>
                      <td colSpan="5" className="noData">
                        No Data found
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
          <AlertModal
            show={modalShow}
            onHide={() => setModalShow(false)}
            status={statuses}
            content={content}
          />
        </div>
      </div>
    </div>
  );
};

export default Table;
