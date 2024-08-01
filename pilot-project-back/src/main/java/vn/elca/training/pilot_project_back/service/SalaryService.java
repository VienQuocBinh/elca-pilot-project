package vn.elca.training.pilot_project_back.service;

import org.springframework.data.domain.Page;
import vn.elca.training.pilot_project_back.dto.SalaryListRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;

public interface SalaryService {
    Page<SalaryResponseDto> getSalariesByEmployerId(SalaryListRequestDto request);

    String exportSalariesFile(Long employerId) throws EntityNotFoundException;

    void processSalaryCsvFilesJob();
}
