package vn.elca.training.pilot_project_front.util;

import vn.elca.training.pilot_project_front.constant.DatePattern;
import vn.elca.training.pilot_project_front.model.FileError;
import vn.elca.training.pilot_project_front.model.Salary;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ValidationUtil {
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DatePattern.PATTERN);

    public List<FileError> validateSalaryFile(File file) {
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

    private boolean isValidDate(String date) {
        try {
            simpleDateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isDateBefore(String dateStringBefore, String dateStringAfter) {
        try {
            Date dateBefore = simpleDateFormat.parse(dateStringBefore);
            Date dateAfter = simpleDateFormat.parse(dateStringAfter);
            return dateBefore.toInstant().isBefore(dateAfter.toInstant());
        } catch (ParseException e) {
            return false;
        }
    }

    private boolean isNegativeBigDecimal(BigDecimal bigDecimal) {
        return bigDecimal.compareTo(new BigDecimal(0)) < 0;
    }
}
