import React, { useState, useMemo, useEffect } from "react";
import {
  createMessage,
  createMesssageReplacer,
  formatedDate,
  ddmmTOmmddChange,
  deepEqual,
} from "../../helpers/util";
import HaisenTable from "../haisen-details-retrieval/HaisenTable";
import TpexSimpleButton from "../../common/components/button";
import "../../styles/table.css";
import { TpexBreadCrumb } from "../../common/components/breadcrumb/breadcrumb";
import { TpexLoader } from "../../common/components/loader/loader";
import { MODULE_CONST } from "../../constants/constant";
import {
  postRequest,
  getRequest,
  putRequest,
} from "../../services/axios-client";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { LABEL_CONST } from "../../constants/label.constant.en";
import "../../common/components/alert-modal/alert-modal.css";
import InvoiceReadOnlyRow from "../../components/haisen-details-retrieval/InvoiceReadOnlyRow";
import {
  filterRows,
  sortRowsHaisenGrid,
} from "../../common/components/tables/helpers";
import InvoiceEditableRow from "../../components/haisen-details-retrieval/InvoiceEditableRow";
import { TpexDatePicker } from "../../common/components/datepicker/datepicker";

const invoiceGridColumns = [
  { id: "invoiceNo", name: "Invoice No." },
  { id: "invoiceDate", name: "Invoice Date", date: true },
  { id: "invoiceAmount", name: "Invoice Amount" },
  { id: "invoiceM3", name: "Invoice M3" },
  { id: "etd", name: "ETD", date: true },
  { id: "eta", name: "ETA", date: true },
  { id: "oceanVessel", name: "Ocean Vessel" },
  { id: "oceanVoyage", name: "Ocean Voyage" },
  { id: "feederVessel", name: "Feeder Vessel" },
  { id: "feederVoyage", name: "Feeder Voyage" },
  { id: "portOfLoading", name: "Port of Loading" },
  { id: "portOfDischarge", name: "Port of Discharge" },
  { id: "haisenNo", name: "Haisen No." },
];

