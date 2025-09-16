
import { deepEqual, formatedDate } from '../../helpers/util';
import { LABEL_CONST } from '../../constants/label.constant.en';
const primaryKey = "partNo";

const partTypeList = [
    { id: 1 , name : '1 - Local'},
    { id: 2 , name : '2 - JSP'},
    { id: 3 , name : '3 - Inhouse'},
    { id: 4 , name : '4 - MSP'}
];

const replaceWithComma = (value) => value.toString().replace(/\./g, ',');

const replaceWithDot = (value) => value.toString().replace(/,/g, '.');

const removeSpecialChar = (value) => value.toString().replace(/[^\w\s]/gi, '');

const formatValue = (value, format, seprator) => {
  let returnNo = value;
  if(value){
      const res = removeSpecialChar(value);
      returnNo = formatStr(res, format, seprator);
  }
  return returnNo;
}

const regex = /^[0-9-]+$/;
    
const removeChar = (value) => value.toString().replace(/^\d+$/, '');
const validateNumber = (value) => regex.test(value);

const formatPartNumber = (value) => {
  const val = value.replace(/\s/g, '');
  return formatValue(val, [5,5,2], "-");
};

const formatPartWeight = (value, previousValue) =>  {
  value = removeSpecialChar(value);
  if(value === "" || validateNumber(value)){
    return formatValue(value, [5,4], ".");
  }else{
    return previousValue;
  }
};

/**
 * 
 * @param {String} input : Input number or string
 * @param {Array} format : array of format [5,5,2]
 * @param {String} sep : seprator could be symbol or string
 * @returns Formatted String Like formatStr("32432432dsd2", [5,5,2], "-"); : output - 32432-432ds-d2
 */
const formatStr = (input, format, sep) => {
    let output = "";
    let idx = 0;
    for (let i = 0; i < format.length && idx < input.length; i++) {
        let len =  output.length === 0 ? 0 :  output.length -1;
        output += input.substring(idx, len + format[i]);
        if (idx + format[i] < input.length) {
            output += sep;
        }
        idx += format[i];
    }
    return output;
}

const filterPartByName = (partType) => {
    const filterPart = partTypeList.find(item => item.id == partType);  
    return filterPart?.name ? filterPart.name : '';
}

const initialValues = { partNumber: "", partName: "", partType: "" };

const intialWarningRes = {
    relaodGrid : false,
    addRecord : false,
    editRecord : false,
    deleteRecord : false
};



const validationForAdd = (addForm, validationObj) => {
    let errMessage = [];
      addForm.forEach(payload => {
        errMessage = validateAddRowWise(payload, errMessage, validationObj);
      });
    return errMessage;
}

const validateAddRowWise = (payload, errMessage, validationObj) => {
    for (const key in payload) {
      if (validationObj?.[key]) { // if key exist in validation object
        errMessage = validateAddRowIfObjectExist(errMessage, key, payload, validationObj)
      }
    }
    return errMessage;
}

const validateAddRowIfObjectExist = (errMessage, key, payload, validationObj) => {
    const [columnName, validationValue, validationRequired, validationMaxLength, valueCanNotBe] = getValidationParameter(key, payload, validationObj);
    
    if (validationRequired)
      errMessage = validationRequiredMessage(validationValue, errMessage);
    
    if(validationMaxLength)
      errMessage = validateMaxLength(key ,validationValue, validationMaxLength,  errMessage, columnName, validationObj);

    if(valueCanNotBe)
      errMessage = validateValueCanNotBe(validationValue, valueCanNotBe,  errMessage);

    return errMessage;
}

const validateValueCanNotBe = (validationValue, valueCanNotBe,  errMessage) => {
    if(validationValue === valueCanNotBe){
      const textLengthMsg =  "Value cannot be " + validationValue;
      if (errMessage.indexOf(textLengthMsg) === -1) {
        errMessage.push(textLengthMsg);
      } 
    }
    return errMessage;
}

const getValidateMaxText = (columnName, maxValue, errMessage) => {
  const textLengthMsg = columnName + " should be " + maxValue + " characters";
  if (errMessage.indexOf(textLengthMsg) === -1) {
    errMessage.push(textLengthMsg);
  }
  return errMessage;
}
const validateMaxLength = (key, validationValue, validationMaxLength, errMessage, columnName, validationObj) => {

  const charWithoutFormat = validationObj[key].maxWithoutFormat;
  const formatValue = removeSpecialChar(validationValue);
  if(charWithoutFormat && formatValue != '' &&  formatValue.length !== charWithoutFormat){
      errMessage = getValidateMaxText(columnName, charWithoutFormat, errMessage);
  } else if (validationValue.length > validationMaxLength) {  
    errMessage = getValidateMaxText(columnName, validationMaxLength, errMessage);  
  }
  return errMessage;
}

