import axios from "axios";

const baseURL =
  process.env.REACT_APP_API_URL ||
  "https://bug-free-fortnight-q7p49pgr9qxfxxx-2500.app.github.dev";

const api = axios.create({
  baseURL,
  timeout: 10000,
});

// 🔐 Token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");

    console.log("➡️ URL:", baseURL);
    console.log("➡️ TOKEN:", token);

    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// 🚨 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    console.error("❌ ERRO:", error.response);

    const status = error.response?.status;
    const isLoginRequest = error.config?.url?.includes("/api/usuarios/login");

    if (status === 401 && !isLoginRequest) {
      localStorage.removeItem("token");
      localStorage.removeItem("usuario");
      window.location.href = "/login";
    }

    return Promise.reject(error);
  }
);

export default api;