const HaisenDetails = () => {
  const [dateFrom, setDateFrom] = useState("");
  const [dateTo, setDateTo] = useState("");
  const [buyer, setbuyer] = useState("");
  const [checkBoxes, setCheckBoxes] = useState({ regular: true, cpospo: true });
  const [searchedData, setSearchedData] = useState([]);
  const [modifyDateFrom, setModifyDateFrom] = useState("");
  const [modifyDateTo, setModifyDateTo] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [portLoadDis, setPortLoadDis] = useState([]);
  const [freshSearch, setFreshSearch] = useState({});
  const [otherFields, setOtherFields] = useState(false);
  const [modalShow, setModalShow] = useState(false);
  const [searchStatus, setSearchStatus] = useState("");
  const [searchContent, setSearchContent] = useState("");
  const [invoiceState, setInvoiceState] = useState({});
  const [validSaveInvoice, setValidSaveInvoice] = useState(false);
  const [invoiceData, setInvoiceData] = useState([]);
  const [gridData, setGridData] = useState([]);
  const [filters, setFilters] = useState({});
  const [hideFilter, setHideFilter] = useState(true);
  const [mulpleSelectedIds, setMulpleSelectedIds] = useState([]);
  const [editFormData, setEditFormData] = useState([]);
  const [etdDate, setEtdDate] = useState([]);
  const [etaDate, setEtaDate] = useState([]);
  const [editedRows, setEditedRows] = useState([]);
  const [invoiceBtnModalShow, setInvoiceBtnModalShow] = useState(false);
  const [invoiceSaveModalShow, setInvoiceSaveModalShow] = useState(false);
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");
  let userId = "TestUser";

  const invoiceWithOutSaveAlert = () => {
    setStatus(LABEL_CONST.WARNING);
    setContent(LABEL_CONST.DO_YOU_WISH_TO_PROCEED_WITHOUT_SAVE);
    setInvoiceSaveModalShow(true);
  };

  const searchAlert = () => {
    setSearchStatus(LABEL_CONST.WARNING);
    setSearchContent(LABEL_CONST.DO_YOU_WISH_TO_PROCEED_WITHOUT_SAVE);
    setModalShow(true);
  };

  const handleDateFrom = (data) => {
    const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
    if (otherFields || validSaveInvoice) {
      searchAlert();
    } else if (!data || (data && !formatedDate(data).match(dateReg))) {
      setDateFrom("");
      let dateddmmyyyy = formatedDate(data);
      setModifyDateFrom(dateddmmyyyy);
    } else {
      setDateFrom(data);
      let dateddmmyyyy = formatedDate(data);
      setModifyDateFrom(dateddmmyyyy);
    }
  };

  const handleDateTo = (data) => {
    const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
    if (otherFields || validSaveInvoice) {
      searchAlert();
    } else if (!data || (data && !formatedDate(data).match(dateReg))) {
      setDateTo("");
      setModifyDateTo("");
    } else {
      setDateTo(data);
      let dateddmmyyyy = formatedDate(data);
      setModifyDateTo(dateddmmyyyy);
    }
  };

  const onChangeSelection = (event) => {
    event.preventDefault();
    if (otherFields || validSaveInvoice) {
      searchAlert();
    } else {
      setbuyer(event.target.value.toUpperCase());
    }
  };

  const handleCheck = (event) => {
    if (otherFields || validSaveInvoice) {
      searchAlert();
    } else {
      let checked = event.target.checked;
      setCheckBoxes({ ...checkBoxes, [event.target.name]: checked });
    }
  };

  const fetchSearch = (searchParams) => {
    setIsLoading(true);
    clearInvoiceAllRows();
    postRequest(
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.API_BASE_URL,
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.FETCH_SEARCH_API,
      {
        etdFrom: searchParams.dateFrom,
        etdTo: searchParams.dateTo,
        buyer: searchParams.buyer,
        orderType: {
          regular: searchParams.regular,
          cpoOrspo: searchParams.cpospo,
        },
      }
    )
      .then((response) => {
        if (response.data.portGrpObj.length !== 0) {
          let newPortGrpObj = response.data.portGrpObj.map((port) => {
            return {
              cd: `${port.cd}-${port.name}`,
              name: `${port.cd}-${port.name}`,
            };
          });
          setPortLoadDis(newPortGrpObj);
          let newData = [...response.data.listInvHaisenDetailResponse];
          let addedIdData = newData.map((srchData, index) => ({
            ...srchData,
            id: index,
          }));

          let addedData = addedIdData.map((srchData) => ({
            ...srchData,
            feederVessel: srchData.feederVessel[0],
            feederVoyage: srchData.feederVoyage[0],
          }));
          setSearchedData(addedData);
          setIsLoading(false);
        }
      })
      .catch((error) => {
        setIsLoading(false);
        if (
          error.response.data.errorMessageParams &&
          Object.keys(error.response.data.errorMessageParams).length > 0
        ) {
          const messageAfterReplace = createMesssageReplacer(
            error.response.data.errorMessageParams,
            error.response.data.exception
          );
          showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
        } else {
          showAlertMsg(
            LABEL_CONST.ERROR,
            createMessage(error.response.data.exception)
          );
        }

        if (error.response.data.etdFrom) {
          showAlertMsg(
            LABEL_CONST.ERROR,
            createMessage(error.response.data.etdFrom)
          );
        }
      });
  };

  const showAlertMsg = (status, content) => {
    setSearchStatus(status);
    setSearchContent(content);
    setModalShow(true);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    if (otherFields || validSaveInvoice) {
      searchAlert();
    } else {
      setSearchedData([]);
      let regular = checkBoxes.regular;
      let cpospo = checkBoxes.cpospo;
      setFreshSearch({
        dateFrom: modifyDateFrom,
        dateTo: modifyDateTo,
        regular,
        cpospo,
        buyer,
      });
      fetchSearch({
        dateFrom: modifyDateFrom,
        dateTo: modifyDateTo,
        regular,
        cpospo,
        buyer,
      });
    }
  };

  const handleResearch = () => {
    setOtherFields(false);
    setModalShow(false);
    setSearchedData([]);
    let regular = checkBoxes.regular;
    let cpospo = checkBoxes.cpospo;
    setFreshSearch({
      dateFrom: modifyDateFrom,
      dateTo: modifyDateTo,
      regular,
      cpospo,
      buyer,
    });
    fetchSearch({
      dateFrom: modifyDateFrom,
      dateTo: modifyDateTo,
      regular,
      cpospo,
      buyer,
    });
  };

  const clearInvoiceTableData = () => {
    setModalShow(false);
    handleCancelInvoiceSave();
  };

  const [sort, setSort] = useState({
    order: "asc",
    orderBy: "id",
    orderDate: false,
  });

  const filteredRows = useMemo(
    () => filterRows(invoiceData, filters),
    [invoiceData, filters]
  );
  const sortedRows = useMemo(
    () => sortRowsHaisenGrid(filteredRows, sort),
    [filteredRows, sort]
  );

  useEffect(() => {
    setGridData(sortedRows);
  }, [sortedRows]);

  const handleSort = (id, date) => {
    setSort((prevSort) => ({
      order:
        prevSort.order === "asc" && prevSort.orderBy === id ? "desc" : "asc",
      orderBy: id,
      orderDate: date,
    }));
  };

  const handleSearch = (value, id) => {
    if (value) {
      setFilters((prevFilters) => ({
        ...prevFilters,
        [id]: value,
      }));
    } else {
      setFilters((prevFilters) => {
        const updatedFilters = { ...prevFilters };
        delete updatedFilters[id];
        return updatedFilters;
      });
    }
  };

  const renderHeader = () => {
    return (
      <tr>
        {invoiceGridColumns.map((gridColumn) => {
          const sortIcon = () => {
            if (gridColumn.id === sort.orderBy) {
              if (sort.order === "asc") {
                return <i className="arrow-up"></i>;
              }
              return <i className="arrow-down"></i>;
            } else {
              return <i className="arrow-down"></i>;
            }
          };
          return (
            <th key={gridColumn.id}>
              {
                <>
                  <span>{gridColumn.name} </span>
                  <span
                    onClick={() => handleSort(gridColumn.id, gridColumn.date)}
                  >
                    {sortIcon()}
                  </span>
                </>
              }
            </th>
          );
        })}
      </tr>
    );
  };

  const renderHeaderSearch = () => {
    return (
      <tr className={hideFilter ? "d-none" : ""}>
        {invoiceGridColumns.map((gridColumn) => {
          return (
            <th
              key={`${gridColumn.id}-filter`}
              className={`${gridColumn.id} pt-0`}
            >
              <input
                key={`${gridColumn.id}-search`}
                type="text"
                className="form-control"
                placeholder={`${gridColumn.name}`}
                value={filters[gridColumn.id] || ""}
                onChange={(event) =>
                  handleSearch(event.target.value, gridColumn.id)
                }
              />
            </th>
          );
        })}
      </tr>
    );
  };

  const handleEditClick = (event, singleRow) => {
    event.preventDefault();
    if (otherFields) {
      searchAlert();
    } else {
      setValidSaveInvoice(true)
      let addIds = [...mulpleSelectedIds, singleRow.id];
      let setrows = [...new Set(addIds)].sort((a, b) => (a > b ? 1 : -1));
      setMulpleSelectedIds(setrows);

      const formValues = {
        id: singleRow.id,
        invoiceNo: singleRow.invoiceNo,
        invoiceDate: singleRow.invoiceDate,
        invoiceAmount: singleRow.invoiceAmount,
        invoiceM3: singleRow.invoiceM3,
        etdDate: singleRow.etdDate,
        etaDate: singleRow.etaDate,
        oceanVessel: singleRow.oceanVessel,
        oceanVoyage: singleRow.oceanVoyage,
        feederVessel: singleRow.feederVessel,
        feederVoyage: singleRow.feederVoyage,
        portOfLoading: singleRow.portOfLoading,
        portOfDischarge: singleRow.portOfDischarge,
        haisenNo: singleRow.haisenNo,
      };

      setEditFormData([...editFormData, formValues]);
      setEditedRows([...editedRows, formValues]);
      setEtaDate([
        ...etaDate,
        {
          id: singleRow.id,
          etaDate: new Date(ddmmTOmmddChange(formValues.etaDate)),
        },
      ]);

      setEtdDate([
        ...etdDate,
        {
          id: singleRow.id,
          etdDate: new Date(ddmmTOmmddChange(formValues.etdDate)),
        },
      ]);
    }
  };

  const handleEditFormChange = (event, searchIndex) => {
    event.preventDefault();
    let copyEditFormData = [...editFormData];
    copyEditFormData[searchIndex] = {
      ...copyEditFormData[searchIndex],
      [event.target.name]: event.target.value,
    };
    setEditFormData(copyEditFormData);
    setValidSaveInvoice(true);
  };

  const handleCancelInvoiceSave = () => {
    setEtdDate([]);
    setEtaDate([]);
    setEditFormData([]);
    setMulpleSelectedIds([]);
    setOtherFields(false);
    setValidSaveInvoice(false);
  };

  const clearInvoiceAllRows = () => {
    setGridData([]);
    setInvoiceState({});
    setInvoiceData([]);
  };

  const handleDateEta = (etaDateArgs, searchIndex) => {
    const dateReg = /^\d{2}([./-])\d{2}\1\d{4}$/;
    if (
      !etaDateArgs ||
      (etaDateArgs && !formatedDate(etaDateArgs).match(dateReg))
    ) {
      let copyEtaDateVal = [...etaDate];
      copyEtaDateVal[searchIndex] = {
        ...copyEtaDateVal[searchIndex],
        etaDate: etaDateArgs,
      };
      setEtaDate(copyEtaDateVal);
      let ddmmyyyyVal = formatedDate(etaDateArgs);
      let editedEtaValue = [...editFormData];
      editedEtaValue[searchIndex] = {
        ...editedEtaValue[searchIndex],
        etaDate: ddmmyyyyVal,
      };
      setEditFormData(editedEtaValue);
    } else {
      let etaDateValue = [...etaDate];
      etaDateValue[searchIndex] = {
        ...etaDateValue[searchIndex],
        etaDate: etaDateArgs,
      };
      setEtaDate(etaDateValue);
      let etaddmmyyyy = formatedDate(etaDateArgs);
      let editFormEtaDateInfo = [...editFormData];
      editFormEtaDateInfo[searchIndex] = {
        ...editFormEtaDateInfo[searchIndex],
        etaDate: etaddmmyyyy,
      };
      setEditFormData(editFormEtaDateInfo);
    }
    setValidSaveInvoice(true);
  };

  const onChangeSelectionPol = (event, searchIndex) => {
    let copyEditPol = [...editFormData];
    copyEditPol[searchIndex] = {
      ...copyEditPol[searchIndex],
      portOfLoading: event.target.value,
    };
    setEditFormData(copyEditPol);
    setValidSaveInvoice(true);
  };

  const onChangeSelectionPod = (event, searchIndex) => {
    let copyEditPod = [...editFormData];
    copyEditPod[searchIndex] = {
      ...copyEditPod[searchIndex],
      portOfDischarge: event.target.value,
    };
    setEditFormData(copyEditPod);
    setValidSaveInvoice(true);
  };

  const handleInvoiceSaveFormSubmit = () => {
    let newEditedRows = editFormData
      .filter((objOne) => {
        return !editedRows.some((objTwo) => {
          return deepEqual(objOne, objTwo);
        });
      })
      .map((obj) => {
        let pol = obj.portOfLoading.split("-");
        let pod = obj.portOfDischarge.split("-");
        return {
          ...obj,
          portOfLoading: pol[0],
          portOfDischarge: pod[0],
        };
      });
    newEditedRows && newEditedRows.length > 0 && setValidSaveInvoice(true);
    if (newEditedRows.length > 0) {
      fetchSaveHaisenDetails(newEditedRows);
    } else {
      setStatus(LABEL_CONST.INFORMATION);
      setContent(LABEL_CONST.NO_CHANGES_TO_SAVE);
      setInvoiceSaveModalShow(true);
    }
  };

  const fetchSaveHaisenDetails = (newEditedRows) => {
    setIsLoading(true);
    let addedIdData = newEditedRows.map((haisenhData) => ({
      ...haisenhData,
      buyer: invoiceState.buyer,
    }));
    putRequest(
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.API_BASE_URL,
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.SAVE_INVOICE_DETAILS +
        `?userId=${userId}`,
      addedIdData
    )
      .then((response) => {
        if (response) {
          setIsLoading(false);
          setStatus(LABEL_CONST.INFORMATION);
          setContent(response.data[0]);
          setInvoiceSaveModalShow(true);
        }
        setEtdDate([]);
        setEtaDate([]);
        setEditFormData([]);
        setMulpleSelectedIds([]);
        handleResearch();
        setValidSaveInvoice(true);
      })
      .catch((e) => {
        if (e) {
          setIsLoading(false);
          setInvoiceSaveModalShow(false);
          catchErrorHandle(e);
        }
      });
  };

  function catchErrorHandle(error) {
    if (
      error.response.data.errorMessageParams &&
      Object.keys(error.response.data.errorMessageParams).length > 0
    ) {
      const messageAfterReplace = createMesssageReplacer(
        error.response.data.errorMessageParams,
        error.response.data.exception
      );
      showAlertMsg(LABEL_CONST.ERROR, messageAfterReplace);
    } else {
      showAlertMsg(
        LABEL_CONST.ERROR,
        createMessage(error.response.data.exception)
      );
    }
  }

  const handleSaveModal = () => {
    if (otherFields) {
      invoiceWithOutSaveAlert();
    } else if (!validSaveInvoice) {
      setStatus(LABEL_CONST.INFORMATION);
      setContent(LABEL_CONST.NO_CHANGES_TO_SAVE);
      setInvoiceSaveModalShow(true);
    } else {
      setStatus(LABEL_CONST.WARNING);
      setContent(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES);
      setInvoiceSaveModalShow(true);
    }
  };

  const fetchInvoiceDetails = () => {
    let haisenNo = invoiceState.haisenNo;
    let haisenYearMonth = invoiceState.haisenYearMonth;
    setIsLoading(true);
    getRequest(
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.API_BASE_URL,
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.FETCH_INVOICE_DETAILS +
        `?haisenNo=${haisenNo}&haisenYear=${haisenYearMonth}`
    )
      .then((response) => {
        let newPortGrpObj = response.data.portGrpObjDto.map((port) => {
          return {
            ...port,
            cd: `${port.cd}-${port.name}`,
            name: `${port.cd}-${port.name}`,
          };
        });
        setPortLoadDis(newPortGrpObj);

        let addedIdData = response.data.invoiceDetailsResponseDto.map(
          (haisenhData, index) => ({
            ...haisenhData,
            id: index,
          })
        );
        setInvoiceData(addedIdData);
        setIsLoading(false);
      })
      .catch((e) => {
        if (e) {
          setIsLoading(false);
          catchErrorHandle(e);
        }
      });
  };

  const handleClickInvoiceBtn = () => {
    if (validSaveInvoice) {
      setStatus(LABEL_CONST.WARNING);
      setContent(LABEL_CONST.DO_YOU_WISH_TO_PROCEED_WITHOUT_SAVE);
      setInvoiceBtnModalShow(true);
    } else if (otherFields) {
      setStatus(LABEL_CONST.ERROR);
      setContent(LABEL_CONST.PLEASE_SAVE_HAISEN_DETAILS_BEFORE_PROCEEDING);
      setInvoiceBtnModalShow(true);
    } else if (Object.keys(invoiceState).length === 0) {
      setStatus(LABEL_CONST.ERROR);
      setContent(LABEL_CONST.SELECT_HAISEN_NO_TO_SEE_INVOICE_DETAILS);
      setInvoiceBtnModalShow(true);
    } else {
      setInvoiceData([]);
      handleCancelInvoiceSave();
      fetchInvoiceDetails();
    }
  };

  const handleInvioceGridReload = () => {
    setInvoiceBtnModalShow(false);
    setInvoiceData([]);
    handleCancelInvoiceSave();
    fetchInvoiceDetails();
  };

  const handleResearchModalClose = () => {
    setInvoiceSaveModalShow(false);
    handleResearch();
  };

  return (
    <>
      <TpexLoader isLoading={isLoading} />
      <main id="main">
        <div className="container-fluid container-padding">
          <TpexBreadCrumb name="Invoice Operations Haisen Details" />
          <div className="panelBox">
            <div className="search-panel">
              <div className="col">
                <div className="heading">
                  <i className="bg-border"></i>
                  <h1>Invoice Operations Haisen Details</h1>
                </div>
              </div>
              <form>
                <div className="row mt-12">
                  <div className="form-group col-2">
                    <div className="customDatePicker mandatoryControl">
                      <label>ETD From</label>
                      <TpexDatePicker
                        className={"form-control"}
                        dateSelected={dateFrom}
                        handleDateSelected={(date) => handleDateFrom(date)}
                        id="requestdatefrom"
                        isDirectDatePicker={true}
                      />
                    </div>
                  </div>
                  <div className="form-group col-2">
                    <div className="customDatePicker">
                      <label>ETD To</label>
                      <TpexDatePicker
                        className={"form-control"}
                        id="requestdateto"
                        dateSelected={dateTo}
                        handleDateSelected={(date) => handleDateTo(date)}
                        isDirectDatePicker={true}
                      />
                    </div>
                  </div>
                  <div className="form-group col-2">
                    <div className="">
                      <label>Buyer</label>
                      <input
                        type="text"
                        className="form-control"
                        id="buyer"
                        name="buyer"
                        value={buyer}
                        onChange={onChangeSelection}
                      />
                    </div>
                  </div>
                  <div className="form-group col-4 mt-4">
                    <div className="order-type">Order Type:</div>
                    <div className="form-check form-check-inline me-5 mandatoryControl">
                      <input
                        className="form-check-input"
                        type="checkbox"
                        id="regular"
                        name="regular"
                        checked={checkBoxes.regular}
                        onChange={handleCheck}
                      />
                      <label htmlFor="checkRegular">Regular</label>
                    </div>
                    <div className="form-check form-check-inline me-1 mandatoryControl">
                      <input
                        className="form-check-input"
                        type="checkbox"
                        id="cpospo"
                        name="cpospo"
                        checked={checkBoxes.cpospo}
                        onChange={handleCheck}
                      />
                      <label htmlFor="checkCpoSpo">CPO/SPO</label>
                    </div>
                  </div>
                  <div className="form-group col-2">
                    <div className="mt-3 pt-2 text-end">
                      <TpexSimpleButton
                        color="btn btn-primary"
                        text="Search"
                        handleClick={handleSubmit}
                      />
                      <AlertModal
                        handleClick={() => {
                          validSaveInvoice
                            ? clearInvoiceTableData()
                            : handleResearch();
                        }}
                        show={modalShow}
                        onHide={() => setModalShow(false)}
                        status={searchStatus}
                        content={searchContent}
                      />
                    </div>
                  </div>
                </div>
              </form>
            </div>
            {searchedData.length !== 0 ? (
              <>
                <HaisenTable
                  id="id"
                  searchedData={searchedData}
                  portLoadDis={portLoadDis}
                  setSearchedData={setSearchedData}
                  freshSearch={freshSearch}
                  fetchSearch={fetchSearch}
                  setOtherFields={setOtherFields}
                  otherFields={otherFields}
                  setIsLoading={setIsLoading}
                  setInvoiceState={setInvoiceState}
                  validSaveInvoice={validSaveInvoice}
                  setValidSaveInvoice={setValidSaveInvoice}
                  handleCancelInvoiceSave={handleCancelInvoiceSave}
                  clearInvoiceAllRows={clearInvoiceAllRows}
                />
              </>
            ) : (
              <HaisenTable />
            )}
          </div>
          <div className="panelBox mt-10">
            <div className="row">
              <div className="col">
                <div className="heading">
                  <i className="bg-border"></i>
                  <h1>Invoice Details</h1>
                </div>
              </div>
            </div>
            <div className="col-10 mt-2 mb-0 ms-3">
              <button
                type="button"
                className="btn btn-primary"
                onClick={handleClickInvoiceBtn}
              >
                Invoice Details
              </button>
              <AlertModal
                handleClick={handleInvioceGridReload}
                show={invoiceBtnModalShow}
                onHide={() => {
                  setInvoiceBtnModalShow(false);
                }}
                status={status}
                content={content}
              />
            </div>
            <div className="grid-panel">
              <div className="row g-0 filter-with-orderType mt-1 mb-2">
                <div className="col-6 offset-6">
                  <div className="d-flex justify-content-end">
                    <button
                      className="button-filter"
                      onClick={() => setHideFilter(!hideFilter)}
                    >
                      <i className="filter-button"></i>
                      <span>Filters</span>
                    </button>
                  </div>
                </div>
              </div>
              <div className="grid-table pt-0 mt-0">
                <div className="col">
                  <div className="table-responsive">
                    <table className="table tpexTable">
                      <thead className="text-nowrap">
                        {renderHeader()}
                        {renderHeaderSearch()}
                      </thead>
                      <tbody>
                        {gridData &&
                        gridData !== undefined &&
                        gridData.length > 0 ? (
                          gridData.map((singleRow, index) => (
                            <React.Fragment key={singleRow.id}>
                              {mulpleSelectedIds.includes(singleRow.id) ? (
                                <InvoiceEditableRow
                                  id={singleRow.id}
                                  mulpleSelectedIds={mulpleSelectedIds}
                                  editFormData={editFormData}
                                  handleEditFormChange={handleEditFormChange}
                                  handleCancelInvoiceSave={
                                    handleCancelInvoiceSave
                                  }
                                  etdDate={etdDate}
                                  etaDate={etaDate}
                                  handleDateEta={handleDateEta}
                                  onChangeSelectionPol={onChangeSelectionPol}
                                  onChangeSelectionPod={onChangeSelectionPod}
                                  portLoadDis={portLoadDis}
                                />
                              ) : (
                                <InvoiceReadOnlyRow
                                  singleRow={singleRow}
                                  handleEditClick={handleEditClick}
                                />
                              )}
                            </React.Fragment>
                          ))
                        ) : (
                          <tr>
                            <td className="text-center" colSpan="12">
                              No data found.
                            </td>
                          </tr>
                        )}
                      </tbody>
                    </table>
                  </div>
                </div>
              </div>

              <div className="row">
                <div className="col text-end mt-4">
                  <button
                    type="button"
                    className="btn btn-primary"
                    onClick={handleSaveModal}
                  >
                    Save Invoice Details
                  </button>
                  <AlertModal
                    show={invoiceSaveModalShow}
                    handleClick={() => {
                      otherFields
                        ? handleResearchModalClose()
                        : handleInvoiceSaveFormSubmit();
                    }}
                    onHide={() => {
                      setInvoiceSaveModalShow(false);
                      otherFields
                        ? setOtherFields(true)
                        : handleCancelInvoiceSave();
                    }}
                    status={status}
                    content={content}
                  />
                </div>
              </div>
            </div>
          </div>
        </div>
      </main>
    </>
  );
};

export default HaisenDetails;
