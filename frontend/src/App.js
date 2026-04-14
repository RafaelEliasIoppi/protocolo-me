import React, { useEffect, useState } from "react";
import Login from "./componentes/login";
import Dashboard from "./componentes/Dashboard";
import "./App.css";

function App() {
  const [isLogged, setIsLogged] = useState(false);
  const [theme, setTheme] = useState("dark");

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
  }, [theme]);

  return (
    <div className={`app-shell ${theme}-theme`}>
      {isLogged ? <Dashboard onLogout={() => setIsLogged(false)} theme={theme} setTheme={setTheme} /> : <Login onLogin={() => setIsLogged(true)} />}
    </div>
  );
}

export default App;
