import { useState, useMemo, useEffect, useContext } from 'react'
import "../../styles/table.css";
import { sortRows, filterRows, paginateRows } from '../../common/components/tables/helpers';
import { Pagination } from '../../common/components/tables/Pagination'
import "react-datepicker/dist/react-datepicker.css";
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexSelect } from '../../common/components/select';
import TableFilter from './TableFilter';
import EditDataContext from './EditDataContext';


const isObjEmpty = obj => Object.keys(obj).length === 0;

const findObjByKeyIndex = (keyIndex, collectionObj) => {

  const obj = collectionObj.find(_obj => _obj[keyIndex] ? _obj : undefined);

  return obj ? obj[keyIndex] : {};
};

const InputField = ({
  column,
  index,
  row,
  idName
}) => {
  const editDataContextRef = useContext(EditDataContext);
  const {id} = column;
  const [inputVal, setInputVal] = useState(row[id]);
  return (
    <td
      test-id={`td-${id}`}
      key={`td-${id}${index}`}
    >
      <input
        key={`txt-${id}${index}`}
        id={id}
        name={id}
        type="text"
        className={`form-control`}
        value={inputVal}
        onChange={evt => {
          const inputValue = evt.target.value;
          setInputVal(inputValue);
          const objToEdit = {};
          objToEdit[index] = row;
          objToEdit[index][id] = inputValue;
          
          let getExistingRecord = findObjByKeyIndex(index, editDataContextRef.current);
          
          if (isObjEmpty(getExistingRecord)) {
            editDataContextRef.current.push(objToEdit);
          }
        }}
      />
    </td>
  );
};

const SelectField = ({
  column,
  index,
  row,
  idName
}) => {
  const getBrokerName = (optionId, options) => {

    let selectedOptionName;
    
    if (optionId !== '') {
      const selectedOption = options.find(option => option["id"] === optionId);
      return selectedOption?.name ? selectedOption.name : optionId;
    }

    return selectedOptionName ? selectedOptionName : optionId;
  };

  const editDataContextRef = useContext(EditDataContext);
  const {id, customBrokerSelectList} = column;
  const [selectedVal, setSelectedVal] = useState(row['customBrokerCode']);

  return (
    <td
      test-id={`td-${id}`}
      key={`td-${id}${index}`}
    >
      <TpexSelect
        key={`${id}-${index}`}
        blankRequired={false}
        hasValue={selectedVal}
        id="id"
        margin="minWdt"
        moduleName={`${id}__${index}`}
        onChangeSelection={evt => {

          const selectedValue = evt.target.value;
          setSelectedVal(selectedValue);
          const objToEdit = {};
          objToEdit[index] = row;
          
          if (id === 'customBrokerName') {
            objToEdit[index]['customBrokerCode'] = selectedValue;
            objToEdit[index][id] = getBrokerName(selectedValue, customBrokerSelectList);
          } else {
            objToEdit[index][id] = selectedValue;
          }
          
          if (editDataContextRef.current[index]) {
            editDataContextRef.current[index] = objToEdit;
          } else {
            editDataContextRef.current.push(objToEdit);
          }
        }}
        options={customBrokerSelectList} 
        selected={selectedVal}
      />
    </td>
  );
};

const DefaultEditTd = ({column, row, index}) =>{
  const {id, type} = column;
  
  return(
    <td
      id={`${id}`}
      key={`${id}-${index}`}
      className={`${row[id]}-status ${type === "number" ? ' text-end' : ''}`}
    >
      <span>{row[id] || ''}</span>
    </td>
  );
};

