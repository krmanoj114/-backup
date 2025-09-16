import { SHOW_ALERT, HIDE_ALERT, GET_COMPANY_LIST_SUCCESS, CHANGE_COMPANY } from "./actionTypes";

const initialState = {showAlert:false, alertStatus:"", alertTitle:"", alertContent:"", companyList:[], currentCompanyCode:"", action:null, actionProps:null};

const AppReducer = (state = initialState, action) => {
    switch (action.type) {
        case SHOW_ALERT:
            state = {...state, showAlert:true, alertStatus:action.payload.alertStatus, alertTitle:action.payload.alertTitle, alertContent:action.payload.alertContent, action:action.payload.action, actionProps:action.payload.actionProps };
            break;
        case HIDE_ALERT:
            state = {...state, showAlert:false};
            break;
        case GET_COMPANY_LIST_SUCCESS:
            state = {...state, companyList:action.payload.companies, currentCompanyCode:action.payload.companyDefault};
            break;
        case CHANGE_COMPANY:
            console.log(action);
            state = {...state, currentCompanyCode: action.payload};
            break;
        default:
            break;
    }
    return state;
}

export default AppReducer;