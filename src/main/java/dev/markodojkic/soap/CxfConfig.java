package dev.markodojkic.soap;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class CxfConfig {
    private final Bus bus;
    private final EligibilityService service;

    public CxfConfig(Bus bus, EligibilityService service) {
        this.bus = bus;
        this.service = service;
    }

    @PostConstruct
    void publish() {
        EndpointImpl endpoint = new EndpointImpl(bus, service);
        endpoint.publish("/eligibility");
    }
}
