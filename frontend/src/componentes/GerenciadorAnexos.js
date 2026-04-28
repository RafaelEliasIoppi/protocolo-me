import { useEffect, useState } from "react";
import anexoService from "../services/anexoService";
import "../styles/GerenciadorAnexos.css";

function GerenciadorAnexos({ tipoAnexo, idExameOuProtocolo, titulo = "Anexos" }) {
  const [anexos, setAnexos] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [sucesso, setSucesso] = useState("");
  const [uploadando, setUploadando] = useState(false);
  const [mostraForm, setMostraForm] = useState(false);
  const [formData, setFormData] = useState({
    arquivo: null,
    descricao: "",
    uploadPor: ""
  });

  // Carregar anexos ao montar o componente
  useEffect(() => {
    carregarAnexos();
  }, [tipoAnexo, idExameOuProtocolo]);

  const carregarAnexos = async () => {
    setCarregando(true);
    setErro("");
    try {
      let dados;
      if (tipoAnexo === "EXAME") {
        dados = await anexoService.listarAnexosExame(idExameOuProtocolo);
      } else if (tipoAnexo === "ENTREVISTA") {
        dados = await anexoService.listarAnexosEntrevista(idExameOuProtocolo);
      }
      setAnexos(dados || []);
    } catch (e) {
      setErro("Erro ao carregar anexos");
      console.error(e);
    } finally {
      setCarregando(false);
    }
  };

  const selecionarArquivo = (e) => {
    const arquivo = e.target.files[0];
    if (arquivo) {
      const validacao = anexoService.validarArquivo(arquivo);
      if (!validacao.valido) {
        setErro(validacao.erro);
        return;
      }
      setFormData({ ...formData, arquivo });
      setErro("");
    }
  };

  const instanciarExame = (e) => {
    setFormData({ ...formData, uploadPor: e.target.value });
  };

  const atualizarDescricaoAnexo = (e) => {
    setFormData({ ...formData, descricao: e.target.value });
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!formData.arquivo) {
      setErro("Selecione um arquivo");
      return;
    }

    setUploadando(true);
    setErro("");
    setSucesso("");
    try {
      if (tipoAnexo === "EXAME") {
        await anexoService.uploadAnexoExame(
          idExameOuProtocolo,
          formData.arquivo,
          formData.descricao,
          formData.uploadPor
        );
      } else if (tipoAnexo === "ENTREVISTA") {
        await anexoService.uploadAnexoEntrevista(
          idExameOuProtocolo,
          formData.arquivo,
          formData.descricao,
          formData.uploadPor
        );
      }
      setSucesso("Arquivo enviado com sucesso!");
      setFormData({ arquivo: null, descricao: "", uploadPor: "" });
      setMostraForm(false);
      await carregarAnexos();
    } catch (e) {
      setErro("Erro ao fazer upload: " + e.message);
      console.error(e);
    } finally {
      setUploadando(false);
    }
  };

  const handleDownload = async (anexo) => {
    try {
      const blob = await anexoService.downloadAnexo(anexo.id);
      anexoService.efetuarDownload(blob, anexo.nomeArquivo);
    } catch (e) {
      setErro("Erro ao fazer download");
      console.error(e);
    }
  };

  const handleDeletar = async (anexoId) => {
    if (window.confirm("Tem certeza que deseja deletar este arquivo?")) {
      try {
        await anexoService.deletarAnexo(anexoId);
        setSucesso("Arquivo deletado com sucesso");
        await carregarAnexos();
      } catch (e) {
        setErro("Erro ao deletar arquivo");
        console.error(e);
      }
    }
  };

  return (
    <div className="gerenciador-anexos">
      <div className="anexos-header">
        <h3>📎 {titulo}</h3>
        <button
          className="btn-toggle-form"
          onClick={() => setMostraForm(!mostraForm)}
        >
          {mostraForm ? "Fechar" : "+ Adicionar Arquivo"}
        </button>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}
      {sucesso && <div className="mensagem sucesso">{sucesso}</div>}

      {mostraForm && (
        <form onSubmit={handleUpload} className="form-upload">
          <div className="form-group">
            <label>Arquivo (PDF, DOC, IMG, etc.)</label>
            <input
              type="file"
              onChange={selecionarArquivo}
              accept=".pdf,.doc,.docx,.xls,.xlsx,.ppt,.pptx,.jpg,.jpeg,.png,.gif,.bmp,.txt,.csv,.zip,.rar"
              className="input-file"
            />
            {formData.arquivo && (
              <small className="arquivo-selecionado">
                ✓ {formData.arquivo.name} ({anexoService.formatarTamanho(formData.arquivo.size)})
              </small>
            )}
          </div>

          <div className="form-group">
            <label>Descrição (opcional)</label>
            <input
              type="text"
              value={formData.descricao}
              onChange={atualizarDescricaoAnexo}
              placeholder="Ex: Laudo do exame, Autorização da família, etc."
              className="input-text"
            />
          </div>

          <div className="form-group">
            <label>Responsável pelo upload (opcional)</label>
            <input
              type="text"
              value={formData.uploadPor}
              onChange={instanciarExame}
              placeholder="Ex: Dr. Silva, Enf. Maria"
              className="input-text"
            />
          </div>

          <button type="submit" disabled={uploadando} className="btn-submit">
            {uploadando ? "⏳ Enviando..." : "📤 Enviar Arquivo"}
          </button>
        </form>
      )}

      {carregando ? (
        <p className="carregando">⏳ Carregando anexos...</p>
      ) : anexos.length > 0 ? (
        <div className="lista-anexos">
          <div className="anexos-info">
            <small>Total de anexos: <strong>{anexos.length}</strong></small>
          </div>
          <div className="anexos-lista">
            {anexos.map((anexo) => (
              <div key={anexo.id} className="anexo-item">
                <div className="anexo-info">
                  <div className="anexo-nome">
                    <span className="anexo-icone">📄</span>
                    <strong>{anexo.nomeArquivo}</strong>
                  </div>
                  <small className="anexo-detalhes">
                    {anexoService.formatarTamanho(anexo.tamanhoBytes)} • Enviado por {anexo.uploadPor || "N/A"} • {new Date(anexo.dataUpload).toLocaleDateString("pt-BR")}
                  </small>
                  {anexo.descricao && (
                    <small className="anexo-descricao">
                      {anexo.descricao}
                    </small>
                  )}
                </div>
                <div className="anexo-acoes">
                  <button
                    className="btn-download"
                    onClick={() => handleDownload(anexo)}
                    title="Download"
                  >
                    ⬇️
                  </button>
                  <button
                    className="btn-delete"
                    onClick={() => handleDeletar(anexo.id)}
                    title="Deletar"
                  >
                    🗑️
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ) : (
        <p className="sem-anexos">Nenhum anexo neste momento</p>
      )}
    </div>
  );
}

export default GerenciadorAnexos;
