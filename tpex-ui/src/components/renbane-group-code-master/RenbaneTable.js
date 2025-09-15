import { useState, useMemo, useEffect, useContext } from 'react'
import "../../styles/table.css";
import { sortRowsWithDateString, filterRows, paginateRows } from '../../common/components/tables/helpers';
import { Pagination } from '../../common/components/tables/Pagination';
import {
  deleteRequest,
  postRequest,
  putRequest
} from '../../services/axios-client';
import { MODULE_CONST } from '../../constants/constant';
import "react-datepicker/dist/react-datepicker.css";
import { LABEL_CONST } from '../../constants/label.constant.en';
import { columns, columnsData } from "./columns";
import Checkbox from '../../common/components/checkbox';

import { TableAction } from './TableAction';
import { TableAddRow } from './TableAddRow';
import { TableEditRow } from './TableEditRow';
import {
  isObject,
  isObjectEmpty,
  copyObject,
  createMesssageReplacer,
  createMessage,
  compareDates,
  formatedDate,
  formatedDate_ddmmyyyy
} from '../../helpers/util';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { getDeletedPayload } from "./custom-data";
import GridDataContext from './GridDataContext';

const RenbaneTable = ({
  rows = [],
  containerColumns=[],
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
  userId = "LoginUser",
  tableName = "",
  editTable = false,
  hideAction="",
  dropDownData=[],
  setDropDownDataList,
  validationObj = null,
  addFormAllowed = 10,
  customActions = false,
  handleCustomAction,
  customEdit = false,
  defaultSortingId = "",
  defaultSortingOrder = "asc",
  popupContent,
  isLoad,
  destination
}) => {

  const gridDataContextRef = useContext(GridDataContext);

  const [activePage, setActivePage] = useState(1);
  const [filters, setFilters] = useState({});
  const orderBydata = defaultSortingId || idName;
  const [sort, setSort] = useState({
    order: defaultSortingOrder,
    orderBy: orderBydata
  });
  const [isCheck, setIsCheck] = useState([]);
  const [formField, setFormField] = useState({});
  const [isAdd, setIsAdd] = useState(false);
  const [dataForEdit, setDataForEdit] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [filterToggle, setFilterToggle] = useState(false);
  const [messageType, setMessageType] = useState();
  const [messageText, setMessageText] = useState();
  const [modalShowAlert, setModalShowAlert] = useState(false);
  const [addData, setAddData] = useState([]);
  const [actionCase, setActionCase] = useState("");
  const [saveCaseData, setSaveCaseData] = useState([]);
  const rowsPerPage = rowPerPage;
  
  const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters]);
  const sortedRows = useMemo(() => sortRowsWithDateString(filteredRows, sort), [filteredRows, sort]);
  
  const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage);
  const count = filteredRows.length;
  const totalPages = Math.ceil(count / rowsPerPage);
  const columnsUpdated = columnsData;

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
  };

  const openAlertBox = (
    messegeType,
    messageText = "",
    type = ""
  ) => {
    if (type) {
      setActionCase(type);
    }
    setMessageType(messegeType);
    setMessageText(messageText);
    setModalShowAlert(true)
  };

  const clearAll = () => {
    setSort({
      order: defaultSortingOrder, orderBy: defaultSortingId || idName
    })
    setActivePage(1)
    setFilters({})
  };

  const handleSort = (id, date) => {
    setActivePage(1)
    setSort((prevSort) => ({
      order: prevSort.order === 'asc' && prevSort.orderBy === id ? 'desc' : 'asc',
      orderBy: id,
      orderDate: date
    }))
  };

  const handleColumnHover = (e) => {
    //hover
  };

  const checkBoxAndFalseCondition = (rowToUpdate) => {
    const rowUpdate = copyObject(rowToUpdate);
    if (dataForEdit[rowUpdate[idName]]) {
      let dataForEditRemove = copyObject(dataForEdit);
      delete dataForEditRemove[rowUpdate[idName]];
      setDataForEdit(dataForEditRemove);
    }
  };

  const checkBoxAndTrueCondition = (rowToUpdate) => {
    const rowUpdate = copyObject(rowToUpdate);
    if (!dataForEdit[rowUpdate[idName]]) {
      let dataForEditCopy = copyObject(dataForEdit);
      dataForEditCopy[rowUpdate[idName]] = rowUpdate;
      setDataForEdit(dataForEditCopy);
    }
  };

  const handleEditDateSelected = (date, dateName, idName) => {
    const dateFormatted = formatedDate(date);
    const dataForEditChange = dataForEdit;
    dataForEditChange[idName][dateName] = dateFormatted;
    setDataForEdit({ ...dataForEditChange });
  };

  const handleDateSelected = (date, dateName, idx) => {
    const dateFormatted = formatedDate_ddmmyyyy(date);
    const addDataCopy = copyObject(addData);
    addDataCopy[idx][dateName] = dateFormatted;
    setAddData(addDataCopy);
  };

  const renbanEditMode = (rowToUpdate) => {
    const rowUpdate = copyObject(rowToUpdate);
    const isSelectedRowAlreadyOpen = !!dataForEdit[rowUpdate[idName]];
    if (isSelectedRowAlreadyOpen) {
      if (isCheck.includes(rowUpdate[idName])) {
        const isCheckCopy = copyObject(isCheck);
        const index = isCheckCopy.indexOf(rowUpdate[idName]);
        if (index > -1) {
          isCheckCopy.splice(index, 1);
        }
        setIsCheck(isCheckCopy);
      }

      if (dataForEdit[rowUpdate[idName]]) {
        let dataForEditRemove = dataForEdit;
        delete dataForEditRemove[rowUpdate[idName]];
        setDataForEdit(dataForEditRemove);
      }

    } else {
      if (!isCheck.includes(rowUpdate[idName])) {
        setIsCheck([...isCheck, rowUpdate[idName]]);
      }

      if (!dataForEdit[rowUpdate[idName]]) {
        let dataForEditCopy = copyObject(dataForEdit);
        dataForEditCopy[rowUpdate[idName]] = rowUpdate;
        setDataForEdit(dataForEditCopy);
      }
    }
  };

  const changeToEdit = (e, rowToUpdate, index) => {
    setAddData([]);
    if (isCrud || customEdit) {
      setIsAdd(false);
      if (e.target.checked === false && e.target.type === "checkbox") {
        checkBoxAndFalseCondition(rowToUpdate);
       
      } else if (e.target.checked === true && e.target.type === "checkbox") {
        checkBoxAndTrueCondition(rowToUpdate);
       
      } else {
        if (e.type === "click" && e.target.tagName === "TD") {
          renbanEditMode(rowToUpdate);
        }
      }
    }
  };

  const handleClick = (e, row) => {
    const { id, checked } = e.target;
    setIsCheck([...isCheck, parseInt(id)]);
  
    if (!checked) {
      setIsCheck(isCheck.filter((item) => {
        return item !== parseInt(id);
      }));
    }
  };

  const sortIcon = (column) => {
    if (column.id === sort.orderBy) {
      if (sort.order === 'asc') {
        return <i className="arrow-up"></i>
      }
      return <i className="arrow-down"></i>
    } else {
      return <i className="arrow-down"></i>
    }
  };

  const toggleFilter = () => {
    setFilterToggle(!filterToggle);
  };

  const deleteRecords = () => { 
    const deletePayload = getDeletedPayload(
      isCheck,
      primaryKey,
      userId,
      tableName,
      moduleName,
      rows,
      dataForEdit
    );

    setIsLoading(true);
    deleteRequest(MODULE_CONST[moduleName].API_BASE_URL, MODULE_CONST[moduleName].DELETE_API, deletePayload).then(delResponse => {
      if (Number(delResponse.data.statusCode) === 200) {
        setIsCheck([]);
        setDataForEdit({});
        refreshGrid();
        openAlertBox(
          LABEL_CONST.INFORMATION,
          createMessage(delResponse.data.statusMessage)
        );
      } else {
        openAlertBox(
          LABEL_CONST.ERROR,
          delResponse.data.exception
        );
      }
      
    }).catch(function (error) {
      setIsCheck([]);
      setDataForEdit({});
      refreshGrid();
      if(error.response.data.statusCode === 400){
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.statusMessage));
      } else{
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
      }
    })
  };

  const getDateObj = validDateString => new Date(validDateString);

  const verifyDataForSave = dataToSave => {

    const anyETDFromGreaterThanETDTo = dataToSave.find(data => {

      const {etdFromDate, etdToDate} = data;

      return (getDateObj(etdFromDate) > getDateObj(etdToDate));
    }); 
    
    return !anyETDFromGreaterThanETDTo;
  };

  const saveDataWithCall = payloadToSend => {

    if (addData && addData.length > 0) {
      postRequest(
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.API_BASE_URL,
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.SAVE_API,
        payloadToSend
      ).then(res => {
        setAddData([]);
        setIsCheck([]);
        refreshGrid();
        setIsLoading(false);
        openAlertBox(LABEL_CONST.INFORMATION, createMessage(res.data.statusMessage));
      }).catch(err => {
        console.log(err.response.data.exception);
        setIsLoading(false);
        openAlertBox(
          LABEL_CONST.ERROR,
          createMessage(err.response.data.exception)
        );
      });
    } else {
      // update 
      putRequest(
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.API_BASE_URL,
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.UPDATE_API,
        payloadToSend
      ).then(res => {
        setAddData([]);
        setIsCheck([]);
        refreshGrid();
        setIsLoading(false);
        openAlertBox(LABEL_CONST.INFORMATION, createMessage(res.data.statusMessage));
      }).catch(err => {
        console.log(err.response.data.exception);
        setIsLoading(false);
        openAlertBox(
          LABEL_CONST.ERROR,
          createMessage(err.response.data.exception)
        );
      });
    }
  };

  const handleAlertConfirmation = (e) => {
    if (e.target.title === "deleteRecordCase-Warning") {
     deleteRecords();
    }

    if (e.target.title === "saveRecordCase-Warning") {
      const isDataAllowedToSave = verifyDataForSave(saveCaseData);

      if (isDataAllowedToSave) {
        saveDataWithCall(saveCaseData);
      } else {
        openAlertBox(
          LABEL_CONST.ERROR,
          createMessage(LABEL_CONST.ERR_CM_3007)
        );
      }
      
    }
  };

  const handleActionClick = (e) => {
    if (e.target.textContent.toLowerCase() === 'add') {
      if (addData.length < addFormAllowed) {
        clearAll();
        setFilterToggle(false);
        
        if (!isAdd) {
          setIsAdd(true);
        }

        setIsCheck([]);
        setDataForEdit({});
        const keyWithBlankValue = columnsUpdated.reduce((acc, cur) => {
          acc[cur.id] = "";
          return acc;
        }, {});
        const addDataCopy = copyObject(addData);
        addDataCopy.push(keyWithBlankValue);
        setAddData(addDataCopy);
      } else {
        openAlertBox(
          LABEL_CONST.ERROR,
          createMesssageReplacer(
            {
              noOfRecord: addFormAllowed
            },
            'ADD_MAX_FORM_ERR',
            true
          )
        );
      }
    }

    if (e.target.textContent.toLowerCase().startsWith('delete')) {
      if (isCheck.length > 0) {
        openAlertBox(LABEL_CONST.WARNING, createMesssageReplacer({ noOfRecord: isCheck.length }, 'RECORD_DELETED_CONFIRMATION', true), 'deleteRecordCase');
      }
      else {
        openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3004'));
      }
    }
  };

  const removeAddForm = (e, index) => {
    const addFormCopy = copyObject(addData).filter((f, i) => i !== index);
    setAddData([...addFormCopy]);
    if (!addFormCopy.length) {
      setIsAdd(false);
    }
  };

  const inputBoxChange = (e, index) => {
    if (isAdd) {
      const addDataCopy = copyObject(addData);
      const nameIndex = e.target.name.split("__");
      addDataCopy[nameIndex[1]][nameIndex[0]] = e.target.value;
      setAddData(addDataCopy);
    } else {
      setFormField({
        ...formField,
        [e.target.name]: e.target.value
      })
    }
  };

  const editClass = (key) => {
    return key !== true ? "nonEditable": "editable";
  };

  const dropdownChange = (e, colname, idx, dropDownId) => {
    if (isAdd) {
      const addDataCopy = copyObject(addData);
      addDataCopy[idx][colname] = e.map(selectedObj => selectedObj.value).join();
      setAddData(addDataCopy);
    }
  };

  const splittedGroups = codes => codes.split(',');

  const getGroupDetailsToAdd = (renGroupCode, groupId, folderName) => {
    
    const groupDetails = [];
    const codes = splittedGroups(renGroupCode);

    codes.forEach(code => {
      groupDetails.push({
        groupId: groupId,
        renbanGroupCode: code.trim(),
        folderName: folderName
      });  
    });

    return groupDetails;
  };

  const getGroupDetails = ({
    renGroupCode1,
    folderName1,
    renGroupCode2,
    folderName2,
    renGroupCode3,
    folderName3,
    renGroupCode4,
    folderName4,
    renGroupCode5,
    folderName5
  }) => {

    const groupDetails = [];
  
    if (renGroupCode1 && renGroupCode1 !== '') {

      const groupDetailsToAdd = getGroupDetailsToAdd(renGroupCode1, "1", folderName1);

      groupDetails.push(...groupDetailsToAdd);
      
    }

    if (renGroupCode2 && renGroupCode2 !== '') {
      const groupDetailsToAdd = getGroupDetailsToAdd(renGroupCode2, "2", folderName2);

      groupDetails.push(...groupDetailsToAdd);

    }

    if (renGroupCode3 && renGroupCode3 !== '') {
      const groupDetailsToAdd = getGroupDetailsToAdd(renGroupCode3, "3", folderName3);

      groupDetails.push(...groupDetailsToAdd);

    }

    if (renGroupCode4 && renGroupCode4 !== '') {
      const groupDetailsToAdd = getGroupDetailsToAdd(renGroupCode4, "4", folderName4);

      groupDetails.push(...groupDetailsToAdd);

    }

    if (renGroupCode5 && renGroupCode5 !== '') {
      const groupDetailsToAdd = getGroupDetailsToAdd(renGroupCode5, "5", folderName5);

      groupDetails.push(...groupDetailsToAdd);

    }

    return groupDetails;
  };

  const getPayloadData = dataArray => {
    return dataArray.map(data => {
      const {
        renGroupCode1,
        folderName1,
        renGroupCode2,
        folderName2,
        renGroupCode3,
        folderName3,
        renGroupCode4,
        folderName4,
        renGroupCode5,
        folderName5,
        effectiveFromDt,
        effctiveToDt
      } = data;

      return {
        contDstCd: destination.value,
        updateBy: userId,
        etdFromDate: effectiveFromDt,
        etdToDate: effctiveToDt,
        goupdIdDetails: getGroupDetails({
          renGroupCode1,
          folderName1,
          
          renGroupCode2,
          folderName2,

          renGroupCode3,
          folderName3,

          renGroupCode4,
          folderName4,

          renGroupCode5,
          folderName5
        })
      };
    });
  };

  const getDateFromDateString = dateString => {

    const [d, m, y] = dateString.split(/\D/);

    return new Date(y, m-1, d);
  };

  const getDayDifference = payloadData => {

    if (rows.length === 0) {
      return 1;
    }
    
    const allETDToDatesFromExisting = rows.map(
      rowObj => getDateFromDateString(rowObj['effctiveToDt'])
    );

    const lastETDToDateFromExisting = Math.max.apply(null, [...allETDToDatesFromExisting]);

    const allETDFromDateFromPayload = payloadData.map(
      _saveCaseData => {
        return getDateFromDateString(_saveCaseData['etdFromDate']);
      }
    );
    
    const firstETDFromDateFromPayload = Math.min.apply(null, [...allETDFromDateFromPayload]);

    return (
      (firstETDFromDateFromPayload - lastETDToDateFromExisting) / (1000 * 3600 * 24)
    );
  };

  const checkForOverlapping = payloadData => {
    const overlappingRecords = payloadData.filter(_payload => {
      const overlappingRecord = rows.find(row => {

        const rowFromDate = getDateFromDateString(row.effectiveFromDt);
        const rowToDate = getDateFromDateString(row.effctiveToDt);

        const payloadFromDate = getDateFromDateString(_payload.etdFromDate);
        
        return ((rowFromDate <= payloadFromDate) && (rowToDate >= payloadFromDate));
      });

      return !!overlappingRecord;
    });

    if (overlappingRecords && overlappingRecords.length > 0) {
      openAlertBox(
        LABEL_CONST.ERROR,
        createMessage(LABEL_CONST.OVERLAPPING_ETD_FROM_AND_TO)
      );
    } else {
      setSaveCaseData(payloadData);

      openAlertBox(
        LABEL_CONST.WARNING,
        createMessage(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES),
        'saveRecordCase'
      );
    }
  };

  const checkForAddOrEditPayload = payloadData => {
    if (addData && addData.length > 0) {

      checkForOverlapping(payloadData);
    } else {

      const anyInValidEtdToDate = payloadData.find(_payload => {
        const payloadFromDate = getDateFromDateString(_payload.etdFromDate);
        const payloadToDate = getDateFromDateString(_payload.etdToDate);
        return (payloadToDate < payloadFromDate);
      });

      if (anyInValidEtdToDate) {
        openAlertBox(
          LABEL_CONST.ERROR,
          createMessage(LABEL_CONST.ETD_TO_LESS_THAN_ETD_FROM)
        );  
      } else {
        setSaveCaseData(payloadData);

        openAlertBox(
          LABEL_CONST.WARNING,
          createMessage(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES),
          'saveRecordCase'
        );
      }
    }
  };

  const showMessageAndOrSaveData = (dayDifference, payloadData) => {

    if (dayDifference > 1) {
      setSaveCaseData(payloadData);

      openAlertBox(
        LABEL_CONST.WARNING,
        createMessage(LABEL_CONST.ETD_FROM_NOT_IN_CONTINUATION),
        'saveRecordCase'
      );
    } else if (dayDifference === 1) {
      setSaveCaseData(payloadData);

      openAlertBox(
        LABEL_CONST.WARNING,
        createMessage(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES),
        'saveRecordCase'
      );
    } else {
      checkForAddOrEditPayload(payloadData);
    }
  };

  const anyMandatoryInformationNotFilled = dataToCheck => {
    return  dataToCheck.filter(data => {
      return (
        data['effctiveToDt'] === '' ||
        data['effectiveFromDt'] === '' ||
        data['renGroupCode1'] === '' ||
        data['folderName1'] === ''
      );
    });
  };

  const checkRealEditAndSave = payloadData => {
    const reallyEditedData = payloadData.filter(data => {
      const etdToDateBeforeEditObj = rows.find(dataBeforeEdit => dataBeforeEdit['effectiveFromDt'] === data['etdFromDate'])

      if (etdToDateBeforeEditObj) {
        return etdToDateBeforeEditObj['effctiveToDt'] !== data['etdToDate'];
      }

      return false;
    });

    if (reallyEditedData.length > 0) {
      const dayDifference = getDayDifference(payloadData);

      showMessageAndOrSaveData(dayDifference, payloadData);
    } else {
      openAlertBox(
        LABEL_CONST.INFORMATION,
        createMessage(LABEL_CONST.NO_CHANGES_TO_SAVE)
      );
    }
  };

  const saveAddedData = addData => {
    const mandatoryCheckObj = anyMandatoryInformationNotFilled(addData);

      if (mandatoryCheckObj.length > 0) {
        openAlertBox(
          LABEL_CONST.ERROR,
          createMessage(LABEL_CONST.SELECT_MANDATORY_INFO)
        );

      } else {
        const payloadData = getPayloadData(addData);

        const dayDifference = getDayDifference(payloadData);

        showMessageAndOrSaveData(dayDifference, payloadData);
      }
  };

  const saveEditedData = dataForEdit => {
    if (dataForEdit && isObject(dataForEdit) && !isObjectEmpty(dataForEdit)) {
      const editedData = [];
      for (const prop in dataForEdit) {
        editedData.push(dataForEdit[prop]);
      }
      const payloadData = getPayloadData(editedData);

      checkRealEditAndSave(payloadData);
    } else {
      openAlertBox(
        LABEL_CONST.ERROR,
        createMessage(LABEL_CONST.SELECT_MANDATORY_INFO)
      );  
    }
  };
  
  const saveRenbaneData = () => {
    if (addData.length > 0) {
      saveAddedData(addData);
    } else {
      if (rows && rows.length > 0) {
        saveEditedData(dataForEdit);
      } else {
        openAlertBox(
          LABEL_CONST.INFORMATION,
          createMessage(LABEL_CONST.NO_CHANGES_TO_SAVE)
        );
      }
    }
  };

  useEffect(() => {
    setTimeout(function () {
      setIsLoading(false);
    }, 2000);
  }, []);

  useEffect(() => {
    clearAll();
  }, [rows]);

  useEffect(() => {
    if (addData.length > 0) {
      gridDataContextRef.current = {
        isGridEditable: true
      };
    } else {
      gridDataContextRef.current = {
        isGridEditable: false
      };
    }
    
  }, [addData]);

  useEffect(() => {
    if(gridDataContextRef.current.removeAdded) {
      gridDataContextRef.current.removeAdded = false;
      setIsAdd(false);
      setAddData([]);
    }
  }, [gridDataContextRef.current.removeAdded]);

  return (
    <>
    
      {/* table action  */}
      <TableAction
        isCrud={isCrud}
        columns={columnsUpdated}
        isCheck={isCheck}
        hideAction={!!destination}
        handleActionClick={handleActionClick}
        toggleFilter={toggleFilter}
        filter={filter} 
        columns2={columns}
      />

      <div className="grid-table">
        <div className="row g-0">
          <div>
            <div className="col">
              <div className="table-responsive tpexTableRenbane-height">
                <table className={`table tpexTable tpexTableRenbane tableWithChecbox ${editTable ? "tpexTableEdit" : ""}`}>
                  <thead className="text-nowrap">
                    {columns.length ?
                      <tr key="grid-sort-asc-dsc-group">
                        {columns.map((column) => {
                          if(column.group === true){
                            return(
                              <th colSpan="2" className="colSpan-th" key={column.id}>
                                <span>{column.name}</span>
                              </th>
                              )
                          }else{
                            if(column.name === ''){
                              return(
                                <th key={column.id} className="checkboxCol">
                                  <span>{column.name}</span>
                                </th>
                                )
                            }else{
                              return(
                                <th rowSpan="2" key={column.id}>
                                  <span>{column.name}</span>
                                  <span onClick={() => handleSort(column.id, column.date)}>{sortIcon(column)}</span>
                                </th>
                                )
                            }
                          }
                        })}
                      </tr>
                      : ''
                    }
                    {columnsData.length ?
                      <tr key="grid-sort-asc-dsc">
                        {columnsData.map((column) => {
                          if(column.date !== true){
                            if(column.id === 'check'){
                              return(
                                <th key={column.id} className="checkboxCol">
                                  <span>{column.name}</span>
                                </th>
                              )
                            }else{
                              return(
                                <th scope="col" key={column.id}>
                                  <span>{column.name}</span>
                                  <span onClick={() => handleSort(column.id, column.date)}>{sortIcon(column)}</span>
                                </th>
                              )
                            }
                          }
                        })}
                      </tr>
                      : ''
                    }
                    {filterToggle ?
                      <tr>
                        {
                          selectRow ?
                          <th className="checkboxCol"></th> : ''
                        }
                        {
                          filterToggle &&
                          serverSideFilter === false &&
                          columnsData.map(column => {
                              if(column.id !== 'check'){
                                return (
                                  <th scope="col" key={`${column.id}-filter`} className="pt-0">
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
                              }
                            }
                          )
                        }
                      </tr>
                      : ''
                    }
                  </thead>
                  <tbody>
                    {/* add */}
                    {
                      isAdd &&
                      addData.map((add, addindex) => {
                        return (
                          <tr key={`tr-${addKey++}`}>
                            <td className="checkboxCol">
                              <div className="minus-box">
                                <i
                                  className="minus-icon"
                                  onClick={event => removeAddForm(event, addindex)}
                                ></i>
                              </div>
                            </td>
                            <TableAddRow
                              columns={columnsUpdated}
                              addData={addData}
                              dropDownData={dropDownData}
                              addindex={addindex}
                              inputBoxChange={inputBoxChange}
                              handleDateSelected={handleDateSelected}
                              dropdownChange={dropdownChange}
                              setDropDownDataList={setDropDownDataList}
                            />
                          </tr>
                        )
                      })
                    }

                    {/* add end */}

                    {calculatedRows.map((row, index) => {
                      return (
                        <tr
                          className={editClass(
                            compareDates(row.effectiveFromDt, row.effctiveToDt)
                          )}
                          key={row[idName]}
                          onMouseOver={(event) => handleColumnHover(event)}
                          onClick={e => changeToEdit(e, row, index)}
                        >
                          {
                            selectRow === true &&
                            <td className="checkboxCol">
                              <Checkbox
                                key={row[idName]}
                                type="checkbox"
                                name={row[idName]}
                                id={row[idName]}
                                handleClick={(e) => handleClick(e, row)}
                                isChecked={isCheck.includes(row[idName])}
                             />
                            </td>
                          }
                          
                          {
                            columnsData.map((column) => {
                              if (isCheck.includes(row[idName])){
                               return (
                                    <TableEditRow
                                      handleEditDateSelected={handleEditDateSelected}
                                      isCheck={isCheck}
                                      idName={idName}
                                      dataForEdit={dataForEdit}
                                      index={index}
                                      row={row}
                                      column={column}
                                      key={`${column.id}`}
                                  />
                                ) 
                                }else{
                                  return(
                                    column.id !== 'check' && <td id={`${column.id}-${index}`} key={`${column.id}`} className={`${row[column.id]}-status ${column.type === "number" ? ' text-end' : ''}`}>
                                      <span>
                                        {row[column.id]}
                                      </span>
                                    </td>
                                  )
                                }
                              })
                           }
                        </tr>
                      )
                    })}

                    {/* no data in table  */}
                    {!rows.length && !isLoading ?
                      <tr>
                        <td
                          colSpan={columns.length + (selectRow ? 1 : 0)}
                          className="noData"
                        >{LABEL_CONST.NO_DATA_FOUND}</td>
                      </tr>
                      : ""
                    }

                  </tbody>
                </table>
              </div>
              {
                pagination === true &&
                count > 0 ? (
                  <Pagination
                    activePage={activePage}
                    count={count}
                    rowsPerPage={rowsPerPage}
                    totalPages={totalPages}
                    pagination={pagination}
                    setActivePage={setActivePage}
                  />
                ) : ''
              }
            </div>
          </div>
        </div>
      </div>
      
      <div className="row mt-10">
          <div className="form-group col align-self-center">
              <div className="d-flex justify-content-end">
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={saveRenbaneData}
                  >Save</button>
              </div>
          </div>
      </div>
      
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
  );
};

export default RenbaneTable;