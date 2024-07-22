package vn.elca.training.pilot_project_back.service;

import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;

import java.util.List;

public interface EmployerService {
    List<EmployerResponseDto> getEmployers(EmployerSearchRequestDto searchRequestDto);

    EmployerResponseDto getEmployerById(long id) throws EntityNotFoundException;

    EmployerResponseDto createEmployer(EmployerCreateRequestDto requestDto);

    void deleteEmployer(long id) throws EntityNotFoundException;
}
