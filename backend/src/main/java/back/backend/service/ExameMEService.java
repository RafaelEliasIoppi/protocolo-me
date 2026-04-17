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

    @Autowired(required = false)
    private ProtocoloMEService protocoloMEService;

    // Criar exame
    public ExameME criarExame(ExameME exame) {
        // Exame criado sem resultado deve permanecer pendente.
        // A data de realização é preenchida quando o resultado for registrado.
        if (exame.getResultado() == null || exame.getResultado().trim().isEmpty()) {
            exame.setDataRealizacao(null);
        } else if (exame.getDataRealizacao() == null) {
            exame.setDataRealizacao(LocalDateTime.now());
        }
        ExameME exameSalvo = exameRepository.save(exame);
        
        // Atualizar o status do protocolo automaticamente
        if (exameSalvo.getProtocoloME() != null && protocoloMEService != null) {
            protocoloMEService.atualizarStatusAutomatico(exameSalvo.getProtocoloME().getId());
        }
        
        return exameSalvo;
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

        ExameME exameSalvo = exameRepository.save(exame);
        
        // Atualizar o status do protocolo automaticamente
        if (exameSalvo.getProtocoloME() != null && protocoloMEService != null) {
            protocoloMEService.atualizarStatusAutomatico(exameSalvo.getProtocoloME().getId());
        }

        return exameSalvo;
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
        int totalExames = exames.size();
        int examesRealizados = (int) exames.stream().filter(e -> e.getDataRealizacao() != null).count();
        int examesPendentes = totalExames - examesRealizados;

        int examesClinicosTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO).count();
        int examesComplementaresTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR).count();
        int examesLaboratoriaisTotal = (int) exames.stream().filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL).count();

        int examesClinicosRealizados = (int) exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.CLINICO && e.getDataRealizacao() != null)
            .count();
        int examesComplementaresRealizados = (int) exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.COMPLEMENTAR && e.getDataRealizacao() != null)
            .count();
        int examesLaboratoriaisRealizados = (int) exames.stream()
            .filter(e -> e.getCategoria() == ExameME.CategoriaExame.LABORATORIAL && e.getDataRealizacao() != null)
            .count();

        resumo.setTotalExames(totalExames);
        resumo.setExamesRealizados(examesRealizados);
        resumo.setExamesPendentes(examesPendentes);
        resumo.setExames_Clinicos(examesClinicosRealizados);
        resumo.setExamesClinicosTotal(examesClinicosTotal);
        resumo.setExamesComplementares(examesComplementaresRealizados);
        resumo.setExamesComplementaresTotal(examesComplementaresTotal);
        resumo.setExamesLaboratoriais(examesLaboratoriaisRealizados);
        resumo.setExamesLaboratoriaisTotal(examesLaboratoriaisTotal);

        return resumo;
    }

    // Classes auxiliares
    public static class ExameResumo {
        private int totalExames;
        private int examesRealizados;
        private int examesPendentes;
        private int exames_Clinicos;
        private int examesClinicosTotal;
        private int examesComplementares;
        private int examesComplementaresTotal;
        private int examesLaboratoriais;
        private int examesLaboratoriaisTotal;

        // Getters e Setters
        public int getTotalExames() { return totalExames; }
        public void setTotalExames(int totalExames) { this.totalExames = totalExames; }

        public int getExamesRealizados() { return examesRealizados; }
        public void setExamesRealizados(int examesRealizados) { this.examesRealizados = examesRealizados; }

        public int getExamesPendentes() { return examesPendentes; }
        public void setExamesPendentes(int examesPendentes) { this.examesPendentes = examesPendentes; }

        public int getExames_Clinicos() { return exames_Clinicos; }
        public void setExames_Clinicos(int exames_Clinicos) { this.exames_Clinicos = exames_Clinicos; }

        public int getExamesClinicosTotal() { return examesClinicosTotal; }
        public void setExamesClinicosTotal(int examesClinicosTotal) { this.examesClinicosTotal = examesClinicosTotal; }

        public int getExamesComplementares() { return examesComplementares; }
        public void setExamesComplementares(int examesComplementares) { this.examesComplementares = examesComplementares; }

        public int getExamesComplementaresTotal() { return examesComplementaresTotal; }
        public void setExamesComplementaresTotal(int examesComplementaresTotal) { this.examesComplementaresTotal = examesComplementaresTotal; }

        public int getExamesLaboratoriais() { return examesLaboratoriais; }
        public void setExamesLaboratoriais(int examesLaboratoriais) { this.examesLaboratoriais = examesLaboratoriais; }

        public int getExamesLaboratoriaisTotal() { return examesLaboratoriaisTotal; }
        public void setExamesLaboratoriaisTotal(int examesLaboratoriaisTotal) { this.examesLaboratoriaisTotal = examesLaboratoriaisTotal; }
    }
}
