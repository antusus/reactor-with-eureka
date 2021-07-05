package pl.kamil.reactorplayground.client;

import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
class GreetingClient {
  private final WebClient webClient;

  GreetingClient(WebClientFactory webClientFactory) {
    webClient = webClientFactory.create().baseUrl("http://error-service/").build();
  }

  public Mono<String> greetThatFailsSometimes(UUID uuid) {
    return webClient.get().uri(b -> b.pathSegment("cb").queryParam("uid", uuid.toString()).build())
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> greetAfterTwoRetries(UUID uuid) {
    return this.webClient.get()
        .uri(b -> b.pathSegment("retry").queryParam("uid", uuid.toString()).build())
        .retrieve()
        .bodyToMono(String.class);
  }

  public Mono<String> greet(UUID uuid) {
    return webClient.get()
        .uri(b -> b.pathSegment("ok").queryParam("uid", uuid.toString()).build())
        .retrieve()
        .bodyToMono(String.class);
  }
}
