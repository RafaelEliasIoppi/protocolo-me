import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import apiClient from "../services/apiClient";
import pacienteService from "../services/pacienteService";
import "../styles/Dashboard.css";

function Dashboard({ onLogout, theme, setTheme, role }) {
  const [pacientes, setPacientes] = useState([]);
  const [protocolosME, setProtocolosME] = useState([]);
  const [notificacoes, setNotificacoes] = useState([]);
  const [carregando, setCarregando] = useState(false);

  useEffect(() => {
    let ativo = true;

    const carregarDados = async () => {
      try {
        setCarregando(true);

        const pacientesResponse = await pacienteService.listar();
        const pacientesData = Array.isArray(pacientesResponse)
          ? pacientesResponse
          : Array.isArray(pacientesResponse?.data)
            ? pacientesResponse.data
            : [];

        if (!ativo) {
          return;
        }

        setPacientes(pacientesData);

        if (role === "MEDICO" || role === "ENFERMEIRO" || role === "CENTRAL_TRANSPLANTES") {
          try {
            const protocolosResponse = await apiClient.get("/api/pacientes/em-protocolo-me");
            if (ativo) {
              setProtocolosME(Array.isArray(protocolosResponse.data) ? protocolosResponse.data : []);
            }
          } catch (error) {
            console.error("Erro ao carregar protocolos ME:", error);
            if (ativo) {
              setProtocolosME([]);
            }
          }
        } else {
          setProtocolosME([]);
        }

        if (ativo) {
          setNotificacoes(gerarNotificacoes(pacientesData));
        }
      } catch (error) {
        console.error("Erro ao carregar dados:", error);
      } finally {
        if (ativo) {
          setCarregando(false);
        }
      }
    };

    carregarDados();
    const intervalo = setInterval(carregarDados, 30000);

    return () => {
      ativo = false;
      clearInterval(intervalo);
    };
  }, [role]);

  const gerarNotificacoes = (listaPacientes) => {
    const novasNotificacoes = [];

    const pacientesEmProtocolo = listaPacientes.filter(
      (p) => Array.isArray(p.protocolosME) && p.protocolosME.length > 0,
    );

    if (pacientesEmProtocolo.length > 0) {
      const naoConfirmados = pacientesEmProtocolo.filter(
        (p) => p.protocolosME[0]?.status !== "MORTE_CEREBRAL_CONFIRMADA",
      ).length;

      novasNotificacoes.push({
        id: 1,
        tipo: "info",
        titulo: `${pacientesEmProtocolo.length} Protocolo(s) ME em Acompanhamento`,
        detalhe: `${naoConfirmados} aguardando confirmação de morte cerebral`,
      });
    }

    const internadosSemProtocolo = listaPacientes.filter(
      (p) => p.status === "INTERNADO" && (!Array.isArray(p.protocolosME) || p.protocolosME.length === 0),
    );

    if (internadosSemProtocolo.length > 0) {
      novasNotificacoes.push({
        id: 2,
        tipo: "warning",
        titulo: `${internadosSemProtocolo.length} Paciente(s) Internado(s) Sem Protocolo`,
        detalhe: "Verificar possibilidade de iniciar protocolo ME",
      });
    }

    const confirmados = pacientesEmProtocolo.filter(
      (p) => p.protocolosME[0]?.status === "MORTE_CEREBRAL_CONFIRMADA",
    ).length;

    if (confirmados > 0) {
      novasNotificacoes.push({
        id: 3,
        tipo: "alert",
        titulo: `${confirmados} Morte(s) Cerebral(is) Confirmada(s)`,
        detalhe: "Referências enviadas para a central de transplantes",
      });
    }

    return novasNotificacoes;
  };

  const totalPacientes = pacientes.length;
  const internados = pacientes.filter((p) => p.status === "INTERNADO").length;
  const emProtocoloME = protocolosME.length;
  const meConfirmada = protocolosME.filter(
    (p) => p.protocolosME?.[0]?.status === "MORTE_CEREBRAL_CONFIRMADA",
  ).length;

  const isMedico = role === "MEDICO" || role === "ENFERMEIRO";
  const isCentral = role === "CENTRAL_TRANSPLANTES";
  const isAdmin = role === "ADMIN";

  return (
    <section className="dashboard">
      <div className="dashboard-header">
        <div>
          <h1>Dashboard Principal</h1>
          <p>Sistema de Protocolo ME</p>
        </div>
        <div>
          <button className="secondary-button" onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>
            {theme === "dark" ? "Claro" : "Escuro"}
          </button>
          <button className="secondary-button" onClick={onLogout}>Sair</button>
        </div>
      </div>

      {carregando && <p className="note">Carregando dados...</p>}

      <div className="resumo-grid">
        <div className="resumo-card total">
          <h3>{totalPacientes}</h3>
          <p>Total de Pacientes</p>
        </div>
        <div className="resumo-card internados">
          <h3>{internados}</h3>
          <p>Pacientes Internados</p>
        </div>
        <div className="resumo-card protocolo">
          <h3>{emProtocoloME}</h3>
          <p>Em Protocolo ME</p>
        </div>
        <div className="resumo-card confirmado">
          <h3>{meConfirmada}</h3>
          <p>ME Confirmadas</p>
        </div>
      </div>

      <div className="panel notificacoes-panel">
        <h2>Notificacoes em Tempo Real</h2>
        {notificacoes.length === 0 ? (
          <p className="note">Tudo funcionando normalmente</p>
        ) : (
          <div className="notificacoes-lista">
            {notificacoes.map((notif) => (
              <div key={notif.id} className={`notificacao-item notif-${notif.tipo}`}>
                <h4>{notif.titulo}</h4>
                <p>{notif.detalhe}</p>
              </div>
            ))}
          </div>
        )}
      </div>

      {isMedico && (
        <div className="panel secao-medico">
          <h2>Secao do Medico/Enfermeiro</h2>
          <div className="acoes-rapidas">
            <Link to="/protocolo-me-medico" className="acao-card">
              <h3>Meu Protocolo ME</h3>
              <p>Gerenciar pacientes em protocolo ME e adicionar exames</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
            <Link to="/cadastros/pacientes" className="acao-card">
              <h3>Cadastro de Pacientes</h3>
              <p>Cadastrar novos pacientes internados</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
          </div>
        </div>
      )}

      {isCentral && (
        <div className="panel secao-central">
          <h2>Secao da Central de Transplantes</h2>
          <div className="acoes-rapidas">
            <Link to="/dashboard-central" className="acao-card">
              <h3>Painel Central</h3>
              <p>Monitorar todos os pacientes em protocolo ME do estado</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
            <Link to="/cadastros/hospitais" className="acao-card">
              <h3>Cadastro de Hospitais</h3>
              <p>Gerenciar hospitais e suas informacoes</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
          </div>
        </div>
      )}

      {isAdmin && (
        <div className="panel secao-admin">
          <h2>Secao Administrativa</h2>
          <div className="acoes-rapidas">
            <Link to="/admin/usuarios" className="acao-card">
              <h3>Cadastro de Usuarios</h3>
              <p>Gerenciar usuarios e suas permissoes</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
            <Link to="/dashboard-central" className="acao-card">
              <h3>Painel Central</h3>
              <p>Visualizar dados de todos os estados</p>
              <span className="link-arrow">&rarr;</span>
            </Link>
          </div>
        </div>
      )}

      <div className="panel info-usuario">
        <h2>Sua Sessao</h2>
        <p>
          Perfil: <strong>{role || "Nao identificado"}</strong>
        </p>
        <p>Dados atualizados em tempo real a cada 30 segundos</p>
      </div>
    </section>
  );
}

export default Dashboard;
