package back.backend.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import back.backend.dto.ProtocoloMEDTO;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.ProtocoloMapper;
import back.backend.model.CentralTransplantes;
import back.backend.model.ExameME;
import back.backend.model.Paciente;
import back.backend.model.ProtocoloME;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.ExameMERepository;
import back.backend.repository.PacienteRepository;
import back.backend.repository.ProtocoloMERepository;

@Service
public class ProtocoloMEService {

    @Autowired
    private ProtocoloMERepository protocoloRepository;

    @Autowired
    private CentralTransplantesRepository centralRepository;

    @Autowired
    private ExameMERepository exameRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProtocoloMapper protocoloMapper;

    @Autowired
    private ExameMEService exameMEService;

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

    public ProtocoloMEDTO criarProtocoloPorPacienteId(Long pacienteId,
            String diagnostico,
            String numero) {

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
                                : "Não informado"));

        ProtocoloME salvo = salvar(p);

        paciente.setStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME);
        pacienteRepository.save(paciente);

        return toDTO(salvo);
    }

    // ================= QUERY =================

    public List<ProtocoloMEDTO> listarTodos() {
        return protocoloRepository.findAllWithDetalhes()
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<ProtocoloMEDTO> buscarPorId(Long id) {
        return protocoloRepository.findByIdWithDetalhes(id)
                .map(this::toDTO);
    }

    public ProtocoloMEDTO buscarPorIdOuFalhar(Long id) {
        return toDTO(
                protocoloRepository.findByIdWithDetalhes(id)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado: " + id)));
    }

    public Optional<ProtocoloMEDTO> buscarPorNumeroProtocolo(String numero) {
        return protocoloRepository.findByNumeroProtocolo(numero)
                .map(this::toDTO);
    }

    public ProtocoloMEDTO buscarPorNumeroProtocoloOuFalhar(String numero) {
        return toDTO(
                protocoloRepository.findByNumeroProtocolo(numero)
                        .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado")));
    }

    public List<ProtocoloMEDTO> listarPorCentral(Long centralId) {
        CentralTransplantes c = centralRepository.findById(centralId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"));

        return protocoloRepository.findByCentralTransplantes(c)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProtocoloMEDTO> listarPorStatus(ProtocoloME.StatusProtocoloME status) {
        return protocoloRepository.findByStatus(status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProtocoloMEDTO> listarPorStatus(String status) {
        return listarPorStatus(parseStatus(status));
    }

    public List<ProtocoloMEDTO> listarPorPeriodo(LocalDateTime ini, LocalDateTime fim) {
        return protocoloRepository.findByDataNotificacaoBetween(ini, fim)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProtocoloMEDTO> listarPorCentralEStatus(Long centralId, String status) {
        CentralTransplantes c = centralRepository.findById(centralId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Central não encontrada"));

        ProtocoloME.StatusProtocoloME statusEnum = parseStatus(status);

        return protocoloRepository.findByCentralTransplantesAndStatus(c, statusEnum)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ProtocoloMEDTO> listarPorHospitalOrigem(String hospital) {
        return protocoloRepository.findByHospitalOrigem(hospital)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // ================= UPDATE =================

    public ProtocoloMEDTO atualizarProtocolo(Long id, ProtocoloME novo) {

        ProtocoloME p = buscarOuFalhar(id);

        if (novo.getNumeroProtocolo() != null &&
                protocoloRepository.existsByNumeroProtocoloAndIdNot(
                        novo.getNumeroProtocolo(), id)) {

            throw new ConflitoNegocioException("Número de protocolo já existe");
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

    public ProtocoloMEDTO atualizarRelatorioFinal(Long id, String textoRelatorio, String atualizadoPor) {
        ProtocoloME p = buscarOuFalhar(id);
        p.setRelatorioFinalEditavel(textoRelatorio);
        p.setRelatorioFinalAtualizadoPor(atualizadoPor);
        p.setRelatorioFinalAtualizadoEm(LocalDateTime.now());
        return toDTO(salvar(p));
    }

    public ProtocoloMEDTO alterarStatus(Long id, String novoStatus) {
        ProtocoloME p = buscarOuFalhar(id);
        p.setStatus(parseStatus(novoStatus));
        return toDTO(salvar(p));
    }

    // ================= ACTIONS =================

    public ProtocoloMEDTO confirmarMorteCerebral(Long id) {
        return executar(id, p -> {
            p.setStatus(ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA);
            p.setDataConfirmacaoME(LocalDateTime.now());
        });
    }

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

    public ProtocoloMEDTO marcarEntrevistaFamiliar(Long id) {
        return executar(id, p -> {
            p.setStatus(ProtocoloME.StatusProtocoloME.ENTREVISTA_FAMILIAR);
        });
    }

    public ProtocoloMEDTO registrarNotificacaoFamilia(Long id) {
        return executar(id, p -> {
            p.setFamiliaNotificada(true);
            p.setDataNotificacaoFamilia(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO registrarResultadoEntrevista(Long id,
            boolean autorizouDoacao,
            String observacoes) {

        ProtocoloME protocolo = buscarOuFalhar(id);

        protocolo.setFamiliaNotificada(true);
        protocolo.setDataNotificacaoFamilia(LocalDateTime.now());
        protocolo.setAutopsiaAutorizada(autorizouDoacao);

        // 🔥 Persistir decisão em Doacao para o cálculo automático de status funcionar
        if (protocolo.getDoacao() == null) {
            back.backend.model.Doacao doacao = new back.backend.model.Doacao();
            doacao.setProtocoloME(protocolo);
            doacao.setCentralTransplantes(protocolo.getCentralTransplantes());
            protocolo.setDoacao(doacao);
        }

        protocolo.getDoacao().setAutorizada(autorizouDoacao);
        protocolo.getDoacao().setDataEntrevista(LocalDateTime.now());
        protocolo.getDoacao().setObservacoes(observacoes);
        protocolo.getDoacao().setStatus(autorizouDoacao
                ? back.backend.model.Doacao.StatusDoacao.AUTORIZADA
                : back.backend.model.Doacao.StatusDoacao.RECUSADA);

        protocolo.setStatus(protocolo.calcularStatusAutomatico());

        return toDTO(salvar(protocolo));
    }

    public ProtocoloMEDTO autorizarAutopsia(Long id) {
        return executar(id, p -> {
            p.setAutopsiaAutorizada(true);
        });
    }

    public ProtocoloMEDTO registrarPreservacaoOrgaos(Long id) {
        return executar(id, p -> {
            p.setPreservacaoOrgaos(true);
            p.setDataPreservacao(LocalDateTime.now());
        });
    }

    public ProtocoloMEDTO atualizarStatusAutomatico(Long id) {
        ProtocoloME p = buscarOuFalhar(id);
        ProtocoloME.StatusProtocoloME novoStatus = p.calcularStatusAutomatico();
        p.setStatus(novoStatus);
        return toDTO(salvar(p));
    }

    // ================= CORE =================

    private ProtocoloMEDTO executar(Long id,
            java.util.function.Consumer<ProtocoloME> acao) {

        ProtocoloME p = buscarOuFalhar(id);
        acao.accept(p);
        return toDTO(salvar(p));
    }

    private ProtocoloME.StatusProtocoloME parseStatus(String status) {
        return ProtocoloME.StatusProtocoloME.valueOf(status.toUpperCase());
    }

    private void sincronizarStatusPaciente(ProtocoloME protocolo) {

        if (protocolo.getPaciente() == null)
            return;

        Paciente paciente = pacienteRepository.findById(
                protocolo.getPaciente().getId())
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

    // ================= VALIDAÇÃO DE TESTES PELA CENTRAL =================

    public ProtocoloMEDTO validarTesteClinico1(Long id, String validadoPor, String observacoes) {
        return executar(id, p -> {
            p.setTesteClinico1Validado(true);
            p.setDataValidacaoTesteClinico1(LocalDateTime.now());
            p.setValidadosPor(validadoPor);
            if (observacoes != null) {
                // Adicionar observação se houver
            }
            // ✅ Recalcular status automaticamente
            p.setStatus(p.calcularStatusAutomatico());
        });
    }

    public ProtocoloMEDTO validarTesteClinico2(Long id, String validadoPor, String observacoes) {
        return executar(id, p -> {
            p.setTesteClinico2Validado(true);
            p.setDataValidacaoTesteClinico2(LocalDateTime.now());
            p.setValidadosPor(validadoPor);
            // ✅ Recalcular status automaticamente
            p.setStatus(p.calcularStatusAutomatico());
        });
    }

    public ProtocoloMEDTO validarTestesComplementares(Long id, String validadoPor, String observacoes) {
        return executar(id, p -> {
            p.setTestesComplementaresValidados(true);
            p.setDataValidacaoTesteComplementar(LocalDateTime.now());
            p.setValidadosPor(validadoPor);
            // ✅ Recalcular status automaticamente
            p.setStatus(p.calcularStatusAutomatico());
        });
    }

    public ProtocoloMEDTO validarApneia(Long id, String validadoPor, String observacoes) {
        return executar(id, p -> {
            p.setApneiaValidada(true);
            p.setDataValidacaoApneia(LocalDateTime.now());
            p.setValidadosPor(validadoPor);
            // ✅ Recalcular status automaticamente
            p.setStatus(p.calcularStatusAutomatico());
        });
    }

    public ProtocoloMEDTO validarExame(Long id, Long exameId, boolean validado, String validadoPor,
            String observacoes) {
        ProtocoloME protocolo = buscarOuFalhar(id);
        ExameME exame = exameRepository.findById(exameId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado"));

        if (!exame.getProtocoloME().getId().equals(id)) {
            throw new ConflitoNegocioException("Exame não pertence a este protocolo");
        }

        if (validado) {
            exame.setStatusValidacao(ExameME.StatusValidacao.VALIDADO);
        } else {
            exame.setStatusValidacao(ExameME.StatusValidacao.REJEITADO);
        }

        exame.setValidadoPor(validadoPor);
        exame.setDataValidacao(LocalDateTime.now());
        exame.setObservacoesValidacao(observacoes);

        exameRepository.save(exame);

        // ✅ Atualizar indicadores do protocolo com base em VALIDADOS
        exameMEService.atualizarIndicadoresProtocolo(id);

        // ✅ Recarregar protocolo com novos valores
        protocolo = buscarOuFalhar(id);

        // ✅ Recalcular status automaticamente
        protocolo.setStatus(protocolo.calcularStatusAutomatico());

        // ✅ Salvar recalcula e sincroniza
        return toDTO(salvar(protocolo));
    }

    // ================= DELETE =================

    public void deletarProtocolo(@NonNull Long id) {
        if (!protocoloRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Protocolo não encontrado");
        }
        protocoloRepository.deleteById(id);
    }

    private ProtocoloMEDTO toDTO(ProtocoloME protocolo) {
        return protocoloMapper.toDTO(protocolo);
    }
}
