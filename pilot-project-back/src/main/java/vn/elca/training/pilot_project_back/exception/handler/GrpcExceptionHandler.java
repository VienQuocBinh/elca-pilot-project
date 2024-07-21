package vn.elca.training.pilot_project_back.exception.handler;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;

@Slf4j
public class GrpcExceptionHandler {
    public static StatusRuntimeException handleException(Exception e) {
        Status status;
        if (e instanceof EntityNotFoundException) {
            status = Status.NOT_FOUND
                    .withDescription(e.getMessage())
                    .withCause(e);
        } else {
            status = Status.UNKNOWN.withDescription("Unknown error occurred").withCause(e);
        }
        log.error("Exception: " + e.getMessage());
        return status.asRuntimeException();
    }
}

