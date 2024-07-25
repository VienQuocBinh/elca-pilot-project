package vn.elca.training.pilot_project_back.service;

import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;

public interface ValidationService {
    void validateEmployerCreateRequestProto(EmployerCreateRequest createRequest) throws ValidationException;

    void validateEmployerUpdateRequestProto(EmployerUpdateRequest updateRequest) throws ValidationException;
}
