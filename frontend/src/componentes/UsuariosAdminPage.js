import { useEffect, useMemo, useState, useCallback } from "react";
import autenticarService from "../services/autenticarService";
import { getApiErrorMessage } from "../utils/apiError";

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
  role: "MEDICO",
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

  const [formData, setFormData] = useState(
    estadoInicialFormulario
  );

  const [usuarioSelecionado, setUsuarioSelecionado] =
    useState(null);

  const [formEdicao, setFormEdicao] =
    useState(estadoInicialEdicao);

  const [novaSenha, setNovaSenha] = useState("");

  const [usuarios, setUsuarios] = useState([]);

  const [termoBusca, setTermoBusca] = useState("");

  const [filtroRole, setFiltroRole] =
    useState("TODOS");

  const [erro, setErro] = useState("");

  const [sucesso, setSucesso] = useState("");

  const [carregando, setCarregando] =
    useState(false);

  const [carregandoLista, setCarregandoLista] =
    useState(false);

  const limparMensagens = () => {
    setErro("");
    setSucesso("");
  };

  const normalizarEmail = (email) =>
    email.trim().toLowerCase();

  const carregarUsuarios = useCallback(async () => {

    try {

      setCarregandoLista(true);

      const lista =
        await autenticarService.listarUsuarios();

      setUsuarios(
        Array.isArray(lista) ? lista : []
      );

    } catch (error) {

      setErro(
        getApiErrorMessage(
          error,
          "Erro ao carregar usuários"
        )
      );

    } finally {

      setCarregandoLista(false);

    }

  }, []);

  useEffect(() => {

    setFormData(estadoInicialFormulario);

    setUsuarioSelecionado(null);

    limparMensagens();

    carregarUsuarios();

  }, [carregarUsuarios]);

  const usuariosFiltrados = useMemo(() => {

    const busca =
      termoBusca.trim().toLowerCase();

    return usuarios.filter((u) => {

      const nome =
        (u.nome || "").toLowerCase();

      const email =
        (u.email || "").toLowerCase();

      const role = u.role || "";

      const passaRole =
        filtroRole === "TODOS" ||
        role === filtroRole;

      const passaBusca =
        !busca ||
        nome.includes(busca) ||
        email.includes(busca);

      return passaRole && passaBusca;

    });

  }, [usuarios, termoBusca, filtroRole]);

  const criarUsuario = async (e) => {

    e.preventDefault();

    limparMensagens();

    if (
      !formData.nome ||
      !formData.email ||
      !formData.senha
    ) {

      setErro("Preencha todos os campos");

      return;
    }

    try {

      setCarregando(true);

      await autenticarService.registrarAdmin({
        ...formData,
        nome: formData.nome.trim(),
        email: normalizarEmail(
          formData.email
        ),
      });

      setSucesso(
        "Usuário cadastrado com sucesso!"
      );

      setFormData(
        estadoInicialFormulario
      );

      await carregarUsuarios();

    } catch (error) {

      setErro(
        getApiErrorMessage(
          error,
          "Erro ao cadastrar usuário"
        )
      );

    } finally {

      setCarregando(false);

    }
  };

  const abrirEdicao = (usuario) => {

    limparMensagens();

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

  };

  const fecharEdicao = () => {

    setUsuarioSelecionado(null);

    setFormEdicao(
      estadoInicialEdicao
    );

    setNovaSenha("");

    limparMensagens();

  };

  const salvarEdicao = async (e) => {

    e.preventDefault();

    if (!usuarioSelecionado) return;

    limparMensagens();

    try {

      if (novaSenha.trim()) {

        const confirmar =
          window.confirm(
            "Deseja realmente redefinir a senha?"
          );

        if (!confirmar) {
          return;
        }
      }

      setCarregando(true);

      await autenticarService.atualizarUsuario(
        usuarioSelecionado.id,
        {
          ...formEdicao,
          email: normalizarEmail(
            formEdicao.email
          ),
        }
      );

      if (novaSenha.trim()) {

        await autenticarService.redefinirSenha(
          usuarioSelecionado.id,
          novaSenha.trim()
        );
      }

      setSucesso(
        "Usuário atualizado com sucesso!"
      );

      fecharEdicao();

      await carregarUsuarios();

    } catch (error) {

      setErro(
        getApiErrorMessage(
          error,
          "Erro ao atualizar usuário"
        )
      );

    } finally {

      setCarregando(false);

    }
  };

  return (
    <section className="usuarios-admin-page">

      <div className="brand-bar">

        <div>

          <h1>
            Administração de Usuários
          </h1>

          <p>
            Cadastre, atualize e gerencie
            perfis de acesso do sistema.
          </p>

        </div>

      </div>

      {erro && (
        <div className="mensagem erro">
          {erro}
        </div>
      )}

      {sucesso && (
        <div className="mensagem sucesso">
          {sucesso}
        </div>
      )}

      <div className="overview-layout">

        <div className="panel usuarios-panel">

          <header>

            <div>

              <h2>
                Cadastrar Usuário
              </h2>

              <p className="note">
                Disponível apenas para perfil
                administrador.
              </p>

            </div>

          </header>

          <form
            className="usuarios-form"
            onSubmit={criarUsuario}
            autoComplete="off"
          >

            <input
              className="input-field"
              placeholder="Nome"
              autoComplete="off"
              value={formData.nome}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  nome: e.target.value
                }))
              }
            />

            <input
              className="input-field"
              type="email"
              placeholder="Email"
              autoComplete="off"
              value={formData.email}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  email: e.target.value
                }))
              }
            />

            <input
              className="input-field"
              type="password"
              placeholder="Senha"
              autoComplete="new-password"
              value={formData.senha}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  senha: e.target.value
                }))
              }
            />

            <select
              className="select-field"
              value={formData.role}
              onChange={(e) =>
                setFormData((prev) => ({
                  ...prev,
                  role: e.target.value
                }))
              }
            >

              {roleOpcoes.map((r) => (
                <option
                  key={r.value}
                  value={r.value}
                >
                  {r.label}
                </option>
              ))}

            </select>

            <button
              className="primary-button"
              disabled={carregando}
            >
              {carregando
                ? "Salvando..."
                : "Cadastrar"}
            </button>

          </form>

        </div>

        <div className="panel usuarios-panel">

          <header>

            <div>

              <h2>
                Usuários Cadastrados
              </h2>

              <p className="note">
                Selecione um usuário para editar
                dados e permissões.
              </p>

            </div>

          </header>

          <div className="usuarios-filtros">

            <input
              className="input-field"
              type="text"
              placeholder="Buscar por nome ou email"
              value={termoBusca}
              onChange={(e) =>
                setTermoBusca(e.target.value)
              }
            />

            <select
              className="select-field"
              value={filtroRole}
              onChange={(e) =>
                setFiltroRole(e.target.value)
              }
            >

              <option value="TODOS">
                Todos os perfis
              </option>

              {roleOpcoes.map((r) => (
                <option
                  key={r.value}
                  value={r.value}
                >
                  {r.label}
                </option>
              ))}

            </select>

          </div>

          <p className="note usuarios-resumo-lista">

            Exibindo {usuariosFiltrados.length}
            {" "}de{" "}
            {usuarios.length} usuários

          </p>

          {carregandoLista ? (

            <p>Carregando...</p>

          ) : usuarios.length === 0 ? (

            <p>
              Nenhum usuário cadastrado
            </p>

          ) : usuariosFiltrados.length === 0 ? (

            <p>
              Nenhum usuário encontrado
              com os filtros atuais
            </p>

          ) : (

            <div className="usuarios-lista">

              {usuariosFiltrados.map((u) => {

                const roleAtual =
                  roleOpcoes.find(
                    (r) => r.value === u.role
                  );

                return (
                  <div
                    key={u.id}
                    className="usuario-card"
                  >

                    <div className="usuario-card-info">

                      <strong>
                        {u.nome}
                      </strong>

                      <span>
                        {u.email}
                      </span>

                      <div className="usuario-meta">

                        <span className="usuario-role-badge">
                          {roleAtual?.label || u.role}
                        </span>

                        <span
                          className={`usuario-status ${
                            u.ativo
                              ? "ativo"
                              : "inativo"
                          }`}
                        >
                          {u.ativo
                            ? "Ativo"
                            : "Inativo"}
                        </span>

                      </div>

                    </div>

                    <button
                      className="secondary-button"
                      type="button"
                      onClick={() =>
                        abrirEdicao(u)
                      }
                    >
                      Editar
                    </button>

                  </div>
                );
              })}

            </div>
          )}

        </div>

      </div>

      {usuarioSelecionado && (

        <div className="panel usuarios-panel usuarios-edicao-panel">

          <header>

            <div>

              <h2>
                Editando Usuário
              </h2>

              <p className="note">
                Atualize os campos e salve
                as alterações.
              </p>

            </div>

          </header>

          <form
            className="usuarios-form"
            onSubmit={salvarEdicao}
            autoComplete="off"
          >

            <input
              className="input-field"
              autoComplete="off"
              value={formEdicao.nome}
              onChange={(e) =>
                setFormEdicao((prev) => ({
                  ...prev,
                  nome: e.target.value
                }))
              }
            />

            <input
              className="input-field"
              type="email"
              autoComplete="off"
              value={formEdicao.email}
              onChange={(e) =>
                setFormEdicao((prev) => ({
                  ...prev,
                  email: e.target.value
                }))
              }
            />

            <select
              className="select-field"
              value={formEdicao.role}
              onChange={(e) =>
                setFormEdicao((prev) => ({
                  ...prev,
                  role: e.target.value
                }))
              }
            >

              {roleOpcoes.map((r) => (
                <option
                  key={r.value}
                  value={r.value}
                >
                  {r.label}
                </option>
              ))}

            </select>

            <input
              className="input-field"
              type="password"
              placeholder="Nova senha (opcional)"
              autoComplete="new-password"
              value={novaSenha}
              onChange={(e) =>
                setNovaSenha(e.target.value)
              }
            />

            <label className="usuario-ativo-toggle">

              <input
                type="checkbox"
                checked={formEdicao.ativo}
                onChange={(e) =>
                  setFormEdicao((prev) => ({
                    ...prev,
                    ativo: e.target.checked
                  }))
                }
              />

              Usuário ativo

            </label>

            <div className="action-row">

              <button
                className="primary-button"
                disabled={carregando}
              >
                {carregando
                  ? "Salvando..."
                  : "Salvar"}
              </button>

              <button
                className="secondary-button"
                type="button"
                onClick={fecharEdicao}
              >
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
