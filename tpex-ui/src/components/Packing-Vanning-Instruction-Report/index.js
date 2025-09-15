import React, { useState, useEffect } from 'react';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import ReportTypeList from './report-type';
import Reportform from './report-form';
import './style.css';

const PackingVanningInstructionReport = () => {
    const [customFlag, setCustomFlag] = useState('');
    const [customLabelValue, setCustomLabelValue] = useState('');
    const [reportType, setReportType] = useState("RDLY019")
    

    const reportChange = (e)=>{
        setReportType(e.target.value)
    }

    useEffect(() => {

    }, [reportType])

    useEffect(() => {

    }, [customFlag])

    return (
        <>
            <main id="main">
                <div className="container-fluid container-padding packing-vanning-ins">
                    <TpexBreadCrumb name="Packing and Vanning Instruction" />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading">
                                    <i className="bg-border"></i>
                                    <h1>Packing and Vanning Instruction</h1>
                                </div>
                            </div>
                            <>
                                <ReportTypeList
                                    reportType={reportType}
                                    setReportType={setReportType}
                                    customLabelValue={customLabelValue}
                                    setCustomLabelValue={setCustomLabelValue}
                                    setCustomFlag={setCustomFlag}
                                    reportChange={reportChange}
                                />

        
                                <Reportform
                                    reportType={reportType}
                                    customFlag={customFlag}
                                />
                            </>
                        </div>
                    </div>
                </div>
            </main>
        </>
    );
};

export default PackingVanningInstructionReport;