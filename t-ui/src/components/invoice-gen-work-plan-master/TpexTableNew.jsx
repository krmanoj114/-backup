import { useState, useMemo, useEffect } from 'react';
import "../../styles/table.css";
import { sortRowsWithDateString, filterRows, paginateRows } from '../../common/components/tables/helpers';
import Checkbox from '../../common/components/checkbox';
import { formatedDate, ddmmTOmmddChange, copyObject } from '../../helpers/util';
import { TpexSelect } from '../../common/components/select';
import ReactPaginate from 'react-paginate';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';

export const TpexTableNew = ({ columns, rows, moduleName = "", selectAll, idName = "id", freezetable,
  rowPerPage, simpletable = false, margin = "", isCrud = false, totalItemsServer,
  currentServerPage, onPaginationClick, setClickSave, refreshGridSave }) => {
  const [activePage, setActivePage] = useState(1)
  const [filters, setFilters] = useState({})
  const [sort, setSort] = useState({ order: 'asc', orderBy: idName, orderDate: false })
  const [isCheckAll, setIsCheckAll] = useState(false);
  const [isCheck, setIsCheck] = useState([]);

  const rowsPerPage = rowPerPage;

  const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters])
  const sortedRows = useMemo(() => sortRowsWithDateString(filteredRows, sort), [filteredRows, sort])
  const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage)
  const count = filteredRows.length
  const [editIndex, setEditIndex] = useState([]);
  const [tableInputs, setTableInputs] = useState([]);
  const [tableDates, setTableDates] = useState([]);
  const [tableSelects, setTableSelects] = useState([]);
  const [formField, setFormField] = useState([]);//reqPayLoadForSave
  const [fldEdit, setFldEdit] = useState(false);
  const [hideFilter, setHideFilter] = useState(true);

  useEffect(() => {
    setFormField(rows);
    setFldEdit(refreshGridSave);
    setEditIndex([]);
  }, [rows, refreshGridSave]);

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
    setIsCheck(rows.map(li => li.id));
    if (isCheckAll) {
      setIsCheck([]);
    }
  };

  const handleClick = e => {
    const { id, checked } = e.target;
    setIsCheck([...isCheck, id]);
    if (!checked) {
      setIsCheck(isCheck.filter(item => item !== id));
    }
  };
  const inputBoxChange = (e, idNm) => {
    const idName = idNm.split("__")[0];
    const idx = idNm.split("__")[1];
    let tableInputsCopy = copyObject(tableInputs);

    if(tableInputs[idx]) {
      tableInputsCopy[idx] = {
        ...tableInputs[idx],
        [idName]: e.target.value
      }
    } else {
      tableInputsCopy[idx] = {
        vessel1: '',
        voy1: '',
        vessel2: '',
        voy2: '',
        vessel3: '',
        voy3: '',
        [idName]: e.target.value
      }
    }
   
    setTableInputs(tableInputsCopy);
    let finalData = copyObject(formField);
    finalData[idx] = {
      ...formField[idx], [idName]:e.target.value
    }
    setFormField(finalData);
  }
  const selectBoxChange = (e, idNm) => {
    const idName = idNm.split("__")[0];
    const idx = idNm.split("__")[1];
    let tableSelectsCopy = copyObject(tableSelects);

    if(tableSelects[idx]) {
      tableSelectsCopy[idx] = {
        ...tableSelects[idx],
        [idName]: e.target.value
      }
    } else {
      tableSelectsCopy[idx] = {
        broker: '',
        portOfLoading: '',
        portOfDischarge: '',
        [idName]: e.target.value
      }
    }
    setTableSelects(tableSelectsCopy);
    let finalData = copyObject(formField);
    finalData[idx] = {
      ...formField[idx], [idName]:e.target.value
    }
    setFormField(finalData);
  }
  const dateBoxChange = (e, idNm) => {
    const idName = idNm.split("__")[0];
    const idx = idNm.split("__")[1];
    let tableDatesCopy = copyObject(tableDates);

    if(tableDates[idx]) {
      tableDatesCopy[idx] = {
        ...tableDates[idx],
        [idName]: e
      }
    } else {
      tableDatesCopy[idx] = {
        issueInvoiceDate: '',
        etd1: '',
        eta1: '',
        etd2: '',
        eta2: '',
        etd3: '',
        eta3: '',
        [idName]: e
      }
    }
   
    setTableDates(tableDatesCopy);
    const dateFormateChk = formatedDate(e);
    let finalData = copyObject(formField);
    finalData[idx] = {
      ...formField[idx], [idName]:dateFormateChk
    }
    setFormField(finalData);
  }

  useEffect(() => {
    setClickSave(formField);
  }, [formField]);


  function handlePageClick(data) {
    onPaginationClick(data.selected);
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
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isCheck])

  useEffect(() => {
    setIsCheckAll(false);
    setIsCheck([]);
  }, [columns])

  function dateSelectVal(date){
    return date !== null ? new Date(ddmmTOmmddChange(date)) : date;
  }
  function editRow(e,index, sRow) {
    if(e.target.tagName.toLowerCase()==="td"){    
    const selectedRow = JSON.parse(JSON.stringify(sRow));
    setFldEdit(!fldEdit);

    let tableDatesCopy = tableDates;

    tableDatesCopy[index] = {
      ...tableDates[index],
      issueInvoiceDate: dateSelectVal(selectedRow.issueInvoiceDate),
      etd1: dateSelectVal(selectedRow.etd1),
      eta1: dateSelectVal(selectedRow.eta1),
      etd2: dateSelectVal(selectedRow.etd2),
      eta2: dateSelectVal(selectedRow.eta2),
      etd3: dateSelectVal(selectedRow.etd3),
      eta3: dateSelectVal(selectedRow.eta3),
    }
    setTableDates(tableDatesCopy);

    let tableSelectsCopy = copyObject(tableSelects);
    tableSelectsCopy[index] = {
    ...tableSelects[index],
      
      broker: selectedRow.broker,
      portOfLoading: selectedRow.portOfLoading,
      portOfDischarge: selectedRow.portOfDischarge
  }
  setTableSelects(tableSelectsCopy);
    let tableInputsCopy = copyObject(tableInputs);
    tableInputsCopy[index] = {
      ...tableInputs[index],
      vessel1: selectedRow.vessel1,
      voy1: selectedRow.voy1,
      vessel2: selectedRow.vessel2,
      voy2: selectedRow.voy2,
      vessel3: selectedRow.vessel3,
      voy3: selectedRow.voy3,
      folderName: selectedRow.folderName,
    }
    setTableInputs(tableInputsCopy);

    if(editIndex[index] && (editIndex[index] === true || editIndex[index] === false)) {
      editIndex[index] = !editIndex[index];
    } else {
      editIndex[index] = true;
    }
    
    setEditIndex([...editIndex]);
    if(editIndex[index] === false){
      let finalData = copyObject(formField);
      finalData[index] = selectedRow;
      setFormField(finalData);
    }
  }
}

