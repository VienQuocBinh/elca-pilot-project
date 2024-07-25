package vn.elca.training.pilot_project_back.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import vn.elca.training.pilot_project_back.constant.LanguageBundleKey;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.exception.model.ErrorDetail;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.ValidationService;
import vn.elca.training.proto.employer.EmployerCreateRequest;
import vn.elca.training.proto.employer.EmployerUpdateRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ValidationServiceImpl implements ValidationService {
    private final SimpleDateFormat simpleDateFormat;
    private final EmployerRepository employerRepository;
    @Value("${ide.regexp}")
    private String ideNumberRegex;

    @Override
    public void validateEmployerCreateRequestProto(EmployerCreateRequest createRequest) throws ValidationException {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        String nameField = "name";
        String ideNumberField = "ideNumber";
        String dateCreationField = "dateCreation";
        String dateExpirationField = "dateExpiration";
        // Validate name
        String name = createRequest.getName();
        if (name.trim().isEmpty()) {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(nameField)
                    .value(name)
                    .message("The name is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_NAME_REQUIRED)
                    .build());
        }
        // Validate ideNumber
        String ideNumber = createRequest.getIdeNumber();
        if (!ideNumber.isEmpty()) {
            if (!Pattern.matches(ideNumberRegex, ideNumber)) {
                errorDetails.add(ErrorDetail.builder()
                        .object(createRequest.getClass().getSimpleName())
                        .field(ideNumberField)
                        .value(ideNumber)
                        .message("Invalid ideNumber format")
                        .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_FORMAT)
                        .build());
            }
            // Check duplicate ide number
            employerRepository.findByIdeNumber(createRequest.getIdeNumber())
                    .ifPresent(employer -> errorDetails.add(ErrorDetail.builder()
                            .object(createRequest.getClass().getSimpleName())
                            .field(ideNumberField)
                            .value(employer.getIdeNumber())
                            .message("IDE Number is already existed")
                            .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_DUPLICATE)
                            .build()));
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(ideNumberField)
                    .value(ideNumber)
                    .message("IDE Number is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_REQUIRED)
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
                        .fxErrorKey(LanguageBundleKey.ERROR_DATE_CREATION_FORMAT)
                        .build());
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(createRequest.getClass().getSimpleName())
                    .field(dateCreationField)
                    .value(dateCreation)
                    .message("Creation date is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_CREATION_REQUIRED)
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
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_EXPIRATION_FORMAT)
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
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_ORDER)
                    .build());
        }

        if (!errorDetails.isEmpty()) {
            throw new ValidationException(errorDetails);
        }
    }

    @Override
    public void validateEmployerUpdateRequestProto(EmployerUpdateRequest updateRequest) throws ValidationException {
        List<ErrorDetail> errorDetails = new ArrayList<>();
        String nameField = "name";
        String ideNumberField = "ideNumber";
        String dateCreationField = "dateCreation";
        String dateExpirationField = "dateExpiration";
        // Validate name
        String name = updateRequest.getName();
        if (name.trim().isEmpty()) {
            errorDetails.add(ErrorDetail.builder()
                    .object(updateRequest.getClass().getSimpleName())
                    .field(nameField)
                    .value(name)
                    .message("The name is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_NAME_REQUIRED)
                    .build());
        }
        // Validate ideNumber
        String ideNumber = updateRequest.getIdeNumber();
        if (!ideNumber.isEmpty()) {
            Optional<Employer> employerOptional = employerRepository.findByIdeNumber(updateRequest.getIdeNumber());
            if (!Pattern.matches(ideNumberRegex, ideNumber)) {
                errorDetails.add(ErrorDetail.builder()
                        .object(updateRequest.getClass().getSimpleName())
                        .field(ideNumberField)
                        .value(ideNumber)
                        .message("Invalid ideNumber format")
                        .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_FORMAT)
                        .build());
            } else if (employerOptional.isPresent() && !employerOptional.get().getId().equals(updateRequest.getId())) {
                // Check duplicate ide number with others employer
                errorDetails.add(ErrorDetail.builder()
                        .object(updateRequest.getClass().getSimpleName())
                        .field(ideNumberField)
                        .value(updateRequest.getIdeNumber())
                        .message("IDE Number is already existed")
                        .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_DUPLICATE)
                        .build());
            }
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(updateRequest.getClass().getSimpleName())
                    .field(ideNumberField)
                    .value(ideNumber)
                    .message("IDE Number is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_IDE_NUMBER_REQUIRED)
                    .build());
        }
        // Validate dateCreation
        String dateCreation = updateRequest.getDateCreation();
        if (!dateCreation.isEmpty()) {
            if (!isValidDate(dateCreation))
                errorDetails.add(ErrorDetail.builder()
                        .object(updateRequest.getClass().getSimpleName())
                        .field(dateCreationField)
                        .value(dateCreation)
                        .message("Invalid date format for creation date")
                        .fxErrorKey(LanguageBundleKey.ERROR_DATE_CREATION_FORMAT)
                        .build());
        } else {
            errorDetails.add(ErrorDetail.builder()
                    .object(updateRequest.getClass().getSimpleName())
                    .field(dateCreationField)
                    .value(dateCreation)
                    .message("Creation date is required")
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_CREATION_REQUIRED)
                    .build());
        }
        // Validate dateExpiration
        String dateExpiration = updateRequest.getDateExpiration();
        if (!dateExpiration.isEmpty() && !isValidDate(dateExpiration))
            errorDetails.add(ErrorDetail.builder()
                    .object(updateRequest.getClass().getSimpleName())
                    .field(dateExpirationField)
                    .value(dateExpiration)
                    .message("Invalid date format for expiration date")
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_EXPIRATION_FORMAT)
                    .build());

        // Validate create date is before expiration date
        if (!dateCreation.isEmpty()
                && isValidDate(dateCreation)
                && !dateExpiration.isEmpty()
                && isValidDate(dateExpiration)
                && !isDateBefore(dateCreation, dateExpiration)) {
            errorDetails.add(ErrorDetail.builder()
                    .object(updateRequest.getClass().getSimpleName())
                    .field("dateCreation and dateExpiration")
                    .value(dateExpiration)
                    .message("Created Date must be before Expired Date")
                    .fxErrorKey(LanguageBundleKey.ERROR_DATE_ORDER)
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
