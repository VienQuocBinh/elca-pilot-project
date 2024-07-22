package vn.elca.training.pilot_project_back.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.proto.employer.EmployerSearchRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidationUtil {
    private final SimpleDateFormat simpleDateFormat;

    public void validateEmployerSearchRequestDto(EmployerSearchRequest searchRequestDto) throws ValidationException {
        List<String> errors = new ArrayList<>();
        // Validate ideNumber
        String ideNumber = searchRequestDto.getIdeNumber();
        String pattern = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}";
        if (!ideNumber.isEmpty() && !Pattern.matches(pattern, ideNumber)) {
            errors.add("Invalid ideNumber format: " + ideNumber);
        }
        // Validate createdDate
        String createdDate = searchRequestDto.getCreatedDate();
        if (!createdDate.isEmpty() && isValidDate(createdDate)) {
            errors.add("Invalid date format for createdDate: " + createdDate);
        }
        // Validate expiredDate
        String expiredDate = searchRequestDto.getExpiredDate();
        if (!expiredDate.isEmpty() && isValidDate(expiredDate)) {
            errors.add("Invalid date format for expiredDate: " + expiredDate);
        }
        if (!errors.isEmpty()) {
            throw new ValidationException(errors);
        }
    }

    private boolean isValidDate(String date) {
        try {
            simpleDateFormat.parse(date);
            return false;
        } catch (ParseException e) {
            return true;
        }
    }
}
