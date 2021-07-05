package pl.kamil.reactorplayground.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.UUID;

@Component
public class RetryClient implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger log = LoggerFactory.getLogger(RetryClient.class);
  private final GreetingClient client;

  public RetryClient(GreetingClient greetingClient) {
    this.client = greetingClient;
  }

  public Mono<String> greet(UUID uuid) {
    return client.greetAfterTwoRetries(uuid)
        .retryWhen(retryPolicy());
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var uuid = UUID.randomUUID();
    greet(uuid)
        .doOnSubscribe(s -> log.info("Getting greeting for " + uuid))
        .subscribe(
            greeting -> log.info("Received greeting: " + greeting),
            throwable -> log.error("Error while getting greeting", throwable));
  }

  private Retry retryPolicy() {
    // error-service returns greeting after 2 attempts, so we are setting retry to 3
    // we are logging each attempt
    // Retry.backoff builds exponential backoff strategy
    return Retry.backoff(3, Duration.ofMillis(100))
        .maxBackoff(Duration.ofSeconds(1))
        .doBeforeRetry(this::logAttempt);
  }

  private void logAttempt(Retry.RetrySignal retrySignal) {
    var totalRetries = retrySignal.totalRetries();
    log.info("Retry number " + totalRetries);
  }
}
