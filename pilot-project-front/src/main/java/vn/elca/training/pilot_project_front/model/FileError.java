package vn.elca.training.pilot_project_front.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FileError {
    private int lineNumber;
    private String errorValue;
    private String errorMessage;
}
