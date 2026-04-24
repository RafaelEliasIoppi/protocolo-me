package back.backend.service;

import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.ExameME;
import back.backend.repository.ExameMERepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExameMEService {

    private final ExameMERepository exameRepository;

    public ExameMEService(ExameMERepository exameRepository) {
        this.exameRepository = exameRepository;
    }

    public ExameME criarExame(ExameME exame) {
        exame.setDataCriacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());
        return exameRepository.save(exame);
    }

    public List<ExameME> listarExamesPorProtocolo(Long protocoloId) {
        return exameRepository.findByProtocoloME_Id(protocoloId);
    }

    public List<ExameME> listarExamesClinico(Long protocoloId) {
        return exameRepository.findByProtocoloME_IdAndCategoria(protocoloId, ExameME.CategoriaExame.CLINICO);
    }

    public List<ExameME> listarExamesComplementares(Long protocoloId) {
        return exameRepository.findByProtocoloME_IdAndCategoria(protocoloId, ExameME.CategoriaExame.COMPLEMENTAR);
    }

    public List<ExameME> listarExamesLaboratoriais(Long protocoloId) {
        return exameRepository.findByProtocoloME_IdAndCategoria(protocoloId, ExameME.CategoriaExame.LABORATORIAL);
    }

    public Optional<ExameME> buscarPorId(Long id) {
        return exameRepository.findById(id);
    }

    public ExameME buscarPorIdOuFalhar(Long id) {
        return exameRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado"));
    }

    public ExameME atualizarExame(Long id, ExameME dados) {
        ExameME exame = exameRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado"));

        exame.setDescricao(dados.getDescricao());
        exame.setCategoria(dados.getCategoria());
        exame.setTipoExame(dados.getTipoExame());
        exame.setObservacoes(dados.getObservacoes());
        exame.setDataRealizacao(dados.getDataRealizacao());
        exame.setResponsavel(dados.getResponsavel());
        exame.setDataAtualizacao(LocalDateTime.now());

        return exameRepository.save(exame);
    }

    public ExameME registrarResultado(Long id, String resultado, Boolean resultadoPositivo, String responsavel) {
        ExameME exame = exameRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Exame não encontrado"));

        exame.setResultado(resultado);
        exame.setResultado_positivo(resultadoPositivo);
        exame.setResponsavel(responsavel);
        exame.setDataRealizacao(LocalDateTime.now());
        exame.setDataAtualizacao(LocalDateTime.now());

        return exameRepository.save(exame);
    }

    public void deletarExame(Long id) {
        if (!exameRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Exame não encontrado");
        }
        exameRepository.deleteById(id);
    }

    public ExameResumo obterResumoExames(Long protocoloId) {
        List<ExameME> exames = exameRepository.findByProtocoloME_Id(protocoloId);

        int total = exames.size();
        int realizados = (int) exames.stream().filter(e -> e.getResultado() != null).count();
        int pendentes = total - realizados;

        int clinicosTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO).count();
        int clinicosRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO)
                .filter(e -> e.getResultado() != null)
                .count();

        int complementaresTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR).count();
        int complementaresRealizados = (int) exames.stream()
                .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR)
                .filter(e -> e.getResultado() != null)
                .count();

        int laboratoriaisTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL).count();
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

    public static class ExameResumo {

        private final int totalExames;
        private final int examesRealizados;
        private final int examesPendentes;
        private final int exames_Clinicos;
        private final int examesClinicosTotal;
        private final int examesComplementares;
        private final int examesComplementaresTotal;
        private final int examesLaboratoriais;
        private final int examesLaboratoriaisTotal;

        public ExameResumo(int totalExames,
                           int examesRealizados,
                           int examesPendentes,
                           int exames_Clinicos,
                           int examesClinicosTotal,
                           int examesComplementares,
                           int examesComplementaresTotal,
                           int examesLaboratoriais,
                           int examesLaboratoriaisTotal) {
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