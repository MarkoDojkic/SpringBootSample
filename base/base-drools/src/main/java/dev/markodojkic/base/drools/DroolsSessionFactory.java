package dev.markodojkic.base.drools;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;

public class DroolsSessionFactory {
    private final KieServices kieServices;

    public DroolsSessionFactory() {
        this(KieServices.Factory.get());
    }

    public DroolsSessionFactory(KieServices kieServices) {
        this.kieServices = kieServices;
    }

    public KieSession create(String sessionName) {
        return kieServices.newKieClasspathContainer().newKieSession(sessionName);
    }
}
