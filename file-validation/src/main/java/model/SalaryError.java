package model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SalaryError {
    private Salary salary;
    private String message;

    public String[] toStringArray() {
        String[] errors = salary.toStringArray();
        String[] resultArray = new String[errors.length + 1];
        System.arraycopy(errors, 0, resultArray, 0, errors.length);
        resultArray[errors.length - 1] = message;
        return resultArray;
    }
}
