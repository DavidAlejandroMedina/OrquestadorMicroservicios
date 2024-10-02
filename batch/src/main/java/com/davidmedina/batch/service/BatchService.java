package com.davidmedina.batch.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class BatchService {

    private static final Logger LOG = LoggerFactory.getLogger(BatchService.class);
    private final WebClient webClient;

    public BatchService(WebClient.Builder webClientbuilder) {
        this.webClient = webClientbuilder.build();
    }

    @Scheduled(fixedRate = 120000)  // Cada 2 minutos
    public void callOrchestrator() {
        String orchestratorUrl = "http://localhost:9000/call-microservice";
        String enigmaRequest = "{\n" +
                "    \"data\": [\n" +
                "        {\n" +
                "            \"header\": {\n" +
                "                \"id\": \"12345\",\n" +
                "                \"type\": \"TestGiraffeRefrigerator\"\n" +
                "            },\n" +
                "            \"enigma\": \"How to put a giraffe into a refrigerator?\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        webClient.post()
                .uri(orchestratorUrl)
                .header("Content-Type", "application/json")
                .bodyValue(enigmaRequest)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> System.out.println("\nResponse from orchestrator: \n" + response))
                .doOnError(error -> System.err.println("\nError: " + error.getMessage()))
                .subscribe();
    }
}
