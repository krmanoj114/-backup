import React, { useState, useEffect } from 'react';
import { useSelector } from "react-redux/es/hooks/useSelector";
import "../../styles/table.css";
import "./manual-invoice-generation.css";
import { LABEL_CONST } from '../../constants/label.constant.en';
import { MODULE_CONST } from '../../constants/constant';
import { getRequest } from '../../services/axios-client';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexLoader } from '../../common/components/loader/loader';
import { createMessage, formatedDate, formatDateSlash } from '../../helpers/util';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { TpexTable } from '../../common/components/tables';
import AlertModal from "../../common/components/alert-modal/alert-modal";
import TpexSimpleButton from '../../common/components/button';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';

const ManualInvoiceGeneration = () => {

    const [isLoading, setIsLoading] = useState(true);
    const [orderTypeValue, setOrderTypeValue] = useState("A");
    const [alertModalShow, setAlertModalShow] = useState(false);
    const [rows, setRows] = useState([]);
    const [destination, setDestination] = useState([]);
    const [destinationList, setDestinationList] = useState([]);
    const [etdFrom, setEtdFrom] = useState("");
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");
    const [checkBoxValue, setCheckBoxvalue] = useState(true);
    const [checkBoxCheckValue, setCheckBoxCheckValue] = useState(false);

    const compnayCode = useSelector(state => state.app.currentCompanyCode);

    const columns = [
        {
            id: "sno",
            name: "S No."
        },
        {
            id: "ranbanNumber",
            name: "Container Renban No.",
            type: "string",
        },
        {
            id: "vanPlant",
            name: "Van Plant",
            type: "string",
        },
        {
            id: "shippingResult",
            name: "Shipping Result",
            type: "string",
        },
        {
            id: "type",
            name: "Type",
            type: "string",
        },
        {
            id: "invoiceStatus",
            name: "Invoice Status",
            type: "string",
        }
    ];

    const okConfirm = (e) => {
        setAlertModalShow(false);
        if (e.target.title === 'Search-Warning') {
            getSearchData();
        }
    };

    function handleSelectedOptions(e, name) {
        setIsLoading(true);
        if (name === "destination") {
            setDestination(e);
            setIsLoading(false);
        }
    };
    
    function handleDateSelected(d, name) {
        const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
        if (!d || (d && !formatedDate(d).match(dateReg))) {
            if (name === "etdFrom") {
                setEtdFrom("");
            }
        }
        else {
            if (name === "etdFrom") {
                setEtdFrom(d);
            }
        }
    };

    function getCarFamilyDestinations() {
        setIsLoading(true);
        getRequest(MODULE_CONST.MIX_PRIVILEGE_MASTER.API_BASE_URL, MODULE_CONST.MIX_PRIVILEGE_MASTER.CAR_FAMILY_DEST).then(dataRes => {
            setDestinationList(dataRes.data.destinations.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + '-' + d.fdDstNm
                }
            }));
            setIsLoading(false);
        }).catch(function (error) {
            console.log('getDestinations', error.message);
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.message));
            setIsLoading(false);
        });
    };

    useEffect(() => {
        getCarFamilyDestinations();
    }, []);
    useEffect(() => {

    }, [compnayCode]);

    //Alert Box messages
    const showAlertMsg = (title, content, btnName) => {
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
        setParentBtnName(btnName || '');
    }

    // Search Button Click
    const handleSearch = () => {
        if (destination == "" || etdFrom == "") {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(LABEL_CONST.ERR_CM_3001));
        } else {
            getSearchData();
        }
    };

    //get request
    const getSearchData = () => {
        setIsLoading(true);
        const invTypeParam = orderTypeValue === '' || orderTypeValue === 'A' ? '' : orderTypeValue;
        const etdFromParam  = etdFrom !== null ? formatDateSlash(etdFrom) : '';
        const reqPara = `?etdDate=${etdFromParam}&destCode=${destination.value}&invoiceType=${invTypeParam}&companyCode=${compnayCode}`;
        getRequest(MODULE_CONST.MANUAL_INVOICE_GENERATION.API_BASE_URL,
            MODULE_CONST.MANUAL_INVOICE_GENERATION.INVOICE_LIST_API + reqPara).then(dataRes => {

                if (dataRes?.data?.listInvoiceResponse.length) {
                    setRows(dataRes.data.listInvoiceResponse.map((k, i) => {
                        k.sno = i + 1;
                        k.idCount = "idCount-" + (i + 1);
                        return k;
                    }))
                } else {
                    setRows([]);
                    showAlertMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                }
                setIsLoading(false);

            }).catch(function (error) {
                setIsLoading(false);
                console.log('error', error);
                showAlertMsg(LABEL_CONST.ERROR, error.message);
            });
    };


    // Invoices Type Radio Buttons function
    const orderTypeSelect = (e) => {
        const value = e.target.value;
        const incompleteLotValue = (value === "A" || value === "P");

        setOrderTypeValue(value);
        setCheckBoxvalue(incompleteLotValue);

        if (checkBoxCheckValue === true && incompleteLotValue) {
            setCheckBoxCheckValue(false);
        }
    };
    
    // Incomplete Lot Invoice checkbox function
    const handleCheckBox = () => {
        setCheckBoxCheckValue(!checkBoxCheckValue);
    };

    return (
        <>
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name="Manual Invoice Generation" />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>Manual Invoice Generation</h1></div>
                            </div>
                            <form>
                                <div className='row mt-10 mb-10'>
                                    <div className='col-3'>
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="destination">Destination</label>
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
                                                BASE_URL={process.env.REACT_APP_API_BASE_URL}
                                            />
                                        </div>
                                    </div>
                                    <div className='col-2'>
                                        <div className="customDatePicker mandatoryControl ">
                                            <label htmlFor="etd">Invoice ETD</label>
                                            <TpexDatePicker
                                                id="etdFrom"
                                                dateSelected={etdFrom}
                                                handleDateSelected={e => handleDateSelected(e, "etdFrom")}
                                                isDirectDatePicker={true}
                                            />
                                        </div>
                                    </div>
                                    <div className='col-7 mt-20 pt-2'>
                                        <div className="form-check form-check-inline">
                                            <input
                                                className="form-check-input"
                                                type="checkbox"
                                                name="incomplotInvoiceCheckbox"
                                                id="incomplotInvoiceCheckbox"
                                                value="Incomplete_Lot_Invoice"
                                                checked={checkBoxCheckValue}
                                                disabled={checkBoxValue}
                                                onChange={() => handleCheckBox()}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="incomplotInvoiceCheckbox">Incomplete Lot Invoice</label>
                                        </div>
                                    </div>
                                </div>

                                <div className="row mt-10">
                                    <div className="form-group col-9">
                                        <div className="order-type">Invoices Type:</div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="setUpType"
                                                id="inv_setup_all"
                                                value="A"
                                                checked={orderTypeValue === "A"}
                                                onChange={(e) => { orderTypeSelect(e) }}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_setup_all">All</label>
                                        </div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="setUpType"
                                                id="inv_setup_lot"
                                                value="L"
                                                checked={orderTypeValue === "L"}
                                                onChange={(e) => { orderTypeSelect(e) }}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_setup_lot">Lot</label>
                                        </div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="setUpType"
                                                id="inv_setup_pxp"
                                                value="P"
                                                checked={orderTypeValue === "P"}
                                                onChange={(e) => { orderTypeSelect(e) }}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_setup_pxp">PxP</label>
                                        </div>


                                    </div>
                                    <div className="form-group col-3 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.SEARCH}
                                                handleClick={handleSearch}
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* table  */}
                    <div className="panelBox mt-10 manual-invoice-gen">
                        <div className="grid-panel mt-0">
                            <TpexTable
                                rows={rows}
                                columns={columns}
                                idName="idCount"
                                moduleName="MANUAL_INVOICE_GENERATION"
                                rowPerPage={10}
                                selectAll={true}
                                selectRow={true}
                                isCrud={false}
                                pagination={true}
                                filter={false}
                                serverSideFilter={false}
                                editTable={false}
                                addEditDataForParent={() => true}
                                defaultSortingId="ranbanNumber"
                            //dropDownData={""}
                            />
                            <div className="form-group align-self-end mt-20 mb-10">
                                <div className="d-flex justify-content-end">
                                    <TpexSimpleButton color="primary" text="Generate Invoice" />
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
                    handleClick={okConfirm}
                    parentBtnName={parentBtnName}
                />
            </main>
        </>
    );
}

export default ManualInvoiceGeneration;