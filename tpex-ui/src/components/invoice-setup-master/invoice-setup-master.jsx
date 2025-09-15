import React, { useState, useEffect } from 'react';
import './invoice-setup-master.css';
import "../../styles/table.css";
import TpexSimpleButton from '../../common/components/button';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { MODULE_CONST } from '../../constants/constant';
import { getRequest} from '../../services/axios-client';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexLoader } from '../../common/components/loader/loader';
import { createMessage } from '../../helpers/util';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { ShippingControlTable } from '../../components/shipping-control-master/shipping-control-table';

function InvoiceSetupMaster() {
    const [orderTypeValue, setOrderTypeValue] = useState("P");
    const [searchResult, setsearchResult] = useState({});
    const [isLoading, setIsLoading] = useState(true);

    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");

    const [options, setOptions] = useState([]);
    const [selectedImpCode, setSelectedImpCode] = useState([]);
    const [rows, setRows] = useState([]);
    const [apiResultData, setApiResultData] = useState([]);
    const [isSearchBtnClick, setIsSearchBtnClick] = useState(null);
    const pageSize = 10;
    const [validationObj, setValidationObj] = useState(null);
    const [cfcListData, setCfcListData] = useState([]);
    const [exporterCodeList, setExporterCodeList] = useState([]);
    const [reExpCodeList, setReExpCodeList] = useState([]);
    const [lineCodeList, setLineCodeList] = useState([]);
    const [packingMonthList, setPackingMonthList] = useState([]);
    const [priceMethodList, setPriceMethodList] = useState([]);
    const [modifyFlg, setModifyFlg] = useState(false);
    // Table grid data
    const columns = [
        { id: 'cfc', name: 'CFC', type: 'multiselect', required: true, isMulti: true, editFlag: true, multiSelectList: cfcListData },
        { id: 'exporterCode', name: 'Exporter Code', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: exporterCodeList },
        { id: 'reExpCode', name: 'Re Exp. Code', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: reExpCodeList },
        { id: 'lineCode', name: 'Line Code', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: lineCodeList },
        { id: 'packingMonth', name: 'Packing Month', type: 'select', isBlank: false, required: true, editFlag: true, selectList: packingMonthList },
        { id: 'priceMethod', name: 'Price Method', type: 'select', isBlank: true, required: true, editFlag: true, selectList: priceMethodList },
        { id: 'vanDateFrom', name: 'Van Date From', type: 'date', required: true, editFlag: true, date: true },
        { id: 'vanDateTo', name: 'Van Date To', type: 'date', required: true, editFlag: true, date: true }
    ];
    // Get Importer Code API
    const getImporterCode = () => {
        setIsLoading(true);        
        getRequest(MODULE_CONST.INVOICE_SETUP_MASTER.API_BASE_URL, MODULE_CONST.INVOICE_SETUP_MASTER.IMPORTER_CODE_API).then(data =>{
            setIsLoading(false); 
            const dataSelect = data.data.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + ' - '+ d.fdDstNm
                }
            });
            setOptions(dataSelect);
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportTypes =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    };
    function handleSelectedOptions(e) {
        setSelectedImpCode(e);
    }
    function getSearchData(radioBtnVal, selectedImpCode) {
        setIsLoading(true);
        setsearchResult({});
        setModifyFlg(false);
        setIsSearchBtnClick(false);
        getRequest(MODULE_CONST.INVOICE_SETUP_MASTER.API_BASE_URL,
             MODULE_CONST.INVOICE_SETUP_MASTER.SEARCH_API+`?setupType=${radioBtnVal}&importerCode=${selectedImpCode?.value ? selectedImpCode.value:''}`).then(dataRes => {
            setIsLoading(false);
            const apiResult = dataRes.data;
            setApiResultData(dataRes.data);
            if (apiResult.invSetupMasterList.length) {
            const datatest = apiResult.invSetupMasterList.map((k, i) => {
                k.idCount = "idCount-"+ (i+1);
                k.idList = (i+1);
                k.setupType = radioBtnVal;
                k.importerCode = selectedImpCode;
                return k;
            })
            setRows(datatest);
            } else {
                setRows([]);
                if(arguments[2]===undefined && arguments[2] !== 'callFromRefreshGrid'){    
                    showAlertMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                }
            } 
            const valObj = getValidationObject(columns)
            setValidationObj(valObj);
            setCfcListData(apiResult.cfcList);
            setExporterCodeList(apiResult.exporterCodeList)
            setReExpCodeList(apiResult.reExpCodeList);
            setLineCodeList(apiResult.lineCodeList);
            setPackingMonthList(apiResult.packingMonthList);
            setPriceMethodList(apiResult.priceMethodList);
            setIsSearchBtnClick(true); // For Warning msg
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getError =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });        
    }

    useEffect(() => {
        getImporterCode();
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
        getSearchData(orderTypeValue, selectedImpCode, 'callFromRefreshGrid');
    }
    const modifyFld = (e) =>{
        setModifyFlg(e);
    }
    const handleRadioChange = e => {
        setOrderTypeValue(e.target.value);
    }
// common dropdown handel function

    function handleSearch(e){
         if((orderTypeValue && orderTypeValue === '') || selectedImpCode.length === 0){
            showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
            setsearchResult({});
        } else if(modifyFlg===true){
            showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, "Search");
        }else {
            getSearchData(orderTypeValue, selectedImpCode);
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
            getSearchData(orderTypeValue, selectedImpCode);
        }
    }

    return (
        <>
        {/* spinner */}
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.INVOICE_SETUP_MASTER} />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.INVOICE_SETUP_MASTER}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 invoice-maintenance">
                                    <div className="form-group col-4">
                                    <div className="order-type">{LABEL_CONST.SETUP_TYPE}:</div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="setUpType"
                                                id="inv_setup_pxp"
                                                value="P"
                                                checked={orderTypeValue === "P"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_setup_pxp">{LABEL_CONST.P_X_P}</label>
                                        </div>
                                    
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="setUpType"
                                                id="inv_setup_lot"
                                                value="L"
                                                checked={orderTypeValue === "L"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_setup_lot">{LABEL_CONST.LOT}</label>
                                        </div>
                                    </div>
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect mt-10 mandatoryControl">
                                            <label>{LABEL_CONST.IMPORTER_CODE}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                handleSelectedOptions={handleSelectedOptions}
                                                name="importerCode_select"
                                                noOptionsText="Select Importer Code"
                                                value={selectedImpCode}
                                                isMulti={false}
                                                staticValues={options}   // required when server side is false
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
                                    moduleName="INVOICE_SETUP_MASTER"
                                    setModifyFlag={modifyFld}
                                    refreshGrid={refreshGrid}
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
                    handleClick={okConfirm}
                    parentBtnName={parentBtnName}
                />
            </main>
        </>
    )
}
export {InvoiceSetupMaster};