import React, { useState, useRef } from 'react';
import "./renbane-group-code-master.css";
import {  getRequest, postRequest } from '../../services/axios-client';
import { ADMIN_SERVICE, MODULE_CONST } from '../../constants/constant';
import { TpexLoader } from '../../common/components/loader/loader';
import { TpexBreadCrumb } from '../../common/components/breadcrumb/breadcrumb';
import { LABEL_CONST } from '../../constants/label.constant.en';
import { TpexMultiSelectSeach } from '../../common/components/multiselect/multiselect';
import TpexSimpleButton from '../../common/components/button';
import RenbaneTable from './RenbaneTable';
import AlertModal from "../../common/components/alert-modal/alert-modal";
import useGetRequest from "../../hook/useRequest";
import { createMessage } from '../../helpers/util';
import GridDataContext from './GridDataContext';

const RenbaneGroundCodeMaster = () => {

    const gridDataContextRef = useRef({});
    
    const [destination, setDestination] = useState(null);
    const [modalShow, setModalShow] = useState(false);
    const [statusTitle, setStatusTitle] = useState("");
    const [statusContent, setstatusContent] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [rows, setRows] = useState([]);
    const [dropDownDataList, setDropDownDataList] = useState([]);
    
    const reqPayload = {
        finalDestCodeAndName: (
            destination !== null ? destination.value : null
        )
    };

    /*
     @@@ Destination + Shiping Company API //Start
    */

    const getAllRequests = useGetRequest(() => getRequest(
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.API_BASE_URL,
        MODULE_CONST.RENBANE_GROUP_CODE_MASTER.RENBANE_CODE_MASTER_DEST)
    );
    
    const initialisedValue = data => {
        return {
            finalDestination: data || []
        };
    };

    const requestData = initialisedValue(getAllRequests?.data?.data);
    const destinationList =  requestData.finalDestination.map((d) => {
        return {
            value: d.fdDstCd,
            label: d.fdDstCd + '-' + d.fdDstNm
        }
    });

    const removeDuplicates = arr => {
        return [...new Set(arr)];
    };

    const finalData = rows.map((item) => {
            
            let groupdIdDetails1 = ( item.goupdIdDetails["1"] !== null  &&  item.goupdIdDetails["1"] !== undefined);
            let groupdIdDetails2 = ( item.goupdIdDetails["2"] !== null  &&  item.goupdIdDetails["2"] !== undefined);
            let groupdIdDetails3 = ( item.goupdIdDetails["3"] !== null  &&  item.goupdIdDetails["3"] !== undefined);
            let groupdIdDetails4 = ( item.goupdIdDetails["4"] !== null  &&  item.goupdIdDetails["4"] !== undefined);
            let groupdIdDetails5 = ( item.goupdIdDetails["5"] !== null  &&  item.goupdIdDetails["5"] !== undefined);

            let renGroupCode1 = groupdIdDetails1 ? item.goupdIdDetails["1"].map(obj => obj.renbanGroupCode) : '';
            renGroupCode1 = removeDuplicates(renGroupCode1).toString();
            let renGroupCode2 = groupdIdDetails2 ? item.goupdIdDetails["2"].map(obj => obj.renbanGroupCode) : '';
            renGroupCode2 = removeDuplicates(renGroupCode2).toString();
            let renGroupCode3 =groupdIdDetails3 ? item.goupdIdDetails["3"].map(obj => obj.renbanGroupCode) : '';
            renGroupCode3 = removeDuplicates(renGroupCode3).toString();
            let renGroupCode4 = groupdIdDetails4 ? item.goupdIdDetails["4"].map(obj => obj.renbanGroupCode) : '';
            renGroupCode4 = removeDuplicates(renGroupCode4).toString();
            let renGroupCode5 = groupdIdDetails5 ? item.goupdIdDetails["5"].map(obj => obj.renbanGroupCode) : '';
            renGroupCode5 = removeDuplicates(renGroupCode5).toString();

            let folderName1 = groupdIdDetails1 ? item.goupdIdDetails["1"].map(obj => obj.folderName) : '';
            folderName1 = removeDuplicates(folderName1).toString();
            let folderName2 =  groupdIdDetails2 ? item.goupdIdDetails["2"].map(obj => obj.folderName) : '';
            folderName2 = removeDuplicates(folderName2).toString();
            let folderName3 = groupdIdDetails3 ? item.goupdIdDetails["3"].map(obj => obj.folderName) : '';
            folderName3 = removeDuplicates(folderName3).toString();
            let folderName4 = groupdIdDetails4 ? item.goupdIdDetails["4"].map(obj => obj.folderName) : '';
            folderName4 = removeDuplicates(folderName4).toString();
            let folderName5 = groupdIdDetails5 ? item.goupdIdDetails["5"].map(obj => obj.folderName) : '';
            folderName5 = removeDuplicates(folderName5).toString();

            return{
                contDstCd : item.contDstCd,
                effectiveFromDt : item.etdFromDate,
                effctiveToDt : item.etdToDate,
                renGroupCode1,
                renGroupCode2,
                renGroupCode3,
                renGroupCode4,
                renGroupCode5,
                folderName1,
                folderName2,
                folderName3,
                folderName4,
                folderName5
            }

            
    });

    let newRows = [...finalData];
    let addIdRows = newRows.map((rowData, index) => ({
        ...rowData,
        id: index,
    }));

    const showStatusMsg = (title, content) => {
        setStatusTitle(title);
        setstatusContent(content);
        setModalShow(true);
    };

    /*
     @@@ Destination + Shiping Company API //End
    */



    const handleSelectedOptions = (e, name) => {
        if (name === "destination") {
            setDestination(e);
        }
    };

    const handleValidation = () => {
        if(destination === null) {
            return false;
        }

        setIsLoading(true);
        return true;
    };  

    const searchDataHandler = (e) => {
        
        if (gridDataContextRef.current.isGridEditable) {
            
            gridDataContextRef.current.isGridEditable = false;
            gridDataContextRef.current.removeAdded = true;
            
        }
        
        if(handleValidation()){
            setRows([]);
            postRequest(
                MODULE_CONST.RENBANE_GROUP_CODE_MASTER.API_BASE_URL,
                MODULE_CONST.RENBANE_GROUP_CODE_MASTER.SEARCH_API,
                reqPayload
            ).then(response => {
                const {renbanData, renbanGroupCodeList} = response.data;
                setDropDownDataList(renbanGroupCodeList);
                if (response.data.renbanData.length > 0) {
                    setRows(renbanData);
                } else {
                    showStatusMsg(LABEL_CONST.INFORMATION, createMessage('INFO_CM_3001'));
                }
                setIsLoading(false);
            }).catch(function (error) {
                showStatusMsg(LABEL_CONST.ERROR, createMessage(error.response.data.exception));
                setIsLoading(false);
            });
        }else{
            showStatusMsg(LABEL_CONST.ERROR, createMessage('ERR_CM_3001'));
        }
    };

    const refreshGrid = () => searchDataHandler();
    
    return (
        <GridDataContext.Provider value={gridDataContextRef}>
            <TpexLoader
                isLoading={getAllRequests.isLoading || isLoading}
            />
            
            <main id="main">
                <div className="container-fluid container-padding">
                    {/* Breadcrumb //Starts*/}
                    <TpexBreadCrumb name='Invoice Masters' />
                    {/* Breadcrumb //End*/}

                    {/* Renbane Group Code Master Container //Starts*/}
                    <div className="panelBox">
                        <div className="search-panel">
                            <div className="col">
                                <div className="heading">
                                    <i className="bg-border"></i>
                                    <h1>Renban Group Code Master</h1>
                                </div>
                            </div>

                            <form>
                                <div className="row mt-10">
                                    <div className="form-group col-10">
                                        <div className="custom-multiSelect mandatoryControl">
                                            <label>{LABEL_CONST.CONTAINER_DESITNATION}</label>
                                            <TpexMultiSelectSeach
                                                isMandatory={true}
                                                searchUrl={''}
                                                handleSelectedOptions={e => handleSelectedOptions(e, 'destination')}
                                                name="destination"
                                                noOptionsText="Select Destination..."
                                                value={destination}
                                                isMulti={false}
                                                id="destinationId"
                                                serverSide={false}
                                                staticValues={destinationList}
                                                BASE_URL={ADMIN_SERVICE}
                                            />
                                        </div>
                                    </div>
                                    
                                    <div className="form-group col-2 align-self-end">
                                        <div className="d-flex justify-content-end pb-10">
                                            <TpexSimpleButton
                                                color="primary"
                                                text={LABEL_CONST.SEARCH}
                                                handleClick={searchDataHandler}
                                                topmargin="4"
                                            />
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    {/* Renbane Group Code Master Container //End*/}
                      <div className="panelBox mt-16">
                        <div className="grid-panel">
                            <RenbaneTable
                                rows={addIdRows}
                                idName="id"
                                moduleName="RENBANE_GROUP_CODE_MASTER"
                                selectAll={false}
                                selectRow={true}
                                rowPerPage={10}
                                isCrud={true}
                                pagination={true}
                                filter={true}
                                serverSideFilter={false}
                                editTable={true}
                                customEdit={true}
                                dropDownData = {dropDownDataList}
                                setDropDownDataList={setDropDownDataList}
                                refreshGrid={refreshGrid}
                                destination={destination}
                            />
                        </div>
                    </div>
                </div>
                
                <AlertModal
                    show={modalShow}
                    onHide={() => setModalShow(false)}
                    status={statusTitle}
                    content={statusContent}
                />
            </main>
        </GridDataContext.Provider>
    )
};

export default RenbaneGroundCodeMaster;