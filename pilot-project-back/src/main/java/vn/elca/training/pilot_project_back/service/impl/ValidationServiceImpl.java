package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.exception.model.ErrorDetail;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.ValidationService;
import vn.elca.training.proto.employer.EmployerCreateRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final SimpleDateFormat simpleDateFormat;
    private final EmployerRepository employerRepository;
    @Value("${ide.regexp}")
    private String ideNumberRegex;

    @Override
    public void validateEmployerCreateRequestProto(EmployerCreateRequest createRequest) throws ValidationException, ParseException {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        String ideNumberField = "ideNumber";
        String dateCreationField = "dateCreation";
        String dateExpirationField = "dateExpiration";
        // Validate ideNumber
        String ideNumber = createRequest.getIdeNumber();
        if (!ideNumber.isEmpty()) {
            if (!Pattern.matches(ideNumberRegex, ideNumber)) {
                errorDetails.add(ErrorDetail.builder()
                        .object(createRequest.getClass().getSimpleName())
                        .field(ideNumberField)
                        .value(ideNumber)
                        .message("Invalid ideNumber format")
                        .fxErrorKey("error.ideNumber.format")
                        .build());
            }
            // Check duplicate ide number
            employerRepository.findByIdeNumber(createRequest.getIdeNumber())
                    .ifPresent(employer -> errorDetails.add(ErrorDetail.builder()
                            .object(createRequest.getClass().getSimpleName())
                            .field(ideNumberField)
                            .value(employer.getIdeNumber())
                            .message("IDE Number is already existed")
                            .fxErrorKey("error.ideNumber.duplicate")
                            .build()));
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(ideNumberField)
                    .value(ideNumber)
                    .message("IDE Number is required")
                    .fxErrorKey("error.ideNumber.required")
                    .build());
        }
        // Validate dateCreation
        String dateCreation = createRequest.getDateCreation();
        if (!dateCreation.isEmpty()) {
            if (!isValidDate(dateCreation))
                errorDetails.add(ErrorDetail.builder()
                        .object(createRequest.getClass().getSimpleName())
                        .field(dateCreationField)
                        .value(dateCreation)
                        .message("Invalid date format for creation date")
                        .fxErrorKey("error.dateCreation.format")
                        .build());
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(dateCreationField)
                    .value(dateCreation)
                    .message("Creation date is required")
                    .fxErrorKey("error.dateCreation.required")
                    .build());
        }
        // Validate dateExpiration
        String dateExpiration = createRequest.getDateExpiration();
        if (!dateExpiration.isEmpty() && !isValidDate(dateExpiration)) {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(dateExpirationField)
                    .value(dateExpiration)
                    .message("Invalid date format for expiration date")
                    .fxErrorKey("error.dateExpiration.format")
                    .build());
        }

        // Validate create date is before expiration date
        if (!dateCreation.isEmpty()
                && isValidDate(dateCreation)
                && !dateExpiration.isEmpty()
                && isValidDate(dateExpiration)
                && !isDateBefore(dateCreation, dateExpiration)) {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field("dateCreation and dateExpiration")
                    .value(dateExpiration)
                    .message("Created Date must be before Expired Date")
                    .fxErrorKey("error.dateOrder")
                    .build());
        }

        if (!errorDetails.isEmpty()) {
            throw new ValidationException(errorDetails);
        }
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
}
