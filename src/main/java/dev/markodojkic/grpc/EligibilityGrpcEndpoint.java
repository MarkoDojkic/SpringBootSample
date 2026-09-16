package dev.markodojkic.grpc;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class EligibilityGrpcEndpoint extends EligibilityGrpcServiceGrpc.EligibilityGrpcServiceImplBase {
    @Override
    public void checkEligibility(EligibilityRequest request,
                                  StreamObserver<EligibilityResponse> responseObserver) {
        responseObserver.onNext(EligibilityResponse.newBuilder()
                .setPatientId(request.getPatientId())
                .setEligible(true)
                .setMessage("ELIGIBLE:" + request.getPatientId())
                .build());
        responseObserver.onCompleted();
    }
}
