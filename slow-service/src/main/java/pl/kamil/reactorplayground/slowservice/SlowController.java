package pl.kamil.reactorplayground.slowservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
public class SlowController {
  private final long dtim;
  private final AtomicInteger port = new AtomicInteger();


  SlowController(@Value("${service.delay}") long dtim) {
    this.dtim = dtim;
    System.out.printf("-------------------%nStarting with delay set to %s seconds%n-------------------%n", dtim);
  }

  @EventListener
  public void onWebServerInitialized(WebServerInitializedEvent event) {
    port.set(event.getWebServer().getPort());
  }


  @GetMapping("/greetings")
  Mono<GreetingResponse> greet(@RequestParam(required = false, defaultValue = "world") String name) {
    var start = Instant.now();
    return Mono
        .fromSupplier(() -> new GreetingResponse(
            String.format("Hello, %s! (from %s took %s(s))", name, port, Duration.between(start, Instant.now()).getSeconds()))
        )
        .doOnNext(System.out::println)
        .delaySubscription(Duration.ofSeconds(dtim));

  }
}
