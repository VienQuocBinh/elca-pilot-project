package util;

public class HeaderBuild {
    private HeaderBuild() {
    }

    public static String[] buildSalaryErrorHeader() {
        return new String[]{"No", "avsNumber", "lastName", "firstName", "startDate", "endDate", "avsAmount", "acAmount", "afAmount", "errorMessage"};
    }

    public static String[] buildEmployerHeader() {
        return new String[]{"id", "pensionType", "name", "number", "ideNumber", "dateCreation", "dateExpiration"};
    }

    public static String[] buildSalaryHeader() {
        return new String[]{"id", "avsNumber", "lastName", "firstName", "startDate", "endDate", "avsAmount", "acAmount", "afAmount"};
    }
}
