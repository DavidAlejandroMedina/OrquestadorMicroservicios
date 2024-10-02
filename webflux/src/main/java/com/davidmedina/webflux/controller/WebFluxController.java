package com.davidmedina.webflux.controller;

import com.davidmedina.webflux.model.JsonApiBodyRequest;
import com.davidmedina.webflux.service.StepThreeService;
import com.davidmedina.webflux.service.StepTwoService;
import com.davidmedina.webflux.service.StepOneService;
import com.davidmedina.webflux.service.WebhookService;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class WebFluxController {
    private final StepOneService stepOneService;
    private final StepTwoService stepTwoService;
    private final StepThreeService stepThreeService;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;
    private final WebhookService webhookService;

    private static final Logger LOG = LoggerFactory.getLogger(StepTwoService.class);

    public WebFluxController(StepOneService stepOneService, StepTwoService stepTwoService, StepThreeService stepThreeService, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry, WebhookService webhookService) {
        this.stepOneService = stepOneService;
        this.stepTwoService = stepTwoService;
        this.stepThreeService = stepThreeService;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
        this.webhookService = webhookService;
    }

    @PostMapping("/call-microservice")
    public Flux<String> callMicroservicePosts(@RequestBody JsonApiBodyRequest requestBody) {

        String webhookUrl = "http://localhost:8083/webhook";
        webhookService.callWebhook(webhookUrl, requestBody)
                .subscribe(response -> LOG.info("\n\nWebhook call completed with response: \n" + response),
                error -> LOG.error("Error occurred during webhook call: ", error));

        String microserviceUrl1 = "http://localhost:8080/getStep";
        String microserviceUrl2 = "http://localhost:8081/getStep";
        String microserviceUrl3 = "http://localhost:8082/getStep";

        Mono<String> response1 = stepOneService.callStepOne(microserviceUrl1, requestBody, "1");
        Mono<String> response2 = stepTwoService.callStepTwo(microserviceUrl2, requestBody, "2");
        Mono<String> response3 = stepThreeService.callStepThree(microserviceUrl3, requestBody, "3");

        // Usando zip para ejecutarlas en paralelo
        return Flux.zip(response1, response2, response3)
                .map(tuple -> {
                    String step1Answer = extractAnswer(tuple.getT1());
                    String step2Answer = extractAnswer(tuple.getT2());
                    String step3Answer = extractAnswer(tuple.getT3());

                    return String.format(
                            "{\"data\": [{\"header\": {\"id\": \"12345\", \"type\": \"TestGiraffeRefrigerator\"}, \"answer\": \"Step1: %s - Step2: %s - Step3: %s\"}]}",
                            step1Answer, step2Answer, step3Answer
                    );
                });
    }


    private String extractAnswer(String jsonResponse) {
        return jsonResponse.replaceAll(".*\"answer\":\"([^\"]+)\".*", "$1");
    }
}
