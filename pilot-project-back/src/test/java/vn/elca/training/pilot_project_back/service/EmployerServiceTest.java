package vn.elca.training.pilot_project_back.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.*;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.impl.EmployerServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {
    @Mock
    private EmployerMapper employerMapper;
    @Mock
    private SalaryMapper salaryMapper;
    @Mock
    private EmployerRepository employerRepository;
    @InjectMocks
    private EmployerServiceImpl employerService;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(employerService, "pageSize", "10");
    }

    @Test
    void testGetEmployers() {
        EmployerSearchRequestDto searchRequestDto = EmployerSearchRequestDto.builder()
                .pensionType(PensionType.NONE)
                .build();
        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employers.add(Employer.builder()
                    .id((long) i)
                    .name("A" + i)
                    .number("00000" + i)
                    .ideNumber("CHE-123.456.78" + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("number"));
        Page<Employer> employerPage = new PageImpl<>(employers, pageable, employers.size());
        when(employerRepository.findAll(any(Predicate.class), eq(pageable))).thenReturn(employerPage);
        Page<EmployerResponseDto> result = employerService.getEmployers(searchRequestDto);

        Assertions.assertEquals(5, result.getTotalElements());
    }

    @Test
    void testGetEmployersPaging() {
        EmployerSearchRequestDto searchRequestDto = EmployerSearchRequestDto.builder()
                .pensionType(PensionType.NONE)
                .pagingRequest(PagingRequestDto.builder()
                        .pageIndex(0)
                        .build())
                .build();
        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            employers.add(Employer.builder()
                    .id((long) i)
                    .name("A" + i)
                    .number("00000" + i)
                    .ideNumber("CHE-123.456.78" + i)
                    .build());
        }

        Pageable pageable = PageRequest.of(0, 10, Sort.by("number"));
        Page<Employer> employerPage = new PageImpl<>(employers, pageable, employers.size());
        when(employerRepository.findAll(any(Predicate.class), eq(pageable))).thenReturn(employerPage);
        Page<EmployerResponseDto> result = employerService.getEmployers(searchRequestDto);

        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(2, result.getTotalPages());
        Assertions.assertEquals(20, result.getTotalElements());
    }

    @Test
    void testGetEmployerById() {
        when(employerRepository.findById(anyLong())).thenReturn(Optional.ofNullable(Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .build()));
        Assertions.assertNotNull(employerRepository.findById(anyLong()));
        verify(employerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testGetEmployerByIdThrowException() {
        when(employerRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> employerService.getEmployerById(anyLong()));
        verify(employerRepository, times(1)).findById(anyLong());
    }

    @Test
    void testCreateEmployer() {
        EmployerCreateRequestDto requestDto = EmployerCreateRequestDto.builder()
                .name("A")
                .ideNumber("CHE-123.456.789")
                .build();
        Employer employer = Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .build();
        EmployerResponseDto responseDto = EmployerResponseDto.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .build();
        when(employerMapper.mapCreateDtoToEntity(requestDto)).thenReturn(employer);
        when(employerMapper.mapEntityToResponseDto(employer)).thenReturn(responseDto);
        when(employerRepository.save(employer)).thenReturn(employer);
        EmployerResponseDto result = employerService.createEmployer(requestDto);
        Assertions.assertEquals(responseDto.getId(), result.getId());
        Assertions.assertEquals(responseDto.getNumber(), result.getNumber());
        verify(employerRepository, times(1)).save(employer);
    }

    @Test
    void testDeleteEmployer() throws EntityNotFoundException {
        long id = 1L;
        Employer employer = Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .build();
        when(employerRepository.findById(id)).thenReturn(Optional.of(employer));
        employerService.deleteEmployer(id);
        verify(employerRepository, times(1)).delete(employer);
    }

    @Test
    void testDeleteEmployerThrowException() {
        long id = 1L;
        when(employerRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> employerService.deleteEmployer(id));
        verify(employerRepository).findById(id);
        verify(employerRepository, times(0)).delete(any());
    }

    @Test
    void testUpdateEmployer() throws EntityNotFoundException {
        long id = 1L;
        Employer employer = Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .salaries(new ArrayList<>())
                .build();
        EmployerUpdateRequestDto requestDto = EmployerUpdateRequestDto.builder()
                .id(id)
                .name("A1")
                .salaries(new ArrayList<>())
                .build();

        List<Salary> salaries = new ArrayList<>();
        when(salaryMapper.mapCreateListRequestDtoToEntityList(requestDto.getSalaries())).thenReturn(salaries);
        when(employerRepository.findById(id)).thenReturn(Optional.of(employer));

        employerService.updateEmployer(requestDto);
        verify(employerRepository).saveAndFlush(employer);
        verify(employerMapper).mapEntityToResponseDto(employer);

        assertEquals(employer.getId(), requestDto.getId());
        assertEquals(employer.getName(), requestDto.getName());
        assertEquals(employer.getIdeNumber(), requestDto.getIdeNumber());
    }

    @Test
    void testUpdateEmployerThrowException() {
        long id = 1L;
        Employer employer = Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .salaries(new ArrayList<>())
                .build();
        EmployerUpdateRequestDto requestDto = EmployerUpdateRequestDto.builder()
                .id(id)
                .name("A1")
                .salaries(new ArrayList<>())
                .build();
        when(employerRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> employerService.updateEmployer(requestDto));
        verify(employerRepository, times(0)).saveAndFlush(employer);
    }
}
