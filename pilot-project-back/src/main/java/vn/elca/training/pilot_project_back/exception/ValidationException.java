package vn.elca.training.pilot_project_back.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends Exception {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public String getErrorsString() {
        return String.join("; ", errors);
    }
}
