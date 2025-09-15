import { GET_IMPORTER_CODE, GET_IMPORTER_CODE_FAILED, GET_IMPORTER_CODE_SUCCESS, GET_SEARCH_DATA, GET_SEARCH_DATA_FAILED, GET_SEARCH_DATA_SUCCESS } from "./actionTypes";

export const getSearchData = (payload) => {
    return {type:GET_SEARCH_DATA, payload};
}

export const getSearchDataSuccess = (payload) => {
    return {type: GET_SEARCH_DATA_SUCCESS, payload}
}

export const  getSearchDataFailed = (payload) => {
    return {type: GET_SEARCH_DATA_FAILED, payload}
}

export const getImporterCode = () => {
    return {type:GET_IMPORTER_CODE};
}

export const getImporterCodeSuccess = (payload) => {
    return {type: GET_IMPORTER_CODE_SUCCESS, payload}
}

export const  getImporterCodeFailed = (payload) => {
    return {type: GET_IMPORTER_CODE_FAILED, payload}
}