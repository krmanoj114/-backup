import React from 'react'

const DataTableFilter = ({toggleFilter, columns}) => {
  const filter = true;
  return (
    <div className="row g-0">
        {filter && columns.length ?
            <>
                {/* filter option  */}
                {filter ?
                    <div className="col">
                    <div className="d-flex justify-content-end mt-2">
                        <button className="button-filter" onClick={toggleFilter}><i className="filter-button"></i><span>Filters</span></button>
                    </div>
                    </div> : ''
                }
            </>
            : ''
            }
    </div>
  )
}

export default DataTableFilter;
