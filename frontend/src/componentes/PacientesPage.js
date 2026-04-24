import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../services/apiClient";
import PacienteForm from "./PacienteForm";

function PacientesPage() {
  const navigate = useNavigate();
  const [estatisticas, setEstatisticas] = useState(null);
  const [carregandoEstatisticas, setCarregandoEstatisticas] = useState(false);

  const normalizarNumero = (valor) => (typeof valor === "number" ? valor : 0);

  useEffect(() => {
    const carregarEstatisticas = async () => {
      try {
        setCarregandoEstatisticas(true);
        const response = await apiClient.get("/api/pacientes/estatisticas/resumo");
        setEstatisticas(response.data || {});
      } catch (error) {
        console.error("Erro ao carregar estatísticas de pacientes:", error);
        setEstatisticas({});
      } finally {
        setCarregandoEstatisticas(false);
      }
    };

    carregarEstatisticas();
  }, []);

  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Cadastro de Pacientes</h1>
          <p>Médicos, enfermeiros e central de transplantes podem gerenciar pacientes.</p>
        </div>
        <button
          className="secondary-button"
          onClick={() => navigate("/cadastros/pacientes/novo")}
        >
          Novo Cadastro
        </button>
      </div>

      <div className="panel">
        <h2>Resumo de Pacientes</h2>
        {carregandoEstatisticas ? (
          <p>Carregando estatísticas...</p>
        ) : (
          <div className="estatisticas-grid">
            <div className="stat-card">
              <div className="stat-valor">{normalizarNumero(estatisticas?.totalPacientes)}</div>
              <div className="stat-label">Total de Pacientes</div>
            </div>
            <div className="stat-card stat-internados">
              <div className="stat-valor">{normalizarNumero(estatisticas?.pacientesInternados)}</div>
              <div className="stat-label">Internados</div>
            </div>
            <div className="stat-card stat-protocolo">
              <div className="stat-valor">{normalizarNumero(estatisticas?.pacientesEmProtocoloME)}</div>
              <div className="stat-label">Em Protocolo ME</div>
            </div>
            <div className="stat-card stat-apto">
              <div className="stat-valor">{normalizarNumero(estatisticas?.pacientesAptosTransplante)}</div>
              <div className="stat-label">Aptos Transplante</div>
            </div>
            <div className="stat-card stat-nao-apto">
              <div className="stat-valor">{normalizarNumero(estatisticas?.pacientesNaoAptos)}</div>
              <div className="stat-label">Não Aptos</div>
            </div>
          </div>
        )}
      </div>

      <PacienteForm
        ocultarResumo
        somenteListagem
        onEditarPaciente={(paciente) => navigate("/cadastros/pacientes/novo", { state: { paciente } })}
      />
    </section>
  );
}

export default PacientesPage;
