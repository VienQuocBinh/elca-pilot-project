package vn.elca.training.pilot_project_front.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import vn.elca.training.pilot_project_front.model.ErrorDetail;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonStringify {
    private JsonStringify() {
    }

    public static List<ErrorDetail> convertStringErrorDetailToList(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsonArrayString = jsonString.substring(jsonString.indexOf("["));
            List<ErrorDetail> errorDetails = objectMapper.readValue(jsonArrayString, new TypeReference<List<ErrorDetail>>() {
            });
            for (ErrorDetail errorDetail : errorDetails) {
                System.out.println(errorDetail.getObject());
                System.out.println(errorDetail.getField());
                System.out.println(errorDetail.getValue());
                System.out.println(errorDetail.getMessage());
                System.out.println(errorDetail.getFxErrorKey());
            }
            return errorDetails;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
