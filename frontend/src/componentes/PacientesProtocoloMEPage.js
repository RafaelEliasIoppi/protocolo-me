import { useEffect, useState } from "react";
import hospitalService from "../services/hospitalService";
import pacienteService from "../services/pacienteService";
import protocoloService from "../services/protocoloService";
import "../styles/PacientesProtocoloMEPage.css";
import { formatarCpf } from "../utils/cpf";
import GerenciadorOrgaosDoados from "./GerenciadorOrgaosDoados";

function PacientesProtocoloMEPage() {
  const [pacientes, setPacientes] = useState([]);
  const [carregando, setCarregando] = useState(false);
  const [erro, setErro] = useState("");
  const [filtroHospital, setFiltroHospital] = useState("");
  const [hospitais, setHospitais] = useState([]);
  const [protocolosComOrgaosAbertos, setProtocolosComOrgaosAbertos] = useState(new Set());

  const normalizarLista = (dados) => {
    if (Array.isArray(dados)) return dados;
    if (Array.isArray(dados?.content)) return dados.content;
    if (Array.isArray(dados?.data)) return dados.data;
    return [];
  };

  const carregarPacientesProtocoloME = async (hospitalId = "") => {
    setCarregando(true);
    setErro("");
    try {
      const dados = hospitalId
        ? await pacienteService.listarEmProtocoloMEPorHospital(hospitalId)
        : await pacienteService.listarEmProtocoloME();

      setPacientes(normalizarLista(dados));
    } catch (err) {
      setErro("Erro ao carregar pacientes em protocolo de ME");
      console.error(err);
    } finally {
      setCarregando(false);
    }
  };

  const carregarHospitais = async () => {
    try {
      const dados = await hospitalService.listar();
      setHospitais(Array.isArray(dados) ? dados : []);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    carregarHospitais();
    carregarPacientesProtocoloME();
  }, []);

  const filtrarPorHospital = (e) => {
    const hospitalId = e.target.value;
    setFiltroHospital(hospitalId);
    carregarPacientesProtocoloME(hospitalId);
  };

  const toggleOrgaosDoados = (protocoloId) => {
    setProtocolosComOrgaosAbertos((prev) => {
      const novo = new Set(prev);
      novo.has(protocoloId) ? novo.delete(protocoloId) : novo.add(protocoloId);
      return novo;
    });
  };

  const editarNumeroProtocolo = async (protocolo) => {
    const numeroNovo = window.prompt(
      "Informe o novo numero do protocolo:",
      protocolo?.numeroProtocolo || ""
    );

    if (!numeroNovo?.trim()) return;

    try {
      await protocoloService.atualizar(protocolo.id, {
        ...protocolo,
        numeroProtocolo: numeroNovo.trim()
      });

      await carregarPacientesProtocoloME(filtroHospital);
    } catch (err) {
      setErro("Erro ao atualizar protocolo");
    }
  };

  return (
    <section className="pacientes-protocolo-me-page">
      <div className="brand-bar">
        <div>
          <h1>Pacientes em Protocolo de Morte Encefálica</h1>
          <p>Visualize apenas pacientes em protocolo ME.</p>
        </div>

        <button
          className="secondary-button"
          onClick={() => carregarPacientesProtocoloME(filtroHospital)}
        >
          🔄 Atualizar
        </button>
      </div>

      {erro && <div className="mensagem erro">{erro}</div>}

      <div className="panel">
        <div className="filtro-section">
          <label>Filtrar por Hospital:</label>
          <select value={filtroHospital} onChange={filtrarPorHospital}>
            <option value="">Todos</option>
            {hospitais.map((h) => (
              <option key={h.id} value={h.id}>
                {h.nomeHospital || h.nome}
              </option>
            ))}
          </select>
        </div>

        {carregando ? (
          <div>Carregando...</div>
        ) : pacientes.length > 0 ? (
          <div className="pacientes-grid">
            {pacientes.map((paciente) => (
              <div key={paciente.id} className="paciente-card">
                <h3>{paciente.nome}</h3>

                <p>CPF: {formatarCpf(paciente.cpf)}</p>
                <p>Hospital: {paciente.hospital?.nomeHospital}</p>

                {paciente.protocolosME?.map((protocolo) => (
                  <div key={protocolo.id}>
                    <p>Protocolo: {protocolo.numeroProtocolo}</p>

                    <button onClick={() => editarNumeroProtocolo(protocolo)}>
                      Editar protocolo
                    </button>

                    <button onClick={() => toggleOrgaosDoados(protocolo.id)}>
                      Órgãos
                    </button>

                    {protocolosComOrgaosAbertos.has(protocolo.id) && (
                      <GerenciadorOrgaosDoados protocoloId={protocolo.id} />
                    )}
                  </div>
                ))}
              </div>
            ))}
          </div>
        ) : (
          <p>Nenhum paciente em protocolo ME</p>
        )}
      </div>
    </section>
  );
}

export default PacientesProtocoloMEPage;
