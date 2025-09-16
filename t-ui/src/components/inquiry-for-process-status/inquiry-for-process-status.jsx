import React, { useState, useEffect, useRef } from 'react';
import './inquiry-for-process-status.css';
import "../../styles/table.css";
import TpexSimpleButton from '../../common/components/button';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { DEFAULT_GRID_RECORD, MODULE_CONST } from '../../constants/constant';
import { postRequest } from '../../services/axios-client';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexSelect } from '../../common/components/select';
import { TpexLoader } from '../../common/components/loader/loader';
import { useLocation, useNavigate } from 'react-router-dom';
import { TpexTable } from '../../common/components/tables/TpexTable';
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { createMessage, createMesssageReplacer, dateCompare, getFormatedDateWithSlash_Time } from '../../helpers/util';
import ProcessLogDetailModal from './process-log-model';
import EditDataContext from '../VesselBookingMaster/EditDataContext';
import { DateControl, DropDownControl, SuggestiveDropDown, InputTextControl } from './submit-batch-control';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';

export const InquiryProcessStatus = () => {
    const editDataContextRef = useRef([]);
    const [parameterCount, setParameterCount] = useState(0);
    const [controlHTML, setControlHTML] = useState([]);
    const [dropDownItems, setDropDownItems] = useState([]);

    const location = useLocation();
    const navigate = useNavigate();
    const [isLoading, setIsLoading] = useState(true);
    const [rows, setRows] = useState([]);
    const [searchResultData, setsearchResultData] = useState([]);
    const [selectedInterfaceVal, setSelectedInterfaceVal] = useState('');    

    const initialInterfaceVal = '';
    const [inquiryProcessList, setInquiryProcessList] = useState([]);
    const loginUserId = 'ROEM';

    const [processNameList, setProcessNameList] = useState([]);
    const [selectedProcessNameVal, setSelectedProcessNameVal] = useState('');    
    const initialProcessNameVal = '';

    const [systemNameList, setSystemNameList] = useState([]);
    const [selectedSystemNameVal, setSelectedSystemNameVal] = useState('');    
    const initialSystemNameVal = '';

    const [fromDateTime, setFromDateTime] = useState(new Date(new Date().setHours(0,0)));
    const [endDateTime, setEndDateTime] = useState(new Date(new Date().setHours(0,0)));
    const [radioBtnDis, setradioBtnDis] = useState(true);
    const [createdByVal, setCreatedByVal] = useState("");

    const [modalShow, setModalShow] = useState(false);
    const [searchStatus,setSearchStatus] = useState("");
    const [searchContent,setSearchContent] = useState("");

    const [selRowPopupData, setSelRowPopupData]=useState([]);
    const [modalPopUpShow, setModalPopUpShow] = useState(false);

    const [errorBtnShow, setErrorBtnShow] = useState(false);
    const [parentBtnName, setParentBtnName] = useState("");
    const [payloadData, setPayloadData] = useState({});

    function popupTableData(params) {
        setIsLoading(false);
        setSelRowPopupData(params);
        setModalPopUpShow(true);
    }
    //onLoad Interfacw list 
    function getInvoiceNosData(loginId) {
        setIsLoading(true);
        const reqPayLoad = {
            "userId": loginId
        }
        postRequest(MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL, MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_API, reqPayLoad).then(dataRes => {
            if(dataRes.data && dataRes.data.length !==0){
                const inquiryProList = dataRes.data.reduce((accumulator, d) => {
                    return [...accumulator, {
                        "id": d.processId,
                        "name": d.processName 
                    }];
                }, []);     
                const removeProcessIds = ['DA90070', 'PBINS004TMAP', 'BINF301']      
                const interfaceDropdwnList = dataRes.data.reduce((accumulator, d) => {
                    if(!removeProcessIds.includes(d.processId)){
                        return [...accumulator, {
                        "id": d.processId,
                        "name": d.processName 
                    }];
                }
                return accumulator;
                }, []);       
                
                setInquiryProcessList(interfaceDropdwnList);
                setProcessNameList(inquiryProList);
            }              
        }).catch(function (err) {
            catchErrorHandle(err);
        }).finally(() => {
            setIsLoading(false);
        });
    }
    //System Dropdown data
    function getSystemName(loginId) {
        setIsLoading(true);
        const reqPayLoad = {
            "userId": loginId
        }
        postRequest(MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL, MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_SYSTEM_API, reqPayLoad).then(dataRes => {
            if(dataRes.data && dataRes.data.length !==0){
                const systemNameList = dataRes.data.reduce((accumulator, d) => {
                    return [...accumulator, {
                        "id": d.systemNameValue,
                        "name": d.systemName 
                    }];
                }, []);                
                setSystemNameList(systemNameList);
            }              
        }).catch(function (error) {
            showAlertMsg(LABEL_CONST.ERROR,error.message);
        }).finally(() => {
            setIsLoading(false);
        });
    }

    // On interface dropdown change
    function getAdditionalDropdown(selectedVal) {
        setIsLoading(true);
        postRequest(MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL, MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_SELECTION_API, selectedVal).then(dataRes => {
            let res = dataRes.data;
            if(res.processId === 'BINF005'){
                res = {
                    "processId": "BINF005",
                        "processName": "Upload Shipping Result from GPAC",
                            "parameters": [
                                {
                                    "parameterTitle": "Target Container",
                                    "parameterName": "targetContainer",
                                    "controlType": "suggestiveDropDown",
                                    "isRequired": true,
                                    "maxLength": "100"
                                }
                            ],
                                "dropDownItems": [
                                    {
                                        "TargetContainer": [
                                            {
                                                "label": "TargetContainer1",
                                                "value": "1"
                                            },
                                            {
                                                "label": "TargetContainer2",
                                                "value": "2"
                                            }
                                        ]
                                    }
                                ]
                }
            }
            setParameterCount(res.parameters.length);
            res.parameters?.length !==0 ? setControlHTML(res.parameters) : setControlHTML([]);
            res.parameters?.length !==0 ? setDropDownItems(res.dropDownItems) : setDropDownItems([]);
        }).catch(function (err) {
            catchErrorHandle(err);
        }).finally(() => {
            setIsLoading(false);
        });
    }

    function catchErrorHandle(error){
        if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
                if(error.response.data.errorMessageParams?.ixosFlag){
                    showAlertMsg(LABEL_CONST.WARNING, createMessage(error.response.data.exception), "InvoiceBtn");
                    return;
                }
            const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
            showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
          } else {
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
          }      
          console.log('getProcessNames Error', error);
    }
    // On interface submit API
    function setInterfaceSubmit(selectedInterface) {
        setIsLoading(true);
        postRequest(MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL, MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_SUBMIT_API, selectedInterface).then(dataRes => {
            showAlertMsg(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
            setPayloadData({});
            resetScreen();
        }).catch(function (error) {
            if(error?.response?.data?.exception === 'WARN_AD_3003' || error?.response?.data?.exception === 'WARN_AD_3004'){
                catchErrorHandle(error);
            } else {
                catchErrorHandle(error);
                resetScreen();
            }
        }).finally(() => {
            setIsLoading(false);
        });
    }

// Show Table Data
    function showTableResult(selectedData){
        setIsLoading(true);
        postRequest(MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.API_BASE_URL, MODULE_CONST.INQUIRY_PROCESS_STATUS_CONTROL.INQUIRY_LIST_PROCESS_API, selectedData).then(dataRes => {
            if(dataRes.data.processList.length > 0){
                    setradioBtnDis(false);
                    const rowsData = dataRes.data.processList.map((k, i) => {
                        k.idCount = i;
                        return k;
                    });
                    const rowsErrData = dataRes.data.processList.filter(k => k.status === 'Error');
                    setsearchResultData(rowsData);
                    if(dataRes.data.hasAdminRole===true){
                        setCreatedByVal('all_process');
                        setRows(rowsData);
                    } else {
                        setCreatedByVal('owner_process');
                        const ownerList = rowsData.filter(data => data.owner === loginUserId);
                        setRows(ownerList);

                    }
                    rowsErrData.length > 0 ? setErrorBtnShow(true):setErrorBtnShow(false);
            } else {
                setradioBtnDis(true);
                setRows([]);
                setsearchResultData([]);
                setCreatedByVal('');
                showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.NO_DATA_FOUND);
                setErrorBtnShow(false);
            }
        }).catch(function (error) {
            showAlertMsg(LABEL_CONST.ERROR,error.message);
            resetScreen();
        }).finally(() => {
            setIsLoading(false);
        });
    }

    // Screen Reset fields
    function resetScreen(){
        editDataContextRef.current = [];
        setParameterCount(0);
        setSelectedInterfaceVal('');
        setradioBtnDis(true);
        setCreatedByVal('');
        setRows([]);
        setFromDateTime(null);
        setEndDateTime(null);
        setSelectedProcessNameVal('');
    }
    //Alert Box messages
    function showAlertMsg(status, content, btnName){
        setSearchStatus(status);
        setSearchContent(content);
        setModalShow(true);
        if(arguments[2] !== undefined || btnName !== undefined){            
            setParentBtnName(btnName);
        } else {
            setParentBtnName('');
        }
    }
    
    const okConfirm = (e)=>{
        setModalShow(false);
        if(e.target.title==='InvoiceBtn-Warning'){
            const updatedParameters = payloadData;
            updatedParameters.parameters = {...updatedParameters.parameters, ...{"warnFlag":"N"} }
            setInterfaceSubmit(updatedParameters);
        }
    }
    useEffect(() => {       
        getInvoiceNosData(loginUserId);
        getSystemName(loginUserId);
    }, [loginUserId]);

    useEffect(() => {
        if (location?.state && location.state.processName !==null) {
            if(processNameList && processNameList.length > 0){
                const navigatedProcess = processNameList.find(
                    x => x.name === location.state.processName
                );
                if (navigatedProcess) {
                    setSelectedProcessNameVal(navigatedProcess.id);
                }

                navigate(location.state, {});
            }         
        }
        // eslint-disable-next-line
    }, [location.state, processNameList]);

    function selectBoxChange(e) {
        editDataContextRef.current = [];
        setParameterCount(0);
        setSelectedInterfaceVal(e.target.value);
        if(e?.target.value && e.target.value !==''){
            const selectedProcessObj = inquiryProcessList.find(x=> {return x.id===e.target.value});
            const reqData = {
                "processId": selectedProcessObj.id,
                "processName": selectedProcessObj.name,
                "userId": loginUserId
            }
            getAdditionalDropdown(reqData);
        } 
    }
    
    // Process Name dropdown change
    function processSelectBoxChange(e){
        setSelectedProcessNameVal(e.target.value);
    }

    // System Name dropdown change
    function systemSelectBoxChange(e){
        setSelectedSystemNameVal(e.target.value);
    }

    const handleRadioChange = e => {
        setCreatedByVal(e.target.value);
        setRows([]);
        if(e.target.value ==="all_process"){
            setRows(searchResultData);
        }
        if(e.target.value ==="owner_process"){
            const ownerList = searchResultData.filter(data => data.owner === loginUserId);
            setRows(ownerList);
        }
    }

    function checkValNull(fldKey) {
        const chekKeyVal = [];
        Object.keys(fldKey).forEach(key => {

            if (fldKey[key][Object.keys(fldKey[key])] === null || fldKey[key][Object.keys(fldKey[key])] === '') {
                chekKeyVal.push(Object.keys(fldKey[key]));
            }

        });
        return chekKeyVal.length;
    }
    const allField = (k)=>{
        let flag=false;
        if(k?.length === 0 || 
            (k[0]?.['invoiceNo'] === '' && k[1]?.['haisenNo'] === '') ||
            (k[0]?.['invoiceNo'] === '' && k[1]?.['haisenNo'] === undefined) ||
            (k[0]?.['invoiceNo'] === undefined && k[0]?.['haisenNo'] === '')){
                flag = true;
            }
        return flag;
    }

    const optionalField = (z)=>{
        let optionalflag=false;
        if((z[0]?.['invoiceNo'] === '' || z[0]?.['invoiceNo'] === undefined) &&
        (z[0]?.['haisenNo'] !=='' || z[1]?.['haisenNo'] !=='') &&
        (z[1]?.['haisenYearMonth'] === null || z[1]?.['haisenYearMonth'] === undefined) &&
        (z[2]?.['haisenYearMonth'] === null || z[2]?.['haisenYearMonth'] === undefined)){
            optionalflag = true;
            }
        return optionalflag;
    }

    function submitInterface(e){
        let z = editDataContextRef.current;
        if((selectedInterfaceVal===null || selectedInterfaceVal==='') ||
            (selectedInterfaceVal !=='BINF016' && z?.length !== parameterCount) ||
            (selectedInterfaceVal !=='BINF016' && z?.length === parameterCount && checkValNull(z) > 0)) {
                showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
         } 

         else if(selectedInterfaceVal==='BINF016' && allField(z)){
            showAlertMsg(LABEL_CONST.ERROR, 'Please select Invoice No. or Haisen No. with Year Month information.');
        } 
         
         else if(selectedInterfaceVal==='BINF016' && optionalField(z)){
            showAlertMsg(LABEL_CONST.ERROR, 'Please specify Haisen Year and month.');
         }
         else {
            submitPayload();
        }
    }

    const submitPayload =()=>{
        const selectedProcessObj = inquiryProcessList.find(x=> {return x.id===selectedInterfaceVal});
            let paraObj = {};
            if(editDataContextRef.current?.length > 0){
                    editDataContextRef.current.forEach(ed => {
                        for (const prop in ed) {
                            if(ed[prop]===null){
                                ed[prop] ='';
                            }
                            if(ed[prop]!==''){
                                paraObj[prop] = ed[prop];
                            }
                        }                       
                });
            };
            const reqData = {
                "processId": selectedProcessObj.id,
                "processName": selectedProcessObj.name,
                "parameters": paraObj,
                "userId": loginUserId
            }
        setInterfaceSubmit(reqData);
        setPayloadData(reqData);
    }

    function addEditDataForParent(add, edit) {
        console.log(' ');
    }
    
