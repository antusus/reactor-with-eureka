package pl.kamil.reactorplayground.customerservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/customers")
class CustomerController {
  private final int delayInMillis;
  private final Map<Integer, Customer> customers = Map.of(
      1, "Jane",
      2, "Mia",
      3, "Leroy",
      4, "Badhr",
      5, "Zhen",
      6, "Juliette",
      7, "Artem",
      8, "Michelle",
      9, "Eva",
      10, "Richard")
      .entrySet()
      .stream()
      .collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> new Customer(e.getKey(), e.getValue())));

  CustomerController(@Value("${rsb.delay:2000}") int delayInMillis) {
    this.delayInMillis = delayInMillis;
  }

  @GetMapping
  Flux<Customer> getCustomers(
      @RequestParam(required = false) Integer[] ids,
      @RequestParam(required = false) boolean shouldDelay
  ) {
    var customerStream = this.customers.values().stream();
    return Optional.ofNullable(ids)
        .map(Arrays::asList)
        .map(idsList -> customerStream.filter(c -> idsList.contains(c.id())))
        .map(s -> toFlux(s, shouldDelay))
        .orElse(toFlux(customerStream, shouldDelay));
  }

  private Flux<Customer> toFlux(Stream<Customer> customerStream, boolean shouldDelay) {
    var customerFlux = Flux.fromStream(customerStream);
    return shouldDelay ? customerFlux.delaySubscription(Duration.ofMillis(delayInMillis)) : customerFlux;
  }
}