import {
  createMessage,
  createMesssageReplacer,
  ddmmTOmmddChange,
  deepEqual,
  formatedDate,
} from "../../helpers/util";
import React, { useEffect, useState, useMemo } from "react";
import EditableRow from "../haisen-details-retrieval/EditableRow";
import ReadOnlyRow from "../haisen-details-retrieval/ReadOnlyRow";
import "../../styles/table.css";
import {
  filterRows,
  sortRowsHaisenGrid,
} from "../../common/components/tables/helpers";
import { MODULE_CONST } from "../../constants/constant";
import { postRequest } from "../../services/axios-client";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { LABEL_CONST } from "../../constants/label.constant.en";

const gridColumns = [
  { id: "check" },
  { id: "haisenYearMonth", name: "Haisen YMTH", date: true },
  { id: "haisenNo", name: "Haisen No." },
  { id: "etd", name: "ETD", date: true },
  { id: "eta", name: "ETA", date: true },
  { id: "buyer", name: "Buyer" },
  { id: "portOfLoading", name: "Port of Loading" },
  { id: "portOfDischarge", name: "Port of Discharge" },
  { id: "oceanVessel", name: "Ocean Vessel" },
  { id: "oceanVoyage", name: "Ocean Voyage" },
  { id: "feederVessel", name: "Feeder Vessel" },
  { id: "feederVoyage", name: "Feeder Voyage" },
  { id: "noOf20ftContainer", name: "No. of 20ft Container" },
  { id: "noOf40ftContainer", name: "No. of 40ft Container" },
  { id: "containerEfficiency", name: "Container Efficiency (%)" },
];

