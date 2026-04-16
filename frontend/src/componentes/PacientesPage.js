import React from "react";
import PacienteForm from "./PacienteForm";

function PacientesPage() {
  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Cadastro de Pacientes</h1>
          <p>Somente médicos e enfermeiros podem cadastrar pacientes.</p>
        </div>
      </div>
      <PacienteForm />
    </section>
  );
}

export default PacientesPage;
