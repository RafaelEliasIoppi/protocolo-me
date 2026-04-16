import React, { useEffect, useState } from "react";
import CentralTransplantesForm from "./CentralTransplantesForm";
import centralTransplantesService from "../services/centralTransplantesService";

function CentraisPage() {
  const [centrais, setCentrais] = useState([]);
  const [centralEmEdicao, setCentralEmEdicao] = useState(null);
  const [erro, setErro] = useState("");

  const carregarCentrais = async () => {
    try {
      const response = await centralTransplantesService.listar();
      const dados = Array.isArray(response?.data) ? response.data : Array.isArray(response) ? response : [];
      setCentrais(dados);
      setErro("");
    } catch (e) {
      setErro("Erro ao carregar centrais.");
      setCentrais([]);
    }
  };

  useEffect(() => {
    carregarCentrais();
  }, []);

  const excluirCentral = async (id) => {
    if (!window.confirm("Deseja realmente excluir esta central?")) {
      return;
    }

    try {
      await centralTransplantesService.deletar(id);
      await carregarCentrais();
      if (centralEmEdicao?.id === id) {
        setCentralEmEdicao(null);
      }
    } catch (e) {
      setErro("Não foi possível excluir a central.");
    }
  };

  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Cadastro de Centrais</h1>
          <p>Tela separada para cadastro e gestão de centrais de transplantes.</p>
        </div>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <CentralTransplantesForm
        centralParaEditar={centralEmEdicao}
        onSuccess={() => {
          carregarCentrais();
          setCentralEmEdicao(null);
        }}
      />

      <div className="panel" style={{ marginTop: 24 }}>
        <header>
          <div>
            <h2>Centrais Cadastradas</h2>
            <p className="note">Selecione editar para atualizar os dados da central.</p>
          </div>
        </header>

        {centrais.length === 0 ? (
          <p className="note">Nenhuma central encontrada.</p>
        ) : (
          <div className="list-panel">
            {centrais.map((central) => (
              <div className="patient-card" key={central.id}>
                <div className="patient-info">
                  <h4>{central.nome}</h4>
                  <span>Cidade: {central.cidade} / {central.estado}</span>
                  <span>Coordenador: {central.coordenador || "Não informado"}</span>
                </div>
                <div className="action-buttons">
                  <button className="edit-button" onClick={() => setCentralEmEdicao(central)} title="Editar central">✏️</button>
                  <button className="delete-button" onClick={() => excluirCentral(central.id)} title="Excluir central">🗑️</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </section>
  );
}

export default CentraisPage;
