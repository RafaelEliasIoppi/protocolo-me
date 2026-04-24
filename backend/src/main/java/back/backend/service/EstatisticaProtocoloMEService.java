package back.backend.service;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.model.EstatisticaProtocoloME;
import back.backend.model.ProtocoloME;
import back.backend.repository.EstatisticaProtocoloMERepository;
import back.backend.repository.ProtocoloMERepository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

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

    private static final List<String> CAMPOS_PROTOCOLO = Arrays.asList(
            "ofNac","rgctDoador","nomeDoador","hospitalNotif","dataOf","regPdot","regOf",
            "mes","municipio","idDoad","faixaEtariaDoad","sexoDoad","aboDoad",
            "resCausaMorte","dm","has","etilismo","tabagismo","crInicial","crFinal",
            "rimD","rimE","coracao","pulmD","pulmE","figado","corneas","pele","ossoMusculo",
            "destRimD","destRimE","destCoracao","destPulmD","destPulmE","destFigado",
            "txRinsBloco","txPulmBilat","txRimFig","txPulmDRim","txPulmERim","txCorRim",
            "txCorPulm","descarteRimD","descarteRimE","descarteCoracao","descartePulmaoD",
            "descartePulmaoE","descarteFigado","motivoDescarteEsclarecer",
            "hospEquipeRecRd","rgctRd","receptorRd","idadeRecRd","sexoRecRd","mesTxRd",
            "hospEquipeRecRe","rgctRe","receptorRe","idadeRecRe","sexoRecRe","mesTxRe",
            "hospEquipeRecFig","rgctFig","receptorFig","idadeRecFig","sexoRecFig","mesTxFig",
            "hospEquipeRecPulmD","rgctPulmD","receptorPulmD","idadeRecPulmD","sexoRecPulmD","mesTxPulmD",
            "hospEquipeRecPulmE","rgctPulmE","receptorPulmE","idadeRecPulmE","sexoRecPulmE","mesTxPulmE",
            "hospEquipeRecCor","rgctCor","receptorCor","idadeRecCor","sexoRecCor","mesTxCor",
            "doadorOfertaNacional","classif","algumOrgaoImplantadoNoRs",
            "recusaRim","recusaFigado","recusaCoracao","recusaPulmao","observacoes"
    );

    private static final List<String> CAMPOS_SIM_NAO = Arrays.asList(
            "dm","has","etilismo","tabagismo","rimD","rimE","coracao","pulmD","pulmE","figado",
            "corneas","pele","ossoMusculo","txRinsBloco","txPulmBilat","txRimFig","txPulmDRim",
            "txPulmERim","txCorRim","txCorPulm","descarteRimD","descarteRimE","descarteCoracao",
            "descartePulmaoD","descartePulmaoE","descarteFigado","doadorOfertaNacional",
            "algumOrgaoImplantadoNoRs","recusaRim","recusaFigado","recusaCoracao","recusaPulmao"
    );

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
                throw new RuntimeException("Erro ao migrar dados", e);
            }
        }
    }

    public EstatisticaProtocoloMEDTO obterPorProtocoloId(Long protocoloId) {

        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        return estatisticaRepository.findByProtocoloMEId(protocoloId)
                .map(e -> toDTO(e, protocolo))
                .orElseGet(() -> toDTO(new EstatisticaProtocoloME(), protocolo));
    }

    public EstatisticaProtocoloMEDTO salvarOuAtualizar(Long protocoloId,
                                                       EstatisticaProtocoloMEDTO payload) {

        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        EstatisticaProtocoloME estatistica =
                estatisticaRepository.findByProtocoloMEId(protocoloId)
                        .orElseGet(EstatisticaProtocoloME::new);

        estatistica.setProtocoloME(protocolo);
        estatistica.setAtualizadoPor(payload.getAtualizadoPor());
        estatistica.setDataAtualizacao(LocalDateTime.now());

        aplicarCamposNaEntidade(estatistica, payload.getCampos());

        return toDTO(estatisticaRepository.save(estatistica), protocolo);
    }

    // restante da sua lógica mantida igual (reflection etc.)

    private String normalizarPeriodicidade(String periodicidade) {
        if (periodicidade == null) return "ANUAL";
        String p = periodicidade.toUpperCase();
        return (p.equals("MENSAL") || p.equals("ANUAL")) ? p : "ANUAL";
    }

    public List<EstatisticaProtocoloMEDTO> listarPorPeriodicidade(String periodicidade, Integer ano, Integer mes) {
        String periodicidadeNormalizada = normalizarPeriodicidade(periodicidade);

        return estatisticaRepository.findAll().stream()
                .filter(estatistica -> periodicidadeNormalizada.equalsIgnoreCase(normalizarPeriodicidade(estatistica.getPeriodicidade())))
                .filter(estatistica -> ano == null || Objects.equals(estatistica.getAnoCompetencia(), ano))
                .filter(estatistica -> mes == null || Objects.equals(estatistica.getMesCompetencia(), mes))
                .map(estatistica -> toDTO(estatistica, estatistica.getProtocoloME()))
                .collect(Collectors.toList());
    }

    public List<ProtocoloSemEstatisticaDTO> listarProtocolosSemEstatistica(Integer ano) {
        Set<Long> idsComEstatistica = estatisticaRepository.findAll().stream()
                .map(e -> e.getProtocoloME() != null ? e.getProtocoloME().getId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return protocoloRepository.findAll().stream()
                .filter(protocolo -> !idsComEstatistica.contains(protocolo.getId()))
                .filter(protocolo -> ano == null || (protocolo.getDataNotificacao() != null && protocolo.getDataNotificacao().getYear() == ano))
                .map(protocolo -> {
                    ProtocoloSemEstatisticaDTO dto = new ProtocoloSemEstatisticaDTO();
                    dto.setProtocoloId(protocolo.getId());
                    dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
                    dto.setPacienteId(protocolo.getPaciente() != null ? protocolo.getPaciente().getId() : null);
                    dto.setNomeDoador(protocolo.getPaciente() != null ? protocolo.getPaciente().getNome() : null);
                    dto.setAno(ano);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private void aplicarCamposNaEntidade(EstatisticaProtocoloME estatistica, Map<String, String> campos) {
        estatistica.setDadosCamposJson(writeCamposJson(campos));

        for (Map.Entry<String, String> entry : campos.entrySet()) {
            try {
                Field field = EstatisticaProtocoloME.class.getDeclaredField(entry.getKey());
                field.setAccessible(true);
                if (field.getType() == String.class) {
                    field.set(estatistica, entry.getValue());
                }
            } catch (NoSuchFieldException ignored) {
                // Campo legado não mapeado diretamente
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Erro ao aplicar campo " + entry.getKey(), e);
            }
        }
    }

    private EstatisticaProtocoloMEDTO toDTO(EstatisticaProtocoloME estatistica, ProtocoloME protocolo) {
        EstatisticaProtocoloMEDTO dto = new EstatisticaProtocoloMEDTO();
        dto.setId(estatistica.getId());
        dto.setProtocoloMEId(protocolo != null ? protocolo.getId() : null);
        dto.setNumeroProtocolo(protocolo != null ? protocolo.getNumeroProtocolo() : null);
        dto.setPacienteId(protocolo != null && protocolo.getPaciente() != null ? protocolo.getPaciente().getId() : null);
        dto.setNomeDoador(getStringField(estatistica, "nomeDoador"));
        dto.setAnoCompetencia(estatistica.getAnoCompetencia());
        dto.setMesCompetencia(estatistica.getMesCompetencia());
        dto.setPeriodicidade(estatistica.getPeriodicidade());
        dto.setAtualizadoPor(estatistica.getAtualizadoPor());
        dto.setDataAtualizacao(estatistica.getDataAtualizacao());

        Map<String, String> campos = new LinkedHashMap<>();
        for (String campo : CAMPOS_PROTOCOLO) {
            String valor = getStringField(estatistica, campo);
            if (valor != null) {
                campos.put(campo, valor);
            }
        }
        for (String campo : CAMPOS_SIM_NAO) {
            String valor = getStringField(estatistica, campo);
            if (valor != null && !campos.containsKey(campo)) {
                campos.put(campo, valor);
            }
        }

        dto.setCampos(campos);
        return dto;
    }

    private String getStringField(EstatisticaProtocoloME estatistica, String fieldName) {
        try {
            Field field = EstatisticaProtocoloME.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(estatistica);
            return value != null ? value.toString() : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private String writeCamposJson(Map<String, String> campos) {
        try {
            return objectMapper.writeValueAsString(campos != null ? campos : Collections.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar campos da estatística", e);
        }
    }

    public static class ProtocoloSemEstatisticaDTO {

        private Long protocoloId;
        private String numeroProtocolo;
        private Long pacienteId;
        private String nomeDoador;
        private Integer ano;

        public Long getProtocoloId() { return protocoloId; }
        public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

        public Long getPacienteId() { return pacienteId; }
        public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

        public String getNomeDoador() { return nomeDoador; }
        public void setNomeDoador(String nomeDoador) { this.nomeDoador = nomeDoador; }

        public Integer getAno() { return ano; }
        public void setAno(Integer ano) { this.ano = ano; }
    }
}