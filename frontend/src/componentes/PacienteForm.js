import React, { useEffect, useState } from "react";
import api from "../api/api";

function PacienteForm({ paciente, onSave, onCancel }) {
  const [formData, setFormData] = useState({
    nome: "",
    cpf: "",
    telefone: "",
    statusProtocolo: "Aberto",
    hospital: { id: "", nome: "" }
  });
  const [hospitais, setHospitais] = useState([]);

  useEffect(() => {
    if (paciente) {
      setFormData({
        nome: paciente.nome || "",
        cpf: paciente.cpf || "",
        telefone: paciente.telefone || "",
        statusProtocolo: paciente.statusProtocolo || "Aberto",
        hospital: paciente.hospital || { id: "", nome: "" }
      });
    }

    // Carregar lista de hospitais
    const fetchHospitais = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await api.get("/hospitais", {
          headers: { Authorization: `Bearer ${token}` },
        });
        setHospitais(response.data);
      } catch (error) {
        console.error("Erro ao buscar hospitais:", error);
      }
    };
    fetchHospitais();
  }, [paciente]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem("token");
      const dataToSend = {
        ...formData,
        hospital: { id: parseInt(formData.hospital.id) }
      };

      let response;
      if (paciente) {
        response = await api.put(`/pacientes/${paciente.id}`, dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
      } else {
        response = await api.post("/pacientes", dataToSend, {
          headers: { Authorization: `Bearer ${token}` },
        });
      }

      onSave(response.data);
      if (!paciente) {
        setFormData({
          nome: "",
          cpf: "",
          telefone: "",
          statusProtocolo: "Aberto",
          hospital: { id: "", nome: "" }
        });
      }
    } catch (error) {
      console.error("Erro ao salvar paciente:", error);
      alert("Erro ao salvar paciente.");
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    if (name === "hospitalId") {
      const selectedHospital = hospitais.find(h => h.id === parseInt(value));
      setFormData(prev => ({
        ...prev,
        hospital: { id: value, nome: selectedHospital?.nome || "" }
      }));
    } else {
      setFormData(prev => ({ ...prev, [name]: value }));
    }
  };

  return (
    <form onSubmit={handleSubmit} className="form-panel">
      <input
        type="text"
        name="nome"
        className="input-field"
        placeholder="Nome"
        value={formData.nome}
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="cpf"
        className="input-field"
        placeholder="CPF"
        value={formData.cpf}
        onChange={handleChange}
        required
      />
      <input
        type="text"
        name="telefone"
        className="input-field"
        placeholder="Telefone"
        value={formData.telefone}
        onChange={handleChange}
      />
      <select
        name="statusProtocolo"
        className="select-field"
        value={formData.statusProtocolo}
        onChange={handleChange}
      >
        <option value="Aberto">Aberto</option>
        <option value="Em andamento">Em andamento</option>
        <option value="Concluído">Concluído</option>
        <option value="Cancelado">Cancelado</option>
      </select>
      <select
        name="hospitalId"
        className="select-field"
        value={formData.hospital.id}
        onChange={handleChange}
        required
      >
        <option value="">Selecione um hospital</option>
        {hospitais.map(hospital => (
          <option key={hospital.id} value={hospital.id}>
            {hospital.nome} - {hospital.cidade}
          </option>
        ))}
      </select>
      <div className="action-row">
        <button type="submit" className="primary-button">
          {paciente ? "Atualizar" : "Cadastrar"}
        </button>
        {paciente && (
          <button type="button" className="secondary-button" onClick={onCancel}>
            Cancelar
          </button>
        )}
      </div>
    </form>
  );
}

export default PacienteForm;
