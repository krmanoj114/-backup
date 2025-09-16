import React, { useState, useRef } from 'react';
import "./vesselBookingMaster.css";
import { v4 as uuidv4 } from 'uuid';

import { getRequest, postRequest } from '../../services/axios-client';
import { ADMIN_SERVICE, MODULE_CONST } from '../../constants/constant';
import { TpexLoader } from '../../common/components/loader/loader';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import TpexTable from './TpexTableVMS';
import TpexSimpleButton from '../../common/components/button';
import { formatedDate, formatedDate_yyyymm, createMessage } from '../../helpers/util';
import AlertModal from "../../common/components/alert-modal/alert-modal";
import useGetRequest from "../../hook/useRequest";
import { saveAs } from 'file-saver';
import EditDataContext from './EditDataContext';

const VesselBookingMaster = () => {

    const [effectiveFromVanningMonth, setEffectiveFromVanningMonth] = useState(null);
    const [destination, setDestination] = useState("");
    const [dateEtdFrom, setInvEtdDateFrom] = useState(null);
    const [dateEtdTo, setInvEtdDateTo] = useState(null);
    const [shippingComp, setShippingComp] = useState("");
    const [modalShow, setModalShow] = useState(false);
    const [statusTitle, setStatusTitle] = useState("");
    const [statusContent, setstatusContent] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [rows, setRows] = useState([]);
    const [customBrokerMaster, setCustomBrokerMaster] = useState([]);
    const [actionCase, setActionCase] = useState("");
    const [saveCaseData, setSaveCaseData] = useState([]);

    const editDataContextRef = useRef([]);

    const downloadURL = MODULE_CONST.VESSEL_BOOKING_MASTER.API_BASE_URL + MODULE_CONST.VESSEL_BOOKING_MASTER.DOWNLOAD_API;
    const userId = "LoginUser";

    const reqPayload = {
        "requestBody" : {
            vanningMonth : formatedDate_yyyymm(effectiveFromVanningMonth),
            etdFrom: (dateEtdFrom !== null) ? formatedDate(dateEtdFrom) : "",
            etdTo: (dateEtdTo !== null) ? formatedDate(dateEtdTo) : "",
            shippingCompanyCode: (shippingComp !== null) ? shippingComp.value : "",
            destinationCode : (destination !== null ? destination.value : null)
        }
    };

    const reqDownloadPayLoad = {
        vanningMonth: formatedDate_yyyymm(effectiveFromVanningMonth),
        etdFrom: (dateEtdFrom !== null) ? formatedDate(dateEtdFrom) : "",
        etdTo: (dateEtdTo !== null) ? formatedDate(dateEtdTo) : "",
        shippingCompanyCode: (shippingComp !== null) ? shippingComp.value : "",
        destinationCode: (destination !== null ? destination.value : null)
    };

    /*
     @@@ Destination + Shiping Company API //Start
    */

    const getAllRequests = useGetRequest(
        () => getRequest(
            MODULE_CONST.VESSEL_BOOKING_MASTER.API_BASE_URL,
            MODULE_CONST.VESSEL_BOOKING_MASTER.VESSEL_BOOKING_DEST
        )
    );

    const initialisedValue = (data) => {
        return {
            finalDestination: data?.finalDestination || [],
            shippingCompany: data?.shippingCompany || []
        }
    };

    const requestData = initialisedValue(getAllRequests?.data?.data);

    const destinationList = requestData.finalDestination.map((d) => {
        return {
            value: d.fdDstCd,
            label: d.fdDstCd + '-' + d.fdDstNm
        }
    });

    const shipingCompList = requestData.shippingCompany.map((s) => {
        return {
            value: s.scmCd,
            label: s.scmCd
        }
    });

    const customBrokerSearchDisplay = rows.map(customBroker => {
        const { customBrokerCode, customBrokerName } = customBroker;
        let customBrokerSearchDisplay = '';

        if (customBrokerCode !== null && customBrokerName !== null) {
            customBrokerSearchDisplay = `${customBrokerCode} ${customBrokerName}`;
        }

        return customBrokerSearchDisplay;
    });

    const customBrokerMList = customBrokerMaster.map(customBroker => (
        {
            id: customBroker.customBrokerCode,
            name: customBroker.customBrokerDisplay
        }
    ));

    // Table grid data
    const columns = [

        { id: 2, name: '', group: true },
        { id: 3, name: '', group: true },
        { id: 4, name: '', group: true },
        { id: 5, name: '', group: true },
        { id: 1, name: 'No. of Container', group: true },
        { id: 6, name: '', group: true },
        { id: 7, name: '', group: true },
        { id: 8, name: '', group: true },
        { id: 9, name: '', group: true },
        { id: 10, name: '', group: true },
        { id: 11, name: '', group: true }
    ];

    const containerColumns = [
        { id: 'destinationCode', name: 'Destination Code' },
        { id: 'etd1', name: 'ETD1', date: true },
        { id: 'finalEta', name: 'Final ETA', date: true },
        { id: 'renbanCode', name: 'RENBAN Code' },
        { id: 'noOfContainer20ft', name: "20'", noOfContainer20ft: false },
        { id: 'noOfContainer40ft', name: "40'", noOfContainer40ft: false },
        { id: 'shippingCompany', name: 'Ship. Comp.' },
        {
            id: 'customBrokerName',
            name: 'Custom Broker',
            type: 'select',
            customBrokerList: customBrokerSearchDisplay,
            customBrokerSelectList: customBrokerMList
        },
        {
            id: 'bookingNo',
            name: 'Booking No.',
            type: 'input'
        },
        {
            id: 'vessel1',
            name: 'Vessel 1',
            type: 'input'
        },

        { id: 'bookingStatus', name: 'Booking Status' },
    ];

    const showStatusMsg = (title, content, type) => {
        if (type) {
            setActionCase(type);
        }
        setStatusTitle(title);
        setstatusContent(content);
        setModalShow(true);
    };

    /*
     @@@ Destination + Shiping Company API //End
    */

    /*
     @@@ Date picker Method //Start
    */
    const handleDateSelected = (d, name) => {
        if (name === "effectiveFromVanningMonth") {
            setEffectiveFromVanningMonth(d)
        }
    }
    /*
     @@@ Date picker Method //End
    */

    const handleSelectedOptions = (e, name) => {
        if (name === "destination") {
            setDestination(e);
        }
    }

    const selectBoxChange = (e) => {
        setShippingComp(e);
    };

    const etdDateFrom = (e) => {
        setInvEtdDateFrom(e);
    };

    const etdDateTo = (e) => {
        setInvEtdDateTo(e);
    };

    const handleValidation = () => {
        if (effectiveFromVanningMonth !== null) {
            return true;
        }
        setIsLoading(false);
        return false;
    };

    const checkIfAllowedToEdit = rowData => {
        return (
            (rowData['noOfContainer20ft'] === "0" && rowData['noOfContainer40ft'] === "0") ?
                false :
                !rowData.etdPassMonth
        );
    };

    const setAllRowsData = responseData => {
        let addIdRows = responseData.map(rowData => {
            let rowObjToSend = {
                ...rowData,
                id: uuidv4(),
                editable: false,
                allowedToEdit: checkIfAllowedToEdit(rowData),
                vessel1: (
                    rowData['noOfContainer20ft'] === "0" && rowData['noOfContainer40ft'] === "0"
                ) ? 'Cancelled' : rowData['vessel1'],
                customBrokerCode: rowData['customBrokerCode'] ? rowData['customBrokerCode'] : '',
                customBrokerName: rowData['customBrokerName'] ? rowData['customBrokerName'] : ''
            }

            return rowObjToSend;
        });

        setRows(addIdRows);
    };

    const searchDataHandler = () => {
        if (handleValidation()) {
            setIsLoading(true);
            postRequest(
                MODULE_CONST.VESSEL_BOOKING_MASTER.API_BASE_URL,
                MODULE_CONST.VESSEL_BOOKING_MASTER.SEARCH_API,
                reqPayload
            ).then(response => {
                if (response.data.responseBody.data.length > 0) {
                    setAllRowsData(response.data.responseBody.data);
                } else {
                    setRows([]);
                    showStatusMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                }

                if (response.data.responseBody.customBrokerMaster.length > 0) {

                    setCustomBrokerMaster([
                        { customBrokerCode: '', customBrokerDisplay: '' },
                        ...response.data.responseBody.customBrokerMaster
                    ]);
                } else {
                    setCustomBrokerMaster([]);
                }
                setIsLoading(false);
            }).catch(function (error) {
                let errorCode = error.response.data["requestBody.vanningMonth"] || error.response.data["requestBody.etdFrom"];
                showStatusMsg(LABEL_CONST.ERROR, createMessage(`${errorCode}`));
                setIsLoading(false);
            });
        } else {
            showStatusMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }
    };

    /*
        @@@ Download Method //Start
    */
    const downloadTableGridData = async () => {
        setIsLoading(true);
        fetch(downloadURL, {
            method: 'POST',
            headers: {
                'Accept': '*/*',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(reqDownloadPayLoad)
        }).then((response) => {
            if (response.headers.get("Content-Type") === 'application/json') {
                return response.json();
            } else {
                return response.blob();
            }
        }).then(
            (data) => {
                if (data.type === 'application/octet-stream') {
                    saveAs(data, 'VesselBookingMaster.zip');
                    setIsLoading(false);
                } else {
                    showStatusMsg(LABEL_CONST.INFORMATION, createMessage(data.message));
                }
                setIsLoading(false);
            }
        ).catch(function (error) {
            showStatusMsg(LABEL_CONST.ERROR, LABEL_CONST[error.response.data.vanningMonth]);
        });
    };

    const downloadTableData = (e) => {
        if (rows.length > 0) {
            downloadTableGridData();
        } else {
            showStatusMsg(LABEL_CONST.INFORMATION, LABEL_CONST.NO_RECORD_FOR_DOWNLOAD);
            return false;
        }
    };

    const saveCaseRecord = saveCaseData => {
        setIsLoading(true);
        postRequest(
            MODULE_CONST.VESSEL_BOOKING_MASTER.API_BASE_URL,
            MODULE_CONST.VESSEL_BOOKING_MASTER.SAVE_API,
            saveCaseData
        ).then(res => {
            editDataContextRef.current = [];
            searchDataHandler();
            setIsLoading(false);
            showStatusMsg(LABEL_CONST.INFORMATION, LABEL_CONST[res.data.message]);
        }).catch(err => {
            setIsLoading(false);
            showStatusMsg(LABEL_CONST.ERROR, createMessage(LABEL_CONST.ERR_IN_1004));
        });
    };

    const handleAlertConfirmation = (e) => {
        setModalShow(false);

        if (e.target.title === "saveRecordCase-Warning") {
            saveCaseRecord(saveCaseData);
        }
    };

    const areBookingNumbersValid = arrayObj => {
        const originalBookingNumbers = arrayObj.map(arrayItemObj => arrayItemObj.bookingNo);

        const uniqueBookingNumbers = [...new Set([...originalBookingNumbers])];

        return originalBookingNumbers.length === uniqueBookingNumbers.length;
    };

    const isBookingNumbersExists = arrayObj => {

        let isAlreadyExisting = false;

        const originalBookingNumbers = arrayObj.map(arrayItemObj => arrayItemObj.bookingNo);


        originalBookingNumbers.forEach(bookingNumber => {

            if (bookingNumber !== '') {
                const foundBookingNumberObj = rows.find(record => (
                    record.editable === false &&
                    record.bookingNo === bookingNumber
                ));

                isAlreadyExisting = foundBookingNumberObj ? true : false;
            }
        });

        return isAlreadyExisting;
    };

    const showPopupWhileNoData = rows => {
        if (rows.length === 0) {
            showStatusMsg(
                LABEL_CONST.ERROR,
                createMessage(LABEL_CONST.SEARCH_HAS_NOT_BEEN_PERFORMED)
            );
        } else {

            const anyEditable = rows.find(row => row.editable === true);

            if (anyEditable) {
                showStatusMsg(
                    LABEL_CONST.INFORMATION,
                    createMessage(LABEL_CONST.NO_CHANGES_TO_SAVE)
                );
            } else {

                const cancelledAndUpdatedRows = rows.filter(row => row.update === true);

                if (cancelledAndUpdatedRows.length > 0) {
                    setSaveCaseData(cancelledAndUpdatedRows);
                    showStatusMsg(
                        LABEL_CONST.WARNING,
                        createMessage(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES),
                        'saveRecordCase'
                    );
                } else {
                    showStatusMsg(
                        LABEL_CONST.ERROR,
                        createMessage(LABEL_CONST.ERR_CM_3001)
                    );
                }
            }
        }
    };

    const saveVesselData = () => {
        if (editDataContextRef.current && editDataContextRef.current.length > 0) {
            const payload = [];

            editDataContextRef.current.forEach(ed => {
                for (const prop in ed) {
                    payload.push(ed[prop]);
                }
            });

            if (areBookingNumbersValid(payload)) {

                if (isBookingNumbersExists(payload)) {
                    showStatusMsg(
                        LABEL_CONST.ERROR,
                        createMessage(LABEL_CONST.ERR_MN_4002)
                    );
                } else {
                    const cancelledAndUpdatedRows = rows.filter(row => row.update);
                    setSaveCaseData([...cancelledAndUpdatedRows, ...payload]);

                    showStatusMsg(
                        LABEL_CONST.WARNING,
                        createMessage(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES),
                        'saveRecordCase'
                    );
                }
            } else {

                showStatusMsg(
                    LABEL_CONST.ERROR,
                    createMessage("Please check booking numbers in your entries")
                );
            }
        } else {
            showPopupWhileNoData(rows);
        }
    };

    const updateWorkPlanMaster = () => {
        if (handleValidation()) {
            const updateWorkPlanMasterPayload = {
                userId,
                vanningMonth: formatedDate_yyyymm(effectiveFromVanningMonth),
                etdFrom: (dateEtdFrom !== null) ? formatedDate(dateEtdFrom) : null,
                etdTo: (dateEtdTo !== null) ? formatedDate(dateEtdTo) : null,
                shippingCompanyCode: (shippingComp !== null) ? shippingComp.value : null,
                destinationCode: (destination !== null ? destination.value : null)
            };
            setIsLoading(true);
            postRequest(
                MODULE_CONST.VESSEL_BOOKING_MASTER.TPEX_INVOICE_BATCH,
                MODULE_CONST.VESSEL_BOOKING_MASTER.WORK_PLAN_MASTER_API,
                updateWorkPlanMasterPayload
            ).then(response => {
                    setIsLoading(false);
                    response?.data?.statusCode===200 && showStatusMsg(LABEL_CONST.INFORMATION, response?.data?.statusMessage);
                
            }).catch((e) => {
                let errorDetailsCode = e?.response?.data?.exception;
                setIsLoading(false);
                showStatusMsg(LABEL_CONST.ERROR, LABEL_CONST[errorDetailsCode]);
            });
        } else {
            showStatusMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }
    };

    /*
        @@@ Save Method //End
    */
    return (
        <EditDataContext.Provider value={editDataContextRef}>
            <TpexLoader isLoading={getAllRequests.isLoading || isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    {/* Breadcrumb //Starts*/}
                    <TpexBreadCrumb name='Vessel Booking Master' />
                    {/* Breadcrumb //End*/}

                    {/* Vessel Booking Master Container //Starts*/}
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="col">
                                <div className="heading"><i className="bg-border"></i><h1>Vessel Booking Master</h1></div>
                            </div>
                            <form>
                                <div className="row">
                                    <div className="col-9">
                                        <div className=" VMS-wrapper">
                                            <div className="row mt-10">
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker mandatoryControl">
                                                        <label>{LABEL_CONST.VANNING_MONTH}</label>
                                                        <TpexDatePicker
                                                            dateFormat="yyyy/MM"
                                                            className="form-control"
                                                            dateSelected={effectiveFromVanningMonth}
                                                            id="effectiveFromVanningMonth"
                                                            handleDateSelected={e => handleDateSelected(e, "effectiveFromVanningMonth")}
                                                            showMonthYearPicker
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="custom-multiSelect ship-comp">
                                                        <label>Destination</label>
                                                        <TpexMultiSelectSeach
                                                            searchUrl={''}
                                                            handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                                                            name="destination"
                                                            noOptionsText="Select Destination..."
                                                            value={destination}
                                                            isMulti={false}
                                                            id="destinationId"
                                                            serverSide={false}
                                                            staticValues={destinationList}
                                                            BASE_URL={ADMIN_SERVICE}
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker">
                                                        <label>ETD Date From</label>
                                                        <TpexDatePicker
                                                            dateFormat="dd/MM/yyyy"
                                                            id="etdDateFrom"
                                                            dateSelected={dateEtdFrom}
                                                            className="form-control"
                                                            handleDateSelected={e => etdDateFrom(e)}
                                                        />
                                                    </div>
                                                </div>
                                                <div className="form-group col-3">
                                                    <div className="customDatePicker">
                                                        <label>ETD Date To</label>
                                                        <TpexDatePicker
                                                            dateFormat="dd/MM/yyyy"
                                                            id="etdDateTo"
                                                            dateSelected={dateEtdTo}
                                                            className="form-control"
                                                            handleDateSelected={e => etdDateTo(e)}
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                            <div className="row mt-10 pb-10">
                                                <div className="form-group col-3">
                                                    <div className="custom-multiSelect ship-comp">
                                                        <label>{LABEL_CONST.SHIP_COMP}</label>
                                                        <TpexMultiSelectSeach
                                                            searchUrl={''}
                                                            handleSelectedOptions={e => selectBoxChange(e)}
                                                            name="shipping-dropdown"
                                                            noOptionsText="Select Destination..."
                                                            defaultValue={shippingComp}
                                                            isMulti={false}
                                                            id="name"
                                                            serverSide={false}
                                                            staticValues={shipingCompList}
                                                            BASE_URL={ADMIN_SERVICE}
                                                        />
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                    <div className="form-group col-3 align-self-end">
                                        <div className="d-flex justify-content-end pb-10">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.SEARCH}
                                                handleClick={searchDataHandler}
                                                topmargin="4"
                                            />
                                        </div>

                                        <div className="d-flex justify-content-end pb-10">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.UPDATE_WORK_PLAN_MASTER}
                                                handleClick={updateWorkPlanMaster}
                                                topmargin="4"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* Vessel Booking Master Container //End*/}

                    <div className="panelBox mt-16">
                        <div className="grid-panel">
                            <TpexTable
                                columns={columns}
                                containerColumns={containerColumns}
                                customEdit={true}
                                editTable={true}
                                filter={true}
                                idName="id"
                                isCrud={false}
                                pagination={true}
                                rows={rows}
                                rowPerPage={10}
                                selectRow={true}
                                serverSideFilter={false}
                            />
                            <div className="gridfooter mt-10">
                                <div className="row">
                                    <div className="col g-0">
                                        <div className="heading"><i className="bg-border"></i><h1>Upload Download Action</h1></div>
                                    </div>
                                </div>
                            </div>
                            <div className="row mt-10">
                                <div className="form-group col-4">
                                    <div className="input-group custom-file-button">
                                        <input className="form-control" type="file" id="formFile" />
                                        <label className="input-group-text" htmlFor="formFile">Browse</label>
                                    </div>
                                </div>
                                <div className="form-group col-4 align-self-center">
                                    <TpexSimpleButton color="outline-primary" leftmargin="3" text="Upload" />
                                    <button type="button" className="btn btn-outline-primary ms-3" onClick={downloadTableData}>Download</button>
                                </div>
                                <div className="form-group col-4 align-self-center">
                                    <div className="d-flex justify-content-end">
                                        <button
                                            type="button"
                                            className="btn btn-primary"
                                            onClick={saveVesselData}
                                        >Save</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <AlertModal
                    handleClick={handleAlertConfirmation}
                    show={modalShow}
                    onHide={() => {
                        setModalShow(false);
                    }}
                    status={statusTitle}
                    content={statusContent}
                    parentBtnName={actionCase}
                />
            </main>
        </EditDataContext.Provider>
    );
};

export default VesselBookingMaster;