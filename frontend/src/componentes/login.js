import React, { useState } from "react";
import autenticarService from "../services/autenticarService";

function Login({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [nome, setNome] = useState("");
  const [role, setRole] = useState("USER");
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
      const mensagemErro = error.response?.data?.erro || error.response?.data?.mensagem || 
                          (isRegister ? "Erro ao cadastrar usuário" : "Email ou senha inválidos");
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
          <p>Use seu login para acessar o dashboard moderno da transportadora.</p>
        </div>

        {erro && <div className="erro-message" style={{ color: 'red', padding: '10px', marginBottom: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>{erro}</div>}
        {mensagem && <div className="sucesso-message" style={{ color: 'green', padding: '10px', marginBottom: '10px', backgroundColor: '#e6ffe6', borderRadius: '4px' }}>{mensagem}</div>}

        <div className="login-panel">
          <form onSubmit={handleSubmit}>
            {isRegister && (
              <>
                <input 
                  type="text" 
                  className="input-field" 
                  placeholder="Nome" 
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
                  <option value="USER">Usuário</option>
                  <option value="ADMIN">Administrador</option>
                </select>
              </>
            )}
            <input 
              type="email" 
              className="input-field" 
              placeholder="Email" 
              value={email} 
              onChange={(e) => setEmail(e.target.value)} 
              disabled={carregando}
            />
            <input 
              type="password" 
              className="input-field" 
              placeholder="Senha" 
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
