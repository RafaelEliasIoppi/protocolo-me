import React, { useState, useEffect } from "react";
import apiClient from "../services/apiClient";
import OrgaoDoadoManager from "./OrgaoDoadoManager";
import "../styles/PacientesProtocoloMEPage.css";

function PacientesProtocoloMEPage() {
  const [pacientes, setPacientes] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [filtroHospital, setFiltroHospital] = useState("");
  const [hospitais, setHospitais] = useState([]);
  const [protocolosComOrgaosAbertos, setProtocolosComOrgaosAbertos] = useState(new Set());

  // Carregar pacientes em protocolo de ME
  const carregarPacientesProtocoloME = async (hospitalId = "") => {
    setCarregando(true);
    setErro("");
    try {
      let endpoint = "/api/pacientes/em-protocolo-me";
      if (hospitalId) {
        endpoint = `/api/pacientes/em-protocolo-me/hospital/${hospitalId}`;
      }
      const response = await apiClient.get(endpoint);
      setPacientes(response.data);
    } catch (err) {
      setErro("Erro ao carregar pacientes em protocolo de ME");
      console.error("Erro:", err);
    } finally {
      setCarregando(false);
    }
  };

  // Carregar lista de hospitais para filtro
  const carregarHospitais = async () => {
    try {
      const response = await apiClient.get("/api/hospitais");
      setHospitais(response.data);
    } catch (err) {
      console.error("Erro ao carregar hospitais:", err);
    }
  };

  // Inicializar
  useEffect(() => {
    carregarHospitais();
    carregarPacientesProtocoloME();
  }, []);

  // Se hotel for alterado, recarregar pacientes
  const handleFiltroHospitalChange = (e) => {
    const hospitalId = e.target.value;
    setFiltroHospital(hospitalId);
    if (hospitalId) {
      carregarPacientesProtocoloME(hospitalId);
    } else {
      carregarPacientesProtocoloME("");
    }
  };

  const formatarStatusEntrevista = (status) => {
    const mapa = {
      NAO_INICIADA: "Não iniciada",
      EM_ANDAMENTO: "Em andamento",
      AUTORIZADA: "Autorizada",
      RECUSADA: "Recusada"
    };

    return mapa[status] || status || "Não iniciada";
  };

  const toggleOrgaosDoados = (protocoloId) => {
    setProtocolosComOrgaosAbertos((prev) => {
      const novoSet = new Set(prev);
      if (novoSet.has(protocoloId)) {
        novoSet.delete(protocoloId);
      } else {
        novoSet.add(protocoloId);
      }
      return novoSet;
    });
  };

  return (
    <section className="pacientes-protocolo-me-page">
      <div className="brand-bar">
        <div>
          <h1>Pacientes em Protocolo de Morte Encefálica</h1>
          <p>Visualize apenas pacientes que já iniciaram o protocolo de ME.</p>
        </div>
        <button className="secondary-button" onClick={() => carregarPacientesProtocoloME(filtroHospital)}>
          🔄 Atualizar
        </button>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <div className="panel">
        <div className="filtro-section">
          <label htmlFor="filtro-hospital">Filtrar por Hospital:</label>
          <select
            id="filtro-hospital"
            value={filtroHospital}
            onChange={handleFiltroHospitalChange}
            className="select-filtro"
          >
            <option value="">Todos os Hospitais</option>
            {hospitais.map((hospital) => (
              <option key={hospital.id} value={hospital.id}>
                {hospital.nomeHospital || hospital.nome}
              </option>
            ))}
          </select>
        </div>

        {carregando ? (
          <div className="carregando">⏳ Carregando pacientes...</div>
        ) : pacientes.length > 0 ? (
          <div className="pacientes-container">
            <div className="info-resumo">
              <p>
                <strong>Total de pacientes em protocolo ME:</strong> {pacientes.length}
              </p>
            </div>

            <div className="pacientes-grid">
              {pacientes.map((paciente) => (
                <div key={paciente.id} className="paciente-card protocolo-me">
                  <div className="card-header">
                    <h3>{paciente.nome}</h3>
                    <span className="badge badge-protocolo">EM PROTOCOLO ME</span>
                  </div>

                  <div className="card-body">
                    <div className="info-row">
                      <label>CPF:</label>
                      <span>{paciente.cpf}</span>
                    </div>
                    <div className="info-row">
                      <label>Data de Nascimento:</label>
                      <span>
                        {new Date(paciente.dataNascimento).toLocaleDateString("pt-BR")}
                      </span>
                    </div>
                    <div className="info-row">
                      <label>Gênero:</label>
                      <span>{paciente.genero}</span>
                    </div>
                    <div className="info-row">
                      <label>Hospital:</label>
                      <span>{paciente.hospital?.nomeHospital || "N/A"}</span>
                    </div>
                    <div className="info-row">
                      <label>Leito:</label>
                      <span>{paciente.leito || "N/A"}</span>
                    </div>
                    <div className="info-row">
                      <label>Data de Internação:</label>
                      <span>
                        {paciente.dataInternacao
                          ? new Date(paciente.dataInternacao).toLocaleDateString("pt-BR")
                          : "N/A"}
                      </span>
                    </div>
                    <div className="info-row">
                      <label>Status:</label>
                      <span className="status-badge status-ativo">{paciente.status}</span>
                    </div>
                    <div className="info-row">
                      <label>Entrevista Familiar:</label>
                      <span className="status-badge status-ativo">
                        {formatarStatusEntrevista(paciente.statusEntrevistaFamiliar)}
                      </span>
                    </div>
                    {paciente.diagnosticoPrincipal && (
                      <div className="info-row">
                        <label>Diagnóstico:</label>
                        <span className="diagnostico">
                          {paciente.diagnosticoPrincipal}
                        </span>
                      </div>
                    )}
                  </div>

                  {paciente.protocolosME && paciente.protocolosME.length > 0 && (
                    <div className="card-protocols">
                      <h4>Protocolos ME Associados:</h4>
                      <ul className="protocol-list">
                        {paciente.protocolosME.map((protocolo) => (
                          <li key={protocolo.id}>
                            <strong>Número:</strong> {protocolo.numeroProtocolo}
                            <br />
                            <strong>Status:</strong> {protocolo.status}
                            <br />
                            <strong>Hospital Origem:</strong> {protocolo.hospitalOrigem}

                            <div className="protocolo-acoes">
                              <button
                                type="button"
                                className="btn-orgaos-doados"
                                onClick={() => toggleOrgaosDoados(protocolo.id)}
                              >
                                {protocolosComOrgaosAbertos.has(protocolo.id)
                                  ? "Ocultar Órgãos Doados"
                                  : "Informar Órgãos Doados"}
                              </button>
                            </div>

                            {protocolosComOrgaosAbertos.has(protocolo.id) && (
                              <div className="protocolo-orgaos-manager">
                                <OrgaoDoadoManager protocoloId={protocolo.id} />
                              </div>
                            )}
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </div>
              ))}
            </div>
          </div>
        ) : (
          <div className="panel">
            <p className="note">
              ✓ Nenhum paciente em protocolo de ME no momento
              {filtroHospital && " neste hospital"}
            </p>
          </div>
        )}
      </div>
    </section>
  );
}

export default PacientesProtocoloMEPage;
