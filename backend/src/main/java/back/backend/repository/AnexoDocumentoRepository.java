package back.backend.repository;

import back.backend.model.AnexoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnexoDocumentoRepository extends JpaRepository<AnexoDocumento, Long> {

    /**
     * Buscar anexos por ID do exame
     */
    List<AnexoDocumento> findByExameMEId(Long exameMEId);
    List<AnexoDocumento> findByExameMEIdIn(List<Long> exameMEIds);
    void deleteByExameMEIdIn(List<Long> exameMEIds);

    /**
     * Buscar anexos por ID do protocolo (para entrevista familiar)
     */
    List<AnexoDocumento> findByProtocoloMEId(Long protocoloMEId);
    void deleteByProtocoloMEId(Long protocoloMEId);

    /**
     * Buscar anexos por tipo (EXAME ou ENTREVISTA)
     */
    List<AnexoDocumento> findByTipoAnexo(String tipoAnexo);

    /**
     * Contar anexos de um exame
     */
    long countByExameMEId(Long exameMEId);

    /**
     * Contar anexos de um protocolo (entrevista)
     */
    long countByProtocoloMEId(Long protocoloMEId);

    /**
     * Buscar anexos por tipo e ID
     */
    @Query("SELECT a FROM AnexoDocumento a WHERE a.tipoAnexo = :tipoAnexo AND " +
           "((a.tipoAnexo = 'EXAME' AND a.exameMEId = :id) OR " +
           "(a.tipoAnexo = 'ENTREVISTA' AND a.protocoloMEId = :id))")
    List<AnexoDocumento> findByTipoAnexoAndId(@Param("tipoAnexo") String tipoAnexo, @Param("id") Long id);
}
