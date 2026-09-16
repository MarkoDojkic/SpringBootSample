package dev.markodojkic.api;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.r4.model.Patient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fhir")
public class FhirController {
    private final FhirContext fhirContext = FhirContext.forR4();

    @PostMapping("/patient")
    public String patient(
            @RequestParam(name = "familyName") String familyName,
            @RequestParam(name = "givenName") String givenName) {
        Patient patient = new Patient();
        patient.addName().setFamily(familyName).addGiven(givenName);
        return fhirContext.newJsonParser().setPrettyPrint(true).encodeResourceToString(patient);
    }
}
