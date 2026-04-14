package back.backend.repository;

import back.backend.model.Paciente;
import back.backend.model.Hospital;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Long> {

    Optional<Paciente> findByCpf(String cpf);

    List<Paciente> findByHospital(Hospital hospital);

    List<Paciente> findByStatus(Paciente.StatusPaciente status);

    List<Paciente> findByHospitalAndStatus(Hospital hospital, Paciente.StatusPaciente status);

    @Query("SELECT p FROM Paciente p WHERE p.hospital = :hospital AND p.dataNascimento BETWEEN :dataInicio AND :dataFim")
    List<Paciente> findPacientesByHospitalAndDataNascimento(
        @Param("hospital") Hospital hospital,
        @Param("dataInicio") LocalDate dataInicio,
        @Param("dataFim") LocalDate dataFim
    );

    @Query("SELECT p FROM Paciente p WHERE LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Paciente> findByNomeContainingIgnoreCase(@Param("nome") String nome);

    @Query("SELECT p FROM Paciente p WHERE p.hospital = :hospital AND LOWER(p.nome) LIKE LOWER(CONCAT('%', :nome, '%'))")
    List<Paciente> findByHospitalAndNomeContainingIgnoreCase(
        @Param("hospital") Hospital hospital,
        @Param("nome") String nome
    );

    long countByStatus(Paciente.StatusPaciente status);

    long countByHospital(Hospital hospital);

}
