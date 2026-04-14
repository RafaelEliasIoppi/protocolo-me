import React, { useEffect, useState } from "react";
import api from "../api/api";
import PacienteForm from "./PacienteForm";

function Dashboard({ onLogout }) {
  const [pacientes, setPacientes] = useState([]);

  useEffect(() => {
    const fetchPacientes = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await api.get("/pacientes", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setPacientes(response.data);
      } catch (error) {
        console.error("Erro ao buscar pacientes:", error);
        alert("Erro ao carregar pacientes. Verifique se está logado.");
      }
    };
    fetchPacientes();
  }, []);

  const totalPacientes = pacientes.length;
  const protocoloAberto = pacientes.filter((p) => p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("aberto")).length;
  const protocoloConcluido = pacientes.filter((p) => p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("conclu")).length;

  return (
    <div>
      <div className="brand-bar">
        <div>
          <h1>Transportadora Dashboard</h1>
          <p>Visão geral dos pacientes e protocolos em um painel moderno.</p>
        </div>
        <button className="secondary-button" onClick={() => { localStorage.removeItem("token"); onLogout(); }}>
          Logout
        </button>
      </div>

      <div className="dashboard-grid">
        <div>
          <div className="stats-grid">
            <div className="stat-card">
              <h3>Total de pacientes</h3>
              <strong>{totalPacientes}</strong>
            </div>
            <div className="stat-card">
              <h3>Protocolos abertos</h3>
              <strong>{protocoloAberto}</strong>
            </div>
            <div className="stat-card">
              <h3>Protocolos concluídos</h3>
              <strong>{protocoloConcluido}</strong>
            </div>
          </div>

          <div className="panel">
            <header>
              <div>
                <h2>Pacientes</h2>
                <p className="note">Acompanhe os registros recentes e o status dos protocolos.</p>
              </div>
            </header>

            <div className="list-panel">
              {pacientes.map((p) => (
                <div className="patient-card" key={p.id}>
                  <div>
                    <h4>{p.nome}</h4>
                    <span>{p.cpf ? `CPF: ${p.cpf}` : "CPF não informado"}</span>
                  </div>
                  <span className={`status-pill ${p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("conclu") ? "status-closed" : "status-open"}`}>
                    {p.statusProtocolo || "Sem status"}
                  </span>
                </div>
              ))}
              {pacientes.length === 0 && <p className="note">Nenhum paciente encontrado no momento.</p>}
            </div>
          </div>
        </div>

        <div className="form-panel card">
          <h3>Novo paciente</h3>
          <PacienteForm />
        </div>
      </div>
    </div>
  );
}

export default Dashboard;
