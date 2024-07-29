package util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;
import config.ErrorConfig;
import model.Salary;
import model.SalaryError;
import model.SalaryFileResult;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class.getName());

    private FileUtil() {
    }

    public static SalaryFileResult processSalaryCsvFiles(File file) {
        String invalidNumber = "Invalid number value";
        String invalidAvsNumberFormat = "Invalid AVS number format";
        String duplicateAvsNumber = "Duplicate AVS number";
        String invalidDateOrder = "Invalid Date Order";
        String invalidDate = "Invalid date";
        String invalidRequiredName = "The name is required";
        List<Salary> salaries = new ArrayList<>();
        List<SalaryError> errors = new ArrayList<>();
        Map<String, List<Salary>> mapSalary = new HashMap<>(); // Key: Avs number. Value: list of salary has that AVS number
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
                                String avsNumber = line[i];
                                salary.setAvsNumber(avsNumber);
                                if (!ValidationUtil.isValidAvsNumber(avsNumber)) {
                                    hasError = true;
                                    errorMessage.append(invalidAvsNumberFormat).append(";");
                                }
                                // Check duplicate avs number in the file
                                if (mapSalary.containsKey(avsNumber)) {
                                    // Put to existed list salary with the same Avs number
                                    // and update the duplicate error message for the previous items
                                    mapSalary.get(avsNumber).add(salary);
                                    for (SalaryError er : errors) {
                                        if (er.getSalary().getAvsNumber().equals(avsNumber)) {
                                            er.setMessage(duplicateAvsNumber + ";" + er.getMessage());
                                        }
                                    }
                                    errorMessage.append(duplicateAvsNumber).append(";");
                                    hasError = true;
                                } else {
                                    // Put as new item
                                    List<Salary> duplicateItems = new ArrayList<>();
                                    duplicateItems.add(salary);
                                    mapSalary.put(avsNumber, duplicateItems);
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
                            case "EmployerIdeNumber":
                                String employerIdeNumber = line[i];
                                salary.setEmployerIdeNumber(employerIdeNumber);
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

    public static String writErrorCsvFile(String fileName, String[] header, List<String[]> data) {
        SimpleDateFormat sdf = new SimpleDateFormat(ErrorConfig.ERROR_DATE_FILE_NAME_PATTERN);
        String dateTime = sdf.format(new Date());
        fileName += "_errors_" + dateTime + ".csv";
        // Create the directory if it does not exist
        File directory = new File(ErrorConfig.ERROR_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = ErrorConfig.ERROR_DIR + File.separator + fileName;
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(header);
            for (String[] datum : data) {
                writer.writeNext(datum);
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return fileName;
    }
}
