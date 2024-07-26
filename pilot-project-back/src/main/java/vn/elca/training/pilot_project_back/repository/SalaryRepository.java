package vn.elca.training.pilot_project_back.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.elca.training.pilot_project_back.entity.Salary;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Page<Salary> findByEmployerId(Long employerId, Pageable pageable);
}
