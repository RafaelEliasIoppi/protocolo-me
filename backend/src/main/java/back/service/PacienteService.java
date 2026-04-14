package back.service;

import java.util.List;
import org.springframework.stereotype.Service;
import back.model.Paciente;
import back.repository.PacienteRepository;


@Service
public class PacienteService {

    private final PacienteRepository pacienteRepository;

    public PacienteService(PacienteRepository pacienteRepository) {
        this.pacienteRepository = pacienteRepository;
    }

    public Paciente salvar(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    public Paciente buscarPorId(Long id) {
        return pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
    }

    public Paciente atualizar(Long id, Paciente pacienteAtualizado) {
        Paciente paciente = buscarPorId(id);
        paciente.setNome(pacienteAtualizado.getNome());
        paciente.setCpf(pacienteAtualizado.getCpf());
        paciente.setTelefone(pacienteAtualizado.getTelefone());
        paciente.setStatusProtocolo(pacienteAtualizado.getStatusProtocolo());
        paciente.setHospital(pacienteAtualizado.getHospital());
        return pacienteRepository.save(paciente);
    }

    public void deletar(Long id) {
        Paciente paciente = buscarPorId(id);
        pacienteRepository.delete(paciente);
    }

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente atualizarProtocolo(Long id, String status) {
        Paciente paciente = buscarPorId(id);
        paciente.setStatusProtocolo(status);
        return pacienteRepository.save(paciente);
    }
}
