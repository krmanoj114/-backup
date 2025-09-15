import './report-viewer.css';
import { useSearchParams } from "react-router-dom";
import { LABEL_CONST } from "../../../constants/label.constant.en";
import TpexSimpleButton from "../../../common/components/button";
import { useEffect, useState } from "react";
import { MIME_TYPE, MODULE_CONST } from "../../../constants/constant";
import { getFileRequest, postFileRequest } from "../../../services/axios-client";
import { TpexLoader } from "../../../common/components/loader/loader";
import AlertModal from "../../../common/components/alert-modal/alert-modal";

export function ReportViewer() {
  const [searchParams,] = useSearchParams();
  const [isPdf, setIsPdf] = useState(false);
  const [isXlsx, setIsXlsx] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [modalShowAlert, setModalShowAlert] = useState(false);
  const [messageType, setMessageType] = useState();
  const [messageText, setMessageText] = useState();

  const fileNameWithFullPath = searchParams.get("file");
  const orderType = searchParams.get("orderType");
  const invoiceNumber = searchParams.get("invoiceNumber");
  const reportId = searchParams.get("reportTypes");
  const etd = searchParams.get("etd");
  const etdTo = "";
  const destination = searchParams.get("destinations");
  const bookingNo = searchParams.get("bookingNo");
  const reportName = searchParams.get("reportName");

  useEffect(() => {
    const isPdfReport = searchParams.get("pdf");
    const isXlsxReport = searchParams.get("xlsx");
    setIsPdf(isPdfReport);
    setIsXlsx(isXlsxReport);
  }, []);

  const exportXLS = (e) => {
    setIsLoading(true);
    const apiUrl = MODULE_CONST.INVOICE_SHIPPING_REPORT.DOWNLOAD_PDF_XLS + `?userId=TestUser&orderType=${orderType}&invoiceNumber=${invoiceNumber}&reportId=${reportId}&etd=${etd}&etdTo=${etdTo}&destination=${destination}&reportFormat=xlsx&bookingNo=${bookingNo}`
    downloadXportFile(apiUrl);
  };

  function downloadXportFile(apiUrl) {
    const link = document.createElement("a");
    link.target = "_blank";
    link.download = reportName.split(".")[0] + ".xlsx";
    getFileRequest(MODULE_CONST.INVOICE_SHIPPING_REPORT.API_BASE_URL, apiUrl).then((res) => {
      link.href = URL.createObjectURL(
        new Blob([res.data], { type: MIME_TYPE["xlsx"] })
      );
      link.click();
      setIsLoading(false);
    }).catch(function (error) {
      setIsLoading(false);
      console.log('downloadXportFile error =>', error.message);
      openAlertBox(LABEL_CONST.ERROR, error.message);
    });
  }

  function downloadPdf() {
    setIsLoading(true);
    const uri = MODULE_CONST.INVOICE_SHIPPING_REPORT.API_BASE_URL_REPORT;    
    const uriPath = MODULE_CONST.INVOICE_SHIPPING_REPORT.REPORT_PATH;

    const payload = {
      orderType: orderType,
      invoiceNumber: invoiceNumber,
      reportTypes: [reportId],
      bookingNo: bookingNo,
      etd: etd,
      etdTo: etdTo,
      destinations: destination ? [destination] : [],
      userId: "TestUser",
    }

    postFileRequest(uri, uriPath, payload).then((res) => {
      let link = document.createElement("a");
      link.href = URL.createObjectURL(
        new Blob([res.data], { type: MIME_TYPE["pdf"] })
      );
      link.download = res.headers.filename;
      link.click();
    }).catch(function (error) {
      console.log('downloadPdf error =>', error);
    }).finally(() => {
      setIsLoading(false);
    });
  }

  const handleAlertConfirmation = () => {
    console.log('in handleAlertConfirmation')
  };

  function openAlertBox(messegeType = LABEL_CONST.WARNING, messageText = "") {
    setMessageType(messegeType);
    setMessageText(messageText);
    setModalShowAlert(true)
  };

  return (
    <>
      <TpexLoader isLoading={isLoading} />   

      {isXlsx === true || isXlsx == "true" ?
        <div className="row text-end">
          <div className="col-3 text-center mt-3 report-name-pdf">
            {reportName}
          </div>
          <div className="col-8 mb-1">
            <TpexSimpleButton color="primary" text={LABEL_CONST.DOWNLOAD_PDF} handleClick={event => downloadPdf()} topmargin="2" />
            <TpexSimpleButton color="primary" text={LABEL_CONST.EXPORT_XLS} handleClick={event => exportXLS(event)} topmargin="2" leftmargin="2" />
          </div>
        </div> : ''
      }
      {isPdf === true || isPdf == "true" ?
        <div className={`embed-responsive report-pdf-embed`} style={{ height: "100vh" }}>
          <embed
            src={fileNameWithFullPath}
            type="application/pdf"
            width="100%"
            height="100%"
          />
        </div> : <div className="row">
          <div className="col-5"></div>
          <div className="col-3">{reportId} - {LABEL_CONST.NO_PDF_REPORT}</div>
          <div className="col-4"></div>
        </div>
      }
      {/* alert modal  */}
      <AlertModal
        handleClick={handleAlertConfirmation}
        show={modalShowAlert}
        onHide={() => setModalShowAlert(false)}
        status={messageType}
        content={messageText}
      />
    </>
  )
}