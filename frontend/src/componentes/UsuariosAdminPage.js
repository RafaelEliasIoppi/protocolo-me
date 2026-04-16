import React, { useState } from "react";
import apiClient from "../services/apiClient";

const roleOpcoes = [
  { value: "ADMIN", label: "Administrador" },
  { value: "CENTRAL_TRANSPLANTES", label: "Central de Transplantes" },
  { value: "COORDENADOR_TRANSPLANTES", label: "Coordenador de Transplantes" },
  { value: "MEDICO", label: "Médico" },
  { value: "ENFERMEIRO", label: "Enfermeiro" },
];

function UsuariosAdminPage() {
  const [formData, setFormData] = useState({
    nome: "",
    email: "",
    senha: "",
    role: "CENTRAL_TRANSPLANTES",
  });
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [carregando, setCarregando] = useState(false);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro("");
    setSucesso("");

    if (!formData.nome || !formData.email || !formData.senha || !formData.role) {
      setErro("Preencha todos os campos");
      return;
    }

    try {
      setCarregando(true);
      await apiClient.post("/api/usuarios/admin/registrar", formData);
      setSucesso("Usuário cadastrado com sucesso!");
      setFormData({
        nome: "",
        email: "",
        senha: "",
        role: "CENTRAL_TRANSPLANTES",
      });
    } catch (error) {
      setErro(error.response?.data?.erro || "Erro ao cadastrar usuário");
    } finally {
      setCarregando(false);
    }
  };

  return (
    <section>
      <div className="brand-bar">
        <div>
          <h1>Administração de Usuários</h1>
          <p>Cadastro exclusivo para perfis administrativos e operacionais.</p>
        </div>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      <div className="panel">
        <header>
          <div>
            <h2>Novo Usuário</h2>
            <p className="note">Use esta área para criar Central de Transplantes, Coordenador e Administrador.</p>
          </div>
        </header>

        <form className="form-panel card" onSubmit={handleSubmit}>
          <div className="form-row">
            <input
              className="input-field"
              name="nome"
              value={formData.nome}
              onChange={handleChange}
              placeholder="Nome completo"
            />
            <input
              className="input-field"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              placeholder="Email"
            />
          </div>

          <div className="form-row">
            <input
              className="input-field"
              name="senha"
              type="password"
              value={formData.senha}
              onChange={handleChange}
              placeholder="Senha"
            />
            <select className="select-field" name="role" value={formData.role} onChange={handleChange}>
              {roleOpcoes.map((opcao) => (
                <option key={opcao.value} value={opcao.value}>{opcao.label}</option>
              ))}
            </select>
          </div>

          <button className="primary-button" type="submit" disabled={carregando}>
            {carregando ? "Cadastrando..." : "Cadastrar Usuário"}
          </button>
        </form>
      </div>
    </section>
  );
}

export default UsuariosAdminPage;