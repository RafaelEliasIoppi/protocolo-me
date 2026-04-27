package back.backend.service;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.dto.ProtocoloSemEstatisticaDTO;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.EstatisticaProtocoloME;
import back.backend.model.ProtocoloME;
import back.backend.repository.EstatisticaProtocoloMERepository;
import back.backend.repository.ProtocoloMERepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstatisticaProtocoloMEService {

    private final EstatisticaProtocoloMERepository estatisticaRepository;
    private final ProtocoloMERepository protocoloRepository;
    private final ObjectMapper objectMapper;

    public EstatisticaProtocoloMEService(
            EstatisticaProtocoloMERepository estatisticaRepository,
            ProtocoloMERepository protocoloRepository,
            ObjectMapper objectMapper) {
        this.estatisticaRepository = estatisticaRepository;
        this.protocoloRepository = protocoloRepository;
        this.objectMapper = objectMapper;
    }

    private static final TypeReference<Map<String, String>> TYPE_REF_MAP =
            new TypeReference<>() {};

    // 🔥 CACHE DE FIELDS (melhora performance)
    private static final Map<String, Field> FIELD_CACHE = new HashMap<>();

    static {
        for (Field field : EstatisticaProtocoloME.class.getDeclaredFields()) {
            field.setAccessible(true);
            FIELD_CACHE.put(field.getName(), field);
        }
    }

    // 🔥 CAMPOS PADRÃO (ajusta conforme SNT)
    private static final List<String> CAMPOS_PROTOCOLO = List.of(
            "nomeDoador", "rgctDoador", "hospitalNotif",
            "municipio", "sexoDoad", "aboDoad"
    );

    private static final List<String> CAMPOS_SIM_NAO = List.of(
            "rimD", "rimE", "figado", "coracao", "pulmD", "pulmE"
    );

    // =========================
    // MIGRAÇÃO JSON → COLUNAS
    // =========================

    @PostConstruct
    public void migrarDadosLegadosParaColunas() {
        List<EstatisticaProtocoloME> estatisticas = estatisticaRepository.findAll();

        for (EstatisticaProtocoloME estatistica : estatisticas) {

            if (estatistica.getDadosCamposJson() == null ||
                estatistica.getDadosCamposJson().isBlank()) {
                continue;
            }

            try {
                Map<String, String> campos =
                        objectMapper.readValue(estatistica.getDadosCamposJson(), TYPE_REF_MAP);

                aplicarCamposNaEntidade(estatistica, campos);
                estatisticaRepository.save(estatistica);

            } catch (Exception e) {
                throw new IllegalStateException("Erro ao migrar dados", e);
            }
        }
    }

    // =========================
    // 🔥 GERAÇÃO AUTOMÁTICA
    // =========================

    public EstatisticaProtocoloME gerarPorProtocolo(Long protocoloId) {

        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado"));

        EstatisticaProtocoloME estatistica = new EstatisticaProtocoloME();
        estatistica.setProtocoloME(protocolo);

        // 📅 competência
        if (protocolo.getDataNotificacao() != null) {
            estatistica.setAnoCompetencia(protocolo.getDataNotificacao().getYear());
            estatistica.setMesCompetencia(protocolo.getDataNotificacao().getMonthValue());
        }

        // 👤 dados do doador
        if (protocolo.getPaciente() != null) {
            estatistica.setNomeDoador(protocolo.getPaciente().getNome());
        }

        estatistica.setHospitalNotif(protocolo.getHospitalOrigem());
        estatistica.setDataAtualizacao(LocalDateTime.now());

        return estatisticaRepository.save(estatistica);
    }

    // =========================
    // LISTAGENS
    // =========================

    public List<EstatisticaProtocoloMEDTO> listarPorPeriodicidade(String periodicidade, Integer ano, Integer mes) {

        if (mes != null && ano == null) {
            throw new IllegalArgumentException("Mês precisa ser informado junto com o ano");
        }

        return estatisticaRepository.findAll().stream()
            .filter(e -> periodicidade == null ||
                (e.getPeriodicidade() != null && e.getPeriodicidade().name().equalsIgnoreCase(periodicidade)))
                .filter(e -> ano == null || Objects.equals(e.getAnoCompetencia(), ano))
                .filter(e -> mes == null || Objects.equals(e.getMesCompetencia(), mes))
                .map(e -> toDTO(e, e.getProtocoloME()))
                .collect(Collectors.toList());
    }

    public List<ProtocoloSemEstatisticaDTO> listarProtocolosSemEstatistica(Integer ano) {

        Set<Long> idsComEstatistica = estatisticaRepository.findAll().stream()
                .map(e -> e.getProtocoloME() != null ? e.getProtocoloME().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return protocoloRepository.findAll().stream()
                .filter(p -> !idsComEstatistica.contains(p.getId()))
                .filter(p -> ano == null ||
                        (p.getDataNotificacao() != null &&
                         p.getDataNotificacao().getYear() == ano))
                .map(p -> {
                    ProtocoloSemEstatisticaDTO dto = new ProtocoloSemEstatisticaDTO();
                    dto.setProtocoloMEId(p.getId());
                    dto.setNumeroProtocolo(p.getNumeroProtocolo());
                    dto.setNomeDoador(p.getPaciente() != null ? p.getPaciente().getNome() : null);
                    dto.setAno(ano);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public EstatisticaProtocoloMEDTO obterPorProtocoloId(Long protocoloId) {
        EstatisticaProtocoloME estatistica = estatisticaRepository
                .findByProtocoloMEId(protocoloId)
                .orElseGet(() -> gerarPorProtocolo(protocoloId));

        return toDTO(estatistica, estatistica.getProtocoloME());
    }

    public EstatisticaProtocoloMEDTO salvarOuAtualizar(Long protocoloId, EstatisticaProtocoloMEDTO payload) {

        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado"));

        EstatisticaProtocoloME estatistica = estatisticaRepository
                .findByProtocoloMEId(protocoloId)
                .orElseGet(() -> {
                    EstatisticaProtocoloME nova = new EstatisticaProtocoloME();
                    nova.setProtocoloME(protocolo);
                    return nova;
                });

        if (payload.getAnoCompetencia() != null) {
            estatistica.setAnoCompetencia(payload.getAnoCompetencia());
        }
        if (payload.getMesCompetencia() != null) {
            estatistica.setMesCompetencia(payload.getMesCompetencia());
        }
        if (payload.getPeriodicidade() != null && !payload.getPeriodicidade().isBlank()) {
            estatistica.setPeriodicidade(EstatisticaProtocoloME.Periodicidade.valueOf(payload.getPeriodicidade().toUpperCase()));
        }
        estatistica.setAtualizadoPor(payload.getAtualizadoPor());

        Map<String, String> campos = payload.getCampos() != null
                ? new LinkedHashMap<>(payload.getCampos())
                : new LinkedHashMap<>();
        aplicarCamposNaEntidade(estatistica, campos);

        EstatisticaProtocoloME salvo = estatisticaRepository.save(estatistica);
        return toDTO(salvo, protocolo);
    }

    // =========================
    // REFLECTION HELPERS
    // =========================

    private void aplicarCamposNaEntidade(EstatisticaProtocoloME estatistica, Map<String, String> campos) {

        estatistica.setDadosCamposJson(writeCamposJson(campos));

        for (Map.Entry<String, String> entry : campos.entrySet()) {

            Field field = FIELD_CACHE.get(entry.getKey());

            if (field != null && field.getType() == String.class) {
                try {
                    field.set(estatistica, entry.getValue());
                } catch (IllegalAccessException e) {
                    throw new IllegalStateException("Erro ao aplicar campo " + entry.getKey(), e);
                }
            }
        }
    }

    private String getStringField(EstatisticaProtocoloME estatistica, String fieldName) {
        try {
            Field field = FIELD_CACHE.get(fieldName);
            if (field == null) return null;
            Object value = field.get(estatistica);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private String writeCamposJson(Map<String, String> campos) {
        try {
            return objectMapper.writeValueAsString(
                    campos != null ? campos : Collections.emptyMap()
            );
        } catch (Exception e) {
            throw new IllegalStateException("Erro ao serializar campos", e);
        }
    }

    // =========================
    // DTO
    // =========================

    private EstatisticaProtocoloMEDTO toDTO(EstatisticaProtocoloME estatistica, ProtocoloME protocolo) {

        EstatisticaProtocoloMEDTO dto = new EstatisticaProtocoloMEDTO();

        dto.setId(estatistica.getId());
        dto.setProtocoloMEId(protocolo != null ? protocolo.getId() : null);
        dto.setNumeroProtocolo(protocolo != null ? protocolo.getNumeroProtocolo() : null);

        dto.setNomeDoador(getStringField(estatistica, "nomeDoador"));
        dto.setAnoCompetencia(estatistica.getAnoCompetencia());
        dto.setMesCompetencia(estatistica.getMesCompetencia());
        dto.setPeriodicidade(estatistica.getPeriodicidade() != null
            ? estatistica.getPeriodicidade().name()
            : null);

        Map<String, String> campos = new LinkedHashMap<>();

        for (String campo : CAMPOS_PROTOCOLO) {
            String valor = getStringField(estatistica, campo);
            if (valor != null) campos.put(campo, valor);
        }

        for (String campo : CAMPOS_SIM_NAO) {
            String valor = getStringField(estatistica, campo);
            if (valor != null) campos.put(campo, valor);
        }

        dto.setCampos(campos);

        return dto;
    }
}
