import React, { useState } from "react";
import autenticarService from "../services/autenticarService";

function Login({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [form, setForm] = useState({
    nome: "",
    email: "",
    senha: "",
    role: "MEDICO"
  });

  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  const validarEmail = (email) => {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  };

  const handleChange = (campo, valor) => {
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

    if (isRegister && (!form.nome || form.nome.trim().length < 3)) {
      return "Nome deve ter pelo menos 3 caracteres";
    }

    return null;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    limparMensagens();

    const erroValidacao = validarFormulario();
    if (erroValidacao) {
      setErro(erroValidacao);
      return;
    }

    setCarregando(true);

    try {
      const emailNormalizado = form.email.trim().toLowerCase();

      if (isRegister) {
        await autenticarService.registrar({
          nome: form.nome.trim(),
          email: emailNormalizado,
          senha: form.senha,
          role: form.role
        });

        setMensagem("Usuário cadastrado com sucesso!");
        setTimeout(() => setIsRegister(false), 1500);

      } else {
        const response = await autenticarService.login(
          emailNormalizado,
          form.senha
        );

        // 🔐 SALVAR TOKEN
        localStorage.setItem("token", response.token);
        localStorage.setItem("usuario", JSON.stringify(response.usuario));

        setMensagem("Login realizado com sucesso!");

        // limpa senha da memória
        setForm((prev) => ({ ...prev, senha: "" }));

        setTimeout(() => onLogin(), 1000);
      }

    } catch (error) {
      const erroBackend =
        error.response?.data?.erro ||
        error.response?.data?.mensagem;

      setErro(
        erroBackend === "Email já cadastrado"
          ? "Este email já está em uso."
          : erroBackend || "Erro na operação"
      );
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-hero">
          <h2>{isRegister ? "Criar conta" : "Login"}</h2>
          <p>
            {isRegister
              ? "Cadastre um novo usuário para acessar o sistema."
              : "Acesse o sistema com seu email e senha."}
          </p>
        </div>

        <div className="login-panel">
          {erro && <div className="mensagem erro">{erro}</div>}
          {mensagem && <div className="mensagem sucesso">{mensagem}</div>}

          <form onSubmit={handleSubmit}>

            {isRegister && (
              <>
                <input
                  type="text"
                  placeholder="Nome"
                  value={form.nome}
                  onChange={(e) => handleChange("nome", e.target.value)}
                  disabled={carregando}
                />

                <select
                  value={form.role}
                  onChange={(e) => handleChange("role", e.target.value)}
                  disabled={carregando}
                >
                  <option value="MEDICO">Médico</option>
                  <option value="ENFERMEIRO">Enfermeiro</option>
                </select>
              </>
            )}

            <input
              type="email"
              placeholder="Email"
              value={form.email}
              onChange={(e) => handleChange("email", e.target.value)}
              disabled={carregando}
            />

            <input
              type="password"
              placeholder="Senha"
              value={form.senha}
              onChange={(e) => handleChange("senha", e.target.value)}
              disabled={carregando}
            />

            <button type="submit" disabled={carregando}>
              {carregando
                ? "Processando..."
                : isRegister
                ? "Cadastrar"
                : "Entrar"}
            </button>
          </form>

          <div style={{ marginTop: 10 }}>
            <button
              className="secondary-button"
              onClick={() => {
                setIsRegister(!isRegister);
                limparMensagens();
              }}
              disabled={carregando}
            >
              {isRegister ? "Voltar ao login" : "Criar conta"}
            </button>
          </div>
        </div>

      </div>
    </div>
  );
}

export default Login;