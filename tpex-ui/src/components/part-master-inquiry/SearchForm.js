import React, { useState, useContext, useEffect } from "react";
import TpexSimpleButton from "../../common/components/button";
import { TpexSelect } from "../../common/components/select";
import { LABEL_CONST } from '../../constants/label.constant.en';
import './style.css';
import { postRequest } from '../../services/axios-client';
import { MODULE_CONST } from '../../constants/constant';
import {formatPartNumber, getFormattedResponse, partTypeList, removeSpecialChar, initialValues, intialWarningRes} from './partMasterUtil.js';
import PmiDataContext from "../../context/PmiDataContext";
import {useSelector } from "react-redux/es/hooks/useSelector";

const SearchForm = (props) => {
    const pmiDataContextRef = useContext(PmiDataContext);
    const [values, setValues] = useState(initialValues);    
    const companyCode = useSelector(state => state.app.currentCompanyCode);

    const getPartMasterInquiryData = () => {
        props.setPageLoaderPmi(true);
        const {API_BASE_URL, SEARCH_URL} = MODULE_CONST.PART_MASTER_INQUIRY;
        const {partNumber, partName, partType} = values;
        const params = { 
            cmpCd : companyCode,
            partNo : removeSpecialChar(partNumber),
            partName : partName,
            partType : partType
        };
    
        postRequest(API_BASE_URL, `${SEARCH_URL}`, params)
                .then( dataRes => {
                    const dataResponse = dataRes?.data;
                    if(dataResponse.length > 0){
                        const dataRows = getFormattedResponse(dataResponse);
                        props.setRowsPmi(dataRows);
                    }else{
                        props.setRowsPmi([]);
                        props.openAlertBoxPmi(LABEL_CONST.INFORMATION, LABEL_CONST.NO_DATA_FOUND);
                    }
                }).catch(error => {
                    props.openAlertBoxPmi(LABEL_CONST.ERROR, error?.response?.data?.error);
                }).finally(() => {
                    props.setPageLoaderPmi(false);                    
                    props.warningRes.relaodGrid && props.setWarningRes({...intialWarningRes, relaodGrid : false});
        });

    };

    const checkValid = () => (values.partType !== '' || values.partName !== '' || values.partNumber !== '');

    const validateSearchForm = () => {
        const isValid = checkValid();
        if(isValid){
            getPartMasterInquiryData();
        }else{
            props.openAlertBoxPmi(LABEL_CONST.ERROR, LABEL_CONST.INPUT_ATLEAST_ONE_SEARCH);
        }
    };

    const onSearchClick = () => {
        const addLength = pmiDataContextRef?.addData ? pmiDataContextRef.addData.length : 0;
        const editLength = pmiDataContextRef?.editData ? pmiDataContextRef.editData.length : 0;        
        if (addLength > 0 || editLength > 0) {
            props.openAlertBoxPmi(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, 'unsaveddata');
        } else {
            validateSearchForm();
        }
    };
    
    const handlePartNumberChange = (name, value) => {   
        const formatedValue = formatPartNumber(value);
        handleInputChangePmi(name, formatedValue);  
    };

    const handleInputChangePmi = (name, value) => {        
        setValues({
            ...values,
            [name]: value,
        });
    };

    

    const reloadGrid = () => {
        const isValid = checkValid();
        if(isValid){
            getPartMasterInquiryData();
        }else{
            props.setRowsPmi([]);
        }
    }
    useEffect(()=>{
        if(props.warningRes.relaodGrid){
            reloadGrid();
        }
    }, [props.warningRes]);

    useEffect(() => {        
        pmiDataContextRef.searchValue = values;
    }, [values]);

    useEffect(() => {
        setValues(initialValues);
        props.setWarningRes({...intialWarningRes , relaodGrid : true});
    }, [companyCode]);

    return (
        <div className="panelBox">
            <div className="search-panel">
                <div className="row g-0">
                    <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.PART_MASTER_INQUIRY}</h1></div>
                </div>
                <form>
                    <div className="row mt-10 mb-10 pb-10">
                        <div className="col-2">
                            <div className="custom-multiSelect mandatoryControl">
                                <label>Part No.</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="partno"
                                    name="partNumber"
                                    maxLength="14"
                                    value={values.partNumber}
                                    onChange={(e) => handlePartNumberChange('partNumber', e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="col-5">
                            <div className="custom-multiSelect mandatoryControl">
                                <label>Part Name</label>
                                <input
                                    type="text"
                                    className="form-control"
                                    id="partName"
                                    name="partName"
                                    maxLength="40"
                                    value={values.partName}
                                    onChange={(e) => handleInputChangePmi('partName', e.target.value)}
                                />
                            </div>
                        </div>
                        <div className="col-2">
                            <div className="custom-multiSelect mandatoryControl">
                                <label>Part Type</label>
                                <TpexSelect
                                    selected=""
                                    id="partType"
                                    hasValue={values.partType}
                                    onChangeSelection={(e) => handleInputChangePmi('partType', e.target.value)}
                                    options={partTypeList}
                                />
                            </div>
                        </div>
                        <div className="col-3 align-self-end">
                            <div className="d-flex justify-content-end">
                                <TpexSimpleButton
                                    color="btn btn-primary"
                                    handleClick={onSearchClick}
                                    text={LABEL_CONST.SEARCH}
                                />
                            </div>
                        </div>
                    </div>

                </form>
            </div>
        </div>
    )

}


export default SearchForm