import { useState, useMemo, useEffect } from 'react'
import "../../styles/table.css";
import { sortRowsWithDateString, filterRows, paginateRows } from '../../common/components/tables/helpers'
import Checkbox from '../../common/components/checkbox';
import TpexSimpleButton from '../../common/components/button';
import {
  copyObject,
  createMessage,
  createMesssageReplacer,
  deepEqual
} from '../../helpers/util';
import { deleteRequest, getRequest, postRequest } from '../../services/axios-client';
import { MODULE_CONST } from '../../constants/constant';
import { TpexLoader } from '../../common/components/loader/loader';
import { LABEL_CONST } from '../../constants/label.constant.en'
import { TpexSelect } from '../../common/components/select';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { Pagination } from '../../common/components/tables/Pagination';
import { EnginePartTableEditRow } from './EnginePartTableEditRow';
import { EnginePartFilter } from './engine-part-filter';
import { TableNoData } from '../../common/components/tables/TableNoData';
import { ShippingTableHeader } from './shipping-control-tableHeader';

export const EnginePartTable = ({
  columns = [],
  rows = [],
  moduleName = "",
  selectAll = false,
  selectRow = false,
  idName = "id",
  rowPerPage = 10,
  pagination = false,
  isCrud = false,
  primaryKey = null,
  filter = true,
  serverSideFilter = false,
  refreshGrid,
  validationObj = null,
  addFormAllowed = 10,
  customEdit = false,
  defaultSortingId = "",
  defaultSortingOrder = "asc",
  setModifyFlag,
  isSearchBtnClick,
}) => {

  const [activePage, setActivePage] = useState(1)
  const [filters, setFilters] = useState({})
  const orderBydata = defaultSortingId || idName;
  const [sort, setSort] = useState({ order: defaultSortingOrder, orderBy: orderBydata })
  const [isCheckAll, setIsCheckAll] = useState(false);
  const [isCheck, setIsCheck] = useState([]);
  const [isAdd, setIsAdd] = useState(false);
  const [addData, setAddData] = useState([]);
  const [dataForEdit, setDataForEdit] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [filterToggle, setFilterToggle] = useState(false);

  const [isCopy, setIsCopy] = useState(false);
  const [copyData, setCopyData] = useState({});

  const rowsPerPage = rowPerPage;

  const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters])
  const sortedRows = useMemo(() => sortRowsWithDateString(filteredRows, sort), [filteredRows, sort])
  const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage)
  const count = filteredRows.length;
  const totalPages = Math.ceil(count / rowsPerPage);

  const [modalShowAlert, setModalShowAlert] = useState(false);
  const [messageType, setMessageType] = useState();
  const [messageText, setMessageText] = useState();
  const [actionCase, setActionCase] = useState("");
  const [saveCaseData, setSaveCaseData] = useState([]);

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
    setSort({ order: defaultSortingOrder, orderBy: defaultSortingId || idName })
    setActivePage(1)
    setFilters({})
  }

  const handleSort = (id, date) => {
    setActivePage(1)
    setSort((prevSort) => ({
      order: prevSort.order === 'asc' && prevSort.orderBy === id ? 'desc' : 'asc',
      orderBy: id,
      orderDate: date
    }));
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
      setModifyFlag(false);
    }
    const selectRow = rows.find(item => (item.idCount === id || item.idList === id));
    setCopyData(selectRow);
  };

  function savePayloadAsPerModule(i) {
    let data = {};
    data.crFmlyCode = i.carFamilyCode;
    data.importerCode = i.importerCode;
    data.exporterCode = i.exporterCode;
    data.partNo = i.partNo;
    data.lotModuleCode = i.lotModuleCode;
    data.companyCode = "TMT";
    return data;
  }

  function rowNewOldFlag(k) {
    if (isAdd || isCopy) {
      k["isNewRow"] = "Y";
    } else {
      k["isNewRow"] = "N";
    }
    return k;
  }
  
  function editRecord(request) {
    const editDataSelected = Object.values(request).map(e => {
      return e;
    });
    let selectedPayload = [];
    let apiPath='';

    for (let i of editDataSelected) {
        let selectedData = savePayloadAsPerModule(rowNewOldFlag(i));
        selectedPayload.push(selectedData);
    }
     
    apiPath = MODULE_CONST[moduleName].SAVE_API+`?userId=TestUser`;    
    saveAPICall(apiPath, selectedPayload);
  }

  function saveAPICall(api, data){
    setIsLoading(true);
    postRequest(MODULE_CONST[moduleName].API_BASE_URL, api, data).then(dataRes => {
      setIsLoading(false);
      if (dataRes.data.statusCode.toString() === "200") {
        setDataForEdit({});
        refreshGrid();
        setIsCheck([]);
        setSaveCaseData([]);
        openAlertBox(LABEL_CONST.INFORMATION, createMessage(dataRes.data.statusMessage));
      } else {
        const messageAfterReplace = createMesssageReplacer(dataRes.data.errorMessageParams, dataRes.data.exception);
        openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
      }
    }).catch(function (error) {
      setIsLoading(false);
      catchErrHandle(error);
    });
  }

  function saveCaseRecord(payload) {
    let data = payload.map((e) => { e.confirmFlag="true"; return e;});
    let api = MODULE_CONST[moduleName].SAVE_API+`?userId=TestUser`;
    saveAPICall(api, data);
  }

  const removeAddForm = (e, index) => {
    const addFormCopy = copyObject(addData).filter((f, i) => i !== index);
    setAddData([...addFormCopy]);
    if (!addFormCopy.length) {
      setIsAdd(false);
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
    ((isCheck.length > 0) || (addData.length > 0)) ? setModifyFlag(true) : setModifyFlag(false);
  }, [isCheck, isCheckAll, rows, moduleName, addData, setModifyFlag])

  useEffect(() => {
  }, [columns])

  useEffect(() => {
    setActivePage(1);
    setIsCheckAll(false);
    clearAll();
    if(isSearchBtnClick) {
      setIsAdd(false);
      setIsCheck([]);
      setAddData([]);
      setDataForEdit({});
    }
  }, [rows, isSearchBtnClick]);

  useEffect(() => {
    //dataForEdit
  }, [dataForEdit, addData]);

  useEffect(() => {
    setAddData([]);
    setIsAdd(false);
    setCopyData({});
    setIsCopy(false);
  }, [])

  const inputBoxChange = (e) => {
      const addDataCopy = copyObject(addData);
      const nameIndex = e.target.name.split("__");
      addDataCopy[nameIndex[1]][nameIndex[0]] = e.target.value;
      setAddData(addDataCopy);
  }
  
  const handleAddOptions = (e, col) => {
      const addDataCopy = copyObject(addData);
      const selectToChange = col.split("__");
      const nameKey = selectToChange[0].split('Label');
      addDataCopy[selectToChange[1]][selectToChange[0]+'Obj'] = e;
      addDataCopy[selectToChange[1]][selectToChange[0]] = e !==null ? getLabelValues(e, 'value', 'label'):'';
      addDataCopy[selectToChange[1]][nameKey[0]] = e !==null ? getLabelValues(e, 'label', 'value'):'';
      if(moduleName==='INVOICE_SETUP_MASTER'){
        addDataCopy[selectToChange[1]][selectToChange[0]] = e !==null ? getLabelValues(e, 'label', 'value'):'';
      }
      setAddData(addDataCopy);
  }

  function handleAddPLS(e) {
    const addDataCopy = copyObject(addData);
    const nameIndex = e.target.name.split("__");
    addDataCopy[nameIndex[1]][nameIndex[0]] = e.target.checked === true ? 'Y':'N';
    setAddData(addDataCopy);
  };

  function handleAllDataForEdit() {
    const dataForEditAllRow = rows.reduce((acc, curr) => {
      acc[curr[primaryKey]] = curr;
      return acc;
    }, {})
    setDataForEdit({ ...dataForEditAllRow });
  }

  function editInputBoxChange(e, pKey) {
    if (["text", "number"].includes(e.target.type)) {
      const inputToChange = e.target.name.split("__");
      const dataForEditChange = dataForEdit;
      dataForEditChange[pKey][inputToChange[0]] = e.target.value;
      setDataForEdit({ ...dataForEditChange });
    }
  }

  function selectBoxAddChange(e) {
    const addDataCopy = copyObject(addData);
    const nameIndex = e.target.name.split("__");
    addDataCopy[nameIndex[1]][nameIndex[0]] = e.target.value;
    setAddData(addDataCopy);

if([nameIndex[0]].toString() === 'packingMonth'){
  if(e.target.value === 'Y'){
    addDataCopy[nameIndex[1]]['priceMethod'] = 'ETD';
    document.getElementsByName("priceMethod__"+nameIndex[1])[0].disabled = true;
  } else {
    addDataCopy[nameIndex[1]]['priceMethod'] = '';
    document.getElementsByName("priceMethod__"+nameIndex[1])[0].disabled = false;
  }
  
}
    if([nameIndex[0]].toString() === 'buyer'){
      setIsLoading(true);
      getRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].BUYER_SELECTED_LIST_API + `?buyer=${e.target.value}`).then(data =>{
          setIsLoading(false);
          addDataCopy[nameIndex[1]]['consigneeList'] = data.data.consigneeList;
          addDataCopy[nameIndex[1]]['consigneeLabelObj'] = null;
          addDataCopy[nameIndex[1]]['consigneeLabel'] = '';
          addDataCopy[nameIndex[1]]['consignee'] = '';

          addDataCopy[nameIndex[1]]['notifyPartyList'] = data.data.notifyPartyList;
          addDataCopy[nameIndex[1]]['notifyPartyLabelObj'] = null;
          addDataCopy[nameIndex[1]]['notifyPartyLabel'] = '';
          addDataCopy[nameIndex[1]]['notifyParty'] = '';

            setAddData(addDataCopy);
      }).catch(function (error){
          setIsLoading(false);
          openAlertBox(LABEL_CONST.ERROR, error.message);
      });
    }
  }

  function selectBoxChange(e, pKey) {
      const selectToChange = e.target.name.split("__");     
      const dataForEditChange = dataForEdit;
      dataForEditChange[pKey][selectToChange[0]] = e.target.value; 
      setDataForEdit({ ...dataForEditChange });
      if([selectToChange[0]].toString() === 'buyer'){
        setIsLoading(true);
        getRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].BUYER_SELECTED_LIST_API + `?buyer=${e.target.value}`).then(data =>{
            setIsLoading(false);
              dataForEditChange[pKey]['consigneeList'] = data.data.consigneeList;
              dataForEditChange[pKey]['consigneeLabelObj'] = null;
              dataForEditChange[pKey]['consigneeLabel'] = '';
              dataForEditChange[pKey]['consignee'] = '';

              dataForEditChange[pKey]['notifyPartyList'] = data.data.notifyPartyList;
              dataForEditChange[pKey]['notifyPartyLabelObj'] = null;
              dataForEditChange[pKey]['notifyPartyLabel'] = '';
              dataForEditChange[pKey]['notifyParty'] = '';
              
              setDataForEdit({ ...dataForEditChange });
              setCopyData(dataForEditChange[pKey]);
        }).catch(function (error){
            setIsLoading(false);
            openAlertBox(LABEL_CONST.ERROR, error.message);
        });
      }
  }

  function selectBoxList(id, multiSelectList, updatedlist){    
    let listName = id.split("Label");
    if(id==='consigneeLabel' || id==='notifyPartyLabel'){
        return updatedlist[listName[0]+'List'];
    } else {
      return multiSelectList;
    }
  }

  function getLabelValues(d, v, z){
    if(moduleName === 'MIX_PRIVILEGE_MASTER' || moduleName==='INVOICE_SETUP_MASTER'){
      return d.map(k => k[v]);
    }
    else {
      return d[z];
    }    
  }

  function handleSelectedOptions(e, pKey, col, itm){
    if(moduleName === 'MIX_PRIVILEGE_MASTER' && (arguments[3] !== undefined || itm !== undefined)){
      setModifyFlag(!deepEqual(e, itm));
    }
      const selectToChange = col.split("__");
      const nameKey = selectToChange[0].split('Label');
      const dataForEditChange = dataForEdit;
      dataForEditChange[pKey][selectToChange[0]+'Obj'] = e;
      dataForEditChange[pKey][selectToChange[0]] = e !== null ? getLabelValues(e, 'value', 'label'):'';
      dataForEditChange[pKey][nameKey[0]] = e !== null ? getLabelValues(e, 'label', 'value'):'';
      setDataForEdit({ ...dataForEditChange });
  }
  
  function handlePLSClick(e, pKey) {
    const selectToChange = e.target.name.split("__");
    const dataForEditChange = dataForEdit;
    dataForEditChange[pKey][selectToChange[0]] = e.target.checked === true ? 'Y':'N';
    setDataForEdit({ ...dataForEditChange });
  }
  
  function soldToMessrsColDefault(id, acc, list) {
    if (id === 'soldToMessrsLabel') {
      const messerVal = list.find(x => { return x.value === 'TMX' });
      acc[id] = messerVal.label;
      acc['soldToMessrs'] = messerVal.value;
      acc[id + 'Obj'] = messerVal;
      return acc;
    } else if (id === 'plsFlag') {
      acc[id] = 'Y';
    } else if (id === 'packingMonth') {
      acc[id] = 'N';
    }
    else {
      acc[id] = "";
      acc[id + 'Obj'] = "";
    }
  }

  function addRecordData(actionType){
    if (addData.length < addFormAllowed) {
      clearAll();
      setFilterToggle(false);        
      setIsCheck([]);
      setDataForEdit({});
      const keyWithBlankValue = columns.reduce((acc, cur) => {
        soldToMessrsColDefault(cur.id, acc, cur.multiSelectList);
        return acc;
      }, {});
      const addDataCopy = copyObject(addData);
      addDataCopy.push(keyWithBlankValue);
      if(actionType === 'add'){
        if(isCopy === true && addData.length > 0){
          addData.splice(0, addData.length);
          let blankArr = [];
          blankArr.push(keyWithBlankValue);
          setAddData(blankArr);
        } else {
          setAddData(addDataCopy);
        }

        (isAdd === false) && setIsAdd(true);
        (isCopy === true) && setIsCopy(false);
        
      }
      else if(actionType==='copy'){
        isCopy === false && setIsCopy(true);
        isAdd === true && setIsAdd(false);
        const lastObj = addDataCopy[addDataCopy.length-1];
        
        lastObj.carFamilyCode = copyData.carFamilyCode.id;
        lastObj.exporterCode = copyData.exporterCode.id;
        lastObj.importerCode = copyData.importerCode.id;
        lastObj.lotModuleCode = copyData.lotModuleCode.id;
        lastObj.partNo = copyData.partNo.id;


        addDataCopy[addDataCopy.length-1] = lastObj;
        setAddData(addDataCopy);
        setCopyData({});
      }
    } else {
      openAlertBox(LABEL_CONST.ERROR, createMesssageReplacer({ noOfRecord: addFormAllowed }, 'ADD_MAX_FORM_ERR', true));
    }
  }

  function payloadAsPerModule(i){
    let data = {};
    data.crFmlyCode = i.carFamilyCode.id;
    data.importerCode = i.importerCode.id;
    data.exporterCode = i.exporterCode.id;
    data.partNo = i.partNo.id;
    data.lotModuleCode = i.lotModuleCode.id;
    data.companyCode = "TMT";
    return data;
  }

  const deleteRecords = () => {
    const selectedDeletePayload = [];
      for (let i of rows) {
        for (let prop of isCheck) {
              if (i.idList === prop) {
                let selectedData = payloadAsPerModule(i);
                selectedDeletePayload.push(selectedData);
          }
        }
      }
    const deletePayload = {
      data: selectedDeletePayload.map(x=> {return x;})
    };

    setIsLoading(true);
    deleteRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].DELETE_API, deletePayload).then(delResponse => {
      setIsLoading(false);
      if (Number(delResponse.data.statusCode) === 200) {
        setIsCheck([]);
        setDataForEdit({});
        refreshGrid();
        openAlertBox(LABEL_CONST.INFORMATION, createMesssageReplacer({ count: delResponse.data.errorMessageParams.count }, delResponse.data.statusMessage, false));
      } else {
        openAlertBox(LABEL_CONST.ERROR, delResponse.data.exception);
      }
    }).catch(function (error) {
      setIsLoading(false);
      if(error.response.data.statusCode === 417) {
        error.response.data.exception = error.response.data.statusMessage;
        openAlertBox(LABEL_CONST.ERROR, createMesssageReplacer({ errorCode: error.response.data.errorMessageParams.errorCode }, error.response.data.statusMessage, false));
      } else{
        catchErrorHandle(error);
      }
    });

  };

  const handleActionClick = (e) => {
    if (e.target.textContent.toLowerCase() === 'add') {
      addRecordData('add');
    }
    if (e.target.textContent.toLowerCase().startsWith('delete')) {
      if (isCheck.length > 0) {
        openAlertBox(LABEL_CONST.WARNING, createMesssageReplacer({ noOfRecord: isCheck.length }, 'RECORD_DELETED_CONFIRMATION', true), 'deleteRecordCase');
      }
      else {
        openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.ERR_CM_3004);
      }
    }
    if (e.target.textContent.toLowerCase() === 'save') {
      handleSave();
    }
    if (e.target.textContent.toLowerCase() === 'copy') {
      handleCopy();
    }
  };

  function handleCopy(){
    if (isCheck.length === 0) {
      openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.ERR_CM_3003);
    } else if (isCheck.length > 1) {
      openAlertBox(LABEL_CONST.ERROR, LABEL_CONST.SELECT_ONE_RECORD_COPY);
    } else {
      addRecordData('copy');
    }
  }
  function handleSave() {
    if (isAdd || isCopy) {
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
      const isValidationPassed = validationForEdit(dataForEdit);
      if (isValidationPassed?.length) {
        if(isValidationPassed.join("\n") !== LABEL_CONST.NO_CHANGE_TO_SAVE){
          let counter2 = 0;
          openAlertBox(LABEL_CONST.ERROR, isValidationPassed.map((a) => {
            return <p key={`erredit-${counter2++}`}>{a}</p>;
          }
          ));
        } else {
          openAlertBox(LABEL_CONST.INFORMATION, isValidationPassed.join("\n"));
        }
      } else {
        openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES, 'editRecordCase');
      }
    }
  }

  function validationRequiredMessage(validationValue, errMessage) {
    if (validationValue === "" || !validationValue) {
      if (errMessage.indexOf(LABEL_CONST.SELECT_MANDATORY_INFO) === -1) {
        errMessage.unshift(LABEL_CONST.SELECT_MANDATORY_INFO);
      }
    }
    return errMessage;
  }

  function getValidationParameter(key, payload) {
    const validationValue = payload[key];
    const validationRequired = validationObj[key].required;
    return [validationValue, validationRequired];
  }

  function validateAddRowIfObjectExist(errMessage, key, payload) {
    const [validationValue, validationRequired] = getValidationParameter(key, payload);
    if (validationRequired) {
      errMessage = validationRequiredMessage(validationValue, errMessage);
    }    
    return errMessage;
  }

  function validateAddRowWise(payload, errMessage) {
    for (const key in payload) {
      if (validationObj?.validationObj[key]) {//if key exist in validation object
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

  function isEditDataSameAsOld(payloadsUpdated) {
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
  function validationForEdit(payloads) {
    const payloadsUpdated = Object.values(payloads);
    let errMessage = [];
    const isObjectSame = isEditDataSameAsOld(payloadsUpdated);

    if (!payloadsUpdated.length || isObjectSame.every(f => f === true)) {
      errMessage.push(LABEL_CONST.NO_CHANGE_TO_SAVE)
      return errMessage;
    }

    for (const keyArr in payloadsUpdated) {
      const payload = payloadsUpdated[keyArr];
      errMessage = validateAddRowWise(payload, errMessage);
    }

    return errMessage;
  }
  function handleColumnHover(e) {
    //hover
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
      setDataForEdit(dataForEditCopy);
    }
  }

  function getRowA (rowUpdate) {
    return rows.find(item => (
      item.idCount === rowUpdate[primaryKey] ||
      item.idList === rowUpdate[primaryKey]
    ))
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
      moduleName === 'MIX_PRIVILEGE_MASTER' && setModifyFlag(false);
    } else {
      if (!isCheck.includes(rowUpdate[primaryKey])) {
        setIsCheck([
          ...isCheck,
          rowUpdate[primaryKey]
        ]);
      }

      setCopyData(getRowA(rowUpdate));
      
      if (!dataForEdit[rowUpdate[primaryKey]]) {
        let dataForEditCopy = copyObject(dataForEdit);
        dataForEditCopy[rowUpdate[primaryKey]] = rowUpdate;
        setDataForEdit(dataForEditCopy);
      }
    }
  }

  function changeToEdit(e, rowToUpdate, index) {
    if ((isCrud || customEdit) && moduleName !== 'INVOICE_SETUP_MASTER') {
      setIsAdd(false);
      setAddData([]);
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

  function toggleFilter() {
    setFilterToggle(!filterToggle);
  }

  const handleAlertConfirmation = (e) => {
    setModalShowAlert(false);
    if (e.target.title === "deleteRecordCase-Warning") {
      deleteRecords();
    }
    if (e.target.title === "addRecordCase-Warning") {
      editRecord(addData);
    }
    if (e.target.title === "editRecordCase-Warning") {
      editRecord(dataForEdit);
    }    
    if (e.target.title === "saveRecordCase-Warning") {
      saveCaseRecord(saveCaseData);
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

  function catchErrorHandle(error){
    let errKey = error.response.data.errorMessageParamsArray;
    if (errKey && Object.keys(errKey).length > 0) {
      if(Object.keys(errKey.vanDateFrom).length > 0){
        let errMessage = [];
        for (let prop of errKey.vanDateFrom) {
          const errText = createMesssageReplacer({vanDateFrom:prop}, error.response.data.statusMessage);
          errMessage.push(errText);
        }
        openAlertBox(LABEL_CONST.ERROR, errMessage.join("\n"));
      }
    } else {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.statusMessage));
    }      
}
function catchErrArr(errKey){
  if(Object.keys(errKey.errorMessageParamsArray.Priority_N).length > 0){
    let errData = {};
    if(errKey.exception==='ERR_IN_1040'){
      errData = {N:errKey.errorMessageParamsArray.Priority_N[0], N1:errKey.errorMessageParamsArray.Priority_N[1]}
    } else if(errKey.exception==='ERR_IN_1039'){
      errData = {N:errKey.errorMessageParamsArray.Priority_N}
    } else if(errKey.exception==='WARN_IN_1041'){
      errData = {N:errKey.errorMessageParamsArray.Priority_N, 1:errKey.errorMessageParamsArray.Privilege_Names}
      openAlertBox(LABEL_CONST.WARNING, createMesssageReplacer(errData, errKey.exception), 'saveRecordCase');
      return false;
    } else if(errKey.exception==='WARN_IN_1040'){
      errData = {N:errKey.errorMessageParamsArray.Priority_N}
      openAlertBox(LABEL_CONST.WARNING, createMesssageReplacer(errData, errKey.exception), 'saveRecordCase');
      return false;
    }
    const messageAfterReplace = createMesssageReplacer(errData, errKey.exception);
    openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
  }
}
function catchErrHandle(error){
  let errKey = error.response.data;
  if (errKey.errorMessageParams && Object.keys(errKey.errorMessageParams).length > 0) {
    if(errKey.statusCode == 500 && moduleName!=="INVOICE_SETUP_MASTER" && moduleName!=="RETURNABLE_PACKING_MASTER"){
      errKey.exception = errKey.statusMessage
    }
      const messageAfterReplace = createMesssageReplacer(errKey.errorMessageParams, errKey.exception);
      openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
    }
  else if (errKey.errorMessageParamsArray && Object.keys(errKey.errorMessageParamsArray).length > 0) {
    catchErrArr(errKey);
  }  else {
     if(errKey.exception==='INFO_CM_3008'){
        openAlertBox(LABEL_CONST.INFORMATION, createMessage(errKey.exception));
      } else{
        openAlertBox(LABEL_CONST.ERROR, createMessage(errKey.exception));
      }
    }
}
function classNameRetun(colId){
  return (colId ==='consigneeLabel' || colId ==='notifyPartyLabel' 
          || colId==='priorityTwo' || colId === 'priorityTwo' 
          || colId === 'priorityThree' || colId === 'priorityFour' 
          || colId === 'priorityFive' || colId === 'exporterCode'
          || colId === 'reExpCode' || colId === 'lineCode') ? '': 'mandatoryControl';
}
function chkBoxFlag(colId){
  return colId === "Y";
}

function moduleClassRetun(){
  let classname = '';
  if(moduleName === 'SHIPPING_CONTROL_MASTER_LIST'){
    classname = 'tableinvoice tableShippingControl';
  } else if(moduleName === 'MIX_PRIVILEGE_MASTER'){
    classname = 'mixPriviligeMaster';
  } else {
    classname = moduleName;
  }
  return classname;
}

function editControlClass(col, rowEdit, module){
  let classname='';
if(module === 'INVOICE_SETUP_MASTER'){
  classname = 'editTableRow disableClick'
} else {
  const caseTwo = ((rowEdit && (module === 'MIX_PRIVILEGE_MASTER'))) ? `editTableRow disableClick`:`editTableRow`;
  classname = col ? `` : caseTwo;
}
return classname;
}
  return (
    <>
    {/* spinner */}
    <TpexLoader isLoading={isLoading} /> 
     {/* filter option  */}
     <EnginePartFilter 
        toggleFilter={toggleFilter} 
        isCrud={isCrud} 
        filter={filter} 
        rows={rows} 
        columns={columns} 
        handleActionClick={handleActionClick} 
        isCheck={isCheck}
        moduleName={moduleName}
        isSearchBtnClick={isSearchBtnClick}
      />
      <div className="grid-table">
        <div className="row g-0">
          <div className={moduleClassRetun()}>
            <div className="col">
              <div className="table-responsive">
                <table className={`table tpexTable tableWithChecbox`}>
                  <ShippingTableHeader
                    selectAll={selectAll}
                    selectRow={selectRow}
                    handleSelectAll={handleSelectAll}
                    columns={columns}
                    isCheckAll={isCheckAll}
                    sort={sort}
                    handleSort={handleSort}
                    filterToggle={filterToggle}
                    serverSideFilter={serverSideFilter}
                    filters={filters}
                    handleSearch={handleSearch}
                  />
                  <tbody>
                    {/* add */}
                    {(isAdd || isCopy) && addData.map((add, i) => {
                      const addindex = i;
                      
                      return (<tr key={`addData-${addindex}`}>
                        <td className="checkboxCol">
                          <div className="minus-box"><i className="minus-icon" onClick={event => removeAddForm(event, addindex)}></i></div>
                        </td>
                        {columns.map((column) => {
                          if (column.type === 'select') {
                            return <td key={`td-${column.id}${addindex}`} className="mandatoryControl">
                              <TpexSelect
                                moduleName={`${column.id}__${addindex}`}
                                options={column.selectList} 
                                margin="minWdt" 
                                blankRequired={column.isBlank}
                                //selected={addData[addindex][column.id]} 
                                onChangeSelection={(event) => selectBoxAddChange(event)}
                                hasValue={addData[addindex][column.id]} 
                              />
                            </td>
                        }
                        else if (column.type === 'multiselect') {
                          return <td key={`td-${column.id}${addindex}`} className={classNameRetun(column.id)}>
                            <div className="custom-multiSelect">
                            <TpexMultiSelectSeach
                                id={`id-${column.id}-${addindex}`}
                                handleSelectedOptions={(e) => handleAddOptions(e,`${column.id}__${addindex}`)}
                                name={column.id}
                                value={addData[addindex][column.id+'Obj']}
                                isMulti={column.isMulti}
                                serverSide={false}
                                staticValues={selectBoxList(column.id, column.multiSelectList, addData[addindex])}
                            />
                            </div>
                          </td>
                        } else if (column.type === 'checkbox') {
                          return <td key={`td-${column.id}${addindex}`} className="mandatoryControl">
                            <Checkbox
                              type="checkbox"
                              name={`${column.id}__${addindex}`}
                              id={`${column.id}__${addindex}`}
                              handleClick={(e) => handleAddPLS(e)}
                              isChecked={chkBoxFlag(addData[addindex][column.id])}
                            />
                          </td>
                        } else if (column.type === 'text'){
                          return <td key={`td-${column.id}${addindex}`} className={column.required ? "mandatoryControl" : ""}>
                            <input
                              key={`txt-${column.id}${addindex}`}
                              id={`${column.id}${addindex}`}
                              name={`${column.id}__${addindex}`}
                              type="text"
                              className={`form-control`}
                              maxLength={column.maxLength}
                              value={addData[addindex][column.id]}
                              onChange={event => inputBoxChange(event)}
                            />
                          </td>
                        } else {
                          return <td key={`add-${column.id}-${addindex}`} className={`add-${column.id}`}>{isCopy ? addData[addindex][column.id] : ''} </td>
                        }
                      }
                        )}
                      </tr>)
                    })
                    }
                    {/* add end */}

                    {/* {rows start} */}
                    {calculatedRows.map((row, i) => {
                      const index = i;
                      return (
                        <tr key={row[idName]} onMouseOver={(event) => handleColumnHover(event)} onClick={e => changeToEdit(e, row, index)} className={editControlClass(isCheck.includes(row[primaryKey]), row['flag'], moduleName)}>
                          {selectRow === true &&
                            <td className="checkboxCol"><Checkbox
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
                          <EnginePartTableEditRow
                            editInputBoxChange={editInputBoxChange}
                            selectBoxChange={selectBoxChange}
                            handleSelectedOptions={handleSelectedOptions}
                            selectBoxList={selectBoxList}
                            handlePLSClick={handlePLSClick}
                            classNameRetun={classNameRetun}
                            chkBoxFlag={chkBoxFlag}
                            columns={columns}
                            isCheck={isCheck}
                            primaryKey={primaryKey}
                            dataForEdit={dataForEdit}
                            index={index}
                            row={row}
                            screenModule={moduleName}
                          />
                          
                        </tr>
                      )
                    })}
                    {/* no data in table  */}
                    {(isSearchBtnClick && isAdd && rows.length === 0)
                     || (rows.length === 0 && isAdd) ? '':
                    <TableNoData
                      rows={rows}
                      isLoading={isLoading}
                      columns={columns}
                      selectRow={selectRow}
                    />
                    }
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
      </div>

      {isCrud && columns.length ?
        <div className="gridfooter mt-10">
          <div className="row g-0">
            <div className="form-group col align-self-center">
              <div className="d-flex justify-content-end">
                <TpexSimpleButton color="primary" text={LABEL_CONST.SAVE} handleClick={event => handleActionClick(event)} />
              </div>
            </div>
          </div>
        </div>
        : ''
      }
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
};