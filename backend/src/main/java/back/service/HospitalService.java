package back.service;

import java.util.List;
import org.springframework.stereotype.Service;
import back.model.Hospital;
import back.repository.HospitalRepository;  


@Service
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public HospitalService(HospitalRepository hospitalRepository) {
        this.hospitalRepository = hospitalRepository;
    }

    public Hospital salvar(Hospital hospital) {
        return hospitalRepository.save(hospital);
    }

    public List<Hospital> listarTodos() {
        return hospitalRepository.findAll();
    }
}
