package vn.elca.training.pilot_project_back.service;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.impl.SalaryServiceImpl;

import java.text.SimpleDateFormat;

@ExtendWith(MockitoExtension.class)
public class SalaryServiceTest {
    @Mock
    private SalaryRepository salaryRepository;
    @Mock
    private SalaryMapper salaryMapper;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private SimpleDateFormat simpleDateFormat;
    @InjectMocks
    private SalaryServiceImpl salaryService;
}
