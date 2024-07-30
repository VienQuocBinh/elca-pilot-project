package vn.elca.training.pilot_project_back.service.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;
import vn.elca.training.proto.common.ConfigServiceGrpc;
import vn.elca.training.proto.common.Empty;
import vn.elca.training.proto.common.ScheduleEnabledResponse;

@GrpcService
public class ConfigServiceImpl extends ConfigServiceGrpc.ConfigServiceImplBase {
    @Value("${schedule.enabled}")
    private String scheduleEnabled;

    @Override
    public void getScheduleEnabled(Empty request, StreamObserver<ScheduleEnabledResponse> responseObserver) {
        ScheduleEnabledResponse response = ScheduleEnabledResponse.newBuilder()
                .setEnabled(Boolean.parseBoolean(scheduleEnabled))
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
