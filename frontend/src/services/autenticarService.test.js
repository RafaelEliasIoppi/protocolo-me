import autenticarService from './autenticarService';
import apiClient from './apiClient';

jest.mock('./apiClient');

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
      apiClient.post.mockResolvedValue(response);

      const result = await autenticarService.login('teste@email.com', 'senha123');

      expect(result).toEqual(response.data);
      expect(localStorage.getItem('token')).toBe('test-token-123');
      expect(apiClient.post).toHaveBeenCalledWith('/api/usuarios/login', {
        email: 'teste@email.com',
        senha: 'senha123'
      });
    });

    it('deve falhar no login com credenciais inválidas', async () => {
      apiClient.post.mockRejectedValue(new Error('Unauthorized'));

      await expect(autenticarService.login('teste@email.com', 'senhaerrada'))
        .rejects.toThrow();
    });
  });

  describe('registrar', () => {
    it('deve registrar novo usuário', async () => {
      const response = {
        data: {
          id: 1,
          email: 'novo@email.com',
          nome: 'Novo Usuario',
          role: 'MEDICO'
        }
      };
      apiClient.post.mockResolvedValue(response);

      const result = await autenticarService.registrar({
        email: 'novo@email.com',
        senha: 'senha123',
        nome: 'Novo Usuario'
      });

      expect(result).toEqual(response.data);
      expect(apiClient.post).toHaveBeenCalledWith('/api/usuarios', expect.objectContaining({
        email: 'novo@email.com',
        nome: 'Novo Usuario'
      }));
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
    it('deve retornar true se token existe', () => {
      localStorage.setItem('token', 'test-token');
      
      expect(autenticarService.isAutenticado()).toBe(true);
    });

    it('deve retornar false se token não existe', () => {
      localStorage.clear();
      
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
