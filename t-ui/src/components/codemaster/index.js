import { useEffect, useState } from "react";
import { TpexSelect } from "../../common/components/select";
import { TpexTable } from "../../common/components/tables";
import "../../styles/table.css";
import { getRequest, postRequest } from '../../services/axios-client';
import { CODE_MASTER_ID, MODULE_CONST } from "../../constants/constant";
import TpexSimpleButton from "../../common/components/button";
import { LABEL_CONST } from "../../constants/label.constant.en";
import { TpexBreadCrumb } from "../../common/components/breadcrumb/breadcrumb";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { createMessage, createMesssageReplacer } from "../../helpers/util";
import { useSelector } from "react-redux";

function Codemaster() {
  const { currentCompanyCode } = useSelector(state => state.app);
  const [codeMasterList, setCodeMasterList] = useState([]);
  const [masterName, setMasterName] = useState('');
  const [masterListName, setMasterListName] = useState('');
  const [codeMasterNames, setCodeMasterNames] = useState([]);
  const [rows, setRows] = useState([]);
  const [columns, setColumns] = useState([]);
  const [tableName, setTableName] = useState("");
  const [codeMasterPrimaryKey, setCodeMasterPrimaryKey] = useState(null);
  const [validationObj, setValidationObj] = useState(null);
  const [selectedCodeMaster, setSelectedCodeMaster] = useState("");
  const [modalShowAlert, setModalShowAlert] = useState(false);
  const [messageType, setMessageType] = useState();
  const [messageText, setMessageText] = useState();
  const [dropDownDataList, setDropDownDataList] = useState({
    BUY_CD: [],
    CURR_CD: [],
    PAYMENT_TERM: []
  });
  const [hasUnsavedData, setHasUnsavedData] = useState(false);
  const [actionCase, setActionCase] = useState("");

  useEffect(() => {
    if(currentCompanyCode !== null) {
      setSelectedCodeMaster('');
      resetAllValue();
    }
  }, [currentCompanyCode]);

  function getCodeMasterList() {
    const params = {
      userId: "LoginUserId",
      cmpCd: currentCompanyCode
    }
    postRequest(MODULE_CONST.CODE_MASTER.API_BASE_URL, MODULE_CONST.CODE_MASTER.LIST, params).then(data => {
      setCodeMasterNames(data.data);
      const codeMList = data.data.map(d => {
        return {
          id: d.codeMasterId,
          name: d.codeMasterName
        }
      });
      setCodeMasterList(codeMList);
    }).catch(function (error) {

      if (error.response.data.errorMessageParams && Object.keys(error.response.data.errorMessageParams).length > 0) {
        const messageAfterReplace = createMesssageReplacer(error.response.data.errorMessageParams, error.response.data.exception);
        openAlertBox(LABEL_CONST.ERROR, messageAfterReplace);
      } else {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
      }      
      console.log('getCodeMasterList Error', error);
    });

  }

function disableKeys(k) {
  if (selectedCodeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER) {
    if (k.PAYMENT_TERM === "InDays") {
      k.NO_OF_DAYS_DISABLE = false;
      k.DAY_OF_MTH_DISABLE = true;
      k.MTH_NO_DISABLE = true;
    }
    if (k.PAYMENT_TERM === "InMonth") {
      k.NO_OF_DAYS_DISABLE = true;
      k.DAY_OF_MTH_DISABLE = false;
      k.MTH_NO_DISABLE = false;
    }
  }

  return k;
}

  const getMasterCodeData = async (masterName) => {
    const request = {
      ...codeMasterNames.find(master => String(master.codeMasterId) === String(masterName)),
      userId: "LoginUserId",
      cmpCd: currentCompanyCode
    }
    postRequest(MODULE_CONST.CODE_MASTER.API_BASE_URL, MODULE_CONST.CODE_MASTER.CODE_MASTER_DETAILS, request).then(data => {
      
      setColumns(data.data.columns.columns || []);
      if (data.data.data.length) {
        setRows(data.data.data.map((k, i) => {
          k.idList = i + 1;
          //payment term mster conditional mandatory
          k = disableKeys(k);
          if (masterListName === CODE_MASTER_ID.FINAL_DEST_MASTER) k.DST_CD_PRIMARY = k.DST_CD;          

          return k;
        }))
      } else {
        setRows([]);
      }
      if (selectedCodeMaster === CODE_MASTER_ID.PAYMENT_TERM_MASTER) {
        setDropDownDataList({
          ...dropDownDataList,
          PAYMENT_TERM: [{
            id: "InDays",
            name: "In Days"
          },
          {
            id: "InMonth",
            name: "In Month"
          }]
        })
      }
      setTableName(data.data.columns.tableName);
      setCodeMasterPrimaryKey(data.data.columns.primaryKey);
      const valObj = getValidationObject(data.data.columns.columns)
      setValidationObj(valObj);
    }).catch(function (error) {
      console.log('Error', error);
    });

  }

  function onChangeSelection(event) {
    setSelectedCodeMaster(event.target.value);    
    let index = event.nativeEvent.target.selectedIndex;    
    setMasterName(event.nativeEvent.target[index].text);
    if (event.target.value === CODE_MASTER_ID.FINAL_DEST_MASTER) {
      getDDValues3(['CURR_BUYER_DROPDOWN_API']);
    }
  }

  function getDDValues3(apiNames = []) {
    let finalData = {};
    for (let apiName of apiNames) {
      getRequest(MODULE_CONST.CODE_MASTER.API_BASE_URL, MODULE_CONST.CODE_MASTER[apiName] + "?cmpCd=" + currentCompanyCode).then(data => {      
          finalData.BUY_CD = data.data.buyer;      
          finalData.CURR_CD = data.data.currency;
      }).catch(function (error) {
        openAlertBox(LABEL_CONST.ERROR, createMessage(error.response.data.exception));        
      }).finally(function (_f) {
        setDropDownDataList({ ...finalData })
      })
    };
  }
  
  function getGridData() {
    if (selectedCodeMaster && masterListName && selectedCodeMaster == masterListName) {
      getMasterCodeData(masterListName);
    } else {
      resetAllValue();
      setMasterListName(selectedCodeMaster);
      if (!selectedCodeMaster) {
        setTimeout(function () {
          openAlertBox(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }, 300);
      }
    }
  }

  function onSearchClick() {
    if (hasUnsavedData) {
      openAlertBox(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, 'unsaveddata');
    } else {
      getGridData();
    }
  }

  function getValidationObject(validationData) {
    return validationData.reduce(
      (acc, current) => {
        acc[current.id] = current;
        return acc;
      },
      {});
  }

  const resetAllValue = () => {
    setMasterListName('');
    setRows([]);
    setColumns([]);
  }

  useEffect(() => {
    if (masterListName) {
      getMasterCodeData(masterListName);
    }
    // eslint-disable-next-line
  }, [masterListName]);

  useEffect(() => {
    getCodeMasterList();
  }, []);

  useEffect(() => {
    // validation oject set
  }, [validationObj, dropDownDataList]);

  function refreshGrid() {
    console.log("upgrading the grid again");
    getMasterCodeData(masterListName);
  }

  const handleAlertConfirmation = (e) => {
    setModalShowAlert(false);
    if (e.target.title === "unsaveddata-Warning") {  
      getGridData();
    }
  };

  function openAlertBox(messegeType, messageText = "", type = "") {
    if (type) {
      setActionCase(type);
    }
    setMessageType(messegeType);
    setMessageText(messageText);
    setModalShowAlert(true)
  };

  function addEditDataForParent(add, edit) {
    if (add.length || Object.keys(edit).length) {
      setHasUnsavedData(true);
    } else {
      setHasUnsavedData(false);
    }
  }

  return (
    <>
      <main id="main">
        <div className="container-fluid container-padding">

          <TpexBreadCrumb name={LABEL_CONST.CODE_MASTER_MAINTENANCE} />

          <div className="panelBox">
            <div className="search-panel">
              <div className="row g-0">
                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.CODE_MASTER_MAINTENANCE}</h1></div>
              </div>
              <form>
                <div className="row mt-10">
                  <div className="col-3">
                    <div className="custom-multiSelect mandatoryControl">
                      <label>Code Master</label>
                      <TpexSelect selected="" id="id" hasValue={selectedCodeMaster} onChangeSelection={onChangeSelection} options={codeMasterList} />
                    </div>
                  </div>
                  <div className="col-9 align-self-end">
                    <div className="d-flex justify-content-end">
                      <TpexSimpleButton color="btn btn-primary" handleClick={onSearchClick} text={LABEL_CONST.SEARCH} />
                    </div>
                  </div>
                </div>
              </form>
            </div>

            <div className="grid-panel mt-0">
              {masterListName !== '' ?
                <TpexTable
                  rows={rows}
                  idName={codeMasterPrimaryKey}
                  defaultSortingId="idList"
                  rowPerPage={10}
                  selectAll={false}
                  selectRow={true}
                  columns={columns}
                  moduleName="CODE_MASTER"
                  margin="mt-3"
                  isCrud={true}
                  primaryKey={codeMasterPrimaryKey}
                  pagination={true}
                  filter={true}
                  serverSideFilter={false}
                  refreshGrid={refreshGrid}
                  tableName={tableName}
                  validationObj={validationObj}
                  editTable={true}
                  dropDownData = {dropDownDataList}
                  codeMaster = {selectedCodeMaster}
                  addEditDataForParent={addEditDataForParent}
                  codeMasterName={masterName}
                  codeMasterId={masterListName}
                />
                : ''
                // <p>Select code master from drop down list</p>

              }
            </div>
          </div>
        </div>
          {/* alert modal  */}
        <AlertModal
          handleClick={handleAlertConfirmation}
          show={modalShowAlert}
          onHide={() => setModalShowAlert(false)}
          status={messageType}
          content={messageText}
          parentBtnName={actionCase}
           />
      </main>
    </>
  )
}
export { Codemaster };