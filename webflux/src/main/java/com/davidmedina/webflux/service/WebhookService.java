package com.davidmedina.webflux.service;

import com.davidmedina.webflux.model.JsonApiBodyRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class WebhookService {
    private final WebClient.Builder webClientBuilder;

    private static final Logger LOG = LoggerFactory.getLogger(WebhookService.class);

    public WebhookService(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    public Mono<String> callWebhook(String url, JsonApiBodyRequest enigmaRequest) {
        return webClientBuilder.build()
                .post()
                .uri(url)
                .header("Content-Type", "application/json")
                .bodyValue(enigmaRequest)
                .retrieve()
                .bodyToMono(String.class);
    }

    /*public Mono<Void> processWebhook(String payload) {
        return Mono.fromRunnable(() -> {
            // Process the payload here
            System.out.println("Webhook response: " + payload);
        });
    }*/
}
