package back.backend.service;

import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.Paciente;
import back.backend.model.Hospital;
import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import back.backend.model.AnexoDocumento;
import back.backend.repository.PacienteRepository;
import back.backend.repository.HospitalRepository;
import back.backend.repository.AnexoDocumentoRepository;
import back.backend.repository.EstatisticaProtocoloMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    @Autowired
    private AnexoDocumentoRepository anexoDocumentoRepository;

    @Autowired
    private EstatisticaProtocoloMERepository estatisticaProtocoloMERepository;

    /**
     * Criar novo paciente
     */
    public Paciente criarPaciente(Paciente paciente) {
        if (paciente.getStatus() == null || paciente.getStatus() == Paciente.StatusPaciente.EM_PROTOCOLO_ME) {
            paciente.setStatus(Paciente.StatusPaciente.INTERNADO);
        }
        paciente.setCpf(normalizarCpf(paciente.getCpf()));
        preencherHospitalOrigemSeNecessario(paciente);
        validarPaciente(paciente);
        return pacienteRepository.save(paciente);
    }

    /**
     * Atualizar dados do paciente
     */
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
        Paciente paciente = obterPacientePorId(id);

        if (pacienteAtualizado.getCpf() != null && !pacienteAtualizado.getCpf().trim().isEmpty()) {
            paciente.setCpf(normalizarCpf(pacienteAtualizado.getCpf()));
        }

        if (pacienteAtualizado.getHospital() != null) {
            paciente.setHospital(pacienteAtualizado.getHospital());
        }
        
        if (pacienteAtualizado.getNome() != null) paciente.setNome(pacienteAtualizado.getNome());
        if (pacienteAtualizado.getDataNascimento() != null) paciente.setDataNascimento(pacienteAtualizado.getDataNascimento());
        if (pacienteAtualizado.getGenero() != null) paciente.setGenero(pacienteAtualizado.getGenero());
        if (pacienteAtualizado.getLeito() != null) paciente.setLeito(pacienteAtualizado.getLeito());
        if (pacienteAtualizado.getDiagnosticoPrincipal() != null) paciente.setDiagnosticoPrincipal(pacienteAtualizado.getDiagnosticoPrincipal());
        if (pacienteAtualizado.getHistoricoMedico() != null) paciente.setHistoricoMedico(pacienteAtualizado.getHistoricoMedico());
        if (pacienteAtualizado.getNomeResponsavel() != null) paciente.setNomeResponsavel(pacienteAtualizado.getNomeResponsavel());
        if (pacienteAtualizado.getTelefoneResponsavel() != null) paciente.setTelefoneResponsavel(pacienteAtualizado.getTelefoneResponsavel());
        if (pacienteAtualizado.getEmailResponsavel() != null) paciente.setEmailResponsavel(pacienteAtualizado.getEmailResponsavel());
        if (pacienteAtualizado.getStatusEntrevistaFamiliar() != null) paciente.setStatusEntrevistaFamiliar(pacienteAtualizado.getStatusEntrevistaFamiliar());
        if (pacienteAtualizado.getObservacoesEntrevistaFamiliar() != null) paciente.setObservacoesEntrevistaFamiliar(pacienteAtualizado.getObservacoesEntrevistaFamiliar());
        if (pacienteAtualizado.getDataEntrevistaFamiliar() != null) paciente.setDataEntrevistaFamiliar(pacienteAtualizado.getDataEntrevistaFamiliar());
        if (pacienteAtualizado.getStatus() != null) paciente.setStatus(pacienteAtualizado.getStatus());

        preencherHospitalOrigemSeNecessario(paciente);
        
        return pacienteRepository.save(paciente);
    }

    /**
     * Atualizar status do paciente
     */
    public Paciente atualizarStatus(Long id, Paciente.StatusPaciente novoStatus) {
        Paciente paciente = obterPacientePorId(id);
        paciente.setStatus(novoStatus);
        return pacienteRepository.save(paciente);
    }

    public Paciente atualizarStatus(Long id, String novoStatus) {
        Paciente.StatusPaciente status = Paciente.StatusPaciente.valueOf(novoStatus.toUpperCase());
        return atualizarStatus(id, status);
    }

    /**
     * Obter paciente por ID
     */
    public Paciente obterPacientePorId(Long id) {
        return pacienteRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com ID: " + id));
    }

    /**
     * Obter paciente por CPF
     */
    public Paciente obterPacientePorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado com CPF: " + cpf));
    }

    /**
     * Listar todos os pacientes
     */
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    /**
     * Listar pacientes por hospital
     */
    public List<Paciente> listarPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospital(hospital);
    }

    /**
     * Listar pacientes por status
     */
    public List<Paciente> listarPorStatus(Paciente.StatusPaciente status) {
        return pacienteRepository.findByStatus(status);
    }

    public List<Paciente> listarPorStatus(String status) {
        Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
        return listarPorStatus(statusEnum);
    }

    /**
     * Listar pacientes por hospital e status
     */
    public List<Paciente> listarPorHospitalEStatus(Long hospitalId, Paciente.StatusPaciente status) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospitalAndStatus(hospital, status);
    }

    public List<Paciente> listarPorHospitalEStatus(Long hospitalId, String status) {
        Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
        return listarPorHospitalEStatus(hospitalId, statusEnum);
    }

    /**
     * Procurar pacientes por nome
     */
    public List<Paciente> procurarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Procurar pacientes por nome em um hospital específico
     */
    public List<Paciente> procurarPorNomeEHospital(Long hospitalId, String nome) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospitalAndNomeContainingIgnoreCase(hospital, nome);
    }

    /**
     * Listar apenas pacientes que já entraram em Protocolo de ME
     */
    public List<Paciente> listarPacientesEmProtocoloME() {
        return pacienteRepository.findPacientesEmProtocoloME();
    }

    /**
     * Listar apenas pacientes em Protocolo de ME de um hospital específico
     */
    public List<Paciente> listarPacientesEmProtocoloMEPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findPacientesEmProtocoloMEPorHospital(hospital);
    }

    /**
     * Deletar paciente
     */
    public void deletarPaciente(Long id) {
        Paciente paciente = obterPacientePorId(id);

        List<ProtocoloME> protocolos = paciente.getProtocolosME() != null
            ? new ArrayList<>(paciente.getProtocolosME())
            : new ArrayList<>();

        for (ProtocoloME protocolo : protocolos) {
            Long protocoloId = protocolo.getId();
            if (protocoloId == null) {
                continue;
            }

            estatisticaProtocoloMERepository.deleteByProtocoloMEId(protocoloId);
            anexoDocumentoRepository.deleteByProtocoloMEId(protocoloId);

            List<Long> idsExames = protocolo.getExames() != null
                ? protocolo.getExames().stream()
                    .map(ExameME::getId)
                    .filter(java.util.Objects::nonNull)
                    .collect(Collectors.toList())
                : new ArrayList<>();

            if (!idsExames.isEmpty()) {
                anexoDocumentoRepository.deleteByExameMEIdIn(idsExames);
            }
        }

        pacienteRepository.delete(paciente);
    }

    /**
     * Obter estatísticas de pacientes por status
     */
    public PacienteStatisticas obterEstatisticas() {
        PacienteStatisticas stats = new PacienteStatisticas();
        stats.setTotalPacientes(pacienteRepository.count());
        stats.setPacientesInternados(pacienteRepository.countByStatus(Paciente.StatusPaciente.INTERNADO));
        stats.setPacientesEmProtocoloME(pacienteRepository.countByStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME));
        stats.setPacientesAptosTransplante(pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE));
        stats.setPacientesNaoAptos(pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO));
        return stats;
    }

    public RelatorioFinalPaciente gerarRelatorioFinalPaciente(Long pacienteId) {
        Paciente paciente = obterPacientePorId(pacienteId);
        RelatorioFinalPaciente relatorio = new RelatorioFinalPaciente();

        relatorio.setGeradoEm(LocalDateTime.now());
        relatorio.setPacienteId(paciente.getId());
        relatorio.setNomePaciente(paciente.getNome());
        relatorio.setCpf(paciente.getCpf());
        relatorio.setHospital(paciente.getHospital() != null ? paciente.getHospital().getNome() : null);
        relatorio.setStatusPaciente(paciente.getStatus() != null ? paciente.getStatus().name() : null);
        relatorio.setStatusEntrevistaFamiliar(paciente.getStatusEntrevistaFamiliar());

        List<ProtocoloME> protocolosPaciente = paciente.getProtocolosME() != null
            ? new ArrayList<>(paciente.getProtocolosME())
            : new ArrayList<>();

        protocolosPaciente.sort(
            Comparator.comparing(ProtocoloME::getDataNotificacao, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(ProtocoloME::getDataCriacao, Comparator.nullsLast(Comparator.naturalOrder()))
        );

        List<ResumoProtocoloRelatorio> resumos = new ArrayList<>();
        for (ProtocoloME protocolo : protocolosPaciente) {
            ResumoProtocoloRelatorio resumo = new ResumoProtocoloRelatorio();
            resumo.setProtocoloId(protocolo.getId());
            resumo.setNumeroProtocolo(protocolo.getNumeroProtocolo());
            resumo.setStatusProtocolo(protocolo.getStatus() != null ? protocolo.getStatus().name() : null);
            resumo.setDataNotificacao(protocolo.getDataNotificacao());
            resumo.setDataConfirmacaoME(protocolo.getDataConfirmacaoME());
            resumo.setDiagnosticoBasico(protocolo.getDiagnosticoBasico());
            resumo.setHospitalOrigem(protocolo.getHospitalOrigem());

            List<ExameME> exames = protocolo.getExames() != null ? protocolo.getExames() : new ArrayList<>();
            int totalExames = exames.size();
            int examesRealizados = (int) exames.stream().filter(this::exameRealizado).count();
            int examesClinicosRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO && exameRealizado(e))
                .count();
            int examesComplementaresRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR && exameRealizado(e))
                .count();
            int examesLaboratoriaisRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL && exameRealizado(e))
                .count();

            resumo.setTotalExames(totalExames);
            resumo.setExamesRealizados(examesRealizados);
            resumo.setExamesPendentes(Math.max(0, totalExames - examesRealizados));
            resumo.setExamesClinicosRealizados(examesClinicosRealizados);
            resumo.setExamesComplementaresRealizados(examesComplementaresRealizados);
            resumo.setExamesLaboratoriaisRealizados(examesLaboratoriaisRealizados);

            resumo.setFamiliaNotificada(Boolean.TRUE.equals(protocolo.getFamiliaNotificada()));
            resumo.setAutopsiaAutorizada(Boolean.TRUE.equals(protocolo.getAutopsiaAutorizada()));
            resumo.setDataNotificacaoFamilia(protocolo.getDataNotificacaoFamilia());
            resumo.setRelatorioFinalEditavel(protocolo.getRelatorioFinalEditavel());

            List<AnexoResumoRelatorio> anexosResumo = new ArrayList<>();
            List<AnexoDocumento> anexosEntrevista = anexoDocumentoRepository.findByProtocoloMEId(protocolo.getId());
            List<Long> idsExames = exames.stream()
                .map(ExameME::getId)
                .collect(java.util.stream.Collectors.toList());

            List<AnexoDocumento> anexosExame = idsExames.isEmpty()
                ? new ArrayList<>()
                : anexoDocumentoRepository.findByExameMEIdIn(idsExames);

            Map<Long, AnexoDocumento> anexosUnicos = new LinkedHashMap<>();
            for (AnexoDocumento anexo : anexosEntrevista) {
                anexosUnicos.put(anexo.getId(), anexo);
            }
            for (AnexoDocumento anexo : anexosExame) {
                anexosUnicos.put(anexo.getId(), anexo);
            }

            anexosUnicos.values().forEach(anexo -> {
                AnexoResumoRelatorio item = new AnexoResumoRelatorio();
                item.setId(anexo.getId());
                item.setNomeArquivo(anexo.getNomeArquivo());
                item.setTipoAnexo(anexo.getTipoAnexo());
                item.setDescricao(anexo.getDescricao());
                item.setDataUpload(anexo.getDataUpload());
                anexosResumo.add(item);
            });
            resumo.setAnexos(anexosResumo);

            resumos.add(resumo);
        }

        relatorio.setTotalProtocolos(resumos.size());
        relatorio.setProtocolos(resumos);

        if (!resumos.isEmpty()) {
            ResumoProtocoloRelatorio ultimoProtocolo = resumos.get(resumos.size() - 1);
            relatorio.setStatusFinalProtocolo(ultimoProtocolo.getStatusProtocolo());
            if (ultimoProtocolo.getRelatorioFinalEditavel() != null && !ultimoProtocolo.getRelatorioFinalEditavel().trim().isEmpty()) {
                relatorio.setConclusaoFinal(ultimoProtocolo.getRelatorioFinalEditavel().trim());
            } else {
                relatorio.setConclusaoFinal(obterConclusaoFinal(ultimoProtocolo.getStatusProtocolo()));
            }
        } else {
            relatorio.setStatusFinalProtocolo("SEM_PROTOCOLO");
            relatorio.setConclusaoFinal("Paciente sem protocolo de ME registrado.");
        }

        return relatorio;
    }

    public List<RelatorioFinalPaciente> gerarRelatoriosFinaisPacientes() {
        List<Paciente> pacientes = listarTodos();
        List<RelatorioFinalPaciente> relatorios = new ArrayList<>();

        for (Paciente paciente : pacientes) {
            relatorios.add(gerarRelatorioFinalPaciente(paciente.getId()));
        }

        return relatorios;
    }

    private boolean exameRealizado(ExameME exame) {
        if (exame == null) {
            return false;
        }

        boolean temResultadoTexto = exame.getResultado() != null && !exame.getResultado().trim().isEmpty();
        boolean temResultadoBooleano = exame.getResultado_positivo() != null;
        boolean temDataRealizacao = exame.getDataRealizacao() != null;

        return temResultadoTexto || temResultadoBooleano || temDataRealizacao;
    }

    private String obterConclusaoFinal(String statusProtocolo) {
        if (statusProtocolo == null) {
            return "Status final não identificado.";
        }

        switch (statusProtocolo) {
            case "DOACAO_AUTORIZADA":
                return "Protocolo concluído com autorização familiar para doação.";
            case "FAMILIA_RECUSOU":
                return "Protocolo concluído com recusa familiar.";
            case "MORTE_CEREBRAL_CONFIRMADA":
                return "Morte cerebral confirmada; pendente etapa de entrevista familiar.";
            case "ENTREVISTA_FAMILIAR":
                return "Protocolo na etapa de entrevista familiar.";
            case "EM_PROCESSO":
                return "Protocolo em andamento, com exames ainda pendentes.";
            case "NOTIFICADO":
                return "Protocolo notificado, aguardando início dos exames.";
            case "CONTRAINDICADO":
                return "Protocolo finalizado por contraindicação para doação.";
            case "FINALIZADO":
                return "Protocolo finalizado.";
            default:
                return "Status final: " + statusProtocolo;
        }
    }

    /**
     * Validar dados do paciente
     */
    private void validarPaciente(Paciente paciente) {
        if (paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do paciente é obrigatório");
        }
        
        paciente.setCpf(normalizarCpf(paciente.getCpf()));
        
        if (paciente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }
        
        if (paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        
        if (paciente.getGenero() == null) {
            throw new IllegalArgumentException("Gênero é obrigatório");
        }
        
        if (paciente.getHospital() == null || paciente.getHospital().getId() == null) {
            throw new IllegalArgumentException("Hospital é obrigatório");
        }
        
        // Validar se CPF já existe (exceto se for update do próprio paciente)
        Optional<Paciente> existente = pacienteRepository.findAll().stream()
                .filter(item -> item.getCpf() != null)
                .filter(item -> normalizarCpf(item.getCpf()).equals(normalizarCpf(paciente.getCpf())))
                .findFirst();

        if (existente.isPresent() && !existente.get().getId().equals(paciente.getId())) {
            throw new IllegalArgumentException("CPF já está registrado no sistema");
        }
    }

    private String normalizarCpf(String cpf) {
        if (cpf == null) {
            throw new IllegalArgumentException("CPF do paciente é obrigatório");
        }

        String apenasNumeros = cpf.replaceAll("\\D", "");
        if (apenasNumeros.length() != 11) {
            throw new IllegalArgumentException("CPF deve conter 11 dígitos");
        }

        return apenasNumeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    private void preencherHospitalOrigemSeNecessario(Paciente paciente) {
        if (paciente == null) {
            return;
        }

        boolean hospitalOrigemVazio = paciente.getHospitalOrigem() == null || paciente.getHospitalOrigem().trim().isEmpty();
        if (hospitalOrigemVazio && paciente.getHospital() != null) {
            Long hospitalId = paciente.getHospital().getId();
            if (hospitalId != null) {
                Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado com ID: " + hospitalId));
                paciente.setHospitalOrigem(hospital.getNome());
                paciente.setHospital(hospital);
            }
        }
    }

    // Inner class para estatísticas
    public static class PacienteStatisticas {
        private long totalPacientes;
        private long pacientesInternados;
        private long pacientesEmProtocoloME;
        private long pacientesAptosTransplante;
        private long pacientesNaoAptos;

        // Getters e Setters
        public long getTotalPacientes() { return totalPacientes; }
        public void setTotalPacientes(long total) { this.totalPacientes = total; }

        public long getPacientesInternados() { return pacientesInternados; }
        public void setPacientesInternados(long qt) { this.pacientesInternados = qt; }

        public long getPacientesEmProtocoloME() { return pacientesEmProtocoloME; }
        public void setPacientesEmProtocoloME(long qt) { this.pacientesEmProtocoloME = qt; }

        public long getPacientesAptosTransplante() { return pacientesAptosTransplante; }
        public void setPacientesAptosTransplante(long qt) { this.pacientesAptosTransplante = qt; }

        public long getPacientesNaoAptos() { return pacientesNaoAptos; }
        public void setPacientesNaoAptos(long qt) { this.pacientesNaoAptos = qt; }
    }

    public static class RelatorioFinalPaciente {
        private Long pacienteId;
        private String nomePaciente;
        private String cpf;
        private String hospital;
        private String statusPaciente;
        private String statusEntrevistaFamiliar;
        private LocalDateTime geradoEm;
        private int totalProtocolos;
        private String statusFinalProtocolo;
        private String conclusaoFinal;
        private List<ResumoProtocoloRelatorio> protocolos;

        public Long getPacienteId() { return pacienteId; }
        public void setPacienteId(Long pacienteId) { this.pacienteId = pacienteId; }

        public String getNomePaciente() { return nomePaciente; }
        public void setNomePaciente(String nomePaciente) { this.nomePaciente = nomePaciente; }

        public String getCpf() { return cpf; }
        public void setCpf(String cpf) { this.cpf = cpf; }

        public String getHospital() { return hospital; }
        public void setHospital(String hospital) { this.hospital = hospital; }

        public String getStatusPaciente() { return statusPaciente; }
        public void setStatusPaciente(String statusPaciente) { this.statusPaciente = statusPaciente; }

        public String getStatusEntrevistaFamiliar() { return statusEntrevistaFamiliar; }
        public void setStatusEntrevistaFamiliar(String statusEntrevistaFamiliar) { this.statusEntrevistaFamiliar = statusEntrevistaFamiliar; }

        public LocalDateTime getGeradoEm() { return geradoEm; }
        public void setGeradoEm(LocalDateTime geradoEm) { this.geradoEm = geradoEm; }

        public int getTotalProtocolos() { return totalProtocolos; }
        public void setTotalProtocolos(int totalProtocolos) { this.totalProtocolos = totalProtocolos; }

        public String getStatusFinalProtocolo() { return statusFinalProtocolo; }
        public void setStatusFinalProtocolo(String statusFinalProtocolo) { this.statusFinalProtocolo = statusFinalProtocolo; }

        public String getConclusaoFinal() { return conclusaoFinal; }
        public void setConclusaoFinal(String conclusaoFinal) { this.conclusaoFinal = conclusaoFinal; }

        public List<ResumoProtocoloRelatorio> getProtocolos() { return protocolos; }
        public void setProtocolos(List<ResumoProtocoloRelatorio> protocolos) { this.protocolos = protocolos; }
    }

    public static class ResumoProtocoloRelatorio {
        private Long protocoloId;
        private String numeroProtocolo;
        private String statusProtocolo;
        private LocalDateTime dataNotificacao;
        private LocalDateTime dataConfirmacaoME;
        private String diagnosticoBasico;
        private String hospitalOrigem;
        private int totalExames;
        private int examesRealizados;
        private int examesPendentes;
        private int examesClinicosRealizados;
        private int examesComplementaresRealizados;
        private int examesLaboratoriaisRealizados;
        private boolean familiaNotificada;
        private boolean autopsiaAutorizada;
        private LocalDateTime dataNotificacaoFamilia;
        private String relatorioFinalEditavel;
        private List<AnexoResumoRelatorio> anexos;

        public Long getProtocoloId() { return protocoloId; }
        public void setProtocoloId(Long protocoloId) { this.protocoloId = protocoloId; }

        public String getNumeroProtocolo() { return numeroProtocolo; }
        public void setNumeroProtocolo(String numeroProtocolo) { this.numeroProtocolo = numeroProtocolo; }

        public String getStatusProtocolo() { return statusProtocolo; }
        public void setStatusProtocolo(String statusProtocolo) { this.statusProtocolo = statusProtocolo; }

        public LocalDateTime getDataNotificacao() { return dataNotificacao; }
        public void setDataNotificacao(LocalDateTime dataNotificacao) { this.dataNotificacao = dataNotificacao; }

        public LocalDateTime getDataConfirmacaoME() { return dataConfirmacaoME; }
        public void setDataConfirmacaoME(LocalDateTime dataConfirmacaoME) { this.dataConfirmacaoME = dataConfirmacaoME; }

        public String getDiagnosticoBasico() { return diagnosticoBasico; }
        public void setDiagnosticoBasico(String diagnosticoBasico) { this.diagnosticoBasico = diagnosticoBasico; }

        public String getHospitalOrigem() { return hospitalOrigem; }
        public void setHospitalOrigem(String hospitalOrigem) { this.hospitalOrigem = hospitalOrigem; }

        public int getTotalExames() { return totalExames; }
        public void setTotalExames(int totalExames) { this.totalExames = totalExames; }

        public int getExamesRealizados() { return examesRealizados; }
        public void setExamesRealizados(int examesRealizados) { this.examesRealizados = examesRealizados; }

        public int getExamesPendentes() { return examesPendentes; }
        public void setExamesPendentes(int examesPendentes) { this.examesPendentes = examesPendentes; }

        public int getExamesClinicosRealizados() { return examesClinicosRealizados; }
        public void setExamesClinicosRealizados(int examesClinicosRealizados) { this.examesClinicosRealizados = examesClinicosRealizados; }

        public int getExamesComplementaresRealizados() { return examesComplementaresRealizados; }
        public void setExamesComplementaresRealizados(int examesComplementaresRealizados) { this.examesComplementaresRealizados = examesComplementaresRealizados; }

        public int getExamesLaboratoriaisRealizados() { return examesLaboratoriaisRealizados; }
        public void setExamesLaboratoriaisRealizados(int examesLaboratoriaisRealizados) { this.examesLaboratoriaisRealizados = examesLaboratoriaisRealizados; }

        public boolean isFamiliaNotificada() { return familiaNotificada; }
        public void setFamiliaNotificada(boolean familiaNotificada) { this.familiaNotificada = familiaNotificada; }

        public boolean isAutopsiaAutorizada() { return autopsiaAutorizada; }
        public void setAutopsiaAutorizada(boolean autopsiaAutorizada) { this.autopsiaAutorizada = autopsiaAutorizada; }

        public LocalDateTime getDataNotificacaoFamilia() { return dataNotificacaoFamilia; }
        public void setDataNotificacaoFamilia(LocalDateTime dataNotificacaoFamilia) { this.dataNotificacaoFamilia = dataNotificacaoFamilia; }

        public String getRelatorioFinalEditavel() { return relatorioFinalEditavel; }
        public void setRelatorioFinalEditavel(String relatorioFinalEditavel) { this.relatorioFinalEditavel = relatorioFinalEditavel; }

        public List<AnexoResumoRelatorio> getAnexos() { return anexos; }
        public void setAnexos(List<AnexoResumoRelatorio> anexos) { this.anexos = anexos; }
    }

    public static class AnexoResumoRelatorio {
        private Long id;
        private String nomeArquivo;
        private String tipoAnexo;
        private String descricao;
        private LocalDateTime dataUpload;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getNomeArquivo() { return nomeArquivo; }
        public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }

        public String getTipoAnexo() { return tipoAnexo; }
        public void setTipoAnexo(String tipoAnexo) { this.tipoAnexo = tipoAnexo; }

        public String getDescricao() { return descricao; }
        public void setDescricao(String descricao) { this.descricao = descricao; }

        public LocalDateTime getDataUpload() { return dataUpload; }
        public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }
    }

}
