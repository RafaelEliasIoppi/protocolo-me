package back.backend.repository;

import back.backend.model.ProtocoloME;
import back.backend.model.CentralTransplantes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ProtocoloMERepository extends JpaRepository<ProtocoloME, Long> {
    Optional<ProtocoloME> findByNumeroProtocolo(String numeroProtocolo);
    List<ProtocoloME> findByCentralTransplantes(CentralTransplantes centralTransplantes);
    List<ProtocoloME> findByStatus(ProtocoloME.StatusProtocoloME status);
    List<ProtocoloME> findByCentralTransplantesAndStatus(CentralTransplantes centralTransplantes, ProtocoloME.StatusProtocoloME status);
    List<ProtocoloME> findByDataNotificacaoBetween(LocalDateTime dataInicio, LocalDateTime dataFim);
    List<ProtocoloME> findByHospitalOrigem(String hospitalOrigem);
    List<ProtocoloME> findByPacienteId(Long pacienteId);
}
