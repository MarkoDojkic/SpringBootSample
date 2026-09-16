package dev.markodojkic.soap;

import jakarta.jws.WebService;
import org.springframework.stereotype.Service;

@Service
@WebService(
        serviceName = "EligibilityService",
        portName = "EligibilityPort",
        targetNamespace = "http://soap.markodojkic.dev/",
        endpointInterface = "dev.markodojkic.soap.EligibilityService")
public class EligibilityServiceImpl implements EligibilityService {
    @Override
    public String checkEligibility(String patientId) {
        return "ELIGIBLE:" + patientId;
    }
}
