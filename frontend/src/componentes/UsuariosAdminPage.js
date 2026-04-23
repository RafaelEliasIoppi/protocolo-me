import React, { useEffect, useState } from "react";
import autenticarService from "../services/autenticarService";

const roleOpcoes = [
  { value: "ADMIN", label: "Administrador" },
  { value: "CENTRAL_TRANSPLANTES", label: "Central de Transplantes" },
  { value: "COORDENADOR_TRANSPLANTES", label: "Coordenador" },
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

  // =========================
  // LOAD
  // =========================
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

  // =========================
  // UTIL
  // =========================
  const limparMensagens = () => {
    setErro("");
    setSucesso("");
  };

  const normalizarEmail = (email) =>
    email.trim().toLowerCase();

  // =========================
  // CADASTRO
  // =========================
  const handleSubmit = async (e) => {
    e.preventDefault();
    limparMensagens();

    if (!formData.nome || !formData.email || !formData.senha) {
      setErro("Preencha todos os campos");
      return;
    }

    try {
      setCarregando(true);

      await autenticarService.registrarAdmin({
        ...formData,
        email: normalizarEmail(formData.email),
        nome: formData.nome.trim(),
      });

      setSucesso("Usuário cadastrado com sucesso!");
      setFormData(estadoInicialFormulario);

      await carregarUsuarios();
    } catch (error) {
      setErro(error.response?.data?.erro || "Erro ao cadastrar usuário");
    } finally {
      setCarregando(false);
    }
  };

  // =========================
  // EDIÇÃO
  // =========================
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
    limparMensagens();
  };

  const fecharEdicao = () => {
    setUsuarioSelecionado(null);
    setFormEdicao(estadoInicialEdicao);
    setNovaSenha("");
  };

  const salvarEdicao = async (e) => {
    e.preventDefault();

    if (!usuarioSelecionado) return;

    try {
      setCarregando(true);

      await autenticarService.atualizarUsuario(usuarioSelecionado.id, {
        ...formEdicao,
        email: normalizarEmail(formEdicao.email),
      });

      if (novaSenha.trim()) {
        if (!window.confirm("Deseja realmente redefinir a senha?")) return;

        await autenticarService.redefinirSenha(
          usuarioSelecionado.id,
          novaSenha.trim()
        );
      }

      setSucesso("Usuário atualizado com sucesso!");
      fecharEdicao();
      await carregarUsuarios();

    } catch (error) {
      setErro(
        error.response?.data?.erro ||
        error.response?.data?.mensagem ||
        "Erro ao atualizar usuário"
      );
    } finally {
      setCarregando(false);
    }
  };

  // =========================
  // UI
  // =========================
  return (
    <section className="usuarios-admin-page">
      <div className="brand-bar">
        <div>
          <h1>Administração de Usuários</h1>
          <p>Cadastre, atualize e gerencie perfis de acesso do sistema.</p>
        </div>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      <div className="overview-layout">
        <div className="panel usuarios-panel">
          <header>
            <div>
              <h2>Cadastrar Usuário</h2>
              <p className="note">Disponível apenas para perfil administrador.</p>
            </div>
          </header>

          <form className="usuarios-form" onSubmit={handleSubmit}>
            <input
              className="input-field"
              placeholder="Nome"
              value={formData.nome}
              onChange={(e) =>
                setFormData({ ...formData, nome: e.target.value })
              }
            />

            <input
              className="input-field"
              type="email"
              placeholder="Email"
              value={formData.email}
              onChange={(e) =>
                setFormData({ ...formData, email: e.target.value })
              }
            />

            <input
              className="input-field"
              type="password"
              placeholder="Senha"
              value={formData.senha}
              onChange={(e) =>
                setFormData({ ...formData, senha: e.target.value })
              }
            />

            <select
              className="select-field"
              value={formData.role}
              onChange={(e) =>
                setFormData({ ...formData, role: e.target.value })
              }
            >
              {roleOpcoes.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>

            <button className="primary-button" disabled={carregando}>
              {carregando ? "Salvando..." : "Cadastrar"}
            </button>
          </form>
        </div>

        <div className="panel usuarios-panel">
          <header>
            <div>
              <h2>Usuários Cadastrados</h2>
              <p className="note">Selecione um usuário para editar dados e permissões.</p>
            </div>
          </header>

          {carregandoLista ? (
            <p>Carregando...</p>
          ) : usuarios.length === 0 ? (
            <p>Nenhum usuário cadastrado</p>
          ) : (
            <div className="usuarios-lista">
              {usuarios.map((u) => (
                <div key={u.id} className="usuario-card">
                  <div className="usuario-card-info">
                    <strong>{u.nome}</strong>
                    <span>{u.email}</span>
                    <span className="note">{u.role}</span>
                  </div>
                  <button className="secondary-button" onClick={() => abrirEdicao(u)}>
                    Editar
                  </button>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>

      {/* EDIÇÃO */}
      {usuarioSelecionado && (
        <div className="panel usuarios-panel usuarios-edicao-panel">
          <header>
            <div>
              <h2>Editando Usuário</h2>
              <p className="note">Atualize os campos e salve as alterações.</p>
            </div>
          </header>

          <form className="usuarios-form" onSubmit={salvarEdicao}>
            <input
              className="input-field"
              value={formEdicao.nome}
              onChange={(e) =>
                setFormEdicao({ ...formEdicao, nome: e.target.value })
              }
            />

            <input
              className="input-field"
              type="email"
              value={formEdicao.email}
              onChange={(e) =>
                setFormEdicao({ ...formEdicao, email: e.target.value })
              }
            />

            <select
              className="select-field"
              value={formEdicao.role}
              onChange={(e) =>
                setFormEdicao({ ...formEdicao, role: e.target.value })
              }
            >
              {roleOpcoes.map((r) => (
                <option key={r.value} value={r.value}>
                  {r.label}
                </option>
              ))}
            </select>

            <input
              className="input-field"
              type="password"
              placeholder="Nova senha (opcional)"
              value={novaSenha}
              onChange={(e) => setNovaSenha(e.target.value)}
            />

            <label className="usuario-ativo-toggle">
              <input
                type="checkbox"
                checked={formEdicao.ativo}
                onChange={(e) =>
                  setFormEdicao({ ...formEdicao, ativo: e.target.checked })
                }
              />
              Usuário ativo
            </label>

            <div className="action-row">
              <button className="primary-button" disabled={carregando}>
                {carregando ? "Salvando..." : "Salvar"}
              </button>

              <button className="secondary-button" type="button" onClick={fecharEdicao}>
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