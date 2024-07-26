package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.service.SalaryService;
import vn.elca.training.proto.common.EmployerId;
import vn.elca.training.proto.salary.SalaryListResponse;
import vn.elca.training.proto.salary.SalaryServiceGrpc;

import java.util.List;

@GrpcService
@RequiredArgsConstructor
public class SalaryServiceGrpcImpl extends SalaryServiceGrpc.SalaryServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(SalaryServiceGrpcImpl.class);
    private final SalaryService salaryService;
    private final SalaryMapper salaryMapper;

    @Override
    public void getSalariesByEmployerId(EmployerId request, StreamObserver<SalaryListResponse> responseObserver) {
        try {
            List<SalaryResponseDto> salaryResponseDtos = salaryService.getSalariesByEmployerId(request.getId());
            responseObserver.onNext(SalaryListResponse
                    .newBuilder()
                    .addAllSalaries(salaryMapper.mapResponseDtoListToResponseProtoList(salaryResponseDtos))
                    .build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            log.error(e.getMessage());
            responseObserver.onError(e);
        }
    }
}
