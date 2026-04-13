import React, { useState } from "react";
import Login from "./components/Login";
import Dashboard from "./components/Dashboard";

function App() {
  const [isLogged, setIsLogged] = useState(false);

  return (
    <div>
      {isLogged ? <Dashboard /> : <Login onLogin={() => setIsLogged(true)} />}
    </div>
  );
}

export default App;
