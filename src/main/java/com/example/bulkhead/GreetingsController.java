package com.example.bulkhead;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class GreetingsController {

  @GetMapping("/greeting")
  @Bulkhead(name = "greetingBulkHead", fallbackMethod = "greetingFallBack")
  public ResponseEntity<String> greeting(@RequestParam(name = "name") String name) {
    log.info("greeting called");
    return ResponseEntity.ok().body("Hi " + name + ". Good ");
  }

  public ResponseEntity<String> greetingFallBack(String name, BulkheadFullException ex) {
    HttpHeaders header = new HttpHeaders();
    header.add("Retry-After", "10");
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .headers(header)
        .body("Too many concurrent request");
  }
}
