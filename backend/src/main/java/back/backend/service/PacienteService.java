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
                    atualizado.getHospital().getId())
                    .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));

            paciente.setHospital(hospital);
        }

        copiarCampos(paciente, atualizado);
        preencherHospital(paciente);

        return toDTO(pacienteRepository.save(paciente));
    }

    // ================= STATUS =================

    public PacienteDTO atualizarStatus(Long id, String novoStatus) {
        try {
            Paciente.StatusPaciente status = Paciente.StatusPaciente.valueOf(novoStatus.toUpperCase());

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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado")));
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
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));
        return pacienteRepository.findByHospital(hospital)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<PacienteDTO> listarPorStatus(String status) {
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            return pacienteRepository.findByStatus(statusEnum)
                    .stream()
                    .map(this::toDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    public List<PacienteEmProtocoloDTO> listarPorStatusSemProtocoloAtivo(String status) {
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            return pacienteRepository.findByStatus(statusEnum)
                    .stream()
                    .filter(this::naoPossuiProtocoloAtivo)
                    .map(this::toPacienteEmProtocoloDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    public List<PacienteDTO> listarPorHospitalEStatus(Long hospitalId, String status) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));
        try {
            Paciente.StatusPaciente statusEnum = Paciente.StatusPaciente.valueOf(status.toUpperCase());
            return pacienteRepository.findByHospitalAndStatus(hospital, statusEnum)
                    .stream()
                    .map(this::toDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }

    public List<PacienteEmProtocoloDTO> listarEmProtocoloME() {
        return pacienteRepository.findPacientesEmProtocoloME()
                .stream()
                .map(this::toPacienteEmProtocoloDTO)
                .toList();
    }

    public List<PacienteEmProtocoloDTO> listarEmProtocoloMEPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));
        return pacienteRepository.findPacientesEmProtocoloMEPorHospital(hospital)
                .stream()
                .map(this::toPacienteEmProtocoloDTO)
                .toList();
    }

    public PacienteRelatorioFinalDTO obterRelatorioFinal(Long id) {
        Paciente paciente = buscarPacienteEntityPorId(id);
        PacienteRelatorioFinalDTO dto = new PacienteRelatorioFinalDTO();
        dto.setPacienteId(paciente.getId());
        dto.setNomePaciente(paciente.getNome());
        dto.setCpf(paciente.getCpf());
        dto.setHospital(Optional.ofNullable(paciente.getHospital()).map(Hospital::getNome).orElse(null));
        dto.setStatusPaciente(Optional.ofNullable(paciente.getStatus()).map(Enum::name).orElse(null));
        dto.setStatusEntrevistaFamiliar(paciente.getStatusEntrevistaFamiliar());
        return dto;
    }

    public PacienteStatisticas obterEstatisticas() {
        long total = pacienteRepository.count();
        long internados = pacienteRepository.countByStatus(Paciente.StatusPaciente.INTERNADO);
        long emProtocolo = pacienteRepository.countByStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME);
        long aptos = pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE);
        long naoAptos = pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO);

        return new PacienteStatisticas(total, internados, emProtocolo, aptos, naoAptos);
    }

    // ================= DELETE =================

    public void deletarPaciente(Long id) {

        Paciente paciente = buscarPacienteEntityPorId(id);

        List<ProtocoloME> protocolos = Optional.ofNullable(paciente.getProtocolosME())
                .orElse(new ArrayList<>());

        for (ProtocoloME protocolo : protocolos) {

            Long protocoloId = protocolo.getId();
            if (protocoloId == null)
                continue;

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

    private PacienteEmProtocoloDTO toPacienteEmProtocoloDTO(Paciente paciente) {
        PacienteEmProtocoloDTO dto = new PacienteEmProtocoloDTO();
        dto.setId(paciente.getId());
        dto.setNome(paciente.getNome());
        dto.setCpf(paciente.getCpf());
        dto.setLeito(paciente.getLeito());
        dto.setStatus(Optional.ofNullable(paciente.getStatus()).map(Enum::name).orElse(null));
        dto.setStatusEntrevistaFamiliar(paciente.getStatusEntrevistaFamiliar());

        Optional.ofNullable(paciente.getHospital())
                .ifPresent(h -> dto.setHospital(new PacienteEmProtocoloDTO.HospitalResumoDTO(h.getId(), h.getNome())));

        return dto;
    }

   private boolean naoPossuiProtocoloAtivo(Paciente paciente) {
    List<ProtocoloME> protocolos = Optional.ofNullable(paciente.getProtocolosME())
            .orElse(Collections.emptyList());

    return protocolos.stream().noneMatch(protocolo -> {
        if (protocolo == null || protocolo.getStatus() == null) {
            return false;
        }

        return switch (protocolo.getStatus()) {
            case NOTIFICADO,
                 EM_PROCESSO,
                 MORTE_CEREBRAL_CONFIRMADA,
                 ENTREVISTA_FAMILIAR ->
                true;

            // 🔥 ESTES NÃO SÃO ATIVOS
            case FINALIZADO,
                 FAMILIA_RECUSOU,
                 DOACAO_AUTORIZADA,
                 CONTRAINDICADO ->
                false;
        };
    });
    }

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

        Optional<Paciente> existente = pacienteRepository.findByCpf(paciente.getCpf());

        if (existente.isPresent() &&
                !existente.get().getId().equals(paciente.getId())) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }

    private void preencherHospital(Paciente paciente) {
        // Preenchimento automático se necessário
    }

    private void copiarCampos(Paciente destino, Paciente origem) {
        if (origem.getNome() != null)
            destino.setNome(origem.getNome());
        if (origem.getDataNascimento() != null)
            destino.setDataNascimento(origem.getDataNascimento());
        if (origem.getGenero() != null)
            destino.setGenero(origem.getGenero());
        if (origem.getLeito() != null)
            destino.setLeito(origem.getLeito());
        if (origem.getDataInternacao() != null)
            destino.setDataInternacao(origem.getDataInternacao());
        if (origem.getDiagnosticoPrincipal() != null)
            destino.setDiagnosticoPrincipal(origem.getDiagnosticoPrincipal());
        if (origem.getHistoricoMedico() != null)
            destino.setHistoricoMedico(origem.getHistoricoMedico());
        if (origem.getNomeResponsavel() != null)
            destino.setNomeResponsavel(origem.getNomeResponsavel());
        if (origem.getTelefoneResponsavel() != null)
            destino.setTelefoneResponsavel(origem.getTelefoneResponsavel());
        if (origem.getEmailResponsavel() != null)
            destino.setEmailResponsavel(origem.getEmailResponsavel());
        if (origem.getStatus() != null)
            destino.setStatus(origem.getStatus());
    }

    private Paciente buscarPacienteEntityPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado: " + id));
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

        return n;
    }

    // ================= INNER CLASS =================

    public static class PacienteStatisticas {
        public long total;
        public long internados;
        public long emProtocolo;
        public long aptos;
        public long naoAptos;

        public PacienteStatisticas(long total, long internados, long emProtocolo, long aptos, long naoAptos) {
            this.total = total;
            this.internados = internados;
            this.emProtocolo = emProtocolo;
            this.aptos = aptos;
            this.naoAptos = naoAptos;
        }
    }
}
