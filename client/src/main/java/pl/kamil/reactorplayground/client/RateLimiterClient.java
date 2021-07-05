package pl.kamil.reactorplayground.client;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimiterClient implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger log = LoggerFactory.getLogger(RateLimiterClient.class);
  private final WebClient webClient;
  private final RateLimiter rateLimiter;
  private final AtomicInteger successes = new AtomicInteger();
  private final AtomicInteger failures = new AtomicInteger();

  RateLimiterClient(WebClientFactory webClientFactory, RateLimiterRegistry rateLimiterRegistry) {
    webClient = webClientFactory.create().baseUrl("http://error-service/ok").build();
    rateLimiter = rateLimiterRegistry.rateLimiter("greetings", RateLimiterConfiguration.CUSTOM);
  }

  public Mono<String> greet(UUID uuid) {
    return webClient.get()
        .uri(b -> b.queryParam("uid", uuid.toString()).build())
        .retrieve()
        .bodyToMono(String.class)
        .transformDeferred(RateLimiterOperator.of(rateLimiter));
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var uuid = UUID.randomUUID();
    var numberOfRequests = 20;
    var cdl = new CountDownLatch(numberOfRequests);
    for (int i = 0; i < numberOfRequests; i++) {
      greet(uuid)
          .doOnError(t -> failures.incrementAndGet())
          .doOnNext(g -> {
            log.info("Got greeting " + g);
            successes.incrementAndGet();
          })
          .doOnTerminate(cdl::countDown)
          .subscribe();
    }
    try {
      cdl.await();
      log.info("Number of successes: " + successes.get());
      log.info("Number of failures: " + failures.get());
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
