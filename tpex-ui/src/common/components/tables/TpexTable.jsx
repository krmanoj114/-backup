import { useState, useMemo, useEffect } from 'react'
import "../../../styles/table.css";
import { sortRows, filterRows, paginateRows } from './helpers'
import { Pagination } from './Pagination'
import Checkbox from '../checkbox';
import "react-datepicker/dist/react-datepicker.css";
import { copyObject, createMessage, createMesssageReplacer, formatedDate, formatedDate_yyyymm, getFileExtension, getFilename } from '../../../helpers/util';
import { getFileRequest, getRequest, postRequest, putRequest } from '../../../services/axios-client';
import { CODE_MASTER_ID, MIME_TYPE, MODULE_CONST } from '../../../constants/constant';
import { TpexLoader } from '../loader/loader';
import { LABEL_CONST } from '../../../constants/label.constant.en'
import { changeInputFormat, customMessageModuleWise, getAddPayload, formatPartNo, getDeletedPayload, getEditPayload, getHttpMethod, isInCustomList, updateRowsClientSide } from './custom-data';
import AlertModal from '../alert-modal/alert-modal';
import { TableEditRow } from './TableEditRow';
import { TableAddRow } from './TableAddRow';
import { TableFilter } from './TableFilter';
import { TableCustomActions } from './TableCustomActions';
import { TableNoData } from './TableNoData';
import { TableAction } from './TableAction';
import TpexSimpleButton from '../button';

function shallowEqual(object1, object2) {
  const keys1 = Object.keys(object1);
  const keys2 = Object.keys(object2);
  if (keys1.length !== keys2.length) {
    return false;
  }
  for (let key of keys1) {
    if (object1[key] !== object2[key]) {
      return false;
    }
  }
  return true;
}

