import { useEffect, useState } from 'react';
import centralDashboardService from '../services/centralDashboardService';
import '../styles/RelatoriosBibliotecaPage.css';

function RelatoriosBibliotecaPage() {
  const [relatorios, setRelatorios] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    let mounted = true;
    centralDashboardService.listarRelatoriosGerados()
      .then((data) => { if (mounted) setRelatorios(data || []); })
      .catch((err) => { if (mounted) setError(err?.message || String(err)); })
      .finally(() => { if (mounted) setLoading(false); });

    return () => { mounted = false; };
  }, []);

  const escapeHtml = (unsafe) => {
    if (unsafe == null) return '';
    return String(unsafe)
      .replace(/&/g, '&amp;')
      .replace(/</g, '&lt;')
      .replace(/>/g, '&gt;');
  };

  const visualizarImprimir = async (item) => {
    try {
      const content = await centralDashboardService.obterRelatorioFinalPaciente(item.pacienteId);
      const win = window.open('', '_blank', 'noopener,noreferrer');
      win.document.write(`<!doctype html><html><head><meta charset="utf-8"><title>Relatório - ${item.nomePaciente}</title>
        <style>body{font-family:Arial,Helvetica,sans-serif;padding:20px}</style></head><body>
        <h1>Relatório Final</h1><h2>${item.nomePaciente} — ${item.numeroProtocolo || ''}</h2>
        <pre>${escapeHtml(content || '')}</pre>
        </body></html>`);
      win.document.close();
      setTimeout(() => { try { win.print(); } catch(e){ console.warn(e); } }, 300);
    } catch (e) {
      alert('Falha ao obter o relatório: ' + (e?.message || e));
    }
  };

  const baixar = async (item) => {
    try {
      const content = await centralDashboardService.obterRelatorioFinalPaciente(item.pacienteId);
      const blob = new Blob([content || ''], { type: 'text/plain;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `relatorio_${item.numeroProtocolo || item.pacienteId}.txt`;
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(url);
    } catch (e) {
      alert('Falha ao baixar o relatório: ' + (e?.message || e));
    }
  };

  return (
    <div className="panel relatorios-biblioteca">
      <h2>Biblioteca de Relatórios</h2>
      {loading && <p>Carregando...</p>}
      {error && <p className="error">Erro: {error}</p>}

      {!loading && !error && (
        <>
          <p className="note">Lista de relatórios gerados para protocolos. Use visualizar para imprimir.</p>
          <table className="relatorios-table">
            <thead>
              <tr>
                <th>Paciente</th>
                <th>CPF</th>
                <th>Protocolo</th>
                <th>Atualizado em</th>
                <th>Editável</th>
                <th>Ações</th>
              </tr>
            </thead>
            <tbody>
              {relatorios.length === 0 && (
                <tr><td colSpan="6">Nenhum relatório encontrado.</td></tr>
              )}
              {relatorios.map((r) => (
                <tr key={`${r.pacienteId}-${r.protocoloId}`}>
                  <td>{r.nomePaciente}</td>
                  <td>{r.cpf}</td>
                  <td>{r.numeroProtocolo}</td>
                  <td>{r.dataAtualizacao ? new Date(r.dataAtualizacao).toLocaleString() : '—'}</td>
                  <td>{r.relatorioFinalEditavel ? 'Sim' : 'Não'}</td>
                  <td className="acoes">
                    <button onClick={() => visualizarImprimir(r)}>Visualizar / Imprimir</button>
                    <button onClick={() => baixar(r)}>Baixar</button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </>
      )}
    </div>
  );
}

export default RelatoriosBibliotecaPage;
