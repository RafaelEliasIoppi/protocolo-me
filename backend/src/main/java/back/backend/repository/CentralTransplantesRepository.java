package back.backend.repository;

import back.backend.model.CentralTransplantes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface CentralTransplantesRepository extends JpaRepository<CentralTransplantes, Long> {
    Optional<CentralTransplantes> findByCnpj(String cnpj);
    Optional<CentralTransplantes> findByNome(String nome);
    List<CentralTransplantes> findByCidade(String cidade);
    List<CentralTransplantes> findByEstado(String estado);
    List<CentralTransplantes> findByStatusOperacional(CentralTransplantes.StatusCentral status);
}
