package vn.elca.training.pilot_project_front.util;

import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import vn.elca.training.pilot_project_front.constant.DatePattern;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
}
