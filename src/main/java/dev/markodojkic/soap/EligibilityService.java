package dev.markodojkic.soap;

import jakarta.jws.WebMethod;
import jakarta.jws.WebService;

@WebService(targetNamespace = "http://soap.markodojkic.dev/")
public interface EligibilityService {
    @WebMethod
    String checkEligibility(String patientId);
}
