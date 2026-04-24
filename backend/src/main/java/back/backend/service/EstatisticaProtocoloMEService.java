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
}