import api from './clienteHttpService';

export const usuarioService = {
  alterarMinhaSenha: async ({ senhaAtual, senhaNova, confirmarSenha }) => {
    const response = await api.patch('/api/usuarios/minha-senha', {
      senhaAtual,
      senhaNova,
      confirmarSenha,
    });
    return response.data;
  },
};

export default usuarioService;
