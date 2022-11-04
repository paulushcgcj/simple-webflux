package io.github.paulushcgcj.devopsdemo.configuration;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

@Configuration
public class MetricConfiguration {

  @Bean
  public TimedAspect timedAspect(MeterRegistry registry) {
    return new TimedAspect(registry);
  }

  @Bean
  public MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
    return registry -> {
      registry.config()
          .commonTags(tags())
          .meterFilter(ignoreTag())
          .meterFilter(distributionMeter());
    };
  }

  private MeterFilter distributionMeter() {
    return new MeterFilter() {
      @Override
      public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
        return DistributionStatisticConfig
            .builder()
            .percentilePrecision(5)
            .percentilesHistogram(true)
            .percentiles(0.5, 0.95, 0.99)
            .serviceLevelObjectives(0.1,0.01,0.001,0.0001,0.00001,0.000001)
            .build()
            .merge(config);
      }
    };
  }

  private MeterFilter ignoreTag() {
    return MeterFilter.ignoreTags("type");
  }

  private String[] tags(){
    return new String[]{"version", "1.0.0","product", "company"};
  }

  @Bean
  public MeterRegistryCustomizer<PrometheusMeterRegistry> prometheusConfiguration() {
    return MeterRegistry::config;
  }

  @Bean
  @ConditionalOnProperty(value = "spring.zipkin.enabled", havingValue = "false")
  public SpanHandler spanHandler() {
    return new SpanHandler() {
      @Override
      public boolean end(TraceContext context, MutableSpan span, Cause cause) {
        return true;
      }
    };
  }

}
