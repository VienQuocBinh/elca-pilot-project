package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.QSalary;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.SalaryService;
import vn.elca.training.proto.salary.SalaryListRequest;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final SalaryMapper salaryMapper;
    @Value("${paging.default.page.size}")
    private String pageSize;

    @Override
    public Page<SalaryResponseDto> getSalariesByEmployerId(SalaryListRequest request) {
        Pageable pageable = PageRequest.of(request.getPagingRequest().getPageIndex(),
                Integer.parseInt(pageSize),
                Sort.by(QSalary.salary.lastName.getMetadata().getName(),
                        QSalary.salary.firstName.getMetadata().getName()));
        Page<Salary> salaries = salaryRepository.findByEmployerId(request.getEmployerId(), pageable);
        return new PageImpl<>(
                salaries.stream().map(salaryMapper::mapEntityToResponseDto).collect(Collectors.toList()),
                pageable,
                salaries.getTotalElements()
        );
    }
}
