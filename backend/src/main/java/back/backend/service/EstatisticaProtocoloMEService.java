package back.backend.service;

import back.backend.dto.EstatisticaProtocoloMEDTO;
import back.backend.model.EstatisticaProtocoloME;
import back.backend.model.ProtocoloME;
import back.backend.repository.EstatisticaProtocoloMERepository;
import back.backend.repository.ProtocoloMERepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EstatisticaProtocoloMEService {

    private static final TypeReference<Map<String, String>> TYPE_REF_MAP = new TypeReference<Map<String, String>>() {};

    private static final List<String> CAMPOS_PROTOCOLO = Arrays.asList(
            "ofNac",
            "rgctDoador",
            "nomeDoador",
            "hospitalNotif",
            "dataOf",
            "regPdot",
            "regOf",
            "mes",
            "municipio",
            "idDoad",
            "faixaEtariaDoad",
            "sexoDoad",
            "aboDoad",
            "resCausaMorte",
            "dm",
            "has",
            "etilismo",
            "tabagismo",
            "crInicial",
            "crFinal",
            "rimD",
            "rimE",
            "coracao",
            "pulmD",
            "pulmE",
            "figado",
            "corneas",
            "pele",
            "ossoMusculo",
            "destRimD",
            "destRimE",
            "destCoracao",
            "destPulmD",
            "destPulmE",
            "destFigado",
            "txRinsBloco",
            "txPulmBilat",
            "txRimFig",
            "txPulmDRim",
            "txPulmERim",
            "txCorRim",
            "txCorPulm",
            "descarteRimD",
            "descarteRimE",
            "descarteCoracao",
            "descartePulmaoD",
            "descartePulmaoE",
            "descarteFigado",
            "motivoDescarteEsclarecer",
            "hospEquipeRecRd",
            "rgctRd",
            "receptorRd",
            "idadeRecRd",
            "sexoRecRd",
            "mesTxRd",
            "hospEquipeRecRe",
            "rgctRe",
            "receptorRe",
            "idadeRecRe",
            "sexoRecRe",
            "mesTxRe",
            "hospEquipeRecFig",
            "rgctFig",
            "receptorFig",
            "idadeRecFig",
            "sexoRecFig",
            "mesTxFig",
            "hospEquipeRecPulmD",
            "rgctPulmD",
            "receptorPulmD",
            "idadeRecPulmD",
            "sexoRecPulmD",
            "mesTxPulmD",
            "hospEquipeRecPulmE",
            "rgctPulmE",
            "receptorPulmE",
            "idadeRecPulmE",
            "sexoRecPulmE",
            "mesTxPulmE",
            "hospEquipeRecCor",
            "rgctCor",
            "receptorCor",
            "idadeRecCor",
            "sexoRecCor",
            "mesTxCor",
            "doadorOfertaNacional",
            "classif",
            "algumOrgaoImplantadoNoRs",
            "recusaRim",
            "recusaFigado",
            "recusaCoracao",
            "recusaPulmao",
            "observacoes"
    );

    @Autowired
    private EstatisticaProtocoloMERepository estatisticaRepository;

    @Autowired
    private ProtocoloMERepository protocoloRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void migrarDadosLegadosParaColunas() {
        List<EstatisticaProtocoloME> estatisticas = estatisticaRepository.findAll();
        boolean houveMigracao = false;

        for (EstatisticaProtocoloME estatistica : estatisticas) {
            if (estatistica.getDadosCamposJson() == null || estatistica.getDadosCamposJson().trim().isEmpty()) {
                continue;
            }

            try {
                Map<String, String> campos = objectMapper.readValue(estatistica.getDadosCamposJson(), TYPE_REF_MAP);
                aplicarCamposNaEntidade(estatistica, campos);
                estatisticaRepository.save(estatistica);
                houveMigracao = true;
            } catch (Exception e) {
                throw new RuntimeException("Erro ao migrar dados legados de estatística para colunas", e);
            }
        }

        if (houveMigracao) {
            return;
        }
    }

    public EstatisticaProtocoloMEDTO obterPorProtocoloId(Long protocoloId) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        Optional<EstatisticaProtocoloME> estatisticaOpt = estatisticaRepository.findByProtocoloMEId(protocoloId);
        if (estatisticaOpt.isEmpty()) {
            return toDTO(new EstatisticaProtocoloME(), protocolo);
        }

        return toDTO(estatisticaOpt.get(), protocolo);
    }

    public EstatisticaProtocoloMEDTO salvarOuAtualizar(Long protocoloId, EstatisticaProtocoloMEDTO payload) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        EstatisticaProtocoloME estatistica = estatisticaRepository.findByProtocoloMEId(protocoloId)
                .orElseGet(EstatisticaProtocoloME::new);

        estatistica.setProtocoloME(protocolo);

        Integer ano = payload.getAnoCompetencia();
        Integer mes = payload.getMesCompetencia();
        if (ano == null) {
            LocalDateTime dataBase = protocolo.getDataNotificacao() != null ? protocolo.getDataNotificacao() : protocolo.getDataCriacao();
            if (dataBase != null) {
                ano = dataBase.getYear();
                mes = dataBase.getMonthValue();
            }
        }

        estatistica.setAnoCompetencia(ano);
        estatistica.setMesCompetencia(mes);
        estatistica.setPeriodicidade(normalizarPeriodicidade(payload.getPeriodicidade()));
        estatistica.setAtualizadoPor(payload.getAtualizadoPor());
        estatistica.setDataAtualizacao(LocalDateTime.now());

        aplicarCamposNaEntidade(estatistica, payload.getCampos());

        EstatisticaProtocoloME salvo = estatisticaRepository.save(estatistica);
        return toDTO(salvo, protocolo);
    }

    public List<EstatisticaProtocoloMEDTO> listarPorPeriodicidade(String periodicidade, Integer ano, Integer mes) {
        String periodicidadeNormalizada = normalizarPeriodicidade(periodicidade);
        List<ProtocoloME> protocolos = protocoloRepository.findAll();

        return protocolos.stream()
                .filter(p -> filtrarPeriodo(p, periodicidadeNormalizada, ano, mes))
                .map(this::montarDTOComEstatisticaOpcional)
                .collect(Collectors.toList());
    }

    public List<ProtocoloSemEstatisticaDTO> listarProtocolosSemEstatistica(Integer ano) {
        List<ProtocoloME> protocolos = protocoloRepository.findAll();

        return protocolos.stream()
                .filter(protocolo -> {
                    LocalDateTime dataBase = protocolo.getDataNotificacao() != null ? protocolo.getDataNotificacao() : protocolo.getDataCriacao();
                    return ano == null || (dataBase != null && dataBase.getYear() == ano);
                })
                .filter(protocolo -> !possuiEstatisticaPreenchida(protocolo))
                .map(this::toProtocoloSemEstatisticaDTO)
                .collect(Collectors.toList());
    }

    private boolean filtrarPeriodo(ProtocoloME protocolo, String periodicidade, Integer ano, Integer mes) {
        LocalDateTime dataBase = protocolo.getDataNotificacao() != null ? protocolo.getDataNotificacao() : protocolo.getDataCriacao();
        if (dataBase == null) {
            return false;
        }

        if (ano != null && dataBase.getYear() != ano) {
            return false;
        }

        if ("MENSAL".equals(periodicidade) && mes != null) {
            return dataBase.getMonthValue() == mes;
        }

        return true;
    }

    private EstatisticaProtocoloMEDTO montarDTOComEstatisticaOpcional(ProtocoloME protocolo) {
        Optional<EstatisticaProtocoloME> estatisticaOpt = estatisticaRepository.findByProtocoloMEId(protocolo.getId());
        if (estatisticaOpt.isPresent()) {
            return toDTO(estatisticaOpt.get(), protocolo);
        }
        return toDTO(new EstatisticaProtocoloME(), protocolo);
    }

    private boolean possuiEstatisticaPreenchida(ProtocoloME protocolo) {
        Optional<EstatisticaProtocoloME> estatisticaOpt = estatisticaRepository.findByProtocoloMEId(protocolo.getId());
        if (estatisticaOpt.isEmpty()) {
            return false;
        }

        EstatisticaProtocoloME estatistica = estatisticaOpt.get();
        for (String nomeCampo : CAMPOS_PROTOCOLO) {
            String valor = getFieldValue(estatistica, nomeCampo);
            if (valor != null && !valor.trim().isEmpty()) {
                return true;
            }
        }

        return false;
    }

    private ProtocoloSemEstatisticaDTO toProtocoloSemEstatisticaDTO(ProtocoloME protocolo) {
        ProtocoloSemEstatisticaDTO dto = new ProtocoloSemEstatisticaDTO();
        dto.setProtocoloMEId(protocolo.getId());
        dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
        dto.setNomeDoador(protocolo.getPaciente() != null ? protocolo.getPaciente().getNome() : null);
        dto.setHospitalOrigem(protocolo.getHospitalOrigem());
        dto.setDataNotificacao(protocolo.getDataNotificacao());
        dto.setStatus(protocolo.getStatus() != null ? protocolo.getStatus().name() : null);
        dto.setMensagem("Protocolo sem estatística preenchida");
        return dto;
    }

    private EstatisticaProtocoloMEDTO toDTO(EstatisticaProtocoloME entity, ProtocoloME protocolo) {
        EstatisticaProtocoloMEDTO dto = new EstatisticaProtocoloMEDTO();
        dto.setId(entity.getId());
        dto.setProtocoloMEId(protocolo.getId());
        dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
        dto.setPacienteId(protocolo.getPaciente() != null ? protocolo.getPaciente().getId() : null);
        dto.setNomeDoador(protocolo.getPaciente() != null ? protocolo.getPaciente().getNome() : null);
        dto.setAnoCompetencia(entity.getAnoCompetencia());
        dto.setMesCompetencia(entity.getMesCompetencia());
        dto.setPeriodicidade(entity.getPeriodicidade() != null ? entity.getPeriodicidade() : "ANUAL");
        dto.setAtualizadoPor(entity.getAtualizadoPor());
        dto.setDataAtualizacao(entity.getDataAtualizacao());

        dto.setCampos(extrairCamposDaEntidade(entity));

        if (dto.getAnoCompetencia() == null) {
            LocalDateTime dataBase = protocolo.getDataNotificacao() != null ? protocolo.getDataNotificacao() : protocolo.getDataCriacao();
            if (dataBase != null) {
                dto.setAnoCompetencia(dataBase.getYear());
                dto.setMesCompetencia(dataBase.getMonthValue());
            }
        }

        return dto;
    }

    private void aplicarCamposNaEntidade(EstatisticaProtocoloME entidade, Map<String, String> camposPayload) {
        Map<String, String> camposAtualizados = extrairCamposDaEntidade(entidade);
        if (camposPayload != null) {
            camposAtualizados.putAll(camposPayload);
        }

        for (String nomeCampo : CAMPOS_PROTOCOLO) {
            setFieldValue(entidade, nomeCampo, camposAtualizados.get(nomeCampo));
        }

        try {
            entidade.setDadosCamposJson(objectMapper.writeValueAsString(camposAtualizados));
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar campos de estatística", e);
        }
    }

    private Map<String, String> extrairCamposDaEntidade(EstatisticaProtocoloME entidade) {
        Map<String, String> campos = new LinkedHashMap<>();
        boolean encontrouCampoNaoNulo = false;

        for (String nomeCampo : CAMPOS_PROTOCOLO) {
            String valor = getFieldValue(entidade, nomeCampo);
            if (valor != null) {
                encontrouCampoNaoNulo = true;
            }
            campos.put(nomeCampo, valor);
        }

        if (!encontrouCampoNaoNulo && entidade.getDadosCamposJson() != null && !entidade.getDadosCamposJson().trim().isEmpty()) {
            try {
                Map<String, String> fallback = objectMapper.readValue(entidade.getDadosCamposJson(), TYPE_REF_MAP);
                for (String nomeCampo : CAMPOS_PROTOCOLO) {
                    if (fallback.containsKey(nomeCampo)) {
                        campos.put(nomeCampo, fallback.get(nomeCampo));
                    }
                }
            } catch (Exception ignored) {
                return campos;
            }
        }

        return campos;
    }

    private String getFieldValue(EstatisticaProtocoloME entidade, String nomeCampo) {
        try {
            Field field = EstatisticaProtocoloME.class.getDeclaredField(nomeCampo);
            field.setAccessible(true);
            Object valor = field.get(entidade);
            return valor != null ? String.valueOf(valor) : null;
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    private void setFieldValue(EstatisticaProtocoloME entidade, String nomeCampo, String valor) {
        try {
            Field field = EstatisticaProtocoloME.class.getDeclaredField(nomeCampo);
            field.setAccessible(true);
            field.set(entidade, valor);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Erro ao aplicar campo de estatística: " + nomeCampo, e);
        }
    }

    public static class ProtocoloSemEstatisticaDTO {
        private Long protocoloMEId;
        private String numeroProtocolo;
        private String nomeDoador;
        private String hospitalOrigem;
        private LocalDateTime dataNotificacao;
        private String status;
        private String mensagem;

        public Long getProtocoloMEId() { return protocoloMEId; }
        public void setProtocoloMEId(Long protocoloMEId) { this.protocoloMEId = protocoloMEId; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

        public String getNomeDoador() { return nomeDoador; }
        public void setNomeDoador(String nomeDoador) { this.nomeDoador = nomeDoador; }

        public String getHospitalOrigem() { return hospitalOrigem; }
        public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

        public LocalDateTime getDataNotificacao() { return dataNotificacao; }
        public void setDataNotificacao(LocalDateTime dataNotificacao) { this.dataNotificacao = dataNotificacao; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMensagem() { return mensagem; }
        public void setMensagem(String mensagem) { this.mensagem = mensagem; }
    }

    private String normalizarPeriodicidade(String periodicidade) {
        if (periodicidade == null) {
            return "ANUAL";
        }
        String valor = periodicidade.trim().toUpperCase();
        if (!"MENSAL".equals(valor) && !"ANUAL".equals(valor)) {
            return "ANUAL";
        }
        return valor;
    }
}
