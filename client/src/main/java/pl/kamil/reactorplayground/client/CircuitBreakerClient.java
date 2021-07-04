package pl.kamil.reactorplayground.client;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static pl.kamil.reactorplayground.client.CircuitBreakerConfiguration.GREETINGS;

@Component
public class CircuitBreakerClient implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(CircuitBreakerClient.class);
  private final WebClient webClient;
  private final CircuitBreaker circuitBreaker;

  CircuitBreakerClient(WebClientFactory webClientFactory, CircuitBreakerRegistry circuitBreakerRegistry) {
    this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("greetings", GREETINGS);
    webClient = webClientFactory.create().baseUrl("http://error-service/cb").build();
  }

  public Mono<String> greet(UUID uuid) {
    return webClient.get().uri(b -> b.queryParam("uid", uuid.toString()).build())
        .retrieve()
        .bodyToMono(String.class)
        .transformDeferred(CircuitBreakerOperator.of(circuitBreaker));
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var uuid = UUID.randomUUID();
    greet(uuid)
        .doOnError(e -> {
          if (e instanceof WebClientResponseException.InternalServerError) {
            log.error("Network exception");
          }
          if (e instanceof CallNotPermittedException) {
            log.error("Breaker opened");
          }
        })
        .retry(10)
        .doOnSubscribe(s -> log.info("Getting greeting for " + uuid + " with Circuit Breaker"))
        .subscribe(log::info, t -> log.error("Error getting greeting with Circuit Breaker", t));
  }
}
