import React, { useEffect, useState } from 'react';
import { TpexBreadCrumb } from '../../../common/components/breadcrumb/breadcrumb';
import TpexSimpleButton from '../../../common/components/button';
import { TpexDatePicker } from '../../../common/components/datepicker/datepicker';
import { LABEL_CONST } from '../../../constants/label.constant.en';
import { createMessage, dateCompare, formatedDate } from '../../../helpers/util';
import { TpexMultiSelectSeach } from '../../../common/components/multiselect/multiselect';
import { ADMIN_SERVICE, MODULE_CONST } from '../../../constants/constant';
import { getRequest, postRequest } from '../../../services/axios-client';
import { TpexTable } from '../../../common/components/tables';
import { TpexLoader } from '../../../common/components/loader/loader';
import AlertModal from '../../../common/components/alert-modal/alert-modal';

export function ShippingContainerResult() {
    const [destinationList, setDestinationList] = useState([]);
    const [renbanCodeList, setRenbanCodeList] = useState([]);
    const [etdFrom, setEtdFrom] = useState("");
    const [etdTo, setEtdTo] = useState("");
    const [bookingNumberValue, setBookingNumberValue] = useState("");
    const [renbanCode, setRenbanCode] = useState([]);
    const [destination, setDestination] = useState([]);
    const [rows, setRows] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [modalShowAlert, setModalShowAlert] = useState(false);
    const [messageType, setMessageType] = useState();
    const [messageText, setMessageText] = useState();

    const columns = [
        {
            "id": "srNo",
            "name": "S.No.",
            "type": "string",
            "max": "99999",
            "min": "1",
            "required": true,
            "editable": false,
            "unique": true,
            "link": false
        },
        {
            "id": "etd",
            "name": "ETD",
            "type": "date",
            "max": "01/01/9999",
            "min": "01/01/1900",
            "required": true,
            "editable": true,
            "unique": false,
            "link": false
        },
        {
            "id": "bookingNo",
            "name": "Booking No.",
            "type": "string",
            "max": "999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "containerRenbanNo",
            "name": "Container Renban No.",
            "type": "string",
            "max": "999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "isoContainerNo",
            "name": "ISO Container No.",
            "type": "string",
            "max": "999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "sealNo",
            "name": "Seal No.",
            "type": "string",
            "max": "999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "containerSize",
            "name": "Container Size (Ft.)",
            "type": "number",
            "max": "9999999999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "dgContainer",
            "name": "D.G. Container",
            "type": "string",
            "max": "999999999999",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "planVanningDate",
            "name": "Plan Vanning Date",
            "type": "date",
            "max": "01/01/9999",
            "min": "01/01/1900",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "shippingResultRecieved",
            "name": "Shipping Result Received",
            "type": "string",
            "max": "10000",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        },
        {
            "id": "invoiceStatus",
            "name": "Invoice status",
            "type": "string",
            "max": "10000",
            "min": "1",
            "required": false,
            "editable": false,
            "unique": false,
            "link": false
        }
    ];

    function getDestinationList() {
        setIsLoading(true);
        getRequest(MODULE_CONST.SHIPPING_CONTAINER_RESULT.API_BASE_URL, MODULE_CONST.SHIPPING_CONTAINER_RESULT.DEST_API).then(dataRes => {
            const destList = dataRes.data.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + " - " + d.fdDstNm
                }
            })
            setDestinationList(destList);
            setIsLoading(false);
        }).catch(function (error) {
            console.log('getDestinationList =>', error.message);
            setIsLoading(false);
        });
    }

    function getRenbanCode(dest) {
        setIsLoading(true);
        setRenbanCodeList([]);
        if (dest) {
            const code = dest.value ? dest.value : '';
            getRequest(MODULE_CONST.SHIPPING_CONTAINER_RESULT.API_BASE_URL, MODULE_CONST.SHIPPING_CONTAINER_RESULT.RENBAN_API + '?contDstCode=' + code).then(dataRes => {
                const renBanCodesArr = dataRes.data.map(r => {
                    return {
                        value: r.renbanCode,
                        label: r.renbanCodeValue
                    }
                })
                setRenbanCodeList(renBanCodesArr);
                setIsLoading(false);
            }).catch(function (error) {
                console.log('getRenbanCode', error);
                setIsLoading(false);
            });
        }
    }

    function handleDateSelected(d, name) {
        const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
        if (!d || (d && !formatedDate(d).match(dateReg))) {
            if (name === "etdTo") {
                setEtdTo("")
            }
            if (name === "etdFrom") {
                setEtdFrom("");
                setEtdTo("")
            }
        }

        else {
            if (name === "etdTo") {
                setEtdTo(d)
            }
            if (name === "etdFrom") {
                setEtdFrom(d);
                setEtdTo(d)
            }
        }
    }

    function handleValidation() {
        const destSelected = destination ? destination.value : "";
        if (!etdTo || !etdFrom || !destSelected) {
            openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
            return false;
        }
        if (!(etdTo && etdFrom && dateCompare(etdFrom, etdTo))) {
            openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_IN_1016'));

            return false
        }
        setIsLoading(false);
        return true;
    }

    function handleSelectedOptions(e, name) {
        setIsLoading(true);
        if (name === "destination") {
            setDestination(e);
            setRenbanCode([]);
            getRenbanCode(e);
            setIsLoading(false);
        } else if (name === "renbanCode") {
            setRenbanCode(e);
            setIsLoading(false);
        } else {
            setRenbanCode([]);
            setIsLoading(false);
        }
        
    }

    const handleClick = () => {
        if (handleValidation()) {
            setIsLoading(true);
            const destinationSelected = destination ? destination.value : "";
            const renbanCodeSelected = renbanCode.map(r => r.value);
            const reqPayload = {
                etdFrom: formatedDate(etdFrom),
                etdTo: formatedDate(etdTo),
                containerDestination: destinationSelected,
                bookingNo: bookingNumberValue,
                renbanCodes: renbanCodeSelected
            };
            postRequest(MODULE_CONST.SHIPPING_CONTAINER_RESULT.API_BASE_URL, MODULE_CONST.SHIPPING_CONTAINER_RESULT.GRID_API, reqPayload).then(dataRes => {
                setRows(dataRes.data);
                setIsLoading(false);
            }).catch(function (error) {
                console.log('shipping container result grid', error);
                openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
                setIsLoading(false);
            });

        }
    }

    function setBookingNumber(e) {
        setBookingNumberValue(e.target.value);
    }

    useEffect(() => {
        getDestinationList();
    }, []);

    function handleEstimateCompareInvoice(e) {
        console.log('handleEstimateCompareInvoice clicked', e)
    }

    function handleCalculateEstimate(e) {
        console.log('handleCalculateEstimate clicked', e)
    }

    const handleAlertConfirmation = () => {
        console.log('in handleAlertConfirmation')
    };

    function openAlertBox(messegeType, messageText = "") {
        setMessageType(messegeType);
        setMessageText(messageText);
        setModalShowAlert(true)
    };

    function addEditDataForParent(add, edit) {
        console.log(' ');
    }

    return (
        <>
          <TpexLoader isLoading={isLoading} />   
          
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.SHIP_CONT_RESULT} />
                    <div className="panelBox">
                        <div className="search-panel shipping-container-result">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.SHIP_CONT_RESULT}</h1></div>
                            </div>
                            <form>
                                {/* ETD FROM / TO / DESTINATION */}
                            <div className="row">
                                <div className="col-10 g-0 panel-scroll-container">
                                <div className="panel-scroll-wrapper">
                                <div className="row mt-10">
                                    <div className="col-3">
                                        <div className="customDatePicker mandatoryControl ">
                                            <label htmlFor="etd">{LABEL_CONST.ETD_FROM}</label>
                                            <TpexDatePicker
                                                dateFormat="dd/MM/yyyy"
                                                dateSelected={etdFrom}
                                                id="etdFrom"
                                                handleDateSelected={e => handleDateSelected(e, "etdFrom")}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-3">
                                        <div className="customDatePicker mandatoryControl ">
                                            <label htmlFor="etd">{LABEL_CONST.ETD_TO}</label>
                                            <TpexDatePicker
                                                dateFormat="dd/MM/yyyy"
                                                dateSelected={etdTo}
                                                id="etdTo"
                                                handleDateSelected={e => handleDateSelected(e, "etdTo")}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-3">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="destination">{LABEL_CONST.CONT_DEST}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                                                name="destination"
                                                noOptionsText="Search..."
                                                value={destination}
                                                isMulti={false}
                                                id="destinationId"
                                                serverSide={false}
                                                staticValues={destinationList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-3">
                                        <div className="">
                                            <label htmlFor="bookingNumber">{LABEL_CONST.BOOKING_NO}</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="bookingNumberId"
                                                name="bookingNumber"
                                                value={bookingNumberValue}
                                                minLength="0"
                                                maxLength="15"
                                                onChange={setBookingNumber}
                                            />
                                        </div>
                                    </div>
                                </div>
                                {/* booking no / renban code */}
                                <div className="row mt-10 pb-10">
                                    <div className="col">
                                        <div className="custom-multiSelect">
                                            <label htmlFor="renbanCode">{LABEL_CONST.RENBAN_CODE}</label>
                                            <TpexMultiSelectSeach
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'renbanCode')}
                                                name="renbanCode"
                                                noOptionsText=""
                                                value={renbanCode}
                                                isMulti={true}
                                                id="renbanCodeId"
                                                serverSide={false}
                                                staticValues={renbanCodeList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    </div>
                                </div>
                                </div>
                                <div className="form-group col-2 align-self-end">
                                    <div className="d-flex justify-content-end pb-10">
                                        <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={handleClick} topmargin="4" />
                                    </div>
                                </div>
                            </div>
                            </form>
                        </div>
                    </div>
                    <div className="panelBox mt-10">
                        {/* table  */}
                        <div className="grid-panel mt-0 pb-0">
                            <TpexTable
                                rows={rows}
                                idName="srNo"
                                moduleName="SHIPPING_CONTAINER_RESULT"
                                rowPerPage={99999999}
                                selectAll={false}
                                selectRow={false}
                                columns={columns}
                                isCrud={false}
                                pagination={false}
                                filter={true}
                                serverSideFilter={false}
                                editTable={false}
                                addEditDataForParent={addEditDataForParent}
                            />
                        {/* button  */}
                        <div className="row">
                            <div className="col mt-10 pb-10 text-end">
                                <TpexSimpleButton color="primary" handleClick={event => handleEstimateCompareInvoice(event)} text={LABEL_CONST.ESTIMATE_COMP_INV} />
                                <TpexSimpleButton color="primary" handleClick={event => handleCalculateEstimate(event)} text={LABEL_CONST.CALCULATE_ESTIMATE} leftmargin="3" />
                            </div>
                        </div>
                        </div>
                    </div>
                </div>
                {/* alert modal  */}
                <AlertModal
                    handleClick={handleAlertConfirmation}
                    show={modalShowAlert}
                    onHide={() => setModalShowAlert(false)}
                    status={messageType}
                    content={messageText} />
            </main>
        </>
    )
};