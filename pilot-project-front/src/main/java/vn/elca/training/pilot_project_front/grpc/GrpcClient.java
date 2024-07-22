package vn.elca.training.pilot_project_front.grpc;

import io.grpc.ManagedChannel;
import vn.elca.training.proto.employer.EmployerListResponse;
import vn.elca.training.proto.employer.EmployerSearchRequest;
import vn.elca.training.proto.employer.EmployerServiceGrpc;

public class GrpcClient {
    private final EmployerServiceGrpc.EmployerServiceBlockingStub asyncStub;

    public GrpcClient(ManagedChannel channel) {
        asyncStub = EmployerServiceGrpc.newBlockingStub(channel);
    }

    public EmployerListResponse search(EmployerSearchRequest request) {
        return asyncStub.getEmployers(request);
    }
}
