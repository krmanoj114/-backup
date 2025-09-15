import React, { useState, useEffect } from 'react';
import './engine-part-master.css';
import "../../styles/table.css";
import TpexSimpleButton from '../../common/components/button';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { EnginePartTable } from '../../components/shipping-control-master/engine-part-table';
import { TpexSelect } from '../../common/components/select';
import { useDispatch, useSelector } from 'react-redux';
import { getImporterCode, getSearchData } from '../../store/engine_part_master/action';
import { formatPartNo } from '../../helpers/util';
import { showAlert } from '../../store/app/action';

function EnginePartMaster() {
    const dispatch = useDispatch();
    const { importList, data } = useSelector(state => state.enginepartmaster);
    const { currentCompanyCode } = useSelector(state => state.app);

    const [partNumberValue, setPartNumber] = useState("");
    
    const [optionsImp, setOptionsImp] = useState([]);
    const [selectedImpCode, setSelectedImpCode] = useState("");

    const [optionsExp, setOptionsExp] = useState([]);
    const [selectedExpCode, setSelectedExpCode] = useState("");

    const [optionsCarFamily, setCarFamilyOptions] = useState([]);
    const [selectedCarFamilyCode, setSelectedCarFamilyCode] = useState("");

    const [rows, setRows] = useState([]);
    const [modifyFlag, setModifyFlag] = useState(false);
    const pageSize = 10;

    const columns = [
        {
            id: "exporterCode",
            name: "Exporter Code",
            type: "select",
            selectList: optionsExp,
        },
        {
            id: "importerCode",
            name: "Importer Code",
            type: "select",
            selectList: optionsImp,
        },
        {
            id: "carFamilyCode",
            name: "Car Family Code",
            type: "select",
            selectList: optionsCarFamily,
        },
        {
            id: "partNo",
            name: "Part No.",
            type:"text",
            required: "true",
            maxLength: 12
        },
        {
            id: "lotModuleCode",
            name: "Lot/Module Code",
            type:"text",
            maxLength: 2
        }
    ]

    useEffect(() => {
        if(currentCompanyCode) {
            //reset page
            console.log(currentCompanyCode);
        }
    }, [currentCompanyCode]);

    useEffect(() => {
        dispatch(getImporterCode());
    }, []);
    
    useEffect(() => {
        if(importList) {
            setCarFamilyOptions(importList.carFamilyCodeWithCodeDTOs);
            setOptionsImp(importList.destinationNameWithCodeDTO);
            setOptionsExp(importList.destinationNameWithCodeDTO);
        }
    }, [importList])

    useEffect(() => {
        if(data) {
            setRows(data);
        }
    }, [data]);

    function handleCarFamilyCode(e) {
        setSelectedCarFamilyCode(e.target.value);
    }

    function handleExpCode(e) {
        setSelectedExpCode(e.target.value);
    }

    function handleImpCode(e) {
        setSelectedImpCode(e.target.value);
    }

    function getData() {
        const payload = {
            "exporterCode": selectedExpCode,
            "importerCode": selectedImpCode,
            "carFamilyCode": selectedCarFamilyCode,
            "partNo" : partNumberValue
          };
        dispatch(getSearchData(payload));
    }

    //common dropdown handle function
    function handleSearch(e) {
        if(modifyFlag === true){
            dispatch(showAlert({alertStatus: LABEL_CONST.WARNING, alertTitle:"Save Changes", alertContent:LABEL_CONST.SEARCH_MODIFY_WARN, action:null, actionProps:null}));
        } else {
            getData();
        }
    }

    function refreshGrid() {
        getData();
    }
    
    const modifyFld = (e) =>{
        setModifyFlag(e);
    }

    return (
        <>
        {/* spinner */}
            {/* <TpexLoader isLoading={isLoading} /> */}
            <main id="main">
                <div className="container-fluid container-padding">
                    <TpexBreadCrumb name={LABEL_CONST.ENGINE_PART_MASTER} />
                    <div className="panelBox pb-10">
                        <div className="search-panel">
                            <div className="row g-0">
                                <div className="heading"><i className="bg-border"></i><h1>{LABEL_CONST.ENGINE_PART_MASTER}</h1></div>
                            </div>
                            <form>
                                <div className="row mt-10 invoice-maintenance">
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect">
                                            <label>{LABEL_CONST.EXPORTER_CODE}</label>
                                            <TpexSelect
                                            onChangeSelection={handleExpCode}
                                            name="exporterCode_select"
                                            noOptionsText="Select Exporter Code"
                                            hasValue={selectedExpCode}
                                            options={optionsExp}   // required when server side is false
                                        />
                                        </div>
                                    </div>
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect">
                                            <label>{LABEL_CONST.IMPORTER_CODE}</label>
                                            <TpexSelect
                                            onChangeSelection={handleImpCode}
                                            name="importerCode_select"
                                            noOptionsText="Select Importer Code"
                                            hasValue={selectedImpCode}
                                            options={optionsImp}   // required when server side is false
                                        />
                                        </div>
                                    </div>
                                </div>
                                <div className="row mt-10 invoice-maintenance">    
                                    <div className="form-group col-4">
                                        <div className="custom-multiSelect">
                                            <label>{LABEL_CONST.CAR_FAMILY_CODE}</label>
                                            <TpexSelect
                                            onChangeSelection={handleCarFamilyCode}
                                            name="carFamilyCode_select"
                                            noOptionsText="Select Car Family"
                                            hasValue={selectedCarFamilyCode}
                                            options={optionsCarFamily}   // required when server side is false
                                        />
                                        </div>
                                    </div>
                                    <div className="form-group col-4">
                                        <div>
                                            <label htmlFor="partNumber">{LABEL_CONST.PART_NO}</label>
                                            <input
                                                type="text"
                                                className="form-control"
                                                id="partNumber"
                                                name="partNumber"
                                                value={formatPartNo(partNumberValue)}
                                                minLength="1"
                                                maxLength="14"
                                                onChange={event => setPartNumber(event.target.value.replace(/[^0-9a-zA-Z]/g, ''))}
                                            />
                                        </div>
                                    </div>
                                    <div className="form-group col-4 align-self-end">
                                        <div className="d-flex justify-content-end">
                                            <TpexSimpleButton color="primary" text={LABEL_CONST.SEARCH} handleClick={event => handleSearch(event)} topmargin="4" />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div> 
                    </div>
                    {/* table  */}
                    <div className="panelBox mt-10">
                        <div className="grid-panel mt-0">
                            <EnginePartTable
                                rows={rows}
                                idName="idList"
                                primaryKey="idList"
                                defaultSortingId="idList"
                                rowPerPage={pageSize}
                                selectAll={false}
                                selectRow={true}
                                columns={columns}
                                pagination={true}
                                isCrud={true}
                                isCopy={true}
                                setModifyFlag={modifyFld}
                                moduleName="ENGINE_PART_MASTER"
                                refreshGrid={refreshGrid}
                            />
                        </div>
                    </div>
                </div>
            </main>
        </>
    )
}
export {EnginePartMaster};