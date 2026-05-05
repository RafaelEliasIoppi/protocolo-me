import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import hospitalService from "../services/hospitalService";
import "../styles/HospitaisPage.css";
import HospitalForm from "./HospitalForm";

function HospitaisPage() {
  const [hospitais, setHospitais] = useState([]);
  const [hospitalEmEdicao, setHospitalEmEdicao] = useState(null);
  const [erro, setErro] = useState("");

  const carregarHospitais = async () => {
    try {
      const response = await hospitalService.listar();
      setHospitais(Array.isArray(response) ? response : []);
      setErro("");
    } catch (e) {
      setErro("Erro ao carregar hospitais.");
      setHospitais([]);
    }
  };

  useEffect(() => {
    carregarHospitais();
  }, []);

  const excluirHospital = async (id) => {
    if (!window.confirm("Deseja realmente excluir este hospital?")) {
      return;
    }

    try {
      await hospitalService.deletar(id);
      await carregarHospitais();
      if (hospitalEmEdicao?.id === id) {
        setHospitalEmEdicao(null);
      }
    } catch (e) {
      setErro("Não foi possível excluir o hospital.");
    }
  };

  return (
    <section className="hospitais-page">
      <div className="brand-bar">
        <div>
          <h1>Cadastro de Hospitais</h1>
          <p>Somente a Central de Transplantes pode cadastrar hospitais.</p>
        </div>
        <Link className="secondary-button" to="/cadastros/hospitais/status">
          Gerenciar Status
        </Link>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <HospitalForm
        hospitalParaEditar={hospitalEmEdicao}
        onSuccess={() => {
          carregarHospitais();
          setHospitalEmEdicao(null);
        }}
      />

      <div className="panel hospitais-lista-panel">
        <header>
          <div>
            <h2>Hospitais Cadastrados</h2>
            <p className="note">Use editar para atualizar e excluir para remover o cadastro.</p>
          </div>
        </header>

        {hospitais.length === 0 ? (
          <p className="note empty-state">Nenhum hospital encontrado.</p>
        ) : (
          <div className="list-panel">
            {hospitais.map((hospital) => (
              <div className="patient-card" key={hospital.id}>
                <div className="patient-info">
                  <h4>{hospital.nome}</h4>
                  <span>Cidade: {hospital.cidade} / {hospital.estado}</span>
                  <span>Email: {hospital.email || "Não informado"}</span>
                </div>
                <div className="action-buttons">
                  <Link
                    className="secondary-button"
                    to="/cadastros/hospitais/status"
                    title="Gerenciar status"
                  >
                    Status
                  </Link>
                  <button className="edit-button" onClick={() => setHospitalEmEdicao(hospital)} title="Editar hospital">✏️</button>
                  <button className="delete-button" onClick={() => excluirHospital(hospital.id)} title="Excluir hospital">🗑️</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

export default HospitaisPage;
