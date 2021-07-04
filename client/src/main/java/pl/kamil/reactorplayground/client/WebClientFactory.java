package pl.kamil.reactorplayground.client;

import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {
  private WebClient.Builder builder;

  WebClientFactory(WebClient.Builder builder, ReactorLoadBalancerExchangeFilterFunction loadBalancerFunction) {
    this.builder = builder.filter(loadBalancerFunction);
  }

  public WebClient.Builder create() {
    return builder.clone();
  }
}
