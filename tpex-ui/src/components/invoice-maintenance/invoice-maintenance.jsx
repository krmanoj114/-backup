import React, { useState, useEffect } from 'react';
import './invoice-maintenance.css';
import "../../styles/table.css";
import TpexSimpleButton from '../../common/components/button';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { MODULE_CONST } from '../../constants/constant';
import { getRequest, putRequest } from '../../services/axios-client';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexSelect } from '../../common/components/select';
import { TpexLoader } from '../../common/components/loader/loader';
import { createMessage, objectsAreSame } from '../../helpers/util';
import AlertModal from '../../common/components/alert-modal/alert-modal';

function InvoiceMaintenance() {
    const [orderTypeValue, setOrderTypeValue] = useState("regular");
    const [invoiceList, setInvoiceList] = useState([]);
    const [regularType, setRegularType] = useState([]);
    const [cpoType, setCPOType] = useState([]);
    const [selectedOption, setSelectedOption] = useState([]);
    const [searchResult, setsearchResult] = useState({});
    const [searchResultCopy, setsearchResultCopy] = useState({});
    const [isLoading, setIsLoading] = useState(true);
    const [selectedProductGrp, setSelectedProductGrp] = useState([]);
    const [selectedPaymentTerm, setSelectedPaymentTerm] = useState([]);
    const [selectedSCAuth, setSelectedSCAuth] = useState('');
    const [scAuth, setSCAuth] = useState([]);
    const [scInvoiceFlag, setSCInvoiceFlag] = useState();
    const [paymentTermList, setPaymentTermList] = useState([]);
    const [productGrp, setProductGrp] = useState([]);
    const [editFLag, setEditFlag] = useState(false);

    const [initialPaymentTerm, setInitialPaymentTerm] = useState('');
    const [initialProductGrp, setInitialProductGrp] = useState('');
    const [initialSCAuth, setInitialSCAuth] = useState('');

    const [customerDetailsList, setCustomerDetailsList] = useState([]);
    const [selectedCustomer, setSelectedCustomer] = useState([]);
    const [initialSelectedCustomer, setInitialSelectedCustomer] = useState('');
    const [initialSelectedCustAdd, setInitialSelectedCustAdd] = useState('');

    const [notifyPartyList, setNotifyPartyList] = useState([]);
    const [selectedNotifyParty, setSelectedNotifyParty] = useState([]);
    const [initialNotifyParty, setInitialNotifyParty] = useState('');
    const [initialNotifyPartyAdd, setInitialNotifyPartyAdd] = useState('');
    const [custAndNotifyPartyAddr, setCustomerAndNotifyPartyAddr] = useState([]);

    const [consigneeDetailsList, setConsigneeDetailsList] = useState([]);
    const [selectedConsignee, setSelectedConsignee] = useState([]);
    const [initialConsignee, setInitialConsignee] = useState('');
    const [initialConsigneeAdd, setInitialConsigneeAdd] = useState('');
    const [consigneeAddr, setConsigneeAddr] = useState([]);
    const [notifyPartyAddr, setNotifyPartyAddr] = useState([]);

    const [selectedCarFamily, setselectedCarFamily] = useState([]);

    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");
    
    function setInputVal(fldName){
       return fldName && fldName !== null ? fldName : '';
    }
    function setInputNumberVal(fldName){
        return fldName!==undefined && (fldName >= 0 || fldName !== null) ? fldName:'';
     }
    function getInvoiceNosData() {
        setIsLoading(true);
        getRequest(MODULE_CONST.INVOICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.INVOICE_MAINTENANCE.INVOICE_LIST_API).then(dataRes => {
            setIsLoading(false);
            const regularArry = dataRes.data.reduce((accumulator, d) => {
                if(d.orderType && d.orderType==="R"){
                    return [...accumulator, {
                    value: d.invoiceNumber,
                    label: d.invoiceNumber 
                }];
                }
                return accumulator;
              }, []);
            const cpoArry = dataRes.data.reduce((accumulator, d) => {
                if(d.orderType && (d.orderType === "C" || d.orderType==="S")){
                  return [...accumulator, {
                    value: d.invoiceNumber,
                    label: d.invoiceNumber 
                }];
                }
                return accumulator;
              }, []);


              setRegularType(regularArry);
              setCPOType(cpoArry);
              
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportTypes =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    }

    function paymentProductList(list, ptCd, ptDesc, setList, obj, setValue){
        if(list && list !==null){
            const data = list.reduce((accumulator, d) => {
                return [...accumulator, {
                    value: d[ptCd] +"-"+ d[ptDesc],
                    label: d[ptCd] +"-"+ d[ptDesc] 
                }];
            }, []);
            
            setList(data);
            if(obj !==null){
                const defaultData = data.find(x=> {return x.value===obj});
                setValue(defaultData);
            } else {
                setValue([]);
            }
        }
    }

    function setSCAuthList(list, setList){
        if(list && list.length !==0){
            const scAuthList = list.reduce((accumulator, d) => {
                return [...accumulator, {
                    "id": d,
                    "name": d 
                }];
            }, []);
            
            setList(scAuthList);
        }
    }

    function addWithLabel(add, name){
        let labelWithAdd = '';
        if(add !== null){
            labelWithAdd = name + " | "+ add;
        } else {
            labelWithAdd = name;
        }
        return labelWithAdd;
    }

    function setDefaultState(listVal, setValue) {
        if(listVal !==null){
            setValue(listVal);
        } else { // if previous search have data and current have not data then it'll clear the state
            setValue('');
        }
    }

    function setDefaultStateArr(listVal, filtrData, setValue, setIniVal){
        if(listVal !==null){
            const data = filtrData.find(x=> {return x.value=== listVal});
            setValue(data);
            setIniVal(listVal);
        } else {
            setValue([]);
            setIniVal('');
        }
    }

    function setCarFamilyData(data, setValue){
        if(data && data !==null){
            setValue(data);
         }
    }
    function getSearchData(envNo) {
        setIsLoading(true);
        setsearchResult({});
        setsearchResultCopy({});
        setSCInvoiceFlag();
        setEditFlag(false);
        getRequest(MODULE_CONST.INVOICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.INVOICE_MAINTENANCE.INVOICE_SEARCH_API+`?envNo=${envNo}`).then(dataRes => {
            setIsLoading(false);
            paymentProductList(dataRes.data.paymentTermObj, "ptCd", "ptDesc", setPaymentTermList, dataRes.data.paymentTerm, setSelectedPaymentTerm);
            paymentProductList(dataRes.data.productGrpObj, "ipgProdGrpCd", "ipgProdGrpDesc", setProductGrp, dataRes.data.productGrp, setSelectedProductGrp);
            setSCAuthList(dataRes.data.scAuthorize, setSCAuth);

            setsearchResult(dataRes.data);
            setsearchResultCopy(dataRes.data)
            setSCInvoiceFlag(dataRes.data.scInv);

            setInitialPaymentTerm(dataRes.data.paymentTerm);
            setInitialProductGrp(dataRes.data.productGrp);
            setInitialSCAuth(dataRes.data.scAuth);

            if(dataRes.data.customercodeObj1 && dataRes.data.customercodeObj1 !==null){
                setCustomerAndNotifyPartyAddr(dataRes.data.customercodeObj1);
                const customerDetailsData = dataRes.data.customercodeObj1.reduce((accumulator, d) => {
                    return [...accumulator, {
                      value: d.indCust +"-"+ d.indCustNm,
                      label: addWithLabel(d.indCustAddr1, d.indCust +"-"+ d.indCustNm)
                  }];
                }, []);
                setCustomerDetailsList(customerDetailsData);
                setDefaultStateArr(dataRes.data.customerCodeName, customerDetailsData, setSelectedCustomer, setInitialSelectedCustomer);
                setDefaultState(dataRes.data.customerCodeAddress, setInitialSelectedCustAdd);
             }
             // Notify Party Data
             if(dataRes.data.notifyrcodeObj1 && dataRes.data.notifyrcodeObj1 !==null){
                setNotifyPartyAddr(dataRes.data.notifyrcodeObj1);
                const notifyPartyData = dataRes.data.notifyrcodeObj1.reduce((accumulator, d) => {
                    return [...accumulator, {
                      value: d.indNotifyName,
                      label: addWithLabel(d.indNotifyAddr1, d.indNotifyName)
                  }];
                }, []);
                const filteredData = notifyPartyData.filter((el) =>{ return el.value != null; });
                setNotifyPartyList(filteredData);
                setDefaultStateArr(dataRes.data.notifyName, notifyPartyData, setSelectedNotifyParty, setInitialNotifyParty);
                setDefaultState(dataRes.data.notifyCodeAddress, setInitialNotifyPartyAdd);
             }
             // Consignee details
             if(dataRes.data.companyNameObj && dataRes.data.companyNameObj !==null){
                setConsigneeAddr(dataRes.data.companyNameObj);
                const consigneeData = dataRes.data.companyNameObj.reduce((accumulator, d) => {
                    return [...accumulator, {
                      value: d.cmpName,
                      label: addWithLabel(d.cmpAdd1, d.cmpName)
                  }];
                }, []);
                setConsigneeDetailsList(consigneeData);
                setDefaultStateArr(dataRes.data.cmpNm, consigneeData, setSelectedConsignee, setInitialConsignee);
                setDefaultState(dataRes.data.cnsgCodeAddress, setInitialConsigneeAdd);        
             }
             setCarFamilyData(dataRes.data.carFamilyDto, setselectedCarFamily);
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getError =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
        
        
        
    }

    useEffect(() => {
        getInvoiceNosData();
    }, []);

    useEffect(() => {
        // Do nothing
    }, [selectedPaymentTerm]);
    useEffect(() => {
        // Do nothing
    }, [selectedProductGrp]);
    useEffect(() => {
        // Do nothing
    }, [searchResult]);

    

    useEffect(() => {        
        if(regularType && regularType.length > 0){
           setInvoiceList(regularType);
        }
    }, [regularType]);

    const handleRadioChange = e => {
        setOrderTypeValue(e.target.value);
        setSelectedOption([]);
        if(e.target.value ==="regular"){
            if(regularType && regularType.length > 0){
                setInvoiceList(regularType);
            } else {
                setInvoiceList([]);
             }
        }
        if(e.target.value ==="cpo/spo"){
            if(cpoType && cpoType.length > 0){
                setInvoiceList(cpoType);
             } else {
                setInvoiceList([]);
             }
        }
    }

    function handleSelectedOptions(e) {
        if(e!==null){
            setSelectedOption(e);
        } else {
            setSelectedOption([]);
        }
        
    }

    function selectBoxChange(e) {
        if(e===null || (e && e.target.value !== initialSCAuth)){
            setEditFlag(true);
        }
        else {
            setEditFlag(false);
        }
        setSelectedSCAuth(e.target.value);
    }

function finalAddress(selResult, addrKey ){
    const add1 = selResult[addrKey+'1']!==null?selResult[addrKey+'1']+" ":'';
    const add2 = selResult[addrKey+'2']!==null?selResult[addrKey+'2']+" ":'';
    const add3 = selResult[addrKey+'3']!==null?selResult[addrKey+'3']+" ":'';
    const add4 = selResult[addrKey+'4']!==null?selResult[addrKey+'4']:'';
    const completeAdd = add1+add2+add3+add4;
    return completeAdd;
}

function dropdownEdit(e, defaultVal, setSelectedValue){
    if(e===null || (e && e.label !== defaultVal)){
        setEditFlag(true);
    }
    else {
        setEditFlag(false);
    }
    setSelectedValue(e);
}
// common dropdown handel function
    function dropdownChange(e, loadVal, setValue) {
        dropdownEdit(e, loadVal, setValue);
        if(arguments[3]==="NotifyParty"){
            if(e!==null){
                const defaultNotifyPartyAddr = notifyPartyAddr.find(x=> {return x.indNotifyName===e.value});
                const finalAdd = finalAddress(defaultNotifyPartyAddr, 'indNotifyAddr'); 
                setsearchResult({
                    ...searchResult,
                    ["notifyCode"]: defaultNotifyPartyAddr.indNotify
                });
                setInitialNotifyPartyAdd(finalAdd);
            } else {
                setsearchResult({
                    ...searchResult,
                    ["notifyCode"]: ''
                });
                setInitialNotifyPartyAdd('');
            }
        }
        if(arguments[3]==="CustomerDetail"){
            if(e!==null){
                const defaultCustomerDetail = custAndNotifyPartyAddr.find(x=> {return (x.indCust+"-"+x.indCustNm)===e.value});
                const finalAdd = finalAddress(defaultCustomerDetail, 'indCustAddr'); 
                setInitialSelectedCustAdd(finalAdd);
            } else {
                setInitialSelectedCustAdd('');
            }
        }
        if(arguments[3]==="ConsigneeDetail"){
            if(e!==null){
                const defaultConsigneeDetail = consigneeAddr.find(x=> {return x.cmpName===e.value});
                const finalAdd = finalAddress(defaultConsigneeDetail, 'cmpAdd'); 
                setsearchResult({
                    ...searchResult,
                    ["cmpCd"]: defaultConsigneeDetail.cmpCd
                });
                setInitialConsigneeAdd(finalAdd);
            } else {
                setsearchResult({
                    ...searchResult,
                    ["cmpCd"]: ''
                });
                setInitialConsigneeAdd('');
            }
        }
    }

    function handleSearch(e){
         if(selectedOption && selectedOption.length===0){
            showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_IN_1013'));
            setsearchResult({});
            setsearchResultCopy({});
            setSCInvoiceFlag('');
            setPaymentTermList([]);
            setProductGrp([]);
            setSCAuth([]);

            setEditFlag(false);
            setSelectedProductGrp([]);
            setSelectedPaymentTerm([]);
            setSelectedSCAuth('');

            setSelectedCustomer([]);
            setInitialSelectedCustomer('');
            setCustomerDetailsList([]);     
            setInitialSelectedCustAdd('');  
            
            setNotifyPartyList([]);
            setSelectedNotifyParty([]);
            setCustomerAndNotifyPartyAddr([]);
            setInitialNotifyParty('');
            setInitialNotifyPartyAdd('');

            setConsigneeDetailsList([]);
            setSelectedConsignee([]);
            setConsigneeAddr([]);
            setNotifyPartyAddr([]);
            setInitialConsignee('');
            setInitialConsigneeAdd('');

            setselectedCarFamily([]);

            return false;
        } else if(editFLag===true){
            showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, "Search");
        }else {
            getSearchData(selectedOption.value);
        }
    }
    function isEmpty(obj) {
        for (let x in obj) { 
            if (obj.hasOwnProperty(x)) {
                return false;
            }
         }
        return true;
     }

    function handleSave(e){
        if(searchResultCopy && isEmpty(searchResultCopy)){
            showAlertMsg(LABEL_CONST.ERROR, LABEL_CONST.SAVE_WITHOUT_SEARCH);
            setEditFlag(false);
        } else 
        if(!isEmpty(searchResultCopy) 
            && ((selectedProductGrp===undefined || selectedProductGrp===null || isEmpty(selectedProductGrp))
            || (selectedPaymentTerm===undefined || selectedPaymentTerm===null || isEmpty(selectedPaymentTerm))
            || (selectedCustomer===undefined || selectedCustomer===null || isEmpty(selectedCustomer))
            || (selectedConsignee===undefined || selectedConsignee===null || isEmpty(selectedConsignee))
            || (searchResult.shipingMark1===undefined || searchResult.shipingMark1===null || searchResult.shipingMark1==='')
            || (searchResult.shipingMark2===undefined || searchResult.shipingMark2===null || searchResult.shipingMark2==='')
            || (searchResult.shipingMark3===undefined || searchResult.shipingMark3===null || searchResult.shipingMark3==='')
            || (searchResult.shipingMark5===undefined || searchResult.shipingMark5===null || searchResult.shipingMark5==='')
            || (searchResult.countryOfOriginFlg==="N" && (searchResult.shipingMark8===undefined || searchResult.shipingMark8===null || searchResult.shipingMark8===''))
            || (searchResult.description1===undefined || searchResult.description1===null || searchResult.description1==='')
            || (searchResult.description2===undefined || searchResult.description2===null || searchResult.description2==='')
            )) {
                showAlertMsg(LABEL_CONST.ERROR, LABEL_CONST.SELECT_MANDATORY_INFO);
            } 
        else
        if(!isEmpty(searchResultCopy) 
            && !isEmpty(searchResult) 
            && objectsAreSame(searchResult,searchResultCopy)
            && editFLag === false){
            showAlertMsg(LABEL_CONST.INFORMATION, LABEL_CONST.NO_CHANGES_TO_SAVE);
        }        
        else {
            showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.SAVE_CHANGE_CONFIRMATION, "Save");
        }
    }
    function updateInvDetail(req){
        setIsLoading(true);
        putRequest(MODULE_CONST.INVOICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.INVOICE_MAINTENANCE.INVOICE_UPDATE_API, req).then(dataRes => {
            setIsLoading(false);
              showAlertMsg(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
              getSearchData(selectedOption.value); // After update again search
        }).catch(function (error) {
            setIsLoading(false);
            console.log('getError =>', error);
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.statusMessage));
        });
    }
    function inputBoxChange(e, fldKey){
        if(e===null || (e && e.target.value !== searchResultCopy[fldKey])){
            setEditFlag(true);
        }
        else {
            setEditFlag(false);
        }
        setsearchResult({
            ...searchResult,
            [fldKey]: e.target.value
          });
      }
    const numberWithCommas = (x) => {
        return x.replace(/(\d{2})(\d{3})(\d{3})/, "$1,$2,$3");
    };
    const numberWithDot = (beforeDot, afterDot) => {
        return numberWithCommas(beforeDot) + '.' + afterDot;
    }
    const formatNumbers = (x) => { 
        if (x.indexOf('.') > -1){ 
            const [beforeDot, afterDot] = x.split('.');
            x = numberWithDot(beforeDot.replace(/[.,\s]/g, ''), afterDot.slice(0, 2)); 
        } else { 
            let res = x.toString().replace(/[.,\s]/g, ''); 
            if(res.length >= 9) { 
                x = numberWithDot(res.substr(0,8), res.substr(8)); 
            }else if(res.length < 9){
                 x = numberWithCommas(x) 
            } 
        } return x; 
    }

      function inputBoxChangeValid(e, fldKey){
        const re = /^[0-9.,]+$/;
        if(e===null || (e && e.target.value !== searchResultCopy[fldKey])){
            setEditFlag(true);
        }
        else {
            setEditFlag(false);
        }
        if (e.target.value === "" || re.test(e.target.value)) {
            setsearchResult({
                ...searchResult,
                [fldKey]: formatNumbers(e.target.value)
            });
        }
      };
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
            getSearchData(selectedOption.value);
        }

        if(e.target.title==='Save-Warning'){
            let notifyName = selectedNotifyParty && selectedNotifyParty.length > 0 ? selectedNotifyParty.value:initialNotifyParty;
            const reqData = {
                "invNo": selectedOption.value,
                "productGrpObj": selectedProductGrp.value,
                "scAuthorize": selectedSCAuth,
                "paymentTermObj": selectedPaymentTerm.value,
                
                "custCode": selectedCustomer.value,
                "consineeName": selectedConsignee.value,
                "notifyPartyName": notifyName,

                "indMark1": searchResult.shipingMark1,
                "indMark2": searchResult.shipingMark2,
                "indMark3": searchResult.shipingMark3,
                "indMark4": searchResult.shipingMark4,
                "indMark5": searchResult.shipingMark5,
                "indMark7": searchResult.shipingMark7,
                "indMark8": searchResult.shipingMark8,
                "indGoodsDesc1": searchResult.description1,
                "indGoodsDesc2": searchResult.description2,
                "indGoodsDesc3": searchResult.description3,
                "indGoodsDesc4": searchResult.description4,
                "indGoodsDesc5": searchResult.description5,
                "indGoodsDesc6": searchResult.description6,
                "freight":searchResult.freight,
                "insurance":searchResult.insurance
            }
            updateInvDetail(reqData);
        }
    }

    return (
        <>
        {/* spinner */}
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.INVOICE_MAINTENANCE} />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.INVOICE_MAINTENANCE}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 invoice-maintenance">
                                    <div className="form-group col-4">
                                    <div className="order-type">Order Type:</div>
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="orderType"
                                                id="inv_main_regular"
                                                value="regular"
                                                checked={orderTypeValue === "regular"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_main_regular">{LABEL_CONST.REGULAR}</label>
                                        </div>
                                    
                                        <div className="form-check form-check-inline mandatoryControl">
                                            <input
                                                className="form-check-input"
                                                type="radio"
                                                name="orderType"
                                                id="inv_main_cpo_spo"
                                                value="cpo/spo"
                                                checked={orderTypeValue === "cpo/spo"}
                                                onChange={handleRadioChange}
                                            />
                                            <label className="form-check-label ordertype-label" htmlFor="inv_main_cpo_spo">{LABEL_CONST.CPO_SPO}</label>
                                        </div>
                                    </div>
                                    <div className="form-group col-4 mandatoryControl">
                                        <div className="custom-multiSelect mt-10">
                                            <label>{LABEL_CONST.INVOICE_NO}</label>
                                            <TpexMultiSelectSeach
                                                id="inv_main_invoiceList"
                                                isMandatory={true}
                                                handleSelectedOptions={handleSelectedOptions}
                                                name={LABEL_CONST.INVOICE_NO}
                                                noOptionsText={LABEL_CONST.INVOICE_NO}
                                                value={selectedOption}
                                                isMulti={false}
                                                serverSide={false}
                                                staticValues={invoiceList}
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
                                <div className="panelBox mt-10 mb-20">
                                    <div className="grid-panel">
                                    <div className="row">
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.INVOICE_DATE}</label>                                                
                                            <input type="text" id="invoiceDate" className="form-control" disabled 
                                                   value={setInputVal(searchResult.invDate)} />                                            
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.ETD}</label>                                                
                                            <input type="text" id="invoiceETD" className="form-control" disabled 
                                                   value={setInputVal(searchResult.etdDate)} />                                            
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.ETA}</label>                                                
                                            <input type="text" id="invoiceETA" className="form-control" disabled
                                                   value={setInputVal(searchResult.etaDate)} />                                            
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.INVOICE_TYPE}</label>                                                
                                            <input type="text" id="invoiceType" className="form-control" disabled 
                                                   value={setInputVal(searchResult.invType)} />                                            
                                        </div>
                                        <div className="form-group col-1">
                                            <label>{LABEL_CONST.SC_INVOICE}</label>                                                
                                            <input type="text" id="scInvoice" className="form-control" disabled value={scInvoiceFlag || ''} />                                            
                                        </div>
                                        {scInvoiceFlag==='Y' && 
                                        <div className="form-group col-3">
                                            <label>{LABEL_CONST.SC_AUTHORIZED}</label>  
                                            <TpexSelect 
                                                moduleName="SC-Auth"
                                                options={scAuth}
                                                blankRequired={false} 
                                                selected={initialSCAuth}
                                                onChangeSelection={(e) => selectBoxChange(e)}
                                                hasValue={selectedSCAuth}
                                            />
                                        </div>
                                        }
                                    </div>

                                    <div className="row mt-10">
                                        <div className="form-group col-4">
                                            <label>{LABEL_CONST.OCEAN_VESSEL}</label>                                                
                                            <input type="text" id="oceanVessel" className="form-control" disabled 
                                                   value={setInputVal(searchResult.oceanVessel)} />                                            
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.OCEAN_VOYAGE}</label>                                                
                                            <input type="text" id="oceanVoyage" className="form-control" disabled 
                                                   value={setInputVal(searchResult.oceanVoyage)} />                                            
                                        </div>
                                        <div className="form-group col-2"></div>
                                        
                                        <div className="form-group col-4 mandatoryControl">
                                            <div className="custom-multiSelect">
                                                <label>{LABEL_CONST.PRODUCT_GROUP}</label>     
                                                <TpexMultiSelectSeach
                                                    isMandatory={true}
                                                    id="productGroup"
                                                    handleSelectedOptions={(e) => dropdownChange(e, initialProductGrp, setSelectedProductGrp)}
                                                    name={LABEL_CONST.PRODUCT_GROUP}
                                                    noOptionsText={LABEL_CONST.PRODUCT_GROUP}
                                                    value={selectedProductGrp}
                                                    isMulti={false}
                                                    serverSide={false}
                                                    staticValues={productGrp}
                                                />
                                            </div>
                                        </div>
                                        
                                    </div>
                                    <div className="row mt-10">
                                        <div className="form-group col-4">
                                            <label>{LABEL_CONST.FEEDER_VESSEL}</label>                                                
                                            <input type="text" id="feederVessel" className="form-control" disabled 
                                                   value={setInputVal(searchResult.feederVessel)} />                                            
                                        </div>
                                        
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.FEEDER_VOYAGE}</label>                                                
                                            <input type="text" id="feederVoyage" className="form-control" disabled
                                                   value={setInputVal(searchResult.feederVoyage)} />                                            
                                        </div>
                                        
                                    </div>
                                    <div className="row mt-10">
                                    <div className="form-group col-4">
                                            <label>{LABEL_CONST.PORT_OF_LOADING}</label>                                                
                                            <input type="text" id="portLoading" className="form-control" disabled
                                                   value={setInputVal(searchResult.portOfLoading)} />                                            
                                        </div>
                                        <div className="form-group col-4">
                                            <label>{LABEL_CONST.PORT_OF_DISCHARGE}</label>                                                
                                            <input type="text" id="portDischarge" className="form-control" disabled 
                                                   value={setInputVal(searchResult.portOfDischarge)} />                                            
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.FREIGHT}</label>
                                            <input type="text" id="freight" className="form-control" 
                                                   value={setInputNumberVal(searchResult.freight)}
                                                   maxLength={13}
                                                   onChange={(e) => inputBoxChangeValid(e, "freight")}
                                            />  
                                        </div>
                                        <div className="form-group col-2">
                                            <label>{LABEL_CONST.INSURANCE}</label>
                                            <input type="text" id="insurance" className="form-control"  
                                                   value={setInputNumberVal(searchResult.insurance)} 
                                                   maxLength={13}
                                                   onChange={(e) => inputBoxChangeValid(e, "insurance")}
                                            />
                                        </div>
                                        </div>
                                        <div className="row mt-10">
                                    <div className="form-group col-4">
                                            <label>{LABEL_CONST.CURRENCY_CODE}</label>
                                            <input type="text" id="currencyCode" className="form-control" disabled 
                                                   value={setInputVal(searchResult.currencyCode)} />                                            
                                        </div>
                                        <div className="form-group col-8 mandatoryControl">
                                            <div className="custom-multiSelect">
                                                <label>{LABEL_CONST.PAYMENT_TERM}</label>     
                                                <TpexMultiSelectSeach
                                                    isMandatory={true}
                                                    id="paymentTerm"
                                                    handleSelectedOptions={(e) => dropdownChange(e, initialPaymentTerm, setSelectedPaymentTerm)}
                                                    name={LABEL_CONST.PAYMENT_TERM}
                                                    noOptionsText={LABEL_CONST.PAYMENT_TERM}
                                                    value={selectedPaymentTerm}
                                                    isMulti={false}
                                                    serverSide={false}
                                                    staticValues={paymentTermList}
                                                />
                                            </div>
                                        </div>
                                    </div>
                        {/* Car Family Start */}
                        <div className="row mt-20">
                            <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.ORDER_AND_CAR_FAMILY_DETAILS}</h2></div>
                            </div>
                            <div className="mt-10">
                                <div className="grid-table m-0">
                                    <div className="row g-0">
                                            <div className="col">
                                                <div className="table-responsive">
                                                    <table className="table tpexTable displayTable">
                                                        <thead className="text-nowrap">
                                                            <tr>
                                                                <th>{LABEL_CONST.ORDER_NO}</th>
                                                                <th>{LABEL_CONST.CAR_FAMILY}</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                        {selectedCarFamily.length === 0 && <tr><td colSpan="2" className="noData">{LABEL_CONST.NO_DATA_FOUND}</td></tr>}
                                                        {selectedCarFamily.map((o,i) => {
                                                             const addKey = i;
                                                            return <tr key={`tblRow_${addKey}`} id={`tblRow_`+i}>
                                                                <td>{o.orderNo}</td>
                                                                <td>{o.carFamilyCode+"-"+o.carName}</td></tr>
                                                        })}
                                                        </tbody>
                                                    </table>
                                                </div>
                                            </div>
                                    </div> 
                                </div>
                            </div>
                            {/* Car Family End */}
                            <div className="row mt-20">                                
                                <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.CUSTOMER_DETAILS}</h2></div>
                                <div className="form-group col-12 mandatoryControl mt-10">
                                    <div className="custom-multiSelect" data-title={initialSelectedCustAdd}>
                                        <label>{LABEL_CONST.CODE}</label>     
                                        <TpexMultiSelectSeach
                                            isMandatory={true}
                                            id="customer-detail"
                                            handleSelectedOptions={(e) => dropdownChange(e, initialSelectedCustomer, setSelectedCustomer, "CustomerDetail")}
                                            name={LABEL_CONST.CODE}
                                            noOptionsText={LABEL_CONST.CODE}
                                            value={selectedCustomer}
                                            isMulti={false}
                                            serverSide={false}
                                            staticValues={customerDetailsList}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="row mt-20">                                
                                <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.NOTIFY_PARTY_DETAILS}</h2></div>
                                <div className="form-group col-1 mt-10">
                                    <label>{LABEL_CONST.CODE}</label>
                                    <input type="text" id="partyCode" className="form-control" disabled
                                        value={setInputVal(searchResult.notifyCode)} />
                                </div>
                                <div className="form-group col-11 mt-10">
                                    <div className="custom-multiSelect" data-title={initialNotifyPartyAdd}>
                                        <label>{LABEL_CONST.NAME}</label>     
                                        <TpexMultiSelectSeach
                                            id="notifyParty"
                                            handleSelectedOptions={(e) => dropdownChange(e, initialNotifyParty, setSelectedNotifyParty, "NotifyParty")}
                                            name={LABEL_CONST.NAME}
                                            noOptionsText={LABEL_CONST.NAME}
                                            value={selectedNotifyParty}
                                            isMulti={false}
                                            serverSide={false}
                                            staticValues={notifyPartyList}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="row mt-20">                                
                                <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.CONSIGNEE_DETAILS}</h2></div>
                                <div className="form-group col-1 mt-10">
                                    <label>{LABEL_CONST.CODE}</label>
                                    <input type="text" id="partyCode" className="form-control" disabled
                                        value={setInputVal(searchResult.cmpCd)} />
                                </div>
                                <div className="form-group col-11 mandatoryControl mt-10">
                                    <div className="custom-multiSelect" data-title={initialConsigneeAdd}>
                                        <label>{LABEL_CONST.NAME}</label>     
                                        <TpexMultiSelectSeach
                                            id="consigneeList"
                                            isMandatory={true}
                                            handleSelectedOptions={(e) => dropdownChange(e, initialConsignee, setSelectedConsignee,"ConsigneeDetail")}
                                            name={LABEL_CONST.NAME}
                                            noOptionsText={LABEL_CONST.NAME}
                                            value={selectedConsignee}
                                            isMulti={false}
                                            serverSide={false}
                                            staticValues={consigneeDetailsList}
                                        />
                                    </div>
                                </div>
                            </div>
                            <div className="row mt-20">                                
                                <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.SHIPPING_MARKS}</h2></div>
                                <div className="form-group col-3 mandatoryControl mt-10">
                                    <label>{LABEL_CONST.SHIPPING_MARK_1}</label>
                                    <input 
                                        type="text"
                                        id="shippingMark_1" 
                                        className="form-control" 
                                        maxLength={50}
                                        onChange={(e) => inputBoxChange(e, "shipingMark1")}
                                        value={setInputVal(searchResult.shipingMark1)} 
                                    />
                                </div>
                                <div className="form-group col-3 mandatoryControl mt-10">
                                    <label>{LABEL_CONST.SHIPPING_MARK_2}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_2" 
                                        className="form-control" 
                                        maxLength={50}
                                        onChange={(e) => inputBoxChange(e, "shipingMark2")}
                                        value={setInputVal(searchResult.shipingMark2)} />
                                </div>
                                
                                <div className="form-group col-6 mandatoryControl mt-10">
                                    <label>{LABEL_CONST.SHIPPING_MARK_3}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_3" 
                                        className="form-control" 
                                        maxLength={50}
                                        onChange={(e) => inputBoxChange(e, "shipingMark3")}
                                        value={setInputVal(searchResult.shipingMark3)} />
                                </div>
                            </div>
                            <div className="row mt-20">            
                                <div className="form-group col-12">
                                    <label>{LABEL_CONST.SHIPPING_MARK_4}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_4" 
                                        className="form-control" 
                                        maxLength={1000}
                                        onChange={(e) => inputBoxChange(e, "shipingMark4")}
                                        value={setInputVal(searchResult.shipingMark4)} />
                                </div>
                            </div>
                            <div className="row mt-20">
                                <div className="form-group col-12 mandatoryControl">
                                    <label>{LABEL_CONST.SHIPPING_MARK_5}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_5" 
                                        className="form-control" 
                                        maxLength={1000}
                                        onChange={(e) => inputBoxChange(e, "shipingMark5")}
                                        value={setInputVal(searchResult.shipingMark5)} />
                                </div>
                            </div>
                            <div className="row mt-20">  
                                <div className="form-group col-12">
                                    <label>{LABEL_CONST.SHIPPING_MARK_6}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_6" 
                                        className="form-control" 
                                        maxLength={1000} 
                                        disabled
                                        value={setInputVal(searchResult.shipingMark6)} />
                                </div>
                            </div>
                            <div className="row mt-20">  
                            
                                <div className="form-group col-6">
                                    <label>{LABEL_CONST.SHIPPING_MARK_7}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_7" 
                                        className="form-control" 
                                        maxLength={50}
                                        onChange={(e) => inputBoxChange(e, "shipingMark7")}
                                        value={setInputVal(searchResult.shipingMark7)} />
                                </div>          
                                <div className={"form-group col-6 " + (searchResult.countryOfOriginFlg && searchResult.countryOfOriginFlg ==='Y'? '' : 'mandatoryControl')}>
                                    <label>{LABEL_CONST.SHIPPING_MARK_8}</label>
                                    <input 
                                        type="text" 
                                        id="shippingMark_8" 
                                        className="form-control" 
                                        maxLength={50}
                                        onChange={(e) => inputBoxChange(e, "shipingMark8")}
                                        disabled={!!(searchResult.countryOfOriginFlg && searchResult.countryOfOriginFlg ==='Y') }
                                        value={setInputVal(searchResult.shipingMark8)} />
                                </div>
                            </div>
                            <div className="row mt-20">                                
                                <div className="heading heading-left-space"><i className="bg-border"></i><h2>{LABEL_CONST.DESCRIPTION_OF_GOODS}</h2></div>
                                <div className="form-group col-4 mandatoryControl mt-10">
                                    <label>{LABEL_CONST.DESCRIPTION_1}</label>
                                    <input 
                                        type="text"
                                        id="description_1"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description1")}
                                        value={setInputVal(searchResult.description1)} 
                                    />
                                </div>
                                <div className="form-group col-4 mandatoryControl mt-10">
                                    <label>{LABEL_CONST.DESCRIPTION_2}</label>
                                    <input 
                                        type="text"
                                        id="description_2"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description2")}
                                        value={setInputVal(searchResult.description2)} 
                                    />
                                </div>
                                <div className="form-group col-4 mt-10">
                                    <label>{LABEL_CONST.DESCRIPTION_3}</label>
                                    <input 
                                        type="text"
                                        id="description_3"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description3")}
                                        value={setInputVal(searchResult.description3)} 
                                    />
                                </div>
                            </div>
                            <div className="row mt-20">                                
                                <div className="form-group col-4">
                                    <label>{LABEL_CONST.DESCRIPTION_4}</label>
                                    <input 
                                        type="text"
                                        id="description_4"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description4")}
                                        value={setInputVal(searchResult.description4)} 
                                    />
                                </div>
                                <div className="form-group col-4">
                                    <label>{LABEL_CONST.DESCRIPTION_5}</label>
                                    <input 
                                        type="text"
                                        id="description_5"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description5")}
                                        value={setInputVal(searchResult.description5)} 
                                    />
                                </div>
                                <div className="form-group col-4">
                                    <label>{LABEL_CONST.DESCRIPTION_6}</label>
                                    <input 
                                        type="text"
                                        id="description_6"
                                        className="form-control"
                                        maxLength={30}
                                        onChange={(e) => inputBoxChange(e, "description6")}
                                        value={setInputVal(searchResult.description6)} 
                                    />
                                </div>
                            </div>
                            <div className='row mt-20'>
                                <div className="form-group col-12 align-self-end">
                                    <div className="d-flex justify-content-end">
                                        <TpexSimpleButton color="primary" text={LABEL_CONST.SAVE} handleClick={event => handleSave(event)} />
                                    </div>
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
    )
}
export {InvoiceMaintenance};