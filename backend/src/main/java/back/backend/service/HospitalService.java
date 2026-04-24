package back.backend.service;

import back.backend.dto.HospitalDTO;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.HospitalMapper;
import back.backend.model.Hospital;
import back.backend.repository.HospitalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;

    public HospitalService(HospitalRepository hospitalRepository, HospitalMapper hospitalMapper) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalMapper = hospitalMapper;
    }

    // CREATE
    public HospitalDTO criarHospital(Hospital hospital) {

        hospitalRepository.findByCnpj(hospital.getCnpj())
                .ifPresent(h -> {
                    throw new ConflitoNegocioException("CNPJ já cadastrado: " + hospital.getCnpj());
                });

        return toDTO(hospitalRepository.save(hospital));
    }

    // READ
    public List<HospitalDTO> listarTodos() {
        return hospitalRepository.findAll().stream().map(this::toDTO).toList();
    }

    public Optional<Hospital> buscarPorId(Long id) {
        return hospitalRepository.findById(id);
    }

    public HospitalDTO buscarPorIdOuFalhar(Long id) {
        return toDTO(hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id)));
    }

    public Optional<Hospital> buscarPorCnpj(String cnpj) {
        return hospitalRepository.findByCnpj(cnpj);
    }

    public HospitalDTO buscarPorCnpjOuFalhar(String cnpj) {
        return toDTO(hospitalRepository.findByCnpj(cnpj)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado")));
    }

    public List<HospitalDTO> listarPorStatus(Hospital.StatusHospital status) {
        return hospitalRepository.findByStatus(status).stream().map(this::toDTO).toList();
    }

    public List<HospitalDTO> listarPorStatus(String status) {
        return listarPorStatus(parseStatus(status));
    }

    public List<HospitalDTO> listarPorCidade(String cidade) {
        return hospitalRepository.findByCidade(cidade).stream().map(this::toDTO).toList();
    }

    public List<HospitalDTO> listarPorEstado(String estado) {
        return hospitalRepository.findByEstado(estado).stream().map(this::toDTO).toList();
    }

    // UPDATE
    public HospitalDTO atualizarHospital(Long id, Hospital atualizado) {

        Hospital hospital = hospitalRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id));

        hospital.setNome(atualizado.getNome());
        hospital.setEndereco(atualizado.getEndereco());
        hospital.setCidade(atualizado.getCidade());
        hospital.setEstado(atualizado.getEstado());
        hospital.setTelefone(atualizado.getTelefone());
        hospital.setEmail(atualizado.getEmail());
        hospital.setResponsavelMedico(atualizado.getResponsavelMedico());

        return toDTO(hospitalRepository.save(hospital));
    }

    // PATCH STATUS
    public HospitalDTO alterarStatus(Long id, Hospital.StatusHospital status) {

        Hospital hospital = hospitalRepository.findById(id)
            .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id));

        hospital.setStatus(status);

        return toDTO(hospitalRepository.save(hospital));
    }

    public HospitalDTO alterarStatus(Long id, String status) {
        return alterarStatus(id, parseStatus(status));
    }

    // DELETE
    public void deletarHospital(Long id) {

        if (!hospitalRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Hospital não encontrado: " + id);
        }

        hospitalRepository.deleteById(id);
    }

    private HospitalDTO toDTO(Hospital hospital) {
        return hospitalMapper.toDTO(hospital);
    }

    private Hospital.StatusHospital parseStatus(String status) {
        return Hospital.StatusHospital.valueOf(status.toUpperCase());
    }
}