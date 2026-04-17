import React, { useState, useEffect } from "react";
import axios from "axios";
import GerenciadorAnexos from "./GerenciadorAnexos";
import "../styles/EntrevistaFamiliarManager.css";

function EntrevistaFamiliarManager({ protocoloMEId }) {
  const [protocolo, setProtocolo] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [formEntrevista, setFormEntrevista] = useState({
    familiaNotificada: false,
    autorizouDoacao: false,
    observacoes: ""
  });

  useEffect(() => {
    carregarProtocolo();
  }, [protocoloMEId]);

  const carregarProtocolo = async () => {
    setCarregando(true);
    try {
      const response = await axios.get(`/api/protocolos-me/${protocoloMEId}`);
      setProtocolo(response.data);
      setFormEntrevista({
        familiaNotificada: response.data.familiaNotificada || false,
        autorizouDoacao: response.data.autopsiaAutorizada || false,
        observacoes: response.data.observacoes || ""
      });
    } catch (e) {
      setErro("Erro ao carregar protocolo");
    } finally {
      setCarregando(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormEntrevista({
      ...formEntrevista,
      [name]: type === "checkbox" ? checked : value
    });
  };

  const marcarParaEntrevista = async () => {
    setSalvando(true);
    setErro("");
    setSucesso("");
    try {
      await axios.post(`/api/protocolos-me/${protocoloMEId}/marcar-entrevista`);
      setSucesso("Protocolo marcado para entrevista familiar");
      await carregarProtocolo();
    } catch (e) {
      setErro(e.response?.data || "Erro ao marcar para entrevista");
    } finally {
      setSalvando(false);
    }
  };

  const salvarResultadoEntrevista = async () => {
    if (!formEntrevista.familiaNotificada) {
      setErro("Indique se a família foi notificada");
      return;
    }

    setSalvando(true);
    setErro("");
    setSucesso("");
    try {
      await axios.post(
        `/api/protocolos-me/${protocoloMEId}/resultado-entrevista`,
        null,
        {
          params: {
            autorizouDoacao: formEntrevista.autorizouDoacao,
            observacoes: formEntrevista.observacoes
          }
        }
      );
      setSucesso(
        formEntrevista.autorizouDoacao
          ? "Doação autorizada pela família"
          : "Doação recusada pela família"
      );
      await carregarProtocolo();
    } catch (e) {
      setErro(e.response?.data || "Erro ao salvar resultado");
    } finally {
      setSalvando(false);
    }
  };

  if (carregando) {
    return <p>Carregando...</p>;
  }

  if (!protocolo) {
    return <p>Protocolo não encontrado</p>;
  }

  const podeMarcarEntrevista =
    protocolo.status === "MORTE_CEREBRAL_CONFIRMADA";
  const emEntrevista = protocolo.status === "ENTREVISTA_FAMILIAR";
  const entrevistaFinalizada =
    protocolo.status === "DOACAO_AUTORIZADA" || protocolo.status === "FAMILIA_RECUSOU";

  return (
    <div className="entrevista-familiar-manager">
      <h2>👨‍👩‍👧 Entrevista Familiar</h2>

      {erro && <div className="mensagem erro">{erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      {/* Status da Entrevista */}
      <div className="entrevista-status">
        <div className="status-card">
          <h4>Status Atual</h4>
          <p className={`status-texto status-${protocolo.status.toLowerCase()}`}>
            {protocolo.status?.replace(/_/g, " ")}
          </p>
        </div>

        <div className="status-card">
          <h4>Resumo no Paciente</h4>
          <p>
            {protocolo.paciente?.statusEntrevistaFamiliar || "NÃO INICIADA"}
          </p>
        </div>

        <div className="status-card">
          <h4>Morte Cerebral Confirmada</h4>
          <p>{protocolo.dataConfirmacaoME ? "✅ Sim" : "❌ Não"}</p>
        </div>

        <div className="status-card">
          <h4>Testes Realizados</h4>
          <ul className="testes-lista">
            <li>{protocolo.testeClinico1Realizado ? "✅" : "⏳"} Teste Clínico 1</li>
            <li>{protocolo.testeClinico2Realizado ? "✅" : "⏳"} Teste Clínico 2</li>
            <li>{protocolo.testesComplementaresRealizados ? "✅" : "⏳"} Testes Complementares</li>
          </ul>
        </div>
      </div>

      {/* Seção de Entrevista */}
      {!entrevistaFinalizada && (
        <div className="entrevista-secao">
          <h3>Realizar Entrevista Familiar</h3>

          {podeMarcarEntrevista && (
            <button
              className="btn-marcar-entrevista"
              onClick={marcarParaEntrevista}
              disabled={salvando}
            >
              {salvando ? "⏳ Processando..." : "📋 Marcar para Entrevista"}
            </button>
          )}

          {emEntrevista && (
            <div className="form-entrevista">
              <div className="form-group">
                <label>
                  <input
                    type="checkbox"
                    name="familiaNotificada"
                    checked={formEntrevista.familiaNotificada}
                    onChange={handleInputChange}
                    disabled={salvando || entrevistaFinalizada}
                  />
                  Família foi notificada e participou da entrevista
                </label>
              </div>

              <div className="form-group">
                <label>
                  <input
                    type="checkbox"
                    name="autorizouDoacao"
                    checked={formEntrevista.autorizouDoacao}
                    onChange={handleInputChange}
                    disabled={!formEntrevista.familiaNotificada || salvando || entrevistaFinalizada}
                  />
                  Família autorizou a doação de órgãos
                </label>
              </div>

              <div className="form-group">
                <label htmlFor="observacoes">Observações da Entrevista</label>
                <textarea
                  id="observacoes"
                  name="observacoes"
                  value={formEntrevista.observacoes}
                  onChange={handleInputChange}
                  placeholder="Registre detalhes importantes da entrevista..."
                  className="textarea-observacoes"
                  disabled={salvando}
                />
              </div>

              <button
                className="btn-salvar-entrevista"
                onClick={salvarResultadoEntrevista}
                disabled={!formEntrevista.familiaNotificada || salvando}
              >
                {salvando ? "⏳ Salvando..." : "💾 Salvar Resultado"}
              </button>
            </div>
          )}
        </div>
      )}

      {/* Resultado da Entrevista */}
      {entrevistaFinalizada && (
        <div className="entrevista-resultado">
          <div className={`resultado-box resultado-${protocolo.status === "DOACAO_AUTORIZADA" ? "autorizado" : "recusado"}`}>
            {protocolo.status === "DOACAO_AUTORIZADA" ? (
              <>
                <h3>✅ Doação Autorizada</h3>
                <p>A família autorizou a doação de órgãos em {new Date(protocolo.dataNotificacaoFamilia).toLocaleDateString("pt-BR")}</p>
              </>
            ) : (
              <>
                <h3>❌ Doação Recusada</h3>
                <p>A família recusou a doação de órgãos em {new Date(protocolo.dataNotificacaoFamilia).toLocaleDateString("pt-BR")}</p>
              </>
            )}
          </div>
        </div>
      )}

      {/* Gerenciador de Anexos para Entrevista */}
      <GerenciadorAnexos
        tipoAnexo="ENTREVISTA"
        idExameOuProtocolo={protocoloMEId}
        titulo="📎 Documentos da Entrevista"
      />
    </div>
  );
}

export default EntrevistaFamiliarManager;
