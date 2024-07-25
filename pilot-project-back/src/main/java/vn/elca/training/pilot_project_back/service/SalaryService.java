package vn.elca.training.pilot_project_back.service;

import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;

import java.util.List;

public interface SalaryService {
    List<SalaryResponseDto> getSalariesByEmployerId(Long employerId);
}
