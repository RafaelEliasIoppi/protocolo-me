import clienteHttpService from './clienteHttpService';
import pacienteService from './pacienteService';

jest.mock('./clienteHttpService');

describe('pacienteService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('listar', () => {
    it('deve listar todos os pacientes', async () => {
      const pacientes = {
        data: [
          { id: 1, nome: 'João', cpf: '123.456.789-00' },
          { id: 2, nome: 'Maria', cpf: '987.654.321-00' }
        ]
      };
      clienteHttpService.get.mockResolvedValue(pacientes);

      const result = await pacienteService.listar();

      expect(result).toEqual(pacientes.data);
      expect(clienteHttpService.get).toHaveBeenCalledWith('/api/pacientes');
    });
  });

  describe('obter', () => {
    it('deve obter paciente por ID', async () => {
      const paciente = {
        data: { id: 1, nome: 'João', cpf: '123.456.789-00' }
      };
      clienteHttpService.get.mockResolvedValue(paciente);

      const result = await pacienteService.obter(1);

      expect(result).toEqual(paciente.data);
      expect(clienteHttpService.get).toHaveBeenCalledWith('/api/pacientes/1');
    });
  });

  describe('criar', () => {
    it('deve criar novo paciente', async () => {
      const novoPaciente = { nome: 'Novo', cpf: '111.111.111-11' };
      const response = {
        data: { id: 3, ...novoPaciente }
      };
      clienteHttpService.post.mockResolvedValue(response);

      const result = await pacienteService.criar(novoPaciente);

      expect(result).toEqual(response.data);
      expect(clienteHttpService.post).toHaveBeenCalledWith('/api/pacientes', novoPaciente);
    });
  });

  describe('atualizar', () => {
    it('deve atualizar paciente existente', async () => {
      const pacienteAtualizado = { id: 1, nome: 'João Atualizado', cpf: '123.456.789-00' };
      const response = { data: pacienteAtualizado };
      clienteHttpService.put.mockResolvedValue(response);

      const result = await pacienteService.atualizar(1, pacienteAtualizado);

      expect(result).toEqual(response.data);
      expect(clienteHttpService.put).toHaveBeenCalledWith('/api/pacientes/1', pacienteAtualizado);
    });
  });

  describe('deletar', () => {
    it('deve deletar paciente por ID', async () => {
      clienteHttpService.delete.mockResolvedValue({ data: { mensagem: 'Deletado' } });

      await pacienteService.deletar(1);

      expect(clienteHttpService.delete).toHaveBeenCalledWith('/api/pacientes/1');
    });
  });

  describe('atualizarStatus', () => {
    it('deve atualizar status do paciente', async () => {
      const response = { data: { id: 1, statusProtocolo: 'Concluído' } };
      clienteHttpService.patch.mockResolvedValue(response);

      const result = await pacienteService.atualizarStatus(1, 'Concluído');

      expect(result).toEqual(response.data);
      expect(clienteHttpService.patch).toHaveBeenCalledWith('/api/pacientes/1/status', { status: 'Concluído' });
    });
  });
});
