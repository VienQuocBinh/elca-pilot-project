package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.SalaryError;
import model.SalaryFileResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import util.HeaderBuild;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.entity.QSalary;
import vn.elca.training.pilot_project_back.entity.Salary;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.SalaryService;
import vn.elca.training.pilot_project_back.util.FileUtil;
import vn.elca.training.proto.salary.SalaryListRequest;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Throwable.class)
@Slf4j
public class SalaryServiceImpl implements SalaryService {
    private final SalaryRepository salaryRepository;
    private final SalaryMapper salaryMapper;
    private final EmployerRepository employerRepository;
    private final SimpleDateFormat simpleDateFormat;
    @Value("${paging.default.page.size}")
    private String pageSize;
    @Value("${salary.csv.file.path}")
    private String directoryPath;
    @Value("${salary.csv.file.processed.path}")
    private String processedPath;
    @Value("${salary.csv.file.export.path}")
    private String exportPath;
    @Value("${salary.csv.file.export.name}")
    private String exportFileName;

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

    @Override
    public String exportSalariesFile(Long employerId) throws EntityNotFoundException {
        Employer employer = employerRepository.findById(employerId).orElseThrow(() ->
                new EntityNotFoundException(Employer.class, "id", employerId));

        List<SalaryResponseDto> collect = salaryRepository.findByEmployerId(employer.getId()).stream()
                .map(salaryMapper::mapEntityToResponseDto)
                .sorted(Comparator.comparing(SalaryResponseDto::getLastName)
                        .thenComparing(SalaryResponseDto::getFirstName))
                .collect(Collectors.toList());
        List<String[]> data = collect.stream().map(dto -> dto.toStringArray(simpleDateFormat)).collect(Collectors.toList());
        return FileUtil.writeCsvFile(exportPath, exportFileName, HeaderBuild.buildSalaryHeader(), data);
    }

    @Override
    public void processSalaryCsvFilesJob() {
        File directory = new File(directoryPath);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".csv"));
            if (files != null && files.length > 0) {
                Arrays.stream(files).forEach(file -> {
                    SalaryFileResult result = util.FileUtil.processSalaryCsvFiles(file);
                    // Handle the result as needed
                    log.info("Processed file: {}", file.getName());
                    // Check AVS number of import success list with the DB
                    Set<model.Salary> salariesToBeRemoved = new HashSet<>();
                    for (model.Salary salary : result.getSalaries()) {
                        try {
                            Date startDateDate = simpleDateFormat.parse(salary.getStartDate());
                            Date endDateDate = simpleDateFormat.parse(salary.getEndDate());
                            List<String> avsNumbersBetweenDates = salaryRepository.findAvsNumbersBetweenDates(startDateDate, endDateDate);
                            if (avsNumbersBetweenDates.contains(salary.getAvsNumber())) {
                                result.getErrors().add(SalaryError.builder()
                                        .salary(salary)
                                        .message("AVS number " + salary.getAvsNumber() + " already existed between " + salary.getStartDate() + " and " + salary.getEndDate())
                                        .build());
                                salariesToBeRemoved.add(salary);
                            }
                            if (!employerRepository.findByIdeNumber(salary.getEmployerIdeNumber()).isPresent()) {
                                result.getErrors().add(SalaryError.builder()
                                        .salary(salary)
                                        .message("Employer with ide number " + salary.getEmployerIdeNumber() + " is not exist")
                                        .build());
                                salariesToBeRemoved.add(salary);
                            }
                        } catch (Exception e) {
                            log.error(e.getMessage());
                        }
                    }
                    // Remove dup sal in import success list
                    result.getSalaries().removeAll(salariesToBeRemoved);
                    if (!result.getErrors().isEmpty()) {
                        String[] header = HeaderBuild.buildSalaryImportErrorHeader();
                        util.FileUtil.writeErrorCsvFile(file.getName(), header, result.getErrors().stream().map(SalaryError::toStringArray).collect(Collectors.toList()));
                    }
                    saveAllSalaries(result.getSalaries());
                });
                // Move processed file to new dir in order to not re-process
                FileUtil.moveFile(directoryPath, processedPath);
            }
        } else {
            log.error("The path is not a directory: {}", directoryPath);
        }
    }

    public void saveAllSalaries(List<model.Salary> salaries) {
        salaryRepository.saveAll(salaries
                .stream().map(salary -> {
                    try {
                        return Salary.builder()
                                .avsNumber(salary.getAvsNumber())
                                .firstName(salary.getFirstName())
                                .lastName(salary.getLastName())
                                .startDate(simpleDateFormat.parse(salary.getStartDate()))
                                .endDate(simpleDateFormat.parse(salary.getEndDate()))
                                .avsAmount(new BigDecimal(salary.getAvsAmount()))
                                .afAmount(new BigDecimal(salary.getAfAmount()))
                                .acAmount(new BigDecimal(salary.getAcAmount()))
                                .employer(employerRepository.findByIdeNumber(salary.getEmployerIdeNumber()).orElse(null))
                                .build();
                    } catch (ParseException e) {
                        log.error(e.getMessage());
                    }
                    return null;
                })
                .collect(Collectors.toList()));
    }
}
