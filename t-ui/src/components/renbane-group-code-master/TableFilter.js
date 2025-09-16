export function TableFilter({ toggleFilter, filter, columns }) {
    return (
        <>
            {filter && columns.length ?
                <>
                    {filter ?
                            <div className="col ">
                                <div className="d-flex justify-content-end mt-2">
                                    <button className="button-filter" onClick={toggleFilter}><i className="filter-button"></i><span>Filters</span></button>
                                </div>
                            </div> : ''
                    }
                </> : ''
            }
        </>
    )

}