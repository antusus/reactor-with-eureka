package pl.kamil.reactorplayground.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.Optional;

@Component
public class OrdersClient implements ApplicationListener<ApplicationReadyEvent> {

  private static final Logger log = LoggerFactory.getLogger(OrdersClient.class);
  private final WebClient webClient;

  public OrdersClient(WebClientFactory webClientFactory) {
    this.webClient = webClientFactory.create()
        .baseUrl("http://order-service/orders")
        .build();
  }

  Flux<Order> getOrders(Integer... ids) {
    return Optional.ofNullable(ids)
        .map(Arrays::asList)
        .map(idsToQuery -> webClient.get().uri(b -> b.queryParam("ids", idsToQuery).build()).retrieve())
        .orElseGet(() -> webClient.get().retrieve())
        .bodyToFlux(Order.class);
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    getOrders(2, 3)
        .subscribe(
            order -> log.info(order.toString()),
            throwable -> log.error("Failed getting orders", throwable)
        );
  }
}
