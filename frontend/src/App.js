import React, { useState } from "react";
import Login from "./componentes/login";
import Dashboard from "./componentes/Dashboard";
import "./App.css";

function App() {
  const [isLogged, setIsLogged] = useState(false);

  return (
    <div className="app-shell">
      {isLogged ? <Dashboard onLogout={() => setIsLogged(false)} /> : <Login onLogin={() => setIsLogged(true)} />}
    </div>
  );
}

export default App;
