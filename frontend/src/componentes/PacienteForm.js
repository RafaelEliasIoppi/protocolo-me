import React, { useState } from "react";
import api from "../api/api";

function PacienteForm() {
  const [nome, setNome] = useState("");
  const [cpf, setCpf] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    const token = localStorage.getItem("token");
    await api.post("/pacientes", { nome, cpf }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    alert("Paciente cadastrado!");
  };

  return (
    <form onSubmit={handleSubmit}>
      <input type="text" placeholder="Nome" value={nome} onChange={(e) => setNome(e.target.value)} />
      <input type="text" placeholder="CPF" value={cpf} onChange={(e) => setCpf(e.target.value)} />
      <button type="submit">Cadastrar</button>
    </form>
  );
}

export default PacienteForm;
