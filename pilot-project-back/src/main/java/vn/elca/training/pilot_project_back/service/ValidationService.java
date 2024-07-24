package vn.elca.training.pilot_project_back.service;

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
public class ValidationService {
    private final SimpleDateFormat simpleDateFormat;

    public void validateEmployerSearchRequestDto(EmployerSearchRequest searchRequestDto) throws ValidationException {
        List<String> errors = new ArrayList<>();
        // Validate ideNumber
        String ideNumber = searchRequestDto.getIdeNumber();
        String pattern = "^(CHE|ADM)-\\d{3}.\\d{3}.\\d{3}";
        if (!ideNumber.isEmpty() && !Pattern.matches(pattern, ideNumber)) {
            errors.add("Invalid ideNumber format: " + ideNumber);
        }
        // Validate dateCreation
        String dateCreation = searchRequestDto.getDateCreation();
        if (!dateCreation.isEmpty() && isValidDate(dateCreation)) {
            errors.add("Invalid date format for creation date: " + dateCreation);
        }
        // Validate dateExpiration
        String dateExpiration = searchRequestDto.getDateExpiration();
        if (!dateExpiration.isEmpty() && isValidDate(dateExpiration)) {
            errors.add("Invalid date format for expiration date: " + dateExpiration);
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