export const TpexTable = ({
  columns = [],
  rows = [],
  moduleName = "",
  selectAll = false,
  selectRow = false,
  idName = "id",
  rowPerPage = 10,
  pagination = false,
  margin = "",
  isCrud = false,
  primaryKey = null,
  filter = true,
  serverSideFilter = false,
  refreshGrid,
  userId = "LoginUserId",
  tableName = "",
  editTable = false,
  validationObj = null,
  addFormAllowed = 10,
  customActions = false,
  customEdit = false,
  defaultSortingId = "",
  defaultSortingOrder = "asc",
  otherData = {},
  copyAction = false,
  handleCustomAction,
  popupContent,
  activeRadioBtn,  
  updateParentRowsStateFromChild,
  dropDownData = {},
  codeMaster = "",
  actionOnTop = false,
  searchCriteria = null,
  addEditDataForParent,
  codeMasterName = "",
  codeMasterId = ""
}) => {

  const [activePage, setActivePage] = useState(1)
  const [filters, setFilters] = useState({})
  const orderBydata = defaultSortingId || idName;
  const [sort, setSort] = useState({ order: defaultSortingOrder, orderBy: orderBydata })
  const [isCheckAll, setIsCheckAll] = useState(false);
  const [isCheck, setIsCheck] = useState([]);
  const [formField, setFormField] = useState({});
  const [isAdd, setIsAdd] = useState(false);
  const [addData, setAddData] = useState([]);
  const [dataForEdit, setDataForEdit] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [filterToggle, setFilterToggle] = useState(false);
  const [modalShowAlert, setModalShowAlert] = useState(false);
  const [messageType, setMessageType] = useState();
  const [messageText, setMessageText] = useState();
  const [actionCase, setActionCase] = useState("");
  const [flag1, setFlag1] = useState(false);
  const [flag2, setFlag2] = useState(false);
  const [columnsUpdated, setColumnsUpdated] = useState(columns);

  const rowsPerPage = rowPerPage;
  const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters]);
  const sortedRows = useMemo(() => sortRows(filteredRows, sort), [filteredRows, sort]);
  const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage);
  const count = filteredRows.length;
  const totalPages = Math.ceil(count / rowsPerPage);
  let addKey  = 0;

  const handleSearch = (value, id) => {
    setActivePage(1)
    if (value) {
      setFilters((prevFilters) => ({
        ...prevFilters,
        [id]: value,
      }))
    } else {
      setFilters((prevFilters) => {
        const updatedFilters = { ...prevFilters }
        delete updatedFilters[id]
        return updatedFilters
      })
    }
  }

  const clearAll = () => {
    setSort({
      order: defaultSortingOrder, orderBy: defaultSortingId || idName
    })
    setActivePage(1)
    setFilters({})
  }

  const handleSort = (id) => {
    setActivePage(1)
    setSort((prevSort) => ({
      order: prevSort.order === 'asc' && prevSort.orderBy === id
        ? 'desc' : 'asc',
      orderBy: id,
    }))
  }

  const handleSelectAll = e => {
    setIsCheckAll(!isCheckAll);
    setIsCheck(rows.map(li => li[idName]));
    handleAllDataForEdit();

    if (isCheckAll) {
      setIsCheck([]);
      setDataForEdit({});
    }
  };

  const handleClick = e => {
    const { id, checked } = e.target;
    setIsCheck([...isCheck, id]);
    if (!checked) {
      setIsCheck(isCheck.filter(item => item !== id));
    }
  };
  
  const openPaymentTermAlertOnSave = (errorCode, dataRes) => {
    let params = dataRes.errorMessageParams.paymenttermcode;
    let exception = dataRes.exception;
    let vary = "";
    if(exception === "ERR_AD_2013") vary = LABEL_CONST.REGULAR;
    if(exception === "ERR_AD_2014") vary = LABEL_CONST.CPO_SPO;
    //"Default Flag <Regular or CPO/SPO>  cannot be set for more than 1 record (Check Payment Term Code : <Payment Term code ,  Payment Term code > "
    let text = LABEL_CONST.DEFAULT_FLAG + " " + vary + " " + LABEL_CONST.ERR_AD_2013 + params;
    openAlertBox(errorCode, text);
  }

  const openPaymentTermAlertOnEdit = (errorCode, dataRes) => {
    let params = '';
    let exception = dataRes.statusMessage;
    let vary = "";
    if(exception === "INFO_CM_3014") {
      vary = LABEL_CONST.REGULAR;
      params = dataRes.errorMessageParams.INFO_CM_3014.split(",");
    }
    if(exception === "INFO_CM_3015") {
      vary = LABEL_CONST.CPO_SPO;
      params = dataRes.errorMessageParams.INFO_CM_3015.split(",");
    }
    //"Default Flag <Regular or CPO/SPO> has be removed from Payment Term Code : <Payment Term code of existing record> and set to Payment Term Code : <Payment Term Code of latest record>
    let text = LABEL_CONST.DEFAULT_FLAG + " " + vary + " " + LABEL_CONST.INFO_CM_3014_1 + params[1] + LABEL_CONST.INFO_CM_3014_2 + params[0] + " .";
    openAlertBox(errorCode, text);
  }

  const openPaymentTermAlertOnBoth = (errorCode, dataRes) => {
    let params = dataRes.errorMessageParams;
    let generalVal = 'INFO_CM_3014' in params ? params.INFO_CM_3014 : '';
    let cpospoVal =  'INFO_CM_3015' in params ? params.INFO_CM_3015 : '';
    let text = '';
    if(generalVal !== '') {
      let generalText = generalVal.split(",");
      text = LABEL_CONST.DEFAULT_FLAG + " " + LABEL_CONST.REGULAR + " " + LABEL_CONST.INFO_CM_3014_1 + generalText[1] + LABEL_CONST.INFO_CM_3014_2 + generalText[0] + " .";
    }

    if(cpospoVal !== ''){
      let cpospoText = cpospoVal.split(",");
      text = text + " " + LABEL_CONST.DEFAULT_FLAG + " " + LABEL_CONST.CPO_SPO + " " + LABEL_CONST.INFO_CM_3014_1 + cpospoText[1] + LABEL_CONST.INFO_CM_3014_2 + cpospoText[0] + " .";
    }
    
    openAlertBox(errorCode, text);
  }

  const showAlertInfo = (dataRes) => {
    if(dataRes.data.statusMessage === 'INFO_CM_3016'){
      openPaymentTermAlertOnBoth(LABEL_CONST.INFORMATION,dataRes.data)
    } else if(dataRes.data.statusMessage === 'INFO_CM_3014' || dataRes.data.statusMessage === 'INFO_CM_3015'){
      openPaymentTermAlertOnEdit(LABEL_CONST.INFORMATION,dataRes.data)
    }else{
      openAlertBox(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
    }
  }

  const addRecord = async (request, e) => {
    const payload = getAddPayload(request, userId, tableName, moduleName, searchCriteria, codeMasterName, codeMasterId);
    const pathName = e.view.location.pathname.replace(/\//g, "");
    setIsLoading(true);
    postRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].ADD_API, payload).then(dataRes => {
      if (Number(dataRes.data.statusCode) === 200) {
        setAddData([]);
        setIsAdd(false);
        refreshGrid();
      
        showAlertInfo(dataRes);

      } else {
        openAlertBox(LABEL_CONST.ERROR, dataRes.data.exception);
      }
    }).catch(function (error) {
      if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
        if(error.response.data.exception === 'ERR_AD_2013' || error.response.data.exception === 'ERR_AD_2014'){
          openPaymentTermAlertOnSave(LABEL_CONST.ERROR,error.response.data)
        }else{
          const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception, false, pathName);
          openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
        }
        
      } else {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));        
      }
    }).finally(() => {
      setIsLoading(false);
    })    

  }

  function refreshGridClientSide(req) {
    const updatedRows = updateRowsClientSide(copyObject(rows), req, moduleName);
    updateParentRowsStateFromChild(updatedRows);
  }

  const showAlertPut = (dataRes) => {
    // partName check
    if (dataRes.data.partName && dataRes.data.partName.length > 0) {
      let counter3 = 1;
      const partNameMessage = [LABEL_CONST.PART_NAME_WARNING_1, dataRes.data.partName.join(", "), LABEL_CONST.PART_NAME_CONFIRMATION].map((a, i) => {
        return <p key={counter3++}>{a}</p>
      })
      openAlertBox(LABEL_CONST.WARNING, partNameMessage, 'partNameCase');
    }
    // usage check
    if (dataRes.data.partUsage && dataRes.data.partUsage.length > 0) {
      let counter4 = 1;
      const partUsageMessage = [LABEL_CONST.PART_USAGE_WARNING_1, dataRes.data.partUsage.join(", "), LABEL_CONST.PART_NAME_CONFIRMATION].map((a, i) => {
        return <p key={counter4++}>{a}</p>;
      })
      openAlertBox(LABEL_CONST.WARNING, partUsageMessage, 'partUsageCase');
    }
  }

  function editRecordPut(payload, request) {
    setIsLoading(true);
    putRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].EDIT_API, payload).then(dataRes => {
      if (Number(dataRes.data.statusCode) === 200) {
        setDataForEdit({});
        refreshGridClientSide(request);
        setIsCheck([]);
        openAlertBox(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
      } else if (!dataRes.data.statusCode || dataRes.data.statusCode === null) {
        showAlertPut(dataRes);
      } else {
        openAlertBox(LABEL_CONST.ERROR, "error");
      }
    }).catch(function (error) {      
      console.log('editRecordPut Error', error);
      if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
        const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
        openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
      } else {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
      }
    }).finally(() => {
      setIsLoading(false);
    })     
  }

  

  function editRecord(request, param1, param2) {
    const dataForEditCopy = JSON.parse(JSON.stringify(dataForEdit));
    let codeMasterDetail = { 
      codeMasterName : codeMasterName,
      codeMasterId : codeMasterId,
      moduleName: moduleName,
      userId : userId,
      primaryKey : primaryKey,
      tableName : tableName
      }
    const payload = getEditPayload(dataForEditCopy, otherData, { p1: param1, p2: param2, codeMaster }, codeMasterDetail);
    const httpMethod = getHttpMethod(moduleName);

    if (httpMethod === "put") {
      editRecordPut(payload, request);
    } else {
      setIsLoading(true);
      postRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].EDIT_API, payload).then(dataRes => {
        
        if (Number(dataRes.data.statusCode) === 200) {
          setDataForEdit({});
          refreshGrid();
          setIsCheck([]);
          showAlertInfo(dataRes);
        } else {
          openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.RECORD_EDIT_FAILED);
        }
      }).catch(function (error) {        
        console.log('editRecord edit Error', error);
        if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
          const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
          openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
        } else {
          openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
        }
      }).finally(() => {
        setIsLoading(false);
      })     

    }

  }

  const removeAddForm = (e, index) => {
    if (e.target.tagName === "TD" || e.target.className === "minus-icon") {
      const addFormCopy = copyObject(addData).filter((f, i) => i !== index);
      setAddData([...addFormCopy]);
      if (!addFormCopy.length) {
        setIsAdd(false);
      }
    }
  }

  useEffect(() => {
    if (isCheck.length === rows.length) {
      if (!isCheckAll) {
        setIsCheckAll(true);
      }
    } else if (isCheck.length !== rows.length) {
      if (isCheckAll) {
        setIsCheckAll(false);
      }
    }
    //console.log('check selected', isCheck)
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isCheck])

  useEffect(() => {
    setColumnsUpdated([...columns]);
    setIsCheckAll(false);
    setIsCheck([]);
    setIsAdd(false);
  }, [columns])

  useEffect(() => {
    clearAll();
    setActivePage(1);
    setIsCheckAll(false);
    setIsCheck([]);
    setDataForEdit({});
    setAddData([]);
  }, [rows]);

  useEffect(() => {
    addEditDataForParent(addData, dataForEdit);
    //dataForEdit
  }, [dataForEdit, addData, columnsUpdated])

  useEffect(() => {
    setColumnsUpdated([...columns]);
    setAddData([]);
    setIsAdd(false);
    setTimeout(function () {
      setIsLoading(false);
    }, 2000);
  }, [])

  useEffect(() => {
    if (activeRadioBtn === "owner_process" || activeRadioBtn === "all_process") {
      setActivePage(1);
    }
  }, [activeRadioBtn]);

  function handleDateSelected(dt, dateName, idx, format) {
    dt = format && format === "yyyy/MM" ? formatedDate_yyyymm(dt) : formatedDate(dt);
    const addDataCopy = copyObject(addData);
    addDataCopy[idx][dateName] = dt;
    setAddData(addDataCopy);
  }

  function handleEditDateSelected(dt, dateName, pKey, format) {
    const dateFormatted = format && format === "yyyy/MM" ? formatedDate_yyyymm(dt) : formatedDate(dt);
    const dataForEditChange = dataForEdit;
    dataForEditChange[pKey][dateName] = dateFormatted;
    setDataForEdit({ ...dataForEditChange });
  }

  function getPartNameByPartNo(partNo, nameIndex, inputValueAfterFormatChange) {
    const addDataCopy = copyObject(addData);
    const nameInput = nameIndex[0];
    addDataCopy[nameIndex[1]][nameIndex[0]] = inputValueAfterFormatChange;
    if (moduleName === 'PXP_PART_PRICE_MAINTENANCE' && nameInput === 'partNo' && inputValueAfterFormatChange.length === 14) {
      getRequest(MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.API_BASE_URL, MODULE_CONST.PXP_PART_PRICE_MAINTENANCE.GET_PARTNAME + partNo).then(dataRes => {
        addDataCopy[nameIndex[1]]['partName'] = dataRes.data || "";
      }).catch(function (error) {
        console.log('getPartNameByPartNo =>', error.message);
        if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
          const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
          openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
        } else {
          openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
        }
      }).finally(() => {
        setAddData(addDataCopy);
      })
    }
    if (moduleName === 'PXP_PART_PRICE_MAINTENANCE'  && nameInput === 'partNo' && inputValueAfterFormatChange.length === 0) {
      addDataCopy[nameIndex[1]]['partName'] = "";
      setAddData(addDataCopy);
    }
  }

  const inputBoxChange = (e, index) => {
    let nameIndex = e.target.name.split("__");
    let inputValueAfterFormatChange = changeInputFormat(e.target.value, moduleName);
    
    if (isAdd) {
      const addDataCopy = copyObject(addData);  
      inputValueAfterFormatChange = (moduleName === "PXP_PART_PRICE_MAINTENANCE" && nameIndex[0] === "partPrice") ?
      formatPartNo(e.target.value, addDataCopy[nameIndex[1]][nameIndex[0]]) : inputValueAfterFormatChange ;          
      addDataCopy[nameIndex[1]][nameIndex[0]] = inputValueAfterFormatChange;
      setAddData(addDataCopy);
      // populate partname based on partno   
      getPartNameByPartNo(inputValueAfterFormatChange, nameIndex, inputValueAfterFormatChange);
   
    } else {
      setFormField({
        ...formField,
        [e.target.name]: inputValueAfterFormatChange
      })
    }
  }

  const dropdownChange = (e, colname, idx) => {
    if (isAdd) {
      const addDataCopy = copyObject(addData);
      addDataCopy[idx][colname] = e.target.value;
      setAddData(addDataCopy);
    }
  } 

  function handleAllDataForEdit() {
    const dataForEditAllRow = rows.reduce((acc, curr) => {
      acc[curr[primaryKey]] = curr;
      return acc;
    }, {});
    setDataForEdit({ ...dataForEditAllRow });
  }

  function editInputBoxChange(e, pKey) {
    if (["text", "number"].includes(e.target.type)) {
      const inputToChange = e.target.name.split("__");
      const dataForEditChange = dataForEdit;
      let value = e.target.value;
      if (moduleName === "PXP_PART_PRICE_MAINTENANCE" && inputToChange[0] === "partPrice") {
        value = formatPartNo(value, dataForEditChange[pKey][inputToChange[0]]);
      }
      dataForEditChange[pKey][inputToChange[0]] = value
      setDataForEdit({ ...dataForEditChange });
    }
  }

  const handleDownload = (e) => {
    e.preventDefault();
    const hrefURL = e.target.origin === 'file://' ? e.target.pathname.slice(1) : e.target.pathname;
    const url = MODULE_CONST[moduleName].DOWNLOAD_API + '?filePath=' + hrefURL;
    const fileName = decodeURI(getFilename(hrefURL));
    const fileExt = getFileExtension(fileName);
    const link = document.createElement("a");
    getFileRequest(MODULE_CONST[moduleName].API_BASE_URL, url).then((res) => {
      link.download = res.headers.get("file_name") || fileName;   
      link.href = URL.createObjectURL(
        new Blob([res.data], { type: MIME_TYPE[fileExt] })
      );
      link.click();
    });
  };

  const handlePopUp = (sRowData) => {
    const payload = {
      "processControlId": sRowData.processControlId,
      "processId": sRowData.processId
    };
    postRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].INQUIRY_LIST_PROCESS_LOGS_API, payload).then(dataRes => {
      setIsLoading(false);
      popupContent(dataRes.data);
    }).catch(function (error) {
      setIsLoading(false);
      openAlertBox(LABEL_CONST.ERROR, error.message);

    });
  }

  const deleteRecords = (e) => { 
    const deletePayload = getDeletedPayload(isCheck, primaryKey, userId, tableName, moduleName, rows, dataForEdit);
    const pathName = e.view.location.pathname.replace(/\//g, "");
    setIsLoading(true);
    postRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].DELETE_API, deletePayload).then(delResponse => {
      if (Number(delResponse.data.statusCode) === 200) {
        setIsCheck([]);
        setDataForEdit({});
        refreshGrid();
        openAlertBox(LABEL_CONST.INFORMATION, createMesssageReplacer({ count: isCheck.length }, delResponse.data.statusMessage));

      } else {
        openAlertBox(LABEL_CONST.ERROR, delResponse.data.exception);
      }
    }).catch(function (error) {
      console.log('deleteRecords Error', error);
      setIsCheck([]);
      setDataForEdit({});
      refreshGrid();
      if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
        const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception, false, pathName);
        openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
      } else {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
      }
    }).finally(() => {
      setIsLoading(false);
    })    

  }
  const handleCopy = (e) =>{
    
      if (!isCheck.length) {
        openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3003'));
      } else if (isCheck.length > 1) {
        openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.SELECT_ONE_RECORD_COPY);
      } else {
        //copy
        const rowCopiedData = Object.values(dataForEdit)[0];
        clearAll();
        setFilterToggle(false);
        isAdd === false && setIsAdd(true);
        const keyDataValue = columnsUpdated.reduce((acc, cur) => {
          if (cur.type === "string" || cur.type === "number") {
            acc[cur.id] = rowCopiedData[cur.id];
          }
          if (cur.type === "date") {
            acc[cur.id] = rowCopiedData[cur.id];
          }
          if (cur.type === "dropdown" && cur.suggestive === true) {
            acc[cur.id] = rowCopiedData[cur.id]
          }
          return acc;
        }, {})

        const addDataCopy = copyObject(addData);
        addDataCopy.push(keyDataValue);
        setAddData(addDataCopy);
        //remove checkbox and delete the selected row data
        setIsCheck([]);
        setDataForEdit({});
      }
  }

  const handleAdd = (e) => {
    console.log("Adding row")
      if (addData.length < addFormAllowed) {
        clearAll();
        setFilterToggle(false);
        isAdd === false && setIsAdd(true);
        setIsCheck([]);
        setDataForEdit({});
        const keyWithBlankValue = columnsUpdated.reduce((acc, cur) => {
          acc[cur.id] = "";
          return acc;
        }, {})
        // custom validation for payment term master tpex-893
        if (codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER) {
          keyWithBlankValue['PAYMENT_TERM'] = "InDays"
          keyWithBlankValue['NO_OF_DAYS_DISABLE'] = false;
          keyWithBlankValue['DAY_OF_MTH_DISABLE'] = true;
          keyWithBlankValue['MTH_NO_DISABLE'] = true;
        }
        const addDataCopy = copyObject(addData);
        addDataCopy.push(keyWithBlankValue);
        setAddData(addDataCopy);
      } else {
        openAlertBox(LABEL_CONST.ERROR, createMesssageReplacer({ noOfRecord: addFormAllowed }, 'ADD_MAX_FORM_ERR', true));
      }
    
  }

  const handleDelete = (e) => {    
    if (isCheck.length > 0) {
      openAlertBox(LABEL_CONST.WARNING, createMesssageReplacer({ noOfRecord: isCheck.length }, 'RECORD_DELETED_CONFIRMATION', true), 'deleteRecordCase');
    }
    else {
      openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3004'));
    }
    
  }
  const handleActionClick = (e) => {
    if (e.target.textContent.toLowerCase() === 'copy') {
      handleCopy(e);
    }
    if (e.target.textContent.toLowerCase() === 'add') {
      handleAdd(e);
    }
    if (e.target.textContent.toLowerCase().startsWith('delete')) {
      handleDelete(e);    
    }    
    if (e.target.textContent.toLowerCase() === 'save') {
      handleSave();
    }
  }

  function handleSave() {
    if (isAdd) {
      const isValidationPassed = verifyValidationPassed(addData);
      if (isValidationPassed.length) {
        let counter = 0;
        openAlertBox(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
          return <p key={`erradd-${counter++}`}>{a}</p>;
        }
        ));

      } else {
        openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES, 'addRecordCase');
      }
    } else {
      const isValidationPassed = verifyValidationPassedEdit(dataForEdit);
      if (isValidationPassed?.length) {
        let counter2 = 0;
        if (isValidationPassed.length === 1 && isValidationPassed[0] === LABEL_CONST.INFO_CM_3008) {
          openAlertBox(LABEL_CONST.INFORMATION, isValidationPassed[0]);
        } else {
          openAlertBox(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
            return <p key={`erredit-${counter2++}`}>{a}</p>;
          }
          ));
        }
      } else {
        openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES, 'editRecordCase');
      }
    }
  }

  function validationRequiredMessage(validationValue, errMessage) {
    if (validationValue === "" || !validationValue) {
      if (errMessage.indexOf(createMessage('ERR_CM_3001')) === -1) {
        errMessage.unshift(createMessage('ERR_CM_3001'));
      }
    }
    return errMessage;
  }

  const validateFamilyMaster = (key, validationValue,validationMinValue, validationMaxValue) => {
    if(key === 'DST_CD' && codeMaster === CODE_MASTER_ID.FINAL_DEST_MASTER){
      const valCount = validationValue.split("").length; 
      const checkOne = (validationMinValue === validationMaxValue && valCount < validationMinValue);
      const checkTwo = (valCount < validationMinValue || valCount > validationMaxValue);
      if (checkOne || checkTwo){
        return true;
      }
    }
    return false;
  }

  function textValidationMessage(validationValue, validationMinValue, validationMaxValue, columnName, errMessage, key="") {
    const valCount = validationValue.split("").length; 
    const isFamilyMaster = validateFamilyMaster(key, validationValue,validationMinValue, validationMaxValue);
    if ((validationMinValue === validationMaxValue && valCount < validationMinValue) && !isFamilyMaster) {
      const textLengthMsg = createMesssageReplacer({ columnName: columnName, maxValue: validationMaxValue }, 'TEXT_LENGTH_LESS_ERROR', true);
      if (errMessage.indexOf(textLengthMsg) === -1) {
        errMessage.push(textLengthMsg);
      }
    }
    else if ((valCount < validationMinValue || valCount > validationMaxValue) && !isFamilyMaster ) {
      const textLengthMsg = columnName + ": " + createMesssageReplacer({ minValue: validationMinValue, maxValue: validationMaxValue }, 'TEXT_LENGTH_ERROR', true);
      if (errMessage.indexOf(textLengthMsg) === -1) {
        errMessage.push(textLengthMsg);
      }
    }
    errMessage = customMessageModuleWise(moduleName, errMessage, key, validationValue, 'text', isFamilyMaster);
    return errMessage;
  }

  function numberValidationMessage(validationValue, validationMinValue, validationMaxValue, columnName, errMessage, key="") {   
    errMessage = customMessageModuleWise(moduleName, errMessage, key, validationValue, 'number');
    if (validationValue < validationMinValue || validationValue > validationMaxValue) {
      const numberLengthMsg = columnName + ": " + createMesssageReplacer({ minValue: validationMinValue, maxValue: validationMaxValue }, 'NUMBER_LENGTH_ERROR', true);
      if (errMessage.indexOf(numberLengthMsg) === -1 && !isInCustomList(moduleName)) {
        errMessage.push(numberLengthMsg);
      }
    }
    return errMessage;
  }

  function getValidationParameter(key, payload) {
    const columnName = validationObj[key].name;
    const validationValue = payload[key];
    const validationType = validationObj[key].type;
    const validationRequired = validationObj[key].required;
    const validationMinValue = Number(validationObj[key].min);
    const validationMaxValue = Number(validationObj[key].max);
    return [columnName, validationValue, validationType, validationRequired, validationMinValue, validationMaxValue];
  }

  function validateAddRowIfObjectExist(errMessage, key, payload) {
    let [columnName, validationValue, validationType, validationRequired, validationMinValue, validationMaxValue] = getValidationParameter(key, payload);
    
    // custom validation for payment term master tpex-893
    if (codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER && key === "NO_OF_DAYS" && payload['PAYMENT_TERM'] === "InDays") {
      validationRequired = true;
    }
    if (codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER && payload['PAYMENT_TERM'] === "InMonth" && (key === "DAY_OF_MTH" || key === "MTH_NO")) {
      validationRequired = true;
    }


    if (validationRequired) {
      errMessage = validationRequiredMessage(validationValue, errMessage);
    }
    if (validationValue && (validationMinValue || validationMaxValue)) {
      if (validationType === "string" || validationType === "text") {
        errMessage = textValidationMessage(validationValue, validationMinValue, validationMaxValue, columnName, errMessage, key);
      }
      if (validationType === "number") {
        errMessage = numberValidationMessage(
          +validationValue,
          validationMinValue,
          validationMaxValue,
          columnName,
          errMessage,
          key
        );
      }
    }     
    
    return errMessage;
  }
  
  function validateAddRowWise(payload, errMessage) {
    for (const key in payload) {
      if (validationObj[key]) { // if key exist in validation object
        errMessage = validateAddRowIfObjectExist(errMessage, key, payload)        
      }
    }
    return errMessage;
  }

  function verifyValidationPassed(addForm) {
    let errMessage = [];
    if (isAdd) {
      addForm.forEach(payload => {
        errMessage = validateAddRowWise(payload, errMessage);
      })
    }
    return errMessage;
  }

  function isChangedDataSameAsOld(payloadsUpdated) {
    return payloadsUpdated.reduce((acc, curr) => {
      const dataBeforeUpdate = rows.find(r => r[primaryKey] === curr[primaryKey]);
      if (dataBeforeUpdate) {
        const trueOrFalse = shallowEqual(dataBeforeUpdate, curr);
        acc.push(trueOrFalse);
      } else {
        acc.push(false);
      }
      return acc;
    }, []);
  }

  function verifyValidationPassedEdit(payloads) {
    const payloadsUpdated = Object.values(payloads);
    let errMessage = [];
    const isObjectSame = isChangedDataSameAsOld(payloadsUpdated);

    if (!payloadsUpdated.length || isObjectSame.every(f => f === true)) {
      errMessage.push(LABEL_CONST.INFO_CM_3008)
      return errMessage;
    }

    for (const keyArr in payloadsUpdated) {
      const payload = payloadsUpdated[keyArr];
      errMessage = validateAddRowWise(payload, errMessage);
    }

    return errMessage;
  }

  function checkBoxAndFalseCondition(rowToUpdate) {
    const rowUpdate = copyObject(rowToUpdate);
    if (dataForEdit[rowUpdate[primaryKey]]) {
      let dataForEditRemove = copyObject(dataForEdit);
      delete dataForEditRemove[rowUpdate[primaryKey]];
      setDataForEdit(dataForEditRemove);
    }
  }

  function checkBoxAndTrueCondition(rowToUpdate) {
    const rowUpdate = copyObject(rowToUpdate);
    if (!dataForEdit[rowUpdate[primaryKey]]) {
      let dataForEditCopy = copyObject(dataForEdit);
      dataForEditCopy[rowUpdate[primaryKey]] = rowUpdate;
      if (moduleName === "PXP_PART_PRICE_MAINTENANCE") {
        dataForEditCopy[rowUpdate[primaryKey]]['partPrice'] = dataForEditCopy[rowUpdate[primaryKey]]['partPrice'].replaceAll(/\,/g, '');
      }     
      setDataForEdit(dataForEditCopy);
    }
  }

  function typeclickAndTagTD(rowToUpdate) {
    const rowUpdate = copyObject(rowToUpdate);
    const isSelectedRowAlreadyOpen = !!dataForEdit[rowUpdate[primaryKey]];
    if (isSelectedRowAlreadyOpen) {
      if (isCheck.includes(rowUpdate[primaryKey])) {
        const isCheckCopy = copyObject(isCheck);
        const index = isCheckCopy.indexOf(rowUpdate[primaryKey]);
        if (index > -1) {
          isCheckCopy.splice(index, 1);
        }
        setIsCheck(isCheckCopy);
      }

      if (dataForEdit[rowUpdate[primaryKey]]) {
        let dataForEditRemove = dataForEdit;
        delete dataForEditRemove[rowUpdate[primaryKey]];
        setDataForEdit(dataForEditRemove);
      }

    } else {
      if (!isCheck.includes(rowUpdate[primaryKey])) {
        setIsCheck([...isCheck, rowUpdate[primaryKey]]);
      }

      if (!dataForEdit[rowUpdate[primaryKey]]) {
        let dataForEditCopy = copyObject(dataForEdit);
        dataForEditCopy[rowUpdate[primaryKey]] = rowUpdate;
        setDataForEdit(dataForEditCopy);
      }
    }
  }

  function changeToEdit(e, rowToUpdate, index) {
    //empty add record 
    setAddData([]);

    if (isCrud || customEdit) {
      setIsAdd(false);
      if (e.target.checked === false && e.target.type === "checkbox") {
        checkBoxAndFalseCondition(rowToUpdate);
       
      } else if (e.target.checked === true && e.target.type === "checkbox") {
        checkBoxAndTrueCondition(rowToUpdate);
       
      } else {

        if (e.type === "click" && e.target.tagName === "TD") {
          typeclickAndTagTD(rowToUpdate);
         
        }
      }
    }
  }

  const getTdClassName = (row) => {
    const res = (moduleName === "MANUAL_INVOICE_GENERATION" && row.invoiceStatus === 'YES' || row.shippingResult === 'NO' || row.type === 'LOT') ? `nonEditable` : `editTableRow`;
    return isCheck.includes(row[primaryKey]) ? `` : res;
  }

  function toggleFilter() {
    setFilterToggle(!filterToggle);
  }

  const handleAlertConfirmation = (e) => {
    if (e.target.title === "deleteRecordCase-Warning") {
      deleteRecords(e);
    }
    if (e.target.title === "addRecordCase-Warning") {
      addRecord(addData, e);
    }
    if (e.target.title === "editRecordCase-Warning") {
      setFlag1(false);
      setFlag2(false);
      editRecord(dataForEdit, false, false);
    }
    if (e.target.title === "partNameCase-Warning") {
      setFlag1(true);
      editRecord(dataForEdit, true, flag2);
    }
    if (e.target.title === "partUsageCase-Warning") {
      setFlag2(true);
      editRecord(dataForEdit, flag1, true);
    }
  };

  function openAlertBox(messegeType, messageText = "", type = "") {
    if (type) {
      setActionCase(type);
    }
    setMessageType(messegeType);
    setMessageText(messageText);
    setModalShowAlert(true)
  };

  function onChangeDDSelection(e, pKey, inputToChange) {

   
    if (["select-one"].includes(e.target.type)) {
      const dataForEditChange = copyObject(dataForEdit);
      
      if (codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER) {
        if (inputToChange === 'PAYMENT_TERM' && e.target.value === "InMonth") {
          dataForEditChange[pKey]['NO_OF_DAYS_DISABLE'] = true;
          dataForEditChange[pKey]['DAY_OF_MTH_DISABLE'] = false;
          dataForEditChange[pKey]['MTH_NO_DISABLE'] = false;
          dataForEditChange[pKey]['NO_OF_DAYS'] = '';
        }
        if (inputToChange === 'PAYMENT_TERM' && e.target.value === "InDays") {
          dataForEditChange[pKey]['NO_OF_DAYS_DISABLE'] = false;
          dataForEditChange[pKey]['DAY_OF_MTH_DISABLE'] = true;
          dataForEditChange[pKey]['MTH_NO_DISABLE'] = true;
          dataForEditChange[pKey]['DAY_OF_MTH'] = '';
          dataForEditChange[pKey]['MTH_NO'] = '';
        }
      }

      dataForEditChange[pKey][inputToChange] = e.target.value;
      setDataForEdit({ ...dataForEditChange });
    }
  }

  function onChangeDDSelectionAddNonSuggestive(e, colname, idx) {
     if (isAdd) {
      const addDataCopy = copyObject(addData);
      addDataCopy[idx][colname] = e?.target?.value ? e.target.value : "";

       // custom validation for payment term master tpex-893
       if (codeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER && colname === 'PAYMENT_TERM') {
         if (e.target.value === "InDays") {
           addDataCopy[idx]['NO_OF_DAYS_DISABLE'] = false;
           addDataCopy[idx]['DAY_OF_MTH_DISABLE'] = true;
           addDataCopy[idx]['MTH_NO_DISABLE'] = true;
           addDataCopy[idx]['DAY_OF_MTH'] = '';
           addDataCopy[idx]['MTH_NO'] = '';
         }
         if (e.target.value === "InMonth") {
           addDataCopy[idx]['NO_OF_DAYS_DISABLE'] = true;
           addDataCopy[idx]['DAY_OF_MTH_DISABLE'] = false;
           addDataCopy[idx]['MTH_NO_DISABLE'] = false;
           addDataCopy[idx]['NO_OF_DAYS'] = '';
         }
       }

      setAddData(addDataCopy);
    }
  }

  function onChangeDDSuggestiveSelection(e, pKey, inputToChange) {
    const dataForEditChange = copyObject(dataForEdit);
    dataForEditChange[pKey][inputToChange] = e;
    setDataForEdit({ ...dataForEditChange });
  }

  function onChangeDDSuggestiveSelectionInAdd(e, colname, idx) {
    if (isAdd) {
      const addDataCopy = copyObject(addData);
      addDataCopy[idx][colname] = e?.value ? e.value : "";
      setAddData(addDataCopy);
    }
  }  

  function handleRowCheckBoxClick(e, pKey, inputToChange) {
    const dataForEditChange = copyObject(dataForEdit);
    dataForEditChange[pKey][inputToChange] = e.target.checked;
    setDataForEdit({ ...dataForEditChange });
  }

  function handleRowCheckBoxAddClick(e, colname, idx) {
    if (isAdd) {
      const addDataCopy = copyObject(addData);
      addDataCopy[idx][colname] = e.target.checked;
      setAddData(addDataCopy);
    }

  }

  return (
    <>
      {/* spinner */}
      <TpexLoader isLoading={isLoading} />

      {actionOnTop ?
        <div className="form-group col-12 text-end pb-0">
          <TpexSimpleButton color="outline-primary" text="Add" bottommargin="2" handleClick={event => handleActionClick(event)} />
          {copyAction ? <TpexSimpleButton color="outline-primary" leftmargin="2" bottommargin="2" text="Copy" handleClick={event => handleActionClick(event)} /> : ""}
          <TpexSimpleButton color="outline-primary" leftmargin="2" rightmargin="3" bottommargin="2" text={`Delete (${isCheck.length})`} handleClick={event => handleActionClick(event)} />
          {filter && columnsUpdated.length ? <button className="button-filter" onClick={toggleFilter}><i className="filter-button"></i><span>Filters</span></button> : ""}
        </div>
        :
        <TableFilter toggleFilter={toggleFilter} filter={filter} columns={columnsUpdated} />
      }
       
      <div className="grid-table">
        <div className="row g-0">
          <div>
            <div className="col">
              <div className={`table-responsive ${!rows.length && !isLoading ? "" : "table-responsive-height"}`}>
                <table className={`table tpexTable tableWithChecbox ${editTable ? "tpexTableEdit" : ""}`}>
                  <thead className="text-nowrap">
                    {columnsUpdated.length ?
                      <tr key="grid-sort-asc-dsc">
                        {selectAll === true && selectRow === true &&
                          <th className="checkboxCol">
                            <Checkbox
                              type="checkbox"
                              name="selectAll"
                              id="selectAll"
                              handleClick={handleSelectAll}
                              isChecked={isCheckAll}
                            />
                          </th>
                        }
                        {selectAll === false && selectRow === true &&
                          <th className="checkboxCol"></th>
                        }
                        {columnsUpdated.map((column) => {

                          const sortIcon = () => {
                            if (column.id === sort.orderBy) {
                              if (sort.order === 'asc') {
                                return <i className="arrow-up"></i>
                              }
                              return <i className="arrow-down"></i>
                            } else {
                              return <i className="arrow-down"></i>
                            }
                          }
                          return (

                            <th key={column.id}>
                              <span>{column.name}</span>
                              <span onClick={() => handleSort(column.id)}>{sortIcon()}</span>
                            </th>
                          )
                        })}
                      </tr>
                      : ''
                    }
                    {filterToggle &&
                      <tr>
                        {selectRow && <th className="checkboxCol"></th>}
                        {filterToggle && serverSideFilter === false && columnsUpdated.map(column => {
                          return (
                            <th key={`${column.id}-filter`} className="pt-0">
                              <input
                                key={`${column.id}-search`}
                                type="text"
                                className="form-control"
                                placeholder={`filter by ${column.name}`}
                                value={filters[column.id] || ''}
                                onChange={event => handleSearch(event.target.value, column.id)}
                              />
                            </th>
                          )
                        })}
                      </tr>
                    }
                  </thead>
                  <tbody>

                    {/* add */}
                    {isAdd && addData.map((add, addindex) => {
                      return (<tr key={`tr-${addKey++}`} onClick={event => removeAddForm(event, addindex)}>
                        <td className="checkboxCol">
                          <div className="minus-box"><i className="minus-icon" onClick={event => removeAddForm(event, addindex)}></i></div>
                        </td>
                        <TableAddRow
                          columns={columnsUpdated}
                          addData={addData}
                          addindex={addindex}
                          codeMaster={codeMaster}
                          dropDownData={dropDownData}
                          inputBoxChange={inputBoxChange}
                          handleDateSelected={handleDateSelected}
                          dropdownChange={dropdownChange}
                          handleRowCheckBoxClick={handleRowCheckBoxAddClick}
                          onChangeDDSelectionAddNonSuggestive={onChangeDDSelectionAddNonSuggestive}
                          onChangeDDSuggestiveSelectionInAdd={onChangeDDSuggestiveSelectionInAdd}
                        />
                      </tr>
                      )
                    })
                    }
                    {/* add end */}

                    {/* {rows start} */}
                    {calculatedRows.map((row, index) => {

                      return (
                        <tr key={row[idName]} onClick={e => changeToEdit(e, row, index)} className={getTdClassName(row)}>
                          {/* checkbox*/}
                          {selectRow === true &&
                            <td className="checkboxCol">
                              <Checkbox
                                key={row[idName]}
                                type="checkbox"
                                name={row[idName]}
                                id={row[idName]}
                                handleClick={handleClick}
                                isChecked={isCheck.includes(row[idName])}
                              />
                            </td>
                          }
                          {/* edit row */}
                          
                          <TableEditRow
                            editInputBoxChange={editInputBoxChange}
                            handleEditDateSelected={handleEditDateSelected}
                            handleDownload={handleDownload}
                            handlePopUp={handlePopUp}
                            columns={columnsUpdated}
                            isCheck={isCheck}
                            primaryKey={primaryKey}
                            dataForEdit={dataForEdit}
                            index={index}
                            row={row}
                            codeMaster={codeMaster}
                            dropDownData={dropDownData}
                            onChangeDDSelection={onChangeDDSelection}
                            onChangeDDSuggestiveSelection={onChangeDDSuggestiveSelection}
                            handleRowCheckBoxClick={handleRowCheckBoxClick}
                          />

                        </tr>
                      )
                    })}

                    {/* no data in table  */}
                    <TableNoData
                      rows={rows}
                      isLoading={isLoading}
                      columns={columnsUpdated}
                      selectRow={selectRow}
                    />

                  </tbody>
                </table>
              </div>
              {/* pagination  */}
              <Pagination
                activePage={activePage}
                count={count}
                rowsPerPage={rowsPerPage}
                totalPages={totalPages}
                pagination={pagination}
                setActivePage={setActivePage}
              />
            </div>
          </div>
        </div>

        {/* custom action */}
        <TableCustomActions
          customActions={customActions}
          isCheck={isCheck}
          handleActionClick={handleActionClick}
          calculatedRows={calculatedRows}
          handleCustomAction={handleCustomAction}
        />

      </div>

      {/* table action  */}
      <TableAction
        isCrud={isCrud}
        columns={columnsUpdated}
        isCheck={isCheck}
        actionOnTop = {actionOnTop}
        copyAction={copyAction}
        handleActionClick={handleActionClick}
      />


      {/* alert modal  */}
      <AlertModal
        handleClick={handleAlertConfirmation}
        show={modalShowAlert}
        onHide={() => setModalShowAlert(false)}
        status={messageType}
        content={messageText}
        parentBtnName={actionCase}
      />
    </>
  )
}