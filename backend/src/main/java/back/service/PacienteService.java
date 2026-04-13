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

    public List<Paciente> listarTodos() {
        return pacienteRepository.findAll();
    }

    public Paciente atualizarProtocolo(Long id, String status) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente não encontrado"));
        paciente.setStatusProtocolo(status);
        return pacienteRepository.save(paciente);
    }
}
