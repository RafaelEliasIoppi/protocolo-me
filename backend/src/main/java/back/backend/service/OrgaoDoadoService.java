@Service
public class OrgaoDoadoService {

    private final OrgaoDoadoRepository orgaoDoadoRepository;
    private final ProtocoloMERepository protocoloMERepository;

    public OrgaoDoadoService(
            OrgaoDoadoRepository orgaoDoadoRepository,
            ProtocoloMERepository protocoloMERepository) {
        this.orgaoDoadoRepository = orgaoDoadoRepository;
        this.protocoloMERepository = protocoloMERepository;
    }

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

    // ---------------- CREATE ----------------

    public OrgaoDoado criar(OrgaoDoado orgaoDoado) {

        validarProtocolo(orgaoDoado);

        validarNome(orgaoDoado);

        aplicarStatusPadrao(orgaoDoado);

        orgaoDoado.setCpfReceptor(normalizarCpf(orgaoDoado.getCpfReceptor()));

        return orgaoDoadoRepository.save(orgaoDoado);
    }

    // ---------------- UPDATE ----------------

    public OrgaoDoado atualizar(Long id, OrgaoDoado atualizado) {

        OrgaoDoado entity = buscarEntity(id);

        if (atualizado.getNomeOrgao() != null) {
            entity.setNomeOrgao(validarENormalizarNomeOrgao(atualizado.getNomeOrgao()));
        }

        if (atualizado.getStatus() != null) {
            entity.setStatus(atualizado.getStatus());
            aplicarDatasStatus(entity);
        }

        copiarCamposSimples(entity, atualizado);

        return orgaoDoadoRepository.save(entity);
    }

    // ---------------- BUSINESS METHODS ----------------

    public OrgaoDoado registrarImplantacao(Long id, String hospital, String paciente) {

        OrgaoDoado entity = buscarEntity(id);

        entity.setStatus(OrgaoDoado.StatusOrgaoDoado.IMPLANTADO);
        entity.setHospitalReceptor(hospital);
        entity.setPacienteReceptor(paciente);
        entity.setDataImplantacao(LocalDateTime.now());

        return orgaoDoadoRepository.save(entity);
    }

    public OrgaoDoado registrarDescarte(Long id, String motivo) {

        OrgaoDoado entity = buscarEntity(id);

        entity.setStatus(OrgaoDoado.StatusOrgaoDoado.DESCARTADO);
        entity.setMotivoDescarte(motivo);
        entity.setDataDescarte(LocalDateTime.now());

        return orgaoDoadoRepository.save(entity);
    }

    // ---------------- QUERY ----------------

    public Optional<OrgaoDoado> buscarPorId(Long id) {
        return orgaoDoadoRepository.findById(id);
    }

    public List<OrgaoDoado> listarPorProtocolo(Long protocoloId) {
        return orgaoDoadoRepository.findByProtocoloMEId(protocoloId);
    }

    public void deletar(Long id) {
        if (!orgaoDoadoRepository.existsById(id)) {
            throw new RuntimeException("Órgão doado não encontrado");
        }
        orgaoDoadoRepository.deleteById(id);
    }

    // ---------------- STATS ----------------

    public OrgaoStatisticas obterEstatisticas(Long protocoloId) {

        List<OrgaoDoado> lista = listarPorProtocolo(protocoloId);

        long total = lista.size();
        long implantados = lista.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.IMPLANTADO)
                .count();

        long descartados = lista.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.DESCARTADO)
                .count();

        long aguardando = lista.stream()
                .filter(o -> o.getStatus() == OrgaoDoado.StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO)
                .count();

        return new OrgaoStatisticas(total, implantados, descartados, aguardando);
    }

    // ---------------- PRIVATE HELPERS ----------------

    private OrgaoDoado buscarEntity(Long id) {
        return orgaoDoadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Órgão doado não encontrado"));
    }

    private void validarProtocolo(OrgaoDoado orgao) {
        if (orgao.getProtocoloME() == null || orgao.getProtocoloME().getId() == null) {
            throw new RuntimeException("Protocolo de ME é obrigatório");
        }

        ProtocoloME protocolo = protocoloMERepository.findById(orgao.getProtocoloME().getId())
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        orgao.setProtocoloME(protocolo);
    }

    private void validarNome(OrgaoDoado orgao) {
        if (orgao.getNomeOrgao() == null || orgao.getNomeOrgao().isBlank()) {
            throw new RuntimeException("Nome do órgão é obrigatório");
        }

        orgao.setNomeOrgao(validarENormalizarNomeOrgao(orgao.getNomeOrgao()));
    }

    private void aplicarStatusPadrao(OrgaoDoado orgao) {
        if (orgao.getStatus() == null) {
            orgao.setStatus(OrgaoDoado.StatusOrgaoDoado.AGUARDANDO_IMPLANTACAO);
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

    private void copiarCamposSimples(OrgaoDoado destino, OrgaoDoado origem) {
        if (origem.getMotivo() != null) destino.setMotivo(origem.getMotivo());
        if (origem.getHospitalReceptor() != null) destino.setHospitalReceptor(origem.getHospitalReceptor());
        if (origem.getPacienteReceptor() != null) destino.setPacienteReceptor(origem.getPacienteReceptor());
        if (origem.getCpfReceptor() != null) destino.setCpfReceptor(normalizarCpf(origem.getCpfReceptor()));
        if (origem.getDataArmazenamento() != null) destino.setDataArmazenamento(origem.getDataArmazenamento());
        if (origem.getMotivoDescarte() != null) destino.setMotivoDescarte(origem.getMotivoDescarte());
        if (origem.getObservacoes() != null) destino.setObservacoes(origem.getObservacoes());
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

        return n.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    // ---------------- INNER CLASS ----------------

    public static class OrgaoStatisticas {
        public final long total;
        public final long implantados;
        public final long descartados;
        public final long aguardando;

        public OrgaoStatisticas(long total, long implantados, long descartados, long aguardando) {
            this.total = total;
            this.implantados = implantados;
            this.descartados = descartados;
            this.aguardando = aguardando;
        }
    }
}