package vn.elca.training.pilot_project_back.service;

import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.proto.employer.EmployerCreateRequest;

import java.text.ParseException;

public interface ValidationService {
    void validateEmployerCreateRequestProto(EmployerCreateRequest createRequest) throws ValidationException, ParseException;
}
