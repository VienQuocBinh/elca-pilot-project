package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.exception.handler.GrpcExceptionHandler;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.service.EmployerService;
import vn.elca.training.proto.employer.EmployerId;
import vn.elca.training.proto.employer.EmployerListResponse;
import vn.elca.training.proto.employer.EmployerResponse;
import vn.elca.training.proto.employer.EmployerServiceGrpc;
import vn.elca.training.proto.employer.Empty;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class EmployerServiceGrpcImpl extends EmployerServiceGrpc.EmployerServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(EmployerServiceGrpcImpl.class);
    private final EmployerService employerService;
    private final EmployerMapper employerMapper;

    @Override
    public void getEmployerById(EmployerId request, StreamObserver<EmployerResponse> responseObserver) {
        try {
            EmployerResponseDto employerById = employerService.getEmployerById(request.getId());
            responseObserver.onNext(employerMapper.dtoToProtoResponse(employerById));
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }

    @Override
    public void getEmployers(Empty request, StreamObserver<EmployerListResponse> responseObserver) {
        List<EmployerResponseDto> employers = employerService.getEmployers();
        List<EmployerResponse> responses = employers.stream()
                .map(employerMapper::dtoToProtoResponse)
                .collect(Collectors.toList());
        responseObserver.onNext(EmployerListResponse.newBuilder()
                .addAllEmployers(responses)
                .build());
        responseObserver.onCompleted();
    }
}
