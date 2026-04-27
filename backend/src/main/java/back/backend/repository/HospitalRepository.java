package back.backend.repository;

import back.backend.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    Optional<Hospital> findByCnpj(String cnpj);
    List<Hospital> findByStatus(Hospital.StatusHospital status);
    List<Hospital> findByCidade(String cidade);
    List<Hospital> findByEstado(String estado);
    List<Hospital> findByCidadeIgnoreCase(String cidade);
    List<Hospital> findByEstadoIgnoreCase(String estado);
}