const getRequiredValidation = (validationObj, payload, key) =>{  
  let isRequired = validationObj[key].required;
  const isRequiredIf = validationObj[key].requiredIf;
  if(isRequiredIf){
    const requiredIfValue =  validationObj[key].requiredIfValue;
    isRequired = requiredIfValue === payload[isRequiredIf];
  }
  return isRequired;
}


const getValidationParameter = (key, payload, validationObj) => {
    const validationValue = payload[key];
    const columnName = validationObj[key].name;
    const validationMaxLength = validationObj[key].checkMaxValidation === true ? validationObj[key].maxLength : null;
    const valueCanNotBe = validationObj[key].valueCanNotBe;
    const validationRequired = getRequiredValidation(validationObj, payload, key);
    return [columnName, validationValue, validationRequired, validationMaxLength, valueCanNotBe];
}

const validationRequiredMessage = (validationValue, errMessage) => {
    if (validationValue === "" || !validationValue) {
      if (errMessage.indexOf(LABEL_CONST.SELECT_MANDATORY_INFO) === -1) {
        errMessage.unshift(LABEL_CONST.SELECT_MANDATORY_INFO);
      }
    }
    return errMessage;
}

const isEditDataSameAsOld = (payloadsUpdated, rows) => {
    return payloadsUpdated.reduce((acc, curr) => {
      const dataBeforeUpdate = rows.find(r => r[primaryKey] === curr[primaryKey]);
      if (dataBeforeUpdate) {
        const trueOrFalse = deepEqual(dataBeforeUpdate, curr);
        acc.push(trueOrFalse);
      } else {
        acc.push(false);
      }
      return acc;
    }, []);
  }

const validationForEdit = (payloads, rows, validationObj) => {
    const payloadsUpdated = Object.values(payloads);
    let errMessage = [];
    const isObjectSame = isEditDataSameAsOld(payloadsUpdated, rows);

    if (!payloadsUpdated.length || isObjectSame.every(f => f === true)) {
      errMessage.push(LABEL_CONST.NO_CHANGE_TO_SAVE)
      return errMessage;
    }

    for (const keyArr in payloadsUpdated) {
      const payload = payloadsUpdated[keyArr];
      errMessage = validateAddRowWise(payload, errMessage, validationObj);
    }

    return errMessage;
}

export function formatDate(d) {
  if (d) {
    let dateSplit = d.split('/');
    return dateSplit[2] + '-' + dateSplit[1] + '-' + dateSplit[0];
  } 
}

const filterPartById = (partType) => {
 
  if(partType.length > 1){
    const filterPart = partTypeList.find(item => item.name == partType);  
    return filterPart?.id ? filterPart.id : '';
  }
  return partType;
  
}
const converToUpperCase = (value) => value.toUpperCase();


const checkPostPayload = (records, userId, cmpCode) => {
  const sysDate = formatDate(formatedDate(new Date()));
  const checkedRecords = Object.values(records).map(e => { 
    const partNo = converToUpperCase(e.partNo);
    const obj = {
      partNo : removeSpecialChar(partNo),
      updateDate : sysDate,
      updateBy : "TestUser",
      batchUpdateDate : sysDate,
      cmpCode : cmpCode,
      type : filterPartById(e.partType), 
      inhouseShop : e.inhouseShop,
      weight : e.partWeight,
      partName : converToUpperCase(e.partName)
    };
    
    return obj;
  });
  return { data : checkedRecords, "userId": userId };
}

const deletePayload = (records, companyCode) => {
    const deleteRecordObjPmi = Object.values(records).map(e => {
        const newObj = {partNo : removeSpecialChar(e.partNo), cmpCd : companyCode}
        return newObj;
    });

    return {data : deleteRecordObjPmi};
}

const isDisabled = (column, data) => (column === 'inhouseShop' && data[column +'__Disable']) ? 'disabled' : '';

const isMandatory = (column, data, isRequired) => {
  let res = '';
  if (column === 'inhouseShop') {
      res = data[column + '__Disable'] ? '' : 'mandatoryControl';
  }else{
      res = isRequired ? "mandatoryControl" : "";
  }
  return res;
}

const getFormattedResponse = (response) => {
 return response.map((k, i) => {
    k.idList = i + 1;
    k.inhouseShop__Disable = k.partType !== "3";
    k.partNo = k.partNo ? formatPartNumber(k.partNo) : k.partNo;
    k.partType = k.partType ? filterPartByName(k.partType) : k.partType; 
    return k;
  });
}

export {
    formatPartNumber,
    validateNumber,
    filterPartByName,
    partTypeList,
    removeSpecialChar,
    initialValues,
    intialWarningRes,
    primaryKey,
    validationForAdd,
    validationForEdit,
    deletePayload,
    isDisabled,
    isMandatory,
    formatPartWeight,
    formatValue,
    checkPostPayload,
    replaceWithComma,
    getFormattedResponse,
    filterPartById,
    replaceWithDot
};
