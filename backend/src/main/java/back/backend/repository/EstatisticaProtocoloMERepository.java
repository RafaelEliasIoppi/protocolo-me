package back.backend.repository;

import back.backend.model.EstatisticaProtocoloME;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstatisticaProtocoloMERepository extends JpaRepository<EstatisticaProtocoloME, Long> {
    Optional<EstatisticaProtocoloME> findByProtocoloMEId(Long protocoloMEId);
    void deleteByProtocoloMEId(Long protocoloMEId);
    List<EstatisticaProtocoloME> findByAnoCompetencia(Integer anoCompetencia);
    List<EstatisticaProtocoloME> findByAnoCompetenciaAndMesCompetencia(Integer anoCompetencia, Integer mesCompetencia);
}
