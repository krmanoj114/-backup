import {
    CHANGE_COMPANY,
    GET_COMPANY_LIST,
    GET_COMPANY_LIST_SUCCESS,
    HIDE_ALERT,
    SHOW_ALERT
} from "./actionTypes";

export const showAlert = payload => {
    return {
        type: SHOW_ALERT,
        payload
    };
}

export const hideAlert = () => {
    return {
        type: HIDE_ALERT
    };
}

export const getCompanyList = payload => {
    return {
        type: GET_COMPANY_LIST,
        payload
    };
}

export const getCompanyListSuccess = payload => {
    return {
        type: GET_COMPANY_LIST_SUCCESS,
        payload
    };
}

export const changeCompany = payload => {
    return {
        type: CHANGE_COMPANY,
        payload
    };
}