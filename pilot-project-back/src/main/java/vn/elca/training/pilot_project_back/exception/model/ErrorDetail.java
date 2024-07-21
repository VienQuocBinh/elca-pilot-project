package vn.elca.training.pilot_project_back.exception.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorDetail {
    private String object;
    private String field;
    private Object value;
    private String message;
}
