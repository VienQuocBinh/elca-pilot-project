package util;

public class HeaderBuild {

    private HeaderBuild() {
    }

    public static String[] buildSalaryErrorHeader() {
        return prependToHeader("No", commonSalaryHeader());
    }

    public static String[] buildSalaryImportErrorHeader() {
        return prependToHeader("LineNo", commonSalaryHeader());
    }

    public static String[] buildEmployerHeader() {
        return new String[]{"pensionType", "name", "number", "ideNumber", "dateCreation", "dateExpiration"};
    }

    public static String[] buildSalaryHeader() {
        return commonSalaryHeader();
    }

    public static String[] buildImportSalaryHeader() {
        return new String[]{"No", "AVSNumber", "LastName", "FirstName", "StartDate", "EndDate", "AVSAmount", "ACAmount", "AFAmount", "EmployerIdeNumber"};
    }

    private static String[] commonSalaryHeader() {
        return new String[]{"avsNumber", "lastName", "firstName", "startDate", "endDate", "avsAmount", "acAmount", "afAmount"};
    }

    private static String[] prependToHeader(String firstElement, String[] header) {
        String[] result = new String[header.length + 1];
        result[0] = firstElement;
        System.arraycopy(header, 0, result, 1, header.length);
        return result;
    }
}
