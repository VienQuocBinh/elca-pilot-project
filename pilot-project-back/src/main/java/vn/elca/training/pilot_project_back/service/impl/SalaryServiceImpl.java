package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.SalaryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final SalaryMapper salaryMapper;

    @Override
    public List<SalaryResponseDto> getSalariesByEmployerId(Long employerId) {
        List<Salary> salaries = salaryRepository.findByEmployerId(employerId);
        return salaries.stream().map(salaryMapper::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }
}
