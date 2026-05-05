import autenticarService from './autenticarService';
import clienteHttpService from './clienteHttpService';

jest.mock('./clienteHttpService');

describe('autenticarService', () => {
  beforeEach(() => {
    localStorage.clear();
    jest.clearAllMocks();
  });

  describe('login', () => {
    it('deve fazer login com sucesso', async () => {
      const response = {
        data: {
          token: 'test-token-123',
          usuario: {
            id: 1,
            email: 'teste@email.com',
            nome: 'Teste Usuario',
            role: 'MEDICO'
          }
        }
      };
      clienteHttpService.post.mockResolvedValue(response);

      const result = await autenticarService.login('teste@email.com', 'senha123');

      expect(result).toEqual(response.data);
      expect(localStorage.getItem('token')).toBe('test-token-123');
      expect(clienteHttpService.post).toHaveBeenCalledWith('/api/usuarios/login', {
        email: 'teste@email.com',
        senha: 'senha123'
      });
    });

    it('deve falhar no login com credenciais inválidas', async () => {
      clienteHttpService.post.mockRejectedValue(new Error('Unauthorized'));

      await expect(autenticarService.login('teste@email.com', 'senhaerrada'))
        .rejects.toThrow();
    });
  });

  describe('logout', () => {
    it('deve remover token do localStorage', () => {
      localStorage.setItem('token', 'test-token');

      autenticarService.logout();

      expect(localStorage.getItem('token')).toBeNull();
    });
  });

  describe('isAutenticado', () => {
    it('deve retornar true se token e usuário com role existem', () => {
      localStorage.setItem('token', 'test-token');
      localStorage.setItem('usuario', JSON.stringify({ role: 'MEDICO' }));

      expect(autenticarService.isAutenticado()).toBe(true);
    });

    it('deve retornar false se token não existe', () => {
      localStorage.clear();

      expect(autenticarService.isAutenticado()).toBe(false);
    });

    it('deve retornar false se token existe sem usuário válido', () => {
      localStorage.setItem('token', 'test-token');

      expect(autenticarService.isAutenticado()).toBe(false);
    });
  });

  describe('obterUsuarioAtual', () => {
    it('deve obter dados do usuário atual', () => {
      const usuarioMock = {
        id: 1,
        email: 'teste@email.com',
        nome: 'Teste',
        role: 'MEDICO'
      };
      localStorage.setItem('usuario', JSON.stringify(usuarioMock));

      const usuario = autenticarService.obterUsuarioAtual();

      expect(usuario).toEqual(usuarioMock);
    });
  });
});
