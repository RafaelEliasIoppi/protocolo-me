import React from 'react';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import Dashboard from './Dashboard';
import api from '../api/api';

jest.mock('../api/api', () => ({
  get: jest.fn(),
}));

describe('Dashboard', () => {
  beforeEach(() => {
    localStorage.setItem('token', 'test-token');
    api.get.mockResolvedValue({
      data: [
        { id: 1, nome: 'João Silva', cpf: '123.456.789-00', statusProtocolo: 'Aberto' },
        { id: 2, nome: 'Maria Souza', cpf: '987.654.321-00', statusProtocolo: 'Concluído' },
      ],
    });
  });

  afterEach(() => {
    jest.resetAllMocks();
    localStorage.clear();
  });

  it('renderiza notificações e permite filtrar pacientes', async () => {
    render(<Dashboard onLogout={jest.fn()} theme="dark" setTheme={jest.fn()} />);

    expect(await screen.findByText('Notificações')).toBeInTheDocument();
    expect(screen.getByText('Protocolo novo recebido')).toBeInTheDocument();
    expect(screen.getByText('Total de pacientes')).toBeInTheDocument();

    await waitFor(() => expect(screen.getByText('João Silva')).toBeInTheDocument());
    expect(screen.getByText('Maria Souza')).toBeInTheDocument();

    const searchInput = screen.getByPlaceholderText('Buscar por nome');
    fireEvent.change(searchInput, { target: { value: 'Maria' } });

    expect(screen.queryByText('João Silva')).not.toBeInTheDocument();
    expect(screen.getByText('Maria Souza')).toBeInTheDocument();

    const statusSelect = screen.getByRole('combobox');

    fireEvent.change(searchInput, { target: { value: '' } });
    fireEvent.change(statusSelect, { target: { value: 'Aberto' } });

    expect(screen.getByText('João Silva')).toBeInTheDocument();
    expect(screen.queryByText('Maria Souza')).not.toBeInTheDocument();
  });
});
