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

    public String processWebhook(JsonApiBodyRequest requestBody) {
        return "Message received from Orchestration\n";
    }

    /*public Mono<String> callWebhook(JsonApiBodyRequest enigmaRequest, String url) {

        return webClientBuilder.build()
                .post()
                .uri(url)
                .bodyValue(enigmaRequest)  // Pasar la respuesta final al webhook
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(body -> System.out.println("Webhook response: " + body));
    }*/
}