const TpexTable = ({
  columns = [],
  rows = [],
  containerColumns=[],
  selectRow = false,
  idName = "id",
  rowPerPage = 10,
  pagination = false,
  isCrud = false,
  filter = true,
  serverSideFilter = false,
  editTable = false,
  customEdit = false,
  defaultSortingId = null,
  defaultSortingOrder = "asc"
}) => {

  const editDataContextRef = useContext(EditDataContext);
  const [activePage, setActivePage] = useState(1)
  const [filters, setFilters] = useState({})
  const [sort, setSort] = useState({ order: defaultSortingOrder, orderBy: defaultSortingId ? defaultSortingId : null })
  const [isLoading, setIsLoading] = useState(true);
  const [filterToggle, setFilterToggle] = useState(false);
  const [currentEditRow, setCurrentEditRow] = useState({});

  const rowsPerPage = rowPerPage;
  const filteredRows = useMemo(() => filterRows(rows, filters), [rows, filters])
  const sortedRows = sortRows(filteredRows, sort)
  const calculatedRows = paginateRows(sortedRows, activePage, rowsPerPage)
  const count = filteredRows.length
  const totalPages = Math.ceil(count / rowsPerPage);

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
      order: defaultSortingOrder, orderBy: defaultSortingId
        ? defaultSortingId : null
    })
    setActivePage(1)
    setFilters({})
  }

  const handleSort = (id) => {
    setActivePage(1)
    setSort((prevSort) => ({
      order: prevSort.order === 'asc' && prevSort.orderBy === id ? 'desc' : 'asc',
      orderBy: id,
    }))
  }

  useEffect(() => {
    setTimeout(function () {
      setIsLoading(false);
    }, 2000);
  }, []);
  
  useEffect(() => {
    clearAll();
  }, [rows]);

  const changeToEdit = (evt, rowToUpdate, index) => {
    const {type, target} = evt;
    if (
      type === "click" &&
      target.tagName === "TD" &&
      rowToUpdate.allowedToEdit
    ) {
      if (rowToUpdate.editable) {
        setCurrentEditRow({});
        editDataContextRef.current = [];
        rowToUpdate.editable = false;
      } else {
        rowToUpdate.editable = true;
        setCurrentEditRow(rowToUpdate);
        if (editDataContextRef.current[index]) {
          editDataContextRef.current[index] = rowToUpdate;
        }
      }
    }
  };

  const toggleFilter = () => {
    setFilterToggle(!filterToggle);
  };

  return (
    <>
      <TableFilter
        toggleFilter={toggleFilter}
        filter={filter}
        columns={columns}
      />

      <div className="grid-table">
        <div className="row g-0">
          <div>
            <div className="col">
              <div className="table-responsive tpexTableVMS-height">
                <table className={`table tpexTable tpexTableVMS tableWithChecbox ${editTable ? "tpexTableEdit" : ""}`}>
                  <thead className="text-nowrap">
                    
                    {
                      columns && 
                      columns.length > 0 && (
                        <tr key="grid-sort-asc-dsc">
                          {
                            columns.map((column) => {
                              if(column.group === true) {
                                return(
                                <th key={column.id} className="colSpan-th">
                                  <span>{column.name}</span>
                                </th>
                                )
                              }
                            })
                          }
                        </tr>
                      )
                    }
                    
                    {
                      containerColumns && 
                      containerColumns.length > 0 && 
                      <tr key="grid-sort-asc-dsc-ord">
                        {
                         containerColumns.map((c) => {
                          const sortIcon = () => {
                            if (c.id === sort.orderBy) {
                              if (sort.order === 'asc') {
                                return <i className="arrow-up"></i>
                              }
                              return <i className="arrow-down"></i>
                            } else {
                              return <i className="arrow-down"></i>
                            }
                          }
                            return(
                              <th key={c.id}>
                                <span>{c.name}</span>
                                <span onClick={() => handleSort(c.id)}>{sortIcon()}</span>
                              </th>
                            )
                         })
                        }
                      </tr>
                    }

                    {
                      filterToggle &&
                      <tr>
                        {
                          filterToggle &&
                          serverSideFilter === false &&
                          containerColumns.map(column => {
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
                          })
                        }
                      </tr>
                    }
                    
                  </thead>
                  
                  <tbody>

                    {
                      calculatedRows.map((row) => {
                        
                        const index = row["id"];

                        const getClassNames = row => {
                          let classNames = '';

                          classNames += (row.update === true) ? `red-row` : ``;
                          classNames += (row.etdPassMonth) ? ' nonEditable' : ' editable';

                          return classNames;
                        };
                        return (
                          <tr
                            key={row[idName]}
                            className={getClassNames(row)}
                            onClick={evt => changeToEdit(evt, row, index)}
                          >
                            {
                              containerColumns.map(column => {
                                const {id, type} = column;
                                if (row.editable) {
                                    switch(type) {
                                      case "input":
                                        return(
                                            <InputField
                                              row={currentEditRow}
                                              idName={idName} 
                                              column={column} 
                                              index={index}
                                              key={id} 
                                            />
                                        );
                                      case "select":
                                        return(
                                            <SelectField 
                                              row={currentEditRow}
                                              idName={idName}  
                                              column={column} 
                                              index={index}
                                              key={id}
                                            />
                                        );
                                        default:
                                          return(
                                              <DefaultEditTd
                                                column={column}
                                                row={row}
                                                index={index}
                                                key={id} 
                                              />
                                        )
                                    }
                                } else {
                                  return (
                                    <td
                                      id={`${id}-${index}`}
                                      key={`${id}`}
                                      className={`${row[id]}-status ${type === "number" ? ' text-end' : ''}`}
                                    >
                                      <span>{row[id]}</span>
                                    </td>
                                  );
                                }
                              })
                            }
                          </tr>
                        )
                      }
                    )}

                    {/* no data in table  */}
                    {!rows.length && !isLoading ?
                      <tr>
                        <td colSpan={columns.length + (selectRow ? 1 : 0)} className="noData">{LABEL_CONST.NO_DATA_FOUND}</td>
                      </tr>
                      : ""
                    }

                  </tbody>
                </table>
              </div>
              
              {
                (pagination === true && count > 0) ? (
                  <Pagination
                    activePage={activePage}
                    count={count}
                    rowsPerPage={rowsPerPage}
                    totalPages={totalPages}
                    pagination={pagination}
                    setActivePage={setActivePage}
                  />
                ) : ('')
              }
            </div>
          </div>
        </div>
      </div>
    </>
  )
};

export default TpexTable;