package vn.elca.training.pilot_project_back.util;

import com.opencsv.CSVWriter;
import config.ErrorConfig;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

public class FileUtil {
    private static final Logger log = Logger.getLogger(FileUtil.class.getName());

    public static String writeCsvFile(String dir, String fileName, String[] header, List<String[]> data) {
        SimpleDateFormat sdf = new SimpleDateFormat(ErrorConfig.ERROR_DATE_FILE_NAME_PATTERN);
        String dateTime = sdf.format(new Date());
        fileName += dateTime + ".csv";
        // Create the directory if it does not exist
        File directory = new File(dir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        String filePath = dir + File.separator + fileName;
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            writer.writeNext(header);
            for (String[] datum : data) {
                writer.writeNext(datum);
            }
        } catch (IOException e) {
            log.warning(e.getMessage());
        }
        return new File(filePath).getAbsolutePath();
    }
}
