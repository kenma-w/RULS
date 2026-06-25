// src/services/api.js
import axios from 'axios';

const API_URL = 'http://localhost:8080';

const login = (email, password) => {
    return axios.post(`${API_URL}/auth/login`, { email, password });
};

const register = (email, password, name) => {
    return axios.post(`${API_URL}/auth/signup`, { email, password, name });
};

const sendMessage = (message) => {
    return axios.post(`${API_URL}/sendMsg`, message);
};

const getMessages = (paramMap) => {
    const response = axios.post(`${API_URL}/getMsg`, paramMap);
    console.log("Response : ", response);
    return response;
};

const searchUsers = (paramMap) => {
    return axios.post(`${API_URL}/searchUsers`, paramMap);
};

const getRecent = (paramMap) => {
    return axios.post(`${API_URL}/getRecent`, paramMap);
};

export { login, register, sendMessage, getMessages, searchUsers, getRecent };
