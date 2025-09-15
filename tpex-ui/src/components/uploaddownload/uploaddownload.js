import React, { useState, useCallback, useEffect } from "react";
import "../uploaddownload/upload.css";
import { MODULE_CONST } from "../../constants/constant";
import { TpexBreadCrumb } from "../../common/components/breadcrumb/breadcrumb";
import { TpexLoader } from "../../common/components/loader/loader";
import DownloadReport from "../../components/uploaddownload/DownloadReport";
import UploadReport from "../../components/uploaddownload/UploadReport";
import { postRequest } from "../../services/axios-client";

function UploadDownload() {
  const userId = "TestUser";
  const [reports, setReports] = useState([]);
  const [downloadReports, setDownloadReports] = useState([]);
  const [processTable, setProcessTable] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    setTimeout(() => {
      setIsLoading(false);
    }, 2000);
  }, []);

  const fetchReports = useCallback(async () => {
    setIsLoading(true);
    postRequest(
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.API_BASE_URL,
      MODULE_CONST.COMMON_DOWNLOAD_UPLOAD.FETCH_REPORT_API,
      {
        userId: userId,
        jsonFileName: "CommonUploadAndDownloadReportNames",
      }
    )
      .then((response) => {       
        let newData = [...response.data.listofProcessCtrl];
        let addedIdData = newData.map((listofProcessCtrl, index) => ({
          ...listofProcessCtrl,
          processId: listofProcessCtrl.processId + index,
        }));
        setProcessTable(addedIdData);      
        setReports(response.data.reportNames.reportNames);
        let downloadDropdownOptions =
          response.data.reportNames.reportNames.filter(
            (obj) => obj.reportId !== 3
          );
        setDownloadReports(downloadDropdownOptions);
        setIsLoading(false);
      })
      .catch((e) => {
        setIsLoading(false);
        console.log(e.message);
      });
  }, []);

  useEffect(() => {
    fetchReports();
  }, [fetchReports]);

  const checkReportName = (name) => name.includes('PxP') === false ? name.replace(/([a-z](?=[A-Z]))/g, "$1 ") : name;
  
  let options = reports.map((option) => {
    let { reportId: id, reportName: name } = option;
    return {
      id: id.toString(),
      name: checkReportName(name),
    };
  });

  let optionsDownload = reports
    .map((option) => {
      let { reportId: id, reportName: name } = option;
      return {
        id: id.toString(),
        name: checkReportName(name),
      };
    })
    .filter((obj) => obj.id !== "3");

  const findReports = (targetVal) => {
    return reports.find((x) => {
      return x.reportId === parseInt(targetVal);
    });
  };

  return (
    <>
      <TpexLoader isLoading={isLoading} />
      <main id="main">
        <div className="container-fluid container-padding">
          <TpexBreadCrumb name="Common Download Upload" />
          <div className="panelBox pb-10">
            <div className="search-panel">
              <div className="col">
                <div className="heading">
                  <i className="bg-border"></i>
                  <h1>Common Download Upload</h1>
                </div>
              </div>
              <div>
                <ul className="nav nav-tabs">
                  <li className="nav-item">
                    <a
                      href="#download"
                      className="nav-link active"
                      data-bs-toggle="tab"
                    >
                      Download Report
                    </a>
                  </li>
                  <li className="nav-item">
                    <a href="#upload" className="nav-link" data-bs-toggle="tab">
                      Upload Report
                    </a>
                  </li>
                </ul>

                <div className="tab-content">
                  <div className="tab-pane fade show active" id="download">
                    <DownloadReport
                      options={optionsDownload}
                      findReports={findReports}
                      reports={downloadReports}
                      userId={userId}
                      setIsLoading={setIsLoading}
                    />
                  </div>
                  <div className="tab-pane fade" id="upload">
                    <UploadReport
                      options={options}
                      userId={userId}
                      processTable={processTable}
                      setProcessTable={setProcessTable}
                      setIsLoading={setIsLoading}
                      findReports={findReports}
                      reports={reports}
                      fetchdata={fetchReports}
                    />
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
}

export default UploadDownload;
