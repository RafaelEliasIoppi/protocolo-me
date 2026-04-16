import React, { useEffect, useMemo, useState } from "react";
import pacienteService from "../services/pacienteService";

function CentralDashboardPage() {
  const [pacientes, setPacientes] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [ultimaAtualizacao, setUltimaAtualizacao] = useState(null);

  const carregarPacientes = async () => {
    try {
      setCarregando(true);
      const response = await pacienteService.listar();
      const dados = Array.isArray(response) ? response : Array.isArray(response?.data) ? response.data : [];
      setPacientes(dados);
      setUltimaAtualizacao(new Date());
      setErro("");
    } catch (e) {
      setErro("Erro ao atualizar painel da central.");
      setPacientes([]);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarPacientes();
    const intervalo = setInterval(carregarPacientes, 8000);
    return () => clearInterval(intervalo);
  }, []);

  const pacientesME = useMemo(() => {
    return pacientes.filter((paciente) => {
      const statusProtocolo = String(paciente.statusProtocolo || "").toUpperCase();
      const statusPaciente = String(paciente.status || "").toUpperCase();
      return statusPaciente === "EM_PROTOCOLO_ME" || statusProtocolo.includes("ME");
    });
  }, [pacientes]);

  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Painel da Central de Transplantes</h1>
          <p>Monitoramento em tempo real dos pacientes que já iniciaram protocolo de ME.</p>
        </div>
        <div className="action-row">
          <button className="secondary-button" onClick={carregarPacientes}>Atualizar agora</button>
        </div>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <div className="panel">
        <header>
          <div>
            <h2>Pacientes em Protocolo de ME</h2>
            <p className="note">
              {ultimaAtualizacao
                ? `Última atualização: ${ultimaAtualizacao.toLocaleTimeString("pt-BR")}`
                : "Aguardando primeira atualização"}
            </p>
          </div>
        </header>

        {carregando && <p className="note">Atualizando painel...</p>}

        {!carregando && pacientesME.length === 0 ? (
          <p className="note">Nenhum paciente em protocolo ME no momento.</p>
        ) : (
          <div className="list-panel">
            {pacientesME.map((paciente) => (
              <div className="patient-card" key={paciente.id}>
                <div className="patient-info">
                  <h4>{paciente.nome}</h4>
                  <span>Hospital: {paciente.hospital?.nome || "Não informado"}</span>
                  <span>Município: {paciente.hospital?.cidade || "Não informado"}</span>
                </div>
                <div className="patient-actions">
                  <span className="status-pill status-pending">
                    {paciente.statusProtocolo || paciente.status || "Sem status"}
                  </span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

export default CentralDashboardPage;
