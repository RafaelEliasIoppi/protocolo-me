import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import clienteHttpService from "../services/clienteHttpService";
import pacienteService from "../services/pacienteService";
import PainelPrincipalPage from "./PainelPrincipalPage";

jest.mock("../services/clienteHttpService", () => ({
  get: jest.fn(),
}));

jest.mock("../services/pacienteService", () => ({
  listar: jest.fn(),
  listarEmProtocoloME: jest.fn(),
}));

describe("Dashboard", () => {
  const routerFutureFlags = {
    v7_startTransition: true,
    v7_relativeSplatPath: true,
  };

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

    pacienteService.listarEmProtocoloME.mockResolvedValue([]);

    clienteHttpService.get.mockResolvedValue({
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
        <MemoryRouter future={routerFutureFlags}>
          <PainelPrincipalPage onLogout={jest.fn()} theme="dark" setTheme={jest.fn()} role="MEDICO" />
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
        <MemoryRouter future={routerFutureFlags}>
          <PainelPrincipalPage onLogout={jest.fn()} theme="light" setTheme={jest.fn()} role="MEDICO" />
      </MemoryRouter>,
    );

    expect(await screen.findByText("1 Paciente(s) Internado(s) Sem Protocolo")).toBeInTheDocument();
    expect(screen.getByText("Verificar possibilidade de iniciar protocolo ME")).toBeInTheDocument();
  });
});
