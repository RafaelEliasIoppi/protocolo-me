import React, { useState } from "react";
import autenticarService from "../services/autenticarService";

function Login({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [nome, setNome] = useState("");
  const [role, setRole] = useState("MEDICO");
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [mensagem, setMensagem] = useState("");

  const validarEmail = (email) => {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setErro("");
    setMensagem("");

    try {
      // Validações
      if (!email || !validarEmail(email)) {
        setErro("Email inválido");
        return;
      }
      if (!senha || senha.length < 6) {
        setErro("Senha deve ter pelo menos 6 caracteres");
        return;
      }

      setCarregando(true);

      if (isRegister) {
        if (!nome || nome.trim().length < 3) {
          setErro("Nome deve ter pelo menos 3 caracteres");
          setCarregando(false);
          return;
        }
        await autenticarService.registrar({ nome, email, senha, role });
        setMensagem("Usuário cadastrado com sucesso! Faça login.");
        setTimeout(() => setIsRegister(false), 2000);
      } else {
        await autenticarService.login(email, senha);
        setMensagem("Login realizado com sucesso!");
        setTimeout(() => onLogin(), 1500);
      }
    } catch (error) {
      const erroBackend = error.response?.data?.erro || error.response?.data?.mensagem;
      const mensagemErro = erroBackend === "Email já cadastrado"
        ? "Este email já está cadastrado. Use outro email para criar a conta."
        : erroBackend || (isRegister ? "Erro ao cadastrar usuário" : "Email ou senha inválidos");
      setErro(mensagemErro);
    } finally {
      setCarregando(false);
    }
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-hero">
          <h2>{isRegister ? "Crie sua conta" : "Bem-vindo de volta"}</h2>
          <p>Use seu login para acessar o sistema de protocolos de morte encefálica.</p>
        </div>

        {erro && <div className="erro-message" style={{ color: 'red', padding: '10px', marginBottom: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>{erro}</div>}
        {mensagem && <div className="sucesso-message" style={{ color: 'green', padding: '10px', marginBottom: '10px', backgroundColor: '#e6ffe6', borderRadius: '4px' }}>{mensagem}</div>}

        <div className="login-panel">
          <form onSubmit={handleSubmit}>
            {isRegister && (
              <>
                <div className="note" style={{ marginBottom: 8 }}>
                  Cadastro público liberado apenas para Médico e Enfermeiro. As demais funções são criadas pela administração.
                </div>
                <input 
                  type="text" 
                  className="input-field" 
                  placeholder="Nome" 
                  autoComplete="name"
                  value={nome} 
                  onChange={(e) => setNome(e.target.value)} 
                  disabled={carregando}
                />
                <select 
                  className="select-field" 
                  value={role} 
                  onChange={(e) => setRole(e.target.value)}
                  disabled={carregando}
                >
                  <option value="MEDICO">Médico</option>
                  <option value="ENFERMEIRO">Enfermeiro</option>
                </select>
              </>
            )}
            <input 
              type="email" 
              className="input-field" 
              placeholder="Email" 
              autoComplete={isRegister ? "email" : "username"}
              value={email} 
              onChange={(e) => setEmail(e.target.value)} 
              disabled={carregando}
            />
            <input 
              type="password" 
              className="input-field" 
              placeholder="Senha" 
              autoComplete={isRegister ? "new-password" : "current-password"}
              value={senha} 
              onChange={(e) => setSenha(e.target.value)} 
              disabled={carregando}
            />
            <button 
              type="submit" 
              className="primary-button"
              disabled={carregando}
            >
              {carregando ? 'Processando...' : (isRegister ? "Cadastrar" : "Entrar")}
            </button>
          </form>

          <button 
            type="button" 
            className="secondary-button" 
            onClick={() => setIsRegister(!isRegister)}
            disabled={carregando}
          >
            {isRegister ? "Já tem conta? Faça login" : "Não tem conta? Cadastre-se"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
