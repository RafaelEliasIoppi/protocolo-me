import { useEffect, useState } from "react";
import clienteHttpService from "../services/clienteHttpService";
import "../styles/EntrevistaFamiliarManager.css";
import { getApiErrorMessage } from "../utils/apiError";
import GerenciadorAnexos from "./GerenciadorAnexos";

function EntrevistaFamiliarManager({ protocoloMEId, onAtualizacao }) {
  const [protocolo, setProtocolo] = useState(null);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [salvando, setSalvando] = useState(false);
  const [formEntrevista, setFormEntrevista] = useState({
    familiaNotificada: false,
    resultadoEntrevista: "",
    autorizouDoacao: false,
    observacoes: ""
  });

  useEffect(() => {
    carregarProtocolo();
  }, [protocoloMEId]);

  const carregarProtocolo = async () => {
    setCarregando(true);
    try {
      const response = await clienteHttpService.get(`/api/protocolos-me/${protocoloMEId}`);
      setProtocolo(response.data);
      const resultadoFinal = response.data.status === "FINALIZADO"
        ? (response.data.autopsiaAutorizada ? "POSITIVO" : "NEGATIVO")
        : response.data.autopsiaAutorizada
          ? "POSITIVO"
          : response.data.status === "FAMILIA_RECUSOU"
            ? "NEGATIVO"
            : "";
      setFormEntrevista({
        familiaNotificada: response.data.familiaNotificada || false,
        resultadoEntrevista: resultadoFinal,
        autorizouDoacao: response.data.autopsiaAutorizada || false,
        observacoes: response.data.observacoes || ""
      });
    } catch (e) {
      setErro(getApiErrorMessage(e, "Erro ao carregar protocolo"));
    } finally {
      setCarregando(false);
    }
  };

  const atualizarCampoFormulario = (e) => {
    const { name, value, type, checked } = e.target;
    setFormEntrevista({
      ...formEntrevista,
      [name]: type === "checkbox" ? checked : value
    });
  };

  const definirResultadoEntrevista = (resultado) => {
    setFormEntrevista((atual) => ({
      ...atual,
      resultadoEntrevista: resultado,
      autorizouDoacao: resultado === "POSITIVO"
    }));
  };

  const obterResultadoDaEntrevista = (protocoloAtual) => {
    if (!protocoloAtual) {
      return "Não iniciado";
    }

    if (protocoloAtual.status === "FINALIZADO" || protocoloAtual.status === "DOACAO_AUTORIZADA") {
      return "Positivo";
    }

    if (protocoloAtual.status === "FAMILIA_RECUSOU") {
      return "Negativo";
    }

    return formEntrevista.resultadoEntrevista === "POSITIVO"
      ? "Positivo"
      : formEntrevista.resultadoEntrevista === "NEGATIVO"
        ? "Negativo"
        : "Não definido";
  };

  const marcarParaEntrevista = async () => {
    setSalvando(true);
    setErro("");
    setSucesso("");
    try {
      await clienteHttpService.post(`/api/protocolos-me/${protocoloMEId}/marcar-entrevista`);
      setSucesso("Protocolo marcado para entrevista familiar");
      await carregarProtocolo();
      if (typeof onAtualizacao === "function") {
        await onAtualizacao();
      }
    } catch (e) {
      setErro(getApiErrorMessage(e, "Erro ao marcar para entrevista"));
    } finally {
      setSalvando(false);
    }
  };

  const salvarResultadoEntrevista = async () => {
    if (!formEntrevista.familiaNotificada) {
      setErro("Indique se a família foi notificada");
      return;
    }

    if (!formEntrevista.resultadoEntrevista) {
      setErro("Selecione o resultado da entrevista: Positivo ou Negativo");
      return;
    }

    setSalvando(true);
    setErro("");
    setSucesso("");
    try {
      await clienteHttpService.post(
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
        formEntrevista.resultadoEntrevista === "POSITIVO"
          ? "Resultado positivo registrado na entrevista"
          : "Resultado negativo registrado na entrevista"
      );
      await carregarProtocolo();
      if (typeof onAtualizacao === "function") {
        await onAtualizacao();
      }
    } catch (e) {
      setErro(getApiErrorMessage(e, "Erro ao salvar resultado"));
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
    protocolo.status === "FINALIZADO" || protocolo.status === "DOACAO_AUTORIZADA" || protocolo.status === "FAMILIA_RECUSOU";

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
          <h4>Resultado da Entrevista</h4>
          <p className={`resultado-resumo resultado-${obterResultadoDaEntrevista(protocolo).toLowerCase().replace(/\s+/g, "-")}`}>
            {obterResultadoDaEntrevista(protocolo)}
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
                    onChange={atualizarCampoFormulario}
                    disabled={salvando || entrevistaFinalizada}
                  />
                  Família foi notificada e participou da entrevista
                </label>
              </div>

              <div className="form-group">
                <label className="grupo-radio-label">Resultado da entrevista</label>
                <div className="radio-group">
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="resultadoEntrevista"
                      value="POSITIVO"
                      checked={formEntrevista.resultadoEntrevista === "POSITIVO"}
                      onChange={() => definirResultadoEntrevista("POSITIVO")}
                      disabled={!formEntrevista.familiaNotificada || salvando || entrevistaFinalizada}
                    />
                    Positivo
                  </label>
                  <label className="radio-option">
                    <input
                      type="radio"
                      name="resultadoEntrevista"
                      value="NEGATIVO"
                      checked={formEntrevista.resultadoEntrevista === "NEGATIVO"}
                      onChange={() => definirResultadoEntrevista("NEGATIVO")}
                      disabled={!formEntrevista.familiaNotificada || salvando || entrevistaFinalizada}
                    />
                    Negativo
                  </label>
                </div>
                <p className="resultado-ajuda">
                  Resultado selecionado: <strong>{obterResultadoDaEntrevista(protocolo)}</strong>
                </p>
              </div>

              <div className="form-group">
                <label htmlFor="observacoes">Observações da Entrevista</label>
                <textarea
                  id="observacoes"
                  name="observacoes"
                  value={formEntrevista.observacoes}
                  onChange={atualizarCampoFormulario}
                  placeholder="Registre detalhes importantes da entrevista..."
                  className="textarea-observacoes"
                  disabled={salvando}
                />
              </div>

              <button
                className="btn-salvar-entrevista"
                onClick={salvarResultadoEntrevista}
                disabled={!formEntrevista.familiaNotificada || !formEntrevista.resultadoEntrevista || salvando}
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
          <div className={`resultado-box resultado-${protocolo.autopsiaAutorizada ? "autorizado" : "recusado"}`}>
            {protocolo.autopsiaAutorizada ? (
              <>
                <h3>✅ Resultado Positivo</h3>
                <p>Status técnico: {protocolo.status.replace(/_/g, " ")}</p>
                <p>A entrevista teve resultado positivo em {new Date(protocolo.dataNotificacaoFamilia).toLocaleDateString("pt-BR")}</p>
              </>
            ) : (
              <>
                <h3>❌ Resultado Negativo</h3>
                <p>Status técnico: {protocolo.status.replace(/_/g, " ")}</p>
                <p>A entrevista teve resultado negativo em {new Date(protocolo.dataNotificacaoFamilia).toLocaleDateString("pt-BR")}</p>
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
