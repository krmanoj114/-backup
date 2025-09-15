import React from 'react';

export function TpexBreadCrumb({ name = "" }) {
    const today = new Date();
    const yyyy = today.getFullYear();
    let mm = today.getMonth() + 1; // Months start at 0!
    let dd = today.getDate();

    if (dd < 10) dd = '0' + dd;
    if (mm < 10) mm = '0' + mm;

    const formattedToday = dd + '/' + mm + '/' + yyyy;
     
    return (
        <>
            {/* Breadcrumb starts*/}
            <div className="row">
                <div className="col-6">
                    <nav aria-label="breadcrumb">
                        <ol className="breadcrumb">
                            <li className="breadcrumb-item"><a href="/">Home</a></li>
                            <li className="breadcrumb-item active" aria-current="page">{name}</li>
                        </ol>
                    </nav>
                </div>

                {/* Breadcrumb ends*/}
                {/* System time starts */}
                <div className="col-6">
                    <div className="d-flex justify-content-end">
                        <div className="currentTime">
                            <i className="timeCalendar"></i>
                            <span>:</span>
                            <span className="ms-1">{formattedToday}</span>
                            <span className="ms-1">{today.toLocaleTimeString()}</span>
                        </div>
                    </div>
                </div>
                {/* System time ends */}
            </div>
        </>
    )
}