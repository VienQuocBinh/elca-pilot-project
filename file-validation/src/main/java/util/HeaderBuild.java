package util;

public class HeaderBuild {

    private HeaderBuild() {
    }

    public static String[] buildSalaryErrorHeader() {
        String[] header = commonSalaryHeader();
        header = appendErrorColToHeader(header);
        return prependToHeader("No", header);
    }

    public static String[] buildSalaryImportErrorHeader() {
        String[] header = commonSalaryHeader();
        header = appendErrorColToHeader(header);
        return prependToHeader("LineNo", header);
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

    private static String[] appendErrorColToHeader(String[] header) {
        String[] result = new String[header.length + 1];
        result[header.length] = "errorMessage";
        System.arraycopy(header, 0, result, 0, header.length);
        return result;
    }
}
