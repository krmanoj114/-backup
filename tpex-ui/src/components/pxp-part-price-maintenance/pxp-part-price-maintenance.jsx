import React, { startTransition, useEffect, useState } from 'react';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexTable } from '../../common/components/tables';
import TpexSimpleButton from '../../common/components/button';
import { getRequest, postRequest } from '../../services/axios-client';
import { ADMIN_SERVICE, MIME_TYPE, MODULE_CONST } from '../../constants/constant';
import { createMessage, createMesssageReplacer, formatedDate_yyyymm, getFileExtension } from '../../helpers/util';
import { TpexLoader } from '../../common/components/loader/loader';
import { columns } from './columns-details';
import AlertModal from '../../common/components/alert-modal/alert-modal';

function PxpPartPriceMaintenance() {
    const [isLoading, setIsLoading] = useState(true);
    const [destinationList, setDestinationList] = useState([]);
    const [carFamilyList, setCarFamilyList] = useState([]);
    const [carFamily, setCarFamily] = useState([]);
    const [destination, setDestination] = useState([]);
    const [effectiveMonthVal, setEffectiveMonth] = useState("")
    const [partNo, setPartNo] = useState("");
    const [rows, setRows] = useState([]);
    const [modalShowAlert, setModalShowAlert] = useState(false);
    const [messageType, setMessageType] = useState();
    const [messageText, setMessageText] = useState();
    const [validationObj, setValidationObj] = useState(null);
    const [dropDownDataList, setDropDownDataList] = useState({
        currency: []
    });
    const [searchCriteria, setSearchCriteria] = useState({ carFamilyCode: "", importerCode: "" });
    const [hasUnsavedData, setHasUnsavedData] = useState(false);
    const [actionCase, setActionCase] = useState("");

    const userId = "TMT";

    const handleAlertConfirmation = (e) => {
        setModalShowAlert(false);
        const title = e.target.title;
        if (title === "unsaveddata-Warning" || title === "unsaveddata_download-Warning") {
            if (handleValidation()) {
                getGridData(title !== 'unsaveddata-Warning');
            } else {
                openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
            }
        }
    };

    function openAlertBox(messegeType, messageText = "", type = "") {
        if (type) {
            setActionCase(type);
        }
        setMessageType(messegeType);
        setMessageText(messageText);
        setModalShowAlert(true)
    };

    function getCarFamilyDestinations() {
        setIsLoading(true);
        getRequest(MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.CAR_FAMILY_DEST + userId).then(dataRes => {
            setDestinationList(dataRes.data.destinationList);
            setCarFamilyList(dataRes.data.carFamilyList);
        }).catch(function (error) {
            console.log('getCarFamilyDestinations =>', error.message);
        }).finally(() => {
            setIsLoading(false);
        });
    }

    useEffect(() => {
        getCarFamilyDestinations();
    }, [])

    useEffect(() => {
        // update row again
    }, [rows, validationObj, searchCriteria])

    function handleSelectedOptions(e, name) {
        setIsLoading(true);
        if (name === "carFamily") {
            setCarFamily(e);
            setSearchCriteria({
                ...searchCriteria,
                carFamilyCode: e?.value ? e.value : ''
            });
        }
        if (name === "destination") {
            setDestination(e);
            setSearchCriteria({
                ...searchCriteria,
                importerCode: e?.value ? e.value : ''
            });
        }
        setIsLoading(false);

    }

    function handleDateSelected(d, name) {
        if (name === "effectiveMonth") {
            setEffectiveMonth(d)
        }
    }
    function setPartNumber(e) {
        let partNumber= e.target.value.split('-').join('')
        if(partNumber) {
            partNumber = partNumber.match(/.{1,5}/g).join('-');
        } 
        else {
            partNumber = "";
        }
        setPartNo(partNumber)
    }

    function handleValidation() {
        const destSelected = destination ? destination.value : "";
        const carFamilySelected = carFamily ? carFamily.value : "";
        if (!carFamilySelected || !destSelected) {
            return false;
        }
        setIsLoading(false);
        return true;
    }

    function refreshGrid() {
        getGridData();
    }

    function getValidationObject(validationData) {
        return validationData.reduce(
            (acc, current) => {
                acc[current.id] = current;
                return acc;
            },
            {});
    }

    const formatPriceDisplay = (x) => {
        const val = x.toString();
        const index = val.indexOf(".");
        let firstValue = '';
        let secondValue = '';
        if (index > -1) {
            const [beforeDot, afterDot] = val.split('.');
            firstValue = beforeDot;
            secondValue = afterDot;
        } else if (index === -1) {
            firstValue = val;
        }
        const formattedValue = Number(firstValue).toLocaleString();
        const retValue = secondValue != '' ? formattedValue + "." + secondValue : formattedValue;
        return retValue;
    }

    function getGridData(downloadRequired = false) {
        const valObj = getValidationObject(columns);
        setValidationObj(valObj);
        setIsLoading(true);
        const reqPayload = {
            effectiveMonth: formatedDate_yyyymm(effectiveMonthVal),
            importerCode: destination ? destination.value : "",
            carFamilyCode: carFamily ? carFamily.value : "",
            partNo: partNo,
            userId: userId
        };
        postRequest(MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.GRID_API, reqPayload).then(dataRes => {
            const currencyData = dataRes.data.currencyList.reduce((acc, val) => {
                acc[val.value] = val.label;
                return acc;
            },{});
         

            if (dataRes.data.partPriceMasterList.length) {          
                setRows(dataRes.data.partPriceMasterList.map((k, i) => {
                    k.idList = i + 1;
                    k.currency = currencyData[k.currency];
                    k.partPrice = formatPriceDisplay(k.partPrice);
                    return k;
                }));
                downloadRequired && downloadRecords();
            } else {
                startTransition(() => {
                    setRows([]);
                });
                openAlertBox(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                setIsLoading(false);
            }

            setDropDownDataList(
                {
                    ...dropDownDataList,
                    currency: dataRes.data.currencyList
                });           
        }).catch(function (error) {
            console.log('pxp part price error', error);
            openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));           
        }).finally(() => {
            !downloadRequired && setIsLoading(false);
        })
    }

    const handleClick = () => {
        if (hasUnsavedData) {
            openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, 'unsaveddata');
        } else {
            if (handleValidation()) {
                getGridData(false);
            } else {
                openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
            }
        }
    }

    const handleDownloadClick = () => {
        if (hasUnsavedData) {
            openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_DOWNLOAD_MODIFY_WARN, 'unsaveddata_download');
        } else {
            if (handleValidation()) {
                getGridData(true);
            } else {
                openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
            }
        }
    }

    function downloadRecords() {
        let fileNameToDownload = "";
        const apiUrl = MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.API_BASE_URL + MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.DOWNLOAD_API + '?carFamilyCode=' + carFamily.value + '&importerCode=' + destination.value + '&userId=TMT&effectiveMonth=' + formatedDate_yyyymm(effectiveMonthVal) + '&partNo=' + partNo;
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
              
                if (data.type === 'application/octet-stream') {
                    const fileExt = getFileExtension(fileNameToDownload);
                    let link = document.createElement("a");
                    link.href = URL.createObjectURL(
                        new Blob([data], { type: MIME_TYPE[fileExt] })
                    );
                    link.download = fileNameToDownload;
                    link.click();
                } else {
                    if (data.status && data.status === "offline") {
                        openAlertBox(LABEL_CONST.INFORMATION, createMessage(data.message));
                    }
                    else if (data.errorMessageParams && Object.keys(data.errorMessageParams).length > 0) {
                        const messageAfterReplace = createMesssageReplacer(data.errorMessageParams, data.exception);
                        openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
                    } else {
                        openAlertBox(LABEL_CONST.ERROR, createMessage(data.exception));
                    }
                }
            }
        ).catch(function (error) {
            console.log('download file error', error);
        }).finally(() => {
            setIsLoading(false);
        });
    }

    function addEditDataForParent(add, edit) {
        if (add.length || Object.keys(edit).length) {
            setHasUnsavedData(true);
        } else {
            setHasUnsavedData(false);
        }
    }

    return (
        <>
            <TpexLoader isLoading={isLoading} />

            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.PXP_PART_PRICE_MAINTENANCE} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.PXP_PART_PRICE_MAINTENANCE}</h1></div>
                            </div>
                            <form>
                                {/* first row */}
                                <div className="row mt-10 pb-2">
                                    <div className="col-4">
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
                                    <div className="col-4 g-0">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="importerCode">Importer Code</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                                                name="importerCode"
                                                noOptionsText="Search..."
                                                value={destination}
                                                isMulti={false}
                                                id="importerCode"
                                                serverSide={false}
                                                staticValues={destinationList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>

                                    <div className="col-4">
                                        <div className="">
                                            <label htmlFor="partNo">Part no</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="partNo"
                                                name="partNo"
                                                value={partNo}
                                                minLength="14"
                                                maxLength="14"
                                                onChange={setPartNumber}
                                            />
                                        </div>
                                    </div>
                                </div>

                                {/* 2nd row */}
                                <div className="row mt-10 pb-2">

                                    <div className="col-3">
                                        <div className="customDatePicker">
                                            <label htmlFor="etd">{LABEL_CONST.EFFECTIVE_MONTH}</label>
                                            <TpexDatePicker
                                                dateFormat="yyyy/MM"
                                                dateSelected={effectiveMonthVal}
                                                id="effectiveMonth"
                                                handleDateSelected={e => handleDateSelected(e, "effectiveMonth")}
                                                showMonthYearPicker
                                            />
                                        </div>
                                    </div>

                                    <div className="col-9 mt-auto text-end">
                                        <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={handleClick} />
                                        <TpexSimpleButton color="primary" leftmargin="2" text={LABEL_CONST.DOWNLOAD} handleClick={handleDownloadClick} />
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
                                idName="id"
                                primaryKey="id"
                                defaultSortingId="idList"
                                //defaultSortingOrder="desc"
                                moduleName="PXP_PART_PRICE_MAINTENANCE"
                                rowPerPage={10}
                                selectAll={false}
                                selectRow={true}
                                columns={columns}
                                validationObj={validationObj}
                                isCrud={true}
                                pagination={true}
                                actionOnTop={false}
                                searchCriteria={searchCriteria}
                                refreshGrid={refreshGrid}
                                filter={true}
                                serverSideFilter={false}
                                editTable={true}
                                copyAction = {true}
                                dropDownData = {dropDownDataList}
                                addEditDataForParent={addEditDataForParent}
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
                    content={messageText} 
                    parentBtnName={actionCase}
                    />
            </main>
        </>
    )
}

export default PxpPartPriceMaintenance;
