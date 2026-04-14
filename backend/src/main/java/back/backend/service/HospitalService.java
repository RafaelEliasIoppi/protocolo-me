package back.backend.service;

import back.backend.model.Hospital;
import back.backend.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;

    // Criar hospital
    public Hospital criarHospital(Hospital hospital) {
        if (hospitalRepository.findByCnpj(hospital.getCnpj()).isPresent()) {
            throw new RuntimeException("Hospital com CNPJ " + hospital.getCnpj() + " já existe");
        }
        return hospitalRepository.save(hospital);
    }

    // Listar todos os hospitais
    public List<Hospital> listarTodos() {
        return hospitalRepository.findAll();
    }

    // Buscar por ID
    public Optional<Hospital> buscarPorId(Long id) {
        return hospitalRepository.findById(id);
    }

    // Buscar por CNPJ
    public Optional<Hospital> buscarPorCnpj(String cnpj) {
        return hospitalRepository.findByCnpj(cnpj);
    }

    // Listar por status
    public List<Hospital> listarPorStatus(Hospital.StatusHospital status) {
        return hospitalRepository.findByStatus(status);
    }

    // Listar por cidade
    public List<Hospital> listarPorCidade(String cidade) {
        return hospitalRepository.findByCidade(cidade);
    }

    // Listar por estado
    public List<Hospital> listarPorEstado(String estado) {
        return hospitalRepository.findByEstado(estado);
    }

    // Atualizar hospital
    public Hospital atualizarHospital(Long id, Hospital hospitalAtualizado) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado com ID: " + id));

        hospital.setNome(hospitalAtualizado.getNome());
        hospital.setEndereco(hospitalAtualizado.getEndereco());
        hospital.setCidade(hospitalAtualizado.getCidade());
        hospital.setEstado(hospitalAtualizado.getEstado());
        hospital.setTelefone(hospitalAtualizado.getTelefone());
        hospital.setEmail(hospitalAtualizado.getEmail());
        hospital.setResponsavelMedico(hospitalAtualizado.getResponsavelMedico());

        return hospitalRepository.save(hospital);
    }

    // Alterar status (por equipe médica)
    public Hospital alterarStatus(Long id, Hospital.StatusHospital novoStatus) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado com ID: " + id));

        hospital.setStatus(novoStatus);
        return hospitalRepository.save(hospital);
    }

    // Deletar hospital
    public void deletarHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new RuntimeException("Hospital não encontrado com ID: " + id);
        }
        hospitalRepository.deleteById(id);
    }
}
