package com.denisenko.health;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import org.jboss.logging.Logger;

@Readiness
@ApplicationScoped
public class ReadinessHealthCheck implements HealthCheck {
    private static final Logger LOG = Logger.getLogger(ReadinessHealthCheck.class);

    @Override
    public HealthCheckResponse call() {
        LOG.info("Readiness probe called");
        return HealthCheckResponse.up("Readiness check");
    }
}
