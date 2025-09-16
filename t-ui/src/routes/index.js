import React, { lazy } from 'react';
import { Route } from "react-router-dom";
import Home from '../components/home/Home';
import InvoiceGenWorkPlanMaster from '../components/invoice-gen-work-plan-master/invoice-gen-work-plan-master';
import OnDemandDownload from '../components/ondemanddownload/ondemanddownload';
import { Codemaster } from '../components/codemaster';
import UploadDownload from '../components/uploaddownload/uploaddownload';
import { InvoiceMaintenance } from '../components/invoice-maintenance/invoice-maintenance';
import InvoiceShippingReports from '../components/reports/invoice-shipping';
import { ShippingContainerResult } from '../components/invoices/shipping-container-result/shipping-container-result';
import LotPriceMaster from '../components/lot-price-master/lot-price-master';
import HaisenDetails from '../components/haisen-details-retrieval/HaisenDetails';
import { InquiryProcessStatus } from '../components/inquiry-for-process-status/inquiry-for-process-status';
import { ShippingControlMaster } from '../components/shipping-control-master/shipping-control-master';
import { ReportViewer } from '../components/reports/report-view/report-viewer';
import VesselBookingMaster from '../components/VesselBookingMaster/vesselBookingMaster';
import { MixPrivilegeMaster } from '../components/mix-privilege-master/mix-privilege-master';
import PxpPartPriceMaintenance from '../components/pxp-part-price-maintenance/pxp-part-price-maintenance';
import RenbaneGroupCodeMaster from '../components/renbane-group-code-master/renbane-group-code-master';
import {ReturnablePackingMaster} from '../components/returnable-packing-master/returnable-packing-master';
import { InvoiceSetupMaster } from '../components/invoice-setup-master/invoice-setup-master';
import { EnginePartMaster } from '../components/engine-part-master/engine-part-master';
import PartMasterInquiry from '../components/part-master-inquiry';
import { InvoiceRecalculation } from '../components/invoice-recalculation';
import  ISOContainer  from '../components/iso-container';
import ManualInvoiceGeneration from '../components/manual-invoice-generation/manual-invoice-generation';
import PackingVanningInstructionReport from '../components/Packing-Vanning-Instruction-Report'

// Pages
const NotFound = lazy(() => import('../common/components/NotFound'));

const AppRouter = menus => {

    const ROUTE_BASED_COMPONENT = {
        A101: <Codemaster />,
        A102: <ShippingControlMaster/>,
        A103: <RenbaneGroupCodeMaster/>,
        A104: <ReturnablePackingMaster/>,
        A105: <InvoiceGenWorkPlanMaster/>,
        A106: <LotPriceMaster/>,
        A107: <VesselBookingMaster/>,
        A108: <MixPrivilegeMaster/>,
        A109: <InvoiceSetupMaster/>,
        A110: <EnginePartMaster/>,
        A111: <ISOContainer/>,

        A201: <InvoiceMaintenance />,
        A202: <InvoiceShippingReports />,
        A203: <InvoiceRecalculation />,
        A204: <ManualInvoiceGeneration />,

        A301: <ReportViewer/>,
        A302: <OnDemandDownload />,
        A303: <UploadDownload/>,
        A304: <ShippingContainerResult/>,
        A305: <InquiryProcessStatus/>,
        A306: <HaisenDetails/>,
        A307: <PxpPartPriceMaintenance/>,
        A308: <PartMasterInquiry />,
        A309: <PackingVanningInstructionReport />
    };
    
    let actualMenusWithRoute = [];

    menus.forEach(eachMenuObj => {

        const {subMenu} = eachMenuObj;

        if (subMenu) {
            actualMenusWithRoute = [
                ...actualMenusWithRoute,
                ...subMenu
            ];
        }
    });

    return (
        <Route>
            <Route path="/home" element={<Home />} />
            
            {
                actualMenusWithRoute.map(({routeLink, id}) => {
                    if (routeLink && id) {
                        return (
                            <Route
                                path={routeLink}
                                element={ROUTE_BASED_COMPONENT[id]}
                                key={id}
                            />
                        )
                    }
                    return null;
                })
            }

            <Route
                path="*"
                element={<NotFound />}
            />
        </Route>
    );
}

export default AppRouter;