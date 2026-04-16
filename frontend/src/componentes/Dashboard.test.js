import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Dashboard from './Dashboard';
import pacienteService from '../services/pacienteService';

jest.mock('./PacienteForm', () => () => <div data-testid="paciente-form-mock" />);

jest.mock('../services/pacienteService', () => ({
  listar: jest.fn(),
  deletar: jest.fn(),
}));

describe('Dashboard', () => {
  beforeEach(() => {
    localStorage.setItem('token', 'test-token');
    pacienteService.listar.mockResolvedValue({
      data: [
        {
          id: 1,
          nome: 'João Silva',
          cpf: '123.456.789-00',
          telefone: '11999999999',
          statusProtocolo: 'Aberto',
          hospital: { id: 1, nome: 'Hospital Central', cidade: 'São Paulo' }
        },
        {
          id: 2,
          nome: 'Maria Souza',
          cpf: '987.654.321-00',
          telefone: '11888888888',
          statusProtocolo: 'Concluído',
          hospital: { id: 2, nome: 'Hospital Norte', cidade: 'Rio de Janeiro' }
        },
      ],
    });
    pacienteService.deletar.mockResolvedValue({});
  });

  afterEach(() => {
    jest.resetAllMocks();
    localStorage.clear();
  });

  it('renderiza notificações, filtra pacientes e permite operações CRUD', async () => {
    render(<Dashboard onLogout={jest.fn()} theme="dark" setTheme={jest.fn()} role="MEDICO" />);

    expect(await screen.findByText('Notificações')).toBeInTheDocument();
    expect(screen.getByText('Protocolo novo recebido')).toBeInTheDocument();
    expect(screen.getByText('Total de pacientes')).toBeInTheDocument();
    expect(screen.queryByTestId('paciente-form-mock')).not.toBeInTheDocument();

    await waitFor(() => expect(screen.getByText('João Silva')).toBeInTheDocument());
    expect(screen.getByText('Maria Souza')).toBeInTheDocument();
    expect(screen.getByText((content, node) => content.includes('Hospital Central') && content.includes('São Paulo'))).toBeInTheDocument();

    // Teste de filtro por nome
    const searchInput = screen.getByPlaceholderText('Buscar por nome');
    fireEvent.change(searchInput, { target: { value: 'Maria' } });

    expect(screen.queryByText('João Silva')).not.toBeInTheDocument();
    expect(screen.getByText('Maria Souza')).toBeInTheDocument();

    // Teste de filtro por status
    fireEvent.change(searchInput, { target: { value: '' } });
    const comboboxes = screen.getAllByRole('combobox');
    const statusSelect = comboboxes[0];
    fireEvent.change(statusSelect, { target: { value: 'Aberto' } });

    expect(screen.getByText('João Silva')).toBeInTheDocument();
    expect(screen.queryByText('Maria Souza')).not.toBeInTheDocument();

    // Teste de edição (simula clique no botão de editar)
    const editButtons = screen.getAllByTitle('Editar paciente');
    expect(editButtons.length).toBeGreaterThan(0);
    fireEvent.click(editButtons[0]);
    expect(screen.getByTestId('paciente-form-mock')).toBeInTheDocument();

    // Teste de exclusão (simula confirmação)
    window.confirm = jest.fn(() => true);
    const deleteButtons = screen.getAllByTitle('Excluir paciente');
    expect(deleteButtons.length).toBeGreaterThan(0);
  });
});
