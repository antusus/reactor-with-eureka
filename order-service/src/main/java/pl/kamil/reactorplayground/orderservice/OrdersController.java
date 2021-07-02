package pl.kamil.reactorplayground.orderservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@RequestMapping("/orders")
public class OrdersController {
  private final Map<Integer, List<Order>> customerOrders = IntStream.range(1, 11)
      .mapToObj(id -> Map.entry(id, new CopyOnWriteArrayList<Order>()))
      .collect(Collectors.toConcurrentMap(Map.Entry::getKey, e -> {
        var orders = e.getValue();
        var maxOrders = (int) (Math.random() * 10);
        if (maxOrders < 1) {
          maxOrders = 1;
        }
        IntStream.range(0, maxOrders)
            .mapToObj(i -> new Order(UUID.randomUUID().toString(), e.getKey()))
            .forEach(orders::add);
        return orders;
      }));

  @GetMapping
  Flux<Order> getOrders(@RequestParam(required = false) Integer[] ids) {
    return Flux.fromStream(Optional.ofNullable(ids)
        .map(Arrays::asList)
        .map(idList -> customerOrders.entrySet().stream()
            .filter(e -> idList.contains(e.getKey()))
            .map(Map.Entry::getValue)
            .flatMap(Collection::stream))
        .orElse(customerOrders.values().stream().flatMap(Collection::stream)));
  }
}
