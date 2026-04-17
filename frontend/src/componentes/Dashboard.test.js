import React from "react";
import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import Dashboard from "./Dashboard";
import apiClient from "../services/apiClient";
import pacienteService from "../services/pacienteService";

jest.mock("../services/apiClient", () => ({
  get: jest.fn(),
}));

jest.mock("../services/pacienteService", () => ({
  listar: jest.fn(),
}));

describe("Dashboard", () => {
  beforeEach(() => {
    jest.clearAllMocks();

    pacienteService.listar.mockResolvedValue({
      data: [
        {
          id: 1,
          nome: "Joao Silva",
          status: "INTERNADO",
          protocolosME: [{ id: 10, status: "EM_PROCESSO" }],
        },
        {
          id: 2,
          nome: "Maria Souza",
          status: "INTERNADO",
          protocolosME: [],
        },
      ],
    });

    apiClient.get.mockResolvedValue({
      data: [
        {
          id: 1,
          protocolosME: [{ id: 10, status: "EM_PROCESSO" }],
        },
      ],
    });
  });

  it("renderiza cards principais e secao do medico", async () => {
    render(
      <MemoryRouter>
        <Dashboard onLogout={jest.fn()} theme="dark" setTheme={jest.fn()} role="MEDICO" />
      </MemoryRouter>,
    );

    expect(await screen.findByText("Dashboard Principal")).toBeInTheDocument();
    expect(screen.getByText("Total de Pacientes")).toBeInTheDocument();
    expect(screen.getByText("Notificacoes em Tempo Real")).toBeInTheDocument();
    expect(screen.getByText("Secao do Medico/Enfermeiro")).toBeInTheDocument();
    expect(screen.getByText("Meu Protocolo ME")).toBeInTheDocument();
  });

  it("mostra notificacao de internados sem protocolo", async () => {
    render(
      <MemoryRouter>
        <Dashboard onLogout={jest.fn()} theme="light" setTheme={jest.fn()} role="MEDICO" />
      </MemoryRouter>,
    );

    expect(await screen.findByText("1 Paciente(s) Internado(s) Sem Protocolo")).toBeInTheDocument();
    expect(screen.getByText("Verificar possibilidade de iniciar protocolo ME")).toBeInTheDocument();
  });
});
