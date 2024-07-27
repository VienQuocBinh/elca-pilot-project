package model;

import lombok.Builder;

@Builder
public class SalaryError {
    private Salary salary;
    private String message;

    public String[] toStringArray() {
        String[] errors = salary.toStringArray();
        String[] resultArray = new String[errors.length + 1];
        System.arraycopy(errors, 0, resultArray, 0, errors.length);
        resultArray[errors.length] = message;
        return resultArray;
    }
}
