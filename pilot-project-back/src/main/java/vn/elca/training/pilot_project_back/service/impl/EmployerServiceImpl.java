package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.EmployerService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class EmployerServiceImpl implements EmployerService {
    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;

    @Override
    public List<EmployerResponseDto> getEmployers() {
        return employerRepository.findAll().stream().map(employerMapper::toResponseDto).collect(Collectors.toList());
    }
}
