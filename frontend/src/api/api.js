import axios from "axios";

const baseURL =
  process.env.REACT_APP_API_URL || `${window.location.origin}/api`;

const api = axios.create({
  baseURL,
  timeout: 10000,
});

export default api;
