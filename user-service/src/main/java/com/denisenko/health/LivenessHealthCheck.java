package com.denisenko.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.jboss.logging.Logger;

@Liveness
@ApplicationScoped
public class LivenessHealthCheck implements HealthCheck {
    private static final Logger LOG = Logger.getLogger(LivenessHealthCheck.class);

    @Override
    public HealthCheckResponse call() {
        LOG.info("Liveness probe called");
        return HealthCheckResponse.up("Liveness check");
    }
}
