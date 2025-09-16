import React, { useState, useEffect } from 'react';
import './returnable-packing-master.css';
import "../../styles/table.css";
import TpexSimpleButton from '../../common/components/button';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { MODULE_CONST } from '../../constants/constant';
import { getRequest} from '../../services/axios-client';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexLoader } from '../../common/components/loader/loader';
import { createMessage, createMesssageReplacer, dateCompare, formatedDate } from '../../helpers/util';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { ShippingControlTable } from '../../components/shipping-control-master/shipping-control-table';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';

function ReturnablePackingMaster() {
    const [rTypeValue, setRTypeValue] = useState("M");
    const [moduleMatCode, setModuleMatCode] = useState('');
    const [textLength, setTextLength] = useState(2);
    const [vanningDateFrom, setVanningDateFrom] = useState(null);
    const [vanningDateTo, setVanningDateTo] = useState(null);
    const [importerCode, setImporterCode] = useState([]);
    const [importerCodeList, setImporterCodeList] = useState([]);
    const [packingPlant, setpackingPlant] = useState([]);
    const [packingPlantList, setpackingPlantList] = useState([]);

    const [searchResult, setsearchResult] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const pageSize = 10;
    const [rows, setRows] = useState([]);
    const [apiResultData, setApiResultData] = useState([]);
    const [packingPlantListGrid, setPackingPlantListGrid] = useState([]);
    const [importerCodeListGrid, setImporterCodeListGrid] = useState([]);
    const [validationObj, setValidationObj] = useState(null);
    const [isSearchBtnClick, setIsSearchBtnClick] = useState(null);
    const [modifyFlg, setModifyFlg] = useState(false);


    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");
    // Table grid data
    const columns = [
        { id: 'sno', name: 'S.No', editFlag: false},
        { id: 'packingPlantLabel', name: 'Packing Plant', type: 'multiselect', required: true, isMulti: false, editFlag: false, multiSelectList: packingPlantListGrid },
        { id: 'importerCodeLabel', name: 'Importer Code', type: 'multiselect', required: true, isMulti: false, editFlag: false, multiSelectList: importerCodeListGrid},
        { id: 'moduleType', name: 'Module type/Material Code',type: 'text', required: true, maxLength: textLength, editFlag: false},
        { id: 'moduleDesciption', name: 'Module/Box Description', type: 'text', required: true, maxLength: 50,editFlag: true},
        { id: 'vanningDateFrom', name: 'Vanning Date From',  type: 'date', required: true, editFlag: false, date: true },
        { id: 'vanningDateTo', name: 'Vanning Date To',  type: 'date', required: true, editFlag: true, date: true }
    ];
    function getOnloadData() {
        setIsLoading(true);
        getRequest(MODULE_CONST.RETURNABLE_PACKING_MASTER.API_BASE_URL, MODULE_CONST.RETURNABLE_PACKING_MASTER.ONLOAD_API+"?cmpCd=TMT").then(dataRes => {
            setIsLoading(false);
            setImporterCodeList(dataRes.data.importerCodeList);
            setpackingPlantList(dataRes.data.packingPlantList);              
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportTypes =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    }
    function filterItem(list, code){        
        const selectedVal = list.find(x=> {return x.value === code});
        if(selectedVal !== undefined){
            return selectedVal;
        }
        return false; 
    }
    function getSearchData() {
        const currentDte = new Date("9999-12-31");
        setIsLoading(true);
        setsearchResult({});
        setModifyFlg(false);
        setIsSearchBtnClick(false);
        let reqPara = `?packingPlant=${packingPlant?.value ? packingPlant.value : ''}&importerCode=${getImporterCodeValue(importerCode)}&moduleType=${moduleMatCode}&vanDateFrom=${vanningDateFrom !== null ? formatedDate(vanningDateFrom) : ''}&vanDateTo=${vanningDateTo !== null ? formatedDate(vanningDateTo):formatedDate(currentDte)}&returnableType=${rTypeValue}&cmpCd=TMT`
        getRequest(MODULE_CONST.RETURNABLE_PACKING_MASTER.API_BASE_URL,
             MODULE_CONST.RETURNABLE_PACKING_MASTER.SEARCH_API+reqPara).then(dataRes => {
            setIsLoading(false);
            const apiResult = dataRes.data;
            if (apiResult.returnablePackingMasterDetails.length) {
            const datatest = apiResult.returnablePackingMasterDetails.map((k, i) => {
                k.idCount = "idCount-"+ (i+1);
                k.idList = (i+1);
                k.sno = (i+1);

                const packingPlantVal = filterItem(apiResult.packingPlantList, k.packingPlant);
                k.packingPlantLabelObj = packingPlantVal;
                k.packingPlantLabel = packingPlantVal.label;

                const importerCodeVal = filterItem(apiResult.importerCodeList, k.importerCode);
                k.importerCodeLabelObj = importerCodeVal;
                k.importerCodeLabel = importerCodeVal.label;

                return k;
            })
            setRows(datatest);
            
        } else {
            setRows([]);
            if(arguments[0]===undefined && arguments[0] !== 'callFromRefreshGrid'){    
                showAlertMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
            }
        }
        setApiResultData(apiResult);
        setPackingPlantListGrid(apiResult.packingPlantList);
        setImporterCodeListGrid(apiResult.importerCodeList);
        const valObj = getValidationObject(columns)
        setValidationObj(valObj);
        setIsSearchBtnClick(true); // For Warning msg
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getError =>', error.message);
            catchErrorHandle(error);
        });        
        
    }
    function catchErrorHandle(error){
        if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
            const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
            showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
          } else {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
          }      
          console.log('getProcessNames Error', error);
    }
    useEffect(() => {
        getOnloadData();
    }, []);

    useEffect(() => {
        // Do nothing
    }, [searchResult]);

    function getValidationObject(validationData) {
        return validationData.reduce(
          (acc, current) => {
            acc[current.id] = current;
            return acc;
          },
        {});
    }

    function refreshGrid() {
        console.log("upgrading the grid again");
        getSearchData('callFromRefreshGrid');
    }

    const modifyFld = (e) =>{
        setModifyFlg(e);
    }

    const handleRadioChange = e => {
        setRTypeValue(e.target.value);  
        e.target.value === 'M' ? setTextLength(2):setTextLength(5);
        setModuleMatCode('');      
    }

function getImporterCodeValue(d){
    return d.map(k => k.value);
}


// common dropdown handel function

    function handleSearch(e){
        if(vanningDateTo !==null && (vanningDateFrom ===null || vanningDateTo ===undefined)){
            showAlertMsg(LABEL_CONST.ERROR, createMessage("Vanning Date From cannot be blank if Vanning Date To is selected."));
        } else if (vanningDateFrom !== null && vanningDateTo !== null && !dateCompare(vanningDateFrom, vanningDateTo)) {
            showAlertMsg(LABEL_CONST.ERROR, createMessage("Vanning Date From should not be greater than Vanning Date To."));
        } else if(modifyFlg===true){
            showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, "Search");
        } else {
            getSearchData();
        }
    }

    //Alert Box messages
    function showAlertMsg(title, content, btnName){
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
        if(arguments[2] !== undefined || btnName !== undefined){            
            setParentBtnName(btnName);
        } else {
            setParentBtnName('');
        }
    }

    const okConfirm = (e)=>{
        setAlertModalShow(false);
        if(e.target.title==='Search-Warning'){
            getSearchData();
        }
    }

    return (
        <>
        {/* spinner */}
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.RETURNABLE_PACKING_MASTER} />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.RETURNABLE_PACKING_MASTER}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 invoice-maintenance">
                                    <div className="form-group col-4">
                                    <div className="order-type">{LABEL_CONST.RETURNABLE_TYPE}:</div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="rType"
                                                id="returnable_module"
                                                value="M"
                                                checked={rTypeValue === "M"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="returnable_module">{LABEL_CONST.MODULE}</label>
                                        </div>
                                    
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="rType"
                                                id="returnable_box_inner"
                                                value="B"
                                                checked={rTypeValue === "B"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="returnable_box_inner">{LABEL_CONST.BOX_INNER_MAT}</label>
                                        </div>
                                    </div>
                                    <div className="form-group col-2">
                                    <label>{LABEL_CONST.MODULE_TYPE_MAT}</label>
                                    <input 
                                        type="text" 
                                        id="returnable_module_type_mat" 
                                        className="form-control" 
                                        maxLength={textLength}
                                        onChange={(e) => setModuleMatCode(e.target.value)}
                                        value={moduleMatCode} />
                                    </div>  
                                    <div className="form-group col-2">
                                            <div className="customDatePicker">
                                                <label>{LABEL_CONST.VANNING_DATE_FROM}</label>
                                                <TpexDatePicker
                                                    id="vanningDateFrom"
                                                    dateSelected={vanningDateFrom}
                                                    handleDateSelected={date => setVanningDateFrom(date)}
                                                    isDirectDatePicker={true}
                                                />
                                            </div>
                                    </div>
                                    <div className="form-group col-2">
                                            <div className="customDatePicker">
                                                <label>{LABEL_CONST.VANNING_DATE_TO}</label>
                                                <TpexDatePicker
                                                    id="vanningDateTo"
                                                    dateSelected={vanningDateTo}
                                                    handleDateSelected={date => setVanningDateTo(date)}
                                                    isDirectDatePicker={true}
                                                />
                                            </div>
                                    </div>
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect mt-10">
                                            <label>{LABEL_CONST.PACKING_PLANT}</label>
                                            <TpexMultiSelectSeach
                                                id="returnable_packing_plant"
                                                handleSelectedOptions={(e) => e!==null ?setpackingPlant(e):setpackingPlant([])}
                                                name={LABEL_CONST.PACKING_PLANT}
                                                noOptionsText={LABEL_CONST.PACKING_PLANT}
                                                value={packingPlant}
                                                isMulti={false}
                                                serverSide={false}
                                                staticValues={packingPlantList}
                                            />
                                        </div>
                                    </div>
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect mt-10">
                                        <label>{LABEL_CONST.IMPORTER_CODE}</label>
                                        <TpexMultiSelectSeach
                                            handleSelectedOptions={(e)=>setImporterCode(e)}
                                            id="returnable_importer_code"
                                            name={LABEL_CONST.IMPORTER_CODE}
                                            noOptionsText={LABEL_CONST.IMPORTER_CODE}
                                            value={importerCode}
                                            serverSide={false}
                                            staticValues={importerCodeList}   // required when server side is false
                                        />
                                    </div>
                                </div> 
                                    <div className="form-group col-4 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={event => handleSearch(event)} topmargin="4" />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* table  */}
                    <div className="panelBox mt-10">
                        <div className="grid-panel mt-0">
                                <ShippingControlTable
                                    rows={rows}
                                    apiResultData={apiResultData}
                                    idName="idCount"
                                    primaryKey="idCount"
                                    defaultSortingId="idList"
                                    rowPerPage={pageSize}
                                    selectAll={false}
                                    selectRow={true}
                                    columns={columns}
                                    pagination={true}
                                    isCrud={true}
                                    moduleName="RETURNABLE_PACKING_MASTER"
                                    refreshGrid={refreshGrid}
                                    setModifyFlag={modifyFld}
                                    isSearchBtnClick={isSearchBtnClick}
                                    validationObj={validationObj}
                                />

                        </div>
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
    )
}
export {ReturnablePackingMaster};