import { formatPartNo } from "../../helpers/util";
import { GET_IMPORTER_CODE_SUCCESS, GET_SEARCH_DATA_SUCCESS } from "./actionTypes";

const initialState = {importList:null, data: null};

const EnginePartMasterReducer = (state = initialState, action) => {
    switch (action.type) {
        case GET_SEARCH_DATA_SUCCESS:
            state = {...state, data: formatSearchData(action.payload.exporterImporterDataDTO, state.importList)};
            break;
        case GET_IMPORTER_CODE_SUCCESS:
            state = {...state, importList:formatImporterCodes(action.payload)}
            break;
        default:
            state = {...state};
            break;
    }
    return state;
}

function formatSearchData(data, importList) {
    for (let item1 of data) {
        item1.carFamilyCode = {id:item1.carFamilyCode, label: getLabelFromCode(importList.carFamilyCodeWithCodeDTOs, item1.carFamilyCode)};
        item1.exporterCode = {id:item1.exporterCode, label: getLabelFromCode(importList.destinationNameWithCodeDTO, item1.exporterCode)};
        item1.importerCode = {id:item1.importerCode, label: getLabelFromCode(importList.destinationNameWithCodeDTO, item1.importerCode)};
        item1.partNo = {id:item1.partNo, label: formatPartNo(item1.partNo)};
        item1.lotModuleCode = {id:item1.lotModuleCode, label: formatLOTModuleCode(item1.lotModuleCode)};
    }
    data.map((k, i) => {
        k.idList = (i + 1).toString();
        //payment term master conditional mandatory
        return k;
    });
    return data;
}

function getLabelFromCode(source, code) {
    for (let item1 of source) {
        if(item1.id === code) {
            return item1.name;
        }
    }
    return "";
}


function formatLOTModuleCode(label) {
    if(label !== "**")
        return "**";
    return label;
}

function formatImporterCodes(data) {
    data.carFamilyCodeWithCodeDTOs.map(d => {return {value: d.id, label: d.name, id: d.id, name: d.name}});
    data.destinationNameWithCodeDTO.map(d =>{ return {value: d.id, label: d.name, name:d.name, id: d.id}});
    return data;
}

export default EnginePartMasterReducer;