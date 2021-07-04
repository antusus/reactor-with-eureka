package pl.kamil.reactorplayground.client;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicLong;

public abstract class TimerUtils {
  public static <T> Mono<T> cache(Mono<T> cache) {
    return cache.doOnNext(c -> System.out.println("receiving " + c.toString())).cache();
  }

  public static <T> Flux<T> cache(Flux<T> cache) {
    return cache.doOnNext(c -> System.out.println("receiving " + c.toString())).cache();
  }

  public static <T> Mono<T> monitor(Mono<T> configMono) {
    var start = new AtomicLong();
    return configMono
        .doOnError(System.err::println)
        .doOnSubscribe((subscription) -> start.set(System.currentTimeMillis())) 
        .doOnNext((greeting) -> System.out.printf("total time: %s%n",
            System.currentTimeMillis() - start.get()));
  }

  public static <T> Flux<T> monitor(Flux<T> configMono) {
    var start = new AtomicLong();
    return configMono
        .doOnError(System.err::println)
        .doOnSubscribe((subscription) -> start.set(System.currentTimeMillis())) 
        .doOnNext((greeting) -> System.out.printf("total time: %s",
            System.currentTimeMillis() - start.get()));
  }
}
