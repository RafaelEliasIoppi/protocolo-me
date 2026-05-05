import clienteHttpService from './clienteHttpService';
import hospitalService from './hospitalService';

jest.mock('./clienteHttpService');

describe('hospitalService', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('listar', () => {
    it('deve listar todos os hospitais', async () => {
      const hospitais = {
        data: [
          { id: 1, nome: 'Hospital Central', cidade: 'São Paulo', uf: 'SP' },
          { id: 2, nome: 'Hospital Norte', cidade: 'Rio de Janeiro', uf: 'RJ' }
        ]
      };
      clienteHttpService.get.mockResolvedValue(hospitais);

      const result = await hospitalService.listar();

      expect(result).toEqual(hospitais.data);
      expect(clienteHttpService.get).toHaveBeenCalledWith('/api/hospitais');
    });
  });

  describe('obter', () => {
    it('deve obter hospital por ID', async () => {
      const hospital = {
        data: { id: 1, nome: 'Hospital Central', cidade: 'São Paulo' }
      };
      clienteHttpService.get.mockResolvedValue(hospital);

      const result = await hospitalService.obter(1);

      expect(result).toEqual(hospital.data);
      expect(clienteHttpService.get).toHaveBeenCalledWith('/api/hospitais/1');
    });
  });

  describe('criar', () => {
    it('deve criar novo hospital', async () => {
      const novoHospital = { nome: 'Hospital Novo', cidade: 'Brasília', uf: 'DF' };
      const response = { data: { id: 3, ...novoHospital } };
      clienteHttpService.post.mockResolvedValue(response);

      const result = await hospitalService.criar(novoHospital);

      expect(result).toEqual(response.data);
      expect(clienteHttpService.post).toHaveBeenCalledWith('/api/hospitais', novoHospital);
    });
  });

  describe('atualizar', () => {
    it('deve atualizar hospital existente', async () => {
      const hospitalAtualizado = { id: 1, nome: 'Hospital Atualizado', cidade: 'São Paulo' };
      const response = { data: hospitalAtualizado };
      clienteHttpService.put.mockResolvedValue(response);

      const result = await hospitalService.atualizar(1, hospitalAtualizado);

      expect(result).toEqual(response.data);
      expect(clienteHttpService.put).toHaveBeenCalledWith('/api/hospitais/1', hospitalAtualizado);
    });
  });

  describe('deletar', () => {
    it('deve deletar hospital por ID', async () => {
      clienteHttpService.delete.mockResolvedValue({ data: { mensagem: 'Deletado' } });

      await hospitalService.deletar(1);

      expect(clienteHttpService.delete).toHaveBeenCalledWith('/api/hospitais/1');
    });
  });
});