function classNameEdit(flg){
  return flg==="Y" || flg === null ? "nonEditable": "editable";
}
function editIconCell(row, rowId, colId){
  if (row === "N") {
    return <td id={`${colId}_${rowId}`} key={colId}><a className='editButton'><i className="btn-edit"></i></a></td>
   } else { return <td key={colId}></td>}
}
function editInputCell(row, rowId, colId, idx, mxLngt){
  if (editIndex[idx] === true ) {
    return <td id={`${colId}_${rowId}`} key={colId} className={colId==="folderName" ? 'mandatoryControl':''}>
      <input 
        type="text" 
        name={`${colId}__${idx}`}
        className="form-control minWdt"
        value={tableInputs[idx][colId] || ''}
        maxLength={mxLngt} 
        onChange={(e) => inputBoxChange(e, colId + "__" + idx)} 
      />
    </td>
  } else{
    return <td id={`${colId}_${rowId}`} key={colId}>{row}</td>
  }
}

  return (
    <>
  <div className="row g-0 filter-with-orderType-invoice">
    <div className="col-6 offset-6">
        <div className="d-flex justify-content-end">
            <button className="button-filter" onClick={()=>setHideFilter(!hideFilter)}><i className="filter-button"></i><span>Filters</span></button>
        </div>
    </div>
</div>
<div className="row g-0 pb-10">
  <div className="grid-table">
    <div className={`${freezetable}`}>
      <div className="col">
      <div className={`table-responsive ${calculatedRows.length === 0 ? "": "tableinvoice-height"}`}>
        <table className="table tpexTable">
          <thead className="text-nowrap">
            <tr key="grid-sort-asc-dsc">
              {columns.map((column) => {

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

                  <th key={column.id} className={column.id}>
                    <span>{column.name}</span>
                    <span onClick={() => handleSort(column.id, column.date)}>{sortIcon()}</span>
                  </th>
                )
              })}
            </tr>
            <tr className={hideFilter ? "d-none": ""}>
              {selectAll === true &&
                <th>
                  <Checkbox
                    type="checkbox"
                    name="selectAll"
                    id="selectAll"
                    handleClick={handleSelectAll}
                    isChecked={isCheckAll}
                  />
                </th>
              }



              {simpletable === false && columns.map(column => {
                return (
                  <th key={`${column.id}-filter`} className={`${column.id} pt-0`}>
                    <input
                      key={`${column.id}-search`}
                      type="text"
                      className="form-control"
                      placeholder={column.name}
                      value={filters[column.id] || ''}
                      onChange={event => handleSearch(event.target.value, column.id)}
                    />
                  </th>
                )
              })}
            </tr>
          </thead>
          <tbody>



            {
              calculatedRows.length === 0 && <tr><td  className="noData" colSpan={columns.length + 1}>No data found</td></tr>
            }

            {calculatedRows.map((row, index) => {

              return (


                <tr className={classNameEdit(row.invGenFlag)}  onClick={(e) => editRow(e, index, row)} key={row[idName]} id={`tblRow_` + index} >

                  {selectAll === true &&
                    <td><Checkbox
                      key={row[idName]}
                      type="checkbox"
                      name={row[idName]}
                      id={row[idName]}
                      handleClick={handleClick}
                      isChecked={isCheck.includes(row[idName])}
                    />
                    </td>
                  }

                  {columns.map((column) => {

                    if (column.invGenFlag === true) {
                      return editIconCell(row[column.id], row[idName], column.id);
                    }
                    else if (column.input === true) {
                      return editInputCell(row[column.id], row[idName], column.id, index, column.maxLength);
                    }
                    else if (column.select === true) {
                      if (editIndex[index] === true ) {
                        return <td id={`${column.id}_${row[idName]}`} key={column.id}>
                          <TpexSelect 
                            moduleName={`${column.id}__${index}`}
                            options={column.selectList} 
                            margin="minWdt" 
                            blankRequired={false} 
                            selected={tableSelects[index][column.id]} 
                            onChangeSelection={(e) => selectBoxChange(e, column.id + "__" + index)} 
                            hasValue={tableSelects[index][column.id]} 
                          />
                        </td>
                      }
                        return <td id={`${column.id}_${row[idName]}`} key={column.id}>{row[column.id]}</td>
                    }
                    else if (column.date === true) {
                      if (editIndex[index] === true ) {
                        return <td id={`${column.id}_${row[idName]}`} key={column.id}>
                          <TpexDatePicker
                            name={`${column.id}__${index}`} 
                            dateSelected={tableDates[index][column.id] ? new Date(tableDates[index][column.id]) : ''}
                            handleDateSelected={(e) => dateBoxChange(e, column.id + "__" + index)}
                            isDirectDatePicker={true}
                          />                            
                        </td>
                      }
                        return <td id={`${column.id}_${row[idName]}`} key={column.id}>{row[column.id]}</td>
                    }
                    return <td id={`${column.id}_${row[idName]}`} key={column.id}>{row[column.id]}</td>
                  })}
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
      </div>
      <div className="row g-0">
      {simpletable === false && count > 0 ? (
        <>
        <div className="col-6 paginationText mt-10 pb-10">
          Page {currentServerPage+1} of {totalItemsServer}
        </div>
        {totalItemsServer > 1 && <div className="col-6">
        <ReactPaginate
        breakLabel="..."
        nextLabel={<i className="pagination-right"></i>}
        onPageChange={e=>handlePageClick(e)}
        pageRangeDisplayed={3}
        marginPagesDisplayed={3}
        pageCount={totalItemsServer}
        previousLabel={<i className="pagination-left"></i>}
        previousClassName={'page-item'}
        previousLinkClassName={'page-link'}
        nextClassName={'page-item'}
        nextLinkClassName={'page-link'}
        renderOnZeroPageCount={null}
        containerClassName={'pagination pagination-server justify-content-end'}
        pageClassName={'page-item'}
        pageLinkClassName={'page-link'}
        activeClassName={'active'}
        forcePage={currentServerPage} 
      />
      </div>
      }
      </>
      ) : (
        ''
      )}
    </div>
  </div>
  </div>
  </div>
    </>
  )
  }