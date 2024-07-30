package vn.elca.training.pilot_project_front.util;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import vn.elca.training.pilot_project_front.constant.DatePattern;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.UnaryOperator;

public class TextFieldUtil {
    public static final String IDE_NUMBER_PATTERN = "YYY-xxx.xxx.xxx";

    private TextFieldUtil() {
    }

    public static void applyAlphabeticFilter(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z ]*")) {
                return change;
            }
            return null;
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    public static void applyDateFilter(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[0-9.]*") && newText.length() <= 10) {
                return change;
            }
            return null;
        };
        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    public static StringConverter<LocalDate> dateStringConverter() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DatePattern.PATTERN);
        return new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return formatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, formatter);
                    } catch (DateTimeParseException e) {
                        // Handle parse exception if needed
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };
    }

    public static TextFormatter<String> applyIdeNumberTextFormatter(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            int caretPosition = change.getCaretPosition();
            if (change.isDeleted() && change.getText().isEmpty()) {
                char caretChar = IDE_NUMBER_PATTERN.charAt(caretPosition);
                if (caretChar == 'Y' || caretChar == 'x' || caretChar == '.' || caretChar == '-') {
                    return replaceCharacter(textField, change, caretPosition, caretChar);
                } else {
                    return null; // Block if not match the pattern
                }
            }
            // Ensure the input is a single character
            if (change.getText().length() != 1) {
                return null;
            }
            char newChar = change.getText().charAt(0);
            int patternIndex = caretPosition - 1;
            if (patternIndex >= IDE_NUMBER_PATTERN.length()) {
                return null; // Block input if it is out of pattern range
            }
            char patternChar = IDE_NUMBER_PATTERN.charAt(patternIndex);

            if ((patternChar == 'Y' && Character.isLetter(newChar))
                    || (patternChar == 'x' && Character.isDigit(newChar))
                    || (patternChar == '-' && Character.isDigit(newChar)
                    || (patternChar == '.' && Character.isDigit(newChar)))) {
                return replaceCharacter(textField, change, patternIndex, newChar);
            }
            return null; // Block input if it doesn't match the expected character
        };

        return new TextFormatter<>(filter);
    }

    private static TextFormatter.Change replaceCharacter(TextField textField, TextFormatter.Change change, int index, char newChar) {
        StringBuilder currentText = new StringBuilder(textField.getText());

        // Ensure '-' and '.' are not inserted again if they already exist
        if ((change.getCaretPosition() == 4 && currentText.charAt(index) == '-')
                || (change.getCaretPosition() == 8 && currentText.charAt(index) == '.')
                || (change.getCaretPosition() == 12 && currentText.charAt(index) == '.')) {
            currentText.setCharAt(index, currentText.charAt(index));
            String newString = currentText.substring(0, Math.min(currentText.length() + 1, IDE_NUMBER_PATTERN.length()));
            textField.setText(newString);

            currentText.setCharAt(index + 1, newChar);
            newString = currentText.substring(0, Math.min(currentText.length() + 2, IDE_NUMBER_PATTERN.length()));
            textField.setText(newString);

            change.setCaretPosition(index + 2);
            textField.positionCaret(change.getCaretPosition());
            change.setAnchor(index + 2);
            change.setRange(index + 2, index + 2);
            change.setText(newString);
            change.setRange(0, newString.length());
            return change;
        }
        currentText.setCharAt(index, newChar);

        // Set the updated text
        String newString = currentText.substring(0, Math.min(currentText.length(), IDE_NUMBER_PATTERN.length()));
        textField.setText(newString);

        textField.positionCaret(index + 1);

        // Return the change object with the updated text
        change.setText(newString);
        change.setRange(0, newString.length());
        return change;
    }
}
