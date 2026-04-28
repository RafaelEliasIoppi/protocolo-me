import axios from "axios";

const api = axios.create({
  baseURL: "", // 🔥 ESSENCIAL
  timeout: 10000,
});

export default api;
