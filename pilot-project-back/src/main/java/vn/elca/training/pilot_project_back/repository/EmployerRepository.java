package vn.elca.training.pilot_project_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import vn.elca.training.pilot_project_back.entity.Employer;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long>, QuerydslPredicateExecutor<Employer> {
    @Query("SELECT MAX(CAST(e.number AS int)) FROM Employer e")
    Integer findMaxNumber();

    Optional<Employer> findByIdeNumber(String ideNumber);
}
