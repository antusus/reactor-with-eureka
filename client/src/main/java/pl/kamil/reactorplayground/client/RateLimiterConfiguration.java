package pl.kamil.reactorplayground.client;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class RateLimiterConfiguration {

  public static final String CUSTOM = "custom";
  private final RateLimiterRegistry rateLimiterRegistry = RateLimiterRegistry.ofDefaults();

  RateLimiterConfiguration() {
    var rateLimiterConfig = RateLimiterConfig.custom()
        .limitRefreshPeriod(Duration.ofMillis(1000))
        .limitForPeriod(10)
        .timeoutDuration(Duration.ofMillis(25)).build();
    rateLimiterRegistry.addConfiguration(CUSTOM, rateLimiterConfig);
  }

  @Bean
  RateLimiterRegistry rateLimiterRegistry() {
    return rateLimiterRegistry;
  }
}
