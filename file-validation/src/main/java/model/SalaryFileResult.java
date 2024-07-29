package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class SalaryFileResult {
    private List<Salary> salaries;
    private List<SalaryError> errors;
}
