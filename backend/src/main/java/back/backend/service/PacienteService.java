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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

        Optional<Paciente> existente =
                pacienteRepository.findByCpf(paciente.getCpf());

        if (existente.isPresent() &&
                !existente.get().getId().equals(paciente.getId())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }

    private Paciente buscarPacienteEntityPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() ->
                        new RecursoNaoEncontradoException("Paciente não encontrado: " + id));
    }

    private PacienteDTO toDTO(Paciente paciente) {
        return pacienteMapper.toDTO(paciente);
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
}
