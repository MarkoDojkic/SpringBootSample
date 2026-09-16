package dev.markodojkic.grpc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.Test;

class EligibilityGrpcEndpointTest {
    @Test
    void shouldReturnEligibleResponse() {
        @SuppressWarnings("unchecked")
        StreamObserver<EligibilityResponse> observer = mock(StreamObserver.class);

        new EligibilityGrpcEndpoint().checkEligibility(
                EligibilityRequest.newBuilder().setPatientId("P123").build(), observer);

        var response = org.mockito.ArgumentCaptor.forClass(EligibilityResponse.class);
        verify(observer).onNext(response.capture());
        verify(observer).onCompleted();
        assertEquals("P123", response.getValue().getPatientId());
        assertEquals("ELIGIBLE:P123", response.getValue().getMessage());
    }
}
