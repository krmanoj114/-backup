import { TpexLoader } from '../../common/components/loader/loader';
import React, { useState, useEffect } from 'react';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import TpexSimpleButton from '../../common/components/button';
import { getRequest } from '../../services/axios-client';
import { ShippingControlTable } from '../../components/shipping-control-master/shipping-control-table';
import './mix-privilege-master.css';
import { ADMIN_SERVICE, MODULE_CONST } from '../../constants/constant';
import AlertModal from '../../common/components/alert-modal/alert-modal';
import { createMessage } from '../../helpers/util';

export function MixPrivilegeMaster() {

    const [isLoading, setIsLoading] = useState(false);
    const [destinationList, setDestinationList] = useState([]);
    const [carFamilyList, setCarFamilyList] = useState([]);
    const [carFamily, setCarFamily] = useState([]);
    const [destination, setDestination] = useState([]);
    const [rows, setRows] = useState([]);

    const [alertModalShow, setAlertModalShow] = useState(false);
    const [alertTitle, setAlertTitle] = useState("");
    const [alertContent, setAlertContent] = useState("");
    const [parentBtnName, setParentBtnName] = useState("");
    
    const [exportList, setExportList] = useState([]);
    const [priorityList, setPriorityList] = useState([]);
    const [modifyFlg, setModifyFlg] = useState(false);

    const [isSearchBtnClick, setIsSearchBtnClick] = useState(null);
    const pageSize = 10;
    const [validationObj, setValidationObj] = useState(null);
    // Table grid data
    const columns = [
        { id: 'reExporterCode', name: 'Re-Export Code', type: 'select', isBlank: true,  required: true, editFlag: false, selectList: exportList },
        { id: 'effFrom', name: 'Van Date From', type: 'date', required: true, editFlag: true, date: true },
        { id: 'effTo', name: 'Van Date To', type: 'date', required: true, editFlag: true, date: true },
        { id: 'priorityOne', name: 'Priority 1', type: 'multiselect', required: true, isMulti: true, editFlag: true, multiSelectList: priorityList },
        { id: 'priorityTwo', name: 'Priority 2', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: priorityList },
        { id: 'priorityThree', name: 'Priority 3', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: priorityList },
        { id: 'priorityFour', name: 'Priority 4', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: priorityList },
        { id: 'priorityFive', name: 'Priority 5', type: 'multiselect', isMulti: true, editFlag: true, multiSelectList: priorityList }
    ];

    const okConfirm = (e) => {
        setAlertModalShow(false);
        if(e.target.title==='Search-Warning'){
            getSearchData(carFamily.value, destination.value);
        }
    };

    const modifyFld = (e) =>{
        setModifyFlg(e);
    }

    //Alert Box messages
    function showAlertMsg(title, content, btnName){
        setAlertTitle(title);
        setAlertContent(content);
        setAlertModalShow(true);
        if(arguments[2] !== undefined || btnName !== undefined){            
            setParentBtnName(btnName);
        } else {
            setParentBtnName('');
        }
    }

    function getCarFamilyDestinations() {
        setIsLoading(true);
        getRequest(MODULE_CONST.MIX_PRIVILEGE_MASTER.API_BASE_URL, MODULE_CONST.MIX_PRIVILEGE_MASTER.CAR_FAMILY_DEST).then(dataRes => {

            setDestinationList(dataRes.data.destinations.map(d => {
                return {
                    value: d.fdDstCd,
                    label: d.fdDstCd + '-' + d.fdDstNm
                }
            }));

            setCarFamilyList(dataRes.data.carFmly.map(d => {
                return {
                    value: d.carFmlyCode,
                    label: d.carFmlyCode + '-' + d.carFmlyName
                }
            }));

            setIsLoading(false);

        }).catch(function (error) {
            console.log('getCarFamilyDestinations =>', error.message);
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.message));
            setIsLoading(false);
        });
    }


    useEffect(() => {
        getCarFamilyDestinations();
    }, []);

    function refreshGrid() {
        console.log("upgrading the grid again");
        getSearchData(carFamily.value, destination.value);
    }

    function handleSelectedOptions(e, name) {
        setIsLoading(true);
        if (name === "destination") {
            setDestination(e);
            setIsLoading(false);
        }

        if (name === "carFamily") {
            setCarFamily(e);
            setIsLoading(false);
        }

    }
    function handleValidation() {
        const destSelected = destination ? destination.value : "";
        const carFamilySelected = carFamily ? carFamily.value : "";
        if (!carFamilySelected || !destSelected) {
            return false;
        }
        setIsLoading(false);
        return true;
    }
    function filterItem (list, filter) {
        let result = [];
            for (let i of list) {
                for (let prop of filter) {
                    if (i.label === prop) {
                        result.push(i);
                    }
                }
            }
        return result;
    }
    function getSearchData(carFamily, destination){
        setIsLoading(true);
        setModifyFlg(false);
        setIsSearchBtnClick(false);
        getRequest(MODULE_CONST.MIX_PRIVILEGE_MASTER.API_BASE_URL,
                MODULE_CONST.MIX_PRIVILEGE_MASTER.GRID_API+`?crFmlyCode=${carFamily}&destCode=${destination}`).then(dataRes => {
            const apiResult = dataRes.data;                
            const priorityListData = apiResult.priority.map(d => {
                return {
                    value: d.value,
                    label: d.label + ' ' + d.value
                }
            });
            if (apiResult.mixPrivilegeDetails.length) {
            const datatest = apiResult.mixPrivilegeDetails.map((k, i) => {
                k.idCount = "idCount-"+ (i+1);
                k.idList = (i+1);
                const selValsOne = filterItem(priorityListData, k.priorityOne);
                k.priorityOneObj = selValsOne;

                const selValsTwo = filterItem(priorityListData, k.priorityTwo);
                k.priorityTwoObj = selValsTwo;

                const selValsThree = filterItem(priorityListData, k.priorityThree);
                k.priorityThreeObj = selValsThree;

                const selValsFour = filterItem(priorityListData, k.priorityFour);
                k.priorityFourObj = selValsFour;

                const selValsFive = filterItem(priorityListData, k.priorityFive);
                k.priorityFiveObj = selValsFive;

                return k;
            })
            setRows(datatest);
            } else {
                setRows([]);
                showAlertMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
            }

            setExportList(apiResult.reExporterCode.map(d => {
                return {
                    id: d.id,
                    name: d.id !== '*' ? d.id + '-' + d.name : d.name
                }
            }));
            setPriorityList(priorityListData);
            setIsSearchBtnClick(true);
            const valObj = getValidationObject(columns)
            setValidationObj(valObj);
            setIsLoading(false);
        }).catch(function (error) {
            console.log('result grid', error);
            showAlertMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
            setIsLoading(false);
        });
    }
    function getValidationObject(validationData) {
        return validationData.reduce(
          (acc, current) => {
            acc[current.id] = current;
            return acc;
          },
          {});
      }
    const searchClick = () => {
        if (!handleValidation()) {
            showAlertMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        } else if(modifyFlg === true){
            showAlertMsg(LABEL_CONST.WARNING, LABEL_CONST.SEARCH_MODIFY_WARN, "Search");
        } else {
            getSearchData(carFamily.value, destination.value);
        }
    }

    return (
        <>        
            <TpexLoader isLoading={isLoading} />
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.MIX_PRIVILEGE_MASTER} />
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.MIX_PRIVILEGE_MASTER}</h1></div>
                            </div>
                            <form>
                                {/* CAR FAMILY / DESTINATION / FROM MONTH */}
                                <div className="row mt-10 pb-2">                                    
                                    <div className="col-6">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="IMPORTER_CODE">{LABEL_CONST.IMPORTER_CODE}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                                                name="IMPORTER_CODE"
                                                noOptionsText="Search..."
                                                value={destination}
                                                isMulti={false}
                                                id="IMPORTER_CODE_ID"
                                                serverSide={false}
                                                staticValues={destinationList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    <div className="col-3">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label htmlFor="carFamily">{LABEL_CONST.CAR_FAMILY_CODE}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'carFamily')}
                                                name="carFamily"
                                                noOptionsText="Search..."
                                                value={carFamily}
                                                isMulti={false}
                                                id="carFamily"
                                                serverSide={false}
                                                staticValues={carFamilyList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>

                                    <div className="col-3 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={searchClick} />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* table  */}
                    <div className="panelBox mt-10">
                        <div className="grid-panel mt-0">
                                <ShippingControlTable 
                                    rows={rows}
                                    idName="idCount"
                                    primaryKey="idCount"
                                    defaultSortingId="idList"
                                    rowPerPage={pageSize}
                                    selectAll={false}
                                    selectRow={true}
                                    columns={columns}
                                    pagination={true}
                                    isCrud={true}
                                    setModifyFlag={modifyFld}
                                    moduleName="MIX_PRIVILEGE_MASTER"
                                    refreshGrid={refreshGrid}
                                    isSearchBtnClick={isSearchBtnClick}
                                    validationObj={validationObj}
                                />

                        </div>
                    </div>
                </div>
                {/* alert modal  */}
                <AlertModal
                    show={alertModalShow}
                    onHide={() => setAlertModalShow(false)}
                    status={alertTitle}
                    content={alertContent}
                    handleClick={okConfirm}
                    parentBtnName={parentBtnName}
                />
            </main>
        </>        
    )
}
