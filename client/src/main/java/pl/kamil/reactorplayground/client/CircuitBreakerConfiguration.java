package pl.kamil.reactorplayground.client;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfiguration {
  public static final String GREETINGS = "greetings";
  private final CircuitBreakerRegistry circuitBreakerRegistry;

  CircuitBreakerConfiguration() {
    circuitBreakerRegistry = CircuitBreakerRegistry.custom().build();
    circuitBreakerRegistry.addConfiguration(GREETINGS, CircuitBreakerConfig
        .custom()
        .failureRateThreshold(50)
        .slidingWindowSize(5)
        .waitDurationInOpenState(Duration.ofMillis(1000))
        .permittedNumberOfCallsInHalfOpenState(2)
        .recordExceptions(WebClientResponseException.InternalServerError.class)
        .build());
  }

  @Bean
  CircuitBreakerRegistry circuitBreakerRegistry() {
    return circuitBreakerRegistry;
  }
}
