import React, { useState } from "react";
import PacienteForm from "./PacienteForm";

function PacientesPage() {
  const [mostrarFormularioPaciente, setMostrarFormularioPaciente] = useState(false);

  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Cadastro de Pacientes</h1>
           <p>Médicos, enfermeiros e central de transplantes podem gerenciar pacientes.</p>
        </div>
        <button className="secondary-button" onClick={() => setMostrarFormularioPaciente((valor) => !valor)}>
          {mostrarFormularioPaciente ? "Fechar cadastro" : "Abrir cadastro"}
        </button>
      </div>

      {mostrarFormularioPaciente ? (
        <PacienteForm onCancel={() => setMostrarFormularioPaciente(false)} />
      ) : (
        <div className="panel">
          <p className="note">O cadastro de pacientes fica oculto até você abrir o formulário.</p>
        </div>
      )}
    </section>
  );
}

export default PacientesPage;
