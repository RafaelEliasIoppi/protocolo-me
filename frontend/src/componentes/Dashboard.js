import React, { useEffect, useMemo, useState } from "react";
import pacienteService from "../services/pacienteService";
import PacienteForm from "./PacienteForm";

function Dashboard({ onLogout, theme, setTheme }) {
  const [pacientes, setPacientes] = useState([]);
  const [searchQuery, setSearchQuery] = useState("");
  const [statusFilter, setStatusFilter] = useState("Todos");
  const [editingPaciente, setEditingPaciente] = useState(null);

  const notifications = [
    { id: 1, title: "Protocolo novo recebido", detail: "Há 5 novos pacientes aguardando análise." },
    { id: 2, title: "Atualização de processo", detail: "2 protocolos concluídos nas últimas 24 horas." },
    { id: 3, title: "Atenção prioritária", detail: "1 paciente precisa de atendimento urgente." },
  ];

  useEffect(() => {
    fetchPacientes();
  }, []);

  const fetchPacientes = async () => {
    try {
      const response = await pacienteService.listar();
      setPacientes(response.data);
    } catch (error) {
      console.error("Erro ao buscar pacientes:", error);
      alert("Erro ao carregar pacientes. Verifique se está logado.");
    }
  };

  const totalPacientes = pacientes.length;
  const protocoloAberto = pacientes.filter((p) => p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("aberto")).length;
  const protocoloConcluido = pacientes.filter((p) => p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("conclu")).length;

  const filteredPacientes = useMemo(() => {
    return pacientes.filter((p) => {
      const matchesName = p.nome.toLowerCase().includes(searchQuery.toLowerCase());
      const matchesStatus =
        statusFilter === "Todos" ||
        (statusFilter === "Aberto" && p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("aberto")) ||
        (statusFilter === "Em andamento" && p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("andamento")) ||
        (statusFilter === "Concluído" && p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("conclu")) ||
        (statusFilter === "Cancelado" && p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("cancelado"));
      return matchesName && matchesStatus;
    });
  }, [pacientes, searchQuery, statusFilter]);

  const handleSavePaciente = (pacienteSalvo) => {
    if (editingPaciente) {
      setPacientes(prev => prev.map(p => p.id === pacienteSalvo.id ? pacienteSalvo : p));
      setEditingPaciente(null);
    } else {
      setPacientes(prev => [...prev, pacienteSalvo]);
    }
  };

  const handleEditPaciente = (paciente) => {
    setEditingPaciente(paciente);
  };

  const handleDeletePaciente = async (id) => {
    if (window.confirm("Tem certeza que deseja excluir este paciente?")) {
      try {
        await pacienteService.deletar(id);
        setPacientes(prev => prev.filter(p => p.id !== id));
      } catch (error) {
        console.error("Erro ao deletar paciente:", error);
        alert("Erro ao excluir paciente.");
      }
    }
  };

  const toggleTheme = () => setTheme(theme === "dark" ? "light" : "dark");

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">
        <div>
          <h2>Transportadora</h2>
          <p>Painel de controle para gerenciar pacientes, protocolos e fluxos.</p>
        </div>

        <div className="sidebar-nav">
          <button className="active">Overview</button>
          <button>Pacientes</button>
          <button>Protocolos</button>
          <button>Configurações</button>
        </div>

        <div>
          <p className="note">Dica: atualize os dados sempre que cadastrar um novo paciente.</p>
        </div>
      </aside>

      <main className="dashboard-main">
        <div className="brand-bar">
          <div>
            <h1>Dashboard Moderno</h1>
            <p>Uma visão clara dos indicadores de saúde e dos protocolos ativos.</p>
          </div>
          <div className="action-row">
            <button className="secondary-button" onClick={toggleTheme}>
              {theme === "dark" ? "Modo Claro" : "Modo Escuro"}
            </button>
            <button className="secondary-button" onClick={() => { localStorage.removeItem("token"); onLogout(); }}>
              Logout
            </button>
          </div>
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

            <div className="notifications-panel panel">
              <header>
                <div>
                  <h2>Notificações</h2>
                  <p className="note">Acompanhe alertas importantes e mudanças recentes.</p>
                </div>
              </header>
              <div className="notifications-grid">
                {notifications.map((note) => (
                  <div className="notification-card" key={note.id}>
                    <strong>{note.title}</strong>
                    <p>{note.detail}</p>
                  </div>
                ))}
              </div>
            </div>

            <div className="panel">
              <header>
                <div>
                  <h2>Resumo dos protocolos</h2>
                  <p className="note">Visualização rápida do status atual em barras de progresso.</p>
                </div>
              </header>

              <div className="chart-card">
                <div className="chart-row">
                  <span>Tempo médio</span>
                  <strong>72%</strong>
                </div>
                <div className="chart-bar background">
                  <div className="chart-progress" style={{ width: "72%" }} />
                </div>

                <div className="chart-row">
                  <span>Urgência</span>
                  <strong>43%</strong>
                </div>
                <div className="chart-bar background">
                  <div className="chart-progress accent" style={{ width: "43%" }} />
                </div>

                <div className="chart-row">
                  <span>Satisfação</span>
                  <strong>89%</strong>
                </div>
                <div className="chart-bar background">
                  <div className="chart-progress success" style={{ width: "89%" }} />
                </div>
              </div>
            </div>

            <div className="panel">
              <header>
                <div>
                  <h2>Pacientes recentes</h2>
                  <p className="note">Acompanhe os registros criados e o status dos protocolos em tempo real.</p>
                </div>
              </header>

              <div className="filter-panel">
                <input
                  className="input-field"
                  type="search"
                  placeholder="Buscar por nome"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                <select className="select-field" value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}>
                  <option value="Todos">Todos os status</option>
                  <option value="Aberto">Aberto</option>
                  <option value="Em andamento">Em andamento</option>
                  <option value="Concluído">Concluído</option>
                  <option value="Cancelado">Cancelado</option>
                </select>
              </div>

              <div className="list-panel">
                {filteredPacientes.length > 0 ? (
                  filteredPacientes.map((p) => (
                    <div className="patient-card" key={p.id}>
                      <div className="patient-info">
                        <h4>{p.nome}</h4>
                        <span>CPF: {p.cpf || "Não informado"}</span>
                        <span>Telefone: {p.telefone || "Não informado"}</span>
                        <span className="hospital-info">
                          Hospital: {p.hospital ? `${p.hospital.nome} - ${p.hospital.cidade}` : "Não atribuído"}
                        </span>
                      </div>
                      <div className="patient-actions">
                        <span className={`status-pill ${p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("conclu") ? "status-closed" : p.statusProtocolo && p.statusProtocolo.toLowerCase().includes("andamento") ? "status-pending" : "status-open"}`}>
                          {p.statusProtocolo || "Sem status"}
                        </span>
                        <div className="action-buttons">
                          <button
                            className="edit-button"
                            onClick={() => handleEditPaciente(p)}
                            title="Editar paciente"
                          >
                            ✏️
                          </button>
                          <button
                            className="delete-button"
                            onClick={() => handleDeletePaciente(p.id)}
                            title="Excluir paciente"
                          >
                            🗑️
                          </button>
                        </div>
                      </div>
                    </div>
                  ))
                ) : (
                  <p className="note">Nenhum paciente encontrado com os filtros aplicados.</p>
                )}
              </div>
            </div>
          </div>

          <div className="form-panel card">
            <h3>{editingPaciente ? "Editar Paciente" : "Novo paciente"}</h3>
            <p className="note">
              {editingPaciente
                ? "Atualize as informações do paciente selecionado."
                : "Cadastro rápido para adicionar pacientes diretamente ao painel."
              }
            </p>
            <PacienteForm
              paciente={editingPaciente}
              onSave={handleSavePaciente}
              onCancel={() => setEditingPaciente(null)}
            />
          </div>
        </div>
      </main>
    </div>
  );
}

export default Dashboard;
