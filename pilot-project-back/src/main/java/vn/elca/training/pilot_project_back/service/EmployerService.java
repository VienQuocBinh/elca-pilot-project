package vn.elca.training.pilot_project_back.service;

import org.springframework.data.domain.Page;
import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerUpdateRequestDto;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;

public interface EmployerService {
    Page<EmployerResponseDto> getEmployers(EmployerSearchRequestDto searchRequestDto);

    String exportFile();

    EmployerResponseDto getEmployerById(long id) throws EntityNotFoundException;

    int getEmployerNextNumber();

    EmployerResponseDto createEmployer(EmployerCreateRequestDto requestDto);

    void deleteEmployer(long id) throws EntityNotFoundException;

    EmployerResponseDto updateEmployer(EmployerUpdateRequestDto requestDto) throws EntityNotFoundException;
}
