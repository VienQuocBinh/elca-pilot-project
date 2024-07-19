package vn.elca.training.pilot_project_back.listener;

import vn.elca.training.pilot_project_back.config.SpringContext;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;

import javax.persistence.PrePersist;

public class EmployerListener {

    @PrePersist
    public void prePersist(Employer employer) {
        EmployerRepository employerRepository = SpringContext.getBean(EmployerRepository.class);
        Integer maxNumber = employerRepository.findMaxNumber();
        int newNumber = (maxNumber != null) ? maxNumber + 1 : 1;
        employer.setNumber(String.format("%06d", newNumber));
    }
}
