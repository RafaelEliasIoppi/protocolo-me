package back.backend.service;

import back.backend.dto.OrgaoDoadoDTO;
import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.OrgaoDoadoMapper;
import back.backend.repository.OrgaoDoadoRepository;
import back.backend.repository.ProtocoloMERepository;

import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrgaoDoadoService {

    private final OrgaoDoadoRepository orgaoDoadoRepository;
    private final ProtocoloMERepository protocoloMERepository;
    private final OrgaoDoadoMapper orgaoDoadoMapper;

    public OrgaoDoadoService(
            OrgaoDoadoRepository orgaoDoadoRepository,
            ProtocoloMERepository protocoloMERepository,
            OrgaoDoadoMapper orgaoDoadoMapper) {

        this.orgaoDoadoRepository = orgaoDoadoRepository;
        this.protocoloMERepository = protocoloMERepository;
        this.orgaoDoadoMapper = orgaoDoadoMapper;
    }

    // ================= CONSTANTES =================

    private static final Set<String> ORGAOS_SNT = Set.of(
            "Coração", "Pulmão", "Fígado", "Rins", "Pâncreas",
            "Intestino", "Córneas", "Pele", "Ossos", "Tendões",
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

    // ================= CREATE =================

    public OrgaoDoadoDTO criar(OrgaoDoado orgaoDoado) {

        validarProtocolo(orgaoDoado);
        validarNome(orgaoDoado);
        aplicarStatusPadrao(orgaoDoado);

        orgaoDoado.setCpfReceptor(normalizarCpf(orgaoDoado.getCpfReceptor()));

        return toDTO(orgaoDoadoRepository.save(orgaoDoado));
    }

    // ================= UPDATE =================

    public OrgaoDoadoDTO atualizar(Long id, OrgaoDoado atualizado) {

        OrgaoDoado entity = buscarEntity(id);

        if (atualizado.getNomeOrgao() != null) {
            entity.setNomeOrgao(
                    validarENormalizarNomeOrgao(atualizado.getNomeOrgao()));
        }

        if (atualizado.getStatus() != null) {
            entity.setStatus(atualizado.getStatus());
            aplicarDatasStatus(entity);
        }

        copiarCamposSimples(entity, atualizado);

        return toDTO(orgaoDoadoRepository.save(entity));
    }

    // ================= AÇÕES =================

    public OrgaoDoadoDTO registrarImplantacao(Long id, String hospital, String paciente) {

        OrgaoDoado entity = buscarEntity(id);

        entity.setStatus(OrgaoDoado.StatusOrgaoDoado.IMPLANTADO);
        entity.setHospitalReceptor(hospital);
        entity.setPacienteReceptor(paciente);
        entity.setDataImplantacao(LocalDateTime.now());

        return toDTO(orgaoDoadoRepository.save(entity));
    }

    public OrgaoDoadoDTO registrarDescarte(Long id, String motivo) {

        OrgaoDoado entity = buscarEntity(id);

        entity.setStatus(OrgaoDoado.StatusOrgaoDoado.DESCARTADO);
        entity.setMotivoDescarte(motivo);
        entity.setDataDescarte(LocalDateTime.now());

        return toDTO(orgaoDoadoRepository.save(entity));
    }

    // ================= QUERY =================

    public OrgaoDoadoDTO buscarPorId(Long id) {
        return toDTO(buscarEntity(id));
    }

    public List<OrgaoDoadoDTO> listarPorProtocolo(Long protocoloId) {
        return orgaoDoadoRepository.findByDoacao_ProtocoloME_Id(protocoloId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public void deletar(Long id) {
        if (!orgaoDoadoRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Órgão doado não encontrado");
        }
        orgaoDoadoRepository.deleteById(id);
    }

    // ================= STATS =================

    public OrgaoStatisticas obterEstatisticas(Long protocoloId) {

        List<OrgaoDoadoDTO> lista = listarPorProtocolo(protocoloId);

        long total = lista.size();

        long implantados = lista.stream()
                .filter(o -> "IMPLANTADO".equals(o.getStatus()))
                .count();

        long descartados = lista.stream()
                .filter(o -> "DESCARTADO".equals(o.getStatus()))
                .count();

        long aguardando = lista.stream()
                .filter(o -> "AGUARDANDO_IMPLANTACAO".equals(o.getStatus()))
                .count();

        return new OrgaoStatisticas(total, implantados, descartados, aguardando);
    }

    // ================= HELPERS =================

    private OrgaoDoado buscarEntity(Long id) {
        return orgaoDoadoRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Órgão doado não encontrado"));
    }

    private OrgaoDoadoDTO toDTO(OrgaoDoado entity) {
        return orgaoDoadoMapper.toDTO(entity);
    }

    private void validarProtocolo(OrgaoDoado orgao) {

        if (orgao.getProtocoloME() == null ||
            orgao.getProtocoloME().getId() == null) {

            throw new IllegalArgumentException("Protocolo de ME é obrigatório");
        }

        ProtocoloME protocolo = protocoloMERepository.findById(
                orgao.getProtocoloME().getId()
        ).orElseThrow(() ->
                new RecursoNaoEncontradoException("Protocolo não encontrado"));

        orgao.setProtocoloME(protocolo);
    }

    private void validarNome(OrgaoDoado orgao) {

        if (orgao.getNomeOrgao() == null ||
            orgao.getNomeOrgao().isBlank()) {

            throw new IllegalArgumentException("Nome do órgão é obrigatório");
        }

        orgao.setNomeOrgao(
                validarENormalizarNomeOrgao(orgao.getNomeOrgao()));
    }

    private void aplicarStatusPadrao(OrgaoDoado orgao) {
        if (orgao.getStatus() == null) {
            orgao.setStatus(
                    OrgaoDoado.StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO);
        }
    }

    private void aplicarDatasStatus(OrgaoDoado orgao) {

        if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO
                && orgao.getDataImplantacao() == null) {

            orgao.setDataImplantacao(LocalDateTime.now());
        }

        if (orgao.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO
                && orgao.getDataDescarte() == null) {

            orgao.setDataDescarte(LocalDateTime.now());
        }
    }

    private void copiarCamposSimples(OrgaoDoado dest, OrgaoDoado orig) {

        if (orig.getMotivo() != null) dest.setMotivo(orig.getMotivo());
        if (orig.getHospitalReceptor() != null) dest.setHospitalReceptor(orig.getHospitalReceptor());
        if (orig.getPacienteReceptor() != null) dest.setPacienteReceptor(orig.getPacienteReceptor());
        if (orig.getCpfReceptor() != null) dest.setCpfReceptor(normalizarCpf(orig.getCpfReceptor()));
        if (orig.getDataArmazenamento() != null) dest.setDataArmazenamento(orig.getDataArmazenamento());
        if (orig.getMotivoDescarte() != null) dest.setMotivoDescarte(orig.getMotivoDescarte());
        if (orig.getObservacoes() != null) dest.setObservacoes(orig.getObservacoes());
    }

    private String validarENormalizarNomeOrgao(String nome) {

        String chave = normalizarChave(nome);
        String canonico = ALIAS_ORGAOS.get(chave);

        if (canonico == null || !ORGAOS_SNT.contains(canonico)) {
            throw new IllegalArgumentException("Órgão inválido");
        }

        return canonico;
    }

    private String normalizarChave(String valor) {
        return Normalizer.normalize(valor.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");
    }

    private String normalizarCpf(String cpf) {

        if (cpf == null) return null;

        String n = cpf.replaceAll("\\D", "");

        if (n.length() != 11) {
            throw new IllegalArgumentException("CPF inválido");
        }

        return n.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4");
    }

    // ================= DTO INTERNO =================

    public static class OrgaoStatisticas {

        public final long total;
        public final long implantados;
        public final long descartados;
        public final long aguardando;

        public OrgaoStatisticas(long total,
                                long implantados,
                                long descartados,
                                long aguardando) {

            this.total = total;
            this.implantados = implantados;
            this.descartados = descartados;
            this.aguardando = aguardando;
        }
    }
}
