package vn.elca.training.pilot_project_back.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.exception.ValidationException;
import vn.elca.training.pilot_project_back.exception.model.ErrorDetail;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
public class GrpcExceptionHandler {
    private GrpcExceptionHandler() {
    }

    public static StatusRuntimeException handleException(Exception e) {
        Status status;
        if (e instanceof EntityNotFoundException) {
            status = Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e);
        } else if (e instanceof ConstraintViolationException) {
            status = Status.INVALID_ARGUMENT
                    .withDescription(buildConstraintErrors(((ConstraintViolationException) e).getConstraintViolations()))
                    .withCause(e);
        } else if (e instanceof ValidationException) {
            status = Status.INVALID_ARGUMENT
                    .withDescription(jsonStringify(((ValidationException) e).getErrors()))
                    .withCause(e);
        } else {
            status = Status.UNKNOWN.withDescription(e.getMessage()).withCause(e);
        }
        log.error("Exception: {}", e.getMessage());
        return status.asRuntimeException();
    }

    private static String buildConstraintErrors(Set<ConstraintViolation<?>> constraintViolations) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            List<ErrorDetail> errorDetails = new ArrayList<>();
            for (ConstraintViolation<?> constraintViolation : constraintViolations) {
                errorDetails.add(ErrorDetail.builder()
                        .object(constraintViolation.getRootBeanClass().getSimpleName())
                        .field(((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().asString())
                        .value(constraintViolation.getInvalidValue())
                        .message(constraintViolation.getMessage())
                        .build());
            }
            return ow.writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static String jsonStringify(List<ErrorDetail> errorDetails) {
        try {
            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            return ow.writeValueAsString(errorDetails);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}

