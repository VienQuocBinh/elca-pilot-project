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
            return objectMapper.readValue(jsonArrayString, new TypeReference<List<ErrorDetail>>() {
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}
