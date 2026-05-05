import axios from "axios";

const clienteHttp = axios.create({
  baseURL: "", //  ESSENCIAL
  timeout: 10000,
});

export default clienteHttp;
