package back.backend.service;

import back.backend.model.ProtocoloME;
import back.backend.model.CentralTransplantes;
import back.backend.model.ExameME;
import back.backend.repository.ProtocoloMERepository;
import back.backend.repository.CentralTransplantesRepository;
import back.backend.repository.ExameMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

@Service
@Transactional
public class ProtocoloMEService {

    @Autowired
    private ProtocoloMERepository protocoloRepository;

    @Autowired
    private CentralTransplantesRepository centralRepository;

    @Autowired
    private ExameMERepository exameMERepository;

    // Criar novo protocolo de ME e auto-popular com 35 exames
    public ProtocoloME criarProtocolo(ProtocoloME protocolo) {
        if (protocoloRepository.findByNumeroProtocolo(protocolo.getNumeroProtocolo()).isPresent()) {
            throw new RuntimeException("Protocolo com número " + protocolo.getNumeroProtocolo() + " já existe");
        }
        
        // Salvar protocolo
        ProtocoloME protocoloSalvo = protocoloRepository.save(protocolo);
        
        // Auto-popular com 35 exames (Clínicos, Complementares, Laboratoriais)
        preencherExamesAutomaticamente(protocoloSalvo);
        
        return protocoloSalvo;
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

    // Registrar teste clínico 2 (requer teste clínico 1 já realizado)
    public ProtocoloME registrarTesteClinico2(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        if (!Boolean.TRUE.equals(protocolo.getTesteClinico1Realizado())) {
            throw new RuntimeException("O Teste Clínico 1 deve ser realizado antes do Teste Clínico 2");
        }
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

    // Confirmar morte cerebral (requer TC1, TC2 e testes complementares realizados)
    public ProtocoloME confirmarMorteCerebral(Long id) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        if (!Boolean.TRUE.equals(protocolo.getTesteClinico1Realizado())) {
            throw new RuntimeException("O Teste Clínico 1 deve ser realizado antes de confirmar a morte cerebral");
        }
        if (!Boolean.TRUE.equals(protocolo.getTesteClinico2Realizado())) {
            throw new RuntimeException("O Teste Clínico 2 deve ser realizado antes de confirmar a morte cerebral");
        }
        protocolo.setDataConfirmacaoME(LocalDateTime.now());
        protocolo.setStatus(ProtocoloME.StatusProtocoloME.MORTE_CEREBRAL_CONFIRMADA);
        return protocoloRepository.save(protocolo);
    }

    // Alterar status do protocolo
    public ProtocoloME alterarStatus(Long id, ProtocoloME.StatusProtocoloME novoStatus) {
        ProtocoloME protocolo = protocoloRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado com ID: " + id));
        protocolo.setStatus(novoStatus);
        return protocoloRepository.save(protocolo);
    }

    // Deletar protocolo (será cascade delete dos exames)
    public void deletarProtocolo(Long id) {
        if (!protocoloRepository.existsById(id)) {
            throw new RuntimeException("Protocolo não encontrado com ID: " + id);
        }
        protocoloRepository.deleteById(id);
    }
}
