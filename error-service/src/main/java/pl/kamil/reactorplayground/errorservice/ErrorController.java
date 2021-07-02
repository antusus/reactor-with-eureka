package pl.kamil.reactorplayground.errorservice;

import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.String.format;

@RestController
public class ErrorController {
  private final AtomicInteger port = new AtomicInteger();
  private final Map<String, AtomicInteger> clientCounts = new ConcurrentHashMap<>();

  @EventListener
  public void onWebServerInitialized(WebServerInitializedEvent event) {
    port.set(event.getWebServer().getPort());
  }

  @GetMapping("/ok")
  Mono<Map<String, String>> okEndpoint(@RequestParam String uid) {
    var attempts = registerClient(uid);
    return Mono.just(Map.of("greeting", format("greeting attempt %s on port %s", attempts, port.get())));
  }

  @GetMapping("/retry")
  Mono<Map<String, String>> retryEndpoint(@RequestParam String uid) {
    var attempts = registerClient(uid);
    return attempts > 2
        ? Mono.just(Map.of("greeting", format("greeting attempt %s on port %s", attempts, port.get())))
        : Mono.error(new IllegalArgumentException("Failed"));

  }

  @GetMapping("/cb")
  Mono<Map<String, String>> circuitBreakerEndpoint(@RequestParam String uid) {
    registerClient(uid);
    return Mono.error(new IllegalArgumentException());
  }

  private int registerClient(String uid) {
    if (uid != null) {
      clientCounts.putIfAbsent(uid, new AtomicInteger(0));
      return clientCounts.get(uid).incrementAndGet();
    }
    return 1;
  }
}
