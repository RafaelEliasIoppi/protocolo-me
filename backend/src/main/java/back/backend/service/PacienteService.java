package back.backend.service;

import back.backend.dto.PacienteDTO;
import back.backend.dto.PacienteEmProtocoloDTO;
import back.backend.dto.PacienteRelatorioFinalDTO;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.PacienteMapper;
import back.backend.model.*;
import back.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private PacienteMapper pacienteMapper;

    // ================= CREATE =================

    public PacienteDTO criarPaciente(Paciente paciente) {

        paciente.setCpf(normalizarCpf(paciente.getCpf()));

        if (paciente.getStatus() == null ||
            paciente.getStatus() == Paciente.StatusPaciente.EM_PROTOCOLO_ME) {
            paciente.setStatus(Paciente.StatusPaciente.INTERNADO);
        }

        preencherHospital(paciente);
        validarPaciente(paciente);

        return toDTO(pacienteRepository.save(paciente));
    }

    // ================= UPDATE =================

    public PacienteDTO atualizarPaciente(Long id, Paciente atualizado) {

        Paciente paciente = buscarPacienteEntityPorId(id);

        if (atualizado.getCpf() != null) {
            paciente.setCpf(normalizarCpf(atualizado.getCpf()));
        }

        if (atualizado.getHospital() != null &&
            atualizado.getHospital().getId() != null) {

            Hospital hospital = hospitalRepository.findById(
                    atualizado.getHospital().getId()
            ).orElseThrow(() ->
                    new RecursoNaoEncontradoException("Hospital não encontrado"));

            paciente.setHospital(hospital);
        }

        copiarCampos(paciente, atualizado);
        preencherHospital(paciente);

        return toDTO(pacienteRepository.save(paciente));
    }

    // ================= STATUS =================

    public PacienteDTO atualizarStatus(Long id, String novoStatus) {
        try {
            Paciente.StatusPaciente status =
                    Paciente.StatusPaciente.valueOf(novoStatus.toUpperCase());

            return atualizarStatus(id, status);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
    }

    public PacienteDTO atualizarStatus(Long id, Paciente.StatusPaciente status) {
        Paciente paciente = buscarPacienteEntityPorId(id);
        paciente.setStatus(status);
        return toDTO(pacienteRepository.save(paciente));
    }

    // ================= GET =================

    public PacienteDTO obterPacientePorId(Long id) {
        return toDTO(buscarPacienteEntityPorId(id));
    }

    public PacienteDTO obterPacientePorCpf(String cpf) {
        return toDTO(pacienteRepository.findByCpf(normalizarCpf(cpf))
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Paciente não encontrado")));
    }

    public List<PacienteDTO> listarTodos() {
        return pacienteRepository.findAll().stream().map(this::toDTO).toList();
    }

    public List<PacienteDTO> buscarPorNome(String nome) {
        if (nome == null || nome.isBlank()) {
            return listarTodos();
        }

        return pacienteRepository.findByNomeContainingIgnoreCase(nome.trim())
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PacienteDTO> listarPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Hospital não encontrado"));

        return pacienteRepository.findByHospital(hospital).stream().map(this::toDTO).toList();
    }

    public List<PacienteDTO> listarPorStatus(String status) {
        try {
            Paciente.StatusPaciente statusEnum =
                    Paciente.StatusPaciente.valueOf(status.toUpperCase());

            return pacienteRepository.findByStatus(statusEnum).stream().map(this::toDTO).toList();

        } catch (Exception e) {
            throw new IllegalArgumentException("Status inválido");
        }
    }

    public List<PacienteDTO> listarPorHospitalEStatus(Long hospitalId, String status) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Hospital não encontrado"));

        try {
            Paciente.StatusPaciente statusEnum =
                    Paciente.StatusPaciente.valueOf(status.toUpperCase());

            return pacienteRepository.findByHospitalAndStatus(hospital, statusEnum).stream().map(this::toDTO).toList();

        } catch (Exception e) {
            throw new IllegalArgumentException("Status inválido");
        }
    }

    public List<PacienteEmProtocoloDTO> listarEmProtocoloME() {
        return pacienteRepository.findPacientesEmProtocoloME().stream()
                .map(this::toProtocoloViewDTO)
                .toList();
    }

    public List<PacienteEmProtocoloDTO> listarEmProtocoloMEPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));

        return pacienteRepository.findPacientesEmProtocoloMEPorHospital(hospital).stream()
                .map(this::toProtocoloViewDTO)
                .toList();
    }

    public PacienteRelatorioFinalDTO obterRelatorioFinal(Long pacienteId) {
        Paciente paciente = buscarPacienteEntityPorId(pacienteId);

        List<ProtocoloME> protocolos = Optional.ofNullable(paciente.getProtocolosME())
                .orElse(List.of())
                .stream()
                .sorted(Comparator.comparing(ProtocoloME::getDataNotificacao,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        PacienteRelatorioFinalDTO dto = new PacienteRelatorioFinalDTO();
        dto.setPacienteId(paciente.getId());
        dto.setNomePaciente(paciente.getNome());
        dto.setCpf(paciente.getCpf());
        dto.setHospital(paciente.getHospital() != null ? paciente.getHospital().getNome() : null);
        dto.setStatusPaciente(paciente.getStatus() != null ? paciente.getStatus().name() : null);
        dto.setStatusEntrevistaFamiliar(paciente.getStatusEntrevistaFamiliar());
        dto.setTotalProtocolos(protocolos.size());

        for (ProtocoloME protocolo : protocolos) {
            dto.getProtocolos().add(toRelatorioResumo(protocolo));
        }

        if (!protocolos.isEmpty()) {
            ProtocoloME principal = protocolos.get(0);
            dto.setStatusFinalProtocolo(principal.getStatus() != null ? principal.getStatus().name() : null);
            dto.setConclusaoFinal(
                    principal.getRelatorioFinalEditavel() != null && !principal.getRelatorioFinalEditavel().isBlank()
                            ? principal.getRelatorioFinalEditavel()
                            : "Sem conclusão preenchida"
            );
        }

        return dto;
    }

    // ================= DELETE =================

    public void deletarPaciente(Long id) {

        Paciente paciente = buscarPacienteEntityPorId(id);

        List<ProtocoloME> protocolos =
                Optional.ofNullable(paciente.getProtocolosME())
                        .orElse(new ArrayList<>());

        for (ProtocoloME protocolo : protocolos) {

            Long protocoloId = protocolo.getId();
            if (protocoloId == null) continue;

            estatisticaProtocoloMERepository.deleteByProtocoloMEId(protocoloId);
            anexoDocumentoRepository.deleteByProtocoloMEId(protocoloId);

            List<Long> idsExames = Optional.ofNullable(protocolo.getExames())
                    .orElse(new ArrayList<>())
                    .stream()
                    .map(ExameME::getId)
                    .filter(Objects::nonNull)
                    .toList();

            if (!idsExames.isEmpty()) {
                anexoDocumentoRepository.deleteByExameMEIdIn(idsExames);
            }
        }

        pacienteRepository.delete(paciente);
    }

    // ================= STATS =================

    public PacienteStatisticas obterEstatisticas() {

        PacienteStatisticas stats = new PacienteStatisticas();

        stats.setTotalPacientes(pacienteRepository.count());
        stats.setPacientesInternados(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.INTERNADO));
        stats.setPacientesEmProtocoloME(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME));
        stats.setPacientesAptosTransplante(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE));
        stats.setPacientesNaoAptos(
                pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO));

        return stats;
    }

    // ================= HELPERS =================

    private void validarPaciente(Paciente paciente) {

        if (paciente.getNome() == null || paciente.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome obrigatório");
        }

        if (paciente.getDataNascimento() == null ||
                paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento inválida");
        }

        if (paciente.getGenero() == null) {
            throw new IllegalArgumentException("Gênero obrigatório");
        }

        if (paciente.getHospital() == null ||
                paciente.getHospital().getId() == null) {
            throw new IllegalArgumentException("Hospital obrigatório");
        }

        // valida CPF duplicado
        Optional<Paciente> existente =
                pacienteRepository.findByCpf(paciente.getCpf());

        if (existente.isPresent() &&
                !existente.get().getId().equals(paciente.getId())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }

    private void preencherHospital(Paciente paciente) {

        if (paciente.getHospital() == null ||
            paciente.getHospital().getId() == null) return;

        Hospital hospital = hospitalRepository.findById(
                paciente.getHospital().getId()
        ).orElseThrow(() ->
                new RecursoNaoEncontradoException("Hospital não encontrado"));

        paciente.setHospital(hospital);

        if (paciente.getHospitalOrigem() == null ||
            paciente.getHospitalOrigem().isBlank()) {

            paciente.setHospitalOrigem(hospital.getNome());
        }
    }

    private void copiarCampos(Paciente dest, Paciente orig) {

        if (orig.getNome() != null) dest.setNome(orig.getNome());
        if (orig.getDataNascimento() != null) dest.setDataNascimento(orig.getDataNascimento());
        if (orig.getGenero() != null) dest.setGenero(orig.getGenero());
        if (orig.getLeito() != null) dest.setLeito(orig.getLeito());
        if (orig.getDiagnosticoPrincipal() != null) dest.setDiagnosticoPrincipal(orig.getDiagnosticoPrincipal());
        if (orig.getHistoricoMedico() != null) dest.setHistoricoMedico(orig.getHistoricoMedico());
        if (orig.getNomeResponsavel() != null) dest.setNomeResponsavel(orig.getNomeResponsavel());
        if (orig.getTelefoneResponsavel() != null) dest.setTelefoneResponsavel(orig.getTelefoneResponsavel());
        if (orig.getEmailResponsavel() != null) dest.setEmailResponsavel(orig.getEmailResponsavel());
        if (orig.getStatusEntrevistaFamiliar() != null) dest.setStatusEntrevistaFamiliar(orig.getStatusEntrevistaFamiliar());
        if (orig.getObservacoesEntrevistaFamiliar() != null) dest.setObservacoesEntrevistaFamiliar(orig.getObservacoesEntrevistaFamiliar());
        if (orig.getDataEntrevistaFamiliar() != null) dest.setDataEntrevistaFamiliar(orig.getDataEntrevistaFamiliar());
        if (orig.getStatus() != null) dest.setStatus(orig.getStatus());
    }

    private Paciente buscarPacienteEntityPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Paciente não encontrado: " + id));
    }

    private PacienteDTO toDTO(Paciente paciente) {
        return pacienteMapper.toDTO(paciente);
    }

    private PacienteEmProtocoloDTO toProtocoloViewDTO(Paciente paciente) {
        PacienteEmProtocoloDTO dto = new PacienteEmProtocoloDTO();
        dto.setId(paciente.getId());
        dto.setNome(paciente.getNome());
        dto.setCpf(paciente.getCpf());
        dto.setDataNascimento(paciente.getDataNascimento());
        dto.setGenero(paciente.getGenero() != null ? paciente.getGenero().name() : null);
        dto.setLeito(paciente.getLeito());
        dto.setDataInternacao(paciente.getDataInternacao());
        dto.setStatus(paciente.getStatus() != null ? paciente.getStatus().name() : null);
        dto.setStatusEntrevistaFamiliar(paciente.getStatusEntrevistaFamiliar());
        dto.setDiagnosticoPrincipal(paciente.getDiagnosticoPrincipal());

        if (paciente.getHospital() != null) {
            dto.setHospital(new PacienteEmProtocoloDTO.HospitalResumoDTO(
                    paciente.getHospital().getId(),
                    paciente.getHospital().getNome()
            ));
        }

        List<PacienteEmProtocoloDTO.ProtocoloResumoDTO> protocolos = Optional.ofNullable(paciente.getProtocolosME())
                .orElse(List.of())
                .stream()
                .map(this::toProtocoloResumo)
                .toList();
        dto.setProtocolosME(protocolos);

        return dto;
    }

    private PacienteEmProtocoloDTO.ProtocoloResumoDTO toProtocoloResumo(ProtocoloME protocolo) {
        PacienteEmProtocoloDTO.ProtocoloResumoDTO dto = new PacienteEmProtocoloDTO.ProtocoloResumoDTO();
        dto.setId(protocolo.getId());
        dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
        dto.setStatus(protocolo.getStatus() != null ? protocolo.getStatus().name() : null);
        dto.setHospitalOrigem(protocolo.getHospitalOrigem());
        dto.setDiagnosticoBasico(protocolo.getDiagnosticoBasico());
        dto.setCausaMorte(protocolo.getCausaMorte());
        dto.setObservacoes(protocolo.getObservacoes());
        dto.setMedicoResponsavel(protocolo.getMedicoResponsavel());
        dto.setEnfermeiro(protocolo.getEnfermeiro());
        dto.setOrgaosDisponiveis(protocolo.getOrgaosDisponiveis());
        return dto;
    }

    private PacienteRelatorioFinalDTO.ProtocoloRelatorioResumoDTO toRelatorioResumo(ProtocoloME protocolo) {
        PacienteRelatorioFinalDTO.ProtocoloRelatorioResumoDTO dto =
                new PacienteRelatorioFinalDTO.ProtocoloRelatorioResumoDTO();

        List<ExameME> exames = Optional.ofNullable(protocolo.getExames()).orElse(List.of());
        int totalExames = exames.size();
        int examesRealizados = (int) exames.stream()
                .filter(e -> e.getDataRealizacao() != null || e.getResultado() != null)
                .count();

        dto.setProtocoloId(protocolo.getId());
        dto.setNumeroProtocolo(protocolo.getNumeroProtocolo());
        dto.setStatusProtocolo(protocolo.getStatus() != null ? protocolo.getStatus().name() : null);
        dto.setDataNotificacao(formatDateTime(protocolo.getDataNotificacao()));
        dto.setDataConfirmacaoME(formatDateTime(protocolo.getDataConfirmacaoME()));
        dto.setTotalExames(totalExames);
        dto.setExamesRealizados(examesRealizados);
        dto.setExamesPendentes(Math.max(totalExames - examesRealizados, 0));
        dto.setExamesClinicosRealizados(contarExamesRealizados(exames, ExameME.CategoriaExame.CLINICO));
        dto.setExamesComplementaresRealizados(contarExamesRealizados(exames, ExameME.CategoriaExame.COMPLEMENTAR));
        dto.setExamesLaboratoriaisRealizados(contarExamesRealizados(exames, ExameME.CategoriaExame.LABORATORIAL));
        dto.setFamiliaNotificada(Boolean.TRUE.equals(protocolo.getFamiliaNotificada()));
        dto.setAutopsiaAutorizada(Boolean.TRUE.equals(protocolo.getAutopsiaAutorizada()));
        dto.setRelatorioFinalEditavel(protocolo.getRelatorioFinalEditavel());
        dto.setAnexos(
                anexoDocumentoRepository.findByProtocoloMEId(protocolo.getId())
                        .stream()
                        .map(AnexoDocumento::getNomeArquivo)
                        .toList()
        );

        return dto;
    }

    private int contarExamesRealizados(List<ExameME> exames, ExameME.CategoriaExame categoria) {
        return (int) exames.stream()
                .filter(e -> e.getCategoria() == categoria)
                .filter(e -> e.getDataRealizacao() != null || e.getResultado() != null)
                .count();
    }

    private String formatDateTime(LocalDateTime data) {
        if (data == null) {
            return null;
        }
        return data.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    private String normalizarCpf(String cpf) {

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException("CPF obrigatório");
        }

        String n = cpf.replaceAll("\\D", "");

        if (n.length() != 11) {
            throw new IllegalArgumentException("CPF inválido");
        }

        return n.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})",
                "$1.$2.$3-$4");
    }

    // ================= DTO INTERNO =================

    public static class PacienteStatisticas {
        private long totalPacientes;
        private long pacientesInternados;
        private long pacientesEmProtocoloME;
        private long pacientesAptosTransplante;
        private long pacientesNaoAptos;

        public long getTotalPacientes() { return totalPacientes; }
        public void setTotalPacientes(long v) { this.totalPacientes = v; }

        public long getPacientesInternados() { return pacientesInternados; }
        public void setPacientesInternados(long v) { this.pacientesInternados = v; }

        public long getPacientesEmProtocoloME() { return pacientesEmProtocoloME; }
        public void setPacientesEmProtocoloME(long v) { this.pacientesEmProtocoloME = v; }

        public long getPacientesAptosTransplante() { return pacientesAptosTransplante; }
        public void setPacientesAptosTransplante(long v) { this.pacientesAptosTransplante = v; }

        public long getPacientesNaoAptos() { return pacientesNaoAptos; }
        public void setPacientesNaoAptos(long v) { this.pacientesNaoAptos = v; }
    }
}