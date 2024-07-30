package vn.elca.training.pilot_project_back.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerUpdateRequestDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.entity.QEmployer;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
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
    private final SalaryMapper salaryMapper;
    @Value("${paging.default.page.size}")
    private String pageSize;

    @Override
    public Page<EmployerResponseDto> getEmployers(EmployerSearchRequestDto searchRequest) {
        Predicate predicate = buildSearchCriteria(searchRequest);
        Sort sort = Sort.by(QEmployer.employer.number.getMetadata().getName());
        Pageable pageable;
        int size = Integer.parseInt(pageSize);
        if (searchRequest.getPagingRequest() == null) {
            pageable = PageRequest.of(0, size, sort);
        } else {
            pageable = PageRequest.of(searchRequest.getPagingRequest().getPageIndex(), size, sort);
        }
        Page<Employer> employers = employerRepository.findAll(predicate, pageable);
        return new PageImpl<>(
                employers.stream().map(employerMapper::mapEntityToResponseDto).collect(Collectors.toList()),
                pageable,
                employers.getTotalElements()
        );
    }

    @Override
    public EmployerResponseDto getEmployerById(long id) throws EntityNotFoundException {
        return employerRepository.findById(id).map(employerMapper::mapEntityToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(Employer.class, "id", id));
    }

    @Override
    public int getEmployerNextNumber() {
        return employerRepository.findMaxNumber() + 1;
    }

    @Override
    public EmployerResponseDto createEmployer(EmployerCreateRequestDto requestDto) {
        Employer employer = employerRepository.save(employerMapper.mapCreateDtoToEntity(requestDto));
        return employerMapper.mapEntityToResponseDto(employer);
    }

    @Override
    public void deleteEmployer(long id) throws EntityNotFoundException {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Employer.class, "id", id));
        employerRepository.delete(employer);
    }

    @Override
    public EmployerResponseDto updateEmployer(EmployerUpdateRequestDto requestDto) throws EntityNotFoundException {
        Employer employer = employerRepository.findById(requestDto.getId())
                .orElseThrow(() -> new EntityNotFoundException(Employer.class, "id", requestDto.getId()));

        employer.setName(requestDto.getName());
        employer.setIdeNumber(requestDto.getIdeNumber());
        employer.setDateCreation(requestDto.getDateCreation());
        employer.setDateExpiration(requestDto.getDateExpiration());
        employer.setPensionType(requestDto.getPensionType());
        List<Salary> salaries = salaryMapper.mapCreateListRequestDtoToEntityList(requestDto.getSalaries());
        for (Salary salary : salaries) {
            salary.setEmployer(employer);
        }
        employer.getSalaries().addAll(salaries);
        employerRepository.saveAndFlush(employer); // To get the latest salary id
        return employerMapper.mapEntityToResponseDto(employer);
    }

    private Predicate buildSearchCriteria(EmployerSearchRequestDto searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();
        QEmployer employer = QEmployer.employer;

        if (searchRequest.getPensionType() != null && !searchRequest.getPensionType().equals(PensionType.NONE)) {
            builder.and(employer.pensionType.eq(searchRequest.getPensionType()));
        }
        if (searchRequest.getName() != null && !searchRequest.getName().isEmpty()) {
            builder.and(employer.name.containsIgnoreCase(searchRequest.getName()));
        }
        if (searchRequest.getNumber() != null && !searchRequest.getNumber().isEmpty()) {
            builder.and(employer.number.contains(searchRequest.getNumber()));
        }
        if (searchRequest.getIdeNumber() != null && !searchRequest.getIdeNumber().isEmpty()) {
            builder.and(employer.ideNumber.containsIgnoreCase(searchRequest.getIdeNumber()));
        }
        if (searchRequest.getDateCreation() != null) {
            // Compare only the date part
            builder.and(Expressions.dateTemplate(java.util.Date.class, "date({0})", employer.dateCreation)
                    .eq(searchRequest.getDateCreation()));
        }
        if (searchRequest.getDateExpiration() != null) {
            // Compare only the date part
            builder.and(Expressions.dateTemplate(java.util.Date.class, "date({0})", employer.dateExpiration)
                    .eq(searchRequest.getDateExpiration()));
        }
        return builder;
    }
}
