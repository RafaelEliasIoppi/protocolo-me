import React, { useState } from "react";
import apiClient from "../services/apiClient";
import "../styles/Dashboard.css";

function AlterarSenhaPage() {
  const [senhaAtual, setSenhaAtual] = useState("");
  const [novaSenha, setNovaSenha] = useState("");
  const [confirmarSenha, setConfirmarSenha] = useState("");
  const [alterandoSenha, setAlterandoSenha] = useState(false);
  const [mensagemSenha, setMensagemSenha] = useState("");
  const [erroSenha, setErroSenha] = useState("");

  const alterarSenha = async (e) => {
    e.preventDefault();
    setErroSenha("");
    setMensagemSenha("");

    // Validações básicas no front
    if (!senhaAtual || senhaAtual.trim().length < 6) {
      setErroSenha("Informe sua senha atual corretamente.");
      return;
    }
    if (!novaSenha || novaSenha.trim().length < 6) {
      setErroSenha("A nova senha deve ter pelo menos 6 caracteres.");
      return;
    }
    if (novaSenha !== confirmarSenha) {
      setErroSenha("A nova senha e a confirmação não conferem.");
      return;
    }

    try {
      setAlterandoSenha(true);
      await apiClient.patch("/api/usuarios/minha-senha", {
        senhaAtual,
        senhaNova: novaSenha,
        confirmarSenha,
      });
      setMensagemSenha("Senha alterada com sucesso. Use a nova senha no próximo login.");
      setSenhaAtual("");
      setNovaSenha("");
      setConfirmarSenha("");
    } catch (error) {
      setErroSenha(error.response?.data?.erro || "Erro ao alterar senha");
    } finally {
      setAlterandoSenha(false);
    }
  };

  return (
    <section className="dashboard">
      <div className="panel">
        <h2>Alterar Senha</h2>
        <p className="note">Informe a senha atual e digite a nova senha duas vezes.</p>
        {erroSenha && <div className="mensagem erro">{erroSenha}</div>}
        {mensagemSenha && <div className="mensagem sucesso">{mensagemSenha}</div>}
        <form className="form-panel card" onSubmit={alterarSenha}>
          <div className="form-row">
            <input
              className="input-field"
              type="password"
              value={senhaAtual}
              onChange={(e) => setSenhaAtual(e.target.value)}
              placeholder="Senha atual"
              disabled={alterandoSenha}
            />
            <input
              className="input-field"
              type="password"
              value={novaSenha}
              onChange={(e) => setNovaSenha(e.target.value)}
              placeholder="Nova senha"
              disabled={alterandoSenha}
            />
          </div>
          <div className="form-row">
            <input
              className="input-field"
              type="password"
              value={confirmarSenha}
              onChange={(e) => setConfirmarSenha(e.target.value)}
              placeholder="Confirmar nova senha"
              disabled={alterandoSenha}
            />
          </div>
          <button className="primary-button" type="submit" disabled={alterandoSenha}>
            {alterandoSenha ? "Alterando..." : "Alterar senha"}
          </button>
        </form>
      </div>
    </section>
  );
}

export default AlterarSenhaPage;
