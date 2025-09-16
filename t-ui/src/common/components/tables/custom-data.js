import { copyObject } from "../../../helpers/util";
import { LABEL_CONST } from "../../../constants/label.constant.en";

const removeForPxpPartPrice = (e, moduleName = '') => {
    if (moduleName === "PXP_PART_PRICE_MAINTENANCE") {
        e.currency = e?.currency?.value ? e.currency.value : e.currency.substring(0, 3);
        e.partPrice = +e.partPrice.replaceAll(/\,/g, '');
        e.id && delete e.id;                
    }
}

const updateCodeMaster = (e, moduleName = '', p = {}) => {
    if (moduleName === "CODE_MASTER" && +p.codeMaster === 4) {        
        e.CURR_CD = e.CURR_CD.substring(0, 3);
        e.BUY_CD = e.BUY_CD.substring(0, 3);
    }
} 

const updatePaymentMasterDetial = (e ) => {    
    if("NO_OF_DAYS_DISABLE" in e) delete e.NO_OF_DAYS_DISABLE;  
    if("DAY_OF_MTH_DISABLE" in e) delete e.DAY_OF_MTH_DISABLE;  
    if("MTH_NO_DISABLE" in e) delete e.MTH_NO_DISABLE;
    if(e.PAYMENT_TERM === "InDays") {
        e.MTH_NO = 0;
        e.DAY_OF_MTH = 0;
    }

    if(e.PAYMENT_TERM === "InMonth") e.NO_OF_DAYS = 0;
}
const updatePaymentTermMaster = (e, codeMasterName = '') => {
    if(codeMasterName !== '' && codeMasterName === 'Payment Term Master'){
        updatePaymentMasterDetial(e);

        e.REGULAR = e.REGULAR === true || e.REGULAR === "Y" ? "Y" : "N";
        e.CPO_SPO = e.CPO_SPO === true || e.CPO_SPO === "Y" ? "Y" : "N";
    }
}
const mapCodeMaster = (e, moduleName = '', codeMasterName = '', p = {}) => {
    
    e.idList && delete e.idList;
    e.UPD_BY = "TestUser";
    e.UPD_DT = "SYSDATE";

    removeForPxpPartPrice(e, moduleName);
    updateCodeMaster(e, moduleName, p);
    updatePaymentTermMaster(e, codeMasterName);
    return e;
}
export function getEditPayload(editDataForPaload = {}, otherData = {}, p = {},codeMasterDetail = {}) {
    let codeMasterName = codeMasterDetail?.codeMasterName;
    let codeMasterId = codeMasterDetail?.codeMasterId;
    let userId = codeMasterDetail?.userId;
    let moduleName = codeMasterDetail?.moduleName;
    let primaryKey = codeMasterDetail?.primaryKey;
    let tableName = codeMasterDetail?.tableName;
    if (moduleName === "LOT_PRICE_MASTER") {
        const keys = Object.keys(editDataForPaload);
        const editDataSelected = Object.values(editDataForPaload).map((e, i) => {
            e.idList && delete e.idList;
            e.updateBy = "Test User";
            e.partUsage = parseInt(e.partUsage);
            e.partNumber = keys[i];
            return e;
        })
        return {
            data: editDataSelected,
            effectiveFromMonth: otherData.effectiveFromMonth,
            effectiveToMonth: otherData.effectiveToMonth,
            carFamily: otherData.carFamily.value,
            finalDestination: otherData.destination.value,
            lotCode: otherData.lotCode,
            currency: otherData.currency,
            partNameConfirmation: p.p1,
            partusageConfirmation: p.p2
        };
    } else {
        const editDataSelected = Object.values(editDataForPaload).map(e => {
            return mapCodeMaster(e, moduleName, codeMasterName, p);
        });
        
        if (moduleName === "PXP_PART_PRICE_MAINTENANCE") {       
            return {
                data: editDataSelected,
                userId    
            };            
        } else {

            if(codeMasterName !== '' && codeMasterName === 'Payment Term Master'){
                return {
                    data: editDataSelected,
                    userId,
                    primaryKey,
                    tableName,
                    codeMasterName : codeMasterName,
                    codeMasterId : codeMasterId
                };
            }
            return {
                data: editDataSelected,
                userId,
                primaryKey,
                tableName
            };
        }

    }
}


export function getDeletedPayload(isCheck, primaryKey, userId, tableName, moduleName, rows, editDataForPaload = {}) {
    if (moduleName === "PXP_PART_PRICE_MAINTENANCE") {
        const getDeletedIds = Object.keys(editDataForPaload);
        const editDataSelected = rows.filter(r => getDeletedIds.includes(r.id)).map(e => {
            e.idList && delete e.idList;
            e.id && delete e.id;
            e.UPD_BY = "TestUser";
            e.UPD_DT = "SYSDATE";
            e.currency = e?.currency?.value ? e.currency.value : e.currency.substring(0, 3);
            e.partPrice = +e.partPrice.replaceAll(/\,/g, '');
            return e;
        });

        return {
            data: editDataSelected,
            userId
        };

    } else {
        return {
            data: isCheck.map(d => {
                return { [primaryKey]: d }
            }),
            userId,
            primaryKey,
            tableName
        }
    }
}

