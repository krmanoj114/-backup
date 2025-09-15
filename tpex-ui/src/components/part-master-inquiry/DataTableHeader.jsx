const DataTableHeader = ({ selectRow, columns, sort, handleSort, filterToggle, serverSideFilter, filters, handleSearch }) => {  
return (
    <>
      <thead className="text-nowrap">
        {columns.length ?
          <tr key="grid-sort-asc-dsc">
            {selectRow === true ?
              <th className="checkboxCol"></th>
              : ''
            }
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

                <th key={column.id}>
                  <span>{column.name}</span>
                  <span onClick={() => handleSort(column.id, column.date)}>{sortIcon()}</span>
                </th>
              )
            })}
          </tr>
          : ''
        }
        {filterToggle ?
          <tr>
            {selectRow ?
              <th className="checkboxCol"></th> : ''
            }
            {filterToggle && serverSideFilter === false && columns.map(column => {
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
          : ''
        }
      </thead>
    </>
    )

}

export default DataTableHeader;