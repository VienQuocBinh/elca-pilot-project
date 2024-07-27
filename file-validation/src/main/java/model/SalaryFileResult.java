package model;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class SalaryFileResult {
    private List<Salary> salaries;
    private List<SalaryError> errors;
}
