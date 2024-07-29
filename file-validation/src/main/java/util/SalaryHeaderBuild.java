package util;

public class SalaryHeaderBuild {
    private SalaryHeaderBuild() {
    }

    public static String[] buildErrorHeader() {
        return new String[]{"No", "avsNumber", "lastName", "firstName", "startDate", "endDate", "avsAmount", "acAmount", "afAmount", "errorMessage"};
    }
}