const getCodeMasterObj = (r) => {
    if("NO_OF_DAYS_DISABLE" in r) delete r.NO_OF_DAYS_DISABLE;  
    if("DAY_OF_MTH_DISABLE" in r) delete r.DAY_OF_MTH_DISABLE;  
    if("MTH_NO_DISABLE" in r) delete r.MTH_NO_DISABLE;

    r.REGULAR = r.REGULAR === true || r.REGULAR === "Y" ? "Y" : "N";
    r.CPO_SPO = r.CPO_SPO === true || r.CPO_SPO === "Y" ? "Y" : "N";

    if(r.PAYMENT_TERM === "InDays") {
        r.MTH_NO = 0;
        r.DAY_OF_MTH = 0;
    }

    if(r.PAYMENT_TERM === "InMonth") r.NO_OF_DAYS = 0;

    r.UPD_BY = "TestUser";
    r.UPD_DT = "SYSDATE";                
    return r;
}

export function getAddPayload(request, userId, tableName, moduleName, searchCriteria, codeMasterName = '', codeMasterId = '') {
    if(codeMasterName !== '' && codeMasterName === 'Payment Term Master'){
        
        return {
            data: copyObject(request).map(r => {
                return getCodeMasterObj(r);
            }),
            userId,
            tableName,
            codeMasterName : codeMasterName,
            codeMasterId : codeMasterId
        }
    }
    return {
        data: copyObject(request).map(r => {
            r.UPD_BY = "TestUser";
            r.UPD_DT = "SYSDATE";
            if (moduleName === "PXP_PART_PRICE_MAINTENANCE") {
                r.currency = r?.currency?.value ? r.currency.value : r.currency.substring(0, 3);
                r.partPrice = +r.partPrice.replaceAll(/\,/g, '');
                r.carFamilyCode = searchCriteria.carFamilyCode;
                r.importerCode = searchCriteria.importerCode;
            }
            return r;
        }),
        userId,
        tableName
    }
}

export function getHttpMethod(moduleName) {
    if (moduleName === "LOT_PRICE_MASTER") {
        return "put";
    }
    else {
        return "post";
    }
}

export function updateRowsClientSide(data, editedRecord, moduleName) {
    if (moduleName === "LOT_PRICE_MASTER") {
        const keys = Object.keys(editedRecord)
        const editedRecordArr = Object.values(editedRecord).map((rec, i) => {
            rec.partNumber = keys[i];
            return rec;
        });

        const dataArr = data.map(d => {
            const found = editedRecordArr.find(e => e.partNumber == d.partNumber);
            if (found) {
                return found
            } else {
                return d;
            }
        })

        return dataArr;
    }
}

export function customMessageModuleWise(moduleName, errMessage, key, validationValue, type = "", isFinalDestMaster = false) {
    
    if (validationValue && key === "partPrice" && validationValue <= 0 && moduleName === 'LOT_PRICE_MASTER' && type === 'number') {
        if (errMessage.indexOf(LABEL_CONST.ERR_IN_1011) === -1) {
            errMessage.push(LABEL_CONST.ERR_IN_1011);
        }
    }

    if (validationValue && key === "partUsage" && validationValue <= 0 && moduleName === 'LOT_PRICE_MASTER' && type === 'number') {
        if (errMessage.indexOf(LABEL_CONST.ERR_IN_1012) === -1) {
            errMessage.push(LABEL_CONST.ERR_IN_1012);
        }
    }

    if (moduleName === 'LOT_PRICE_MASTER' && key === "partName" && type === 'text' && validationValue.includes("~")) {
        if (errMessage.indexOf(LABEL_CONST.ERR_IN_1010) === -1) {
            errMessage.push(LABEL_CONST.ERR_IN_1010);
        }
    }

    customFinalMaster(errMessage, isFinalDestMaster, key);    

    return errMessage;
}

function customFinalMaster(errMessage, isFinalDestMaster, key){
    if(isFinalDestMaster && key === "DST_CD" && (errMessage.indexOf(LABEL_CONST.ERR_FDM_1001) === -1)){
        errMessage.push(LABEL_CONST.ERR_FDM_1001)
    }    
}

export function isInCustomList(moduleName) {
    return ['LOT_PRICE_MASTER', 'PXP_PART_PRICE_MAINTENANCE'].includes(moduleName);
}

export function changeInputFormat(value, module) {
    if (module === 'PXP_PART_PRICE_MAINTENANCE') {
        let partNumber = value.split('-').join('');
        return partNumber ? partNumber.match(/.{1,5}/g).join('-') : "";
    } else {
        return value;
    }
   
}

const regexDot = /^[0-9.]+$/;
const validateNumber = (value) => regexDot.test(value);

export const formatPartNo = (value, previousValue) =>  {
    if(value === "" || validateNumber(value)){
      return formatNumbers(value);
    }else{
      return previousValue;
    }
  };

export const formatPriceDisplay = (x) => {
    const val = x.toString();
    const index = val.indexOf(".");
    let firstValue = '';
    let secondValue = '';
    if (index > -1) {
        const [beforeDot, afterDot] = val.split('.');
        firstValue = beforeDot;
        secondValue = afterDot;
    } else if (index === -1) {
        if (val.length > 11) {
            firstValue = val.slice(0, 11);
            secondValue = val.slice(11, 13);
        } else {
            firstValue = val;
        }
    }
    const formattedValue = Number(firstValue).toLocaleString();
    const retValue = secondValue != '' ? formattedValue + "." + secondValue : formattedValue;
    return retValue;
}

const numberWithDot = (beforeDot, afterDot) => {
    return beforeDot + '.' + afterDot;
}
export const formatNumbers = (x) => {
    if (x.indexOf('.') > -1) {
        const [beforeDot, afterDot] = x.split('.');
        x = numberWithDot(beforeDot.replace(/[.,\s]/g, ''), afterDot.slice(0, 2));
    } else {
        let res = x.toString().replace(/[.,\s]/g, '');
        if (res.length >= 12) {
            x = numberWithDot(res.substr(0, 11), res.substr(11));
        }
    } return x;
}