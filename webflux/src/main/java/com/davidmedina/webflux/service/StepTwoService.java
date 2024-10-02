package com.davidmedina.webflux.service;

import com.davidmedina.webflux.model.JsonApiBodyRequest;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class StepTwoService {
    private final WebClient.Builder webClientBuilder;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;
    private static final Logger LOG = LoggerFactory.getLogger(StepTwoService.class);

    public StepTwoService(WebClient.Builder webClientBuilder, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.webClientBuilder = webClientBuilder;
        this.retry = retryRegistry.retry("stepTwo");
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("stepTwo");

        this.retry.getEventPublisher()
                .onRetry(e -> LOG.info("Retrying Step 2. \n Attempt: {}", e.getNumberOfRetryAttempts()));

        this.circuitBreaker.getEventPublisher()
                .onStateTransition(event -> LOG.info("Circuit Breaker Transition for step 2: from {} to {}",
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()));
    }

    //@Retry(name = "enigmaRetry", fallbackMethod = "fallbackRetry")
    public Mono<String> callStepTwo(String url, JsonApiBodyRequest enigmaRequest, String step) {
        System.out.println(" Making a request to Microservice step " + step + " at :" + LocalDateTime.now());
        return webClientBuilder.build()
                .post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(enigmaRequest)
                .retrieve()
                .bodyToMono(String.class)
                .transformDeferred(RetryOperator.of(retry))
                .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))
                //.onErrorResume(throwable -> fallbackRetry(url, enigmaRequest, step, throwable))
                .onErrorResume(Throwable -> {
                    LOG.warn("Too many retries for Step " + step);
                    return Mono.just("Step "+ step + " not found");
                })
                .doOnNext(body -> System.out.println("Step " + step + " response: " + body));
    }
}
