import { useLocation, useNavigate } from "react-router-dom";
import "../styles/PacienteCadastroPage.css";
import PacienteForm from "./PacienteForm";

function PacienteCadastroPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const pacienteParaEditar = location.state?.paciente || null;

  return (
    <section className="paciente-cadastro-page">
      <div className="brand-bar">
        <div>
          <h1>{pacienteParaEditar ? "Editar Paciente" : "Novo Cadastro de Paciente"}</h1>
          <p>
            {pacienteParaEditar
              ? "Atualize os dados do paciente selecionado."
              : "Preencha os dados para cadastrar um novo paciente."}
          </p>
        </div>
        <button
          className="secondary-button"
          onClick={() => navigate("/cadastros/pacientes")}
        >
          Voltar para Lista
        </button>
      </div>

      <PacienteForm
        paciente={pacienteParaEditar}
        ocultarResumo
        somenteFormulario
      />
    </section>
  );
}

export default PacienteCadastroPage;
