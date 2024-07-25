package vn.elca.training.pilot_project_front.util;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import vn.elca.training.pilot_project_front.model.Salary;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class.getName());

    private FileUtil() {
    }

    public static List<Salary> processSalaryCsvFiles(File file) {
        List<Salary> salaries = new ArrayList<>();
        if (file.getName().endsWith(".csv")) {
            try (CSVReader reader = new CSVReader(new FileReader(file))) {
                String[] header = reader.readNext(); // Read the header
                String[] line;
                while ((line = reader.readNext()) != null) {
                    Salary salary = new Salary();
                    for (int i = 0; i < header.length; i++) {
                        switch (header[i]) {
                            case "AVSNumber":
                                salary.setAvsNumber(line[i]);
                                break;
                            case "LastName":
                                salary.setLastName(line[i]);
                                break;
                            case "FirstName":
                                salary.setFirstName(line[i]);
                                break;
                            case "StartDate":
                                salary.setStartDate(line[i]);
                                break;
                            case "EndDate":
                                salary.setEndDate(line[i]);
                                break;
                            case "AVSAmount":
                                salary.setAvsAmount(line[i]);
                                break;
                            case "ACAmount":
                                salary.setAcAmount(line[i]);
                                break;
                            case "AFAmount":
                                salary.setAfAmount(line[i]);
                                break;
                            default:
                                break;
                        }
                    }
                    salaries.add(salary);
                }
            } catch (IOException | CsvValidationException ex) {
                log.warning(ex.getMessage());
            }
        }
        return salaries;
    }
}
