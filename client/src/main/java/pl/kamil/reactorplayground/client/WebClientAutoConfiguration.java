package pl.kamil.reactorplayground.client;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.loadbalancer.reactive.ReactorLoadBalancerExchangeFilterFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  WebClientFactory webClientFactory(WebClient.Builder builder, ReactorLoadBalancerExchangeFilterFunction loadBalancerFunction) {
    // loadBalancerFunction switches from checking service name in DNS to Eureka registry
    return new WebClientFactory(builder, loadBalancerFunction);
  }
}
