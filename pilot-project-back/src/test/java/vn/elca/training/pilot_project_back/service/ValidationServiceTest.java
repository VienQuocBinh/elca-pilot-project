package vn.elca.training.pilot_project_back.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.repository.SalaryRepository;
import vn.elca.training.pilot_project_back.service.impl.ValidationServiceImpl;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;
import vn.elca.training.proto.employer.PensionTypeProto;

import java.text.SimpleDateFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidationServiceTest {
    private static SimpleDateFormat simpleDateFormat;
    @Mock
    private EmployerRepository employerRepository;
    @Mock
    private SalaryRepository salaryRepository;
    @InjectMocks
    private ValidationServiceImpl validationService;

    @BeforeAll
    public static void setup() {
        simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
        simpleDateFormat.setLenient(false);
    }

    @BeforeEach
    public void setUp() {
        String ideNumberRegex = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}$";
        ReflectionTestUtils.setField(validationService, "simpleDateFormat", simpleDateFormat);
        ReflectionTestUtils.setField(validationService, "ideNumberRegex", ideNumberRegex);
    }

    @Test
    void testValidateEmployerCreateRequestValid() {
        EmployerCreateRequest employerCreateRequest = EmployerCreateRequest.newBuilder()
                .setName("John Doe")
                .setPensionType(PensionTypeProto.REGIONAL)
                .setIdeNumber("CHE-111.222.333")
                .setDateCreation("01.01.2024")
                .setDateExpiration("01.12.2024")
                .build();

        try {
            validationService.validateEmployerCreateRequestProto(employerCreateRequest);
            assertTrue(true);
        } catch (ValidationException e) {
            fail("Expected no ValidationException, but got: " + e.getMessage());
        }
    }

    @Test
    void testValidateEmployerCreateRequestAllFieldsEmpty() {
        EmployerCreateRequest employerCreateRequest = EmployerCreateRequest.newBuilder()
                .setName("")
                .setIdeNumber("")
                .setDateCreation("")
                .setDateExpiration("")
                .build();
        assertThrows(ValidationException.class, () -> validationService.validateEmployerCreateRequestProto(employerCreateRequest));
    }

    @Test
    void testValidateEmployerCreateRequestInvalidFormatField() {
        EmployerCreateRequest employerCreateRequest = EmployerCreateRequest.newBuilder()
                .setName("Test")
                .setIdeNumber("1234567") // not matching pattern
                .setDateCreation("32.12.2024") // invalid date format
                .setDateExpiration("32.12.2024") // invalid date format
                .build();
        assertThrows(ValidationException.class, () -> validationService.validateEmployerCreateRequestProto(employerCreateRequest));
    }

    @Test
    void testValidateEmployerCreateRequestInvalidValueField() {
        EmployerCreateRequest employerCreateRequest = EmployerCreateRequest.newBuilder()
                .setName("Test")
                .setIdeNumber("CHE-123.123.123") // duplicate ide number
                .setDateCreation("31.12.2024")
                .setDateExpiration("30.12.2024") // Expiration date before creation date
                .build();
        when(employerRepository.findByIdeNumber(employerCreateRequest.getIdeNumber())).thenReturn(Optional.of(new Employer()));

        assertThrows(ValidationException.class, () -> validationService.validateEmployerCreateRequestProto(employerCreateRequest));
        verify(employerRepository).findByIdeNumber(employerCreateRequest.getIdeNumber());
    }

    @Test
    void testValidateEmployerUpdateRequestValid() {
        EmployerUpdateRequest updateRequest = EmployerUpdateRequest.newBuilder()
                .setId(1L)
                .setName("John Doe")
                .setPensionType(PensionTypeProto.REGIONAL)
                .setIdeNumber("CHE-111.222.333")
                .setDateCreation("01.01.2024")
                .setDateExpiration("01.12.2024")
                .build();

        try {
            validationService.validateEmployerUpdateRequestProto(updateRequest);
            assertTrue(true);
        } catch (ValidationException e) {
            fail("Expected no ValidationException, but got: " + e.getMessage());
        }
    }

    @Test
    void testValidateEmployerUpdateRequestAllFieldsEmpty() {
        EmployerUpdateRequest employerCreateRequest = EmployerUpdateRequest.newBuilder()
                .setId(1L)
                .setName("")
                .setIdeNumber("")
                .setDateCreation("")
                .setDateExpiration("")
                .build();
        assertThrows(ValidationException.class, () -> validationService.validateEmployerUpdateRequestProto(employerCreateRequest));
    }

    @Test
    void testValidateEmployerUpdateRequestInvalidFormatField() {
        EmployerUpdateRequest employerCreateRequest = EmployerUpdateRequest.newBuilder()
                .setId(1L)
                .setName("Test")
                .setIdeNumber("1234567") // not matching pattern
                .setDateCreation("32.12.2024") // invalid date format
                .setDateExpiration("32.12.2024") // invalid date format
                .build();
        assertThrows(ValidationException.class, () -> validationService.validateEmployerUpdateRequestProto(employerCreateRequest));
    }

    @Test
    void testValidateEmployerUpdateRequestInvalidValueField() {
        EmployerUpdateRequest employerCreateRequest = EmployerUpdateRequest.newBuilder()
                .setId(1L)
                .setName("Test")
                .setIdeNumber("CHE-123.123.123") // duplicate ide number
                .setDateCreation("31.12.2024")
                .setDateExpiration("30.12.2024") // Expiration date before creation date
                .build();
        when(employerRepository.findByIdeNumber(employerCreateRequest.getIdeNumber()))
                .thenReturn(Optional.of(Employer.builder()
                        .id(2L)
                        .ideNumber("CHE-123.123.123") // duplicate ide number
                        .build()));

        assertThrows(ValidationException.class, () -> validationService.validateEmployerUpdateRequestProto(employerCreateRequest));
        verify(employerRepository).findByIdeNumber(employerCreateRequest.getIdeNumber());
    }
}
