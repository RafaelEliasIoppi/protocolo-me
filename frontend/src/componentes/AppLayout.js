import React from "react";
import { Link, Outlet } from "react-router-dom";

function AppLayout({ usuario, theme, setTheme, onLogout }) {
  const role = usuario?.role;

  return (
    <div className="dashboard-shell">
      <aside className="sidebar">

        <div>
          <h2>Protocolo ME</h2>
          <p>Painel administrativo com cadastros separados por funcionalidade.</p>
          <p className="note">Perfil: {role || "Não identificado"}</p>
        </div>

        <div className="sidebar-nav" style={{ gridTemplateColumns: "1fr" }}>
          <Link className="secondary-button" to="/dashboard">Dashboard Geral</Link>
          {(role === "CENTRAL_TRANSPLANTES" || role === "ADMIN" || role === "MEDICO") && (
            <Link className="secondary-button" to="/estatisticas">📊 Estatísticas</Link>
          )}
          {(role === "CENTRAL_TRANSPLANTES" || role === "ADMIN") && (
            <Link className="secondary-button" to="/dashboard-central">Painel da Central</Link>
          )}
          {(role === "MEDICO" || role === "ENFERMEIRO") && (
             <Link className="secondary-button" to="/protocolo-me-medico">Meu Protocolo ME</Link>
           )}
           {(role === "MEDICO" || role === "ENFERMEIRO") && (
            <Link className="secondary-button" to="/cadastros/pacientes">Cadastro de Pacientes</Link>
          )}
          {role === "CENTRAL_TRANSPLANTES" && (
            <Link className="secondary-button" to="/cadastros/hospitais">Cadastro de Hospitais</Link>
          )}
          {(role === "ADMIN" || role === "COORDENADOR_TRANSPLANTES") && (
            <Link className="secondary-button" to="/admin/usuarios">Cadastro de Usuários</Link>
          )}
          <Link className="secondary-button" to="/cadastros/centrais">Cadastro de Centrais</Link>
          <Link className="secondary-button" to="/alterar-senha">Alterar Senha</Link>
        </div>

        <div className="action-row" style={{ justifyContent: "flex-start" }}>
          <button className="secondary-button" onClick={() => setTheme(theme === "dark" ? "light" : "dark")}>
            {theme === "dark" ? "Modo Claro" : "Modo Escuro"}
          </button>
          <button className="secondary-button" onClick={onLogout}>Sair</button>
        </div>
      </aside>

      <main className="dashboard-main">
        <Outlet />
      </main>
    </div>
  );
}

export default AppLayout;
