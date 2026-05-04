package back.backend.service;

import back.backend.dto.HospitalDTO;
import back.backend.exception.ConflitoNegocioException;
import back.backend.exception.RecursoNaoEncontradoException;
import back.backend.mapper.HospitalMapper;
import back.backend.model.Hospital;
import back.backend.repository.HospitalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final HospitalMapper hospitalMapper;

    public HospitalService(HospitalRepository hospitalRepository, HospitalMapper hospitalMapper) {
        this.hospitalRepository = hospitalRepository;
        this.hospitalMapper = hospitalMapper;
    }

    // ================= CREATE =================
    public HospitalDTO criarHospital(Hospital hospital) {
        validarHospital(hospital);
        String cnpjNormalizado = normalizarCnpj(hospital.getCnpj());

        hospitalRepository.findByCnpj(cnpjNormalizado)
                .ifPresent(h -> {
                    throw new ConflitoNegocioException("CNPJ já cadastrado: " + cnpjNormalizado);
                });

        hospital.setCnpj(cnpjNormalizado);

        if (hospital.getStatus() == null) {
            hospital.setStatus(Hospital.StatusHospital.ATIVO);
        }

        return toDTO(hospitalRepository.save(hospital));
    }

    // ================= READ =================
    public List<HospitalDTO> listarTodos() {
        return hospitalRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    public Optional<Hospital> buscarPorId(Long id) {
        return hospitalRepository.findById(id);
    }

    public HospitalDTO buscarPorIdOuFalhar(Long id) {
        return toDTO(hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id)));
    }

    public Optional<Hospital> buscarPorCnpj(String cnpj) {
        return hospitalRepository.findByCnpj(normalizarCnpj(cnpj));
    }

    public HospitalDTO buscarPorCnpjOuFalhar(String cnpj) {
        return toDTO(hospitalRepository.findByCnpj(normalizarCnpj(cnpj))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado")));
    }

    public List<HospitalDTO> listarPorStatus(Hospital.StatusHospital status) {
        return hospitalRepository.findByStatus(status).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<HospitalDTO> listarPorStatus(String status) {
        return listarPorStatus(parseStatus(status));
    }

    public List<HospitalDTO> listarPorCidade(String cidade) {
        return hospitalRepository.findByCidadeIgnoreCase(cidade.trim()).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<HospitalDTO> listarPorEstado(String estado) {
        return hospitalRepository.findByEstadoIgnoreCase(estado.trim()).stream()
                .map(this::toDTO)
                .toList();
    }

    // ================= UPDATE =================
    public HospitalDTO atualizarHospital(Long id, Hospital atualizado) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id));

        // update parcial seguro
        if (atualizado.getNome() != null) hospital.setNome(atualizado.getNome());
        if (atualizado.getEndereco() != null) hospital.setEndereco(atualizado.getEndereco());
        if (atualizado.getCidade() != null) hospital.setCidade(atualizado.getCidade());
        if (atualizado.getEstado() != null) hospital.setEstado(atualizado.getEstado());
        if (atualizado.getTelefone() != null) hospital.setTelefone(atualizado.getTelefone());
        if (atualizado.getEmail() != null) hospital.setEmail(atualizado.getEmail());
        if (atualizado.getResponsavelMedico() != null) hospital.setResponsavelMedico(atualizado.getResponsavelMedico());

        return toDTO(hospitalRepository.save(hospital));
    }

    // ================= STATUS =================
    public HospitalDTO alterarStatus(Long id, Hospital.StatusHospital status) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Hospital não encontrado: " + id));

        hospital.setStatus(status);
        return toDTO(hospitalRepository.save(hospital));
    }

    public HospitalDTO alterarStatus(Long id, String status) {
        return alterarStatus(id, parseStatus(status));
    }

    // ================= DELETE =================
    public void deletarHospital(Long id) {
        if (!hospitalRepository.existsById(id)) {
            throw new RecursoNaoEncontradoException("Hospital não encontrado: " + id);
        }
        hospitalRepository.deleteById(id);
    }

    // ================= HELPERS =================
    private void validarHospital(Hospital hospital) {
        if (hospital.getNome() == null || hospital.getNome().isBlank()) {
            throw new IllegalArgumentException("Nome do hospital é obrigatório");
        }
        if (hospital.getCnpj() == null || hospital.getCnpj().isBlank()) {
            throw new IllegalArgumentException("CNPJ é obrigatório");
        }
    }

    private String normalizarCnpj(String cnpj) {
        if (cnpj == null || cnpj.isBlank()) {
            throw new IllegalArgumentException("CNPJ obrigatório");
        }
        String n = cnpj.replaceAll("\\D", "");
        if (n.length() != 14) {
            throw new IllegalArgumentException("CNPJ inválido");
        }
        return n.replaceAll("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d{2})", "$1.$2.$3/$4-$5");
    }

    private HospitalDTO toDTO(Hospital hospital) {
        return hospitalMapper.toDTO(hospital);
    }

    private Hospital.StatusHospital parseStatus(String status) {
        try {
            return Hospital.StatusHospital.valueOf(status.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Status inválido: " + status);
        }
    }
}
