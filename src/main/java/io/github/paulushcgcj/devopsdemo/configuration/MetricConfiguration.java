package io.github.paulushcgcj.devopsdemo.configuration;

import brave.handler.MutableSpan;
import brave.handler.SpanHandler;
import brave.propagation.TraceContext;
import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
          .commonTags(
              "version", "1.0.0",
              "product", "company"
          )
          .meterFilter(
              new MeterFilter() {
                @Override
                public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                  return DistributionStatisticConfig.builder()
                      .percentiles(0.5, 0.95, 0.99)
                      .build()
                      .merge(config);
                }
              });
    };
  }

  @Bean
  public MeterRegistryCustomizer<PrometheusMeterRegistry> prometheusConfiguration() {
    return MeterRegistry::config;
  }

  @Bean
  public MeterFilter ignoreTag() {
    return MeterFilter.ignoreTags("type");
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
