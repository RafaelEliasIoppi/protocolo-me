package back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import back.model.Hospital;

public interface HospitalRepository extends JpaRepository<Hospital, Long> {
}