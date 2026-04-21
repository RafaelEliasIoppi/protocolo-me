import React, { useEffect, useState } from "react";
import autenticarService from "../services/autenticarService";

const roleOpcoes = [
  { value: "ADMIN", label: "Administrador" },
  { value: "CENTRAL_TRANSPLANTES", label: "Central de Transplantes" },
  { value: "COORDENADOR_TRANSPLANTES", label: "Coordenador de Transplantes" },
  { value: "MEDICO", label: "Médico" },
  { value: "ENFERMEIRO", label: "Enfermeiro" },
];

const estadoInicialFormulario = {
  nome: "",
  email: "",
  senha: "",
  role: "CENTRAL_TRANSPLANTES",
};

const estadoInicialEdicao = {
  nome: "",
  email: "",
  role: "",
  ativo: true,
  crm: "",
  coren: "",
};

function UsuariosAdminPage() {
  const [formData, setFormData] = useState(estadoInicialFormulario);
  const [usuarioSelecionado, setUsuarioSelecionado] = useState(null);
  const [formEdicao, setFormEdicao] = useState(estadoInicialEdicao);
  const [novaSenha, setNovaSenha] = useState("");
  const [usuarios, setUsuarios] = useState([]);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [carregandoLista, setCarregandoLista] = useState(false);

  const carregarUsuarios = async () => {
    try {
      setCarregandoLista(true);
      const lista = await autenticarService.listarUsuarios();
      setUsuarios(Array.isArray(lista) ? lista : []);
    } catch (error) {
      setErro(error.response?.data?.erro || "Erro ao carregar usuários");
    } finally {
      setCarregandoLista(false);
    }
  };

  useEffect(() => {
    carregarUsuarios();
  }, []);

  const abrirEdicao = (usuario) => {
    setUsuarioSelecionado(usuario);
    setFormEdicao({
      nome: usuario.nome || "",
      email: usuario.email || "",
      role: usuario.role || "",
      ativo: usuario.ativo ?? true,
      crm: usuario.crm || "",
      coren: usuario.coren || "",
    });
    setNovaSenha("");
    setErro("");
    setSucesso("");
  };

  const fecharEdicao = () => {
    setUsuarioSelecionado(null);
    setFormEdicao(estadoInicialEdicao);
    setNovaSenha("");
  };

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
      await autenticarService.registrarAdmin(formData);
      setSucesso("Usuário cadastrado com sucesso!");
      setFormData(estadoInicialFormulario);
      await carregarUsuarios();
    } catch (error) {
      setErro(error.response?.data?.erro || "Erro ao cadastrar usuário");
    } finally {
      setCarregando(false);
    }
  };

  const salvarEdicao = async (e) => {
    e.preventDefault();
    if (!usuarioSelecionado) return;

    try {
      setCarregando(true);
      await autenticarService.atualizarUsuario(usuarioSelecionado.id, formEdicao);
      if (novaSenha.trim()) {
        await autenticarService.redefinirSenha(usuarioSelecionado.id, novaSenha.trim());
      }
      setSucesso("Usuário atualizado com sucesso!");
      await carregarUsuarios();
      fecharEdicao();
    } catch (error) {
      setErro(error.response?.data?.erro || error.response?.data?.mensagem || "Erro ao atualizar usuário");
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
            <p className="note">Use esta área para criar Central de Transplantes, Coordenador, Médico, Enfermeiro e Administrador.</p>
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

      <div className="panel" style={{ marginTop: 24 }}>
        <header>
          <div>
            <h2>Usuários Cadastrados</h2>
            <p className="note">Clique em editar para alterar dados ou redefinir senha.</p>
          </div>
        </header>

        {carregandoLista ? (
          <p className="note">Carregando usuários...</p>
        ) : (
          <div className="lista-usuarios" style={{ display: "grid", gap: 12 }}>
            {usuarios.map((usuario) => (
              <div key={usuario.id} className="central-card" style={{ gridTemplateColumns: "1fr auto" }}>
                <div className="central-info">
                  <h4>{usuario.nome}</h4>
                  <span>{usuario.email}</span>
                  <span>Perfil: {usuario.role}</span>
                  <span>Status: {usuario.ativo ? "Ativo" : "Inativo"}</span>
                </div>
                <div className="central-actions">
                  <button className="edit-button" onClick={() => abrirEdicao(usuario)} title="Editar usuário">✏️</button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {usuarioSelecionado && (
        <div className="panel" style={{ marginTop: 24 }}>
          <header>
            <div>
              <h2>Editar Usuário</h2>
              <p className="note">Alterar dados, perfil, status e senha.</p>
            </div>
          </header>

          <form className="form-panel card" onSubmit={salvarEdicao}>
            <div className="form-row">
              <input
                className="input-field"
                value={formEdicao.nome}
                onChange={(e) => setFormEdicao((prev) => ({ ...prev, nome: e.target.value }))}
                placeholder="Nome completo"
              />
              <input
                className="input-field"
                type="email"
                value={formEdicao.email}
                onChange={(e) => setFormEdicao((prev) => ({ ...prev, email: e.target.value }))}
                placeholder="Email"
              />
            </div>

            <div className="form-row">
              <select
                className="select-field"
                value={formEdicao.role}
                onChange={(e) => setFormEdicao((prev) => ({ ...prev, role: e.target.value }))}
              >
                {roleOpcoes.map((opcao) => (
                  <option key={opcao.value} value={opcao.value}>{opcao.label}</option>
                ))}
              </select>
              <select
                className="select-field"
                value={String(formEdicao.ativo)}
                onChange={(e) => setFormEdicao((prev) => ({ ...prev, ativo: e.target.value === "true" }))}
              >
                <option value="true">Ativo</option>
                <option value="false">Inativo</option>
              </select>
            </div>

            {(formEdicao.role === "MEDICO" || formEdicao.role === "ADMIN" || formEdicao.role === "COORDENADOR_TRANSPLANTES") && (
              <div className="form-row">
                <input
                  className="input-field"
                  value={formEdicao.crm}
                  onChange={(e) => setFormEdicao((prev) => ({ ...prev, crm: e.target.value }))}
                  placeholder="CRM"
                />
                <input
                  className="input-field"
                  value={formEdicao.coren}
                  onChange={(e) => setFormEdicao((prev) => ({ ...prev, coren: e.target.value }))}
                  placeholder="COREN"
                />
              </div>
            )}

            <div className="form-row">
              <input
                className="input-field"
                type="password"
                value={novaSenha}
                onChange={(e) => setNovaSenha(e.target.value)}
                placeholder="Nova senha (opcional)"
              />
            </div>

            <div className="action-row" style={{ justifyContent: "flex-start" }}>
              <button className="primary-button" type="submit" disabled={carregando}>
                {carregando ? "Salvando..." : "Salvar alterações"}
              </button>
              <button className="secondary-button" type="button" onClick={fecharEdicao} disabled={carregando}>
                Cancelar
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}

export default UsuariosAdminPage;