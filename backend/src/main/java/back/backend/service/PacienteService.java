package back.backend.service;

import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.*;
import back.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // ================= CREATE =================

    public Paciente criarPaciente(Paciente paciente) {

        paciente.setCpf(normalizarCpf(paciente.getCpf()));

        if (paciente.getStatus() == null ||
            paciente.getStatus() == Paciente.StatusPaciente.EM_PROTOCOLO_ME) {
            paciente.setStatus(Paciente.StatusPaciente.INTERNADO);
        }

        preencherHospital(paciente);
        validarPaciente(paciente);

        return pacienteRepository.save(paciente);
    }

    // ================= UPDATE =================

    public Paciente atualizarPaciente(Long id, Paciente atualizado) {

        Paciente paciente = obterPacientePorId(id);

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

        return pacienteRepository.save(paciente);
    }

    // ================= STATUS =================

    public Paciente atualizarStatus(Long id, String novoStatus) {
        try {
            Paciente.StatusPaciente status =
                    Paciente.StatusPaciente.valueOf(novoStatus.toUpperCase());

            return atualizarStatus(id, status);

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + novoStatus);
        }
    }

    public Paciente atualizarStatus(Long id, Paciente.StatusPaciente status) {
        Paciente paciente = obterPacientePorId(id);
        paciente.setStatus(status);
        return pacienteRepository.save(paciente);
    }

    // ================= GET =================

    public Paciente obterPacientePorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Paciente não encontrado: " + id));
    }

    public Paciente obterPacientePorCpf(String cpf) {
        return pacienteRepository.findByCpf(normalizarCpf(cpf))
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Paciente não encontrado"));
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public List<Paciente> listarPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Hospital não encontrado"));

        return pacienteRepository.findByHospital(hospital);
    }

    public List<Paciente> listarPorStatus(String status) {
        try {
            Paciente.StatusPaciente statusEnum =
                    Paciente.StatusPaciente.valueOf(status.toUpperCase());

            return pacienteRepository.findByStatus(statusEnum);

        } catch (Exception e) {
            throw new IllegalArgumentException("Status inválido");
        }
    }

    public List<Paciente> listarPorHospitalEStatus(Long hospitalId, String status) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Hospital não encontrado"));

        try {
            Paciente.StatusPaciente statusEnum =
                    Paciente.StatusPaciente.valueOf(status.toUpperCase());

            return pacienteRepository.findByHospitalAndStatus(hospital, statusEnum);

        } catch (Exception e) {
            throw new IllegalArgumentException("Status inválido");
        }
    }

    // ================= DELETE =================

    public void deletarPaciente(Long id) {

        Paciente paciente = obterPacientePorId(id);

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