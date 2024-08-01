package vn.elca.training.pilot_project_back.service;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
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

import java.math.BigDecimal;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {
    private static SimpleDateFormat simpleDateFormat;
    @TempDir
    Path tempDir;
    @Mock
    private EmployerMapper employerMapper;
    @Mock
    private SalaryMapper salaryMapper;
    @Mock
    private EmployerRepository employerRepository;
    @InjectMocks
    private EmployerServiceImpl employerService;

    @BeforeAll
    public static void setup() {
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        simpleDateFormat.setLenient(false);
    }

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(employerService, "pageSize", "10");
        ReflectionTestUtils.setField(employerService, "exportPath", tempDir.toString());
        ReflectionTestUtils.setField(employerService, "exportFileName", "employer_test");
        ReflectionTestUtils.setField(employerService, "simpleDateFormat", simpleDateFormat);
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
    void testGetEmployersAllCriteria() {
        EmployerSearchRequestDto searchRequestDto = EmployerSearchRequestDto.builder()
                .pensionType(PensionType.REGIONAL)
                .name("Employer")
                .number("1")
                .ideNumber("CHE-998.345.143")
                .dateCreation(new Date())
                .dateExpiration(new Date())
                .build();

        Pageable pageable = PageRequest.of(0, 10, Sort.by("number"));
        when(employerRepository.findAll(any(Predicate.class), eq(pageable))).thenReturn(new PageImpl<>(new ArrayList<>()));
        Page<EmployerResponseDto> result = employerService.getEmployers(searchRequestDto);

        Assertions.assertEquals(0, result.getTotalElements());
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
        List<Salary> salaries = new ArrayList<>();
        salaries.add(Salary.builder()
                .id(1L)
                .avsNumber("756.3125.8978.12")
                .lastName("Last")
                .firstName("first")
                .startDate(new Date())
                .endDate(new Date())
                .avsAmount(BigDecimal.valueOf(200.8))
                .afAmount(BigDecimal.valueOf(12))
                .acAmount(BigDecimal.valueOf(15))
                .build());
        Employer employer = Employer.builder()
                .id(1L)
                .name("A")
                .number("000001")
                .ideNumber("CHE-123.456.789")
                .salaries(salaries)
                .build();
        List<SalaryCreateRequestDto> salariesDto = new ArrayList<>();
        salariesDto.add(SalaryCreateRequestDto.builder()
                .avsNumber("756.3125.8978.12")
                .lastName("Last")
                .firstName("first")
                .startDate(new Date())
                .endDate(new Date())
                .avsAmount(BigDecimal.valueOf(200.8))
                .afAmount(BigDecimal.valueOf(12))
                .acAmount(BigDecimal.valueOf(15))
                .build());
        EmployerUpdateRequestDto requestDto = EmployerUpdateRequestDto.builder()
                .id(id)
                .name("A1")
                .salaries(salariesDto)
                .build();


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

    @Test
    void testGetEmployerNextNumber() {
        int i = 1;
        when(employerRepository.findMaxNumber()).thenReturn(i);
        int nextNumber = employerService.getEmployerNextNumber();
        verify(employerRepository).findMaxNumber();
        assertEquals(i + 1, nextNumber);
    }

    @Test
    void testGetEmployerNextNumberNoEmployer() {
        when(employerRepository.findMaxNumber()).thenReturn(0);
        int nextNumber = employerService.getEmployerNextNumber();
        verify(employerRepository).findMaxNumber();
        assertEquals(1, nextNumber);
    }

    @Test
    void testExportFileSuccess() {
        List<Employer> employers = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            employers.add(Employer.builder()
                    .id((long) i)
                    .name("A" + i)
                    .number("00000" + i)
                    .ideNumber("CHE-123.456.78" + i)
                    .pensionType(PensionType.REGIONAL)
                    .dateCreation(new Date())
                    .dateExpiration(new Date())
                    .build());
        }
        when(employerRepository.findAll()).thenReturn(employers);
        when(employerMapper.mapEntityToResponseDto(any(Employer.class))).thenAnswer(invocation -> {
            Employer employer = invocation.getArgument(0);
            return EmployerResponseDto.builder()
                    .id(employer.getId())
                    .name(employer.getName())
                    .number(employer.getNumber())
                    .ideNumber(employer.getIdeNumber())
                    .pensionType(employer.getPensionType())
                    .dateCreation(employer.getDateCreation())
                    .dateExpiration(employer.getDateExpiration())
                    .build();
        });

        String filePath = employerService.exportFile();
        verify(employerRepository).findAll();
        verify(employerMapper, times(employers.size())).mapEntityToResponseDto(any(Employer.class));
        assertNotEquals("", filePath);
        assertTrue(filePath.contains(tempDir.toString()));
    }
}
