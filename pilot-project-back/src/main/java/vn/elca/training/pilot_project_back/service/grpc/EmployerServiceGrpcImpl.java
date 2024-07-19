package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import net.devh.boot.grpc.server.service.GrpcService;
import vn.elca.training.pilot_project_back.constant.PensionType;
import vn.elca.training.pilot_project_back.dto.EmployerResponseDto;
import vn.elca.training.pilot_project_back.entity.Employer;
import vn.elca.training.pilot_project_back.mapper.EmployerMapper;
import vn.elca.training.pilot_project_back.mapper.PensionTypeMapper;
import vn.elca.training.pilot_project_back.repository.EmployerRepository;
import vn.elca.training.pilot_project_back.service.EmployerService;
import vn.elca.training.proto.employer.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@GrpcService
@RequiredArgsConstructor
public class EmployerServiceGrpcImpl extends EmployerServiceGrpc.EmployerServiceImplBase {
    private final EmployerRepository employerRepository;
    private final EmployerService employerService;
    private final EmployerMapper employerMapper;

    @Override
    public void getEmployerById(EmployerId request, StreamObserver<EmployerResponse> responseObserver) {
        super.getEmployerById(request, responseObserver);
    }

    @Override
    public void getEmployers(Empty request, StreamObserver<EmployerListResponse> responseObserver) {
        employerRepository.save(Employer.builder()
                .name("Test")
                .pensionType(PensionType.REGIONAL)
                .ideNumber("ide 12391")
                .expiredDate(new Date())
                .build());
        List<Employer> all = employerRepository.findAll();
        List<EmployerResponseDto> employers = employerService.getEmployers();
        List<EmployerResponse> collect1 = employers.stream().map(employerMapper::mapDtoToProtoResponse).collect(Collectors.toList());

        List<EmployerResponse> collect = all.stream().map(employer -> EmployerResponse.newBuilder()
                        .setId(employer.getId())
                        .setName(employer.getName())
                        .setPensionType(PensionTypeMapper.mapEntityToProto(employer.getPensionType()))
                        .setNumber(employer.getNumber())
                        .setIdeNumber(employer.getIdeNumber())
                        .setCreatedDate(String.valueOf(employer.getCreatedDate()))
                        .setUpdatedDate(String.valueOf(employer.getUpdatedDate()))
                        .setExpiredDate(String.valueOf(employer.getExpiredDate()))
                        .build())
                .collect(Collectors.toList());
        responseObserver.onNext(EmployerListResponse.newBuilder()
                .addAllEmployers(collect)
                .build());
        responseObserver.onCompleted();
    }
}
