import React, { useState , useMemo, useEffect, useContext} from 'react';
import DataTableFilter from './DataTableFilter';
import DataTableHeader from './DataTableHeader';
import DataTableEditRow from './DataTableEditRow';
import { sortRowsWithDateString, filterRows, paginateRows } from '../../common/components/tables/helpers'
import { copyObject, createMesssageReplacer  } from '../../helpers/util';
import Checkbox from '../../common/components/checkbox';
import { Pagination } from '../../common/components/tables/Pagination';
import TpexSimpleButton from '../../common/components/button';
import UplaodDownload from './uploadDownload';
import { LABEL_CONST } from '../../constants/label.constant.en';
import PmiDataContext from "../../context/PmiDataContext";
import DataTableAddForm from './DataTableAddForm';
import { primaryKey , formatPartNumber, formatPartWeight} from './partMasterUtil.js';

const DataTable = ({
  rows,
  columns,
  openAlertBoxPmi,
  savePmi,
  setRowsPmi
}) => {

    const pmiDataContextRef = useContext(PmiDataContext);
    const filter = true;
    const customEdit = false;
    const idName = "partNo";
    const defaultSortingId = "idList";
    const isCrud = true;
    const rowsPerPage = 10;
    const selectRow = true;
    const defaultSortingOrder = "asc";
    const isPagination = true;

    const [activePage, setActivePage] = useState(1)
    const [filters, setFilters] = useState({});
    const orderBydata = defaultSortingId;
    const [sort, setSort] = useState({ order: defaultSortingOrder, orderBy: orderBydata });
    const [filterToggle, setFilterToggle] = useState(false);
    const [dataForEditPmi, setDataForEditPmi] = useState({});
    const [isCheck, setIsCheck] = useState([]);
    const [isAdd, setIsAdd] = useState(false);
    const [addData, setAddData] = useState([]);

    const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters]);
    const sortedRows = useMemo(() => sortRowsWithDateString(filteredRows, sort), [filteredRows, sort]);
    const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage);
    const count = filteredRows.length;
    const totalPages = Math.ceil(count / rowsPerPage);
    const addFormAllowed = 10;

    const toggleFilter = () =>  {
        setFilterToggle(!filterToggle);
    }

    const handleSort = (id, date) => {
        setActivePage(1)
        setSort((prevSort) => ({
          order: prevSort.order === 'asc' && prevSort.orderBy === id ? 'desc' : 'asc',
          orderBy: id,
          orderDate: date
        }));
    }
    
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

    const checkBoxAndFalseCondition = (rowToUpdate) => {
        const rowUpdate = copyObject(rowToUpdate);
        if (dataForEditPmi[rowUpdate[primaryKey]]) {
            let dataForEditRemove = copyObject(dataForEditPmi);
            delete dataForEditRemove[rowUpdate[primaryKey]];
            setDataForEditPmi(dataForEditRemove);
        }
    }

    const checkBoxAndTrueCondition = (rowToUpdate) => {
        const rowUpdate = copyObject(rowToUpdate);
        if (!dataForEditPmi[rowUpdate[primaryKey]]) {
          let dataForEditCopy = copyObject(dataForEditPmi);
          dataForEditCopy[rowUpdate[primaryKey]] = rowUpdate;
          setDataForEditPmi(dataForEditCopy);
        }
      }

    const typeclickAndTagTD = (rowToUpdate) => {
        const rowUpdate = copyObject(rowToUpdate);
        const isSelectedRowAlreadyOpen = dataForEditPmi[rowUpdate[primaryKey]];
        if (isSelectedRowAlreadyOpen) {
          if (isCheck.includes(rowUpdate[primaryKey])) {
            const isCheckCopy = copyObject(isCheck);
            const index = isCheckCopy.indexOf(rowUpdate[primaryKey]);
            if (index > -1) {
              isCheckCopy.splice(index, 1);
            }
            setIsCheck(isCheckCopy);
          }
    
          if (dataForEditPmi[rowUpdate[primaryKey]]) {
            let dataForEditRemove = dataForEditPmi;
            delete dataForEditRemove[rowUpdate[primaryKey]];
            setDataForEditPmi(dataForEditRemove);
          }
        } else {
          if (!isCheck.includes(rowUpdate[primaryKey])) {
            setIsCheck([...isCheck, rowUpdate[primaryKey]]);
          }          
          if (!dataForEditPmi[rowUpdate[primaryKey]]) {
            let dataForEditCopy = copyObject(dataForEditPmi);
            dataForEditCopy[rowUpdate[primaryKey]] = rowUpdate;
            setDataForEditPmi(dataForEditCopy);
          }
        }
      }

    const changeToEdit = (e, rowToUpdate) => {
        if ((isCrud || customEdit)) {
          setIsAdd(false);
          setAddData([]);
          const isChecked = e.target.checked;
          const typeCheck = e.target.type === "checkbox";
          if (isChecked === false && typeCheck) {
            checkBoxAndFalseCondition(rowToUpdate);           
          } else if(isChecked === true && typeCheck) {
            checkBoxAndTrueCondition(rowToUpdate);           
          } else {    
            if (e.type === "click" && e.target.tagName === "TD") {
              typeclickAndTagTD(rowToUpdate);             
            }
          }
        }
      }
    

    

    const clearAll = () => {
        setSort({ order: defaultSortingOrder, orderBy: defaultSortingId })
        setActivePage(1)
        setFilters({})
    }

    const handleClick = e => {
        const { id, checked } = e.target;
        setIsCheck([...isCheck, id]);
        if (!checked) {
          setIsCheck(isCheck.filter(item => item !== id));
        }
    };

    const removeAddForm = (_event, index) => {
      
      const addFormCopy = copyObject(addData).filter((_f, i) => i !== index);
      setAddData([...addFormCopy]);
      if (!addFormCopy.length) {
        setIsAdd(false);
      }
    }


    const addPmi = () => {
      if (addData.length < addFormAllowed) {
        clearAll();
        setFilterToggle(false);        
        setIsCheck([]);
        setDataForEditPmi({});
        const keyWithBlankValue = columns.reduce((acc, cur) => {
          acc['inhouseShop__Disable'] = true;
          acc[cur.id] = "";
          return acc;
        }, {});
        const addDataCopy = copyObject(addData);
        addDataCopy.push(keyWithBlankValue);
        setAddData(addDataCopy);
        isAdd === false && setIsAdd(true);         
        
      } else {
        openAlertBoxPmi(LABEL_CONST.ERROR, createMesssageReplacer({ noOfRecord: addFormAllowed }, 'ADD_MAX_FORM_ERR', true));
      }
    }

    const deletePmi = () => {
      if (rows.length === 0) {
        openAlertBoxPmi(LABEL_CONST.ERROR, LABEL_CONST.SEARCH_HAS_NOT_BEEN_PERFORMED);
      }
      else if (isCheck.length > 0) {
        openAlertBoxPmi(LABEL_CONST.WARNING, createMesssageReplacer({ noOfRecord: isCheck.length }, 'RECORD_DELETED_CONFIRMATION', true), 'deleteRecordCase');
      }
      else {
        openAlertBoxPmi(LABEL_CONST.ERROR, LABEL_CONST.ERR_CM_3004);
      }
    }

    const handleActionClick = (e, type) => {
        
      switch(type) {
          case 'Add':
            addPmi();
          break;
          case 'Delete':
            deletePmi();
          break;
          case 'Save':
            savePmi();
          break;
          default :
            break;
      }
    }

    const inputBoxChange = (e, index) => {
      const addDataCopy = copyObject(addData);
      const nameIndex = e.target.name.split("__");
      const name = nameIndex[0];
      let value = e.target.value;
      value = (name === "partNo") ? formatPartNumber(value) : value;
     
      
      if(name === "partWeight"){
        value = formatPartWeight(value, addDataCopy[nameIndex[1]][nameIndex[0]]);
      }

      if(name === "partType"){
        addDataCopy[index]['inhouseShop__Disable'] = value !== "3";
        addDataCopy[index]['inhouseShop'] = value === "3" ? addDataCopy[index]['inhouseShop'] : '';
      }
      addDataCopy[nameIndex[1]][nameIndex[0]] = value;
      setAddData(addDataCopy);
    }

    const editInputBoxChange = (e, pKey) => {
      const inputToChange = e.target.name.split("__");
      const dataForEditChange = dataForEditPmi;

      const name = inputToChange[0];
      let value = e.target.value;
      value = (name === "partNo") ? formatPartNumber(value) : value;
      
      value = (name === "partWeight") ? formatPartWeight(value, dataForEditChange[pKey][inputToChange[0]]) : value;

      if(name === "partType"){
        dataForEditChange[pKey]['inhouseShop__Disable'] = value !== "3";
        dataForEditChange[pKey]['inhouseShop'] = value === "3" ? dataForEditChange[pKey]['inhouseShop'] : '';
      }

      dataForEditChange[pKey][inputToChange[0]] = value;
      setDataForEditPmi({ ...dataForEditChange });
    }

    useEffect(() => {
        setAddData([]);
        setIsAdd(false);
    }, []);

    useEffect(() => {  
        /* */
        setIsAdd(false);
        setDataForEditPmi({});
        setIsCheck([]);
        setAddData([]);
        /* */      
        setActivePage(1);
        clearAll();
    }, [rows]);

    useEffect(() =>{      
      pmiDataContextRef.addData = addData;
    }, [addData]);

    useEffect(() => {
      pmiDataContextRef.editData = dataForEditPmi;
    }, [dataForEditPmi]);

    return (
        <>

            <div className='col-12'>
              <div className="form-group col-12 align-self-center g-0 btn-container">
                <TpexSimpleButton color="outline-primary" text="Add" handleClick={event => handleActionClick(event, "Add")} />
                <TpexSimpleButton color="outline-primary" leftmargin="3" text={`Delete (${isCheck.length})`} handleClick={event => handleActionClick(event, "Delete")} />
                <DataTableFilter
                  toggleFilter={toggleFilter}
                  filter={filter}
                  columns={columns}
                />
              </div>
            </div>
            
            <div className="grid-table mb-10">
                <div className="row g-0">
                    <div>
                        <div className="col">
                            <div className="table-responsive table-responsive-height">
                                <table className="table tpexTable tableWithChecbox tpexTableEdit">
                                <DataTableHeader
                                    selectRow={true}
                                    columns={columns}
                                    sort={sort}
                                    handleSort={handleSort}
                                    filterToggle={filterToggle}
                                    serverSideFilter={false}
                                    filters={filters}
                                    handleSearch={handleSearch}
                                />
                                    <tbody>
                                      {/* add */}
                                      {(isAdd) && <DataTableAddForm 
                                                addData={addData}
                                                removeAddForm = {removeAddForm}
                                                columns = {columns}
                                                inputBoxChange = {inputBoxChange}
                                                isCheck = {isCheck}
                                                /> }
                                    
                                    
                                    {/* add end */}
                                    {calculatedRows.map((row, index) => {
                                        return (
                                            <tr 
                                            key={row[idName]} 
                                            onClick={e => changeToEdit(e, row)} 
                                            className={isCheck.includes(row[primaryKey]) ? `` : `editTableRow`}
                                            >
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
                                              <DataTableEditRow                                              
                                                row={row}
                                                isCheck={isCheck}
                                                columns={columns}
                                                primaryKey={primaryKey}
                                                index={index}
                                                dataForEdit={dataForEditPmi}
                                                editInputBoxChange={editInputBoxChange}
                                              />
                                              
                                            </tr>
                                          )
                                    })}
                                    </tbody>
                                </table>
                            </div>

                            <Pagination
                              activePage={activePage}
                              count={count}
                              rowsPerPage={rowsPerPage}
                              totalPages={totalPages}
                              pagination={isPagination}
                              setActivePage={setActivePage}
                            />
                            
                        </div>
                    </div>
                </div>
            </div>

            {/* table action  */}
            <div className="gridfooter">
              <div className="row">
                <div className="col g-0">
                  <div className="heading">
                    <i className="bg-border"></i>
                    <h1>Upload Download Action</h1>
                  </div>
                </div>
              </div>
            </div>
            <div className="row mt-10">
            <UplaodDownload setRowsPmi={setRowsPmi}/>
              <div className="form-group col-2 align-self-center">
                <div className="d-flex justify-content-end">
                  <button 
                    type="button"
                    className="btn btn-primary"
                    onClick={event => handleActionClick(event, "Save")}
                  >Save</button>
                  </div>
              </div>
            </div>


        </>
    )
}

export default DataTable;
