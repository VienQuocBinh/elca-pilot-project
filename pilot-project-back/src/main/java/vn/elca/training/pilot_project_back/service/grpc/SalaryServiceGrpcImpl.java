package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import vn.elca.training.pilot_project_back.dto.SalaryResponseDto;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.service.SalaryService;
import vn.elca.training.proto.common.EmployerId;
import vn.elca.training.proto.common.FilePath;
import vn.elca.training.proto.common.PagingResponse;
import vn.elca.training.proto.salary.SalaryListRequest;
import vn.elca.training.proto.salary.SalaryListResponse;
import vn.elca.training.proto.salary.SalaryServiceGrpc;

@GrpcService
@RequiredArgsConstructor
public class SalaryServiceGrpcImpl extends SalaryServiceGrpc.SalaryServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(SalaryServiceGrpcImpl.class);
    private final SalaryService salaryService;
    private final SalaryMapper salaryMapper;

    @Override
    public void getSalariesByEmployerId(SalaryListRequest request, StreamObserver<SalaryListResponse> responseObserver) {
        try {
            Page<SalaryResponseDto> salaryResponseDtos = salaryService.getSalariesByEmployerId(request);
            responseObserver.onNext(SalaryListResponse
                    .newBuilder()
                    .setPagingResponse(PagingResponse.newBuilder()
                            .setPageIndex(request.getPagingRequest().getPageIndex())
                            .setPageSize(salaryResponseDtos.getPageable().getPageSize())
                            .setTotalElements(salaryResponseDtos.getTotalElements())
                            .setTotalPages(salaryResponseDtos.getTotalPages())
                            .build())
                    .addAllSalaries(salaryMapper.mapResponseDtoListToResponseProtoList(salaryResponseDtos.getContent()))
                    .build());
            responseObserver.onCompleted();
        } catch (StatusRuntimeException e) {
            log.error(e.getMessage());
            responseObserver.onError(e);
        }
    }

    @Override
    public void exportFilSalaries(EmployerId request, StreamObserver<FilePath> responseObserver) {
        try {
            String filePath = salaryService.exportSalariesFile(request.getId());
            responseObserver.onNext(FilePath.newBuilder().setPath(filePath).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            log.error(e.getMessage());
            responseObserver.onError(e);
        }
    }
}
