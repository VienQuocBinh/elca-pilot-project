package vn.elca.training.pilot_project_back.service;

import config.ErrorConfig;
import model.SalaryError;
import model.SalaryFileResult;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import util.HeaderBuild;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.PagingRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryListRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.entity.QSalary;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.impl.SalaryServiceImpl;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {
    private static SimpleDateFormat simpleDateFormat;
    private final int pageSize = 10;
    @TempDir
    Path tempDir;
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private SalaryMapper salaryMapper;
    @Mock
    private EmployerRepository employerRepository;
    @InjectMocks
    private SalaryServiceImpl salaryService;
    private String originalErrorDir;

    @BeforeAll
    public static void setUp() {
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        simpleDateFormat.setLenient(false);
    }

    @BeforeEach
    public void setup() {
        originalErrorDir = ErrorConfig.ERROR_DIR;
        ErrorConfig.ERROR_DIR = tempDir.toString();
        ReflectionTestUtils.setField(salaryService, "pageSize", String.valueOf(pageSize));
        ReflectionTestUtils.setField(salaryService, "exportPath", tempDir.toString());
        ReflectionTestUtils.setField(salaryService, "exportFileName", "salary_test");
        ReflectionTestUtils.setField(salaryService, "simpleDateFormat", simpleDateFormat);
        ReflectionTestUtils.setField(salaryService, "directoryPath", tempDir.toString());
        ReflectionTestUtils.setField(salaryService, "processedPath", tempDir.toString());
    }

    @AfterEach
    void tearDown() {
        // Restore the original ERROR_DIR value
        ErrorConfig.ERROR_DIR = originalErrorDir;
    }

    @Test
    void testGetSalariesByEmployerId() {
        long employerId = 1;
        Employer employer = Employer.builder()
                .id(employerId)
                .pensionType(PensionType.REGIONAL)
                .name("Employer")
                .number("1")
                .ideNumber("CHE-998.345.143")
                .dateCreation(new Date())
                .dateExpiration(new Date())
                .build();
        List<Salary> salaries = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            salaries.add(Salary.builder()
                    .id((long) i)
                    .firstName("A" + i)
                    .lastName("B" + i)
                    .employer(employer)
                    .build());
        }
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(QSalary.salary.lastName.getMetadata().getName(),
                QSalary.salary.firstName.getMetadata().getName()));
        Page<Salary> salaryPage = new PageImpl<>(salaries, pageable, salaries.size());
        when(salaryRepository.findByEmployerId(employerId, pageable)).thenReturn(salaryPage);
        Page<SalaryResponseDto> salariesByEmployerId = salaryService.getSalariesByEmployerId(SalaryListRequestDto.builder().employerId(employerId).build());

        assertEquals(pageSize, salariesByEmployerId.getTotalElements());
    }

    @Test
    void testGetSalariesByEmployerIdPaging() {
        long employerId = 1;
        Employer employer = Employer.builder()
                .id(employerId)
                .pensionType(PensionType.REGIONAL)
                .name("Employer")
                .number("1")
                .ideNumber("CHE-998.345.143")
                .dateCreation(new Date())
                .dateExpiration(new Date())
                .build();
        List<Salary> salaries = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            salaries.add(Salary.builder()
                    .id((long) i)
                    .firstName("A" + i)
                    .lastName("B" + i)
                    .startDate(new Date())
                    .endDate(new Date())
                    .avsNumber("756.4546.3216.0" + i)
                    .avsAmount(BigDecimal.TEN)
                    .avsAmount(BigDecimal.ONE)
                    .avsAmount(BigDecimal.valueOf(12.2))
                    .employer(employer)
                    .build());
        }
        SalaryListRequestDto requestDto = SalaryListRequestDto.builder().employerId(employerId)
                .pagingRequest(PagingRequestDto.builder().pageIndex(0).build())
                .build();
        Pageable pageable = PageRequest.of(0, pageSize, Sort.by(QSalary.salary.lastName.getMetadata().getName(),
                QSalary.salary.firstName.getMetadata().getName()));
        Page<Salary> salaryPage = new PageImpl<>(salaries, pageable, salaries.size());
        when(salaryRepository.findByEmployerId(employerId, pageable)).thenReturn(salaryPage);
        Page<SalaryResponseDto> salariesByEmployerId = salaryService.getSalariesByEmployerId(requestDto);

        assertEquals(pageSize, salariesByEmployerId.getTotalElements());
    }

    @Test
    void testExportSalary() throws EntityNotFoundException {
        long employerId = 1;
        Employer employer = Employer.builder()
                .id(employerId)
                .pensionType(PensionType.REGIONAL)
                .name("Employer")
                .number("1")
                .ideNumber("CHE-998.345.143")
                .dateCreation(new Date())
                .dateExpiration(new Date())
                .build();
        List<Salary> salaries = new ArrayList<>();
        for (int i = 0; i < pageSize; i++) {
            salaries.add(Salary.builder()
                    .id((long) i)
                    .firstName("A" + i)
                    .lastName("B" + i)
                    .startDate(new Date())
                    .endDate(new Date())
                    .avsNumber("756.4546.3216.0" + i)
                    .avsAmount(BigDecimal.TEN)
                    .acAmount(BigDecimal.ONE)
                    .afAmount(BigDecimal.valueOf(12.2))
                    .employer(employer)
                    .build());
        }

        when(employerRepository.findById(employerId)).thenReturn(Optional.of(employer));
        when(salaryRepository.findByEmployerId(employerId)).thenReturn(salaries);
        when(salaryMapper.mapEntityToResponseDto(any(Salary.class))).thenAnswer(invocation -> {
            Salary salary = invocation.getArgument(0);
            return SalaryResponseDto.builder()
                    .id(salary.getId())
                    .firstName(salary.getFirstName())
                    .lastName(salary.getLastName())
                    .startDate(salary.getStartDate())
                    .endDate(salary.getEndDate())
                    .avsNumber(salary.getAvsNumber())
                    .avsAmount(salary.getAvsAmount())
                    .acAmount(salary.getAcAmount())
                    .afAmount(salary.getAfAmount())
                    .build();
        });

        String filePath = salaryService.exportSalariesFile(employerId);
        verify(employerRepository).findById(employerId);
        verify(salaryRepository).findByEmployerId(employerId);
        verify(salaryMapper, times(salaries.size())).mapEntityToResponseDto(any(Salary.class));
        assertNotEquals("", filePath);
        assertTrue(filePath.contains(tempDir.toString()));
    }

    @Test
    void testExportSalaryThrowException() {
        long employerId = 1;
        when(employerRepository.findById(employerId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> salaryService.exportSalariesFile(employerId));

        verify(employerRepository).findById(employerId);
        verify(salaryRepository, times(0)).findByEmployerId(employerId);
        verify(salaryMapper, times(0)).mapEntityToResponseDto(any(Salary.class));
    }

    @Test
    void testProcessSalaryCsvFilesJobNoFile() {
        try (MockedStatic<util.FileUtil> fileUtilMockedStatic = mockStatic(util.FileUtil.class)) {
            SalaryFileResult fileResult = SalaryFileResult.builder()
                    .errors(new ArrayList<>())
                    .salaries(new ArrayList<>()).build();
            fileUtilMockedStatic.when(() -> util.FileUtil.processSalaryCsvFiles(any(File.class))).thenReturn(fileResult);

            salaryService.processSalaryCsvFilesJob();
            fileUtilMockedStatic.verifyNoInteractions();
        }
    }

    @Test
    void testProcessSalaryCsvFilesJobWithFile() {
        Employer employer = Employer.builder()
                .id(1L)
                .pensionType(PensionType.REGIONAL)
                .name("Employer")
                .number("1")
                .ideNumber("CHE-998.345.143")
                .dateCreation(new Date())
                .dateExpiration(new Date())
                .build();
        // Create a temporary CSV file
        String filePath = util.FileUtil.writeErrorCsvFile("test", HeaderBuild.buildImportSalaryHeader(), new ArrayList<>());
        File file = new File(filePath);
        assertTrue(file.exists());
        try (MockedStatic<util.FileUtil> mockedFileUtil = mockStatic(util.FileUtil.class)) {
            SalaryFileResult mockResult = mock(SalaryFileResult.class);
            List<model.Salary> validSalaries = new ArrayList<>();
            List<model.SalaryError> errorSalaries = new ArrayList<>();
            List<String> existAvsNumberInPeriod = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                validSalaries.add(model.Salary.builder()
                        .id((long) i)
                        .firstName("A" + i)
                        .lastName("B" + i)
                        .startDate(simpleDateFormat.format(new Date()))
                        .endDate(simpleDateFormat.format(new Date()))
                        .avsNumber("756.4546.3216.0" + i)
                        .avsAmount(String.valueOf(BigDecimal.TEN))
                        .acAmount(String.valueOf(BigDecimal.ONE))
                        .afAmount(String.valueOf(BigDecimal.valueOf(12.2)))
                        .employerIdeNumber(employer.getIdeNumber())
                        .build());
                errorSalaries.add(SalaryError.builder().salary(model.Salary.builder()
                                .id((long) i + 10)
                                .firstName("A" + (i + 10))
                                .lastName("B" + (i + 10))
                                .startDate(simpleDateFormat.format(new Date()))
                                .endDate(simpleDateFormat.format(new Date()))
                                .avsNumber("756.4546.3216." + (i + 10))
                                .avsAmount(String.valueOf(BigDecimal.TEN))
                                .acAmount(String.valueOf(BigDecimal.ONE))
                                .afAmount(String.valueOf(BigDecimal.valueOf(12.2)))
                                .employerIdeNumber(employer.getIdeNumber())
                                .build())
                        .message("Test error salaries")
                        .build());
            }
            existAvsNumberInPeriod.add("756.4546.3216.01");
            when(mockResult.getSalaries()).thenReturn(validSalaries); // Mock valid salary list
            when(mockResult.getErrors()).thenReturn(errorSalaries); // Mock errors list
            // Mock processing the CSV file
            mockedFileUtil.when(() -> util.FileUtil.processSalaryCsvFiles(any(File.class))).thenReturn(mockResult);

            when(salaryRepository.findAvsNumbersBetweenDates(any(Date.class), any(Date.class))).thenReturn(existAvsNumberInPeriod);
            when(employerRepository.findByIdeNumber(employer.getIdeNumber())).thenReturn(Optional.empty());

            salaryService.processSalaryCsvFilesJob();
            verify(salaryRepository, times(5)).findAvsNumbersBetweenDates(any(Date.class), any(Date.class));
            verify(salaryRepository).saveAll(anyList());
        }
    }


    @Test
    void testProcessSalaryCsvFilesJob_PathIsNotDirectory() throws IOException {
        File mockFile = Files.createFile(tempDir.resolve("not_a_dir.csv")).toFile();
        ReflectionTestUtils.setField(salaryService, "directoryPath", mockFile.getAbsolutePath());
        salaryService.processSalaryCsvFilesJob();
        try (MockedStatic<util.FileUtil> mockedFileUtil = mockStatic(util.FileUtil.class)) {
            mockedFileUtil.verifyNoInteractions();
            verify(salaryRepository, times(0)).findAvsNumbersBetweenDates(any(Date.class), any(Date.class));
        }
    }

    @Test
    void testProcessSalaryCsvFilesJobStartDateParseException() throws IOException {
        try (MockedStatic<util.FileUtil> mockedFileUtil = mockStatic(util.FileUtil.class)) {
            File tempFile = Files.createFile(tempDir.resolve("test.csv")).toFile();

            SalaryFileResult mockResult = mock(SalaryFileResult.class);
            List<model.Salary> mockSalaries = new ArrayList<>();
            model.Salary mockSalary = new model.Salary();
            mockSalary.setStartDate("invalid_date"); // This will cause a ParseException
            mockSalaries.add(mockSalary);
            when(mockResult.getSalaries()).thenReturn(mockSalaries);
            mockedFileUtil.when(() -> util.FileUtil.processSalaryCsvFiles(tempFile)).thenReturn(mockResult);

            // Inject the directory path into the service
            ReflectionTestUtils.setField(salaryService, "directoryPath", tempDir.toString());

            salaryService.processSalaryCsvFilesJob();
            mockedFileUtil.verify(() -> util.FileUtil.processSalaryCsvFiles(tempFile));
        }
    }

    @Test
    void testProcessSalaryCsvFilesJobEndDateParseException() throws IOException {
        try (MockedStatic<util.FileUtil> mockedFileUtil = mockStatic(util.FileUtil.class)) {
            File tempFile = Files.createFile(tempDir.resolve("test.csv")).toFile();

            SalaryFileResult mockResult = mock(SalaryFileResult.class);
            List<model.Salary> mockSalaries = new ArrayList<>();
            model.Salary mockSalary = new model.Salary();
            mockSalary.setStartDate(simpleDateFormat.format(new Date()));
            mockSalary.setEndDate("invalid_date"); // This will cause a ParseException
            mockSalaries.add(mockSalary);
            when(mockResult.getSalaries()).thenReturn(mockSalaries);
            mockedFileUtil.when(() -> util.FileUtil.processSalaryCsvFiles(tempFile)).thenReturn(mockResult);

            // Inject the directory path into the service
            ReflectionTestUtils.setField(salaryService, "directoryPath", tempDir.toString());

            salaryService.processSalaryCsvFilesJob();
            mockedFileUtil.verify(() -> util.FileUtil.processSalaryCsvFiles(tempFile));
        }
    }

    @Test
    void testProcessSalaryCsvFilesJobValid() throws IOException {
        try (MockedStatic<util.FileUtil> mockedFileUtil = mockStatic(util.FileUtil.class)) {
            File tempFile = Files.createFile(tempDir.resolve("test.csv")).toFile();

            SalaryFileResult mockResult = mock(SalaryFileResult.class);
            List<model.Salary> mockSalaries = new ArrayList<>();
            mockSalaries.add(model.Salary.builder()
                    .startDate(simpleDateFormat.format(new Date()))
                    .endDate(simpleDateFormat.format(new Date()))
                    .avsAmount(String.valueOf(123.1))
                    .afAmount(String.valueOf(123.1))
                    .acAmount(String.valueOf(123.1))
                    .employerIdeNumber("756.4223.1234.33")
                    .build());
            when(mockResult.getSalaries()).thenReturn(mockSalaries);
            when(employerRepository.findByIdeNumber(any(String.class))).thenReturn(Optional.of(new Employer()));
            mockedFileUtil.when(() -> util.FileUtil.processSalaryCsvFiles(tempFile)).thenReturn(mockResult);

            // Inject the directory path into the service
            ReflectionTestUtils.setField(salaryService, "directoryPath", tempDir.toString());

            salaryService.processSalaryCsvFilesJob();
            mockedFileUtil.verify(() -> util.FileUtil.processSalaryCsvFiles(tempFile));
        }
    }
}
