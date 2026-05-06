import { useState } from "react";
import autenticarService from "../services/autenticarService";
import "../styles/LoginPage.css";
import { getApiErrorMessage } from "../utils/apiError";

const estadoInicialFormulario = {
  nome: "",
  email: "",
  senha: "",
  role: "MEDICO"
};

function LoginPage({ onLogin }) {

  const [form, setForm] = useState(estadoInicialFormulario);

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  const validarEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const atualizarCampoConta = (campo, valor) => {
    setForm((prev) => ({
      ...prev,
      [campo]: valor
    }));
  };

  const limparMensagens = () => {
    setErro("");
    setMensagem("");
  };

  const validarFormulario = () => {

    if (!form.email || !validarEmail(form.email)) {
      return "Email inválido";
    }

    if (!form.senha || form.senha.length < 6) {
      return "Senha deve ter pelo menos 6 caracteres";
    }

    return null;
  };

  const fazerLogin = async (e) => {

    e.preventDefault();

    limparMensagens();

    const erroValidacao = validarFormulario();

    if (erroValidacao) {
      setErro(erroValidacao);
      return;
    }

    setCarregando(true);

    try {

      const emailNormalizado =
        form.email.trim().toLowerCase();

      const response = await autenticarService.login(
        emailNormalizado,
        form.senha
      );

      // 🔐 TOKEN
      localStorage.setItem(
        "token",
        response.token
      );

      // 🔐 USUÁRIO
      localStorage.setItem(
        "usuario",
        JSON.stringify(response.usuario)
      );

      setMensagem("Login realizado com sucesso!");

      // limpa senha memória
      setForm((prev) => ({
        ...prev,
        senha: ""
      }));

      setTimeout(() => onLogin(), 1000);

    } catch (error) {

      const erroBackend =
        getApiErrorMessage(error);

      setErro(
        erroBackend || "Erro ao realizar login"
      );

    } finally {

      setCarregando(false);

    }
  };

  return (
    <div className="login-screen login-page">

      <div className="login-card">

        <div className="login-hero">

          <h2>Login</h2>

          <p>
            Acesse o sistema com seu email e senha.
          </p>

        </div>

        <div className="login-panel">

          {erro && (
            <div className="mensagem erro">
              {erro}
            </div>
          )}

          {mensagem && (
            <div className="mensagem sucesso">
              {mensagem}
            </div>
          )}

          <form onSubmit={fazerLogin}>

            <input
              type="email"
              placeholder="Email"
              value={form.email}
              onChange={(e) =>
                atualizarCampoConta(
                  "email",
                  e.target.value
                )
              }
              disabled={carregando}
            />

            <input
              type="password"
              placeholder="Senha"
              value={form.senha}
              onChange={(e) =>
                atualizarCampoConta(
                  "senha",
                  e.target.value
                )
              }
              disabled={carregando}
            />

            <button
              type="submit"
              disabled={carregando}
            >
              {carregando
                ? "Entrando..."
                : "Entrar"}
            </button>

          </form>

        </div>

      </div>

    </div>
  );
}

export default LoginPage;
