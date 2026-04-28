package back.backend.repository;

import back.backend.model.ProtocoloME;
import back.backend.model.CentralTransplantes;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface ProtocoloMERepository extends JpaRepository<ProtocoloME, Long> {

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    @Query("SELECT DISTINCT p FROM ProtocoloME p")
    List<ProtocoloME> findAllWithDetalhes();

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    @Query("SELECT p FROM ProtocoloME p WHERE p.id = :id")
    Optional<ProtocoloME> findByIdWithDetalhes(@Param("id") Long id);

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    Optional<ProtocoloME> findByNumeroProtocolo(String numeroProtocolo);

    boolean existsByNumeroProtocoloAndIdNot(String numeroProtocolo, Long id);

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    @Query("SELECT DISTINCT p FROM ProtocoloME p WHERE p.centralTransplantes = :centralTransplantes")
    List<ProtocoloME> findByCentralTransplantes(CentralTransplantes centralTransplantes);

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    List<ProtocoloME> findByStatus(ProtocoloME.StatusProtocoloME status);

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    @Query("SELECT DISTINCT p FROM ProtocoloME p WHERE p.centralTransplantes = :centralTransplantes AND p.status = :status")
    List<ProtocoloME> findByCentralTransplantesAndStatus(
            CentralTransplantes centralTransplantes,
            ProtocoloME.StatusProtocoloME status
    );

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    @Query("SELECT DISTINCT p FROM ProtocoloME p WHERE p.dataNotificacao BETWEEN :dataInicio AND :dataFim")
    List<ProtocoloME> findByDataNotificacaoBetween(
            LocalDateTime dataInicio,
            LocalDateTime dataFim
    );

    @EntityGraph(attributePaths = {
            "paciente",
            "paciente.hospital",
            "centralTransplantes",
            "doacao",
            "doacao.orgaos"
    })
    List<ProtocoloME> findByHospitalOrigem(String hospitalOrigem);
}