const HaisenTable = ({
  setInvoiceState,
  searchedData,
  portLoadDis,
  id = "id",
  freshSearch,
  fetchSearch,
  setOtherFields,
  otherFields,
  setIsLoading,
  validSaveInvoice,
  setValidSaveInvoice,
  handleCancelInvoiceSave,
  clearInvoiceAllRows,
}) => {
  const [etdDate, setEtdDate] = useState([]);
  const [etaDate, setEtaDate] = useState([]);
  const [editFormData, setEditFormData] = useState([]);
  const [editedRows, setEditedRows] = useState([]);
  const [hideFilter, setHideFilter] = useState(true);
  const [filters, setFilters] = useState({});
  const [gridData, setGridData] = useState([]);
  const [mulpleSelectedIds, setMulpleSelectedIds] = useState([]);
  const [sort, setSort] = useState({
    order: "asc",
    orderBy: id,
    orderDate: false,
  });
  const userId = "TestUser";
  const [modalShow, setModalShow] = useState(false);
  const [status, setStatus] = useState("");
  const [content, setContent] = useState("");
  const [edtFlag, setEdtFlag] = useState(false);
  const [rowClicked, setRowClicked] = useState(false);
  const [selectedId, setSelectedId] = useState("");

  const filteredRows = useMemo(
    () => filterRows(searchedData, filters),
    [searchedData, filters]
  );

  const sortedRows = useMemo(
    () => sortRowsHaisenGrid(filteredRows, sort),
    [filteredRows, sort]
  );

  useEffect(() => {
    setGridData(sortedRows);
  }, [sortedRows]);

  const onChangeSelectionPol = (event, searchIndex) => {
    let copyEditPol = [...editFormData];
    copyEditPol[searchIndex] = {
      ...copyEditPol[searchIndex],
      portOfLoading: event.target.value,
    };
    setEditFormData(copyEditPol);
    setOtherFields(true);
  };

  const onChangeSelectionPod = (event, searchIndex) => {
    let copyEditPod = [...editFormData];
    copyEditPod[searchIndex] = {
      ...copyEditPod[searchIndex],
      portOfDischarge: event.target.value,
    };
    setEditFormData(copyEditPod);
    setOtherFields(true);
  };

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
        {gridColumns.map((gridColumn) => {
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
              {gridColumn.id === "check" ? (
                ""
              ) : (
                <>
                  <span>{gridColumn.name} </span>
                  <span
                    onClick={() => handleSort(gridColumn.id, gridColumn.date)}
                  >
                    {sortIcon()}
                  </span>
                </>
              )}
            </th>
          );
        })}
      </tr>
    );
  };

  const renderHeaderSearch = () => {
    return (
      <tr className={hideFilter ? "d-none" : ""}>
        {gridColumns.map((gridColumn) => {
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

  const searchAlert = () => {
    setStatus(LABEL_CONST.WARNING);
    setContent(LABEL_CONST.DO_YOU_WISH_TO_PROCEED_WITHOUT_SAVE);
    setModalShow(true);
  };

  const handleEditClick = (event, singleRow) => {
    event.preventDefault();
    let res = gridData.includes(singleRow);
    setRowClicked(res);
    setSelectedId(singleRow.id);
    if (validSaveInvoice) {
      searchAlert();
    } else {
      if (event.target.tagName === "TD") {
        setInvoiceState(singleRow);
      } else {
        setOtherFields(true);
        let addIds = [...mulpleSelectedIds, singleRow.id];
        let setrows = [...new Set(addIds)].sort((a, b) => (a > b ? 1 : -1));
        setMulpleSelectedIds(setrows);
        const formValues = {
          id: singleRow.id,
          haisenYearMonth: singleRow.haisenYearMonth,
          haisenNo: singleRow.haisenNo,
          etd: singleRow.etd,
          eta: singleRow.eta,
          buyer: singleRow.buyer,
          portOfLoading: singleRow.portOfLoading,
          portOfDischarge: singleRow.portOfDischarge,
          oceanVessel: singleRow.oceanVessel,
          oceanVoyage: singleRow.oceanVoyage,
          feederVessel: singleRow.feederVessel,
          feederVoyage: singleRow.feederVoyage,
          noOf20ftContainer: singleRow.noOf20ftContainer,
          noOf40ftContainer: singleRow.noOf40ftContainer,
          containerEfficiency: singleRow.containerEfficiency,
        };
        setEditFormData([...editFormData, formValues]);
        setEditedRows([...editedRows, formValues]);
        setEtaDate([
          ...etaDate,
          { id: singleRow.id, eta: new Date(ddmmTOmmddChange(formValues.eta)) },
        ]);
        setEtdDate([
          ...etdDate,
          { id: singleRow.id, etd: new Date(ddmmTOmmddChange(formValues.etd)) },
        ]);
      }
    }
  };

  const handleEditFormSubmit = () => {
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
          feederVessel: [obj.feederVessel],
          feederVoyage: [obj.feederVoyage],
          portOfLoading: pol[0],
          portOfDischarge: pod[0],
        };
      });
    newEditedRows && newEditedRows.length > 0 && setOtherFields(true);
    newEditedRows.length > 0
      ? fetchSaveHaisenDetails(newEditedRows)
      : noChangeInfo();
  };

  const noChangeInfo = () => {
    setStatus(LABEL_CONST.INFORMATION);
    setContent(LABEL_CONST.NO_CHANGES_TO_SAVE);
    setModalShow(true);
  };

  const fetchSaveHaisenDetails = (newEditedRows) => {
    setIsLoading(true);
    postRequest(
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.API_BASE_URL,
      MODULE_CONST.HAISEN_DETAILS_RETRIEVAL.FETCH_SAVE_HAISEN_API +
        `?userId=${userId}`,
      newEditedRows
    )
      .then((response) => {
        setIsLoading(false);
        if (response) {
          clearInvoiceAllRows();
          setStatus(LABEL_CONST.INFORMATION);
          setContent(response.data[0]);
          setModalShow(true);

          setEtdDate([]);
          setEtaDate([]);
          setEditFormData([]);
          setMulpleSelectedIds([]);
          fetchSearch(freshSearch);
          setInvoiceState({});
          setOtherFields(false);
          setSelectedId("");
        }
      })
      .catch((e) => {
        setIsLoading(false);
        catchErrorHandle(e);
        setEdtFlag(true);
      });
  };

  const catchErrorHandle = (error) => {
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
  };

  const showAlertMsg = (status, content) => {
    setStatus(status);
    setContent(content);
    setModalShow(true);
  };

  const handleEditFormChange = (event, searchIndex) => {
    event.preventDefault();
    let copyEditFormData = [...editFormData];
    copyEditFormData[searchIndex] = {
      ...copyEditFormData[searchIndex],
      [event.target.name]: event.target.value,
    };
    setEditFormData(copyEditFormData);
    setOtherFields(true);
  };

  const handleCancelClick = () => {
    setEtdDate([]);
    setEtaDate([]);
    setEditFormData([]);
    setMulpleSelectedIds([]);
    setOtherFields(false);
  };

  const etdDateFormatVerify = (srchIndex, etdDateParam) => {
    let copyEtdDateVerify = [...etdDate];
    copyEtdDateVerify[srchIndex] = {
      ...copyEtdDateVerify[srchIndex],
      etd: etdDateParam,
    };
    setEtdDate(copyEtdDateVerify);
    let dateddmmyyyy = formatedDate(etdDateParam);
    let copyEditFormEtdDate = [...editFormData];
    copyEditFormEtdDate[srchIndex] = {
      ...copyEditFormEtdDate[srchIndex],
      etd: dateddmmyyyy,
    };
    setEditFormData(copyEditFormEtdDate);
  };

  const etdDateFormatRestore = (srchIndx, etdDtParam) => {
    let copyEtdDate = [...etdDate];
    copyEtdDate[srchIndx] = {
      ...copyEtdDate[srchIndx],
      etd: etdDtParam,
    };
    setEtdDate(copyEtdDate);
    let dateddmmyyyyRestore = formatedDate(etdDtParam);
    let copyEditFormEtdDate = [...editFormData];
    copyEditFormEtdDate[srchIndx] = {
      ...copyEditFormEtdDate[srchIndx],
      etd: dateddmmyyyyRestore,
    };
    setEditFormData(copyEditFormEtdDate);
  };

  const handleDateEtd = (etdDateParam, searchIndex) => {
    const etdDateRegExp = /^\d{2}([./-])\d{2}\1\d{4}$/;
    !etdDateParam ||
    (etdDateParam && !formatedDate(etdDateParam).match(etdDateRegExp))
      ? etdDateFormatVerify(searchIndex, etdDateParam)
      : etdDateFormatRestore(searchIndex, etdDateParam);
    setOtherFields(true);
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
        eta: etaDateArgs,
      };
      setEtaDate(copyEtaDateVal);
      let ddmmyyyyVal = formatedDate(etaDateArgs);
      let editedEtaValue = [...editFormData];
      editedEtaValue[searchIndex] = {
        ...editedEtaValue[searchIndex],
        eta: ddmmyyyyVal,
      };
      setEditFormData(editedEtaValue);
    } else {
      let etaDateValue = [...etaDate];
      etaDateValue[searchIndex] = {
        ...etaDateValue[searchIndex],
        eta: etaDateArgs,
      };
      setEtaDate(etaDateValue);
      let etaddmmyyyy = formatedDate(etaDateArgs);
      let editFormEtaDateInfo = [...editFormData];
      editFormEtaDateInfo[searchIndex] = {
        ...editFormEtaDateInfo[searchIndex],
        eta: etaddmmyyyy,
      };
      setEditFormData(editFormEtaDateInfo);
    }
    setOtherFields(true);
  };

  const handleSaveModal = () => {
    if (validSaveInvoice) {
      searchAlert();
    } else if (
      gridData === undefined ||
      gridData.length === 0
    ) {
      setStatus(LABEL_CONST.ERROR);
      setContent(LABEL_CONST.SEARCH_HAS_NOT_BEEN_PERFORMED);
      setModalShow(true);
    } else if (!otherFields) {
      handleCancelClick();
      setStatus(LABEL_CONST.INFORMATION);
      setContent(LABEL_CONST.NO_CHANGES_TO_SAVE);
      setModalShow(true);
    } else {
      setStatus(LABEL_CONST.WARNING);
      setContent(LABEL_CONST.DO_YOU_WISH_TO_SAVE_CHANGES);
      setModalShow(true);
    }
  };

  const clearInvoiceGrid = () => {
    setValidSaveInvoice(true);
    setModalShow(false);
    handleCancelInvoiceSave();
  };

  return (
    <>
      <div className="grid-panel">
        <div className="row g-0 filter-with-orderType mt-1">
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
                  {gridData && gridData !== undefined && gridData.length > 0 ? (
                    gridData.map((singleRow, index) => (
                      <React.Fragment key={singleRow.id}>
                        {mulpleSelectedIds.includes(singleRow.id) ? (
                          <EditableRow
                            id={singleRow.id}
                            mulpleSelectedIds={mulpleSelectedIds}
                            editFormData={editFormData}
                            handleEditFormChange={handleEditFormChange}
                            handleCancelClick={handleCancelClick}
                            etdDate={etdDate}
                            etaDate={etaDate}
                            handleDateEtd={handleDateEtd}
                            handleDateEta={handleDateEta}
                            onChangeSelectionPol={onChangeSelectionPol}
                            onChangeSelectionPod={onChangeSelectionPod}
                            searchedData={searchedData}
                            portLoadDis={portLoadDis}
                          />
                        ) : (
                          <ReadOnlyRow
                            singleRow={singleRow}
                            handleEditClick={handleEditClick}
                            rowClicked={rowClicked}
                            selectedId={selectedId}
                          />
                        )}
                      </React.Fragment>
                    ))
                  ) : (
                    <tr>
                      <td className="noData" colSpan="12">
                        No data found.
                      </td>
                    </tr>
                  )}
                </tbody>
              </table>
            </div>
          </div>
        </div>

        <div className="col text-end mt-2">
          <button
            type="button"
            className="btn btn-primary"
            onClick={handleSaveModal}
          >
            Save Haisen Details
          </button>

          <AlertModal
            handleClick={() => {
              validSaveInvoice ? clearInvoiceGrid() : handleEditFormSubmit();
            }}
            show={modalShow}
            onHide={() => {
              setModalShow(false);
              edtFlag ? setEdtFlag((pre) => !pre) : handleCancelClick();
            }}
            status={status}
            content={content}
          />
        </div>
      </div>
    </>
  );
};

export default HaisenTable;
