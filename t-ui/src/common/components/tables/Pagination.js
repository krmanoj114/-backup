export const Pagination = ({ activePage, count, rowsPerPage, totalPages, setActivePage, pagination }) => {

    return (
        <>
            {pagination === true && count > 0 ? (
                <div className="row g-0">
                    <div className="col-9 paginationText">
                        Page {activePage} of {totalPages}
                    </div>
                    <div className="col-3">
                        <nav aria-label="Pagination">
                            <ul className="pagination justify-content-end" id="pagination-id">
                                <li className={"page-item " + (activePage === 1 ? ' disabled click-disabled' : '')} onClick={() => setActivePage(1)}>
                                    <span className="page-link" >First</span>
                                </li>
                                <li className={"page-item " + (activePage === 1 ? ' disabled click-disabled' : '')} onClick={() => setActivePage(activePage - 1)}>
                                    <span className="page-link" aria-label="Previous">
                                        <span aria-hidden="true"><i className="pagination-left"></i></span>
                                    </span>
                                </li>
                                <li className={"page-item " + (activePage === totalPages ? ' disabled click-disabled' : '')} onClick={() => setActivePage(activePage + 1)}>
                                    <span className="page-link" aria-label="Next">
                                        <span aria-hidden="true"><i className="pagination-right"></i></span>
                                    </span>
                                </li>
                                <li className={"page-item " + (activePage === totalPages ? ' disabled click-disabled' : '')} onClick={() => setActivePage(totalPages)}>
                                    <span className="page-link" >Last</span>
                                </li>
                            </ul>
                        </nav>

                    </div>
                </div>
            ) : (
                ''
            )}
        </>
    )
}
