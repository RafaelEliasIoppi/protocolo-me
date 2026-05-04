import api from "./apiClient";

const TOKEN_KEY = "token";
const USER_KEY = "usuario";

const salvarSessao = (data) => {
  if (data?.token) {
    localStorage.setItem(TOKEN_KEY, data.token);
  }
  if (data?.usuario) {
    localStorage.setItem(USER_KEY, JSON.stringify(data.usuario));
  }
};

const limparSessao = () => {
  localStorage.removeItem(TOKEN_KEY);
  localStorage.removeItem(USER_KEY);
};

export const autenticarService = {

  // =========================
  // AUTH
  // =========================
  async login(email, senha) {
    const response = await api.post("/api/usuarios/login", { email, senha });

    salvarSessao(response.data);

    return response.data;
  },

  async registrarAdmin(usuario) {
    const response = await api.post("/api/usuarios/admin/registrar", usuario);
    return response.data;
  },

  logout() {
    limparSessao();
  },

  // =========================
  // USUÁRIO
  // =========================
  async listarUsuarios() {
    const response = await api.get("/api/usuarios");
    return response.data;
  },

  async atualizarUsuario(id, usuario) {
    const response = await api.put(`/api/usuarios/${id}`, usuario);
    return response.data;
  },

  async redefinirSenha(id, senhaNova) {
    const response = await api.patch(`/api/usuarios/${id}/senha`, { senhaNova });
    return response.data;
  },

  async alterarMinhaSenha(senhaAtual, senhaNova, confirmarSenha) {
    const response = await api.patch("/api/usuarios/minha-senha", {
      senhaAtual,
      senhaNova,
      confirmarSenha,
    });
    return response.data;
  },

  // =========================
  // SESSÃO
  // =========================
  obterUsuarioAtual() {
    try {
      const usuario = localStorage.getItem(USER_KEY);
      return usuario ? JSON.parse(usuario) : null;
    } catch {
      limparSessao();
      return null;
    }
  },

  obterToken() {
    return localStorage.getItem(TOKEN_KEY);
  },

  isAutenticado() {
    const token = this.obterToken();
    const usuario = this.obterUsuarioAtual();
    return !!token && !!usuario;
  },

  isAdmin() {
    const usuario = this.obterUsuarioAtual();
    return usuario?.role === "ADMIN";
  },

  isMedico() {
    const usuario = this.obterUsuarioAtual();
    return usuario?.role === "MEDICO";
  },

  isEnfermeiro() {
    const usuario = this.obterUsuarioAtual();
    return usuario?.role === "ENFERMEIRO";
  }
};

export default autenticarService;
