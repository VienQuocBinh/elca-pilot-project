package vn.elca.training.pilot_project_back.service.impl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerCreateRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.entity.QEmployer;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.EmployerService;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
public class EmployerServiceImpl implements EmployerService {
    private final EmployerRepository employerRepository;
    private final EmployerMapper employerMapper;

    @Override
    public List<EmployerResponseDto> getEmployers(EmployerSearchRequestDto searchRequest) {
        BooleanBuilder builder = buildSearchCriteria(searchRequest);
        Iterable<Employer> employers = employerRepository.findAll(builder);
        return StreamSupport.stream(employers.spliterator(), false)
                .map(employerMapper::mapEntityToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public EmployerResponseDto getEmployerById(long id) throws EntityNotFoundException {
        return employerRepository.findById(id).map(employerMapper::mapEntityToResponseDto)
                .orElseThrow(() -> new EntityNotFoundException(Employer.class, "id", id));
    }

    @Override
    public EmployerResponseDto createEmployer(@Valid EmployerCreateRequestDto requestDto) {
        Employer employer = employerRepository.save(employerMapper.mapCreateDtoToEntity(requestDto));
        return employerMapper.mapEntityToResponseDto(employer);
    }

    @Override
    public void deleteEmployer(long id) throws EntityNotFoundException {
        Employer employer = employerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Employer.class, "id", id));
        employerRepository.delete(employer);
    }

    private BooleanBuilder buildSearchCriteria(EmployerSearchRequestDto searchRequest) {
        BooleanBuilder builder = new BooleanBuilder();
        QEmployer employer = QEmployer.employer;

        if (searchRequest.getPensionType() != null && !searchRequest.getPensionType().equals(PensionType.NONE)) {
            builder.and(employer.pensionType.eq(searchRequest.getPensionType()));
        }
        if (searchRequest.getName() != null && !searchRequest.getName().isEmpty()) {
            builder.and(employer.name.containsIgnoreCase(searchRequest.getName()));
        }
        if (searchRequest.getNumber() != null && !searchRequest.getNumber().isEmpty()) {
            builder.and(employer.number.eq(searchRequest.getNumber()));
        }
        if (searchRequest.getIdeNumber() != null && !searchRequest.getIdeNumber().isEmpty()) {
            builder.and(employer.ideNumber.eq(searchRequest.getIdeNumber()));
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
