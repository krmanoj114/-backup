import React, { useEffect, useState } from 'react';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexTable } from '../../common/components/tables';
import TpexSimpleButton from '../../common/components/button';
import { getRequest, postRequest } from '../../services/axios-client';
import { ADMIN_SERVICE, MODULE_CONST } from '../../constants/constant';
import { createMessage, formatedDate_yyyymm } from '../../helpers/util';
import { TpexLoader } from '../../common/components/loader/loader';
import TpexPartPriceModal from './part-price-modal';
import { columns, columnsPopup } from './columns-details';
import AlertModal from '../../common/components/alert-modal/alert-modal';

function LotPriceMaster() {
    const [isLoading, setIsLoading] = useState(true);
    const [destinationList, setDestinationList] = useState([]);
    const [carFamilyList, setCarFamilyList] = useState([]);
    const [carFamily, setCarFamily] = useState([]);
    const [destination, setDestination] = useState([]);
    const [rows, setRows] = useState([]);
    const [effectiveFromMonthVal, setEffectiveFromMonth] = useState(null)
    const [modalShow, setModalShow] = useState(false);
    const [modalShowAlert, setModalShowAlert] = useState(false);
    const [selectedrow, setSelectedRow] = useState({});
    const [validationObj, setValidationObj] = useState(null);
    const [popupRows, setPopupRows] = useState([]);
    const [messageType, setMessageType] = useState();
    const [messageText, setMessageText] = useState();

    const handleAlertConfirmation = () => {
        console.log('in handleAlertConfirmation')
    };

    function openAlertBox(messegeType, messageText = "") {
        setModalShow(false);
        setMessageType(messegeType);
        setMessageText(messageText);
        setModalShowAlert(true)
    };

    function getCarFamilyDestinations() {
        setIsLoading(true);
        getRequest(MODULE_CONST.LOT_PRICE_MASTER.API_BASE_URL, MODULE_CONST.LOT_PRICE_MASTER.CAR_FAMILY_DEST).then(dataRes => {

            setDestinationList(dataRes.data.destinations.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + '-' + d.fdDstNm
                }
            }));

            setCarFamilyList(dataRes.data.carFmly.map(d => {
                return {
                    value: d.carFmlyCode,
                    label: d.carFmlyCode + '-' + d.carFmlyName
                }
            }));

            setIsLoading(false);

        }).catch(function (error) {
            console.log('getCarFamilyDestinations =>', error.message);
            setIsLoading(false);
        });
    }


    useEffect(() => {
        getCarFamilyDestinations();
    }, [])

    useEffect(() => {
        // do nothing
    }, [popupRows, selectedrow, validationObj])

    function handleSelectedOptions(e, name) {
        setIsLoading(true);
        if (name === "destination") {
            setDestination(e);
            setIsLoading(false);
        }

        if (name === "carFamily") {
            setCarFamily(e);
            setIsLoading(false);
        }

    }

    function handleDateSelected(d, name) {
        if (name === "effectiveFromMonth") {
            setEffectiveFromMonth(d)
        }
    }

    function handleValidation() {
        const destSelected = destination ? destination.value : "";
        const carFamilySelected = carFamily ? carFamily.value : "";
        if (!carFamilySelected || !effectiveFromMonthVal || !destSelected) {
            return false;
        }
        setIsLoading(false);
        return true;
    }

    const handleClick = () => {
        if (handleValidation()) {
            setIsLoading(true);

            const reqPayload = {
                effectiveFromMonth: formatedDate_yyyymm(effectiveFromMonthVal),
                finalDestination: destination ? destination.label : "",
                carFamily: carFamily ? carFamily.label : "",
            };

            postRequest(MODULE_CONST.LOT_PRICE_MASTER.API_BASE_URL, MODULE_CONST.LOT_PRICE_MASTER.GRID_API, reqPayload).then(dataRes => {
                if (dataRes.data.length) {
                    setRows(dataRes.data.map((k, i) => {
                        k.idList = i + 1;
                        k.pKey = carFamily.value + destination.value + k.effectiveFromMonth + k.effectiveToMonth + k.lotCode + k.currency;
                        return k;
                    }))

                } else {
                    setRows([]);
                    openAlertBox(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                }
                setIsLoading(false);
            }).catch(function (error) {
                console.log('lot price master result grid', error);
                openAlertBox(LABEL_CONST.ERROR, error.response.data.exception);
                setIsLoading(false);
            });

        } else {

            openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }
    }

    function handleCustomAction(e, checkSelected) {
        if (checkSelected && !checkSelected.length) {
            openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.SELECT_ROW);
        } else if (checkSelected?.length > 1) {
            openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.SELECT_ONE_ITEM);
        } else {
            const getSelectedData = rows.find(r => r.pKey === checkSelected[0]);
            getPartPriceMaster(getSelectedData);
        }
    }

    function getPartPriceMaster(selData) {
        const valObj = getValidationObject(columnsPopup);
        setValidationObj(valObj);
        setIsLoading(true);

        const payload = {
            effectiveFromMonth: selData.effectiveFromMonth,
            effectiveToMonth: selData.effectiveToMonth,
            carFamily: carFamily.value,
            finalDestination: destination.value,
            lotCode: selData.lotCode,
            currency: selData.currency,
        };

        postRequest(MODULE_CONST.LOT_PRICE_MASTER.API_BASE_URL, MODULE_CONST.LOT_PRICE_MASTER.PART_PRICE_API, payload).then(dataRes => {
            if (!dataRes.data.length) {
                openAlertBox(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
            }
            setPopupRows(dataRes.data);
            setSelectedRow({ ...selData, carFamily: carFamily, destination: destination });
            setModalShow(true);
        }).catch(function (error) {
            console.log('getPartPriceMaster =>', error.message);
        }).finally(() => {
            setIsLoading(false);
        })
    }

    function getValidationObject(validationData) {
        return validationData.reduce(
            (acc, current) => {
                acc[current.id] = current;
                return acc;
            },
            {});
    }
    
    function addEditDataForParent(add, edit) {
        console.log(' ');
    }

    return (
        <>        
            <TpexLoader isLoading={isLoading} />

            {/* modal */}
            <TpexPartPriceModal
                show={modalShow}
                popupRows={popupRows}
                selectedrow={selectedrow}
                validationObj={validationObj}
                onHide={() => setModalShow(false)}
            />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.LOT_PRICE_MAINTENANCE} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.LOT_PRICE_MAINTENANCE}</h1></div>
                            </div>
                            <form>
                                {/* CAR FAMILY / DESTINATION / FROM MONTH */}
                                <div className="row mt-10 pb-2">
                                    <div className="col-3">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="destination">{LABEL_CONST.CAR_FAMILY}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'carFamily')}
                                                name="carFamily"
                                                noOptionsText="Search..."
                                                value={carFamily}
                                                isMulti={false}
                                                id="carFamily"
                                                serverSide={false}
                                                staticValues={carFamilyList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-6 g-0">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="destination">{LABEL_CONST.DESTINATION}</label>
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
                                    <div className="col-2">
                                        <div className="customDatePicker mandatoryControl">
                                            <label htmlFor="etd">{LABEL_CONST.EFFECTIVE_FROM_MONTH}</label>
                                            <TpexDatePicker
                                                dateFormat="yyyy/MM"
                                                dateSelected={effectiveFromMonthVal}
                                                id="effectiveFromMonth"
                                                handleDateSelected={e => handleDateSelected(e, "effectiveFromMonth")}
                                                showMonthYearPicker
                                            />
                                        </div>
                                    </div>

                                    <div className="col-1 g-0 align-self-end">
                                        <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={handleClick} />
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* table  */}
                    <div className="panelBox mt-10">
                        <div className="grid-panel mt-0">
                            <TpexTable
                                rows={rows}
                                idName="pKey"
                                defaultSortingId="idList"
                                //defaultSortingOrder="desc"
                                moduleName="LOT_PRICE_MASTER"
                                rowPerPage={10}
                                selectAll={false}
                                selectRow={true}
                                columns={columns}
                                isCrud={false}
                                pagination={true}
                                filter={true}
                                serverSideFilter={false}
                                editTable={false}
                                customActions={{ name: LABEL_CONST.PART_PRICE }}
                                handleCustomAction={handleCustomAction}
                                addEditDataForParent = { addEditDataForParent }
                            />

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
}

export default LotPriceMaster;
