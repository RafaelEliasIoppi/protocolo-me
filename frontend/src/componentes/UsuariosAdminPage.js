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
    <section>

      <h1>Administração de Usuários</h1>

      {erro && <div className="mensagem erro">{erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      {/* CADASTRO */}
      <form onSubmit={handleSubmit}>
        <input
          placeholder="Nome"
          value={formData.nome}
          onChange={(e) =>
            setFormData({ ...formData, nome: e.target.value })
          }
        />

        <input
          placeholder="Email"
          value={formData.email}
          onChange={(e) =>
            setFormData({ ...formData, email: e.target.value })
          }
        />

        <input
          type="password"
          placeholder="Senha"
          value={formData.senha}
          onChange={(e) =>
            setFormData({ ...formData, senha: e.target.value })
          }
        />

        <select
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

        <button disabled={carregando}>
          {carregando ? "Salvando..." : "Cadastrar"}
        </button>
      </form>

      {/* LISTA */}
      {carregandoLista ? (
        <p>Carregando...</p>
      ) : usuarios.length === 0 ? (
        <p>Nenhum usuário cadastrado</p>
      ) : (
        usuarios.map((u) => (
          <div key={u.id}>
            <b>{u.nome}</b> ({u.email}) - {u.role}
            <button onClick={() => abrirEdicao(u)}>Editar</button>
          </div>
        ))
      )}

      {/* EDIÇÃO */}
      {usuarioSelecionado && (
        <form onSubmit={salvarEdicao}>
          <input
            value={formEdicao.nome}
            onChange={(e) =>
              setFormEdicao({ ...formEdicao, nome: e.target.value })
            }
          />

          <input
            value={formEdicao.email}
            onChange={(e) =>
              setFormEdicao({ ...formEdicao, email: e.target.value })
            }
          />

          <input
            type="password"
            placeholder="Nova senha"
            value={novaSenha}
            onChange={(e) => setNovaSenha(e.target.value)}
          />

          <button disabled={carregando}>
            {carregando ? "Salvando..." : "Salvar"}
          </button>

          <button type="button" onClick={fecharEdicao}>
            Cancelar
          </button>
        </form>
      )}
    </section>
  );
}

export default UsuariosAdminPage;