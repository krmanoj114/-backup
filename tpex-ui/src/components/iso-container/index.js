import { useEffect, useState } from "react";
import { TpexLoader } from '../../common/components/loader/loader';
import "../../styles/table.css";
import './style.css';
import { getRequest, postRequest } from '../../services/axios-client';
import { MODULE_CONST } from "../../constants/constant";
import { LABEL_CONST } from "../../constants/label.constant.en";
import { TpexBreadCrumb } from "../../common/components/breadcrumb/breadcrumb";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { ShippingControlTable } from "../shipping-control-master/shipping-control-table";
import { createMessage, formatedDate_yyyymm, formatedDate } from "../../helpers/util";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";
import { TpexMultiSelectSeach } from "../../common/components/multiselect/multiselect";
import TpexSimpleButton from "../../common/components/button";

const ISOContainer = () => {
    const column = [
        { id: 'etd', name: 'ETD', editFlag: true },
        { id: 'bookingNo', name: 'Booking No.', editFlag: false },
        { id: 'containerRanbanNo', name: 'Container Ranban No.', editFlag: false },
        { id: 'isoContainerNo', name: 'ISO Container No.', type: 'text',  required: true, maxLength: 15, editFlag: true, firstLnth: 5, lastLnth: 5  },
        { id: 'containerType', name: 'Cont. Type', type: 'text', maxLength: 15, required: true, editFlag: true, firstLnth: 5, lastLnth: 5  },
        { id: 'sealNo', name: 'Seal No',type: 'text', maxLength: 15, editFlag: true, firstLnth: 5, lastLnth: 5 },
        { id: 'tareWeight', name: 'Tare Weight', type: 'text', maxLength: 15, editFlag: true, firstLnth: 5, lastLnth: 5 },
        { id: 'containerSize', name: 'Cont. Size', editFlag: false },
        { id: 'shipComp', name: 'Ship Comp.', editFlag: false },
        { id: 'vanningStatus', name: 'Vanning Status', editFlag: false },
    ];

    const [rows, setRows] = useState([]);
    const [apiResultData, setApiResultData] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [vanningMonth, setVanningMonth] = useState(null);
    const [vanningPlant, setVanningPlant] = useState('');
    const [vanningPlantList, setVanningPlantList] = useState([]);
    const [containerDest, setContainerDest] = useState('');
    const [containerDestList, setContainerDestList] = useState([]);
    const [etd, setEtd] = useState(null);
    const [containerRanbanNumber, setContainerRanbanNumber] = useState('');
    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");
    const [validationObj, setValidationObj] = useState(null);

    const handleSearch = () => {
        if (!vanningMonth || !vanningPlant || !containerDest ) {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(LABEL_CONST.ERR_CM_3001));
        } else {
            getSearchData();
        }
    };

    const getValidationObject = (validationData) => {
        return validationData.reduce(
          (acc, current) => {
            acc[current.id] = current;
            return acc;
          },
        {});
    };

    const getSearchData = () => {
        setIsLoading(true);
        let payload = {
            "vanningMonth": formatedDate_yyyymm(vanningMonth),
            "vanningPlant": vanningPlant?.value,
            "containerDestination": containerDest?.value,
            "etd": etd === null ? "" : formatedDate(etd),
            "continerRanbanNo": containerRanbanNumber
        }
        postRequest(MODULE_CONST.ISO_CONTAINER.API_BASE_URL,
            MODULE_CONST.ISO_CONTAINER.SEARCH_API, payload).then(dataRes => {
                if(dataRes?.data?.isoContainerDataDto) {
                    const apiResult = dataRes?.data?.isoContainerDataDto;
                    setApiResultData(payload);
                    if (apiResult?.length > 0) {
                        const dataRows = apiResult.map((k, i) => {
                            k.idCount = "idCount-" + (i + 1);
                            k.idList = (i + 1);
                            k.sno = (i + 1);
                            k.flag = k.vanningStatus === "Completed";
                            return k;
                        })
                        setRows(dataRows);
                    }
                    setIsLoading(false);
                } else if(dataRes?.data?.exception) {
                    setRows([]);
                    setIsLoading(false);
                    showAlertMsg(LABEL_CONST.ERROR, createMessage(dataRes.data.exception));
                }
                const valObj = getValidationObject(column)
                setValidationObj(valObj);

            }).catch(function (error) {
                setRows([]);
                console.log('getError =>', error.message);
                setIsLoading(false);
                showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response?.data?.exception));
            }).finally(() => {
                setIsLoading(false);
            });
    };

    const refreshGrid = ()=> {
        console.log("upgrading the grid again");
        getSearchData();
    };

    const showAlertMsg = (title, content, btnName) => {
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
        if (btnName !== undefined) {
            setParentBtnName(btnName);
        } else {
            setParentBtnName('');
        }
    };

    const handleDate = (date, functionName) => {
        if (date !== undefined && date !== "") {
            functionName(date);
        }
    };

    const handleSelectedOptions = (e, name) => {
        if (name === "containerDest") {
            setContainerDest(e);
        }
        if (name === "vanningPlant") {
            setVanningPlant(e)
        }
    };

    const okConfirm = (e) => {
        setAlertModalShow(false);
        if (e.target.title === 'Search-Warning') {
            getSearchData();
        }
    };

    const getAllRequests = () => {
        setIsLoading(true);
        getRequest(
            MODULE_CONST.ISO_CONTAINER.API_BASE_URL,
            `${MODULE_CONST.ISO_CONTAINER.PLANT_DEST_CODE}`,).then((data) => {
                setIsLoading(false);
                const dataDestSelect = data?.data?.containerDestinationWithCodeDTO.map(d => {
                    return {
                        value: d.id,
                        label: d.name
                    }
                });
                setContainerDestList(dataDestSelect);
                const dataPlantSelect = data?.data?.plantMasterDTO.map(d => {
                    return {
                        value: d.id,
                        label: d.id + ' - ' + d.name
                    }
                });
                setVanningPlantList(dataPlantSelect);
            }).catch((e => {
                setIsLoading(false);
                console.log('getError =>', e.message);
                showAlertMsg(LABEL_CONST.ERROR, createMessage(e.response?.data?.error));
            })
        );
    };
    
    useEffect(() => {
        getAllRequests();
    }, []);

    return (
        <>
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.ISO_CONTAINER} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.ISO_CONTAINER}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 pb-2 search-panal">
                                    <div className="col-2">
                                        <div className="mandatoryControl customDatePicker">
                                            <label>Vanning Month</label>
                                            <TpexDatePicker
                                                id="vanDateTo"
                                                dateFormat="yyyy/MM"
                                                showMonthYearPicker={true}
                                                value={vanningMonth}
                                                dateSelected={vanningMonth}
                                                handleDateSelected={(date) => handleDate(date, setVanningMonth)}
                                                isDirectDatePicker={true}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-2">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="vanningPlant">Vanning Plant</label>
                                            <TpexMultiSelectSeach
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'vanningPlant')}
                                                name="vanningPlant"
                                                noOptionsText="Search..."
                                                value={vanningPlant}
                                                isMulti={false}
                                                id="vanningPlant"
                                                isMandatory
                                                serverSide={false}
                                                staticValues={vanningPlantList}
                                                BASE_URL={process.env.REACT_APP_API_BASE_URL}
                                            />
                                        </div>
                                    </div>

                                    <div className="col-2">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="containerDest">Container Destination</label>
                                            <TpexMultiSelectSeach
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'containerDest')}
                                                name="containerDest"
                                                noOptionsText="Search..."
                                                value={containerDest}
                                                isMulti={false}
                                                isMandatory
                                                id="containerDest"
                                                serverSide={false}
                                                staticValues={containerDestList}
                                                BASE_URL={process.env.REACT_APP_API_BASE_URL}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-2 customDatePicker">
                                        <div>
                                            <label>ETD</label>
                                            <TpexDatePicker
                                                dateFormat="dd/MM/yyyy"
                                                dateSelected={etd}
                                                id="etd"
                                                value={etd}
                                                handleDateSelected={(date) => handleDate(date, setEtd)}
                                                isDirectDatePicker={true}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-2">
                                        <div className="">
                                            <label htmlFor="containerRanbanNumber">Container Ranban No</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="containerRanbanNumber"
                                                name="containerRanbanNumber"
                                                maxLength={6}
                                                onChange={(e) => setContainerRanbanNumber(e.target.value)}
                                                value={containerRanbanNumber}
                                            />
                                        </div>
                                    </div>
                                    <div className="form-group col-2 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.SEARCH}
                                                handleClick={handleSearch}
                                                topmargin="4"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div className="grid-panel panelBox mt-10 iso-main-con">
                        <ShippingControlTable
                            rows={rows}
                            selectAll={true}
                            selectRow={true}
                            idName="idCount"
                            primaryKey="idCount"
                            defaultSortingId="idList"
                            columns={column}
                            pagination={true}
                            margin="gridTable"
                            editTable={true}
                            isCrud={true}
                            moduleName="ISO_CONTAINER"
                            apiResultData={apiResultData}
                            refreshGrid={refreshGrid}
                            validationObj={validationObj}

                        />
                    </div>

                </div>
                <AlertModal
                    show={alertModalShow}
                    onHide={() => setAlertModalShow(false)}
                    status={alertTitle}
                    content={alertContent}
                    parentBtnName={parentBtnName}
                    handleClick={okConfirm}
                />
            </main>
        </>
    );
};

export default ISOContainer;