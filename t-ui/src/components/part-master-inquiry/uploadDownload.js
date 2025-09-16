import React, { useEffect, useState, useContext } from "react";
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { createMessage } from "../../helpers/util";
import PmiDataContext from "../../context/PmiDataContext";
import { MIME_TYPE } from "../../constants/constant";
import { removeSpecialChar } from "./partMasterUtil";
import { useSelector } from "react-redux/es/hooks/useSelector";
import { TpexLoader } from '../../common/components/loader/loader';
import { useNavigate } from "react-router-dom";
import axios from "axios";
const BaseUrl = process.env.REACT_APP_API_BASE_URL2
const uploadUrl = process.env.REACT_APP_API_BASE_URL

const UplaodDownload = (props) => {

    const [file, setFile] = useState(null);
    const [isError, setIsError] = useState(false);
    const [isSuccess, setIsSuccess] = useState(false)
    const [alertModalShow, setAlertModalShow] = useState(false);
    const [popHeading, setPopHeading] = useState('');
    const [popContent, setpopContet] = useState('')
    const [loading, setLoading] = useState(false)
    const PmiData = useContext(PmiDataContext)
    const [statusError, setStatusError] = useState(false)
    const compnayCode = useSelector(state => state.app.currentCompanyCode)
    const [processName, setProcessName] = useState('')
    const navigate = useNavigate()
    const userId = "TestUser"



    /* Checking file type */

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        const ftype = selectedFile.name.split(".").pop()
        setIsSuccess(false)
        const allowedType = ["xls", "xlsx"]
        if (!allowedType.includes(ftype)) {
            setIsError(true);
            handleAlert(LABEL_CONST.ERROR, LABEL_CONST.PLEASE_SELECT_XLSX_FILE_TO_UPLOAD)
            return;
        }
        setIsError(false);
        setFile(selectedFile);

    }

    /* Uploading file type */

    const handleSubmit = () => {
        if (isError) return;
        if (!file) {
            handleAlert(LABEL_CONST.ERROR, createMessage("ERR_CM_3001: " + LABEL_CONST.ERR_CM_3001))
            return
        }
        setLoading(true)
        setIsError(false);
        setIsSuccess(true);
        const formData = new FormData();
        formData.append("file", file);
        const headers = {
            "Content-Type": "multipart/form-data",
        };
        axios
            .post(uploadUrl + `commonUploadDownload/uploadPartMaster?batchName=BINS123&userId=${userId}&companyCode=${compnayCode}`, formData, headers)
            .then((response) => {
                if (response.data.statusMessage === "ERR_AD_4003") {
                    setStatusError(true)
                    setProcessName('Upload Part Master')
                }
                else if(response.data.statusMessage === "INFO_CM_3004"){
                    setTimeout(() => {
                        handleAlert(LABEL_CONST.INFORMATION, LABEL_CONST.INFO_CM_3004)
                    }, 1000);
                    setLoading(false)
                    props.setRowsPmi([]);
                }
            })
            .catch((error) => {

                if (error.response.data.statusCode == 400) {
                    handleAlert(LABEL_CONST.ERROR, LABEL_CONST.ERR_CM_3009)
                    setLoading(false)
                }
                else {
                    handleAlert(LABEL_CONST.ERROR, error.message)
                    setLoading(false)
                }
            }).finally(() => {
                setLoading(false)
            })
    }

    /* Download File */
    const downloadFile = () => {
        let fileNameToDownload = "";
        const payload = {
            cmpCd: compnayCode,
            partNo: removeSpecialChar(PmiData.searchValue.partNumber),
            partName: PmiData.searchValue.partName,
            partType: PmiData.searchValue.partType

        }
        setLoading(true)
        fetch(BaseUrl + 'invoice/partmaster/download', {
            method: 'post',
            headers: {
                'Accept': '*/*',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload)
        })
            .then(response => {
                fileNameToDownload = response.headers.get("filename") || ""
                return response.headers.get("Content-Type") === 'application/json' ? response.json() : response.blob()

            })
            .then(data => {
                let link = document.createElement("a");
                link.href = URL.createObjectURL(
                    new Blob([data], { type: MIME_TYPE["xlsx"] })
                );
                link.download = fileNameToDownload;
                link.click()
                setTimeout(() => {
                    handleAlert(LABEL_CONST.INFORMATION, LABEL_CONST.SUBMITTED_SUCCESSFULLY_INFO)
                }, 2000);

            })
            .catch(error => {
                handleAlert(LABEL_CONST.ERROR, error.message)
            }).finally(() => {
                setLoading(false)
            })
    }

    /* Popup alert messages */

    const handleAlert = (status, message) => {
        setAlertModalShow(true);
        setPopHeading(status);
        setpopContet(message);

    }

    const processInq = (event) => {
        event.preventDefault();
        console.log("ClickHua")
        navigate("/inquiry-for-process-status", {
            state: { processName: processName },
        });
    }



    useEffect(() => {

    }, [isError, isSuccess, compnayCode])


    return (
        <> <TpexLoader isLoading={loading} />
            <div className="form-group col-4">
                <div className="input-group custom-file-button">
                    <input className="form-control" type="file" id="formFile" onChange={handleFileChange} />
                    <label className="input-group-text" htmlFor="formFile" >Browse</label></div>
            </div>
            <div className="form-group col-6 align-self-center d-flex">
                <button type="button" className="btn btn-outline-primary ms-3 mt-0 me-0 mb-0 " title="" id="id" onClick={handleSubmit}>Upload</button>
                <button type="button" className="btn btn-outline-primary ms-3" onClick={downloadFile}>Download</button>
                {
                    statusError ? <div className="form-group col-2 align-self-center error-msg" onClick={() => { setStatusError(false) }}><label className="erro-lbl" onClick={processInq}>File Error</label></div> : null
                }
            </div>



            <AlertModal
                show={alertModalShow}
                onHide={() => setAlertModalShow(false)}
                status={popHeading}
                content={popContent}
            />
        </>
    )

}


export default UplaodDownload