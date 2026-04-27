import axios from "axios";

// Se REACT_APP_API_URL estiver definido, usa ele.
// Caso contrário, usa window.location.origin + "/api"
const baseURL = process.env.REACT_APP_API_URL
  ? process.env.REACT_APP_API_URL
  : `${window.location.origin}/api`;

const api = axios.create({
  baseURL,
  timeout: 10000,
});

export default api;
