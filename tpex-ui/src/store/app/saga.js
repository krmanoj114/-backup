import { call, put, takeLatest } from "redux-saga/effects";
import { GET_COMPANY_LIST } from "./actionTypes";
import { getRequestR } from "../../services/axios-client";
import { getCompanyListSuccess } from "./action";
import { COMPANY_LIST_API, TPEX_ADMIN_SERVICE } from "../../constants/URLHelper";

function* onGetCompanyList({payload}) {
    try {
        const response = yield call(getRequestR, TPEX_ADMIN_SERVICE + COMPANY_LIST_API + "?userId=" + payload.userId);
        if(response.status.toString() === "200") {
            yield put(getCompanyListSuccess(response.data));
        }
        else {
            console.log("Error");
        }
    }
    catch (error) {
        console.log(error);
    }
}


function* AppSaga() {
    yield takeLatest(GET_COMPANY_LIST, onGetCompanyList);
}

export default AppSaga;