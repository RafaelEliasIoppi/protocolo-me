package back.backend.service;

import back.backend.model.ProtocoloME;
import back.backend.model.CentralTransplantes;
import back.backend.model.ExameME;
import back.backend.model.Paciente;
import back.backend.repository.ProtocoloMERepository;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.ExameMERepository;
import back.backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class ProtocoloMEService {

    @Autowired
    private ProtocoloMERepository protocoloRepository;

    @Autowired
    private CentralTransplantesRepository centralRepository;

    @Autowired
    private ExameMERepository exameMERepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    private boolean exameRealizado(ExameME exame) {
        if (exame == null) {
            return false;
        }

        boolean temResultadoTexto = exame.getResultado() != null && !exame.getResultado().trim().isEmpty();
        boolean temResultadoBooleano = exame.getResultado_positivo() != null;
        boolean temDataRealizacao = exame.getDataRealizacao() != null;

        return temResultadoTexto || temResultadoBooleano || temDataRealizacao;
    }

    private void sincronizarStatusPacienteComProtocolo(ProtocoloME protocolo) {
        if (protocolo == null || protocolo.getPaciente() == null || protocolo.getPaciente().getId() == null) {
            return;
        }

        Paciente paciente = pacienteRepository.findById(protocolo.getPaciente().getId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        ProtocoloME.StatusProtocoloME statusProtocolo = protocolo.getStatus();
        if (statusProtocolo == null) {
            return;
        }

        if (statusProtocolo == ProtocoloME.StatusProtocoloME.FINALIZADO) {
            return;
        }

        Paciente.StatusPaciente novoStatusPaciente;
        switch (statusProtocolo) {
            case NOTIFICADO:
            case EM_PROCESSO:
            case MORTE_CEREBRAL_CONFIRMADA:
            case ENTREVISTA_FAMILIAR:
                novoStatusPaciente = Paciente.StatusPaciente.EM_PROTOCOLO_ME;
                break;
            case DOACAO_AUTORIZADA:
                novoStatusPaciente = Paciente.StatusPaciente.APTO_TRANSPLANTE;
                break;
            case FAMILIA_RECUSOU:
            case CONTRAINDICADO:
            case FINALIZADO:
                novoStatusPaciente = Paciente.StatusPaciente.NAO_APTO;
                break;
            default:
                novoStatusPaciente = Paciente.StatusPaciente.EM_PROTOCOLO_ME;
                break;
        }

        if (paciente.getStatus() != novoStatusPaciente) {
            paciente.setStatus(novoStatusPaciente);
            pacienteRepository.save(paciente);
        }
    }

    private void sincronizarEntrevistaPaciente(ProtocoloME protocolo, String statusEntrevista, String observacoes) {
        sincronizarEntrevistaPaciente(protocolo, statusEntrevista, observacoes, null);
    }

    private void sincronizarEntrevistaPaciente(ProtocoloME protocolo, String statusEntrevista, String observacoes, Paciente.StatusPaciente novoStatusPaciente) {
        if (protocolo == null || protocolo.getPaciente() == null || protocolo.getPaciente().getId() == null) {
            return;
        }

        Paciente paciente = pacienteRepository.findById(protocolo.getPaciente().getId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        paciente.setStatusEntrevistaFamiliar(statusEntrevista);
        paciente.setDataEntrevistaFamiliar(LocalDateTime.now());
        if (observacoes != null) {
            String textoObservacoes = observacoes.trim();
            paciente.setObservacoesEntrevistaFamiliar(textoObservacoes.isEmpty() ? null : textoObservacoes);
        }

        if (novoStatusPaciente != null) {
            paciente.setStatus(novoStatusPaciente);
        }

        pacienteRepository.save(paciente);
    }

    // Criar novo protocolo de ME e auto-popular com 35 exames
    public ProtocoloME criarProtocolo(ProtocoloME protocolo) {
        if (protocolo.getPaciente() == null || protocolo.getPaciente().getId() == null) {
            throw new RuntimeException("Paciente é obrigatório para criar protocolo");
        }

        Paciente paciente = pacienteRepository.findById(protocolo.getPaciente().getId())
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));

        if (protocolo.getCentralTransplantes() == null) {
            CentralTransplantes central = centralRepository.findAll().stream()
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Nenhuma central de transplantes cadastrada"));
            protocolo.setCentralTransplantes(central);
        }

        protocolo.setPaciente(paciente);

        if (protocolo.getNumeroProtocolo() == null || protocolo.getNumeroProtocolo().trim().isEmpty()) {
            protocolo.setNumeroProtocolo(gerarNumeroProtocolo());
        }

        if (protocolo.getHospitalOrigem() == null || protocolo.getHospitalOrigem().trim().isEmpty()) {
            if (paciente.getHospitalOrigem() != null && !paciente.getHospitalOrigem().trim().isEmpty()) {
                protocolo.setHospitalOrigem(paciente.getHospitalOrigem());
            } else if (paciente.getHospital() != null) {
                protocolo.setHospitalOrigem(paciente.getHospital().getNome());
            }
        }

        if (protocolo.getStatus() == null) {
            protocolo.setStatus(ProtocoloME.StatusProtocoloME.NOTIFICADO);
        }

        if (protocolo.getDataNotificacao() == null) {
            protocolo.setDataNotificacao(LocalDateTime.now());
        }

        if (protocoloRepository.findByNumeroProtocolo(protocolo.getNumeroProtocolo()).isPresent()) {
            throw new RuntimeException("Protocolo com número " + protocolo.getNumeroProtocolo() + " já existe");
        }
        
        // Salvar protocolo
        ProtocoloME protocoloSalvo = protocoloRepository.save(protocolo);

        // Atualizar status do paciente para em protocolo
        paciente.setStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME);
        pacienteRepository.save(paciente);
        
        // Auto-popular com 35 exames (Clínicos, Complementares, Laboratoriais)
        preencherExamesAutomaticamente(protocoloSalvo);
        
        return protocoloSalvo;
    }

    public ProtocoloME criarProtocoloPorPacienteId(Long pacienteId, String diagnosticoBasico) {
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado com ID: " + pacienteId));

        CentralTransplantes central = centralRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nenhuma central de transplantes cadastrada"));

        ProtocoloME protocolo = new ProtocoloME();
        protocolo.setPaciente(paciente);
        protocolo.setCentralTransplantes(central);
        protocolo.setNumeroProtocolo(gerarNumeroProtocolo());
        protocolo.setHospitalOrigem(
                (paciente.getHospitalOrigem() != null && !paciente.getHospitalOrigem().trim().isEmpty())
                        ? paciente.getHospitalOrigem()
                        : (paciente.getHospital() != null ? paciente.getHospital().getNome() : "Hospital não informado")
        );
        protocolo.setStatus(ProtocoloME.StatusProtocoloME.NOTIFICADO);
        protocolo.setDiagnosticoBasico(diagnosticoBasico);
        protocolo.setDataNotificacao(LocalDateTime.now());

        return criarProtocolo(protocolo);
    }

    private String gerarNumeroProtocolo() {
        String base = "ME-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String candidato = base;
        int sufixo = 1;

        while (protocoloRepository.findByNumeroProtocolo(candidato).isPresent()) {
            candidato = base + "-" + sufixo;
            sufixo++;
        }

        return candidato;
    }

    /**
     * Preenche automaticamente os 35 exames necessários para um protocolo ME
     */
    private void preencherExamesAutomaticamente(ProtocoloME protocolo) {
        // Array com todos os 35 tipos de exames
        ExameME.TipoExame[] examesObrigatorios = {
            // Exames Clínicos (9)
            ExameME.TipoExame.RESPOSTA_ESTIMULO_DORO,
            ExameME.TipoExame.REFLEXO_PUPILAR,
            ExameME.TipoExame.REFLEXO_CORNEAL,
            ExameME.TipoExame.REFLEXO_VESTIBULO_OCULAR,
            ExameME.TipoExame.REFLEXO_NAUSEOSO,
            ExameME.TipoExame.REFLEXO_TOSSE,
            ExameME.TipoExame.APNEIA_TEST,
            ExameME.TipoExame.POSTURA_DECEREBRADO,
            ExameME.TipoExame.POSTURA_DESCEREBRADO,
            
            // Exames Complementares (8)
            ExameME.TipoExame.ANGIOGRAFIA_CEREBRAL,
            ExameME.TipoExame.RESSONANCIA_MAGNETICA,
            ExameME.TipoExame.TOMOGRAFIA_CRANIO,
            ExameME.TipoExame.TOMOGRAFIA_ANGIO,
            ExameME.TipoExame.ULTRASSOM_DOPPLER,
            ExameME.TipoExame.ELETROENCEFALOGRAMA,
            ExameME.TipoExame.MAPEAMENTO_CEREBRAL,
            ExameME.TipoExame.RESSONANCIA_MAGNETICA_FUNCIONAL,
            
            // Exames Laboratoriais (18)
            ExameME.TipoExame.GASOMETRIA_ARTERIAL,
            ExameME.TipoExame.HEMOGRAMA,
            ExameME.TipoExame.ELETRÓLITOS,
            ExameME.TipoExame.GLICEMIA,
            ExameME.TipoExame.CALCIO,
            ExameME.TipoExame.FUNCAO_HEPATICA,
            ExameME.TipoExame.FUNCAO_RENAL,
            ExameME.TipoExame.COAGULACAO,
            ExameME.TipoExame.PROTEINAS_TOTAIS,
            ExameME.TipoExame.SOROLOGIA_HIV,
            ExameME.TipoExame.SOROLOGIA_HEPATITE_B,
            ExameME.TipoExame.SOROLOGIA_HEPATITE_C,
            ExameME.TipoExame.SOROLOGIA_SIFILIS,
            ExameME.TipoExame.CULTURA_SANGUE,
            ExameME.TipoExame.TIPAGEM_SANGUINEA,
            ExameME.TipoExame.SOROLOGIAS_DIVERSAS,
            ExameME.TipoExame.TESTE_FUNCAO_TIREOIDE,
            ExameME.TipoExame.LACTATO
        };
        
        // Para cada tipo de exame, criar e salvar
        for (ExameME.TipoExame tipoExame : examesObrigatorios) {
            ExameME exame = new ExameME();
            exame.setProtocoloME(protocolo);
            exame.setTipoExame(tipoExame);
            exame.setCategoria(tipoExame.getCategoria());
            exame.setDescricao(tipoExame.getLabel());
            exame.setResultado(null); // Será preenchido quando o exame for realizado
            exame.setResultado_positivo(null);
            exame.setDataRealizacao(null);
            exame.setResponsavel(null);
            exame.setObservacoes("Exame criado automaticamente com protocolo");
            
            exameMERepository.save(exame);
        }
    }

    // Listar todos os protocolos
    public List<ProtocoloME> listarTodos() {
        return protocoloRepository.findAll();
    }

    // Buscar por ID
    public Optional<ProtocoloME> buscarPorId(Long id) {
        return protocoloRepository.findById(id);
    }

    // Buscar por número do protocolo
    public Optional<ProtocoloME> buscarPorNumeroProtocolo(String numeroProtocolo) {
        return protocoloRepository.findByNumeroProtocolo(numeroProtocolo);
    }

    // Listar protocolos de uma central
    public List<ProtocoloME> listarPorCentral(Long centralId) {
        CentralTransplantes central = centralRepository.findById(centralId)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));
        return protocoloRepository.findByCentralTransplantes(central);
    }

    // Listar por status
    public List<ProtocoloME> listarPorStatus(ProtocoloME.StatusProtocoloME status) {
        return protocoloRepository.findByStatus(status);
    }

    // Listar por status em uma central específica
    public List<ProtocoloME> listarPorCentralEStatus(Long centralId, ProtocoloME.StatusProtocoloME status) {
        CentralTransplantes central = centralRepository.findById(centralId)
                .orElseThrow(() -> new RuntimeException("Central não encontrada"));
        return protocoloRepository.findByCentralTransplantesAndStatus(central, status);
    }

    // Listar por período
    public List<ProtocoloME> listarPorPeriodo(LocalDateTime dataInicio, LocalDateTime dataFim) {
        return protocoloRepository.findByDataNotificacaoBetween(dataInicio, dataFim);
    }

    // Listar por hospital origem
    public List<ProtocoloME> listarPorHospitalOrigem(String hospitalOrigem) {
        return protocoloRepository.findByHospitalOrigem(hospitalOrigem);
    }

    // Atualizar protocolo
    public ProtocoloME atualizarProtocolo(Long id, ProtocoloME protocoloAtualizado) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + id));

        protocolo.setDiagnosticoBasico(protocoloAtualizado.getDiagnosticoBasico());
        protocolo.setCausaMorte(protocoloAtualizado.getCausaMorte());
        protocolo.setObservacoes(protocoloAtualizado.getObservacoes());
        protocolo.setMedicoResponsavel(protocoloAtualizado.getMedicoResponsavel());
        protocolo.setEnfermeiro(protocoloAtualizado.getEnfermeiro());
        protocolo.setOrgaosDisponiveis(protocoloAtualizado.getOrgaosDisponiveis());

        return protocoloRepository.save(protocolo);
    }

    // Registrar teste clínico 1
    public ProtocoloME registrarTesteClinico1(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setTesteClinico1Realizado(true);
        protocolo.setDataTesteClinico1(LocalDateTime.now());
        return protocoloRepository.save(protocolo);
    }

    // Registrar teste clínico 2
    public ProtocoloME registrarTesteClinico2(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setTesteClinico2Realizado(true);
        protocolo.setDataTesteClinico2(LocalDateTime.now());
        return protocoloRepository.save(protocolo);
    }

    // Registrar testes complementares
    public ProtocoloME registrarTestesComplementares(Long id, String testesComplementares) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setTestesComplementaresRealizados(true);
        protocolo.setTestesComplementares(testesComplementares);
        protocolo.setDataTesteComplementar(LocalDateTime.now());
        return protocoloRepository.save(protocolo);
    }

    // Registrar notificação da família
    public ProtocoloME registrarNotificacaoFamilia(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setFamiliaNotificada(true);
        protocolo.setDataNotificacaoFamilia(LocalDateTime.now());
        return protocoloRepository.save(protocolo);
    }

    // Autorizar autópsia
    public ProtocoloME autorizarAutopsia(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setAutopsiaAutorizada(true);
        return protocoloRepository.save(protocolo);
    }

    // Registrar preservação de órgãos
    public ProtocoloME registrarPreservacaoOrgaos(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setPreservacaoOrgaos(true);
        protocolo.setDataPreservacao(LocalDateTime.now());
        return protocoloRepository.save(protocolo);
    }

    // Confirmar morte cerebral
    public ProtocoloME confirmarMorteCerebral(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        protocolo.setDataConfirmacaoME(LocalDateTime.now());
        protocolo.setStatus(ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA);
        return protocoloRepository.save(protocolo);
    }

    /**
     * Atualiza o status do protocolo automaticamente baseado nos exames realizados
     * Chamado sempre que um exame é inserido ou atualizado
     */
    public ProtocoloME atualizarStatusAutomatico(Long protocolo_me_id) {
        ProtocoloME protocolo = protocoloRepository.findById(protocolo_me_id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + protocolo_me_id));

        if (protocolo.getStatus() == ProtocoloME.StatusProtocoloME.FINALIZADO) {
            return protocolo;
        }
        
        // Contar exames realizados por categoria
        List<ExameME> exames = protocolo.getExames();
        long clinicosRealizados = exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO && exameRealizado(e))
                .count();
        long complementaresRealizados = exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR && exameRealizado(e))
                .count();

        // Recalcula flags sempre, inclusive para cenários de edição/exclusão de exames.
        protocolo.setTesteClinico1Realizado(clinicosRealizados >= 1);
        protocolo.setTesteClinico2Realizado(clinicosRealizados >= 2);
        protocolo.setTestesComplementaresRealizados(complementaresRealizados >= 1);

        // Calcular novo status baseado nos testes
        ProtocoloME.StatusProtocoloME novoStatus = protocolo.calcularStatusAutomatico();
        protocolo.setStatus(novoStatus);

        if (novoStatus == ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA) {
            if (protocolo.getDataConfirmacaoME() == null) {
                protocolo.setDataConfirmacaoME(LocalDateTime.now());
            }
        } else {
            protocolo.setDataConfirmacaoME(null);
        }

        ProtocoloME protocoloAtualizado = protocoloRepository.save(protocolo);
        sincronizarStatusPacienteComProtocolo(protocoloAtualizado);
        return protocoloAtualizado;
    }

    /**
     * Marcar para entrevista familiar
     */
    public ProtocoloME marcarParaEntrevista(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        
        if (!protocolo.estaProtoProntoParaEntrevista()) {
            throw new RuntimeException("Protocolo não está pronto para entrevista. Todos os testes devem estar completos.");
        }
        
        protocolo.setStatus(ProtocoloME.StatusProtocoloME.ENTREVISTA_FAMILIAR);
        ProtocoloME protocoloAtualizado = protocoloRepository.save(protocolo);
        sincronizarEntrevistaPaciente(protocoloAtualizado, "EM_ANDAMENTO", null, Paciente.StatusPaciente.EM_PROTOCOLO_ME);
        sincronizarStatusPacienteComProtocolo(protocoloAtualizado);
        return protocoloAtualizado;
    }

    /**
     * Registrar resultado da entrevista familiar
     */
    public ProtocoloME registrarResultadoEntrevista(Long id, boolean autorizouDoacao, String observacoes) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        if (protocolo.getStatus() == ProtocoloME.StatusProtocoloME.DOACAO_AUTORIZADA
                || protocolo.getStatus() == ProtocoloME.StatusProtocoloME.FAMILIA_RECUSOU) {
            throw new RuntimeException("Entrevista já foi finalizada para este protocolo");
        }
        
        protocolo.setFamiliaNotificada(true);
        protocolo.setDataNotificacaoFamilia(LocalDateTime.now());
        protocolo.setAutopsiaAutorizada(autorizouDoacao);

        protocolo.setStatus(ProtocoloME.StatusProtocoloME.FINALIZADO);

        ProtocoloME protocoloAtualizado = protocoloRepository.save(protocolo);
        sincronizarEntrevistaPaciente(
            protocoloAtualizado,
            autorizouDoacao ? "AUTORIZADA" : "RECUSADA",
            observacoes,
            autorizouDoacao ? Paciente.StatusPaciente.APTO_TRANSPLANTE : Paciente.StatusPaciente.NAO_APTO
        );
        sincronizarStatusPacienteComProtocolo(protocoloAtualizado);
        return protocoloAtualizado;
    }

    // Alterar status do protocolo
    public ProtocoloME alterarStatus(Long id, ProtocoloME.StatusProtocoloME novoStatus) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + id));
        protocolo.setStatus(novoStatus);
        ProtocoloME protocoloAtualizado = protocoloRepository.save(protocolo);
        sincronizarStatusPacienteComProtocolo(protocoloAtualizado);
        return protocoloAtualizado;
    }

    // Deletar protocolo (será cascade delete dos exames)
    public void deletarProtocolo(Long id) {
        if (!protocoloRepository.existsById(id)) {
            throw new RuntimeException("Protocolo não encontrado com ID: " + id);
        }
        protocoloRepository.deleteById(id);
    }
}
