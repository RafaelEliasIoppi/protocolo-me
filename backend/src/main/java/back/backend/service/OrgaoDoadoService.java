package back.backend.service;

import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import back.backend.repository.OrgaoDoadoRepository;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class OrgaoDoadoService {

    private static final Set<String> ORGAOS_SNT = Set.of(
        "Coração",
        "Pulmão",
        "Fígado",
        "Rins",
        "Pâncreas",
        "Intestino",
        "Córneas",
        "Pele",
        "Ossos",
        "Tendões",
        "Válvulas Cardíacas"
    );

    private static final Map<String, String> ALIAS_ORGAOS = Map.ofEntries(
        Map.entry("coracao", "Coração"),
        Map.entry("pulmao", "Pulmão"),
        Map.entry("figado", "Fígado"),
        Map.entry("rim", "Rins"),
        Map.entry("rins", "Rins"),
        Map.entry("pancreas", "Pâncreas"),
        Map.entry("intestino", "Intestino"),
        Map.entry("cornea", "Córneas"),
        Map.entry("corneas", "Córneas"),
        Map.entry("pele", "Pele"),
        Map.entry("osso", "Ossos"),
        Map.entry("ossos", "Ossos"),
        Map.entry("tendao", "Tendões"),
        Map.entry("tendoes", "Tendões"),
        Map.entry("valvula cardiaca", "Válvulas Cardíacas"),
        Map.entry("valvulas cardiacas", "Válvulas Cardíacas")
    );

    @Autowired
    private OrgaoDoadoRepository orgaoDoadoRepository;

    @Autowired
    private ProtocoloMERepository protocoloMERepository;

    /**
     * Cria um novo registro de órgão doado
     */
    public OrgaoDoado criar(OrgaoDoado orgaoDoado) {
        if (orgaoDoado.getProtocoloME() == null || orgaoDoado.getProtocoloME().getId() == null) {
            throw new RuntimeException("Protocolo de ME é obrigatório");
        }

        ProtocoloME protocolo = protocoloMERepository.findById(orgaoDoado.getProtocoloME().getId())
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        orgaoDoado.setProtocoloME(protocolo);
        
        if (orgaoDoado.getNomeOrgao() == null || orgaoDoado.getNomeOrgao().trim().isEmpty()) {
            throw new RuntimeException("Nome do órgão é obrigatório");
        }

        orgaoDoado.setNomeOrgao(validarENormalizarNomeOrgao(orgaoDoado.getNomeOrgao()));

        if (orgaoDoado.getStatus() == null) {
            orgaoDoado.setStatus(OrgaoDoado.StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO);
        }

        return orgaoDoadoRepository.save(orgaoDoado);
    }

    /**
     * Atualiza um registro de órgão doado
     */
    public OrgaoDoado atualizar(Long id, OrgaoDoado orgaoDoadoAtualizado) {
        OrgaoDoado orgaoDoado = orgaoDoadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Órgão doado não encontrado"));

        if (orgaoDoadoAtualizado.getNomeOrgao() != null && !orgaoDoadoAtualizado.getNomeOrgao().isEmpty()) {
            orgaoDoado.setNomeOrgao(validarENormalizarNomeOrgao(orgaoDoadoAtualizado.getNomeOrgao()));
        }

        if (orgaoDoadoAtualizado.getStatus() != null) {
            orgaoDoado.setStatus(orgaoDoadoAtualizado.getStatus());

            // Definir datas automáticas baseado no status
            if (orgaoDoadoAtualizado.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO) {
                if (orgaoDoado.getDataImplantacao() == null) {
                    orgaoDoado.setDataImplantacao(LocalDateTime.now());
                }
            } else if (orgaoDoadoAtualizado.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO) {
                if (orgaoDoado.getDataDescarte() == null) {
                    orgaoDoado.setDataDescarte(LocalDateTime.now());
                }
            }
        }

        if (orgaoDoadoAtualizado.getMotivo() != null) {
            orgaoDoado.setMotivo(orgaoDoadoAtualizado.getMotivo());
        }

        if (orgaoDoadoAtualizado.getHospitalReceptor() != null) {
            orgaoDoado.setHospitalReceptor(orgaoDoadoAtualizado.getHospitalReceptor());
        }

        if (orgaoDoadoAtualizado.getPacienteReceptor() != null) {
            orgaoDoado.setPacienteReceptor(orgaoDoadoAtualizado.getPacienteReceptor());
        }

        if (orgaoDoadoAtualizado.getCpfReceptor() != null) {
            orgaoDoado.setCpfReceptor(orgaoDoadoAtualizado.getCpfReceptor());
        }

        if (orgaoDoadoAtualizado.getDataArmazenamento() != null) {
            orgaoDoado.setDataArmazenamento(orgaoDoadoAtualizado.getDataArmazenamento());
        }

        if (orgaoDoadoAtualizado.getMotivoDescarte() != null) {
            orgaoDoado.setMotivoDescarte(orgaoDoadoAtualizado.getMotivoDescarte());
        }

        if (orgaoDoadoAtualizado.getObservacoes() != null) {
            orgaoDoado.setObservacoes(orgaoDoadoAtualizado.getObservacoes());
        }

        return orgaoDoadoRepository.save(orgaoDoado);
    }

    private String validarENormalizarNomeOrgao(String nomeOrgao) {
        String chave = normalizarChave(nomeOrgao);
        String nomeCanonico = ALIAS_ORGAOS.get(chave);

        if (nomeCanonico == null || !ORGAOS_SNT.contains(nomeCanonico)) {
            throw new IllegalArgumentException("Órgão/tecido inválido. Utilize apenas opções padronizadas do SNT.");
        }

        return nomeCanonico;
    }

    private String normalizarChave(String valor) {
        String semAcento = Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ");
    }

    /**
     * Registra a implantação de um órgão doado
     */
    public OrgaoDoado registrarImplantacao(Long id, String hospitalReceptor, String pacienteReceptor) {
        OrgaoDoado orgaoDoado = orgaoDoadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Órgão doado não encontrado"));

        orgaoDoado.setStatus(OrgaoDoado.StatusOrgaoDoado.IMPLANTADO);
        orgaoDoado.setHospitalReceptor(hospitalReceptor);
        orgaoDoado.setPacienteReceptor(pacienteReceptor);
        orgaoDoado.setDataImplantacao(LocalDateTime.now());

        return orgaoDoadoRepository.save(orgaoDoado);
    }

    /**
     * Registra o descarte de um órgão doado
     */
    public OrgaoDoado registrarDescarte(Long id, String motivo) {
        OrgaoDoado orgaoDoado = orgaoDoadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Órgão doado não encontrado"));

        orgaoDoado.setStatus(OrgaoDoado.StatusOrgaoDoado.DESCARTADO);
        orgaoDoado.setMotivoDescarte(motivo);
        orgaoDoado.setDataDescarte(LocalDateTime.now());

        return orgaoDoadoRepository.save(orgaoDoado);
    }

    /**
     * Busca um órgão doado por ID
     */
    public Optional<OrgaoDoado> buscarPorId(Long id) {
        return orgaoDoadoRepository.findById(id);
    }

    /**
     * Lista todos os órgãos doados de um protocolo
     */
    public List<OrgaoDoado> listarPorProtocolo(Long protocoloId) {
        return orgaoDoadoRepository.findByProtocoloMEId(protocoloId);
    }

    /**
     * Deleta um órgão doado
     */
    public void deletar(Long id) {
        if (!orgaoDoadoRepository.existsById(id)) {
            throw new RuntimeException("Órgão doado não encontrado");
        }
        orgaoDoadoRepository.deleteById(id);
    }

    /**
     * Obtém estatísticas de órgãos por status
     */
    public OrgaoStatisticas obterEstatisticas(Long protocoloId) {
        List<OrgaoDoado> orgaos = listarPorProtocolo(protocoloId);
        
        long implantados = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO)
                .count();
        
        long descartados = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO)
                .count();
        
        long aguardando = orgaos.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO)
                .count();

        return new OrgaoStatisticas(orgaos.size(), implantados, descartados, aguardando);
    }

    /**
     * Classe auxiliar para estatísticas
     */
    public static class OrgaoStatisticas {
        private long total;
        private long implantados;
        private long descartados;
        private long aguardando;

        public OrgaoStatisticas(long total, long implantados, long descartados, long aguardando) {
            this.total = total;
            this.implantados = implantados;
            this.descartados = descartados;
            this.aguardando = aguardando;
        }

        public long getTotal() {
            return total;
        }

        public long getImplantados() {
            return implantados;
        }

        public long getDescartados() {
            return descartados;
        }

        public long getAguardando() {
            return aguardando;
        }
    }
}
