import { TpexLoader } from '../../common/components/loader/loader';
import React, { useState, useEffect } from 'react';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { ShippingControlTable } from './shipping-control-table';
import './shipping-control-master.css';
import { getRequest } from '../../services/axios-client';
import { MODULE_CONST } from '../../constants/constant';
import AlertModal from '../../common/components/alert-modal/alert-modal';

export function ShippingControlMaster() {
    const [isLoading, setIsLoading] = useState(true);
    const pageSize = 10;
    const [rows, setRows] = useState([]);
    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");

    const [buyerList, setBuyerList] = useState([]);
    const [impCodeList, setImpCodeList] = useState([]);
    const [expCodeList, setExpCodeList] = useState([]);
    const [cfcCodeList, setCfcCodeList] = useState([]);
    const [seriesCodeList, setSeriesCodeList] = useState([]);
    const [partCodeList, setPartCodeList] = useState([]);
    const [portOfDischargeList, setPortOfDischargeList] = useState([]);
    const [productGroupList, setProductGroupList] = useState([]);
    const [consigneeList, setConsigneeList] = useState([]);
    const [notifyPartyList, setNotifyPartyList] = useState([]);
    const [tradeTermList, setTradeTermList] = useState([]);
    const [certificationOfOriginReportList, setcertificationOfOriginReportList] = useState([]);
    const [soldToMessrsList, setSoldToMessrsList] = useState([]);
    
    const [validationObj, setValidationObj] = useState(null);

    // Table grid data
    const columns = [
        { id: 'buyer', name: 'Buyer', type: 'select', isBlank: true, editFlag: true, required: true, selectList: buyerList },
        { id: 'impCode', name: 'Imp. Code', type: 'select', isBlank: true, editFlag: false, required: true, selectList: impCodeList },
        { id: 'expCode', name: 'Exp. Code', type: 'select', isBlank: true, editFlag: false, required: true, selectList: expCodeList },
        { id: 'cfcCodeLabel', name: 'CFC Code', type: 'multiselect', isMulti: false, editFlag: false, required: true, multiSelectList: cfcCodeList },
        { id: 'seriesLabel', name: 'Series', type: 'multiselect', isMulti: false, editFlag: false, required: true, multiSelectList: seriesCodeList },
        { id: 'setPartCode', name: 'Set-Part Code', type: 'select',isBlank: true, editFlag: false, required: true, selectList: partCodeList },
        { id: 'portOfDischargeLabel', name: 'Port of Discharge', type: 'multiselect', isMulti: false, editFlag: false, required: true, multiSelectList: portOfDischargeList },
        { id: 'productGroupLabel', name: 'Product Group', type: 'multiselect', isMulti: false, editFlag: true, required: true, multiSelectList: productGroupList },
        { id: 'folderName', name: 'Folder Name (Invoice)', type: 'text', maxLength: 25, required: true},
        { id: 'consigneeLabel', name: 'Consignee', type: 'multiselect', isMulti: false, editFlag: true, multiSelectList: consigneeList },
        { id: 'notifyPartyLabel', name: 'Notify Party Details', type: 'multiselect', isMulti: false, editFlag: true, multiSelectList: notifyPartyList },
        { id: 'goodDesc1', name: 'Good Desc1', type: 'text', required: true, maxLength: 30},
        { id: 'goodDesc2', name: 'Good Desc2', type: 'text', required: true, maxLength: 30 },
        { id: 'goodDesc3', name: 'Good Desc6', type: 'text', required: true, maxLength: 30 },
        { id: 'tradeTerm', name: 'Trade Term', type: 'select', isBlank: false, editFlag: true, required: true, selectList: tradeTermList },
        { id: 'certificationOfOriginReport', name: 'Certification of Origin Report (Save)', type: 'select', editFlag: true, required: true, selectList: certificationOfOriginReportList },
        { id: 'soldToMessrsLabel', name: 'Sold to Messrs.', type: 'multiselect', isMulti: false, editFlag: true, required: true, multiSelectList: soldToMessrsList },
        { id: 'plsFlag', name: 'PLS Flag', type: 'checkbox', editFlag: true },
        { id: 'updateDateTime', name: 'Update date time', date: true }
    ];
    useEffect(() => {
       onloadTableData();
    }, []);
    function refreshGrid() {
        console.log("upgrading the grid again");
        onloadTableData();
    }
    function filterItem(list, code){        
        const cfcVal = list.find(x=> {return x.value === code});
        if(cfcVal !== undefined){
            return cfcVal;
        }
        return false; 
    }
    function listObjData (d){
        return d === false ? []: d;
    }
    function listObjLabel (d){
        return d === false ? "": d.label;
    }
    function onloadTableData(){
        setIsLoading(true);
        getRequest(MODULE_CONST.SHIPPING_CONTROL_MASTER_LIST.API_BASE_URL, MODULE_CONST.SHIPPING_CONTROL_MASTER_LIST.ONLOAD_LIST_API).then(data =>{
            setIsLoading(false);
            const apiResult = data.data;
            if (apiResult.masterList) {
                if (apiResult.masterList.length <= 0) {
                    setRows([]);    
                } else {
                    const rowsNew = apiResult.masterList.map((k, i) => {
                        k.idCount = "idCount-"+ (i+1);
                        k.idList = i+1; 
    
                        const cfcVal = filterItem(apiResult.cfcCodeList, k.cfcCode);
                        k.cfcCodeLabelObj = listObjData(cfcVal);
                        k.cfcCodeLabel = listObjLabel(cfcVal);
    
                        const seriesVal = filterItem(apiResult.seriesList, k.series);
                        k.seriesLabelObj = listObjData(seriesVal);
                        k.seriesLabel = listObjLabel(seriesVal);
    
                        const portVal = filterItem(apiResult.portOfDischargeList, k.portOfDischarge);
                        k.portOfDischargeLabelObj = listObjData(portVal);
                        k.portOfDischargeLabel = listObjLabel(portVal);
    
                        const productGrpVal = filterItem(apiResult.productGroupList, k.productGroup);
                        k.productGroupLabelObj = listObjData(productGrpVal);
                        k.productGroupLabel = listObjLabel(productGrpVal);
    
                        const soldMesserVal = filterItem(apiResult.soldToMessrsList, k.soldToMessrs);
                        k.soldToMessrsLabelObj = listObjData(soldMesserVal);
                        k.soldToMessrsLabel = listObjLabel(soldMesserVal);
    
                        const consigneeVal = filterItem(k.consigneeList, k.consignee);
                        k.consigneeLabelObj = listObjData(consigneeVal);
                        k.consigneeLabel = listObjLabel(consigneeVal);
    
                        const notifyPartyVal = filterItem(k.notifyPartyList, k.notifyParty);
                        k.notifyPartyLabelObj = listObjData(notifyPartyVal);
                        k.notifyPartyLabel = listObjLabel(notifyPartyVal);
    
                        setConsigneeList(k.consigneeList);
                        setNotifyPartyList(k.notifyPartyList);
                        
                        return k;
                    });
                    setRows(rowsNew);
                }
            }
            setBuyerList(apiResult.buyerList);
            setImpCodeList(apiResult.impCodeList);
            setExpCodeList(apiResult.expCodeList);
            setCfcCodeList(apiResult.cfcCodeList);
            setSeriesCodeList(apiResult.seriesList);
            setPartCodeList(apiResult.setPartCodeList);
            setPortOfDischargeList(apiResult.portOfDischargeList);
            setProductGroupList(apiResult.productGroupList);
            setTradeTermList(apiResult.tradeTermList);
            setcertificationOfOriginReportList(apiResult.certificationOfOriginReportList);
            setSoldToMessrsList(apiResult.soldToMessrsList);
            const valObj = getValidationObject(columns)
            setValidationObj(valObj);
        }).catch(function (error) {
            setIsLoading(false);
            showAlertMsg(LABEL_CONST.ERROR, error.message);
        });
    }
    function getValidationObject(validationData) {
        return validationData.reduce(
          (acc, current) => {
            acc[current.id] = current;
            return acc;
          },
          {});
      }
    //Alert Box messages
    function showAlertMsg(title, content){
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
    }
    return (
        <>
            {/* spinner */}
            <TpexLoader isLoading={isLoading} />   
            <main id="main">
            <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.SHIPPING_CONTROL_MASTER} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.SHIPPING_CONTROL_MASTER}</h1></div>
                            </div>
                            <div className="grid-panel">
                                <ShippingControlTable 
                                    rows={rows}
                                    idName="idCount"
                                    primaryKey="idCount"
                                    defaultSortingId="idList"
                                    moduleName="SHIPPING_CONTROL_MASTER_LIST"
                                    rowPerPage={pageSize}
                                    selectAll={false}
                                    selectRow={true}
                                    columns={columns}
                                    pagination={true}
                                    isCrud={true}
                                    margin="gridTable"
                                    freezetable="tableinvoice"
                                    refreshGrid={refreshGrid}
                                    validationObj={validationObj}
                                />
                            </div>
                        </div>
                    </div>
            </div>
            <AlertModal
                show={alertModalShow}
                onHide={() => setAlertModalShow(false)}
                status={alertTitle}
                content={alertContent}
            />
            </main>
        </>
    )
}