import DataTable from "./DataTable";
import React, { useContext, useEffect } from 'react';
import PmiDataContext from "../../context/PmiDataContext";
import {intialWarningRes, validationForAdd, validationForEdit, checkPostPayload, deletePayload , partTypeList} from './partMasterUtil.js';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { createMessage, createMesssageReplacer } from '../../helpers/util';
import { MODULE_CONST } from '../../constants/constant';
import { postRequest, deleteRequest, putRequest } from '../../services/axios-client';
import {useSelector } from "react-redux/es/hooks/useSelector";

const PartMasterGridPanel = ({
    rows,
    setPageLoaderPmi,
    openAlertBoxPmi,
    warningRes,
    setWarningRes,
    inhouseShop,
    setRowsPmi,
}) => {

    const pmiDataContextRef = useContext(PmiDataContext);
    const addDataPmi  = () => pmiDataContextRef?.addData ? pmiDataContextRef.addData : [];
    const editDataPmi = () => pmiDataContextRef?.editData ? pmiDataContextRef.editData : [];
    const userId = "LoginUserId";
    const companyCode = useSelector(state => state.app.currentCompanyCode);    
    

    const tableColumns = [
        { id: 'partNo', name: 'Part No.', type: 'text', isBlank: true, editFlag: false, required: true, maxLength : 14, maxWithoutFormat : 12, checkMaxValidation : true },
        { id: 'partName', name: 'Part Name', type: 'text', isBlank: true, editFlag: true, required: true, maxLength : 40, checkMaxValidation : true },
        { id: 'partType', name: 'Part Type', type: 'select', isBlank: true, editFlag: true, required: true, selectList: partTypeList},
        { id: 'inhouseShop', name: 'Inhouse Shop', type: 'select', isBlank: true, editFlag: true, required: true, requiredIf : 'partType', requiredIfValue : '3' , selectList: inhouseShop },
        { id: 'partWeight', name: 'Part Weight', type: 'text', isBlank: true, editFlag: true, required: true, valueCanNotBe : '0', maxLength : 9, maxWithoutFormat : 8, checkMaxValidation : false },
    ];

    const getValidationObject = (validationData) => {
        return validationData.reduce(
            (acc, current) => {
            acc[current.id] = current;
            return acc;
            },
        {});
    };
    
    const validationObj = getValidationObject(tableColumns);
    
    const validateAddRecords = (addDataPmi) => {
        const isValidationPassed = validationForAdd(addDataPmi, validationObj);
        
        if (isValidationPassed.length) {
            let counter = 0;
            openAlertBoxPmi(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
                return <p key={`erradd-${counter++}`}>{a}</p>;
            }));

        } else {
            openAlertBoxPmi(LABEL_CONST.WARNING, LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES, 'addRecordCase');
        }
    }

    const valdiateEditRecords = () => {
        const editRecordPmi = editDataPmi();
        const isValidationPassed = validationForEdit(editRecordPmi, rows, validationObj);
        if (isValidationPassed?.length) {
            if(isValidationPassed.join("\n") !== LABEL_CONST.NO_CHANGE_TO_SAVE){
                let counter2 = 0;
                openAlertBoxPmi(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
                return <p key={`erredit-${counter2++}`}>{a}</p>;
                }
                ));
            } else {
                openAlertBoxPmi(LABEL_CONST.INFORMATION, isValidationPassed.join("\n"));
            }
        } else {
            openAlertBoxPmi(LABEL_CONST.WARNING, LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES, 'editRecordCase');
        }
    }

    const addUpdatePmi = () => {
        const addRecordPmi = addDataPmi();        
        if(addRecordPmi.length){
            validateAddRecords(addRecordPmi);
        }else{
            valdiateEditRecords();
        }        
    }

    const catchErrArr =(errKey) => {
        if(Object.keys(errKey.errorMessageParamsArray.Priority_N).length > 0){
          let errData = {};
          const messageAfterReplace = createMesssageReplacer(errData, errKey.exception);
          openAlertBoxPmi(LABEL_CONST.ERROR, messageAfterReplace);
        }
    }

    const catchErrHandle = (error) => {
        let errKey = error.response.data;
        if (errKey.errorMessageParams && Object.keys(errKey.errorMessageParams).length > 0) {
            let messageAfterReplace = createMesssageReplacer(errKey.errorMessageParams, errKey.exception);
            messageAfterReplace = messageAfterReplace.replace(/}/g, '');
            openAlertBoxPmi(LABEL_CONST.ERROR, messageAfterReplace);
        }
        else if (errKey.errorMessageParamsArray && Object.keys(errKey.errorMessageParamsArray).length > 0) {
          catchErrArr(errKey);
        } else {
          if(errKey.exception==='INFO_CM_3008'){
                openAlertBoxPmi(LABEL_CONST.INFORMATION, createMessage(errKey.exception));
            } else{
                openAlertBoxPmi(LABEL_CONST.ERROR, createMessage(errKey.exception));
            }
          }
    }

    const addPmiRecord = () => {
        const addRecordPmi = addDataPmi();
        
        const addPayload = checkPostPayload(addRecordPmi, userId, companyCode);

        const {API_BASE_URL, Add_URL} = MODULE_CONST.PART_MASTER_INQUIRY;
        
        postRequest(API_BASE_URL, Add_URL, addPayload).then(dataRes => {
            setPageLoaderPmi(false);
            if (Number(dataRes.data.statusCode) === 200) {
                setWarningRes({...intialWarningRes , relaodGrid : true});
                openAlertBoxPmi(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
            } else {
                openAlertBoxPmi(LABEL_CONST.ERROR, createMessage(LABEL_CONST.RECORD_EDIT_FAILED));
            }
        }).catch(function (error) {
            setPageLoaderPmi(false);
            catchErrHandle(error);
        });
    }

    const updatePmiRecord = () => {
        const editRecordPmi = editDataPmi();

        const editPayload = checkPostPayload(editRecordPmi, userId, companyCode);

        const {API_BASE_URL, EDIT_URL} = MODULE_CONST.PART_MASTER_INQUIRY;

        putRequest(API_BASE_URL, EDIT_URL, editPayload).then(dataRes => {
            setPageLoaderPmi(false);
            if (Number(dataRes.data.statusCode) === 200) {
                setWarningRes({...intialWarningRes , relaodGrid : true});
                openAlertBoxPmi(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
            } else {
                openAlertBoxPmi(LABEL_CONST.ERROR, createMessage(LABEL_CONST.RECORD_EDIT_FAILED));
            }
        }).catch(function (error) {
            setPageLoaderPmi(false);
            catchErrHandle(error);
        });

    }

    const  catchErrorHandle = (error) => {
        let errKey = error.response.data.errorMessageParamsArray;
        if (errKey && Object.keys(errKey).length > 0) {
          if(Object.keys(errKey.vanDateFrom).length > 0){
            let errMessage = [];
            for (let prop of errKey.vanDateFrom) {
              const errText = createMesssageReplacer({vanDateFrom:prop}, error.response.data.statusMessage);
              errMessage.push(errText);
            }
            openAlertBoxPmi(LABEL_CONST.ERROR, errMessage.join("\n"));
          }
        } else {
            openAlertBoxPmi(LABEL_CONST.ERROR, createMessage(error.response.data.statusMessage));
        }      
    }

    const deletePmiRecord = () => {
        const editRecordPmi = editDataPmi();

        const deleteData = {
            data : deletePayload(editRecordPmi, companyCode),
            "userId": userId
        }

        setPageLoaderPmi(true);
        const {API_BASE_URL, DELETE_URL} = MODULE_CONST.PART_MASTER_INQUIRY;

        deleteRequest(API_BASE_URL, DELETE_URL, deleteData).then(delResponse => {
            const data = delResponse?.data;
            setPageLoaderPmi(false);
            if (Number(data.statusCode) === 200) {                
                setWarningRes({...intialWarningRes , relaodGrid : true});
                openAlertBoxPmi(LABEL_CONST.INFORMATION, createMesssageReplacer({ count: data?.errorMessageParams?.count }, data?.statusMessage, false));
            } else {
                openAlertBoxPmi(LABEL_CONST.ERROR, delResponse.exception);
            }
        }).catch(function (error) {
            setPageLoaderPmi(false);
            if(error.response.data.statusCode === 417){
                error.response.data.exception = error.response.data.statusMessage;
                openAlertBoxPmi(LABEL_CONST.ERROR, createMesssageReplacer({ errorCode: error.response.data.errorMessageParams.errorCode }, error.response.data.statusMessage, false));
            } else if(error.response.data.exception==='ERR_CM_3005'){
                openAlertBoxPmi(LABEL_CONST.ERROR, createMesssageReplacer({ errorCode: error.response.data.errorMessageParams.errorCode }, error.response.data.exception));
            } else{
                catchErrorHandle(error);
            }
        });
    }

    useEffect(()=>{
        if(warningRes.addRecord === true){
            addPmiRecord();
            warningRes.addRecord && setWarningRes({...intialWarningRes, addRecord : false});
        }
        if(warningRes.editRecord === true){
            updatePmiRecord();
            warningRes.editRecord && setWarningRes({...intialWarningRes, editRecord : false});
        }
        if(warningRes.deleteRecord === true){
            deletePmiRecord();
            warningRes.deleteRecord && setWarningRes({...intialWarningRes, deleteRecord : false});
        }
    }, [warningRes]);

    useEffect(() => {

    }, [companyCode]);

    return (
        <div className="panelBox part-master-inquiry">
            <div className="grid-panel">
                <DataTable 
                    rows = {rows}
                    columns = {tableColumns}
                    openAlertBoxPmi = {openAlertBoxPmi}
                    savePmi = {addUpdatePmi}
                    setRowsPmi={setRowsPmi}
                    
                />
            </div>
        </div>
    );
};

export default PartMasterGridPanel;