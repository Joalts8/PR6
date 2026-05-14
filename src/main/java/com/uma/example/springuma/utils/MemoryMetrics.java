package com.uma.example.springuma.utils;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class MemoryMetrics {

    public MemoryMetrics(MeterRegistry registry) {
        Gauge.builder("memoria_consumo_porcentaje", this, MemoryMetrics::getMemoryUsagePercentage)
                .description("Porcentaje de consumo de memoria")
                .register(registry);
    }

    private double getMemoryUsagePercentage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        return (double) usedMemory / maxMemory * 100;
    }
}
