import React, { useState } from "react";
import api from "../api/api";

function Login({ onLogin }) {
  const [isRegister, setIsRegister] = useState(false);
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [nome, setNome] = useState("");
  const [role, setRole] = useState("USER");

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (isRegister) {
        await api.post("/usuarios", { nome, email, senha, role });
        alert("Usuário cadastrado! Faça login.");
        setIsRegister(false);
      } else {
        const response = await api.post("/usuarios/login", { email, senha });
        localStorage.setItem("token", response.data.token);
        onLogin();
      }
    } catch (error) {
      alert(isRegister ? "Erro no cadastro" : "Login inválido");
    }
  };

  return (
    <div className="login-screen">
      <div className="login-card">
        <div className="login-hero">
          <h2>{isRegister ? "Crie sua conta" : "Bem-vindo de volta"}</h2>
          <p>Use seu login para acessar o dashboard moderno da transportadora.</p>
        </div>

        <div className="login-panel">
          <form onSubmit={handleSubmit}>
            {isRegister && (
              <>
                <input type="text" className="input-field" placeholder="Nome" value={nome} onChange={(e) => setNome(e.target.value)} />
                <select className="select-field" value={role} onChange={(e) => setRole(e.target.value)}>
                  <option value="USER">Usuário</option>
                  <option value="ADMIN">Administrador</option>
                </select>
              </>
            )}
            <input type="text" className="input-field" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
            <input type="password" className="input-field" placeholder="Senha" value={senha} onChange={(e) => setSenha(e.target.value)} />
            <button type="submit" className="primary-button">{isRegister ? "Cadastrar" : "Entrar"}</button>
          </form>

          <button type="button" className="secondary-button" onClick={() => setIsRegister(!isRegister)}>
            {isRegister ? "Já tem conta? Faça login" : "Não tem conta? Cadastre-se"}
          </button>
        </div>
      </div>
    </div>
  );
}

export default Login;
