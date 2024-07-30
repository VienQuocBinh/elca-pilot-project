package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import model.SalaryError;
import model.SalaryFileResult;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import util.FileUtil;
import util.SalaryHeaderBuild;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.dto.EmployerSearchRequestDto;
import vn.elca.training.pilot_project_back.dto.EmployerUpdateRequestDto;
import vn.elca.training.pilot_project_back.dto.SalaryCreateRequestDto;
import vn.elca.training.pilot_project_back.exception.AvsNumberExistedException;
import vn.elca.training.pilot_project_back.exception.EntityNotFoundException;
import vn.elca.training.pilot_project_back.exception.handler.GrpcExceptionHandler;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.mapper.SalaryMapper;
import vn.elca.training.pilot_project_back.service.EmployerService;
import vn.elca.training.pilot_project_back.service.ValidationService;
import vn.elca.training.proto.common.EmployerId;
import vn.elca.training.proto.common.Empty;
import vn.elca.training.proto.common.PagingResponse;
import vn.elca.training.proto.employer.*;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class EmployerServiceGrpcImpl extends EmployerServiceGrpc.EmployerServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(EmployerServiceGrpcImpl.class);
    private final EmployerService employerService;
    private final EmployerMapper employerMapper;
    private final SalaryMapper salaryMapper;
    private final ValidationService validationService;

    @Override
    public void getEmployerById(EmployerId request, StreamObserver<EmployerResponse> responseObserver) {
        try {
            EmployerResponseDto employerById = employerService.getEmployerById(request.getId());
            // The salariesList of proto is immutable, so need to map in separate method
            EmployerResponse employerResponse = employerMapper.mapResponseDtoToResponseProto(employerById);
            responseObserver.onNext(employerResponse.toBuilder()
                    .addAllSalaries(salaryMapper.mapResponseDtoListToResponseProtoList(employerById.getSalaries()))
                    .build());
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            log.error(e.getMessage());
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }

    @Override
    public void getEmployerNextNumber(Empty request, StreamObserver<EmployerNextNumberResponse> responseObserver) {
        responseObserver.onNext(EmployerNextNumberResponse.newBuilder()
                .setNumber(String.format("%06d", employerService.getEmployerNextNumber()))
                .build());
        responseObserver.onCompleted();
    }

    @Override
    public void getEmployers(EmployerSearchRequest request, StreamObserver<EmployerListResponse> responseObserver) {
        try {
            EmployerSearchRequestDto searchRequestDto = employerMapper.mapSearchRequestProtoToDto(request);
            Page<EmployerResponseDto> employers = employerService.getEmployers(searchRequestDto);
            List<EmployerResponse> responses = employers.getContent().stream()
                    .map(employerMapper::mapResponseDtoToResponseProto)
                    .collect(Collectors.toList());
            responseObserver.onNext(EmployerListResponse.newBuilder()
                    .setPagingResponse(PagingResponse.newBuilder()
                            .setPageIndex(request.getPagingRequest().getPageIndex())
                            .setTotalPages(employers.getTotalPages())
                            .setPageSize(employers.getPageable().getPageSize())
                            .setTotalElements(employers.getTotalElements())
                            .build())
                    .addAllEmployers(responses)
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }

    @Override
    public void createEmployer(EmployerCreateRequest request, StreamObserver<EmployerResponse> responseObserver) {
        try {
            EmployerResponseDto employer = employerService.createEmployer(employerMapper.mapCreateRequestProtoToCreateRequestDto(request));
            responseObserver.onNext(employerMapper.mapResponseDtoToResponseProto(employer));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }

    @Override
    public void deleteEmployer(EmployerId request, StreamObserver<Empty> responseObserver) {
        try {
            employerService.deleteEmployer(request.getId());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }

    @Override
    public void updateEmployer(EmployerUpdateRequest request, StreamObserver<EmployerResponse> responseObserver) {
        try {
            EmployerUpdateRequestDto employerUpdateRequestDto = employerMapper.mapUpdateRequestProtoToCreateRequestDto(request);
            List<SalaryCreateRequestDto> salaryCreateRequestDtos = salaryMapper.mapCreateListRequestProtoToDtoList(request.getSalariesList());

            SalaryFileResult salaryFileResult = validationService.validateFileSalary(salaryCreateRequestDtos);
            employerUpdateRequestDto.setSalaries(salaryCreateRequestDtos);
            if (!salaryFileResult.getErrors().isEmpty()) {
                String[] header = SalaryHeaderBuild.buildErrorHeader();
                FileUtil.writeErrorCsvFile("error", header, salaryFileResult.getErrors().stream().map(SalaryError::toStringArray).collect(Collectors.toList()));
            }
            EmployerResponseDto employerResponseDto = employerService.updateEmployer(employerUpdateRequestDto);
            EmployerResponse employerResponseProto = employerMapper.mapResponseDtoToResponseProto(employerResponseDto);
            if (!salaryFileResult.getErrors().isEmpty()) {
                throw new AvsNumberExistedException("Some Avs numbers are already existed. Please check \"error\" folder");
            }

            responseObserver.onNext(employerResponseProto.toBuilder()
                    .addAllSalaries(salaryMapper.mapResponseDtoListToResponseProtoList(employerResponseDto.getSalaries()))
                    .build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(GrpcExceptionHandler.handleException(e));
        }
    }
}
