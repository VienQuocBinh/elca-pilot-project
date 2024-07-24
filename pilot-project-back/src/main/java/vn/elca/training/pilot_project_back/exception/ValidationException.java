package vn.elca.training.pilot_project_back.exception;

import lombok.Getter;
import vn.elca.training.pilot_project_back.exception.model.ErrorDetail;

import java.util.List;

@Getter
public class ValidationException extends Exception {
    private final List<ErrorDetail> errors;

    public ValidationException(List<ErrorDetail> errors) {
        this.errors = errors;
    }

}
