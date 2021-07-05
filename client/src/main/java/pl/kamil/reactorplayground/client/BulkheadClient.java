package pl.kamil.reactorplayground.client;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BulkheadClient implements ApplicationListener<ApplicationReadyEvent> {
  private static final Logger log = LoggerFactory.getLogger(BulkheadClient.class);
  private final GreetingClient client;
  private final Bulkhead bulkhead;

  BulkheadClient(GreetingClient greetingClient, BulkheadRegistry bulkheadRegistry) {
    this.client = greetingClient;
    bulkhead = bulkheadRegistry.bulkhead("custom", BulkheadConfiguration.CUSTOM);
  }

  public Mono<String> greet(UUID uuid) {
    return client.greet(uuid).transformDeferred(BulkheadOperator.of(bulkhead));
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    var availableProcessors = Runtime.getRuntime().availableProcessors();
    log.info("Available processors " + availableProcessors);
    var counter = new AtomicInteger();
    var scheduler = Schedulers.immediate();
    for (int i = 0; i < availableProcessors; i++) {
      greet(UUID.randomUUID())
          .doOnNext(s -> log.info("Attempt #" + counter.incrementAndGet()))
          .doOnError(t -> log.error("Bulkhead kicked in", t))
//          .subscribeOn(scheduler)
//          .publishOn(scheduler)
          .subscribe();
    }
  }
}
