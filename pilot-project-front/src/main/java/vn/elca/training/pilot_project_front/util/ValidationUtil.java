package vn.elca.training.pilot_project_front.util;

import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.model.FileError;
import vn.elca.training.pilot_project_front.model.Salary;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ValidationUtil {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.PATTERN);

    private ValidationUtil() {
    }

    public static void validateDateFields(DatePicker dpDateCreation, DatePicker dpDateExpiration,
                                          Label lbDateCreationError, Label lbDateExpirationError,
                                          String errorStyleClass, ResourceBundle resourceBundle) {
        validateDateCreation(dpDateCreation, lbDateCreationError, errorStyleClass, resourceBundle);
        validateDateExpiration(dpDateCreation, dpDateExpiration, lbDateExpirationError, errorStyleClass, resourceBundle);
    }

    private static void validateDateCreation(DatePicker dpDateCreation, Label lbDateCreationError,
                                             String errorStyleClass, ResourceBundle resourceBundle) {
        if (dpDateCreation.getValue() == null) {
            showError(dpDateCreation, lbDateCreationError, errorStyleClass, resourceBundle.getString("error.dateCreation.required"));
        } else if (!isValidDate(dpDateCreation.getValue().format(DATE_FORMATTER))) {
            showError(dpDateCreation, lbDateCreationError, errorStyleClass, resourceBundle.getString("error.dateCreation.format"));
        } else {
            hideError(dpDateCreation, lbDateCreationError, errorStyleClass);
        }
    }

    private static void validateDateExpiration(DatePicker dpDateCreation, DatePicker dpDateExpiration,
                                               Label lbDateExpirationError, String errorStyleClass,
                                               ResourceBundle resourceBundle) {
        if (dpDateExpiration.getValue() != null) {
            if (!isValidDate(dpDateExpiration.getValue().format(DATE_FORMATTER))) {
                showError(dpDateExpiration, lbDateExpirationError, errorStyleClass, resourceBundle.getString("error.dateExpiration.format"));
            } else if (!dpDateCreation.getValue().isBefore(dpDateExpiration.getValue())) {
                showError(dpDateCreation, dpDateExpiration, lbDateExpirationError, errorStyleClass, resourceBundle.getString("error.dateOrder"));
            } else {
                hideError(dpDateExpiration, lbDateExpirationError, errorStyleClass);
            }
        }
    }

    private static boolean isValidDate(String date) {
        try {
            DATE_FORMATTER.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static void showError(Control control, Label label, String errorStyleClass, String errorMessage) {
        control.getStyleClass().add(errorStyleClass);
        label.setText(errorMessage);
        label.setVisible(true);
    }

    private static void showError(Control control1, Control control2, Label label, String errorStyleClass, String errorMessage) {
        control1.getStyleClass().add(errorStyleClass);
        control2.getStyleClass().add(errorStyleClass);
        label.setText(errorMessage);
        label.setVisible(true);
    }

    private static void hideError(Control control, Label label, String errorStyleClass) {
        control.getStyleClass().remove(errorStyleClass);
        label.setVisible(false);
    }

    public static List<FileError> validateSalaryFile(File file) {
        String avsNumberRegex = "^756.\\d{4}.\\d{4}.\\d{2}$";
        List<FileError> errors = new ArrayList<>();
        List<Salary> salaries = FileUtil.processSalaryCsvFiles(file);
        String invalidNumberMsg = "Invalid number value";
        String invalidNumberFormatMsg = "Invalid number format";
        String invalidAvsNumberFormatMsg = "Invalid AVS number format";
        String invalidDateOrderMsg = "Invalid Date Order";
        String invalidDate = "Invalid date";
        int lineNo = 1;
        for (Salary salary : salaries) {
            String avsNumber = salary.getAvsNumber();
            if (!avsNumber.matches(avsNumberRegex)) {
                errors.add(FileError.builder()
                        .lineNumber(lineNo)
                        .errorValue(avsNumber)
                        .errorMessage(invalidAvsNumberFormatMsg)
                        .build());

            }
            String startDate = salary.getStartDate();
            String endDate = salary.getEndDate();
            if (isValidDate(startDate) && isValidDate(endDate)) {
                if (!isDateBefore(startDate, endDate)) {
                    errors.add(FileError.builder()
                            .lineNumber(lineNo)
                            .errorValue(startDate + " and " + endDate)
                            .errorMessage(invalidDateOrderMsg)
                            .build());
                }
            } else {
                errors.add(FileError.builder()
                        .lineNumber(lineNo)
                        .errorValue(startDate + " or " + endDate)
                        .errorMessage(invalidDate)
                        .build());
            }
            String avsAmountString = salary.getAvsAmount();
            try {
                BigDecimal avsAmount = new BigDecimal(avsAmountString);
                if (isNegativeBigDecimal(avsAmount)) {
                    errors.add(FileError.builder()
                            .lineNumber(lineNo)
                            .errorValue(avsAmountString)
                            .errorMessage(invalidNumberMsg)
                            .build());
                }
            } catch (NumberFormatException e) {
                errors.add(FileError.builder()
                        .lineNumber(lineNo)
                        .errorValue(avsAmountString)
                        .errorMessage(invalidNumberFormatMsg)
                        .build());
            }
            String acAmountString = salary.getAcAmount();
            try {
                BigDecimal acAmount = new BigDecimal(acAmountString);
                if (isNegativeBigDecimal(acAmount)) {
                    errors.add(FileError.builder()
                            .lineNumber(lineNo)
                            .errorValue(acAmountString)
                            .errorMessage(invalidNumberMsg)
                            .build());
                }
            } catch (NumberFormatException e) {
                errors.add(FileError.builder()
                        .lineNumber(lineNo)
                        .errorValue(acAmountString)
                        .errorMessage(invalidNumberFormatMsg)
                        .build());
            }
            String afAmountString = salary.getAfAmount();
            try {
                BigDecimal afAmount = new BigDecimal(afAmountString);
                if (isNegativeBigDecimal(afAmount)) {
                    errors.add(FileError.builder()
                            .lineNumber(lineNo)
                            .errorValue(afAmountString)
                            .errorMessage(invalidNumberMsg)
                            .build());
                }
            } catch (NumberFormatException e) {
                errors.add(FileError.builder()
                        .lineNumber(lineNo)
                        .errorValue(afAmountString)
                        .errorMessage(invalidNumberFormatMsg)
                        .build());
            }
            lineNo++;
        }
        return errors;
    }

    private static boolean isDateBefore(String dateStringBefore, String dateStringAfter) {
        try {
            LocalDate dateBefore = LocalDate.parse(dateStringBefore, DATE_FORMATTER);
            LocalDate dateAfter = LocalDate.parse(dateStringAfter, DATE_FORMATTER);
            return dateBefore.isBefore(dateAfter);
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isNegativeBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal.compareTo(new BigDecimal(0)) < 0;
    }
}
