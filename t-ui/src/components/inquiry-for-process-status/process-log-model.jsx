import { TpexTable } from '../../common/components/tables/TpexTable';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { useState, useEffect } from 'react';
import Modal from 'react-bootstrap/Modal';
import "../../components/lot-price-master/lot-price-master.css";
import TpexSimpleButton from '../../common/components/button';
import { MIME_TYPE, MODULE_CONST } from '../../constants/constant';
import { createMessage, createMesssageReplacer, getFileExtension } from '../../helpers/util';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { TpexLoader } from '../../common/components/loader/loader';


function ProcessLogDetailModal({ show, popupRows, onHide, errorBtnShow }) {
    const [isLoadingg, setIsLoadingg] = useState(true);
    const [popupRowsData, setPopupRowsData] = useState([]);

    const [alertModalShow, setAlertModalShow] = useState(false);
    const [searchStatus,setSearchStatus] = useState("");
    const [searchContent,setSearchContent] = useState("");

    useEffect(() => {
        setTimeout(function () {
            setIsLoadingg(false);
          }, 2000);
        setPopupRowsData(popupRows);
    }, [popupRows]);

     //Download butoon click
    function downloadProcessLog(e) {
        if (popupRowsData.length > 0) {
            setIsLoadingg(true);
            let fileNameToDownload = "";
            let apiBtnName='';
            if(e.target.id ==='errExclBtn') {
                apiBtnName=MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_DOWNLOAD_ERR_PROCESS_LOGS_API;

            } else if(e.target.id ==='exclBtn') {
                apiBtnName=MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_DOWNLOAD_PROCESS_LOGS_API;
            }
            const apiUrl = MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL + apiBtnName + '?processControlId=' + popupRowsData[0].requestId + '&processId=' + popupRowsData[0].processId;
            fetch(apiUrl, {
                method: 'GET',
                headers: {
                    'Accept': '*/*',
                    'Content-Type': 'application/json',
                }
            }).then((response) => {
                fileNameToDownload = response.headers.get("file_name") || ""
                if (response.headers.get("Content-Type") === 'application/json') {
                    return response.json();
                } else {
                    return response.blob();
                }
            }).then(
                data => {
                    setIsLoadingg(false);
                    if (data.type === 'application/octet-stream') {
                        const fileExt = getFileExtension(fileNameToDownload);
                        let link = document.createElement("a");
                        link.href = URL.createObjectURL(
                            new Blob([data], { type: MIME_TYPE[fileExt] })
                        );
                        link.download = fileNameToDownload;
                        link.click();
                        showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.PROCESS_LOG_DETAILS_EXPORTED);
                    } else {
                        setIsLoadingg(false);
                        catchErr(data);
                    }
                }
            ).catch(function (error) {
                console.log('download file error', error);
                showAlertMsg(LABEL_CONST.ERROR, createMessage(error.message));
            }).finally(() => {
                setIsLoadingg(false);
            });
        } else {
            showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.NO_DATA_FOUND);
        }
    }

    function catchErr(data) {
        if (data.errorMessageParams && Object.keys(data.errorMessageParams).length > 0) {
            const messageAfterReplace = createMesssageReplacer(data.errorMessageParams, data.exception);
            showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
        } else {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(data.exception));
        }
    }

    //Alert Box messages
    function showAlertMsg(status, content){
        setSearchStatus(status);
        setSearchContent(content);
        setAlertModalShow(true);
    }

    function addEditDataForParent(add, edit) {
        console.log(' ');
    }
    
    const columnsPopup = [
        {
            id: "logTime",
            name: "Log Date & Time"
        },
        {
            id: "requestId",
            name: "Request ID"
        },
        {
            id: "correlationId",
            name: "Correlation ID (Micro services)"
        },
        {
            id: "processName",
            name: "Process Name"
        },
        {
            id: "logMessage",
            name: "Log Message"
        },
        {
            id: "status",
            name: "Status"
        }
    ]
    return (
        <>
        {isLoadingg ?
            <TpexLoader /> : ''
          }
        <Modal
            show={show}
            onHide={onHide}
            size="lg"
            aria-labelledby="contained-modal-title-vcenter"
            centered
            backdropClassName="modalTable-backdrop"
            className="modal-table"
        >
            <Modal.Header closeButton>
                <i className="bg-border"></i>
                <Modal.Title>{LABEL_CONST.PROCESS_LOG_DETAILS} </Modal.Title>
            </Modal.Header>
            <Modal.Body>

                {/* table  */}
                <div className="grid-panel mt-10 p-0 pb-0">
                    
                    <TpexTable
                        rows={popupRowsData}
                        idName="processLogId"
                        moduleName="INQUIRY_PROCESS_STATUS_CONTROL"
                        rowPerPage={10}
                        selectAll={false}
                        selectRow={false}
                        columns={columnsPopup}
                        isCrud={false}
                        pagination={true}
                        filter={false}
                        serverSideFilter={false}
                        editTable={false}
                        customEdit={false}
                        addEditDataForParent={addEditDataForParent}
                    />
                    <div className="col-12 align-self-end text-end mt-10">
                    {errorBtnShow && <TpexSimpleButton text="Download Error/Warning File" id='errExclBtn' color="primary" leftmargin="3" handleClick={e=>downloadProcessLog(e)} />}
                        <TpexSimpleButton text="Download in Excel" color="primary" id='exclBtn' leftmargin="3" handleClick={e=>downloadProcessLog(e)} />
                    </div>
                    
                </div>
                <AlertModal
                        show={alertModalShow}
                        onHide={() => setAlertModalShow(false)}
                        status={searchStatus}
                        content={searchContent}
                    />
            </Modal.Body>

        </Modal>
        </>
    );
}

export default ProcessLogDetailModal;
