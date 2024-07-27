package util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import model.Salary;
import model.SalaryError;
import model.SalaryFileResult;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class.getName());

    private FileUtil() {
    }

    public static SalaryFileResult processSalaryCsvFiles(File file) {
        String invalidNumber = "Invalid number value";
        String invalidAvsNumberFormat = "Invalid AVS number format";
        String invalidDateOrder = "Invalid Date Order";
        String invalidDate = "Invalid date";
        String invalidRequiredName = "The name is required";
        List<Salary> salaries = new ArrayList<>();
        List<SalaryError> errors = new ArrayList<>();
        if (file.getName().endsWith(".csv")) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] header = reader.readNext(); // Read the header
                String[] line;
                long lineNo = 0;
                while ((line = reader.readNext()) != null) {
                    Salary salary = new Salary();
                    boolean hasError = false;
                    StringBuilder errorMessage = new StringBuilder();
                    String startDate = "";
                    for (int i = 0; i < header.length; i++) {
                        switch (header[i]) {
                            case "AVSNumber":
                                salary.setAvsNumber(line[i]);
                                if (!ValidationUtil.isValidAvsNumber(line[i])) {
                                    salary.setAvsNumber(line[i]);
                                    hasError = true;
                                    errorMessage.append(invalidAvsNumberFormat).append(";");
                                }
                                break;
                            case "LastName":
                                salary.setLastName(line[i]);
                                if (line[i] == null || line[i].trim().isEmpty()) {
                                    hasError = true;
                                    errorMessage.append(invalidRequiredName).append(";");
                                }
                                break;
                            case "FirstName":
                                salary.setFirstName(line[i]);
                                if (line[i] == null || line[i].trim().isEmpty()) {
                                    hasError = true;
                                    errorMessage.append(invalidRequiredName).append(";");
                                }
                                break;
                            case "StartDate":
                                startDate = line[i];
                                salary.setStartDate(startDate);
                                if (!ValidationUtil.isValidDate(startDate)) {
                                    hasError = true;
                                    errorMessage.append(invalidDate).append(";");
                                }
                                break;
                            case "EndDate":
                                String endDate = line[i];
                                salary.setEndDate(endDate);
                                if (!ValidationUtil.isValidDate(endDate)) {
                                    hasError = true;
                                    errorMessage.append(invalidDate).append(";");
                                } else if (!ValidationUtil.isDateBefore(startDate, endDate)) {
                                    hasError = true;
                                    errorMessage.append(invalidDateOrder).append(";");
                                }
                                break;
                            case "AVSAmount":
                                String avsAmount = line[i];
                                salary.setAvsAmount(avsAmount);
                                if (avsAmount == null || avsAmount.trim().isEmpty() || ValidationUtil.isNegativeBigDecimal(avsAmount)) {
                                    hasError = true;
                                    errorMessage.append(invalidNumber).append(";");
                                }
                                break;
                            case "ACAmount":
                                String acAmount = line[i];
                                salary.setAcAmount(acAmount);
                                if (acAmount == null || acAmount.trim().isEmpty() || ValidationUtil.isNegativeBigDecimal(acAmount)) {
                                    hasError = true;
                                    errorMessage.append(invalidNumber).append(";");
                                }
                                break;
                            case "AFAmount":
                                String afAmount = line[i];
                                salary.setAfAmount(afAmount);
                                if (afAmount == null || afAmount.trim().isEmpty() || ValidationUtil.isNegativeBigDecimal(afAmount)) {
                                    hasError = true;
                                    errorMessage.append(invalidNumber).append(";");
                                }
                                break;
                            default:
                                break;
                        }
                    }
                    lineNo++;
                    salary.setId(lineNo);
                    if (hasError)
                        errors.add(SalaryError.builder()
                                .salary(salary)
                                .message(errorMessage.toString())
                                .build());
                    else
                        salaries.add(salary);
                }
            } catch (IOException | CsvValidationException ex) {
                log.warning(ex.getMessage());
            }
        }
        return SalaryFileResult.builder()
                .salaries(salaries)
                .errors(errors)
                .build();
    }

    public static void writeCsvFile(String fileName, String[] header, List<String[]> data) {
        String directoryPath = "error";
        String dateTimePattern = "yyyyMMdd_HHmmss";
        SimpleDateFormat sdf = new SimpleDateFormat(dateTimePattern);
        String dateTime = sdf.format(new Date());
        fileName += "_errors_" + dateTime + ".csv";
        // Create the directory if it does not exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = directoryPath + File.separator + fileName;
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(header);
            for (String[] datum : data) {
                writer.writeNext(datum);
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }

    }
}
