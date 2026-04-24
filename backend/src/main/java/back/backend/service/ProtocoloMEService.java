package back.backend.service;

import back.backend.dto.ProtocoloMEDTO;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.ProtocoloMapper;
import back.backend.model.*;
import back.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class ProtocoloMEService {

    @Autowired private ProtocoloMERepository protocoloRepository;
    @Autowired private CentralTransplantesRepository centralRepository;
    @Autowired private ExameMERepository exameRepository;
    @Autowired private PacienteRepository pacienteRepository;
    @Autowired private ProtocoloMapper protocoloMapper;

    // ================= HELPERS =================

    private ProtocoloME buscarOuFalhar(Long id) {
        return protocoloRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado: " + id));
    }

    private CentralTransplantes obterCentralPadrao() {
        return centralRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RecursoNaoEncontradoException("Nenhuma central cadastrada"));
    }

    private String gerarNumeroProtocolo() {
        String base = "ME-" + LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        String numero = base;
        int i = 1;

        while (protocoloRepository.findByNumeroProtocolo(numero).isPresent()) {
            numero = base + "-" + i++;
        }

        return numero;
    }

    private ProtocoloME salvar(ProtocoloME p) {
        ProtocoloME salvo = protocoloRepository.save(p);
        sincronizarStatusPaciente(salvo);
        return salvo;
    }

    // ================= CREATE =================

    public ProtocoloMEDTO criarProtocoloPorPacienteId(Long pacienteId, String diagnostico, String numero) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));

        ProtocoloME p = new ProtocoloME();
        p.setPaciente(paciente);
        p.setCentralTransplantes(obterCentralPadrao());
        p.setNumeroProtocolo(numero != null ? numero : gerarNumeroProtocolo());
        p.setDiagnosticoBasico(diagnostico);
        p.setStatus(ProtocoloME.StatusProtocoloME.NOTIFICADO);
        p.setDataNotificacao(LocalDateTime.now());

        p.setHospitalOrigem(
                paciente.getHospitalOrigem() != null
                        ? paciente.getHospitalOrigem()
                        : (paciente.getHospital() != null
                            ? paciente.getHospital().getNome()
                            : "Não informado")
        );

        ProtocoloME salvo = salvar(p);

        paciente.setStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME);
        pacienteRepository.save(paciente);

        return toDTO(salvo);
    }

    // ================= QUERY =================

    public List<ProtocoloMEDTO> listarTodos() {
        return protocoloRepository.findAllWithDetalhes().stream().map(this::toDTO).toList();
    }

    public Optional<ProtocoloMEDTO> buscarPorId(Long id) {
        return protocoloRepository.findByIdWithDetalhes(id).map(this::toDTO);
    }

    public ProtocoloMEDTO buscarPorIdOuFalhar(Long id) {
        return toDTO(protocoloRepository.findByIdWithDetalhes(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado: " + id)));
    }

    public Optional<ProtocoloMEDTO> buscarPorNumeroProtocolo(String numero) {
        return protocoloRepository.findByNumeroProtocolo(numero).map(this::toDTO);
    }

    public ProtocoloMEDTO buscarPorNumeroProtocoloOuFalhar(String numero) {
        return toDTO(protocoloRepository.findByNumeroProtocolo(numero)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado")));
    }

    public List<ProtocoloMEDTO> listarPorCentral(Long centralId) {
        CentralTransplantes c = centralRepository.findById(centralId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"));

        return protocoloRepository.findByCentralTransplantes(c).stream().map(this::toDTO).toList();
    }

    public List<ProtocoloMEDTO> listarPorStatus(ProtocoloME.StatusProtocoloME status) {
        return protocoloRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    public List<ProtocoloMEDTO> listarPorStatus(String status) {
        return listarPorStatus(parseStatus(status));
    }

    public List<ProtocoloMEDTO> listarPorCentralEStatus(Long id, ProtocoloME.StatusProtocoloME status) {
        CentralTransplantes c = centralRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"));

        return protocoloRepository.findByCentralTransplantesAndStatus(c, status).stream().map(this::toDTO).toList();
    }

    public List<ProtocoloMEDTO> listarPorCentralEStatus(Long id, String status) {
        return listarPorCentralEStatus(id, parseStatus(status));
    }

    public List<ProtocoloMEDTO> listarPorPeriodo(LocalDateTime ini, LocalDateTime fim) {
        return protocoloRepository.findByDataNotificacaoBetween(ini, fim).stream().map(this::toDTO).toList();
    }

    public List<ProtocoloMEDTO> listarPorHospitalOrigem(String hospital) {
        return protocoloRepository.findByHospitalOrigem(hospital).stream().map(this::toDTO).toList();
    }

    // ================= UPDATE =================

    public ProtocoloMEDTO atualizarProtocolo(Long id, ProtocoloME novo) {

        ProtocoloME p = buscarOuFalhar(id);

        if (novo.getNumeroProtocolo() != null &&
                protocoloRepository.existsByNumeroProtocoloAndIdNot(novo.getNumeroProtocolo(), id)) {
            throw new IllegalStateException("Número de protocolo já existe");
        }

        p.setNumeroProtocolo(novo.getNumeroProtocolo());
        p.setDiagnosticoBasico(novo.getDiagnosticoBasico());
        p.setCausaMorte(novo.getCausaMorte());
        p.setObservacoes(novo.getObservacoes());
        p.setMedicoResponsavel(novo.getMedicoResponsavel());
        p.setEnfermeiro(novo.getEnfermeiro());
        p.setOrgaosDisponiveis(novo.getOrgaosDisponiveis());

        return toDTO(salvar(p));
    }

    public ProtocoloMEDTO atualizarRelatorioFinal(Long id, String texto, String usuario) {
        ProtocoloME p = buscarOuFalhar(id);

        p.setRelatorioFinalEditavel(texto);
        p.setRelatorioFinalAtualizadoPor(usuario);
        p.setRelatorioFinalAtualizadoEm(LocalDateTime.now());

        return toDTO(salvar(p));
    }

    // ================= ACTIONS =================

    public ProtocoloMEDTO registrarTesteClinico1(Long id) {
        return executar(id, p -> {
            p.setTesteClinico1Realizado(true);
            p.setDataTesteClinico1(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO registrarTesteClinico2(Long id) {
        return executar(id, p -> {
            p.setTesteClinico2Realizado(true);
            p.setDataTesteClinico2(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO confirmarMorteCerebral(Long id) {
        return executar(id, p -> {
            p.setStatus(ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA);
            p.setDataConfirmacaoME(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO registrarNotificacaoFamilia(Long id) {
        return executar(id, p -> {
            p.setFamiliaNotificada(true);
            p.setDataNotificacaoFamilia(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO autorizarAutopsia(Long id) {
        return executar(id, p -> p.setAutopsiaAutorizada(true));
    }

    public ProtocoloMEDTO registrarPreservacaoOrgaos(Long id) {
        return executar(id, p -> {
            p.setPreservacaoOrgaos(true);
            p.setDataPreservacao(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO alterarStatus(Long id, ProtocoloME.StatusProtocoloME status) {
        return executar(id, p -> p.setStatus(status));
    }

    public ProtocoloMEDTO alterarStatus(Long id, String status) {
        return alterarStatus(id, parseStatus(status));
    }

    public ProtocoloMEDTO atualizarStatusAutomatico(Long id) {
        ProtocoloME protocolo = buscarOuFalhar(id);

        if (Boolean.TRUE.equals(protocolo.getAutopsiaAutorizada())) {
            protocolo.setStatus(ProtocoloME.StatusProtocoloME.DOACAO_AUTORIZADA);
        } else if (Boolean.TRUE.equals(protocolo.getFamiliaNotificada())) {
            protocolo.setStatus(ProtocoloME.StatusProtocoloME.EM_PROCESSO);
        } else {
            protocolo.setStatus(ProtocoloME.StatusProtocoloME.NOTIFICADO);
        }

        return toDTO(salvar(protocolo));
    }

    // ================= CORE ENGINE =================

    private ProtocoloMEDTO executar(Long id, java.util.function.Consumer<ProtocoloME> acao) {
        ProtocoloME p = buscarOuFalhar(id);
        acao.accept(p);
        return toDTO(salvar(p));
    }

    private ProtocoloME.StatusProtocoloME parseStatus(String status) {
        return ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
    }

    private void sincronizarStatusPaciente(ProtocoloME protocolo) {
    if (protocolo.getPaciente() == null) return;

    Paciente paciente = pacienteRepository.findById(protocolo.getPaciente().getId())
            .orElseThrow(() -> new RecursoNaoEncontradoException("Paciente não encontrado"));

    switch (protocolo.getStatus()) {

        case DOACAO_AUTORIZADA:
            paciente.setStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE);
            break;

        case FINALIZADO:
        case FAMILIA_RECUSOU:
        case CONTRAINDICADO:
            paciente.setStatus(Paciente.StatusPaciente.NAO_APTO);
            break;

        default:
            paciente.setStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME);
            break;
    }

    pacienteRepository.save(paciente);
}
    // ================= DELETE =================

    public void deletarProtocolo(Long id) {
        if (!protocoloRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Protocolo não encontrado");
        }
        protocoloRepository.deleteById(id);
    }

    private ProtocoloMEDTO toDTO(ProtocoloME protocolo) {
        return protocoloMapper.toDTO(protocolo);
    }
}