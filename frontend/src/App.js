import { useEffect, useMemo, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import "./App.css";
import AlterarSenhaPage from "./componentes/AlterarSenhaPage";
import AppLayout from "./componentes/AppLayout";
import CentraisPage from "./componentes/CentraisPage";
import CentralDashboardPage from "./componentes/CentralDashboardPage";
import Dashboard from "./componentes/Dashboard";
import EstatisticasPage from "./componentes/EstatisticasPage";
import HospitaisPage from "./componentes/HospitaisPage";
import HospitalStatus from "./componentes/HospitalStatus";
import Login from "./componentes/login";
import MedicoProtocoloME from "./componentes/MedicoProtocoloME";
import PacienteCadastroPage from "./componentes/PacienteCadastroPage";
import PacientesPage from "./componentes/PacientesPage";
import PacientesProtocoloMEPage from "./componentes/PacientesProtocoloMEPage";
import UsuariosAdminPage from "./componentes/UsuariosAdminPage";
import autenticarService from "./services/autenticarService";

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
  const [isLogged, setIsLogged] = useState(() => autenticarService.isAutenticado());
  const [theme, setTheme] = useState("dark");

  const usuario = useMemo(() => autenticarService.obterUsuarioAtual(), [isLogged]);

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
  }, [theme]);

  const completarLogin = () => {
    setIsLogged(true);
    navigate("/dashboard");
  };

  const desconectar = () => {
    autenticarService.logout();
    setIsLogged(false);
    navigate("/login");
  };

  return (
    <div className={`app-shell ${theme}-theme`}>
      <Routes>
        <Route
          path="/dashboard-central/telao"
          element={
            <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES"]}>
              <CentralDashboardPage telaoMode />
            </GuardedRoute>
          }
        />

        <Route
          path="/login"
          element={isLogged ? <Navigate to="/dashboard" replace /> : <Login onLogin={completarLogin} />}
        />

        <Route
          path="/"
          element={
            <GuardedRoute isLogged={isLogged}>
              <AppLayout
                usuario={usuario}
                theme={theme}
                setTheme={setTheme}
                onLogout={desconectar}
              />
            </GuardedRoute>
          }
        >
          <Route index element={<Navigate to="/dashboard" replace />} />
          <Route
            path="dashboard"
            element={<Dashboard onLogout={desconectar} theme={theme} setTheme={setTheme} role={usuario?.role} />}
          />
          <Route
            path="alterar-senha"
            element={<AlterarSenhaPage />}
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
            path="estatisticas"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES", "ADMIN", "MEDICO"]}>
                <EstatisticasPage />
              </GuardedRoute>
            }
          />
           <Route
             path="protocolo-me-medico"
             element={
               <GuardedRoute isLogged={isLogged} allowedRoles={["MEDICO", "ENFERMEIRO"]}>
                 <MedicoProtocoloME />
               </GuardedRoute>
             }
           />
          <Route
            path="cadastros/pacientes"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["MEDICO", "ENFERMEIRO", "CENTRAL_TRANSPLANTES"]}>
                <PacientesPage />
              </GuardedRoute>
            }
          />
          <Route
            path="cadastros/pacientes/novo"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["MEDICO", "ENFERMEIRO", "CENTRAL_TRANSPLANTES"]}>
                <PacienteCadastroPage />
              </GuardedRoute>
            }
          />
          <Route
            path="pacientes/protocolo-me"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES", "ADMIN", "MEDICO"]}>
                <PacientesProtocoloMEPage />
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
            path="cadastros/hospitais/status"
            element={
              <GuardedRoute isLogged={isLogged} allowedRoles={["CENTRAL_TRANSPLANTES"]}>
                <HospitalStatus />
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
