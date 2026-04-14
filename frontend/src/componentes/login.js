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
    <div>
      <h2>{isRegister ? "Cadastro" : "Login"}</h2>
      <form onSubmit={handleSubmit}>
        {isRegister && (
          <>
            <input type="text" placeholder="Nome" value={nome} onChange={(e) => setNome(e.target.value)} />
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="USER">Usuário</option>
              <option value="ADMIN">Administrador</option>
            </select>
          </>
        )}
        <input type="text" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input type="password" placeholder="Senha" value={senha} onChange={(e) => setSenha(e.target.value)} />
        <button type="submit">{isRegister ? "Cadastrar" : "Entrar"}</button>
      </form>
      <button onClick={() => setIsRegister(!isRegister)}>
        {isRegister ? "Já tem conta? Faça login" : "Não tem conta? Cadastre-se"}
      </button>
    </div>
  );
}

export default Login;
