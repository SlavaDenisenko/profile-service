package com.denisenko.config;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

import java.util.Arrays;

@Singleton
public class CustomMetricsConfig {
    private static final Logger LOG = Logger.getLogger(CustomMetricsConfig.class);

    @Inject
    MetricsConfig metricsConfig;

    @Produces
    @Singleton
    public MeterFilter enableHistogram() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                String[] percentiles = metricsConfig.percentiles().split(",");
                LOG.info("Percentiles=" + Arrays.toString(percentiles));
                if (!metricsConfig.histogramEnabled() || percentiles.length < 1) {
                    LOG.info("Histogram disabled or percentiles aren't configured");
                    return config;
                }

                double[] percentilesArray = new double[percentiles.length];
                for (int i = 0; i < percentiles.length; i++) {
                    percentilesArray[i] = Double.parseDouble(percentiles[i]);
                }

                if (id.getName().startsWith("http.server.requests")) {
                    return DistributionStatisticConfig.builder()
                            .percentilesHistogram(true)
                            .percentiles(percentilesArray)
                            .build()
                            .merge(config);
                }
                return config;
            }
        };
    }
}
