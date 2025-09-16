import { useState } from "react";
import { TpexLoader } from '../../common/components/loader/loader';
import "../../styles/table.css";
import "./invoice-recalculation.css"
import { getRequest, postRequest } from '../../services/axios-client';
import { MODULE_CONST } from "../../constants/constant";
import TpexSimpleButton from "../../common/components/button";
import { LABEL_CONST } from "../../constants/label.constant.en";
import { TpexBreadCrumb } from "../../common/components/breadcrumb/breadcrumb";
import AlertModal from "../../common/components/alert-modal/alert-modal";
import { TpexMultiSelectSeach } from "../../common/components/multiselect/multiselect";
import { ShippingControlTable } from "../../components/shipping-control-master/shipping-control-table";
import { createMessage } from "../../helpers/util";

function InvoiceRecalculation() {

  const column_Common = [
    { id: 'sno', name: 'S.No', editFlag: false},
    { id: 'cfCode', name: 'CFC', editFlag: false},
    { id: 'cfSeries', name: 'Series Name', editFlag: false },
    { id: 'partNo', name: 'Part No.', editFlag: false},
    { id: 'partName', name: 'Part Name', editFlag: false},
    { id: 'lot', name: 'Lot', editFlag: false},
    { id: 'pakageMonth', name: 'Pkg. Mth', editFlag: false},
  ];  
 
  const column_Privilege = [...column_Common, 
    { id: 'invAico', name: 'Inv. Aico', editFlag: false },
    { id: 'ixosAico', name: 'IXOS Aico', editFlag: false },
    { id: 'incOriginCriteria', name: 'Inv. Origin Criteria', editFlag: false },
    { id: 'ixosOriginCriteria', name: 'IXOS Origin Criteria', editFlag: false },
    { id: 'invHSCode', name: 'Inv. HS CODE', editFlag: false},
    { id: 'ixosHSCode', name: 'IXOS HS CODE', editFlag: false}
  ];
 
  const column_Part_Price = [...column_Common, 
    { id: 'invPartPrice', name: 'Inv. Part Price', editFlag: false},
    { id: 'priceMaster', name: 'Price Master', editFlag: false}  
  ];

  const column_Part_Name = [...column_Common, 
    { id: 'partNameInPriceMaster', name: 'Part Name in Price-Master', editFlag: false},
    { id: 'partNameInPartMaster', name: 'Part Name in Part Master(Ref.)', editFlag: false}
  ];

  const column_Part_Weight = [...column_Common, 
    { id: 'boxSize', name: 'Box size', editFlag: false},
    { id: 'invPartNetWeight', name: 'Inv. Part Net Weight', editFlag: false},
    { id: 'revPartNetWeight', name: 'Rev Part Net Weight(Kg.)', type: 'text', maxLength: 15, editFlag: true, firstLnth: 5, lastLnth: 5},
    { id: 'invBoxtNetWeight', name: 'Inv. Box Weight (kg)(include Part weight)', editFlag: false},
    { id: 'revBoxtNetWeight', name: 'Rev. Inv. Box Weight (kg)(include Part weight)', type: 'text', maxLength: 11, editFlag: true, firstLnth: 5, lastLnth: 3}
  ];

  
  const [columnToDisplay, setColumnToDisplay] = useState(column_Privilege);
  const [rows, setRows] = useState([]);
  const [isLoading, setIsLoading] = useState(false);
  //search panel start
  const [invoiceNumber, setInvoiceNumber] = useState('');  
  const [radioselected, setRadioselected] = useState('recalculate_Privilage');
  const [privilegeType, setPrivilegeType] = useState('PR');
  const [invoiceResult, setInvoiceResult] = useState({});
  const [selectedPartNo, setSelectedPartNo] = useState([]);

  const [alertModalShow, setAlertModalShow] = useState(false);
  const [alertTitle, setAlertTitle] = useState("");
  const [alertContent, setAlertContent] = useState("");
  const [parentBtnName, setParentBtnName] = useState("");
  //search panel end

 const handleRadioChange = (e) => {
    setRadioselected(e.target.value);
    switch (e.target.value) {
      case 'recalculate_Part_Price':
        setPrivilegeType('PP');
        setColumnToDisplay(column_Part_Price);
        setRows([]);
        break;
      case 'recalculate_Part_Name':
        setPrivilegeType('PN');
        setColumnToDisplay(column_Part_Name);
        setRows([]);
        break;
      case 'recalculate_Part_Box_Weight':
        setPrivilegeType('PW');
        setColumnToDisplay(column_Part_Weight);
        setRows([]);
        break;
      default:
        setPrivilegeType('PR');
        setColumnToDisplay(column_Privilege);
        setRows([]);
        break;
    }
  }
  const setInputVal = (fldName)=>{
    return fldName && fldName !== null ? fldName : '';
  }
  const setDetails = (e) => {
    if (e.target.value.length === 10) {
      setIsLoading(true);
      setInvoiceResult({});
      setSelectedPartNo([]);
      getRequest(MODULE_CONST.INVOICE_RECALCULATION.API_BASE_URL,
        MODULE_CONST.INVOICE_RECALCULATION.FETCH_SEARCH_API +
        `?invoiceNumber=${invoiceNumber}&companyCode=TMT`).then(data => {
          if(data.data?.invoiceType ==='P'){data.data.invoiceType='PxP'};
          if(data.data?.invoiceType ==='L'){data.data.invoiceType='LOT'};
          setInvoiceResult(data.data);
        }).catch(function (error) {
          setIsLoading(false);
          showAlertMsg(LABEL_CONST.ERROR, error.message);
        }).finally(() => {
          setIsLoading(false);
      });
    }
  }

  // Search Button Click
  const handleSearch = (e) =>{
    if(invoiceNumber === null || invoiceNumber === undefined || invoiceNumber===''){
      showAlertMsg(LABEL_CONST.ERROR, createMessage(LABEL_CONST.ERR_CM_3001));
    } else if(invoiceResult?.etaFlag === 'N'){
      showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.WARN_INVOICE_RECALCULATION, "Search");
    } else {
        getSearchData();
    }
  }

  const getSearchData = () =>{
      setIsLoading(true);
      let reqPara = {
        "invoiceNumber" : invoiceNumber,
        "partNumber" : selectedPartNo.map(k => k.value),
        "privilege" : privilegeType,
        "companyCode" : "TMT"
    }
    
      postRequest(MODULE_CONST.INVOICE_RECALCULATION.API_BASE_URL,
            MODULE_CONST.INVOICE_RECALCULATION.SEARCH_API, reqPara).then(dataRes => {
          const apiResult = dataRes.data;
          if (apiResult?.length > 0) {
            const datatest = apiResult.map((k, i) => {
                k.idCount = "idCount-"+ (i+1);
                k.idList = (i+1);
                k.sno = (i+1);
                k.calculate = radioselected;
                k.etaFlag = invoiceResult?.etaFlag;
                k.privilegeType = privilegeType;
                k.selectedInvNo = invoiceNumber;
                k.partNoLength = selectedPartNo.length;
                return k;
            })
            setRows(datatest);
          }
          else {
            setRows([]);
            showAlertMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
          }
      }).catch(function (error) {
          setIsLoading(false);
          console.log('getError =>', error.message);
          showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response?.data?.exception));
      }).finally(() => {
        setIsLoading(false);
      });        
      
  }
  //Alert Box messages
  const showAlertMsg = (title, content, btnName) => {
    setAlertTitle(title);
    setAlertContent(content);
    setAlertModalShow(true);
    if (arguments[2] !== undefined || btnName !== undefined) {
      setParentBtnName(btnName);
    } else {
      setParentBtnName('');
    }
  }

  const okConfirm = (e)=>{
      setAlertModalShow(false);
      if(e.target.title==='Search-Warning'){
          getSearchData();
      }
  }

  const refreshGrid = ()=> {
    console.log("upgrading the grid again");
    getSearchData();
}
  return (
    <>
      <TpexLoader isLoading={isLoading} />
      <main id="main">
        <div className="container-fluid container-padding">

          <TpexBreadCrumb name={LABEL_CONST.INVOICE_RECALCULATION} />

          <div className="panelBox">
            <div className="search-panel">
              <div className="row g-0">
                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.INVOICE_RECALCULATION}</h1></div>
              </div>
              <form>
                {/* first row */}
                <div className="row mt-10 pb-2 recalcu-search-panal">
                  <div className="col-2 mandatoryControl">
                    <div>
                      <label>Invoice No.</label>
                      <input
                        type="text"
                        className="form-control"
                        id="invoiceno"
                        name="invoiceno"
                        maxLength="10"
                        value={invoiceNumber}
                        onBlur={(e) => setDetails(e)}
                        onChange={(e) => setInvoiceNumber(e.target.value)}
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div>
                      <label>Type</label>
                      <input
                        type="text"
                        className="form-control"
                        id="type"
                        name="type"
                        disabled
                        value={setInputVal(invoiceResult.invoiceType)}
                      />
                    </div>
                  </div>

                  <div className="col-1">
                    <div className="">
                      <label htmlFor="etd">ETD</label>
                      <input
                        type="text"
                        className="form-control"
                        id="etd"
                        name="etd"
                        disabled
                        value={setInputVal(invoiceResult.etd)}
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div className="">
                      <label htmlFor="eta">ETA</label>
                      <input
                        type="text"
                        className="form-control"
                        id="eta"
                        name="eta"
                        value={setInputVal(invoiceResult.eta)}
                        disabled
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div className="">
                      <label htmlFor="importer">Importer</label>
                      <input
                        type="text"
                        className="form-control"
                        id="importer"
                        name="importer"
                        value={setInputVal(invoiceResult.importerCode)}
                        disabled
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div className="">
                      <label htmlFor="buyer">Buyer</label>
                      <input
                        type="text"
                        className="form-control"
                        id="buyer"
                        name="buyer"
                        value={setInputVal(invoiceResult.buyerCode)}
                        disabled
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div className="">
                      <label htmlFor="privilege">Privilege</label>
                      <input
                        type="text"
                        className="form-control"
                        id="privilege"
                        name="privilege"
                        value={setInputVal(invoiceResult.privilegeCode)}
                        disabled
                      />
                    </div>
                  </div>
                  <div className="col-1">
                    <div className="">
                      <label htmlFor="currency">Currency</label>
                      <input
                        type="text"
                        className="form-control"
                        id="currency"
                        name="currency"
                        value={setInputVal(invoiceResult.currencyCode)}
                        disabled
                      />
                    </div>
                  </div>
                </div>

                {/* 2nd row */}
                <div className="row mt-10 pb-2">
                  <div className="col-4">
                    <div className="custom-multiSelect">
                      <label>Part No.</label>
                      <TpexMultiSelectSeach
                        handleSelectedOptions={(e)=>setSelectedPartNo(e)}
                        name="partNo_dropdown"
                        noOptionsText="Select Part No"
                        value={selectedPartNo}
                        staticValues={invoiceResult.partNumber}   // required when server side is false
                      />
                    </div>
                  </div>
                  <div className="form-group col-6 mt-4">
                  <div className="row">
                    <div className="col-2">
                    <span className="order-type text-nowrap">{LABEL_CONST.RE_CALCULATE}</span>
                    </div>
                    <div className="col-10">
                    <div className="form-check form-check-inline mandatoryControl">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="recalculateRadio"
                        id="recalculate_Privilage"
                        value="recalculate_Privilage"
                        defaultChecked
                        onChange={handleRadioChange}
                      />
                      <label className="form-check-label ordertype-label" htmlFor="recalculate_Privilage">Privilage</label>
                    </div>
                    <div className="form-check form-check-inline mandatoryControl">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="recalculateRadio"
                        id="recalculate_Part_Price"
                        value="recalculate_Part_Price"
                        onChange={handleRadioChange}
                      />
                      <label className="form-check-label ordertype-label" htmlFor="recalculate_Part_Price">Part Price</label>
                    </div>
                    <div className="form-check form-check-inline mandatoryControl">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="recalculateRadio"
                        id="recalculate_Part_Name"
                        value="recalculate_Part_Name"
                        onChange={handleRadioChange}
                      />
                      <label className="form-check-label ordertype-label" htmlFor="recalculate_Part_Name">Part Name</label>
                    </div>
                    <div className="form-check form-check-inline mandatoryControl">
                      <input
                        className="form-check-input"
                        type="radio"
                        name="recalculateRadio"
                        id="recalculate_Part_Box_Weight"
                        value="recalculate_Part_Box_Weight"
                        onChange={(e) => handleRadioChange(e)}
                      />
                      <label className="form-check-label ordertype-label" htmlFor="recalculate_Part_Box_Weight">Part Weight & Box Weight</label>
                      </div>
                      </div>
                    </div>
                  </div>
                  <div className="form-group col-2 align-self-end">
                    <div className="d-flex justify-content-end">
                      <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={event => handleSearch(event)} topmargin="4" />
                    </div>
                  </div>
                </div>
              </form>
            </div>
          </div>
          <div className={`panelBox mt-10 invoiceRecalculation ${radioselected} ${radioselected === 'recalculate_Privilage' ? "chekboxDis" : ""} ${((radioselected === 'recalculate_Privilage') || (radioselected === 'recalculate_Part_Box_Weight')) ? "sticky-table" : ""}`}>
            <div className="grid-panel">
              <ShippingControlTable
                rows={rows}
                idName="idCount"
                primaryKey="idCount"
                defaultSortingId="idList"
                selectAll={true}
                selectRow={true}
                columns={columnToDisplay}
                pagination={true}
                isCrud={radioselected==='recalculate_Part_Box_Weight'}
                moduleName="INVOICE_RECALCULATION"
                refreshGrid={refreshGrid}
              />
            </div>

          </div>
        </div>
        <AlertModal
            show={alertModalShow}
            onHide={() => setAlertModalShow(false)}
            status={alertTitle}
            content={alertContent}
            parentBtnName={parentBtnName}
            handleClick={okConfirm}
        />
      </main>
    </>
  )
}
export { InvoiceRecalculation };