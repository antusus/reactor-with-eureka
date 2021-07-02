package pl.kamil.reactorplayground.profileservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static java.util.stream.Collectors.toConcurrentMap;

@RestController
@RequestMapping("/profiles")
public class ProfilesController {
  private final Map<Integer, Profile> profiles = Map.of(
      1, "jane",
      2, "mia",
      3, "leroy",
      4, "badhr",
      5, "zhen",
      6, "juliette",
      7, "artem",
      8, "michelle",
      9, "eva",
      10, "richard").entrySet().stream()
      .collect(toConcurrentMap(Map.Entry::getKey, e -> new Profile(e.getKey(), e.getValue(), UUID.randomUUID().toString())));

  @GetMapping("/{id}")
  Mono<Profile> getProfile(@PathVariable Integer id) {
    return Mono.just(profiles.get(id));
  }
}
