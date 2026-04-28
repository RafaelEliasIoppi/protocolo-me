package back.backend.service;

import back.backend.dto.ExameMEDTO;
import back.backend.dto.ExameResumoDTO;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.ExameMapper;
import back.backend.mapper.ExameResumoMapper;
import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import back.backend.repository.ExameMERepository;
import back.backend.repository.ProtocoloMERepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExameMEService {

    private final ExameMERepository exameRepository;
    private final ProtocoloMERepository protocoloRepository;
    private final ExameMapper exameMapper;
    private final ExameResumoMapper exameResumoMapper;

    // ================= CREATE =================

    public ExameMEDTO criarExame(ExameME exame) {
        exame.setDataCriacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());

        ExameME salvo = exameRepository.save(exame);
        log.info("Exame criado com ID: {}", salvo.getId());

        // Atualizar indicadores do protocolo
        atualizarIndicadoresProtocolo(salvo.getProtocoloME().getId());

        return toDTO(salvo);
    }

    // ================= LIST =================

    public List<ExameMEDTO> listarExamesPorProtocolo(Long protocoloId) {
        return exameRepository.findByProtocoloME_Id(protocoloId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ExameMEDTO> listarExamesClinico(Long protocoloId) {
        return exameRepository.findByProtocoloME_Id(protocoloId)
            .stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
                .map(this::toDTO)
                .toList();
    }

    public List<ExameMEDTO> listarExamesComplementares(Long protocoloId) {
        return exameRepository.findByProtocoloME_Id(protocoloId)
            .stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR)
                .map(this::toDTO)
                .toList();
    }

    public List<ExameMEDTO> listarExamesLaboratoriais(Long protocoloId) {
        return exameRepository.findByProtocoloME_Id(protocoloId)
            .stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL)
                .map(this::toDTO)
                .toList();
    }

    // ================= GET =================

    public ExameMEDTO buscarPorIdOuFalhar(Long id) {
        return toDTO(buscarEntity(id));
    }

    // ================= UPDATE =================

    public ExameMEDTO atualizarExame(Long id, ExameME dados) {

        ExameME exame = buscarEntity(id);

        if (dados.getDescricao() != null) exame.setDescricao(dados.getDescricao());
        if (dados.getTipoExame() != null) exame.setTipoExame(dados.getTipoExame());
        if (dados.getObservacoes() != null) exame.setObservacoes(dados.getObservacoes());
        if (dados.getDataRealizacao() != null) exame.setDataRealizacao(dados.getDataRealizacao());
        if (dados.getResponsavel() != null) exame.setResponsavel(dados.getResponsavel());

        exame.setDataAtualizacao(LocalDateTime.now());

        ExameME atualizado = exameRepository.save(exame);
        log.info("Exame atualizado ID: {}", id);

        // Atualizar indicadores do protocolo
        atualizarIndicadoresProtocolo(atualizado.getProtocoloME().getId());

        return toDTO(atualizado);
    }

    // ================= RESULT =================

    public ExameMEDTO registrarResultado(Long id, String resultado, Boolean resultadoPositivo, String responsavel) {

        ExameME exame = buscarEntity(id);

        ExameME.ResultadoExame resultadoEnum = parseResultado(resultado, resultadoPositivo);
        exame.setResultado(resultadoEnum);
        exame.setResponsavel(responsavel);
        exame.setDataRealizacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());

        ExameME atualizado = exameRepository.save(exame);
        log.info("Resultado registrado para exame ID: {}", id);

        // Atualizar indicadores do protocolo
        atualizarIndicadoresProtocolo(atualizado.getProtocoloME().getId());

        return toDTO(atualizado);
    }

    // ================= DELETE =================

    public void deletarExame(Long id) {
        ExameME exame = buscarEntity(id);
        Long protocoloId = exame.getProtocoloME().getId();

        exameRepository.delete(exame);
        log.warn("Exame deletado ID: {}", id);

        // Atualizar indicadores do protocolo
        atualizarIndicadoresProtocolo(protocoloId);
    }

    // ================= STATS =================

    public ExameResumoDTO obterResumoExames(Long protocoloId) {

        List<ExameME> exames = exameRepository.findByProtocoloME_Id(protocoloId);

        int total = exames.size();
        int realizados = (int) exames.stream()
                .filter(e -> e.getResultado() != null)
                .count();

        int pendentes = total - realizados;

        int clinicosTotal = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
                .count();

        int clinicosRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
                .filter(e -> e.getResultado() != null)
                .count();

        int complementaresTotal = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR)
                .count();

        int complementaresRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR)
                .filter(e -> e.getResultado() != null)
                .count();

        int laboratoriaisTotal = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL)
                .count();

        int laboratoriaisRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL)
                .filter(e -> e.getResultado() != null)
                .count();

        return exameResumoMapper.toDTO(new ExameResumo(
                total,
                realizados,
                pendentes,
                clinicosRealizados,
                clinicosTotal,
                complementaresRealizados,
                complementaresTotal,
                laboratoriaisRealizados,
                laboratoriaisTotal
        ));
    }

    // ================= HELPERS =================

    private ExameME buscarEntity(Long id) {
        return exameRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado"));
    }

    private ExameMEDTO toDTO(ExameME exame) {
        return exameMapper.toDTO(exame);
    }

    @Transactional
    protected void atualizarIndicadoresProtocolo(Long protocoloId) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Protocolo não encontrado"));

        List<ExameME> exames = exameRepository.findByProtocoloME_Id(protocoloId);

        long clinicosConcluidos = exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
            .filter(e -> e.getResultado() != null)
            .count();

        // Considera qualquer exame clínico concluído para compor o TC1/TC2.
        boolean tc1 = clinicosConcluidos >= 1;
        protocolo.setTesteClinico1Realizado(tc1);
        if (tc1 && protocolo.getDataTesteClinico1() == null) {
            protocolo.setDataTesteClinico1(LocalDateTime.now());
        }

        boolean tc2 = clinicosConcluidos >= 2;
        protocolo.setTesteClinico2Realizado(tc2);
        if (tc2 && protocolo.getDataTesteClinico2() == null) {
            protocolo.setDataTesteClinico2(LocalDateTime.now());
        }

        // Indicador de Testes Complementares
        boolean comp = exames.stream()
                .anyMatch(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR && e.getResultado() != null);
        protocolo.setTestesComplementaresRealizados(comp);
        if (comp && protocolo.getDataTesteComplementar() == null) {
            protocolo.setDataTesteComplementar(LocalDateTime.now());
        }

        // Se todos os testes realizados, marcar data de confirmação se ainda não houver
        if (tc1 && tc2 && comp && protocolo.getDataConfirmacaoME() == null) {
            protocolo.setDataConfirmacaoME(LocalDateTime.now());
        }

        // Atualizar status automático se necessário
        protocolo.setStatus(protocolo.calcularStatusAutomatico());

        protocoloRepository.save(protocolo);
        log.info("Indicadores do protocolo {} atualizados com sucesso", protocoloId);
    }

    private ExameME.ResultadoExame parseResultado(String resultado, Boolean resultadoPositivo) {
        if (resultado != null && !resultado.isBlank()) {
            return ExameME.ResultadoExame.valueOf(resultado.toUpperCase());
        }
        if (resultadoPositivo == null) {
            return null;
        }
        return resultadoPositivo ? ExameME.ResultadoExame.POSITIVO : ExameME.ResultadoExame.NEGATIVO;
    }

    // ================= DTO INTERNO =================

    public static class ExameResumo {

        private final int totalExames;
        private final int examesRealizados;
        private final int examesPendentes;
        private final int examesClinicos;
        private final int examesClinicosTotal;
        private final int examesComplementares;
        private final int examesComplementaresTotal;
        private final int examesLaboratoriais;
        private final int examesLaboratoriaisTotal;

        public ExameResumo(int totalExames,
                           int examesRealizados,
                           int examesPendentes,
                           int examesClinicos,
                           int examesClinicosTotal,
                           int examesComplementares,
                           int examesComplementaresTotal,
                           int examesLaboratoriais,
                           int examesLaboratoriaisTotal) {

            this.totalExames = totalExames;
            this.examesRealizados = examesRealizados;
            this.examesPendentes = examesPendentes;
            this.examesClinicos = examesClinicos;
            this.examesClinicosTotal = examesClinicosTotal;
            this.examesComplementares = examesComplementares;
            this.examesComplementaresTotal = examesComplementaresTotal;
            this.examesLaboratoriais = examesLaboratoriais;
            this.examesLaboratoriaisTotal = examesLaboratoriaisTotal;
        }

        public int getTotalExames() { return totalExames; }
        public int getExamesRealizados() { return examesRealizados; }
        public int getExamesPendentes() { return examesPendentes; }
        public int getExamesClinicos() { return examesClinicos; }
        public int getExamesClinicosTotal() { return examesClinicosTotal; }
        public int getExamesComplementares() { return examesComplementares; }
        public int getExamesComplementaresTotal() { return examesComplementaresTotal; }
        public int getExamesLaboratoriais() { return examesLaboratoriais; }
        public int getExamesLaboratoriaisTotal() { return examesLaboratoriaisTotal; }
    }
}
