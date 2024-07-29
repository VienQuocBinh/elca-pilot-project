package vn.elca.training.pilot_project_back.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.elca.training.pilot_project_back.entity.Salary;

import java.util.Date;
import java.util.List;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {
    Page<Salary> findByEmployerId(Long employerId, Pageable pageable);

    @Query(value = "select avs_number from salary " +
            "where DATE(start_date)= :startDate " +
            "and DATE(end_date) = :endDate " +
            "group by avs_number;",
            nativeQuery = true)
    List<String> findAvsNumbersBetweenDates(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
