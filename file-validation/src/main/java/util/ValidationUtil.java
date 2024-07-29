package util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class ValidationUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String AVS_NUMBER_REGEX = "^756.\\d{4}.\\d{4}.\\d{2}$";

    private ValidationUtil() {
    }

    public static boolean isValidAvsNumber(String avsNumber) {
        return avsNumber.matches(AVS_NUMBER_REGEX);
    }

    public static boolean isValidDate(String date) {
        try {
            DATE_FORMATTER.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isDateBefore(String dateStringBefore, String dateStringAfter) {
        try {
            LocalDate dateBefore = LocalDate.parse(dateStringBefore, DATE_FORMATTER);
            LocalDate dateAfter = LocalDate.parse(dateStringAfter, DATE_FORMATTER);
            return dateBefore.isBefore(dateAfter);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean isNegativeBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal.compareTo(new BigDecimal(0)) < 0;
    }

    public static boolean isNegativeBigDecimal(String bigDecimal) {
        try {
            BigDecimal afAmount = new BigDecimal(bigDecimal);
            if (isNegativeBigDecimal(afAmount)) {
                return true;
            }
        } catch (NumberFormatException e) {
            return true;
        }
        return false;
    }
}
