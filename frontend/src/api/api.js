import axios from "axios";

const baseURL = process.env.REACT_APP_API_URL || window.location.origin;

const api = axios.create({
  baseURL: `${baseURL}/api`,
  timeout: 10000,
});

export default api;
