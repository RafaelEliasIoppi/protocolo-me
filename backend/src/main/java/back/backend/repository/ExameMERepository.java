package back.backend.repository;

import back.backend.model.ExameME;
import back.backend.model.ProtocoloME;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExameMERepository extends JpaRepository<ExameME, Long> {
    List<ExameME> findByProtocoloME(ProtocoloME protocoloME);
    List<ExameME> findByProtocoloMEAndCategoria(ProtocoloME protocoloME, ExameME.CategoriaExame categoria);
    List<ExameME> findByProtocoloMEAndTipoExame(ProtocoloME protocoloME, ExameME.TipoExame tipoExame);
    Optional<ExameME> findFirstByProtocoloME_IdAndTipoExame(Long protocoloMEId, ExameME.TipoExame tipoExame);
    List<ExameME> findByProtocoloMEOrderByDataRealizacaoDesc(ProtocoloME protocoloME);
}
