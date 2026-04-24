package back.backend.service;

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

    // ================= HELPERS =================

    private ProtocoloME buscarOuFalhar(Long id) {
        return protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado: " + id));
    }

    private CentralTransplantes obterCentralPadrao() {
        return centralRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhuma central cadastrada"));
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

    public ProtocoloME criarProtocoloPorPacienteId(Long pacienteId, String diagnostico, String numero) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

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

        return salvo;
    }

    // ================= QUERY =================

    public List<ProtocoloME> listarTodos() {
        return protocoloRepository.findAllWithDetalhes();
    }

    public Optional<ProtocoloME> buscarPorId(Long id) {
        return protocoloRepository.findByIdWithDetalhes(id);
    }

    public Optional<ProtocoloME> buscarPorNumeroProtocolo(String numero) {
        return protocoloRepository.findByNumeroProtocolo(numero);
    }

    public List<ProtocoloME> listarPorCentral(Long centralId) {
        CentralTransplantes c = centralRepository.findById(centralId)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));

        return protocoloRepository.findByCentralTransplantes(c);
    }

    public List<ProtocoloME> listarPorStatus(ProtocoloME.StatusProtocoloME status) {
        return protocoloRepository.findByStatus(status);
    }

    public List<ProtocoloME> listarPorCentralEStatus(Long id, ProtocoloME.StatusProtocoloME status) {
        CentralTransplantes c = centralRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));

        return protocoloRepository.findByCentralTransplantesAndStatus(c, status);
    }

    public List<ProtocoloME> listarPorPeriodo(LocalDateTime ini, LocalDateTime fim) {
        return protocoloRepository.findByDataNotificacaoBetween(ini, fim);
    }

    public List<ProtocoloME> listarPorHospitalOrigem(String hospital) {
        return protocoloRepository.findByHospitalOrigem(hospital);
    }

    // ================= UPDATE =================

    public ProtocoloME atualizarProtocolo(Long id, ProtocoloME novo) {

        ProtocoloME p = buscarOuFalhar(id);

        if (novo.getNumeroProtocolo() != null &&
                protocoloRepository.existsByNumeroProtocoloAndIdNot(novo.getNumeroProtocolo(), id)) {
            throw new RuntimeException("Número de protocolo já existe");
        }

        p.setNumeroProtocolo(novo.getNumeroProtocolo());
        p.setDiagnosticoBasico(novo.getDiagnosticoBasico());
        p.setCausaMorte(novo.getCausaMorte());
        p.setObservacoes(novo.getObservacoes());
        p.setMedicoResponsavel(novo.getMedicoResponsavel());
        p.setEnfermeiro(novo.getEnfermeiro());
        p.setOrgaosDisponiveis(novo.getOrgaosDisponiveis());

        return salvar(p);
    }

    public ProtocoloME atualizarRelatorioFinal(Long id, String texto, String usuario) {
        ProtocoloME p = buscarOuFalhar(id);

        p.setRelatorioFinalEditavel(texto);
        p.setRelatorioFinalAtualizadoPor(usuario);
        p.setRelatorioFinalAtualizadoEm(LocalDateTime.now());

        return salvar(p);
    }

    // ================= ACTIONS =================

    public ProtocoloME registrarTesteClinico1(Long id) {
        return executar(id, p -> {
            p.setTesteClinico1Realizado(true);
            p.setDataTesteClinico1(LocalDateTime.now());
        });
    }

    public ProtocoloME registrarTesteClinico2(Long id) {
        return executar(id, p -> {
            p.setTesteClinico2Realizado(true);
            p.setDataTesteClinico2(LocalDateTime.now());
        });
    }

    public ProtocoloME confirmarMorteCerebral(Long id) {
        return executar(id, p -> {
            p.setStatus(ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA);
            p.setDataConfirmacaoME(LocalDateTime.now());
        });
    }

    public ProtocoloME registrarNotificacaoFamilia(Long id) {
        return executar(id, p -> {
            p.setFamiliaNotificada(true);
            p.setDataNotificacaoFamilia(LocalDateTime.now());
        });
    }

    public ProtocoloME autorizarAutopsia(Long id) {
        return executar(id, p -> p.setAutopsiaAutorizada(true));
    }

    public ProtocoloME registrarPreservacaoOrgaos(Long id) {
        return executar(id, p -> {
            p.setPreservacaoOrgaos(true);
            p.setDataPreservacao(LocalDateTime.now());
        });
    }

    public ProtocoloME alterarStatus(Long id, ProtocoloME.StatusProtocoloME status) {
        return executar(id, p -> p.setStatus(status));
    }

    // ================= CORE ENGINE =================

    private ProtocoloME executar(Long id, java.util.function.Consumer<ProtocoloME> acao) {
        ProtocoloME p = buscarOuFalhar(id);
        acao.accept(p);
        return salvar(p);
    }

    private void sincronizarStatusPaciente(ProtocoloME protocolo) {
    if (protocolo.getPaciente() == null) return;

    Paciente paciente = pacienteRepository.findById(protocolo.getPaciente().getId())
            .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

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
            throw new RuntimeException("Protocolo não encontrado");
        }
        protocoloRepository.deleteById(id);
    }
}