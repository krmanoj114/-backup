const TableFilter = ({
    toggleFilter,
    filter,
    columns
}) => {
    return (
        <>
            {
                columns &&
                columns.length > 0 &&
                <div>
                    {
                        filter &&
                        <div>
                            <div className="col">
                                <div className="d-flex justify-content-end">
                                    <button
                                        className="button-filter"
                                        onClick={toggleFilter}
                                    >
                                        <i className="filter-button"></i>
                                        <span>Filters</span>
                                    </button>
                                </div>
                            </div>
                        </div>
                    }
                </div>
            }
        </>
    );
};

export default TableFilter;