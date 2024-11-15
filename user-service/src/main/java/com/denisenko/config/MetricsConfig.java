package com.denisenko.config;

import io.smallrye.config.ConfigMapping;

@ConfigMapping(prefix = "metrics.config")
public interface MetricsConfig {
    boolean histogramEnabled();

    String percentiles();
}
