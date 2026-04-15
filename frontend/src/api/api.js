import axios from "axios";

// Detectar URL do backend dinamicamente
const backendURL = process.env.REACT_APP_API_URL || 
  `https://${window.location.hostname}:2500`;

const api = axios.create({
  baseURL: `${backendURL}/api`,
});

export default api;
