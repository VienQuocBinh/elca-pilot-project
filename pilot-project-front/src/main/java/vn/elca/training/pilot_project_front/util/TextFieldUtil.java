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
    private TextFieldUtil() {
    }

    public static void applyAlphabeticFilter(TextField textField) {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("[a-zA-Z]*")) {
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
}
