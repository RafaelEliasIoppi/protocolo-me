package back.backend.service;

import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.model.Hospital;
import back.backend.repository.HospitalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    // CREATE
    public Hospital criarHospital(Hospital hospital) {

        hospitalRepository.findByCnpj(hospital.getCnpj())
                .ifPresent(h -> {
                    throw new RuntimeException("CNPJ já cadastrado: " + hospital.getCnpj());
                });

        return hospitalRepository.save(hospital);
    }

    // READ
    public List<Hospital> listarTodos() {
        return hospitalRepository.findAll();
    }

    public Optional<Hospital> buscarPorId(Long id) {
        return hospitalRepository.findById(id);
    }

    public Hospital buscarPorIdOuFalhar(Long id) {
        return hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id));
    }

    public Optional<Hospital> buscarPorCnpj(String cnpj) {
        return hospitalRepository.findByCnpj(cnpj);
    }

    public Hospital buscarPorCnpjOuFalhar(String cnpj) {
        return hospitalRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado"));
    }

    public List<Hospital> listarPorStatus(Hospital.StatusHospital status) {
        return hospitalRepository.findByStatus(status);
    }

    public List<Hospital> listarPorCidade(String cidade) {
        return hospitalRepository.findByCidade(cidade);
    }

    public List<Hospital> listarPorEstado(String estado) {
        return hospitalRepository.findByEstado(estado);
    }

    // UPDATE
    public Hospital atualizarHospital(Long id, Hospital atualizado) {

        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado: " + id));

        hospital.setNome(atualizado.getNome());
        hospital.setEndereco(atualizado.getEndereco());
        hospital.setCidade(atualizado.getCidade());
        hospital.setEstado(atualizado.getEstado());
        hospital.setTelefone(atualizado.getTelefone());
        hospital.setEmail(atualizado.getEmail());
        hospital.setResponsavelMedico(atualizado.getResponsavelMedico());

        return hospitalRepository.save(hospital);
    }

    // PATCH STATUS
    public Hospital alterarStatus(Long id, Hospital.StatusHospital status) {

        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hospital não encontrado: " + id));

        hospital.setStatus(status);

        return hospitalRepository.save(hospital);
    }

    // DELETE
    public void deletarHospital(Long id) {

        if (!hospitalRepository.existsById(id)) {
            throw new RuntimeException("Hospital não encontrado: " + id);
        }

        hospitalRepository.deleteById(id);
    }
}