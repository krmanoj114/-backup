import { TpexSelect } from '../../common/components/select';
import React, { useState, useEffect } from 'react';
import './ondemand.css';
import { getRequest, postRequest } from '../../services/axios-client';
import { createMessage, formatedDate } from '../../helpers/util';
import { DEFAULT_GRID_RECORD, MODULE_CONST } from '../../constants/constant';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexTable } from '../../common/components/tables/TpexTable';
import TpexSimpleButton from '../../common/components/button';
import { useLocation } from 'react-router-dom';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { TpexLoader } from '../../common/components/loader/loader';
import { TpexDatePicker } from '../../common/components/datepicker/datepicker';

const OnDemandDownload = () => {
    const location = useLocation();
    const defaultDate = {
        reportName: location?.state && location.state?.reportName ? location.state.reportName : '',
        status: '',
        createDate: formatedDate(new Date()),
        createBy: MODULE_CONST.ON_DEMAND_DOWNLOAD.DEFAULT_USER
    };
    const [rows, setRows] = useState([]);
    const [reportNames, setReportNames] = useState([]);
    const [reportStatus, setReportStatus] = useState([]);
    const [reportNameSelected, setReportNameSelected] = useState('');
    const [statusSelected, setStatusSelected] = useState('');
    const [dateSelected, setDateSelected] = useState(new Date());
    const [request, setRequest] = useState(defaultDate);
    const [modalShowAlert, setModalShowAlert] = useState(false);
    const [messageType, setMessageType] = useState();
    const [messageText, setMessageText] = useState();
    const [isLoading, setIsLoading] = useState(true);

    const openAlertBox = (messegeType, messageText = "") => {
        setMessageType(messegeType);
        setMessageText(messageText);
        setModalShowAlert(true)
    };

    const getDownloadList = async () => {
        setIsLoading(true);
        postRequest(MODULE_CONST.ON_DEMAND_DOWNLOAD.API_BASE_URL, MODULE_CONST.ON_DEMAND_DOWNLOAD.GRID_API, request).then(dataRes => {
            setRows([...dataRes.data]);
            setIsLoading(false);

        }).catch(function (error) {
            setIsLoading(false);
            console.log('getDownloadList Error', error);
            openAlertBox(LABEL_CONST.ERROR, error.message);
        });
    };

    const getReportNamesAndStatus = async () => {
        setIsLoading(true);
        getRequest(MODULE_CONST.ON_DEMAND_DOWNLOAD.API_BASE_URL, MODULE_CONST.ON_DEMAND_DOWNLOAD.REPORT_STATUS).then(dataRes => {
            const reportsforSelect = dataRes.data.reportNames.map(report => {
                return {
                    "id": report,
                    "name": report,
                }
            });
            const statusforSelect = dataRes.data.status.map(status => {
                return {
                    "id": status,
                    "name": status,
                }
            });
            setReportNames(reportsforSelect);
            setReportStatus(statusforSelect);
            setIsLoading(false);

        }).catch(function (error) {
            setIsLoading(false);
            console.log('getReportNamesAndStatus Error', error);
            openAlertBox(LABEL_CONST.ERROR, error.message);
        });
    };

    const handleClick = () => {
        const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
        if (!dateSelected || (dateSelected && !formatedDate(dateSelected).match(dateReg))) {
            openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        } else {
            getDownloadList();
        }
    };

    const onChangeSelectionManual = reportName => {
        setReportNameSelected(reportName);
        setRequest({
            ...request,
            reportName: reportName
        });
    };

    const onChangeSelection = event => {
        const ddName = event.target.name;
        const ddValue = event.target.value;
        if (ddName === 'ondemand_report_name') {
            setReportNameSelected(ddValue);
            setRequest({
                ...request,
                reportName: ddValue
            });
        }
        if (ddName === 'ondemand_report_status') {
            setStatusSelected(ddValue);
            setRequest({
                ...request,
                status: ddValue
            });
        }
    };

    const handleDateSelected = (d) => {
        setDateSelected(d);
    };

    const columns = [
        {
            id: "reportName",
            name: "Report Name",
            link: false
        },
        {
            id: "createDate",
            name: "Date Time",
            link: false
        },
        {
            id: "createBy",
            name: "User Id",
            link: false
        },
        {
            id: "status",
            name: "Status",
            link: false
        },
        {
            id: "downLoc",
            name: "Download",
            link: true
        }
    ];

    const handleAlertConfirmation = () => {
        console.log('in handleAlertConfirmation')
    };

    function addEditDataForParent(add, edit) {
        console.log(' ');
    }

    useEffect(() => {
        getReportNamesAndStatus();
        getDownloadList();
        // eslint-disable-next-line       
    }, []);

    useEffect(() => {
        //NOTE: createBy will be recieved from the login user. will be implemenet in future
        // createBy just for test, need to remove later
        const dateFormated = formatedDate(dateSelected);
        setRequest({
            reportName: reportNameSelected,
            status: statusSelected,
            createDate: dateFormated,
            createBy: MODULE_CONST.ON_DEMAND_DOWNLOAD.DEFAULT_USER
        })
        // eslint-disable-next-line       
    }, [reportNameSelected, statusSelected, dateSelected]);

    useEffect(() => {
        if (location?.state && location.state?.reportName) {
            onChangeSelectionManual(location.state.reportName);
        }
        // eslint-disable-next-line
    }, [location.state]);

    return (
        <>
            {/* spinner */}
            <TpexLoader isLoading={isLoading} />

            <main id="main">
                <div className="container-fluid container-padding">

                    <TpexBreadCrumb name={LABEL_CONST.ON_DEMAND_DOWNLOAD} />

                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.ON_DEMAND_DOWNLOAD}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 pb-10">
                                    <div className="form-group col-2">
                                        <div className="customDatePicker mandatoryControl">
                                            <label htmlFor="requestdate">{LABEL_CONST.REQUEST_DATE}</label>
                                            <TpexDatePicker
                                                id="requestdate"
                                                className={`form-control ${!dateSelected ? "red-border" : ''}`}
                                                dateSelected={dateSelected}
                                                handleDateSelected={(date) => handleDateSelected(date)}
                                                isDirectDatePicker={true}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-2">
                                        <div className="customSelect">
                                            <label htmlFor="requestdate">{LABEL_CONST.REPORT_STATUS}</label>
                                            <TpexSelect onChangeSelection={onChangeSelection} selected="ALL" moduleName="ondemand_report_status" hasValue={statusSelected} options={reportStatus} />
                                        </div>
                                    </div>
                                    <div className="col-2">
                                        <div className="customSelect">
                                            <label htmlFor="requestdate">{LABEL_CONST.REPORT_NAME}</label>
                                            <TpexSelect onChangeSelection={onChangeSelection} selected="ALL" moduleName="ondemand_report_name" hasValue={reportNameSelected} options={reportNames} />
                                        </div>
                                    </div>
                                    <div className="col-6 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.SEARCH}
                                                handleClick={handleClick}
                                                topmargin="4"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div className="panelBox mt-10">
                        <div className="grid-panel mt-0">
                            <TpexTable
                                rows={rows}
                                idName="reportId"
                                moduleName="ON_DEMAND_DOWNLOAD"
                                rowPerPage={DEFAULT_GRID_RECORD}
                                selectAll={false}
                                selectRow={false}
                                columns={columns}
                                isCrud={false}
                                pagination={true}
                                filter={true}
                                serverSideFilter={false}
                                editTable={false}
                                addEditDataForParent={addEditDataForParent}
                            />
                        </div>
                    </div>
                </div>
                <AlertModal
                    handleClick={handleAlertConfirmation}
                    show={modalShowAlert}
                    onHide={() => setModalShowAlert(false)}
                    status={messageType}
                    content={messageText} />
            </main>

        </>
    );
};

export default OnDemandDownload;