package back.backend.service;

import back.backend.model.Paciente;
import back.backend.model.Hospital;
import back.backend.repository.PacienteRepository;
import back.backend.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private HospitalRepository hospitalRepository;

    /**
     * Criar novo paciente
     */
    public Paciente criarPaciente(Paciente paciente) {
        if (paciente.getStatus() == null || paciente.getStatus() == Paciente.StatusPaciente.EM_PROTOCOLO_ME) {
            paciente.setStatus(Paciente.StatusPaciente.INTERNADO);
        }
        preencherHospitalOrigemSeNecessario(paciente);
        validarPaciente(paciente);
        return pacienteRepository.save(paciente);
    }

    /**
     * Atualizar dados do paciente
     */
    public Paciente atualizarPaciente(Long id, Paciente pacienteAtualizado) {
        Paciente paciente = obterPacientePorId(id);

        if (pacienteAtualizado.getHospital() != null) {
            paciente.setHospital(pacienteAtualizado.getHospital());
        }
        
        if (pacienteAtualizado.getNome() != null) paciente.setNome(pacienteAtualizado.getNome());
        if (pacienteAtualizado.getDataNascimento() != null) paciente.setDataNascimento(pacienteAtualizado.getDataNascimento());
        if (pacienteAtualizado.getGenero() != null) paciente.setGenero(pacienteAtualizado.getGenero());
        if (pacienteAtualizado.getLeito() != null) paciente.setLeito(pacienteAtualizado.getLeito());
        if (pacienteAtualizado.getDiagnosticoPrincipal() != null) paciente.setDiagnosticoPrincipal(pacienteAtualizado.getDiagnosticoPrincipal());
        if (pacienteAtualizado.getHistoricoMedico() != null) paciente.setHistoricoMedico(pacienteAtualizado.getHistoricoMedico());
        if (pacienteAtualizado.getNomeResponsavel() != null) paciente.setNomeResponsavel(pacienteAtualizado.getNomeResponsavel());
        if (pacienteAtualizado.getTelefoneResponsavel() != null) paciente.setTelefoneResponsavel(pacienteAtualizado.getTelefoneResponsavel());
        if (pacienteAtualizado.getEmailResponsavel() != null) paciente.setEmailResponsavel(pacienteAtualizado.getEmailResponsavel());
        if (pacienteAtualizado.getStatus() != null) paciente.setStatus(pacienteAtualizado.getStatus());

        preencherHospitalOrigemSeNecessario(paciente);
        
        return pacienteRepository.save(paciente);
    }

    /**
     * Atualizar status do paciente
     */
    public Paciente atualizarStatus(Long id, Paciente.StatusPaciente novoStatus) {
        Paciente paciente = obterPacientePorId(id);
        paciente.setStatus(novoStatus);
        return pacienteRepository.save(paciente);
    }

    /**
     * Obter paciente por ID
     */
    public Paciente obterPacientePorId(Long id) {
        return pacienteRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com ID: " + id));
    }

    /**
     * Obter paciente por CPF
     */
    public Paciente obterPacientePorCpf(String cpf) {
        return pacienteRepository.findByCpf(cpf)
            .orElseThrow(() -> new IllegalArgumentException("Paciente não encontrado com CPF: " + cpf));
    }

    /**
     * Listar todos os pacientes
     */
    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    /**
     * Listar pacientes por hospital
     */
    public List<Paciente> listarPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospital(hospital);
    }

    /**
     * Listar pacientes por status
     */
    public List<Paciente> listarPorStatus(Paciente.StatusPaciente status) {
        return pacienteRepository.findByStatus(status);
    }

    /**
     * Listar pacientes por hospital e status
     */
    public List<Paciente> listarPorHospitalEStatus(Long hospitalId, Paciente.StatusPaciente status) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospitalAndStatus(hospital, status);
    }

    /**
     * Procurar pacientes por nome
     */
    public List<Paciente> procurarPorNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode estar vazio");
        }
        return pacienteRepository.findByNomeContainingIgnoreCase(nome);
    }

    /**
     * Procurar pacientes por nome em um hospital específico
     */
    public List<Paciente> procurarPorNomeEHospital(Long hospitalId, String nome) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findByHospitalAndNomeContainingIgnoreCase(hospital, nome);
    }

    /**
     * Listar apenas pacientes que já entraram em Protocolo de ME
     */
    public List<Paciente> listarPacientesEmProtocoloME() {
        return pacienteRepository.findPacientesEmProtocoloME();
    }

    /**
     * Listar apenas pacientes em Protocolo de ME de um hospital específico
     */
    public List<Paciente> listarPacientesEmProtocoloMEPorHospital(Long hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
            .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado com ID: " + hospitalId));
        return pacienteRepository.findPacientesEmProtocoloMEPorHospital(hospital);
    }

    /**
     * Deletar paciente
     */
    public void deletarPaciente(Long id) {
        Paciente paciente = obterPacientePorId(id);
        pacienteRepository.delete(paciente);
    }

    /**
     * Obter estatísticas de pacientes por status
     */
    public PacienteStatisticas obterEstatisticas() {
        PacienteStatisticas stats = new PacienteStatisticas();
        stats.setTotalPacientes(pacienteRepository.count());
        stats.setPacientesInternados(pacienteRepository.countByStatus(Paciente.StatusPaciente.INTERNADO));
        stats.setPacientesEmProtocoloME(pacienteRepository.countByStatus(Paciente.StatusPaciente.EM_PROTOCOLO_ME));
        stats.setPacientesAptosTransplante(pacienteRepository.countByStatus(Paciente.StatusPaciente.APTO_TRANSPLANTE));
        stats.setPacientesNaoAptos(pacienteRepository.countByStatus(Paciente.StatusPaciente.NAO_APTO));
        return stats;
    }

    /**
     * Validar dados do paciente
     */
    private void validarPaciente(Paciente paciente) {
        if (paciente.getNome() == null || paciente.getNome().trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do paciente é obrigatório");
        }
        
        if (paciente.getCpf() == null || paciente.getCpf().trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do paciente é obrigatório");
        }
        
        if (paciente.getDataNascimento() == null) {
            throw new IllegalArgumentException("Data de nascimento é obrigatória");
        }
        
        if (paciente.getDataNascimento().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento não pode ser no futuro");
        }
        
        if (paciente.getGenero() == null) {
            throw new IllegalArgumentException("Gênero é obrigatório");
        }
        
        if (paciente.getHospital() == null || paciente.getHospital().getId() == null) {
            throw new IllegalArgumentException("Hospital é obrigatório");
        }
        
        // Validar se CPF já existe (exceto se for update do próprio paciente)
        Optional<Paciente> existente = pacienteRepository.findByCpf(paciente.getCpf());
        if (existente.isPresent() && !existente.get().getId().equals(paciente.getId())) {
            throw new IllegalArgumentException("CPF já está registrado no sistema");
        }
    }

    private void preencherHospitalOrigemSeNecessario(Paciente paciente) {
        if (paciente == null) {
            return;
        }

        boolean hospitalOrigemVazio = paciente.getHospitalOrigem() == null || paciente.getHospitalOrigem().trim().isEmpty();
        if (hospitalOrigemVazio && paciente.getHospital() != null) {
            Long hospitalId = paciente.getHospital().getId();
            if (hospitalId != null) {
                Hospital hospital = hospitalRepository.findById(hospitalId)
                    .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado com ID: " + hospitalId));
                paciente.setHospitalOrigem(hospital.getNome());
                paciente.setHospital(hospital);
            }
        }
    }

    // Inner class para estatísticas
    public static class PacienteStatisticas {
        private long totalPacientes;
        private long pacientesInternados;
        private long pacientesEmProtocoloME;
        private long pacientesAptosTransplante;
        private long pacientesNaoAptos;

        // Getters e Setters
        public long getTotalPacientes() { return totalPacientes; }
        public void setTotalPacientes(long total) { this.totalPacientes = total; }

        public long getPacientesInternados() { return pacientesInternados; }
        public void setPacientesInternados(long qt) { this.pacientesInternados = qt; }

        public long getPacientesEmProtocoloME() { return pacientesEmProtocoloME; }
        public void setPacientesEmProtocoloME(long qt) { this.pacientesEmProtocoloME = qt; }

        public long getPacientesAptosTransplante() { return pacientesAptosTransplante; }
        public void setPacientesAptosTransplante(long qt) { this.pacientesAptosTransplante = qt; }

        public long getPacientesNaoAptos() { return pacientesNaoAptos; }
        public void setPacientesNaoAptos(long qt) { this.pacientesNaoAptos = qt; }
    }

}
