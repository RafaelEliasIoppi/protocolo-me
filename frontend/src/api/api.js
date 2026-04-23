import axios from "axios";

const baseURL =
  process.env.REACT_APP_API_URL || "http://localhost:2500";

const api = axios.create({
  baseURL,
  timeout: 10000,
});

export default api;