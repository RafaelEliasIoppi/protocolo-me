package back.backend.repository;

import back.backend.model.OrgaoDoado;
import back.backend.model.ProtocoloME;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrgaoDoadoRepository extends JpaRepository<OrgaoDoado, Long> {
    List<OrgaoDoado> findByProtocoloME(ProtocoloME protocoloME);
    List<OrgaoDoado> findByProtocoloMEId(Long protocoloMEId);
}
