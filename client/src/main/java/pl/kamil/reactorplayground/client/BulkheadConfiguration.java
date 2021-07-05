package pl.kamil.reactorplayground.client;

import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BulkheadConfiguration {
  public static final String CUSTOM = "custom";
  private final BulkheadRegistry bulkheadRegistry = BulkheadRegistry.ofDefaults();

  BulkheadConfiguration() {
    var availableProcessors = Runtime.getRuntime().availableProcessors();
    var custom = BulkheadConfig.custom()
        .writableStackTraceEnabled(true)
        .maxConcurrentCalls(availableProcessors / 2)
        .maxWaitDuration(Duration.ofMillis(5))
        .build();
    bulkheadRegistry.addConfiguration(CUSTOM, custom);
  }

  @Bean
  BulkheadRegistry bulkheadRegistry() {
    return bulkheadRegistry;
  }
}
