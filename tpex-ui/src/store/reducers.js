import { combineReducers } from "redux";

import AppReducer from "./app/reducer";
import EnginePartMasterReducer from "./engine_part_master/reducer";

const rootReducer = combineReducers({app:AppReducer, enginepartmaster:EnginePartMasterReducer});

export default rootReducer;