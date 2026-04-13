import React, { useEffect, useState } from "react";
import api from "../api/api";
import PacienteForm from "./PacienteForm";

function Dashboard() {
  const [pacientes, setPacientes] = useState([]);

  useEffect(() => {
    const fetchPacientes = async () => {
      const token = localStorage.getItem("token");
      const response = await api.get("/pacientes", {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPacientes(response.data);
    };
    fetchPacientes();
  }, []);

  return (
    <div>
      <h2>Pacientes</h2>
      <PacienteForm />
      <ul>
        {pacientes.map((p) => (
          <li key={p.id}>{p.nome} - {p.statusProtocolo}</li>
        ))}
      </ul>
    </div>
  );
}

export default Dashboard;
