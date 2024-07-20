package vn.elca.training.pilot_project_back.service;

import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;

import java.util.List;

public interface EmployerService {
    List<EmployerResponseDto> getEmployers();

    EmployerResponseDto getEmployerById(long id);
}
