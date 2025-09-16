import { all, fork } from "redux-saga/effects";

import AppSaga from "./app/saga";
import EnginePartMasterSaga from "./engine_part_master/saga";

export default function* rootSaga(){
     yield all([fork(AppSaga)]);
     yield all([fork(EnginePartMasterSaga)]);
}