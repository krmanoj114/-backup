import { call, put, takeLatest } from "redux-saga/effects";
import { getImporterCodeSuccess, getSearchDataSuccess } from "./action";
import { getRequestR, postRequestR } from "../../services/axios-client";
import { showAlert } from "../../store/app/action";

const { EPM_IMPORTER_CODE_API, TPEX_DAILY_SERVICE, EPM_SEARCH_API } = require("../../constants/URLHelper");
const { GET_IMPORTER_CODE, GET_SEARCH_DATA } = require("./actionTypes");

function* onSearchData(payload) {
    const response = yield call(postRequestR, TPEX_DAILY_SERVICE + EPM_SEARCH_API, payload);
    if(response.status.toString() === "200") {
        yield put(getSearchDataSuccess(response.data));
    }
    else {
        yield put(showAlert());
    }
}

function* onGetImporterCode() {
    const response = yield call(getRequestR, TPEX_DAILY_SERVICE + EPM_IMPORTER_CODE_API);
    if(response.status.toString() === "200") {
        yield put(getImporterCodeSuccess(response.data));
    }
    else {
        yield put(showAlert());
    }
}

function* EnginePartMasterSaga() {
    yield takeLatest(GET_SEARCH_DATA, onSearchData);
    yield takeLatest(GET_IMPORTER_CODE, onGetImporterCode);
}

export default EnginePartMasterSaga;