// search process
    function searchInterface(e){
        if((fromDateTime===null || fromDateTime =='' )
            || (endDateTime===null || endDateTime =='')){      
            showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
         } 
         else if (fromDateTime !== null && endDateTime !== null) {
            if (!dateCompare(fromDateTime, endDateTime)) {
                    showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3019'));
            }
         else {
            const reqPara = {
                "processId": selectedProcessNameVal,
                "fromDateTime": getFormatedDateWithSlash_Time(fromDateTime),
                "endDateTime": getFormatedDateWithSlash_Time(endDateTime),
                "userId": loginUserId,
                "systemName": selectedSystemNameVal
            }
            showTableResult(reqPara);
        }
    }
    }
    const columns = [
        {
            id: "processName",
            name: "Process",
            popup: false
        },
        {
            id: "processSubmitTime",
            name: "Submit Time",
            popup: false
        },
        {
            id: "status",
            name: "Status",
            popup: true
        },
        {
            id: "owner",
            name: "Owner",
            popup: false
        },
        {
            id: "systemName",
            name: "System Name",
            popup: false
        },
        {
            id: "batchParameters",
            name: "Batch Parameters",
            popup: false
        }
    ]
    const textFormat = (s) => { return (s).replace(/ /g, "").toLowerCase()};
    return (
        <EditDataContext.Provider value={editDataContextRef}>
        {isLoading ?
            <TpexLoader /> : ''
          }
          
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.INQUIRY_FOR_PROCESS_STATUS_AND_CONTROL} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.INQUIRY_FOR_PROCESS_STATUS_AND_CONTROL}</h1></div>
                            </div>
                            <form>
                            <div className="row mt-10 pb-10">
                                    <div className="col-4">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="ineterface-dropdown-id">{LABEL_CONST.INTERFACE_DROPDOWN}</label>
                                            <TpexSelect 
                                                moduleName="ineterface-dropdown"
                                                options={inquiryProcessList}
                                                blankRequired={true} 
                                                selected={initialInterfaceVal}
                                                onChangeSelection={(e) => selectBoxChange(e)}
                                                hasValue={selectedInterfaceVal}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-6">
                                        <div className="row">
                                        { parameterCount > 0 && 
                                          controlHTML.map( (d, i) =>{
                                            switch(d.controlType){

                                                case "suggestiveDropDown":                                                    
                                                    const suggestiveList = () => {
                                                        const list = dropDownItems?.find(k => textFormat(Object.keys(k)[0]) === textFormat(d.parameterTitle));
                                                        return list?.['TargetContainer'] ? list['TargetContainer'] : [];
                                                    };
                                                    const suggestiveListData = suggestiveList().map(d => {
                                                        return {
                                                            value: d.value,
                                                            label: d.value+'-'+d.label
                                                        }
                                                    });
                                                    return(
                                                    <SuggestiveDropDown
                                                        labelName={d.parameterTitle}
                                                        fieldName={d.parameterName}
                                                        isRequired={d.isRequired}
                                                        dataList={suggestiveListData}
                                                        index={i}
                                                        key={`idCount-${i+1}`}
                                                    />
                                                    );

                                                case "dropDown":
                                                    const list = dropDownItems?.find(k => textFormat(Object.keys(k)[0]) === textFormat(d.parameterTitle));
                                                    const dataDestination = list?.[d.parameterTitle].map(d => {
                                                        return {
                                                            id: d.id,
                                                            name: d.id+'-'+d.name
                                                        }
                                                    });
                                                    return(
                                                    <DropDownControl
                                                        labelName={d.parameterTitle}
                                                        fieldName={d.parameterName}
                                                        selectDropdownList={dataDestination}
                                                        isRequired={d.isRequired}
                                                        index={i}
                                                        key={`idCount-${i+1}`}
                                                    />
                                                    );

                                                case "date":
                                                case "monthYear":
                                                    return(
                                                    <DateControl 
                                                        controlType={d.controlType}
                                                        labelName={d.parameterTitle}
                                                        fieldName={d.parameterName}
                                                        index={i}
                                                        isRequired={d.isRequired}
                                                        key={`idCount-${i+1}`}
                                                    />
                                                    );
                                                    case "inputText":
                                                        return(
                                                        <InputTextControl 
                                                            labelName={d.parameterTitle}
                                                            fieldName={d.parameterName}
                                                            index={i}
                                                            isRequired={d.isRequired}
                                                            maxlength={d.maxLength}
                                                            key={`idCount-${i+1}`}
                                                            processId={selectedInterfaceVal}
                                                        />
                                                        );
                                                default:
                                                    return null;

                                            }
                                          })
                                        }
                                        </div>
                                    </div>

                                    <div className="col-2 align-self-end text-end">
                                        <TpexSimpleButton color="primary" text="Submit" handleClick={event => submitInterface(event)} />
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div className="panelBox mt-10">
                    <div className="row">
                        <div className="col">
                            <div className="sub-heading"><i className="bg-border"></i><h2>{LABEL_CONST.PROCESS_STATUS_ERROR_MESS}</h2></div>
                        </div>
                    </div>
                    <div className="grid-panel">
                        <div className="row">
                            <div className="form-group col-5">
                                <label htmlFor='processName-dropdown-id'>{LABEL_CONST.PROCESS_NAME_DROPDOWN}</label>                                                
                                <TpexSelect 
                                    moduleName="processName-dropdown"
                                    options={processNameList}
                                    blankRequired={true} 
                                    selected={initialProcessNameVal}
                                    onChangeSelection={(e) => processSelectBoxChange(e)}
                                    hasValue={selectedProcessNameVal}
                                />                                          
                            </div>
                            <div className="form-group col-2">
                                <div className="customDatePicker mandatoryControl">
                                <label htmlFor='fromDateTime'>{LABEL_CONST.FROM_DATE_TIME}</label>
                                <TpexDatePicker
                                    className={"form-control"}
                                    id='fromDateTime'
                                    dateSelected={fromDateTime}
                                    handleDateSelected={date => setFromDateTime(date)}
                                    isDirectDatePicker={true}
                                    showDateAndTime={true}
                                />
                                
                                </div>
                            </div>
                            <div className="form-group col-2">
                                <div className="customDatePicker mandatoryControl">
                                    <label htmlFor='toDateTime'>{LABEL_CONST.END_DATE_TIME}</label>
                                    <TpexDatePicker
                                        className={"form-control"}
                                        id='toDateTime'
                                        dateSelected={endDateTime}
                                        handleDateSelected={(date) => setEndDateTime(date)}
                                        isDirectDatePicker={true}
                                        showDateAndTime={true}
                                    />
                                </div>
                            </div>
                            
                            <div className="form-group col-3 align-self-end text-end">
                                <TpexSimpleButton color="primary" text="Search" handleClick={e => searchInterface(e)} />
                            </div>
                        </div>
                        <div className="row mt-10 pb-10">
                            <div className="form-group col-5">
                                <label htmlFor='systemName-dropdownList-id'>{LABEL_CONST.SYSTEM_NAME}</label>                                                
                                <TpexSelect 
                                    moduleName="systemName-dropdownList"
                                    options={systemNameList}
                                    blankRequired={true} 
                                    selected={initialSystemNameVal}
                                    onChangeSelection={(e) => systemSelectBoxChange(e)}
                                    hasValue={selectedSystemNameVal}
                                />                                       
                            </div>
                            <div className="form-group col-7 mt-4">
                                <span className="align-middle order-type">{LABEL_CONST.CREATED_BY}</span>
                                <div className="form-check form-check-inline">
                                    <input
                                        className="form-check-input"
                                        type="radio"
                                        name="createdByRadio"
                                        id="createdBy-All"
                                        value="all_process"
                                        disabled={radioBtnDis}
                                        checked={createdByVal === "all_process"}
                                        onChange={handleRadioChange}
                                    />
                                    <label className="form-check-label ordertype-label" htmlFor="createdBy-All">{LABEL_CONST.ALL_PROCESS}</label>
                                </div>
                                <div className="form-check form-check-inline">
                                    <input
                                        className="form-check-input"
                                        type="radio"
                                        name="createdByRadio"
                                        id="createdBy-owner"
                                        value="owner_process"
                                        disabled={radioBtnDis}
                                        checked={createdByVal === "owner_process"}
                                        onChange={handleRadioChange}
                                    />
                                    <label className="form-check-label ordertype-label" htmlFor="createdBy-owner">{LABEL_CONST.OWNER_PROCESS}</label>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="grid-panel pt-0 filter-with-orderType">
                    <TpexTable 
                        rows={rows} 
                        idName="idCount" 
                        moduleName="INQUIRY_PROCESS_STATUS_CONTROL" 
                        rowPerPage={DEFAULT_GRID_RECORD} 
                        selectAll={false} 
                        selectRow={false}
                        columns={columns} 
                        isCrud={false}
                        pagination={true}
                        filter={true}
                        serverSideFilter={false}
                        editTable={false}
                        popupContent={popupTableData}
                        activeRadioBtn={createdByVal}
                        addEditDataForParent={addEditDataForParent}
                    />
                    </div>
                    <AlertModal
                        show={modalShow}
                        onHide={() => setModalShow(false)}
                        status={searchStatus}
                        content={searchContent}
                        handleClick={okConfirm}
                        parentBtnName={parentBtnName}
                    />
                    {/* modal */}
                    <ProcessLogDetailModal
                        show={modalPopUpShow}
                        popupRows={selRowPopupData}
                        onHide={() => setModalPopUpShow(false)}
                        errorBtnShow={errorBtnShow}
                    />
                    </div>
                </div>
            </main>
        </EditDataContext.Provider>
    )
};