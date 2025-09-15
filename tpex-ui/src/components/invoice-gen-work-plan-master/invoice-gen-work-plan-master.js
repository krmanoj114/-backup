import axios from "axios";
import React, { useEffect, useState } from 'react';
import "./invoice-gen-work-plan-master.css";
import { getFileRequest, getRequest, postRequest } from '../../services/axios-client';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import TpexSimpleButton from '../../common/components/button';
import { MIME_TYPE, MODULE_CONST } from '../../constants/constant';
import { dateCompare, dateValidationForSixMonth, ddmmTOmmddChange, formatedDate, getFormatedDate_Time, deepEqual, createMessage, createMesssageReplacer } from '../../helpers/util';
import { TpexTableNew } from './TpexTableNew';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexLoader } from '../../common/components/loader/loader';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { useNavigate } from "react-router-dom";
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const InvoiceGenWorkPlanMaster = () => {
    const [options, setOptions] = useState([]);
    const [rows, setRows] = useState([]);
    const [rowsFilter, setRowsFilter] = useState([]);

    const [dateInvoiceFrom, setInvDateFrom] = useState(null);
    const [dateInvoiceTo, setInvDateTo] = useState(null);
    const [dateEtdFrom, setInvEtdDateFrom] = useState(null);
    const [dateEtdTo, setInvEtdDateTo] = useState(null);
    const [chkRegular, setRegularChkBx] = useState(false);
    const [chkCPO, setCpoChkBx] = useState(false);
    const [chkBoxDis, setChkBoxDis] = useState(true);
    const [optionsPort, setOptionsPort] = useState([]);
    const [optionsBroker, setOptionsBroker] = useState([]);
    const [gridDataLength, setGridDataLength] = useState(0);
    const currPage = 0;
    const pageSize = 250;
    const [pageSizeTotal, setPageTotalSize] = useState(1);
    const [currentPage, setCurrentServerPage] = useState(0);
    const [savePayload, setSavePayload] = useState([]);
    const [refreshGrid, setRefreshGrid] = useState(false);

    const [selectedDest, setSelectedDest] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const reqPara = {
        issueInvoiceFromDate: '',
        issueInvoiceToDate: '',
        etd1fromDate: '',
        etd1ToDate: '',
        contDest: selectedDest
    };
    const [requsetPayLoad, setReqPara] = useState(reqPara);
    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [uploadFile, setUploadFile] = useState("");
    const navigate = useNavigate();
    const [processName, setProcessName] = useState(null);

    const handleRegularChange = (e, name) => {
        const checked = e.target.checked;
        if (name === 'checkbox1') {
            setRegularChkBx(checked);
        }
        if (name === 'checkbox2') {
            setCpoChkBx(checked);
        }

    };

    // Get Destination API
    const getDestination = () => {
        setIsLoading(true);
        getRequest(MODULE_CONST.INVOICE_GENERATION.API_BASE_URL, MODULE_CONST.INVOICE_GENERATION.DESTINATION_API).then(data => {
            setIsLoading(false);
            const dataSelect = data.data.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + ' - ' + d.fdDstNm
                }
            });
            setOptions(dataSelect);
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportTypes =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    };

    const handleSelectedOptions = (e) => {
        setSelectedDest(e);
    };

    const getDestinationValue = (d) => {
        return d.map(k => k.value);
    };

    const handlePageClickServer = (pageNo) => {
        getInvData(pageNo, pageSize, requsetPayLoad);

    };

    //Table grid data call
    const getInvData = (currPage, pageSize, requsetPayLoad) => {
        setIsLoading(true);
        postRequest(MODULE_CONST.INVOICE_GENERATION.API_BASE_URL, MODULE_CONST.INVOICE_GENERATION.SEARCH_API + `?pageNo=${currPage}&pageSize=${pageSize}`, requsetPayLoad).then(data => {
            if (data.data.invoiceDetails.length === 0) {
                setChkBoxDis(true);
                setInvDateFrom(null);
                setInvDateTo(null);
                setInvEtdDateFrom(null);
                setInvEtdDateTo(null);
                setSelectedDest([]);
                setRowsFilter([]);
                setGridDataLength(data.data.invoiceDetails.length);
                showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.INFO_CM_3001);
                setIsLoading(false);
                return false;
            }

            const rowsNew = data.data.invoiceDetails.map((k, i) => {
                k.bookingNo = k.id.bookingNo;
                k.originalEtd = k.id.originalEtd;
                k.contDest = k.id.contDest;
                k.liner = k.id.liner;
                k.renbanCode = k.id.renbanCode;
                k.idCount = i;
                return k;
            });

            setRows(rowsNew);
            setRowsFilter(rowsNew);
            setChkBoxDis(false);
            setRegularChkBx(true);
            setCpoChkBx(true);
            setGridDataLength(data.data.invoiceDetails.length);
            setRefreshGrid(false);
            setCurrentServerPage(data.data.currentPage);
            setPageTotalSize(data.data.totalPages);
            const dataPortSelect = data.data.portDetails.map(d => {
                return {
                    id: d.code,
                    name: d.code
                }
            });
            setOptionsPort(dataPortSelect);
            const dataBroker = data.data.brokerDetails.map(d => {
                return {
                    id: (d.brokerCode).toUpperCase(),
                    name: (d.brokerCode).toUpperCase() + '-' + (d.brokerName).toUpperCase()
                }
            });
            setOptionsBroker(dataBroker);
            setIsLoading(false);
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportTypes =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    };

    const checkNullForDate = dateObj => dateObj ? formatedDate(dateObj) : null;
    // Download EXCEL file API
    const getDownloadGridData = async () => {
        setIsLoading(true);
        const {API_BASE_URL, DOWNLOAD_API} = MODULE_CONST.INVOICE_GENERATION;

        getRequest(
            API_BASE_URL,
            DOWNLOAD_API + DOWNLOAD_API + `?issueInvoiceFromDate=${checkNullForDate(dateInvoiceFrom)}&issueInvoiceToDate=${checkNullForDate(dateInvoiceTo)}&etd1fromDate=${checkNullForDate(dateEtdFrom)}&etd1ToDate=${checkNullForDate(dateEtdTo)}&contDest=${getDestinationValue(selectedDest)}`
        ).then((data) => {

            if (data.data.status && data.data.status === 'offline') {
                setIsLoading(false);
                showAlertMsg(LABEL_CONST.INFORMATION, createMessage(data.data.message));
                return false;
            } else {
                const link = document.createElement("a");
                link.download = 'InvoiceGenrartionWorkPlan_' + getFormatedDate_Time(new Date());//fileName;
                getFileRequest(
                    API_BASE_URL,
                    DOWNLOAD_API + `?issueInvoiceFromDate=${checkNullForDate(dateInvoiceFrom)}&issueInvoiceToDate=${checkNullForDate(dateInvoiceTo)}&etd1fromDate=${checkNullForDate(dateEtdFrom)}&etd1ToDate=${checkNullForDate(dateEtdTo)}&contDest=${getDestinationValue(selectedDest)}`
                ).then((res) => {
                    setIsLoading(false);
                    link.href = URL.createObjectURL(
                        new Blob([res.data], { type: MIME_TYPE.xlsx })
                    );
                    link.click();
                }).catch(error => {
                    setIsLoading(false);
                    console.log('getError =>', error.message);
                    showAlertMsg(LABEL_CONST.ERROR, error.message);
                });
            }
        }).catch(error => {
            setIsLoading(false);
            console.log('getError =>', error);
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
            return false;
        });
    };

    const EtdAndInvoiceDate = (frmDate, toDate) => {
        let result = true;
        if (frmDate !== null && toDate !== null && !dateCompare(frmDate, toDate)) {
            result = false;
        }
        return result;
    };
    
    const searchDataValidate = (dateInvoiceFrom, dateEtdFrom, dateInvoiceTo, dateEtdTo) => {
        let err = [];
        if ((dateInvoiceFrom === null || dateInvoiceFrom === undefined) &&
            (dateEtdFrom === null || dateEtdFrom === undefined)) {
            err.push(LABEL_CONST.INVOICE_DATE_OVER_ETD_FROM);
        } else if (dateInvoiceFrom !== null && !dateValidationForSixMonth(dateInvoiceFrom)) {
            err.push(LABEL_CONST.SIX_MONTH_LATER_INVOICE_DATE);
        } else if (dateEtdFrom !== null && !dateValidationForSixMonth(dateEtdFrom)) {
            err.push(LABEL_CONST.SIX_MONTH_LATER_ETD_DATE);
        } else if (!EtdAndInvoiceDate(dateInvoiceFrom, dateInvoiceTo)) {
            err.push(LABEL_CONST.INVOICE_FROM_DATE_OVER_TO_DATE);
        } else if (!EtdAndInvoiceDate(dateEtdFrom, dateEtdTo)) {
            err.push(LABEL_CONST.ETD_FROM_OVER_TO_DATE);
        }
        return err;
    };
    
    // Search Button Click
    const searchData = (e) => {
        setProcessName(null);
        const isValidationSearchPassed = searchDataValidate(dateInvoiceFrom, dateEtdFrom, dateInvoiceTo, dateEtdTo);
        if (isValidationSearchPassed?.length) {
            showAlertMsg(LABEL_CONST.ERROR, isValidationSearchPassed.join("\n"));
        }
        else {
            setReqPara({
                ...requsetPayLoad,
                issueInvoiceFromDate: dateInvoiceFrom ? formatedDate(dateInvoiceFrom) : null,
                issueInvoiceToDate: dateInvoiceTo ? formatedDate(dateInvoiceTo) : null,
                etd1fromDate: dateEtdFrom ? formatedDate(dateEtdFrom) : null,
                etd1ToDate: dateEtdTo ? formatedDate(dateEtdTo) : null,
                contDest: getDestinationValue(selectedDest)
            });
            getInvData(currPage, pageSize, requsetPayLoad);
            setRefreshGrid(false);
        }
    };

    //Download butoon click
    const downloadGridData = (e) => {
        if (gridDataLength > 0) {
            setReqPara({
                ...requsetPayLoad,
                issueInvoiceFromDate: dateInvoiceFrom ? formatedDate(dateInvoiceFrom) : null,
                issueInvoiceToDate: dateInvoiceTo ? formatedDate(dateInvoiceTo) : null,
                etd1fromDate: dateEtdFrom ? formatedDate(dateEtdFrom) : null,
                etd1ToDate: dateEtdTo ? formatedDate(dateEtdTo) : null,
                contDest: selectedDest
            });

            getDownloadGridData();
        } else {
            showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.NO_DATA_FOR_DOWNLOAD);
            return false;
        }
    };

    const ETDCompareETA = (no, obj) => {
        let eta = "eta" + no;
        let etd = "etd" + no;
        let result = true;
        if (obj[etd] !== null && obj[eta] !== null) {
            if (!dateCompare(ddmmTOmmddChange(obj[etd]), ddmmTOmmddChange(obj[eta]))) {
                result = false;
            }
        }
        return result;
    };

    const gridBlankColumn = (etd1, eta1, folderName) => {
        let result = true;
        if ((!etd1 && etd1 === null) || (!eta1 && eta1 === null) || (!folderName && (folderName === null || folderName === ''))) {
            result = false;
        }
        return result;
    };

    const gridDateCompare = (issueInvoiceDate, etd1) => {
        let result = true;
        if (issueInvoiceDate !== null && etd1 !== null) {
            if ((!dateCompare(new Date().setHours(0, 0, 0, 0), ddmmTOmmddChange(issueInvoiceDate)))
                || (!dateCompare(ddmmTOmmddChange(issueInvoiceDate), ddmmTOmmddChange(etd1)))) {
                result = false;
            }
        }
        return result;
    };

    const addOtherErr = (data) => {
        let errMessage = [];
        for (let i = 0; i < data.length; i++) {
            let savePayload = data;
            if (!gridBlankColumn(savePayload[i].etd1, savePayload[i].eta1, savePayload[i].folderName)) {
                errMessage.push(LABEL_CONST.SELECT_MANDATORY_INFO);
                return errMessage;
            } else if (!gridDateCompare(savePayload[i].issueInvoiceDate, savePayload[i].etd1)) {
                errMessage.push(LABEL_CONST.INVOICE_DATE_OVER_ETD_DATE);
                return errMessage;
            } else if (!ETDCompareETA(1, savePayload[i])) {
                errMessage.push(LABEL_CONST.ETD1_OVER_ETA1);
                return errMessage;
            } else if (!ETDCompareETA(2, savePayload[i])) {
                errMessage.push(LABEL_CONST.ETD2_OVER_ETA2);
                return errMessage;
            } else if (!ETDCompareETA(3, savePayload[i])) {
                errMessage.push(LABEL_CONST.ETD3_OVER_ETA3);
                return errMessage;
            }
        }
    };

    let modifyDataArry = [];
    
    const verifyValidationPassedEdit = (payloads) => {
        const payloadsUpdated = Object.values(payloads);
        let errMessage = [];

        const isObjectSame = payloadsUpdated.reduce((acc, curr) => {
            const dataBeforeUpdate = rows.find(r => r.idCount === curr.idCount);
            if (dataBeforeUpdate) {
                const trueOrFalse = deepEqual(dataBeforeUpdate, curr);
                acc.push(trueOrFalse);
                if (!trueOrFalse) {
                    modifyDataArry.push(curr);
                }
            } else {
                acc.push(false);
            }
            return acc;
        }, []);

        if (!payloadsUpdated.length || isObjectSame.every(f => f === true)) {
            errMessage.push(LABEL_CONST.NO_CHANGE_TO_SAVE)
            return errMessage;
        }
        return addOtherErr(modifyDataArry);
    };
    
    const catchBlockErr = (error) => {
        if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
            const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
            showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
        } else {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
        }
    };

    const saveInvGenData = (e) => {
        const newRowData = savePayload.filter(item => item.invGenFlag === "N");
        if (gridDataLength > 0) {
            if (newRowData.length !== 0) {
                //SAVE_API
                const isValidationPassed = verifyValidationPassedEdit(newRowData);
                if (isValidationPassed?.length) {
                    if (isValidationPassed.join("\n") !== LABEL_CONST.NO_CHANGE_TO_SAVE) {
                        let counter = 0;
                        showAlertMsg(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
                            return <p key={`erradd-${counter++}`}>{a}</p>;
                        }
                        ));
                    } else {
                        showAlertMsg(LABEL_CONST.INFORMATION, isValidationPassed.join("\n"));
                    }
                } else {
                    const newRowDataFinal = modifyDataArry.map(({ contDest, ...rest }) => rest); // remove contDest key from result
                    let payload = newRowDataFinal.map((e) => { e.companyCode = 'TMT'; return e; });

                    setIsLoading(true);
                    postRequest(MODULE_CONST.INVOICE_GENERATION.API_BASE_URL, MODULE_CONST.INVOICE_GENERATION.SAVE_API, payload).then(dataRes => {
                        setIsLoading(false);
                        showAlertMsg(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
                        getInvData(currPage, pageSize, requsetPayLoad);
                        setRefreshGrid(false);
                    }).catch(function (error) {
                        setIsLoading(false);
                        console.log('Add Error', error.response.data);
                        catchBlockErr(error);
                    });
                }
            } else {
                showAlertMsg(LABEL_CONST.ERROR, LABEL_CONST.MODIFY_RECORD);
                return false;
            }
        } else {
            showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.INFO_CM_3001);
            return false;
        }
    };

    // Table grid data
    const columns = [
        { id: 'invGenFlag', name: 'RowEdit', invGenFlag: true, action: false, editFlag: false },
        { id: 'dsiTiNo', name: 'DSI/TI(CPO/SPO)', editFlag: false },
        { id: 'issueInvoiceDate', name: 'Issue Invoice Date', date: true, editFlag: true },
        { id: 'originalEtd', name: 'Original ETD', editFlag: false },
        { id: 'contDest', name: 'Cont. Destination', editFlag: false },
        { id: 'etd1', name: 'ETD1', date: true, editFlag: true },
        { id: 'eta1', name: 'ETA1', date: true, editFlag: true },
        { id: 'cont20', name: "20'", editFlag: false },
        { id: 'cont40', name: "40'", editFlag: false },
        { id: 'renbanCode', name: 'RENBAN Code', editFlag: false },
        { id: 'liner', name: 'Ship. Comp.', editFlag: false },
        { id: 'broker', name: 'Custom Broker', select: true, editFlag: true, selectList: optionsBroker },
        { id: 'vessel1', name: 'Vessel 1', input: true, editFlag: true, maxLength: 30 },
        { id: 'voy1', name: 'Voy 1', input: true, editFlag: true, maxLength: 10 },
        { id: 'etd2', name: 'ETD2', date: true, editFlag: true },
        { id: 'eta2', name: 'ETA 2', date: true, editFlag: true },
        { id: 'vessel2', name: 'Vessel 2', input: true, editFlag: true, maxLength: 30 },
        { id: 'voy2', name: 'Voy 2', input: true, editFlag: true, maxLength: 10 },
        { id: 'etd3', name: 'ETD 3', date: true, editFlag: true },
        { id: 'eta3', name: 'ETA 3', date: true, editFlag: true },
        { id: 'vessel3', name: 'Vessel 3', input: true, editFlag: true, maxLength: 30 },
        { id: 'voy3', name: 'Voy 3', input: true, editFlag: true, maxLength: 10 },
        { id: 'bookingNo', name: 'Booking No.', editFlag: false },
        { id: 'folderName', name: 'Folder name (Inv Container Level)', input: true, editFlag: true, maxLength: 25 },
        { id: 'portOfLoading', name: 'Port of Loading', select: true, editFlag: true, selectList: optionsPort },
        { id: 'portOfDischarge', name: 'Port of Discharge', select: true, editFlag: true, selectList: optionsPort }
    ];

    //Alert Box messages
    const showAlertMsg = (title, content) => {
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
    };

    const checkfile = (sender) => {
        let validExts = new Array(".xlsx", ".xls");
        let fileExt = sender.name;
        let result = true;
        fileExt = fileExt.substring(fileExt.lastIndexOf('.'));
        if (validExts.indexOf(fileExt) < 0) {
            result = false;
        }
        return result;
    };

    const uploadClick = () => {
        if (uploadFile === "" || uploadFile === undefined) {
            showAlertMsg(LABEL_CONST.ERROR, LABEL_CONST.ERR_FILE_NOT_SELECTED);
        } else if (!checkfile(uploadFile)) {
            showAlertMsg(LABEL_CONST.ERROR, LABEL_CONST.PLEASE_SELECT_XLSX_FILE_TO_UPLOAD);
        } else {
            uploadFileApi();
        }
    };

    const msgTpye = (value) => {
        let label = LABEL_CONST.INFORMATION;
        if (value.split('_')[0] === 'ERR') {
            label = LABEL_CONST.ERROR;
        } else if (value.split('_')[0] === 'INFO') {
            label = LABEL_CONST.INFORMATION;
        }
        return label;
    };

    const uploadFileApi = () => {
        const formData = new FormData();
        const url = `${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL}${MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.UPLOAD_INVOICE_GENERATION_API}&userId=TestUser`;
        formData.append("file", uploadFile);
        const headers = {
            "Content-Type": "multipart/form-data",
        };
        setIsLoading(true);
        axios
            .post(url, formData, headers)
            .then((res) => {
                console.log(res);
                setIsLoading(false);
                if (res.data.statusMessage === 'INFO_AD_4002' || res.data.statusMessage === 'ERR_AD_4003') {
                    setProcessName("Upload Work Plan Master");
                } else {
                    setProcessName(null);
                }
                showAlertMsg(msgTpye(res.data.statusMessage), createMessage(res.data.statusMessage));
                setRowsFilter([]);
                setUploadFile('');
                document.getElementById('formFile').value = '';
            })
            .catch((e) => {
                setIsLoading(false);
                setProcessName(null);
                setRowsFilter([]);
                setUploadFile('');
                document.getElementById('formFile').value = '';
                if (e.response.data.statusCode === 417) {
                    showAlertMsg(LABEL_CONST.ERROR, createMesssageReplacer({ errorCode: e.response.data.errorMessageParams.errorCode }, e.response.data.statusMessage));
                } else {
                    catchBlockErr(e);
                }
            });
    };

    const handleNavigate = async (event) => {
        event.preventDefault();
        navigate("/inquiry-for-process-status", {
            state: { processName: processName },
        });
    };

    useEffect(() => {
        getDestination();
    }, []);

    useEffect(() => {
        setTimeout(function () {
            setIsLoading(false);
        }, 2000);
    }, []);
    
    useEffect(() => {

        setReqPara({
            issueInvoiceFromDate: dateInvoiceFrom ? formatedDate(dateInvoiceFrom) : null,
            issueInvoiceToDate: dateInvoiceTo ? formatedDate(dateInvoiceTo) : null,
            etd1fromDate: dateEtdFrom ? formatedDate(dateEtdFrom) : null,
            etd1ToDate: dateEtdTo ? formatedDate(dateEtdTo) : null,
            contDest: getDestinationValue(selectedDest)
        })
    }, [dateInvoiceFrom, dateInvoiceTo, dateEtdFrom, dateEtdTo, selectedDest]);

    useEffect(() => {
        if (!chkRegular && chkCPO) {
            const database = rows.filter(data => data.dsiTiNo !== null);
            setRowsFilter(database);
        }
        if (!chkCPO && chkRegular) {
            const database = rows.filter(data => data.dsiTiNo === null);
            setRowsFilter(database);
        }
        if (chkRegular && chkCPO) {
            setRowsFilter(rows);
        }
        if (!chkRegular && !chkCPO) {
            setRowsFilter([]);
        }
    }, [chkRegular, chkCPO, rows]);

    return (
        <>
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">

                    {/* Breadcrumb starts*/}
                    <TpexBreadCrumb name='Invoice Generation Work Plan Master' />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="col">
                                <div className="heading"><i className="bg-border"></i><h1>Invoice Generation Work Plan Master</h1></div>
                            </div>
                            <form>
                                <div className="row">
                                    <div className="col-10 g-0 panel-scroll-container">
                                        <div className="panel-scroll-wrapper">
                                            <div className="row mt-10">
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker mandatoryControl">
                                                        <label>Issue Invoice Date From</label>
                                                        <TpexDatePicker
                                                            dateSelected={dateInvoiceFrom}
                                                            handleDateSelected={date => setInvDateFrom(date)}
                                                            isDirectDatePicker={true}
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker">
                                                        <label>Issue Invoice Date To</label>
                                                        <TpexDatePicker
                                                            dateSelected={dateInvoiceTo}
                                                            handleDateSelected={date => setInvDateTo(date)}
                                                            isDirectDatePicker={true}
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker mandatoryControl">
                                                        <label>ETD Date From</label>
                                                        <TpexDatePicker
                                                            dateSelectedselected={dateEtdFrom}
                                                            handleDateSelected={date => setInvEtdDateFrom(date)}
                                                            isDirectDatePicker={true}
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker">
                                                        <label>ETD Date To</label>
                                                        <TpexDatePicker
                                                            dateSelected={dateEtdTo}
                                                            handleDateSelected={date => setInvEtdDateTo(date)}
                                                            isDirectDatePicker={true}
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="row mt-10 pb-10">
                                                <div className="form-group">
                                                    <div className="custom-multiSelect">
                                                        <label>Select Destination</label>
                                                        <TpexMultiSelectSeach
                                                            handleSelectedOptions={handleSelectedOptions}
                                                            name="destination_select"
                                                            noOptionsText="Select Destination"
                                                            value={selectedDest}
                                                            staticValues={options}   // required when server side is false
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="form-group col-2 align-self-end">
                                        <div className="d-flex justify-content-end pb-10">
                                            {/* <TpexSimpleButton color="refresh" text="" /> */}
                                            {/* <TpexSimpleButton color="primary" text="Search" /> */}
                                            <button type="button" className="btn btn-primary ms-3" onClick={searchData}>Search</button>
                                        </div>
                                    </div>

                                </div>
                            </form>
                        </div>
                    </div>
                    <div className="panelBox mt-10">
                        <div className="grid-panel">
                            <div className="row g-0">
                                <div className="col-6">
                                    <div className="order-type">Order Type:</div>
                                    <div className="form-check form-check-inline">
                                        <input className="form-check-input" type="checkbox" value="" id="flexCheckDefault" disabled={chkBoxDis} checked={chkRegular} onChange={e => handleRegularChange(e, 'checkbox1')} />
                                        <label className="form-check-label" htmlFor="flexCheckDefault">
                                            Regular
                                        </label>
                                    </div>
                                    <div className="form-check form-check-inline">
                                        <input className="form-check-input" type="checkbox" value="" id="flexCheckChecked" disabled={chkBoxDis} checked={chkCPO} onChange={e => handleRegularChange(e, 'checkbox2')} />
                                        <label className="form-check-label" htmlFor="flexCheckChecked">
                                            CPO/SPO
                                        </label>
                                    </div>
                                </div>
                            </div>

                            <TpexTableNew rows={rowsFilter} idName="idCount" simpletable={false}
                                rowPerPage={pageSize} selectAll={false} columns={columns} pagination={true} isCrud={true}
                                totalPagesServer={pageSize} currentServerPage={currentPage} totalItemsServer={pageSizeTotal}
                                currentPageServer={currPage}
                                onPaginationClick={handlePageClickServer} margin="gridTable"
                                setClickSave={setSavePayload} refreshGridSave={refreshGrid} freezetable="tableinvoice" />
                            {/* </div> */}
                            <div className="gridfooter">
                                <div className="row">
                                    <div className="col g-0">
                                        <div className="heading"><i className="bg-border"></i><h1>Upload Download Action</h1></div>
                                    </div>
                                </div>
                            </div>
                            <div className="row mt-10">
                                <div className="form-group col-4">
                                    <div className="input-group custom-file-button">
                                        <input className={uploadFile === '' ? "form-control" : "form-control normalText"} type="file" id="formFile" onChange={(e) => setUploadFile(e.target.files[0])} name="file" />
                                        <label className="input-group-text" htmlFor="formFile">Browse</label>
                                    </div>
                                </div>
                                <div className="form-group col-5 align-self-center">
                                    <TpexSimpleButton color="outline-primary" leftmargin="3" text="Upload" handleClick={uploadClick} />
                                    {processName && (<a href="/" className="errLink" color="outline-primary" onClick={handleNavigate}>{LABEL_CONST.PROCESS_STATUS_ERROR_MESS}</a>)}
                                    <button type="button" className="btn btn-outline-primary ms-3" onClick={downloadGridData}>Download</button>
                                </div>
                                <div className="form-group col-3 align-self-center">
                                    <div className="d-flex justify-content-end">
                                        <button type="button" className="btn btn-primary" onClick={saveInvGenData}>Save</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <AlertModal
                    show={alertModalShow}
                    onHide={() => setAlertModalShow(false)}
                    status={alertTitle}
                    content={alertContent}
                />
            </main>
        </>
    );
};

export default InvoiceGenWorkPlanMaster;