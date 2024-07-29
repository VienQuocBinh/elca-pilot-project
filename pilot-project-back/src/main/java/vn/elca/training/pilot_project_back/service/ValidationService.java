package vn.elca.training.pilot_project_back.service;

import model.SalaryFileResult;
import vn.elca.training.pilot_project_back.dto.SalaryCreateRequestDto;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;

import java.util.List;

public interface ValidationService {
    void validateEmployerCreateRequestProto(EmployerCreateRequest createRequest) throws ValidationException;

    void validateEmployerUpdateRequestProto(EmployerUpdateRequest updateRequest) throws ValidationException;

    SalaryFileResult validateFileSalary(List<SalaryCreateRequestDto> salaryCreateRequestDtos);
}
