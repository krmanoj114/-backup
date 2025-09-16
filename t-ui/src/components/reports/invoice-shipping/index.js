import React, { useState, useEffect } from 'react';
import './report.css';
import "react-datepicker/dist/react-datepicker.css";
import TpexSimpleButton from '../../../common/components/button';
import { TpexMultiSelectSeach } from '../../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../../constants/label.constant.en';
import { ADMIN_SERVICE, MIME_TYPE, MODULE_CONST } from '../../../constants/constant';
import { getRequest } from '../../../services/axios-client';
import { copyObject, createMessage, createMesssageReplacer, formatedDate } from '../../../helpers/util';
import { TpexDatePicker } from '../../../common/components/datepicker/datepicker';
import { TpexBreadCrumb } from '../../../common/components/breadcrumb/breadcrumb';
import { TpexLoader } from '../../../common/components/loader/loader';
import AlertModal from "../../../common/components/alert-modal/alert-modal";

const InvoiceShippingReports = () => {
    const [dateSelected, setDateSelected] = useState(null);
    const [orderTypeValue, setOrderTypeValue] = useState("R");
    const [destinationList, setDestinationList] = useState([]);
    const [selectedOption, setSelectedOption] = useState([]);
    const [reportTypesList, setReportTypesList] = useState([]);
    const [reportTypes, setReportTypes] = useState([]);
    const [reportGroupSelected, setReportGroupSelected] = useState(-1);
    const [invoiceNumberValue, setInvoiceNumberValue] = useState("");
    const [bookingNumberValue, setBookingNumberValue] = useState("");
    const [isLoading, setIsLoading] = useState(true);
    const [modalShowAlert, setModalShowAlert] = useState(false);
    const [messageType, setMessageType] = useState();
    const [messageText, setMessageText] = useState();
    const [colWidth, setColWidth] = useState(6);

    const group2Reports = [
        'RINS002DG',
        'RINS104_DG',
        'RINS105',
        'RINS110'
    ];

    let index1 = 0;

    function getReportTypesAndDestination() {
        setIsLoading(true);
        getRequest(MODULE_CONST.INVOICE_SHIPPING_REPORT.API_BASE_URL, MODULE_CONST.INVOICE_SHIPPING_REPORT.REPORT_TYPES_DESTINATION).then(dataRes => {
            const reportTypesWithFlag = dataRes.data.reports.map(r => {
                return {
                    ...r,
                    checked: false
                }
            })
            if (reportTypesWithFlag.length > 12 && reportTypesWithFlag.length <= 18) {
                setColWidth(4)
            } else if (reportTypesWithFlag.length > 18) {
                setColWidth(3)
            } else {
                setColWidth(6)
            }
            setReportTypesList(reportTypesWithFlag);

            setDestinationList(dataRes.data.destinations.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + ' - ' + d.fdDstNm
                }
            }))

            setIsLoading(false);

        }).catch(function (error) {
            if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
                const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
                openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
            } else {
                openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
            }
            console.log('getReportTypes =>', error);
            setIsLoading(false);
        });
    }

    const handleReportTypeChange = (e) => {
        const reportTypeName = e.target.name;
        const reportTypChecked = e.target.checked;
        const reportTypesCopy = copyObject(reportTypes);
        let errorCase = false;
     
        if (group2Reports.includes(reportTypeName) && reportTypChecked === true) {
            const other_selected = reportTypesCopy.filter(rt => !group2Reports.includes(rt));
            if (other_selected.length > 0) {
                errorCase = true;
                openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.OTHER_GROUP_SELECTION_ERR);
            }
        }
        if (!group2Reports.includes(reportTypeName) && reportTypChecked === true) {
            const group2_report_exist = reportTypesCopy.filter(rt => group2Reports.includes(rt));
            if (group2_report_exist.length > 0) {
                errorCase = true;
                openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.OTHER_GROUP_SELECTION_ERR);
            }
        }

        excludeIncludeReportType(errorCase, reportTypChecked, reportTypesCopy, reportTypeName);       
    };

    function excludeIncludeReportType(
        errorCase,
        reportTypChecked,
        reportTypesCopy,
        reportTypeName
    ) {
        if (errorCase === false) {
            if (reportTypChecked) {
                !reportTypesCopy.includes(reportTypeName) && reportTypesCopy.push(reportTypeName);
                setReportTypes([...reportTypesCopy]);
            } else {
                const reportTypeFilter = reportTypesCopy.filter(r => r !== reportTypeName)
                setReportTypes([...reportTypeFilter]);
            }

            // check / uncheck only if no error
            const reportTypesListCopy = copyObject(reportTypesList).map(rt => {
                if (String(rt.reportTypeId) === String(reportTypeName)) {
                    return {
                        ...rt,
                        checked: reportTypChecked
                    }
                }
                return rt;
            });
            setReportTypesList([...reportTypesListCopy]);
        }
    }

    function handleDateSelected(d) {
        const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
        if (!d || (d && !formatedDate(d).match(dateReg))) {
            setDateSelected("")
        } else {
            setDateSelected(d);
        }
    }

    function setInvoiceNumber(e) {
        setInvoiceNumberValue(e.target.value);
    }

    function setBookingNumber(e) {
        setBookingNumberValue(e.target.value);
    }

    function handleValidation() {
        if (!reportTypes.length) {
            return false;
        }
        if (reportGroupSelected === 2) {
            // ETD & Destination mandatory, bookin no optional
            if (!dateSelected || !selectedOption || Object.keys(selectedOption).length === 0) {
                return false;
            }
        } else {
            // Invoice mandatory
            if (!invoiceNumberValue) {
                return false;
            }
        }
        return true;
    }

    const handlePrintPdf = () => {
        setIsLoading(true);
        reportTypes.forEach(report => {
                        const payload = {
                orderType: orderTypeValue,
                invoiceNumber: invoiceNumberValue,
                reportTypes: [report],
                bookingNo: bookingNumberValue,
                etd: dateSelected===null?"":formatedDate(dateSelected),
                etdTo: "",
                destinations: selectedOption.value ? [selectedOption.value] : [],
                userId: "TestUser",
            }
            const reportData = reportTypesList.find(r => r.reportTypeId === report);
            let fileNameToDownload = "";
            let fileName = ""
            let fileExtention = ""
            fetch(MODULE_CONST.INVOICE_SHIPPING_REPORT.API_BASE_URL_REPORT + MODULE_CONST.INVOICE_SHIPPING_REPORT.REPORT_PATH, {
                method: 'POST',
                headers: {
                    'Accept': '*/*',
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(payload)
            }).then((response) => {
                fileNameToDownload = response.headers.get("filename") || ""
                fileName = fileNameToDownload.split(".")
                fileExtention = fileName[1];
                return response.headers.get("Content-Type") === 'application/json' ? response.json() : response.blob() 
            }).then(
                data => {
                
                    if (fileExtention ==='pdf') {
                            let link = document.createElement("a");
                            link.href = URL.createObjectURL(
                                new Blob([data], { type: MIME_TYPE["pdf"] })
                            );
                            link.download = fileNameToDownload;
                            let dateToSend = dateSelected===null?"":formatedDate(dateSelected);
                            window.open("/report/view?file=" + link + "&pdf=" + reportData.isPdf + "&xlsx=" + reportData.isExcel + "&orderType=" + orderTypeValue + "&invoiceNumber=" + invoiceNumberValue + "&reportTypes=" + report + "&reportName=" + fileNameToDownload + "&etd=" + dateToSend + "&destinations=" + (selectedOption.value ? selectedOption.value : "") + "&bookingNo=" + bookingNumberValue, '_blank');
                    }
                        
                       else if(fileExtention === "xlsx") {
                        let link = document.createElement("a");
                        link.href = URL.createObjectURL(
                            new Blob([data], { type: MIME_TYPE["xlsx"] })
                        );
                        link.download = fileNameToDownload;
                        link.click()
                       }
                       
                    else {
                        openAlertBox(LABEL_CONST.ERROR, createMessage(data.exception));
                    }
                }
            ).catch(function (error) {
                console.log('error handlePrintPdf', error)
                openAlertBox(LABEL_CONST.ERROR, createMessage(error.message));
            }).finally(() => {
                setIsLoading(false);
            });


        })
    }

    const handleClick = (e) => {
        e.preventDefault();
        setIsLoading(true);
        if (handleValidation()) {
            setIsLoading(false);
            handlePrintPdf();
        } else {
            setIsLoading(false);
            openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }
    }

    function handleSelectedOptions(e) {
        setSelectedOption(e);
    }

    const handleAlertConfirmation = () => {
        console.log('in handleAlertConfirmation')
    };

    function openAlertBox(messegeType = LABEL_CONST.WARNING, messageText = "") {
        setMessageType(messegeType);
        setMessageText(messageText);
        setModalShowAlert(true)
    };

    useEffect(() => {
        getReportTypesAndDestination();
    }, []);

    useEffect(() => {
        if (reportTypes.length) {
            group2Reports.includes(reportTypes[0]) ?
                setReportGroupSelected(2) :
                setReportGroupSelected(1);
        } else {
            setReportGroupSelected(-1)
        }
        // eslint-disable-next-line
    }, [reportTypes]);

    useEffect(() => {
        if (reportGroupSelected === 2) {
            // clean invoice field
            setInvoiceNumberValue("");
        } else if (reportGroupSelected === 1) {
            // clean etd and destinatin, booking field
            setDateSelected(null);
            setSelectedOption([]);
            setBookingNumberValue("");
        }
    }, [reportGroupSelected]);

    useEffect(()=>{ },[orderTypeValue]);

    return (
        <>
          <TpexLoader isLoading={isLoading} />   
          
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.INVOICE_SHIPPING_REPORT} />
                    <div className="panelBox pb-10">
                        <div className="search-panel invoice-shipping-report">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.INVOICE_SHIPPING_REPORT}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10">
                                    <div className="col">
                                        <span className="align-middle order-type">{LABEL_CONST.ORDER_TYPE}:</span>
                                        <div className="form-check form-check-inline">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="orderType"
                                                id="R"
                                                value="R"
                                                checked={orderTypeValue === "R"}
                                                onChange={()=>{setOrderTypeValue("R")}}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="regular">{LABEL_CONST.REGULAR}</label>
                                        </div>
                                        <div className="form-check form-check-inline ms-5">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="orderType"
                                                id="C"
                                                value="C"
                                                checked={orderTypeValue === "C"}
                                                onChange={()=>{setOrderTypeValue("C")}}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="cpo">{LABEL_CONST.CPO}</label>
                                        </div>
                                        <div className="form-check form-check-inline ms-5">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="orderType"
                                                id="S"
                                                value="S"
                                                checked={orderTypeValue === "S"}
                                                onChange={()=>{setOrderTypeValue("S")}}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="spo">{LABEL_CONST.SPO}</label>
                                        </div>
                                    </div>
                                </div>
                                {/* d-none   css to hide */}
                                <div className="row mt-20">
                                    <div className={`form-group col-3 ${reportGroupSelected === 2 ? "d-none" : ""}`}>
                                        <div className="mandatoryControl">
                                            <label htmlFor="invoiceNumber">{LABEL_CONST.INVOICE_NUMBER}</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="invoiceNumber"
                                                name="invoiceNumber"
                                                value={invoiceNumberValue}
                                                minLength="1"
                                                maxLength="10"
                                                onChange={setInvoiceNumber}
                                            />
                                        </div>
                                    </div>

                                    <div className={`col-2 ${reportGroupSelected === 1 ? "d-none" : ""}`}>
                                        <div className="customDatePicker mandatoryControl ">
                                            <label htmlFor="etd">{LABEL_CONST.ETD}</label>
                                            <TpexDatePicker
                                                dateFormat="dd/MM/yyyy"
                                                dateSelected={dateSelected}
                                                id="etd"
                                                handleDateSelected={handleDateSelected}
                                            />
                                        </div>
                                    </div>

                                    <div className={`col-3 ${reportGroupSelected === 1 ? "d-none" : ""}`}>
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="destination">{LABEL_CONST.DESTINATION}</label>
                                            <TpexMultiSelectSeach
                                                searchUrl={''}
                                                handleSelectedOptions={handleSelectedOptions}
                                                name="destination"
                                                noOptionsText="Search..."
                                                value={selectedOption}
                                                isMulti={false}
                                                serverSide={false}
                                                staticValues={destinationList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    
                                    <div className={`form-group col-3 ${reportGroupSelected === 1 ? "d-none" : ""}`}>
                                        <div className="">
                                            <label htmlFor="bookingNumber">{LABEL_CONST.BOOKING_NO}</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="bookingNumber"
                                                name="bookingNumber"
                                                value={bookingNumberValue}
                                                minLength="1"
                                                maxLength="15"
                                                onChange={setBookingNumber}
                                            />
                                        </div>
                                    </div>
                                </div>

                                <div className="row mt-4">
                                    {/* field set start */}
                                    <div className="col-11">
                                        <fieldset className="scheduler-border">
                                            <legend className="scheduler-border order-type">{LABEL_CONST.REPORT_TYPE}</legend>
                                            {/* main div */}
                                            <div className="row">
                                                {/* first row  */}
                                                <div key="report-type-first-col" className={`col-${colWidth} report-type-checkboxes`}>
                                                    {reportTypesList.length > 0 ?
                                                        reportTypesList.slice(0, 6).map((item, index) => {
                                                            return <div key={`report-type-first-col-${index1++}`} className="form-check">
                                                                <input
                                                                    className="form-check-input"
                                                                    type="checkbox"
                                                                    name={item.reportTypeId}
                                                                    id={`rtype-${item.reportTypeId}`}
                                                                    onChange={handleReportTypeChange}
                                                                    checked={item.checked}
                                                                />
                                                                <label className="form-check-label report-type-label" htmlFor={`rtype-${item.reportTypeId}`}>
                                                                    {item.reportTypeName}
                                                                </label>
                                                            </div>
                                                        })
                                                        : ''
                                                    }
                                                </div>
                                                {/* 2nd col */}
                                                <div key="report-type-second-col" className={`col-${colWidth} report-type-checkboxes`}>
                                                    {reportTypesList.length > 6 ?
                                                        reportTypesList.slice(6, 12).map((item, index) => {
                                                            return <div key={`report-type-second-col-${index1++}`} className="form-check">
                                                                <input
                                                                    className="form-check-input"
                                                                    type="checkbox"
                                                                    name={item.reportTypeId}
                                                                    id={`rtype-${item.reportTypeId}`}
                                                                    onChange={handleReportTypeChange}
                                                                    checked={item.checked}
                                                                />
                                                                <label className="form-check-label report-type-label" htmlFor={`rtype-${item.reportTypeId}`}>
                                                                    {item.reportTypeName}
                                                                </label>
                                                            </div>
                                                        })
                                                        : ''
                                                    }
                                                </div>
                                                 {/* 3rd col */}
                                                 <div key="report-type-3-col" className={`col-${colWidth} report-type-checkboxes`}>
                                                    {reportTypesList.length > 12 ?
                                                        reportTypesList.slice(12, 18).map((item, index) => {
                                                            return <div key={`report-type-3-col-${index1++}`} className="form-check">
                                                                <input
                                                                    className="form-check-input"
                                                                    type="checkbox"
                                                                    name={item.reportTypeId}
                                                                    id={`rtype-${item.reportTypeId}`}
                                                                    onChange={handleReportTypeChange}
                                                                    checked={item.checked}
                                                                />
                                                                <label className="form-check-label report-type-label" htmlFor={`rtype-${item.reportTypeId}`}>
                                                                    {item.reportTypeName}
                                                                </label>
                                                            </div>
                                                        })
                                                        : ''
                                                    }
                                                </div>
                                                 {/* 4th col */}
                                                 <div key="report-type-4-col" className={`col-${colWidth} report-type-checkboxes`}>
                                                    {reportTypesList.length > 18 ?
                                                        reportTypesList.slice(18, 24).map((item, index) => {
                                                            return <div key={`report-type-4-col-${index1++}`} className="form-check">
                                                                <input
                                                                    className="form-check-input"
                                                                    type="checkbox"
                                                                    name={item.reportTypeId}
                                                                    id={`rtype-${item.reportTypeId}`}
                                                                    onChange={handleReportTypeChange}
                                                                    checked={item.checked}
                                                                />
                                                                <label className="form-check-label report-type-label" htmlFor={`rtype-${item.reportTypeId}`}>
                                                                    {item.reportTypeName}
                                                                </label>
                                                            </div>
                                                        })
                                                        : ''
                                                    }
                                                </div>
                                            </div>
                                            {/* main div end */}
                                        </fieldset>
                                    </div>
                                    {/* fieldset end */}
                                </div>

                                <div className="row">
                                    <div className="col text-end">
                                        <TpexSimpleButton disabled={orderTypeValue === "R" ? "" : "disabled"} color="primary" text={LABEL_CONST.PRINT} handleClick={handleClick} topmargin="4" />
                                    </div>
                                </div>
                            </form>
                          
                        </div>
                    </div>
         
                </div>
     
                {/* alert modal  */}
                <AlertModal
                    handleClick={handleAlertConfirmation}
                    show={modalShowAlert}
                    onHide={() => setModalShowAlert(false)}
                    status={messageType}
                    content={messageText}
                />
            </main>
        
        </>
    );
};

export default InvoiceShippingReports