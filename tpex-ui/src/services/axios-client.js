import axios from 'axios';
import { API_TIMEOUT, API_WITH_CREDENTIAL } from '../constants/constant';
import { API_BASE_URL } from '../constants/URLHelper';

const axiosClient = axios.create();

axiosClient.defaults.headers = {
    'Content-Type': 'application/json',
    Accept: 'application/json'
};

axiosClient.defaults.timeout = API_TIMEOUT;

axiosClient.defaults.withCredentials = API_WITH_CREDENTIAL;

axiosClient.defaults.baseURL = API_BASE_URL;

export function getRequest(BASE_URL, URL) {
    setBaseURL(BASE_URL);
    return axiosClient.get(`/${URL}`).then(response => response);
}

export function getRequestR(URL) {
    axiosClient.defaults.baseURL = API_BASE_URL;
    return axiosClient.get(`/${URL}`).then(response => response);
}

export function postRequest(BASE_URL, URL, payload) {
    setBaseURL(BASE_URL);
    return axiosClient.post(`/${URL}`, payload).then(response => response);
}

export function postRequestR(URL, payload) {
    return axiosClient.post(`/${URL}`, payload).then(response => response);
}

export function patchRequest(BASE_URL, URL, payload) {
    setBaseURL(BASE_URL);
    return axiosClient.patch(`/${URL}`, payload).then(response => response);
}

export function deleteRequest(BASE_URL, URL, payload) {
    setBaseURL(BASE_URL);
    return axiosClient.delete(`/${URL}`, payload).then(response => response);
}

export function getFileRequest(BASE_URL, URL) {
    setBaseURL(BASE_URL);
    return axiosClient.get(`/${URL}`, {
        responseType: "blob",
    })
}

export function postFileRequest(BASE_URL, URL, payload) {
    setBaseURL(BASE_URL);
    return axiosClient.post(`/${URL}`, payload, {
        responseType: "blob"
    })
}

function setBaseURL(BASE_URL) {
    axiosClient.defaults.baseURL = BASE_URL;
}

export function putRequest(BASE_URL, URL, payload) {
    setBaseURL(BASE_URL);
    return axiosClient.put(`/${URL}`, payload).then(response => response);
}

export function getRequestWithParams(BASE_URL, URL, params) {
    setBaseURL(BASE_URL);
    return axiosClient.get(`/${URL}`, params).then(response => response);
}

export const tempGetRequest = url => {
    return axiosClient.get(url);
};