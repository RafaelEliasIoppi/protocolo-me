package back.backend.service;

import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import back.backend.repository.ExameMERepository;
import back.backend.repository.ProtocoloMERepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ExameMEService {

    @Autowired
    private ExameMERepository exameRepository;

    @Autowired
    private ProtocoloMERepository protocoloRepository;

    // Criar exame
    public ExameME criarExame(ExameME exame) {
        if (exame.getDataRealizacao() == null) {
            exame.setDataRealizacao(LocalDateTime.now());
        }
        return exameRepository.save(exame);
    }

    // Listar exames de um protocolo
    public List<ExameME> listarExamesPorProtocolo(Long protocoloId) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        return exameRepository.findByProtocoloMEOrderByDataRealizacaoDesc(protocolo);
    }

    // Listar exames por categoria
    public List<ExameME> listarExamePorCategoria(Long protocoloId, ExameME.CategoriaExame categoria) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));
        return exameRepository.findByProtocoloMEAndCategoria(protocolo, categoria);
    }

    // Listar exames clínicos
    public List<ExameME> listarExamesClinico(Long protocoloId) {
        return listarExamePorCategoria(protocoloId, ExameME.CategoriaExame.CLINICO);
    }

    // Listar exames complementares
    public List<ExameME> listarExamesComplementares(Long protocoloId) {
        return listarExamePorCategoria(protocoloId, ExameME.CategoriaExame.COMPLEMENTAR);
    }

    // Listar exames laboratoriais
    public List<ExameME> listarExamesLaboratoriais(Long protocoloId) {
        return listarExamePorCategoria(protocoloId, ExameME.CategoriaExame.LABORATORIAL);
    }

    // Buscar exame por ID
    public Optional<ExameME> buscarPorId(Long id) {
        return exameRepository.findById(id);
    }

    // Atualizar exame
    public ExameME atualizarExame(Long id, ExameME exameAtualizado) {
        ExameME exame = exameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        exame.setResultado(exameAtualizado.getResultado());
        exame.setResultado_positivo(exameAtualizado.getResultado_positivo());
        exame.setResponsavel(exameAtualizado.getResponsavel());
        exame.setObservacoes(exameAtualizado.getObservacoes());

        return exameRepository.save(exame);
    }

    // Registrar resultado
    public ExameME registrarResultado(Long id, String resultado, Boolean resultado_positivo, String responsavel) {
        ExameME exame = exameRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exame não encontrado"));

        exame.setResultado(resultado);
        exame.setResultado_positivo(resultado_positivo);
        exame.setResponsavel(responsavel);
        exame.setDataRealizacao(LocalDateTime.now());

        return exameRepository.save(exame);
    }

    // Deletar exame
    public void deletarExame(Long id) {
        if (!exameRepository.existsById(id)) {
            throw new RuntimeException("Exame não encontrado");
        }
        exameRepository.deleteById(id);
    }

    // Obter resumo dos exames de um protocolo
    public ExameResumo obterResumoExames(Long protocoloId) {
        ProtocoloME protocolo = protocoloRepository.findById(protocoloId)
                .orElseThrow(() -> new RuntimeException("Protocolo não encontrado"));

        List<ExameME> exames = exameRepository.findByProtocoloME(protocolo);
        
        ExameResumo resumo = new ExameResumo();
        resumo.setTotalExames(exames.size());
        resumo.setExamesRealizados((int) exames.stream().filter(e -> e.getDataRealizacao() != null).count());
        resumo.setExames_Clinicos((int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO).count());
        resumo.setExamesComplementares((int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR).count());
        resumo.setExamesLaboratoriais((int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL).count());

        return resumo;
    }

    // Classes auxiliares
    public static class ExameResumo {
        private int totalExames;
        private int examesRealizados;
        private int exames_Clinicos;
        private int examesComplementares;
        private int examesLaboratoriais;

        // Getters e Setters
        public int getTotalExames() { return totalExames; }
        public void setTotalExames(int totalExames) { this.totalExames = totalExames; }

        public int getExamesRealizados() { return examesRealizados; }
        public void setExamesRealizados(int examesRealizados) { this.examesRealizados = examesRealizados; }

        public int getExames_Clinicos() { return exames_Clinicos; }
        public void setExames_Clinicos(int exames_Clinicos) { this.exames_Clinicos = exames_Clinicos; }

        public int getExamesComplementares() { return examesComplementares; }
        public void setExamesComplementares(int examesComplementares) { this.examesComplementares = examesComplementares; }

        public int getExamesLaboratoriais() { return examesLaboratoriais; }
        public void setExamesLaboratoriais(int examesLaboratoriais) { this.examesLaboratoriais = examesLaboratoriais; }
    }
}
