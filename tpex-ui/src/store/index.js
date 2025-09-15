import createSagaMiddleware from "redux-saga";
import rootReducer from "./reducers.js";
import rootSaga from "./sagas.js";
import { configureStore } from "@reduxjs/toolkit";

const sagaMiddleware = createSagaMiddleware();

const store = configureStore({reducer:rootReducer, middleware: [sagaMiddleware]})
sagaMiddleware.run(rootSaga);

export default store;