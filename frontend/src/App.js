import React, { useEffect, useMemo, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import Login from "./componentes/login";
import Dashboard from "./componentes/Dashboard";
import AppLayout from "./componentes/AppLayout";
import PacientesPage from "./componentes/PacientesPage";
import HospitaisPage from "./componentes/HospitaisPage";
import CentraisPage from "./componentes/CentraisPage";
import CentralDashboardPage from "./componentes/CentralDashboardPage";
import UsuariosAdminPage from "./componentes/UsuariosAdminPage";
import autenticarService from "./services/autenticarService";
import "./App.css";

function AcessoNegado() {
  return (
    <div className="panel">
      <h2>Acesso negado</h2>
      <p className="note">Seu perfil não possui permissão para acessar esta página.</p>
    </div>
  );
}

function GuardedRoute({ isLogged, allowedRoles, children }) {
  const usuario = autenticarService.obterUsuarioAtual();
  const role = usuario?.role;

  if (!isLogged || !role) {
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles && !allowedRoles.includes(role)) {
    return <AcessoNegado />;
  }

  return children;
}

function App() {
  const navigate = useNavigate();
  const [isLogged, setIsLogged] = useState(false);
  const [theme, setTheme] = useState("dark");

  const usuario = useMemo(() => autenticarService.obterUsuarioAtual(), [isLogged]);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
  }, [theme]);

  const handleLogin = () => {
    setIsLogged(true);
    navigate("/dashboard");
  };

  const handleLogout = () => {
    autenticarService.logout();
    setIsLogged(false);
    navigate("/login");
  };

  return (
    <div className={`app-shell ${theme}-theme`}>
      <Routes>
        <Route
          path="/login"
          element={isLogged ? <Navigate to="/dashboard" replace /> : <Login onLogin={handleLogin} />}
        />

        <Route
          path="/"
          element={
            <GuardedRoute isLogged={isLogged}>
              <AppLayout
                usuario={usuario}
                theme={theme}
                setTheme={setTheme}
                onLogout={handleLogout}
              />
            </GuardedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route
            path="dashboard"
            element={<Dashboard onLogout={handleLogout} theme={theme} setTheme={setTheme} role={usuario?.role} />}
          />
          <Route
            path="dashboard-central"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES"]}>
                <CentralDashboardPage />
              </GuardedRoute>
            }
          />
          <Route
            path="cadastros/pacientes"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["MEDICO", "ENFERMEIRO"]}>
                <PacientesPage />
              </GuardedRoute>
            }
          />
          <Route
            path="cadastros/hospitais"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES"]}>
                <HospitaisPage />
              </GuardedRoute>
            }
          />
          <Route
            path="cadastros/centrais"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES", "ADMIN"]}>
                <CentraisPage />
              </GuardedRoute>
            }
          />
          <Route
            path="admin/usuarios"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["ADMIN"]}>
                <UsuariosAdminPage />
              </GuardedRoute>
            }
          />
        </Route>

        <Route path="*" element={<Navigate to={isLogged ? "/dashboard" : "/login"} replace />} />
      </Routes>
    </div>
  );
}

export default App;
