
import React, { useState, useCallback, useRef, useEffect } from 'react'
import { TpexLoader } from '../../common/components/loader/loader';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import SearchForm from './SearchForm.js';
import './style.css';
import PartMasterGridPanel from './PartMasterGridPanel';
import PmiDataContext from "../../context/PmiDataContext";
import { intialWarningRes} from './partMasterUtil.js';
import { getRequest } from '../../services/axios-client';
import { MODULE_CONST } from '../../constants/constant';
import {useSelector } from "react-redux/es/hooks/useSelector";

const PartMasterInquiry = () => {
    const [isLoading, setIsLoading] = useState(false);
    const [rows, setRows] = useState([]);    
    const [modalShowAlertPmi, setModalShowAlertPmi] = useState(false);
    const [messageTypePmi, setMessageTypePmi] = useState();
    const [messageTextPmi, setMessageTextPmi] = useState();
    const [actionCasePmi, setActionCasePmi] = useState("");
    const pmiDataContextRef = useRef([]);
    const [warningRes, setWarningRes] = useState(intialWarningRes );    
    const [inhouseShop, setInhouseShop] = useState([]);
    const companyCode = useSelector(state => state.app.currentCompanyCode);
    const setRowsPmi = useCallback((dataRows) => {
        setRows(dataRows);
    });

    const setPageLoaderPmi = (pageLoader) => {
        setIsLoading(pageLoader);
    };

    
    const getInhouseShop = () => {
        const {API_BASE_URL, INHOUSE_SHOP} = MODULE_CONST.PART_MASTER_INQUIRY;

        getRequest(API_BASE_URL, INHOUSE_SHOP + companyCode).then(dataRes => {
            const dataResponse = dataRes?.data?.inhouseDropdown;
            const obj = {id: "", name: ""};
            const rows = dataResponse.map((k, i) => {
                return {
                    id : k.insShopCd,
                    name : k.insShopCdDesc,
                };
            });
            rows.push(obj);
            setInhouseShop(rows);
          }).catch(function (_error) {
          }).finally(() => {
          })
    }
    
    const handleAlertConfirmPmi = (e) => {
        setModalShowAlertPmi(false);
        if (e.target.title === "unsaveddata-Warning") {  
            setWarningRes({...intialWarningRes , relaodGrid : true});
        }
        if (e.target.title === "addRecordCase-Warning") {  
            setWarningRes({...intialWarningRes , addRecord : true});
        }
        if (e.target.title === "editRecordCase-Warning") {  
            setWarningRes({...intialWarningRes , editRecord : true});
        }
        if (e.target.title === "deleteRecordCase-Warning") {  
            setWarningRes({...intialWarningRes , deleteRecord : true});
        }
    };
    
    const openAlertBoxPmi = (messegeTypePmi, messageTextPmi = "", typePmi = "") => {
        if (typePmi) {
            setActionCasePmi(typePmi);
        }
        setMessageTypePmi(messegeTypePmi);
        setMessageTextPmi(messageTextPmi);
        setModalShowAlertPmi(true)
    };

    useEffect(() => {
        getInhouseShop();
    }, [companyCode]);
    
    return (
    <>
        <PmiDataContext.Provider value={pmiDataContextRef}>

            <TpexLoader isLoading={isLoading} />

            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.PART_MASTER_INQUIRY} />

                    <SearchForm 
                        setRowsPmi = {setRowsPmi}
                        openAlertBoxPmi = {openAlertBoxPmi}
                        setPageLoaderPmi = {setPageLoaderPmi}
                        warningRes = {warningRes}
                        setWarningRes = {setWarningRes}
                    />

                    <PartMasterGridPanel
                        rows={rows}
                        setRowsPmi = {setRowsPmi}
                        openAlertBoxPmi={openAlertBoxPmi}
                        setPageLoaderPmi={setPageLoaderPmi}
                        warningRes = {warningRes}
                        setWarningRes = {setWarningRes}
                        inhouseShop = {inhouseShop}
                    />
                    
                </div>

                <AlertModal
                    handleClick={handleAlertConfirmPmi}
                    show={modalShowAlertPmi}
                    onHide={() => setModalShowAlertPmi(false)}
                    status={messageTypePmi}
                    content={messageTextPmi}
                    parentBtnName={actionCasePmi}
                />

            </main>

        </PmiDataContext.Provider>
    </>
    )
}

export default PartMasterInquiry;