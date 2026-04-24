package back.backend.service;

import back.backend.model.ExameME;
import back.backend.repository.ExameMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExameMEService {

    @Autowired
    private ExameMERepository repository;

    // =========================
    // CREATE
    // =========================
    public ExameME criarExame(ExameME exame) {
        exame.setDataCriacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());
        return repository.save(exame);
    }

    // =========================
    // READ
    // =========================
    public List<ExameME> listarExamesPorProtocolo(Long protocoloId) {
        return repository.findByProtocoloME_Id(protocoloId);
    }

    public List<ExameME> listarExamesClinico(Long protocoloId) {
        return repository.findByProtocoloME_IdAndCategoria(
                protocoloId,
                ExameME.CategoriaExame.CLINICO
        );
    }

    public List<ExameME> listarExamesComplementares(Long protocoloId) {
        return repository.findByProtocoloME_IdAndCategoria(
                protocoloId,
                ExameME.CategoriaExame.COMPLEMENTAR
        );
    }

    public List<ExameME> listarExamesLaboratoriais(Long protocoloId) {
        return repository.findByProtocoloME_IdAndCategoria(
                protocoloId,
                ExameME.CategoriaExame.LABORATORIAL
        );
    }

    public Optional<ExameME> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // =========================
    // UPDATE
    // =========================
    public ExameME atualizarExame(Long id, ExameME dados) {
        ExameME exame = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        exame.setDescricao(dados.getDescricao());
        exame.setCategoria(dados.getCategoria());
        exame.setTipoExame(dados.getTipoExame());
        exame.setObservacoes(dados.getObservacoes());
        exame.setDataRealizacao(dados.getDataRealizacao());
        exame.setResponsavel(dados.getResponsavel());

        exame.setDataAtualizacao(LocalDateTime.now());

        return repository.save(exame);
    }

    // =========================
    // RESULTADO
    // =========================
    public ExameME registrarResultado(
            Long id,
            String resultado,
            Boolean resultadoPositivo,
            String responsavel
    ) {
        ExameME exame = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        exame.setResultado(resultado);
        exame.setResultado_positivo(resultadoPositivo);
        exame.setResponsavel(responsavel);
        exame.setDataRealizacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());

        return repository.save(exame);
    }

    // =========================
    // DELETE
    // =========================
    public void deletarExame(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Exame não encontrado");
        }
        repository.deleteById(id);
    }

    // =========================
    // RESUMO
    // =========================
    public ExameResumo obterResumoExames(Long protocoloId) {

        List<ExameME> exames = repository.findByProtocoloME_Id(protocoloId);

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

        return new ExameResumo(
                total,
                realizados,
                pendentes,
                clinicosRealizados,
                clinicosTotal,
                complementaresRealizados,
                complementaresTotal,
                laboratoriaisRealizados,
                laboratoriaisTotal
        );
    }

    // =========================
    // DTO INTERNO
    // =========================
    public static class ExameResumo {

        private int totalExames;
        private int examesRealizados;
        private int examesPendentes;

        private int exames_Clinicos; // legado
        private int examesClinicosTotal;

        private int examesComplementares;
        private int examesComplementaresTotal;

        private int examesLaboratoriais;
        private int examesLaboratoriaisTotal;

        public ExameResumo(int totalExames, int examesRealizados, int examesPendentes,
                           int exames_Clinicos, int examesClinicosTotal,
                           int examesComplementares, int examesComplementaresTotal,
                           int examesLaboratoriais, int examesLaboratoriaisTotal) {

            this.totalExames = totalExames;
            this.examesRealizados = examesRealizados;
            this.examesPendentes = examesPendentes;

            this.exames_Clinicos = exames_Clinicos;
            this.examesClinicosTotal = examesClinicosTotal;

            this.examesComplementares = examesComplementares;
            this.examesComplementaresTotal = examesComplementaresTotal;

            this.examesLaboratoriais = examesLaboratoriais;
            this.examesLaboratoriaisTotal = examesLaboratoriaisTotal;
        }

        // GETTERS
        public int getTotalExames() { return totalExames; }
        public int getExamesRealizados() { return examesRealizados; }
        public int getExamesPendentes() { return examesPendentes; }

        public int getExames_Clinicos() { return exames_Clinicos; }
        public int getExamesClinicosTotal() { return examesClinicosTotal; }

        public int getExamesComplementares() { return examesComplementares; }
        public int getExamesComplementaresTotal() { return examesComplementaresTotal; }

        public int getExamesLaboratoriais() { return examesLaboratoriais; }
        public int getExamesLaboratoriaisTotal() { return examesLaboratoriaisTotal; }
    }
}